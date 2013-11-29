package model;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
	public ClientBean checkClient(String accountNumber, String password) {
		ClientBean tmp = new ClientBean();
		System.out.println("Tyring to check Credentials");
		if (clientData.checkCredentials(accountNumber, password)){
			tmp = clientData.getClientInfo(accountNumber);
		} else {
			tmp = null;
		}
		return tmp;
	}
	
//**********************************ITEM TABLE METHODS *******************************************
	public List<ItemBean> retrieveItems(String category) throws SQLException
	{
		return itemData.retrieveItems(category);
	}
	
	public ItemBean retrieveItem(String number) throws SQLException
	{
		return itemData.retrieveItem(number);
	}
	 
			/*** Function for Shopping Cart 
			 * @throws SQLException ***/
	public CartBean generateShopppingCart(HashMap<String, Integer> cart, ClientBean client) throws SQLException 
	{
		double hst = 13; // this is wrong. change it.
		CartBean tmp = new CartBean();
		ItemBean item = new ItemBean();
		List<ItemBean> listItem = new LinkedList<ItemBean>();
		double total = 0;
		if (cart == null) {
			return null;
		}
		for(String key: cart.keySet()){
			cart.get(key);
			System.out.println("Key = "
					+ key 
					+ ", Value = "
					+ cart.get(key));
			item = itemData.retrieveItem(key);
			item.toString();
			item.setQty(cart.get(key));
			total += (cart.get(key) * item.getPrice());
			listItem.add(item);
		}
		
		tmp.setCustomer(client);;
		tmp.setItems(listItem);
		tmp.setTotal(total);
		tmp.setHST(hst);
		tmp.setGrandTotal(total * hst);
		System.out.println(tmp.toString());
		return null;
		
	}
	
}//end business Logic
