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

public class Triager {
	
	List<Node> developer_list;
	
	private Vector<String> d_list;
	String[] topic_list;
	Vector<Report> train;
	
	private Report report;
		
	private String assign = "";
	private String attach = "";
	private String commit = "";
	private String comment = "";
	
	public Triager(String[] topic_list, Vector<Report> train){
		this.topic_list = topic_list;
		this.train = train;
	}
	
	public void work(Report r) {
		this.report=r;
		this.getD();
	}
		
	private void getD(){
		Vector<String> tmp = null;
		
		for(Report r:train){
			tmp = r.getDev(report.getProduct(), report.getComponent(), report.getPriority(), report.getSeverity());
			if(tmp!=null){
				for(String s:tmp){
					if(!d_list.contains(s))
						d_list.add(s);
				}
			}
		}
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
