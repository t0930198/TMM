package working;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class Triager implements Runnable{
	
	ResultSet participate;
	List<Node> developer_list;
	Vector<String> pre_developers;
	
	String product = "";
	String component = "";
	String priority = "";
	String severity = "";
	int topic=0;
	
	String assign = "";
	String attach = "";
	String commit = "";
	String comment = "";
	
	
	Derby tmmdb;
	
	public Triager(String pro, String com, String pri, String ser, int topic ) throws SQLException{
		this.product=pro;
		this.component=com;
		this.priority=pri;
		this.severity=ser;
		
		participate = tmmdb._select_participate(product, component, severity,
				priority, topic);
	}
	public void run(){
		
	}
	
	private double aScore(){
		return 1;
	}
	
	private double sScore(){
		return 1;
	}
	
	private double dScore(){
		return aScore()*sScore();
	}
	
	public void xmlParsing(String inXml) throws Exception {

		try {
			// dplist = new List<Node>();
			File file = new File(inXml);
			SAXReader reader = new SAXReader();
			Document doc = reader.read(file);
			Node node = doc.selectSingleNode("//bugzilla/bug");
			
			this.developer_list = node.selectNodes("long_desc/who");

			for (int i = 0; i < developer_list.size(); i++) {

				Iterator i1 = developer_list.iterator();
				while (i1.hasNext()) {
					Element element = (Element) i1.next();
					
				}
			}
			this.assign = node.selectSingleNode("short_desc").getText();

		} catch (Exception e) {
			System.out.println("Can't read the file " + inXml);
			e.printStackTrace();
		}

	}
}
