package working;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

public class Testing extends Report implements Runnable {

	static String topic_source = "data/snlp/train_source/01000/summary.txt";
	static String topic_summary;
	static String[] topic_list;

	static String xml_source = "data/xml/";
	static String dest = "data/";
	static String filename = "";

	Vector<Report> reports = new Vector<Report>();
	Report report;
	int paired_topic;

	public static void main(String args[]) throws InterruptedException {
		System.out.println("Working Directory = "
				+ System.getProperty("user.dir"));
		load_topicxml();

		Thread t = new Thread(new Testing());
		t.start();
		t.join();
	}

	public Testing() {

	}

	public void run() {
		System.out.println("inserting");

		long StartTimeb = System.currentTimeMillis();
		for (int i = 214050; i <= 215049; i++) {
			try {
				report = new Report();
				xmlParsing(xml_source + i + ".xml");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		long ProcessTimeb = System.currentTimeMillis() - StartTimeb;
		System.out.println("===== Build table cost time: "
				+ (float) ProcessTimeb / 1000 + " s =====");

		System.exit(0);
		ExecutorService thread_pool = Executors.newFixedThreadPool(4);
		long StartTime = System.currentTimeMillis();
		for (int i = 215050; i <= 215149; i++) {
			try {
				xmlParsing(xml_source + i + ".xml");
				thread_pool.execute(new Thread(new Triager(report.getProduct(),
						report.getComponent(), report.getSeverity(), report
								.getPriority(), compare())));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		try {
			thread_pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long ProcessTime = System.currentTimeMillis() - StartTime;
		System.out.println("===== Parse testing reports cost time: "
				+ (float) ProcessTime / 1000 + " s =====");
		System.out.println("done");

	}

	public void xmlParsing(String inXml) throws Exception {

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

			for (int i = 0; i < report.getDeveloper_list().size(); i++) {

				Iterator i1 = report.getDeveloper_list().iterator();
				while (i1.hasNext()) {
					Element element = (Element) i1.next();
					report.addDeveloper(element.getText());
				}
			}
			report.setShort_desc(node.selectSingleNode("short_desc").getText());

			report.setLong_desc(node.selectSingleNode("long_desc")
					.selectSingleNode("thetext").getText());
			// System.out.println(long_desc);
		} catch (Exception e) {
			File f = new File(xml_source);
			if (!f.exists())
				System.out.println("cant access this folder");
			System.out.println("Can't read the file " + inXml);
			e.printStackTrace();
		}

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

	public int compare() {
		int topic_id = 0;
		int count[] = new int[topic_list.length];
		for (int i : count)
			i = 0;

		for (String list : topic_list) {
			for (String term : list.split("\n")) {

				if (report.getShort_desc().contains(term.split("\t")[1])) {
					count[topic_id]++;
				}
				if (report.getLong_desc().contains(term.split("\t")[1])) {
					count[topic_id]++;
				}
			}
			topic_id++;
		}
		int x = 0;
		for (int i = 0; i < count.length; i++) {

			if (count[x] < count[i]) {
				x = i;
			}
		}
		return x;
	}
}
