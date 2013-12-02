package model;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamResult;

import Marshalling.CustomerType;
import Marshalling.ItemType;
import Marshalling.ItemsType;
import Marshalling.OrderType;

public class FoodRus {
	CategoryDAO categoryData;
	ClientDAO clientData;
	ItemDAO itemData;

	public FoodRus() throws Exception {
			categoryData = new CategoryDAO();
			clientData = new ClientDAO();
			itemData = new ItemDAO();
	}

	/*** CATEGORY TABLE METHODS **/
	public List<CategoryBean> retrieveCategories() throws SQLException {
		return categoryData.retrieveCategories();
	}

	/**
	 * CLIENT TABLE METHODS
	 * 
	 * @throws SQLException
	 **/
	public ClientBean checkClient(String accountNumber, String password) throws SQLException {
		ClientBean tmp = new ClientBean();
		if (clientData.checkCredentials(accountNumber, password)) {
			tmp = clientData.getClientInfo(accountNumber);
		} else {
			tmp = null;
		}
		return tmp;
	}

	/** ITEM TABLE METHODS ***/
	public List<ItemBean> retrieveItems(String category) throws SQLException {
		return itemData.retrieveItems(category);
	}

	public ItemBean retrieveItem(String number) throws SQLException {
		return itemData.retrieveItem(number);
	}

	/***
	 * Function for Shopping Cart
	 * @throws Exception 
	 ***/
	public CartBean generateShopppingCart(HashMap<String, Integer> basket, ClientBean client) throws Exception {		
		double hst = 13; // this is wrong. change it.
		CartBean tmp = new CartBean();
		ItemBean item = new ItemBean();
		List<ItemBean> listItem = new LinkedList<ItemBean>();
		double total = 0;
		
		for (String key : basket.keySet()) {
			basket.get(key);
			item = itemData.retrieveItem(key);
			item.setQty(basket.get(key));
			item.setExtendedPrice(item.getQty()*item.getPrice());
			total += (basket.get(key) * item.getPrice());
			listItem.add(item);
		}

		tmp.setCustomer(client);
		tmp.setItems(listItem);
		tmp.setTotal(total);
		tmp.setHST(hst);
		tmp.setGrandTotal(total * ((hst / 100) + 1));
		return tmp;
	}

	public boolean retrieveBlobs(String filename) throws SQLException, IOException{
		return categoryData.retrieveBlobs(filename);
	}
	
	public boolean export(CartBean cart, String filename) throws Exception {
		boolean res = false;
		int x = 120;
		//change
		Date now = new Date();

		OrderType lw = createOrderType(x, now, cart);
		JAXBContext jc = JAXBContext.newInstance(lw.getClass());
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

		StringWriter sw = new StringWriter();
		sw.write("\n");
		marshaller.marshal(lw, new StreamResult(sw));
		//System.out.println("wah");
		System.out.println(sw.toString()); // for debugging
		FileWriter fw = new FileWriter(filename);
		fw.write(sw.toString());
		fw.close();
		res = true;
		return res;
	}

	private OrderType createOrderType(int x, Date now, CartBean cart) throws Exception {
		OrderType lw = new OrderType();
		// attributes of <order>
		lw.setId(new BigInteger("" + x));

		GregorianCalendar c = new GregorianCalendar();
		c.setTime(now);
		XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		lw.setSubmitted(date);

		// customer information
		CustomerType customer = new CustomerType();
		customer.setAccount(cart.getCustomer().getNumber() + "");
		customer.setName(cart.getCustomer().getName());
		lw.setCustomer(customer);

		// items
		ItemsType items = new ItemsType();
		List<ItemType> listItems = new ArrayList<ItemType>();
		for (ItemBean tmp : cart.getItems()) {
			ItemType item = new ItemType();

			item.setNumber(tmp.getNumber());
			item.setName(tmp.getName());
			item.setPrice(new BigDecimal("" + tmp.getPrice()));
			item.setQuantity(new BigInteger("" + tmp.getQty()));
			item.setExtended(new BigDecimal("" + (tmp.getPrice() * tmp.getQty())));

			listItems.add(item);
		}
		items.setItem(listItems);
		lw.setItems(items);

		// remaining elements
		lw.setTotal(new BigDecimal("" + cart.getTotal()));
		lw.setShipping(new BigDecimal("" + cart.getShipping()));
		lw.setHST(new BigDecimal("" + cart.getHST()));
		lw.setGrandTotal(new BigDecimal("" + cart.getGrandTotal()));
		return lw;
	}
	
	public String getItemName(String itemID) throws SQLException{
		return itemData.getItemName(itemID);
	}
}// end business Logic
