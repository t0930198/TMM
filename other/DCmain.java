import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class DCmain implements Runnable{

	/**
	 * @param args
	 * args[0] = Project Name
	 * args[1] = Product Name
	 * args[2] = Component Name
	 * 

	 * 
	 * Flow:
	 * 		bug_id extracting -> URL parsing -> XML-format transforming -> description & developers extracting -> prediction data generating
	 * 
	 * 
	 */
	
	//BugZilla URL Input
	static String eclipse_url  = "https://bugs.eclipse.org/bugs/show_bug.cgi?ctype=xml&id=";
	static String mozilla_url  = "https://bugzilla.mozilla.org/show_bug.cgi?ctype=xml&id=";
	static String openoffice_url = "https://issues.apache.org/ooo/show_bug.cgi?ctype=xml&id=";
	//Input Path
	static String projectPath = "DATA/msr2013-bug_dataset-master/data/";
	
	//Output Path
	static String productPath = "DATA/Product bug_id/";
	static String componentPath = "DATA/Component bug_id/";
	
	//For Thread
	String tid;
	String project;
	String product;
	String component;
	
	public DCmain(String in_tid, String in_project, String in_product, String in_component) {
		tid = in_tid;
		project = in_project;
		product = in_product;
		component = in_component;
	}
	
	public void run(){
		System.out.println("This is thread [" + tid + "] (" + project + "-" + product + ":" + component + "):");
		
		//Get path
		String inPath = projectPath + project + "/" + product + "/";
		String outPath_product = productPath + project + "/" + product + "/";
		String outPath_component = componentPath + project + "/" + product + "/" + component + "/";
		
		//Get URL
		String inURL = null;
		if (project.equals("eclipse"))
			inURL = eclipse_url;
		else if (project.equals("mozilla"))
			inURL = mozilla_url;
		else if (project.equals("openoffice"))
			inURL = openoffice_url;
		
		
		//---------- Component Part ----------
		// 1. �i�� Tracking Dataset �� XML �ѪR�A�ϥ� component.xml �i���ӡA�H����ư϶����� bug_id
		try {
			xmlParsing_component(inPath + "component.xml", component, outPath_component + "component_bug_id.csv");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Component Bug ID getting done.");
		
		// 2. �i��H�W��z�� bug_id �� severity �z��A�öi�� BR �� S:NS ���G�έp
		try {
			xmlParsing_severity(outPath_component + "component_bug_id.csv", inPath + "severity.xml", outPath_component + "severity_component_bug_id.csv");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Severity Component Bug ID getting done.");
		
		// 3. �ϥΥH�W�Ҩ�o�� severity_component_bug_id�A�i�� BugZilla ������ XML �ɮפU��(�H�ѫ����^���l�� description/developer ��T�ɩҥ�)
			//���o�w�����n�� bug id ��A�i��U�۪� URL �U��
		BufferedReader br;
		try {
			br = new BufferedReader (new FileReader(outPath_component + "severity_component_bug_id.csv"));
			String[] bug_id = br.readLine().split(",");
			System.out.println("Total have " + bug_id.length + " bugs in " + outPath_component + "severity_component_bug_id.csv");
						
			for (int j=0; j<bug_id.length; j++){
				File f = new File(outPath_component + "bugs/" + bug_id[j] + ".xml");
				if ( !f.exists() ){
					xmlExtracting_proxy(inURL + bug_id[j], outPath_component + "bugs/" + bug_id[j] + ".xml", "proxy.yzu.edu.tw/proxy.pac:8080");
				}
				System.out.println("Now COMPONENT processed at id = " + bug_id[j] + ", no." + (j+1) + " bug(" + ((float)(j+1)/bug_id.length)*100 + "%).");
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//---------- Component Part ----------
		
		//---------- Product Part ----------
		// 1. �i�� Tracking Dataset �� XML �ѪR�A�ϥ� reports.xml �i���ӡA�H����ư϶����� bug_id
		try {
			xmlParsing_product(inPath + "reports.xml", outPath_product + "product_bug_id.csv");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Product Bug ID getting done.");
		
		// 2. �i��H�W��z�� bug_id �� severity �z��A�öi�� BR �� S:NS ���G�έp
		try {
			xmlParsing_severity(outPath_product + "product_bug_id.csv", inPath + "severity.xml", outPath_product + "severity_product_bug_id.csv");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Severity Product Bug ID getting done.");
		
		// 3. �ϥΥH�W�Ҩ�o�� severity_product_bug_id�A�i�� BugZilla ������ XML �ɮפU��(�H�ѫ����^���l�� description/developer ��T�ɩҥ�)
			//���o�w�����n�� bug id ��A�i��U�۪� URL �U��
		try {
			br = new BufferedReader (new FileReader(outPath_product + "severity_product_bug_id.csv"));
			String[] bug_id = br.readLine().split(",");
			System.out.println("Total have " + bug_id.length + " bugs in " + outPath_product + "severity_product_bug_id.csv");
				
			for (int j=0; j<bug_id.length; j++){
				File f = new File(outPath_product + "bugs/" + bug_id[j] + ".xml");
				if ( !f.exists() ){
					xmlExtracting_proxy(inURL + bug_id[j], outPath_product + "bugs/" + bug_id[j] + ".xml", "proxy.yzu.edu.tw/proxy.pac:8080");
				}
				System.out.println("Now PRODUCT processed at id = " + bug_id[j] + ", no." + (j+1) + " bug(" + ((float)(j+1)/bug_id.length)*100 + "%).");
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//---------- Product Part ----------
		
		
		
		//�H�W���R�A��ƳB�z�A���槹���@����A�Y�i���ΦA������C(���D�ݭn���s�U����)
		
		
		
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		long StartTime = System.currentTimeMillis(); 		// ��X�ثe�ɶ�
		
		//Temp Setting for args[]
//		args = new String[3];
//		args[0] = "eclipse";
//		args[1] = "Platform";
//		args[2] = "UI";
		
		//-----Thread start-----
		Thread t = new Thread();
		
		t = new Thread(new DCmain("0", args[0], args[1], args[2]));
		t.start();
		t.join();
		
//		t = new Thread(new DCmain("1", "eclipse", "Platform", "UI"));
//		t.start();
//		t.join();
//		
//		t = new Thread(new DCmain("2", "eclipse", "JDT", "UI"));
//		t.start();
//		t.join();
//		
//		t = new Thread(new DCmain("3", "mozilla", "Core", "Layout"));
//		t.start();
//		t.join();
//		
//		t = new Thread(new DCmain("4", "mozilla", "Firefox", "Security"));
//		t.start();
//		t.join();
		//-----Thread end-----
		
		long ProcessTime = System.currentTimeMillis() - StartTime; 			// �p��B�z�ɶ�
		System.out.println("\n========== Total Datasets Collecting Process Time = " + (float)ProcessTime/1000 + " s ==========\n");
	}
	
	//Tracking Dataset �� component.xml �榡�ѪR�A���^��X���U�� bug_id
	public static void xmlParsing_component (String inPath, String inComponent, String outFile) throws Exception{
	//�ϥ� DOM �M��i�� XML �ɮפ��ѪR
		//�ŧi�ѪR�ܼ�
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();  
        //�ŧi�ó]�w�ѪR��
        DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();  
        //�ŧi�@�ɮ��ܼ�  
        Document doc = null;  
        doc = dbBuilder.parse(inPath);  
          
        //��o�S�w���`�I�P�䩳�U�����
        NodeList list = doc.getElementsByTagName("report");  
        //System.out.println(list.getLength());
          
        File f = new File(outFile);
        if ( !(f.exists()) )
        	f.getParentFile().mkdirs();
        
        BufferedWriter bw = new BufferedWriter (new FileWriter(outFile));
        int writeCount = 0;
        //�M�X�`�I���U�Ҧ��l�`�I�P�䤺�e  
        for(int i=0; i<list.getLength(); i++){
            Element element = (Element)list.item(i);  
            String id = element.getAttribute("id");  
            int latest = element.getElementsByTagName("update").getLength();		//�ϥΤ@�ܼƦs��̫�@�� item�A�Y��o�̷s�@�����
            //String update = element.getElementsByTagName("update").item(latest-1).getFirstChild().getNodeValue();  
            latest = element.getElementsByTagName("when").getLength();
            //String when = element.getElementsByTagName("when").item(latest-1).getFirstChild().getNodeValue();  
            latest = element.getElementsByTagName("what").getLength();
            String what = element.getElementsByTagName("what").item(latest-1).getFirstChild().getNodeValue(); 
//            System.out.println("File Information:");   
//            System.out.println("	Bug ID:" + id);   
//            System.out.println("	Update:" + update);   
//            System.out.println("		Latest When:" + when);
//            System.out.println("		Latest What:" + what);
//            System.out.println("-----------------------------------");
            
            //�P�_�ݭn�^�� Component ���O
            if (what.equals(inComponent)){
            	if ( writeCount++ == 0 ){
                	bw.write(id);
                }else{
                	bw.write("," + id);
                }
            }
        }
        bw.close();
	}
	
	//Tracking Dataset �� severity.xml �榡�ѪR�A�íp���ƶ����� severe BR �P non-severe BR ���έp��T
	public static void xmlParsing_severity (String inBugId, String inSeverity, String outSeverity) throws Exception{
	//�ϥ� DOM �M��i�� XML �ɮפ��ѪR
		//�ŧi�ѪR�ܼ�
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();  
		//�ŧi�ó]�w�ѪR��
		DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();  
		//�ŧi�@�ɮ��ܼ�  
		Document doc = null;  
		doc = dbBuilder.parse(inSeverity);  
			          
		//��o�S�w���`�I�P�䩳�U�����
		NodeList list = doc.getElementsByTagName("report");  
			          
		File f = new File(outSeverity);
        if ( !(f.exists()) )
        	f.getParentFile().mkdirs();
		
		BufferedWriter bw = new BufferedWriter (new FileWriter(outSeverity));
		int writeCount = 0;
			
		int numS = 0, numNS = 0;
		//�M�X�`�I���U�Ҧ��l�`�I�P�䤺�e  
		for(int i=0; i<list.getLength(); i++){
			Element element = (Element)list.item(i);  
			String id = element.getAttribute("id");  
			int latest = element.getElementsByTagName("update").getLength();		//�ϥΤ@�ܼƦs��̫�@�� item�A�Y��o�̷s�@�����
			//String update = element.getElementsByTagName("update").item(latest-1).getFirstChild().getNodeValue();  
			latest = element.getElementsByTagName("when").getLength();
			//String when = element.getElementsByTagName("when").item(latest-1).getFirstChild().getNodeValue();  
			latest = element.getElementsByTagName("what").getLength();
			String what = element.getElementsByTagName("what").item(latest-1).getFirstChild().getNodeValue(); 
//			System.out.println("File Information:");   
//			System.out.println("	Bug ID:" + id);   
//			System.out.println("	Update:" + update);   
//			System.out.println("		Latest When:" + when);
//			System.out.println("		Latest What:" + what);
//			System.out.println("-----------------------------------");
			    
			//Ū�� component ���U�� bug_id �i����P�_
			BufferedReader br = new BufferedReader (new FileReader(inBugId));
			String[] bug_id = br.readLine().split(",");
			for (int j=0; j<bug_id.length; j++){
				//�z�� component
				if (id.equals(bug_id[j])){
					//�z�� severity
			    	if (what.equals("blocker") || what.equals("critical") || what.equals("major")){
			    		numS++;
			    		if ( writeCount++ == 0 ){
		                    bw.write(id);
		                }else{
		                    bw.write("," + id);
		                }
					}else if (what.equals("minor") || what.equals("trivial")){
					    numNS++;
					    if ( writeCount++ == 0 ){
		                    bw.write(id);
		                }else{
		                    bw.write("," + id);
		                }
					}
			    	break;
			    }
			}
			br.close();
		}
		bw.close();
		System.out.println("Severe:Non-severe = " + numS + ":" + numNS);
	}
	
	//Tracking Dataset �� report.xml �榡�ѪR�A���^��X���U�� bug_id
	public static void xmlParsing_product (String inPath, String outFile) throws Exception{
	//�ϥ� DOM �M��i�� XML �ɮפ��ѪR
		//�ŧi�ѪR�ܼ�
	    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();  
	    //�ŧi�ó]�w�ѪR��
	    DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();  
	    //�ŧi�@�ɮ��ܼ�  
	    Document doc = null;  
	    doc = dbBuilder.parse(inPath);  
	          
	    //��o�S�w���`�I�P�䩳�U�����
	    NodeList list = doc.getElementsByTagName("report");  
	    //System.out.println(list.getLength());
	    
	    File f = new File(outFile);
        if ( !(f.exists()) )
        	f.getParentFile().mkdirs();
	          
	    BufferedWriter bw = new BufferedWriter (new FileWriter(outFile));
	    int writeCount = 0;
	    //�M�X�`�I���U�Ҧ��l�`�I�P�䤺�e  
	    for(int i=0; i<list.getLength(); i++){
	        Element element = (Element)list.item(i);  
	        String id = element.getAttribute("id");  
	            
	        if ( writeCount++ == 0 ){
            	bw.write(id);
            }else{
            	bw.write("," + id);
            }
	    }
	    bw.close();
	}

	//�H proxy �覡�i�� BugZilla ���� XML �ɮפ��U��
	//http://proxy.yzu.edu.tw/proxy.pac
	//proxy.hinet.net prot:8080
	public static void xmlExtracting_proxy (String inURL, String outFile, String inProxy) throws Exception{
		String[] tokens = inProxy.split(":");
		String inHost = tokens[0];
		String inPort = tokens[1];
				
		// Proxy �]�w
		System.setProperty("http.proxyHost", inHost);
		System.setProperty("http.proxyPort", inPort);
		
		File f = new File(outFile);
        if ( !(f.exists()) )
        	f.getParentFile().mkdirs();
				
		// URL �ŧi
		URL pageUrl = new URL(inURL);
		// �}�Ҧ�y
		DataInputStream in = new DataInputStream(pageUrl.openStream());
		RandomAccessFile out = new RandomAccessFile(outFile, "rw");
		try
		{
		    byte data;
		    // �ƻs�ɮ�
		    while(true)
		    {
		    	data = (byte)in.readByte();
		        out.writeByte(data);
		    } 
		}catch(EOFException e) {}
		       
		// ������y
		in.close();   
		out.close();
		        
		// ���� Proxy �]�w
		System.setProperty("http.proxyHost", "");
	}
	
	
}
