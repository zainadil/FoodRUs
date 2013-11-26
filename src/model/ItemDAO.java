package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class ItemDAO {
	
private String DB_URL = "jdbc:derby://roumani.eecs.yorku.ca:9999/CSE;user=student;password=secret;";
	
	public ItemDAO() throws ClassNotFoundException
	{
		Class.forName("org.apache.derby.jdbc.ClientDriver");
	} 
	
	//Retrieval Method
	public List<ItemBean> retrieveCategories() throws SQLException{
		
		List<ItemBean> rv = new LinkedList<ItemBean>();
		
		Connection conn = null;
		Statement statement = null;
		ResultSet set = null;
		
		try{
			conn = DriverManager.getConnection(DB_URL);
			statement = conn.createStatement();
			statement.executeUpdate("SET SCHEMA ROUMANI");
			set = statement.executeQuery("SELECT * FROM Category ORDER BY NAME");
		
			while(set.next());
				//rv.add(new ItemBean(set.getString("NUMBER"), set.getString("NAME"), ));
		} catch (SQLException e){
			throw new SQLException("SQL Exception", e);
		} finally{
			if(set != null)set.close();
			if(statement != null)statement.close();
			if(conn != null)conn.close();
		}
		
		return rv;
	}

}
