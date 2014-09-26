package working;

import java.util.HashMap;
import java.util.Vector;


public class Triager {
	
	private Vector<String> candidate;
	String[] topic_list;
	Vector<Report> train;
	
	private Report report;
		
	private static HashMap assignee;
	private static HashMap attacher;
	private String commit = "";
	private String comment = "";
	
	public Triager(String[] topic_list, Vector<Report> train, HashMap assignee, HashMap attacher){
		this.topic_list = topic_list;
		this.train = train;
		this.assignee = assignee;
		this.attacher = attacher;
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
					if(!candidate.contains(s))
						candidate.add(s);
				}
			}
		}
	}
	
	private double aScore(String developer){
		assignee.get(developer);
		return 1;
	}
	
	private double sScore(String developer){
		return 1;
	}
	
	private double[] dScore(String developer){
		double[] dscore = new double[9];
		for (int i=1; i<10; i++){
			double r = i*0.1;
			dscore[i] = r*aScore(developer)+(1-r)*sScore(developer);
		}
		return dscore;
	}

}
