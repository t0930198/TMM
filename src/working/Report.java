package working;

import java.util.List;
import java.util.Vector;

public class Report {

	private String bug_id = "";
	private String product = "";
	private String component = "";
	private String priority = "";
	private String severity = "";
	private Vector<String> developer_list = null;
	private String short_desc = "";
	private String long_desc = "";

	public Report(String bug_id, String product, String component,
			String priority, String severity, Vector<String> developer_list,
			String short_desc, String long_desc) {
		super();
		this.bug_id = bug_id;
		this.product = product;
		this.component = component;
		this.priority = priority;
		this.severity = severity;
		this.developer_list = developer_list;
		this.short_desc = short_desc;
		this.long_desc = long_desc;

	}

	public Report() {
		this.developer_list = new Vector<String>();
	}

	public void addDeveloper(String developer) {
		developer_list.add(developer);
	}

	public String getBug_id() {
		return bug_id;
	}

	public void setBug_id(String bug_id) {
		this.bug_id = bug_id;
	}

	public List<String> getDeveloper_list() {
		return developer_list;
	}

	public void setDeveloper_list(Vector<String> developer_list) {
		this.developer_list = developer_list;
	}

	public String getShort_desc() {
		return short_desc;
	}

	public void setShort_desc(String short_desc) {
		this.short_desc = short_desc;
	}

	public String getLong_desc() {
		return long_desc;
	}

	public void setLong_desc(String long_desc) {
		this.long_desc = long_desc;
	}

	public String getProduct() {
		return product;
	}

	public String getComponent() {
		return component;
	}

	public String getPriority() {
		return priority;
	}

	public String getSeverity() {
		return severity;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public Vector<String> getDev(String product, String component,
			String priority, String severity) {
		if (this.product.equals(product) && this.component.equals(component)
				&& this.priority.equals(priority)
				&& this.severity.equals(severity))
			return developer_list;
		else
			return null;
	}
}
