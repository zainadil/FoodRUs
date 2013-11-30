package model;

import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;
import java.util.*;
import javax.imageio.ImageIO;

public class ItemDAO {

	private String DB_URL = "jdbc:derby://roumani.eecs.yorku.ca:9999/CSE;user=student;password=secret;";

	public ItemDAO() throws ClassNotFoundException {
		Class.forName("org.apache.derby.jdbc.ClientDriver");
	}

	// Retrieval Method
	public List<ItemBean> retrieveItems(String category) throws SQLException {

		List<ItemBean> rv = new LinkedList<ItemBean>();

		Connection conn = null;
		Statement statement = null;
		ResultSet set = null;

		// System.out.println("category picked is: " + category);

		try {
			conn = DriverManager.getConnection(DB_URL);
			statement = conn.createStatement();
			statement.executeUpdate("SET SCHEMA ROUMANI");

			String s = "select item.number, " + "item.name, item.price, item.qty, item.onOrder,"
					+ " item.reOrder, item.catId, item.supId, item.CostPrice," + " item.unit, category.id from item, category "
					+ "where item.catID = category.id AND category.name = '" + category + "'";
			
			set = statement.executeQuery(s);
			while (set.next()) {
				rv.add(new ItemBean(set.getString("NUMBER"), set.getString("NAME"), set.getDouble("PRICE"), set.getInt("QTY"), set.getInt("ONORDER"),
						set.getInt("REORDER"), set.getInt("CATID"), set.getInt("SUPID"), set.getDouble("COSTPRICE"), set.getString("UNIT")));
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

	public ItemBean retrieveItem(String number) throws SQLException {
		ItemBean rv = new ItemBean();
		Connection conn = null;
		Statement statement = null;
		ResultSet set = null;
		try {
			conn = DriverManager.getConnection(DB_URL);
			statement = conn.createStatement();
			statement.executeUpdate("SET SCHEMA ROUMANI");
			String s = "select * from item where item.number = '" + number + "'";
			set = statement.executeQuery(s);
			while (set.next()) {
				rv = new ItemBean(set.getString("NUMBER"), set.getString("NAME"), set.getDouble("PRICE"), set.getInt("QTY"), set.getInt("ONORDER"),
						set.getInt("REORDER"), set.getInt("CATID"), set.getInt("SUPID"), set.getDouble("COSTPRICE"), set.getString("UNIT"));
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
				ImageIO.write(image, "png", new File(filename+set.getString("NAME")+".png"));

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
