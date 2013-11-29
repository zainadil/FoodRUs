package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class ClientDAO {

	private String DB_URL = "jdbc:derby://roumani.eecs.yorku.ca:9999/CSE;user=student;password=secret;";

	public ClientDAO() throws ClassNotFoundException {
		Class.forName("org.apache.derby.jdbc.ClientDriver");
	}

	public boolean checkCredentials(String accountNumber, String password) throws SQLException {
		// by default result is false --> we dt want the client to be logged in
		boolean result = false;
		Connection conn = null;
		Statement statement = null;
		ResultSet set = null;

		try {
			conn = DriverManager.getConnection(DB_URL);
			statement = conn.createStatement();
			statement.executeUpdate("SET SCHEMA ROUMANI");
			String q = "SELECT * from CLIENT WHERE NUMBER = " + accountNumber + " AND password = '" + password + "'";
			set = statement.executeQuery(q);

			if (set.next()) result = true;
			else result = false;
		} catch (SQLException e) {
			throw new SQLException("SQL Exception", e);
		} finally {
			if (set != null) set.close();
			if (statement != null) statement.close();
			if (conn != null) conn.close();
		}
		return result;
	}

	public ClientBean getClientInfo(String accountNumber) throws SQLException {
		// by default result is false --> we dt want the client to be logged in
		Connection conn = null;
		Statement statement = null;
		ResultSet set = null;
		ClientBean tmp = null;
		try {
			conn = DriverManager.getConnection(DB_URL);
			statement = conn.createStatement();
			statement.executeUpdate("SET SCHEMA ROUMANI");
			String q = "SELECT * from CLIENT WHERE NUMBER = " + accountNumber;
			set = statement.executeQuery(q);

			if (set.next()) {
				tmp = new ClientBean(set.getString("RATING"), set.getString("NAME"), set.getInt("NUMBER"));

			}
		} catch (SQLException e) {
			throw new SQLException("SQL Exception", e);
		} finally {
			if (set != null) set.close();
			if (statement != null) statement.close();
			if (conn != null) conn.close();
		}

		return tmp;
	}
}