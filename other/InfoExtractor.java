import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;



public class InfoExtractor implements Runnable{

	/**
	 * @param args
	 * args[0] = Project Name
	 * args[1] = Product Name
	 * args[2] = Component Name
	 *
	 * Eclipse/Mozilla
	 * 		Component 1
	 * 			bug_id 1
	 * 				attach.txt
	 * 				severity.txt
	 * 				summary.txt
	 * 				thetext.txt
	 * 				who.txt
	 * 			...
	 * 			bug_id n
	 * 				...
	 * 		...		
	 * 		Component n
	 * 
	 */
		
	//Input Path
	static String productPath = "DATA/Productbug_id/";
	static String componentPath = "DATA/Componentbug_id/";
	
	//Output Path
	static String productPath_formatted = "DATA/Formattedbug_id/Productbug_id/";
	static String componentPath_formatted = "DATA/Formattedbug_id/Componentbug_id/";
	
	//Thread ID
	String tid;
	String project;
	String product;
	String component;
	
	//Constructor
	public InfoExtractor(String in_tid, String in_project, String in_product, String in_component){
		tid = in_tid;
		project = in_project;
		product = in_product;
		component = in_component;
	}
	
	//Implements Runnable Method
	public void run(){
		System.out.println("This is thread [" + tid + "] (" + project + "-" + product + ":" + component + "):");
		
		//Get path
		String inPath_product = productPath + project + "/" + product + "/bugs/";
		String inPath_component = componentPath + project + "/" + product + "/" + component + "/bugs/";
		String outPath_product = productPath_formatted + project + "/" + product + "/";
		String outPath_component = componentPath_formatted + project + "/" + product + "/" + component + "/";
		System.out.println(inPath_component);
		//---------- Component Part ----------
		File f = new File(inPath_component);
		String[] filenames = f.list();
		if(filenames.length==0){
			System.out.println("path error");
			System.exit(0);
		}
		int count = 1;
		int times = 1;
		for (String s:filenames){
			
			String[] tokens = s.split("\\.");
			String inFile = inPath_component + s;
			String outPath = outPath_component;
			
//			f = new File(outPath);
//			if ( !(f.exists()) )
//				f.mkdirs();
				
			//System.out.println(tokens[0] + "\n" + inFile + "\n" + outPath + "\n");
				
			
			File f2 = new File(outPath + tokens[0] + "/");
			if ( !f2.exists() ){
				System.out.println("[COMPONENT Extracting]bug_id file = " + s);
					
				try {
					xmlParsing_(inFile, outPath);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				
			float percentage = ((float)count++/filenames.length)*100;
			if ( percentage >= (10*times) || percentage >= 100 ){
				System.out.println("	COMPONENT Part [" + tid + "] -> " + percentage + "%");
				times++;
				//break;
			}
			//break;
		}
		//System.out.println(filenames.length);
		//---------- Component Part ----------
		
		//---------- Product Part ----------
		f = new File(inPath_product);
		filenames = f.list();
					
		count = 1;
		times = 1;
		for (String s:filenames){
			String[] tokens = s.split("\\.");
			String inFile = inPath_product + s;
			String outPath = outPath_product;
			
//			f = new File(outPath);
//			if ( !(f.exists()) )
//				f.mkdirs();
						
			//System.out.println(tokens[0] + "\n" + inFile + "\n" + outPath + "\n");
						
			//�w��|���s�b���ؿ�i���^��ʧ@ - 20140510
			File f2 = new File(outPath + tokens[0] + "/");
			if ( !f2.exists() ){
				System.out.println("[PRODUCT Extracting]bug_id file = " + s);
							
				try {
					xmlParsing_(inFile, outPath);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
						
			float percentage = ((float)count++/filenames.length)*100;
			if ( percentage >= (10*times) || percentage >= 100 ){
				System.out.println("	PRODUCT Part [" + tid + "] -> " + percentage + "%");
				times++;
				//break;
			}
			//break;
		}
		//System.out.println(filenames.length);
		//---------- Product Part ----------
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		long StartTime = System.currentTimeMillis(); 		// ��X�ثe�ɶ�
		
		//xmlParsing_("DATA/test_xml.xml", "DATA/");
		
		//Temp Setting for args[]
//		args = new String[3];
//		args[0] = "eclipse";
//		args[1] = "Platform";
//		args[2] = "UI";
				
		Thread t = new Thread(new InfoExtractor("0", args[0], args[1], args[2]));
		t.start();
		t.join();
		
//		Thread t1 = new Thread(new InfoExtractor("1", "eclipse", "Platform", "UI"));
//		Thread t2 = new Thread(new InfoExtractor("2", "eclipse", "JDT", "UI"));
//		Thread t3 = new Thread(new InfoExtractor("3", "mozilla", "Core", "Layout"));
//		Thread t4 = new Thread(new InfoExtractor("4", "mozilla", "Firefox", "Security"));
//		
//		t1.start();
//		t2.start();
//		t3.start();
//		t4.start();
//		
//		t1.join();
//		t2.join();
//		t3.join();
//		t4.join();
		
		long ProcessTime = System.currentTimeMillis() - StartTime; 			// �p��B�z�ɶ�
		System.out.println("\n===== Information Extracting Process Time = " + (float)ProcessTime/1000 + " s =====\n");
	}
	
	//XML Parser
	public static void xmlParsing_ (String inXml, String outPath) throws Exception{
		
		    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();  
		    
		    DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();  
		   
		    Document doc = null;  
		    doc = dbBuilder.parse(inXml);  
		          
		    
		    NodeList list = doc.getElementsByTagName("bug");
		    Element element = (Element)list.item(0);
		    
		    
		    String bug_id = element.getElementsByTagName("bug_id").item(0).getFirstChild().getNodeValue();
		    //System.out.println( bug_id );
		    
		    
		    String bug_severity = element.getElementsByTagName("bug_severity").item(0).getFirstChild().getNodeValue();
		    //System.out.println( bug_severity );
		    
		    
		    String short_desc = element.getElementsByTagName("short_desc").item(0).getFirstChild().getNodeValue();
		    //System.out.println( short_desc );
		    
		    
		    list = element.getElementsByTagName("attachment");
		    int numAttach = list.getLength();
		    int numAttach_valid = 0;
		    for (int i=0; i<numAttach; i++){
		    	Element element_child = (Element)list.item(i);
		    	String isobsolete = element_child.getAttribute("isobsolete");
			    if ( isobsolete.equals("0") ){
			    	numAttach_valid++;
			    }	
		    }
		    //System.out.println( numAttach_valid );
		    
		    
		    list = element.getElementsByTagName("long_desc");
		    
		    String thetext = "";
		    try{
		    	thetext = element.getElementsByTagName("thetext").item(0).getFirstChild().getNodeValue();
		    }catch(Exception e){
		    	BufferedWriter tmpBW = new BufferedWriter (new FileWriter("DATA/Formatingbug_id/ExtractingErrorFile.txt", true));
		    	tmpBW.write(bug_id + "\n");
		    	tmpBW.write("list.getLength() = " + list.getLength() + ", ");
		    	tmpBW.write("thetext.length() = " + thetext.length() + "\n");
		    	tmpBW.close();
		    }
		    //String thetext = element.getElementsByTagName("thetext").item(0).getFirstChild().getNodeValue();
		    //System.out.println( thetext );
		    
		    //��o developers ( item(i) ��ܲ� i �� post �� who_id)
		    list = element.getElementsByTagName("who");  
		    int numWho = list.getLength();
		    String[] who = new String[numWho];
		    for (int i=0; i<numWho; i++){
		    	who[i] = element.getElementsByTagName("who").item(i).getFirstChild().getNodeValue();
			    //System.out.println( who[i] );
		    }
		    //System.out.println("Have " + numWho + " posts in bug_id = " + bug_id);
		    
		    //BufferedWriter bw = new BufferedWriter (new FileWriter(outPath + "test_out_id.txt"));
		    //bw.write(bug_id);
		    //bw.close();
		    
		    outPath += bug_id + "/";
		    
		    File f = new File(outPath);
			if ( !f.exists() ){
				f.mkdirs();
			}
		    
		    BufferedWriter bw = new BufferedWriter (new FileWriter(outPath + "_severity.txt"));
		    bw.write(bug_severity);
		    bw.close();
		    
		    bw = new BufferedWriter (new FileWriter(outPath + "_summary.txt"));
		    bw.write(short_desc);
		    bw.close();
		    
		    bw = new BufferedWriter (new FileWriter(outPath + "_attach.txt"));
		    bw.write("" + numAttach_valid);
		    bw.close();
		    
		    bw = new BufferedWriter (new FileWriter(outPath + "_thetext.txt"));
		    bw.write(thetext);
		    bw.close();
		    
		    bw = new BufferedWriter (new FileWriter(outPath + "_who.txt"));
		    for (int i=0; i<numWho; i++){
		    	if ( i != numWho-1)
		    		bw.write(who[i] + "\n");
		    	else
		    		bw.write(who[i]);
		    }
		    bw.close();
	}
	
	
}
