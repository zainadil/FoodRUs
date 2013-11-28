package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class ClientDAO 
{
		
	private String DB_URL = "jdbc:derby://roumani.eecs.yorku.ca:9999/CSE;user=student;password=secret;";
	
	public ClientDAO() throws ClassNotFoundException
	{
		Class.forName("org.apache.derby.jdbc.ClientDriver");
	} 
	
	
		public List<ClientBean> retrieveCategories() throws SQLException{
			
			List<ClientBean> rv = new LinkedList<ClientBean>();
			
			Connection conn = null;
			Statement statement = null;
			ResultSet set = null;
			
			try{
				conn = DriverManager.getConnection(DB_URL);
				statement = conn.createStatement();
				statement.executeUpdate("SET SCHEMA ROUMANI");
				set = statement.executeQuery("SELECT * FROM Client ORDER BY NAME");
			
				while(set.next())
					rv.add(new ClientBean(set.getString("RATING"), set.getString("PASSWORD"), set.getString("NAME"), set.getInt("NUMBER")));
							
			
			} catch (SQLException e){
				throw new SQLException("SQL Exception", e);
			} finally{
				if(set != null)set.close();
				if(statement != null)statement.close();
				if(conn != null)conn.close();
			}
			
			return rv;
		}


		public boolean checkCredentials(String accountNumber, String password) 
		{
			//by default result is false --> we dt want the client to be logged in
			boolean result = false;
			List<ClientBean> rv = new LinkedList<ClientBean>();
			Connection conn = null;
			Statement statement = null;
			ResultSet set = null;
			int accountN = Integer.parseInt("accountNumber");
			try {
				conn = DriverManager.getConnection(DB_URL);
			
			statement = conn.createStatement();
			statement.executeUpdate("SET SCHEMA ROUMANI");
			set = statement.executeQuery("SELECT * from CLIENT WHERE NUMBER = " + accountN + " AND password= " + password);
			
			while(set.next())
				rv.add(new ClientBean(set.getString("RATING"), set.getString("PASSWORD"), set.getString("NAME"), set.getInt("NUMBER")));
					
			if(rv.size() > 0)
				result=true;
			else
				result=false;
			
			
			} catch (SQLException e) {
				System.out.println("problem in checkAccountNumber in clientDAO -- method: checkAccountNumber");
				e.printStackTrace();
			}
			return result;
			
		}


	

}//end ClientDAO class
