package for_tmt;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class TMM_pre implements Runnable {

	static String source = "data/xml/";
	static String dest = "data/";
	static String filename = "";

	String content = "";
	String bug_id = "";
	String short_desc = "";
	String long_desc = "";
	SNLP snlp;

	static int count = 1;
	static int times = 1;

	public static void main(String args[]) throws InterruptedException {
		SimpleDateFormat sdFormat = new SimpleDateFormat("yy-MM-dd-hh-mm");
		Date date = new Date();
		String strDate = sdFormat.format(date);
		filename = strDate;
		Thread t = new Thread(new TMM_pre());
		t.start();
		t.join();
	}

	public TMM_pre() {
		snlp = new SNLP();
	}

	public void run() {

		File folder = new File(source);
		if (!folder.exists())
			folder.mkdirs();

		for (int i = 214049; i <= 215049; i++) {
			long StartTime = System.currentTimeMillis();
			try {
				content = "";
				bug_id = "";
				short_desc = "";
				long_desc = "";
				xmlParsing(source + i + ".xml");
				snlp.work(bug_id, short_desc + long_desc, filename);

				long ProcessTime = System.currentTimeMillis() - StartTime;
				System.out.println("===== Parse " + source + i + ".xml"
						+ " cost time: " + (float) ProcessTime / 1000
						+ " s =====");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void xmlParsing(String inXml) throws Exception {

		try {
			File file = new File(inXml);
			SAXReader reader = new SAXReader();
			Document doc = reader.read(file);
			Node node = doc.selectSingleNode("//bugzilla/bug");

			bug_id = node.selectSingleNode("bug_id").getText();
			short_desc = node.selectSingleNode("short_desc").getText();
			long_desc = node.selectSingleNode("long_desc")
					.selectSingleNode("thetext").getText();

		} catch (Exception e) {
			File f = new File(source);
			if (!f.exists())
				System.out.println("cant access this folder");
			System.out.println("Can't read the file " + inXml);
			e.printStackTrace();
		}

	}
}
