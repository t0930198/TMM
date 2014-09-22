import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.URL;


public class ProxyDownloader {

	
	
	//BugZilla URL input
	static String eclipse_url  = "https://bugs.eclipse.org/bugs/show_bug.cgi?ctype=xml&id=";
         
	//Output path
	static String outPath = "DATA/eclipse_xml/";
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		long StartTime = System.currentTimeMillis(); 		
		
		int begin_id = Integer.parseInt(args[0]);
		int end_id = Integer.parseInt(args[1]);
		int numBR = (end_id - begin_id) + 1;
		int count = 1;
		
		File f = new File(outPath);
		if ( !f.exists() ){
			f.mkdirs();
		}
		
		//Proxy Downloading
		for (int idx=begin_id; idx<end_id; idx++){
			f = new File(outPath + idx + ".xml");
			if ( !f.exists() ){
				xmlExtracting_proxy(eclipse_url + idx, outPath + idx + ".xml", "proxy.yzu.edu.tw/proxy.pac:8080");
			}
			System.out.println("Now proxy parsing at id = " + idx + ", no." + count + " bug(" + ((float)count++/numBR)*100 + "%).");
		}
		
		//File Checking
		xmlChecking(outPath, begin_id, end_id);
		
		long ProcessTime = System.currentTimeMillis() - StartTime; 			
		System.out.println("\n===== Proxy Downloading Process Time = " + (float)ProcessTime/1000 + " s =====\n");
	}

	
	//http://proxy.yzu.edu.tw/proxy.pac
	//proxy.hinet.net prot:8080
	public static void xmlExtracting_proxy (String inURL, String outFile, String inProxy) throws Exception{
		String[] tokens = inProxy.split(":");
		String inHost = tokens[0];
		String inPort = tokens[1];
			
		
		//System.setProperty("http.proxyHost", inHost);
		//System.setProperty("http.proxyPort", inPort);
			
		
		URL pageUrl = new URL(inURL);
		
	    DataInputStream in = new DataInputStream(pageUrl.openStream());
	    RandomAccessFile out = new RandomAccessFile(outFile, "rw");
	    try
	    {
	        byte data;
	        
	        while(true)
	        {
	        	data = (byte)in.readByte();
	        	out.writeByte(data);
	        } 
	    }
	    catch(EOFException e) {}
	       
	    
	    in.close();   
	    out.close();
	        
	    
	    System.setProperty("http.proxyHost", "");
	}
	
	//File checking
	public static void xmlChecking(String path, int beginID, int endID){
		int num = (endID - beginID) + 1;
		for (int id=0; id<num; id++){
			File f = new File(outPath + id + ".xml");
			if ( !f.exists() ){
				System.out.println("File " + id + ".xml is missing.");
			}
		}
		System.out.println("(Between bug_id " + beginID + " to " + endID + ")");
	}
		
}
