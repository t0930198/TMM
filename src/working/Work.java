package working;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;

import util.SNLP;

public class Work extends Report implements Runnable {

	static private int train_begin = 214049;
	static private int train_end = 214949;

	static private String topic_source = "data/snlp/train_source/01000/summary.txt";
	static private String topic_summary;
	static private String[] topic_list;

	static private String xml_source = "data/xml/";
	static private String dest = "data/";
	static private String filename = "";

	private Vector<Report> train = new Vector<Report>();
	private Vector<Report> test = new Vector<Report>();
	private Report report;
	private HashMap<String, Integer> assignee = new HashMap<String, Integer>();
	private HashMap<String, Integer> attacher = new HashMap<String, Integer>();

	private boolean state = false;

	// true=>training, false=>testing

	public static void main(String args[]) throws InterruptedException {
		System.out.println("Working Directory = "
				+ System.getProperty("user.dir"));
		load_topicxml();

		Thread t = new Thread(new Work());
		t.start();
		t.join();
	}

	public Work() {

	}

	public void run() {
		System.out.println("inserting");

		long StartTimeb = System.currentTimeMillis();
		for (int i = train_begin; i < train_end; i++) {
			try {
				report = new Report();
				xmlParsing(xml_source + i + ".xml");
				train.add(report);
				report = null;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		long ProcessTimeb = System.currentTimeMillis() - StartTimeb;
		System.out.println("===== Build table cost time: "
				+ (float) ProcessTimeb / 1000 + " s =====");

		System.exit(0);
		Triager triager = new Triager(train, assignee, attacher);
		long StartTime = System.currentTimeMillis();
		for (int i = train_end; i < (train_end + 100); i++) {
			try {
				report = new Report();
				xmlParsing(xml_source + i + ".xml");
				test.add(report);
				triager.work(report);
				report = null;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		long ProcessTime = System.currentTimeMillis() - StartTime;
		System.out.println("===== Parse testing reports cost time: "
				+ (float) ProcessTime / 1000 + " s =====");
		System.out.println("done");

	}

	public void xmlParsing(String inXml) throws Exception {
		String tmp;
		try {
			// dplist = new List<Node>();
			File file = new File(inXml);
			SAXReader reader = new SAXReader();
			Document doc = reader.read(file);
			Node node = doc.selectSingleNode("//bugzilla/bug");

			report.setBug_id(node.selectSingleNode("bug_id").getText());
			report.setProduct(node.selectSingleNode("product").getText());
			report.setComponent(node.selectSingleNode("component").getText());
			report.setPriority(node.selectSingleNode("priority").getText());
			report.setSeverity(node.selectSingleNode("bug_severity").getText());
			List<Node> list = node.selectNodes("long_desc/who");

			for (int i = 0; i < list.size(); i++) {

				Iterator i1 = list.iterator();
				while (i1.hasNext()) {
					Element element = (Element) i1.next();
					report.addDeveloper(element.getText());
				}
			}
			report.setShort_desc(node.selectSingleNode("short_desc").getText());

			report.setLong_desc(node.selectSingleNode("long_desc")
					.selectSingleNode("thetext").getText());
			
			tmp = node.selectSingleNode("assigned_to").getText();
			if (!assignee.containsKey(tmp))
				assignee.put(tmp, 0);
			else
				assignee.put(tmp, assignee.get(tmp) + 1);

			list = node.selectNodes("attachment/attacher");
			for (int i = 0; i < list.size(); i++) {

				Iterator i1 = list.iterator();
				while (i1.hasNext()) {
					Element element = (Element) i1.next();
					tmp = element.getText();
					if (attacher.containsKey(tmp))
						attacher.put(tmp, 0);
					else
						attacher.put(tmp, attacher.get(tmp) + 1);

				}

			}
			match_topic();

		} catch (Exception e) {
			File f = new File(xml_source);
			if (!f.exists())
				System.out.println("cant access this folder");
			System.out.println("Can't read the file " + inXml);
			e.printStackTrace();
		}

	}

	private void match_topic() {
		
		SNLP snlp = new SNLP();
		int topic = -1;
		int match_count = 0;
		snlp.work(report.getLong_desc() + report.getShort_desc());
		Annotation document = snlp.get_document();
		Vector<String> wordBag = new Vector<String>();
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {
			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific
			// methods
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// this is the text of the token
				String word = token.get(TextAnnotation.class);

				word = word.replaceAll("[\\pP‘’“”`'.:> <\",]", " ");
				wordBag.add(word);
			}
		}
		String[] terms;
		int temp_count = 0;
		for (int i = 0; i < topic_list.length; i++) {

			terms = topic_list[i].split("\n");
			for (String term : terms) {
				if (wordBag.contains(term))
					temp_count++;
			}
			if (temp_count > match_count)
				topic = i;
		}
		report.setTopic(topic);
	}

	/*
	 * load summary to topic_summary split topic to topic_list split list to
	 * topic_item
	 */
	public static void load_topicxml() {
		File topic_xml = new File(topic_source);
		if (!topic_xml.exists()) {
			System.out.println("no topic_xml");
			System.exit(-1);
		} else {
			FileReader fr;
			try {

				fr = new FileReader(topic_xml);
				BufferedReader br = new BufferedReader(fr);
				while (br.ready()) {
					topic_summary += br.readLine() + "\n";
				}
				fr.close();
				topic_list = topic_summary.split("\n\n\n");

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}
