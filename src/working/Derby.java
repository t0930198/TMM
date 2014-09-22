package working;

import java.sql.*;

public class Derby {

	Connection con = null;
	Statement sta;
	ResultSet res;

	boolean data_exist = false;

	public Derby() {

		// Find the driver for a given URL

		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {

			con = DriverManager
					.getConnection("jdbc:derby://localhost:1527/myDB;create=true;");

			System.out.println(con);
			sta = con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * try { sta.executeUpdate("DROP TABLE BUGREPORT"); } catch
		 * (SQLException e) {
		 * 
		 * }
		 */
		try {
			create_table("CREATE TABLE BUGREPORT (ID INT, PRODUCT VARCHAR(50), COMPONENT VARCHAR(50), SEVERITY  VARCHAR(20), PRIORITY  VARCHAR(20), TOPIC INT, PRIMARY KEY(ID)) ");
		} catch (SQLException e) {
			// TODO Auto-generated catch block

			if (e.getMessage().equals(
					"Table/View 'BUGREPORT' already exists in Schema 'APP'.")) {
				try {
					ResultSet r = sta.executeQuery("select * from bugreport");
					// while(r.next())
					// System.out.println(r.getString(1));
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				data_exist = true;
				System.out.println("data already exist");
			}
			else System.exit(-1);

		}

		// sta.executeUpdate("DELETE * FROM BUGREPORT");
	}

	public boolean isDataExist() {
		return data_exist;
	}

	public void create_table(String cmd) throws SQLException {
		System.out.println(cmd);
		sta.executeUpdate(cmd);

	}

	public ResultSet _select_participate(String pro, String com, String sev,
			String pri, int topic) throws SQLException {
		return res = sta.executeQuery("SELECT ID FROM BUGREPORT WHERE "
				+ " PRODUCT = " + pro + " COMPONENT = " + com + " SEVERITY = "
				+ sev + " PRIOIRTY = " + pri + " TOPIC = " + topic
				+ " TOPIC = " + topic + " ORDER BY ID");
	}

	public void _insert_to_bugreport(int id, String pro, String com,
			String sev, String pri, int topic)
			throws SQLException {
		System.out
				.println("INSERT INTO BUGREPORT(ID, PRODUCT, COMPONENT, SEVERITY, PRIORITY, TOPIC) "
						+ "VALUES("
						+ id
						+ ",'"
						+ pro
						+ "','"
						+ com
						+ "','"
						+ sev
						+ "','"
						+ pri
						+ "',"
						+ topic
						+ ")");
		sta.executeUpdate("INSERT INTO BUGREPORT(ID, PRODUCT, COMPONENT, SEVERITY, PRIORITY, TOPIC) "
				+ "VALUES("
				+ id
				+ ",'"
				+ pro
				+ "','"
				+ com
				+ "','"
				+ sev
				+ "','" + pri + "'," + topic + ")");
	}

	public void _insert_to_triagelist(int id, String name) throws SQLException {
		sta.executeUpdate("INSERT INTO BUGREPORT(ID, NAME) " + "VALUES(" + id
				+ "," + name + ")");
	}

	public void close_DB() throws SQLException {

		// sta.executeUpdate("DROP REPORTS");

		sta.close();

		con.close();
	}

}
