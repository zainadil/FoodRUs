package model;

import java.sql.SQLException;
import java.util.List;

public class FoodRus 
{
	CategoryDAO categoryData;
	ClientDAO clientData; 
	ItemDAO itemData;
	
	public FoodRus() 
	{
		try {
			categoryData = new CategoryDAO();
			clientData = new ClientDAO();
			itemData = new ItemDAO();
		} 
		catch (ClassNotFoundException e) {
			
			System.out.println("Class not found Exception -- One of the DAO's wasn't created properly");
		}
		
		
	}
 //*********************************CATEGORY TABLE METHODS***************************************
	public List<CategoryBean> retrieveCategories() throws SQLException 
	{
		return categoryData.retrieveCategories();
		
	}
	

//**********************************CLIENT TABLE METHODS ******************************************
	public boolean checkCredentials(String accountNumber, String password) {
		System.out.println("Tyring to check Credentials");
		return clientData.checkCredentials(accountNumber, password);
		
	}
	
//**********************************ITEM TABLE METHODS *******************************************
	public List<ItemBean> retrieveItems(String category) throws SQLException
	{
		return itemData.retrieveItems(category);
	}
	
	
	
	
}//end business Logic
