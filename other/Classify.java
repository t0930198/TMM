import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Classify implements Runnable {

	/**
	 * @param args
	 *            args[0] = Project Name args[1] = Product Name args[2] =
	 *            Component Name
	 * 
	 *            Eclipse/Mozilla Component 1 bug_id 1 attach.txt severity.txt
	 *            summary.txt thetext.txt who.txt ... bug_id n ... ... Component
	 *            n
	 * 
	 */

	// Input Path
	static String productPath = "DATA/Productbug_id/";
	static String componentPath = "DATA/Componentbug_id/";

	// Output Path
	static String productPath_formatted = "DATA/Formattedbug_id/Productbug_id/";
	static String componentPath_formatted = "DATA/Formattedbug_id/Componentbug_id/";

	// Thread ID
	String tid;
	String project;
	String product;
	String component;

	// Constructor
	public Classify(String in_tid, String in_project) {
		tid = in_tid;
		project = in_project;
	}

	// Implements Runnable Method
	public void run() {
		System.out.println("This is thread [" + tid + "] (" + project + "-"
				+ product + ":" + component + "):");

		// Get path
		String source = "DATA/openoffice_xml/";
		
		
		
		// ---------- Component Part ----------
		File fs = new File(source);
		
		String[] filenames = fs.list();

		int count = 1;
		int times = 1;
		for (String s : filenames) {
			try {
				xmlParsing_(source + s);
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

		Thread t = new Thread(new Classify("0", args[0]));
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
		String project = "openoffice";
		String bug_id = element.getElementsByTagName("bug_id").item(0)
				.getFirstChild().getNodeValue();

		String product = element.getElementsByTagName("product").item(0)
				.getFirstChild().getNodeValue();
		// System.out.println( bug_id );

		String component = element.getElementsByTagName("component").item(0)
				.getFirstChild().getNodeValue();
		// System.out.println( bug_severity );

		String outPathp = productPath + project + "/" + product + "/bugs/";
		String outPathc = componentPath + project + "/" + product + "/" + component + "/bugs/";

		Runtime rt = Runtime.getRuntime();

		File fp = new File(outPathp);
		if (!fp.exists()) {
			fp.mkdirs();
		}
		File fc = new File(outPathc);
		if (!fc.exists()) {
			fc.mkdirs();
		}
		System.out.println("cp  " +" "+ inXml +" "+ outPathp);
		rt.exec("cp  " + inXml +" "+ outPathp);
		System.out.println("cp  " + inXml  +" "+ outPathc);
		rt.exec("cp  " + inXml  +" "+ outPathc);
	}

}
