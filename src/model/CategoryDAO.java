package model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

public class CategoryDAO {

	private String DB_URL = "jdbc:derby://roumani.eecs.yorku.ca:9999/CSE;user=student;password=secret;";

	public CategoryDAO() throws ClassNotFoundException {
		Class.forName("org.apache.derby.jdbc.ClientDriver");
	}

	// Retrieval Method
	public List<CategoryBean> retrieveCategories() throws SQLException {

		List<CategoryBean> rv = new LinkedList<CategoryBean>();

		Connection conn = null;
		Statement statement = null;
		ResultSet set = null;

		try {
			conn = DriverManager.getConnection(DB_URL);
			statement = conn.createStatement();
			statement.executeUpdate("SET SCHEMA ROUMANI");
			set = statement.executeQuery("SELECT * FROM Category ORDER BY NAME");

			while (set.next())
				rv.add(new CategoryBean(set.getInt("ID"), new String(set.getString("NAME")), new String(set.getString("DESCRIPTION")), set
						.getBlob("PICTURE")));

		} catch (SQLException e) {
			throw new SQLException("SQL Exception", e);
		} finally {
			if (set != null) set.close();
			if (statement != null) statement.close();
			if (conn != null) conn.close();
		}

		return rv;
	}
	
	public boolean retrieveBlobs(String filename) throws SQLException, IOException {
		boolean rv = false;
		Connection conn = null;
		Statement statement = null;
		ResultSet set = null;
		
		try {
			conn = DriverManager.getConnection(DB_URL);
			statement = conn.createStatement();
			statement.executeUpdate("SET SCHEMA ROUMANI");
			String s = "select category.id, category.name, category.picture from category";
			set = statement.executeQuery(s);
			while (set.next()) {
				InputStream image_stream = set.getBinaryStream("PICTURE");
				BufferedImage image = ImageIO.read(image_stream);
				String file = filename + "\\"+set.getString("NAME").replaceAll("\\s+","")+".png";
				ImageIO.write(image, "png", new File(file));

			}
		} catch (SQLException e) {
			throw new SQLException("SQL Exception", e);
		} finally {
			if (set != null) set.close();
			if (statement != null) statement.close();
			if (conn != null) conn.close();
		}

		return rv;
	}
}
