import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Duplicate implements Runnable {

	// Input Path
	static String productPath = "DATA/Productbug_id/";
	static String componentPath = "DATA/Componentbug_id/";

	// Output Path

	static String outPath = "DATA/duplicate/";

	// Thread ID
	int start;
	int end;
	String project;
	String product;
	String component;

	// Constructor
	public Duplicate(String in_start, String in_end, String in_project) {
		start = Integer.parseInt(in_start);
		end = Integer.parseInt(in_end);
		project = in_project;
	}

	// Implements Runnable Method
	public void run() {

		// Get path
		String source = "DATA/eclipse_xml/";

		// ---------- Component Part ----------

		int count = 1;
		int times = 1;
		for (int i = start; i < end; i++) {
			try {
				xmlParsing_(source + i);
			} catch (Exception e) {

			}

		}
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		long StartTime = System.currentTimeMillis(); // ��X�ثe�ɶ�

		// xmlParsing_("DATA/test_xml.xml", "DATA/");

		// Temp Setting for args[]
		// args = new String[3];
		// args[0] = "eclipse";
		// args[1] = "Platform";
		// args[2] = "UI";

		Thread t = new Thread(new Duplicate(args[1], args[2], args[0]));
		t.start();
		t.join();

		// Thread t1 = new Thread(new classify("1", "eclipse", "Platform",
		// "UI"));
		// Thread t2 = new Thread(new classify("2", "eclipse", "JDT", "UI"));
		// Thread t3 = new Thread(new classify("3", "mozilla", "Core",
		// "Layout"));
		// Thread t4 = new Thread(new classify("4", "mozilla", "Firefox",
		// "Security"));
		//
		// t1.start();
		// t2.start();
		// t3.start();
		// t4.start();
		//
		// t1.join();
		// t2.join();
		// t3.join();
		// t4.join();

		long ProcessTime = System.currentTimeMillis() - StartTime; // �p��B�z�ɶ�
		System.out.println("\n===== Information Extracting Process Time = "
				+ (float) ProcessTime / 1000 + " s =====\n");
	}

	// XML Parser
	public static void xmlParsing_(String inXml) throws Exception {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

		DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();

		Document doc = null;
		doc = dbBuilder.parse(inXml);

		NodeList list = doc.getElementsByTagName("bug");
		Element element = (Element) list.item(0);
		String project = "eclipse";
		String bug_id = element.getElementsByTagName("bug_id").item(0)
				.getFirstChild().getNodeValue();

		String dup_id = element.getElementsByTagName("dup_id").item(0)
				.getFirstChild().getNodeValue();
		if (dup_id != null) {
			String outFile = outPath + dup_id;

			Runtime rt = Runtime.getRuntime();

			File f = new File(outFile);
			if (!f.exists()) {
				f.mkdirs();
			}

			if (f.canWrite())
				try (PrintWriter out = new PrintWriter(new BufferedWriter(
						new FileWriter(outFile, true)))) {
					out.println(bug_id + ",");
				} catch (IOException e) {
					// exception handling left as an exercise for the reader
				}
		}

	}
}
