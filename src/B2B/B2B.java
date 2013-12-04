package B2B;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import model.CategoryDAO;
import model.ClientDAO;
import model.ItemDAO;
import Marshalling.ItemType;
import Marshalling.OrderType;

public class B2B //POJO
{
	
	// Required to be passed as a parameter
	private static final String key = "cse83111p7";
	
	private static final String store_Toronto_tns = "http://red.cse.yorku.ca:4413/axis/YYZ.jws";
	private static final String store_Vancouver_tns = "http://red.cse.yorku.ca:4413/axis/YVR.jws";
	private static final String store_Halifax_tns = "http://red.cse.yorku.ca:4413/axis/YHZ.jws";

	public static void main(String[] args) throws Exception
	{
		// Directory of PO's need to be supplied
		Scanner input = new Scanner(System.in);
		System.out.print("Please enter the path to the folder containing PO's. (example: /home/user/PO_folder/)\n=> ");
		String directory = input.nextLine().trim();
		if (!(new File(directory).isDirectory()))
		{
			System.out.println("No such directory!");
			return;
		}

		try
		{
			process_all_POs(directory);
		}
		catch(Exception e)
		{
			System.out.println("Exception occured:");
			e.printStackTrace();
		}

	}
	
	// Filters out all the xml files in the directory,
	// merges the data from the PO's into a single HashMap
	// containing itemNumber and quantity to order
	public static void process_all_POs(String directory_path) throws Exception
	{
		File directory = new File(directory_path);
		File[] files = directory.listFiles();
		List<String> list = new ArrayList<String>();
		for(File file : files)
		{
			if (file.getAbsolutePath().contains(".xml"))
			{
				list.add(file.getAbsolutePath());
			}
		}
		
		//Store itemNumber, quantity in the map (merge all PO's into one -> group by itemNumber in map)
		HashMap<String, Integer> map_number_quantity = new HashMap<String, Integer>();
		for(String filepath : list)
		{
			merge_POs_into_One(filepath, map_number_quantity);
		}

		//After merge, order everything required
		order_all(map_number_quantity);
	}
	
	public static void order_all(HashMap<String, Integer> map_number_quantity) throws Exception
	{
		//Build the HTML as required
		String html = "";
		
		//As the orders proceed, populate the HTML with results
		html += "<html><body>";		
		html += "<table border=\"1\" cellpadding=\"3\">";
		html += "<tbody><tr>";
		
		//Display results in a table with 8 columns as follows:
		html += "<th>"	+	"Row"			+	"</th>";
		html += "<th>"	+	"Name"			+	"</th>";
		html += "<th>"	+	"Item#"			+	"</th>";
		html += "<th>"	+	"Price"			+	"</th>";		
		html += "<th>"	+	"Quantity"		+	"</th>";
		html += "<th>"	+	"Extended"		+	"</th>";
		html += "<th>"	+	"Wholesaler"	+	"</th>";
		html += "<th>"	+	"Confirmation#"	+	"</th>";
		
		html += "</tr>";
		
		Set<String> map_keys = map_number_quantity.keySet();
		Iterator<String> iterator = map_keys.iterator();
		
		int rownumber = 0;
		while (iterator.hasNext())
		{
			rownumber++;
			
			String itemNumber = iterator.next();
			int quantity = map_number_quantity.get(""+itemNumber);
			
			//Find cheapest wholesaler, and place order for the given itemNumber and quantity
			int tns_choice = find_cheapest_tns(itemNumber);
			String itemName = check_name(itemNumber, tns_choice);
			double price = check_price(itemNumber, tns_choice);
			
			//Format the prices to 2 decimal places
			price = (double)Math.round(price * 100) / 100;
			double extended_price = (double)Math.round(price*(double)quantity * 100) / 100;
			NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
			
			//Place the order
			String confirmation = place_order(itemNumber, tns_choice, quantity);
			String confirmation_number = confirmation.substring(confirmation.indexOf("#")+2);
			
			String wholesaler = "";
			if (tns_choice == 1) wholesaler = "Toronto";
			if (tns_choice == 2) wholesaler = "Vancouver";
			if (tns_choice == 3) wholesaler = "Halifax";
			
			//Display the progress
			System.out.println("Ordering " + quantity + " of " + itemName + " (item# " + itemNumber + ") from " + wholesaler + " Wholesaler: " + confirmation);
			
			//Populate the table with each order
			html += "<td>"	+	rownumber					+	"</td>";
			html += "<td>"	+	itemName					+	"</td>";
			html += "<td>"	+	itemNumber					+	"</td>";
			html += "<td>"	+	nf.format(price)			+	"</td>";
			html += "<td>"	+	quantity					+	"</td>";
			html += "<td>"	+	nf.format(extended_price)	+	"</td>";
			html += "<td>"	+	wholesaler					+	"</td>";
			html += "<td>"	+	confirmation_number			+	"</td>";
			
			html += "</tr>";				
			
		}
		
		html += "</tbody></table>";
		html += "</html></body>";
		createHTML(html, "output.html");
		
	}
	
	// Opens default browser to display the generated HTML
	public static void createHTML(String html, String filepath) throws Exception
	{
		File file = new File(filepath);
		FileWriter writer = new FileWriter(file);
		writer.write(html);
		writer.close();
		
		Desktop.getDesktop().open(file);
	}
	
	// Unmarshall's all Po's, extracts itemNumber and quantity from each, groups results into a HashMap by itemNumber
	public static void merge_POs_into_One(String filepath, HashMap<String, Integer> map_number_quantity) throws JAXBException
	{
		OrderType order = unmarshall_xml(filepath);

		List<ItemType> orders_list = order.getItems().getItem();
		for(ItemType item : orders_list)
		{
			String itemNumber = item.getNumber();
			int new_quantity = item.getQuantity().intValue();
			if (map_number_quantity.containsKey(""+itemNumber))
			{
				int existing_qty = map_number_quantity.get(""+itemNumber);
				existing_qty += new_quantity;
				map_number_quantity.put(""+itemNumber, existing_qty);
			}
			else
			{
				map_number_quantity.put(""+itemNumber, new_quantity);
			}
		}
	}
	
	// Unmarshalls a single PO xml file based on the PO.xsd schema generated JAXB classes
	public static OrderType unmarshall_xml(String xml_filepath) throws JAXBException
	{
		JAXBContext jc = JAXBContext.newInstance(OrderType.class);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		File xml = new File(xml_filepath);
															
		OrderType order = (OrderType) unmarshaller.unmarshal(xml);
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		return order;		
	}
	
	private static int find_cheapest_tns(String itemNumber) throws Exception
	{
		double price_TOR = check_price(itemNumber, store_Toronto_tns);
		double price_VAN = check_price(itemNumber, store_Vancouver_tns);
		double price_HAL = check_price(itemNumber, store_Halifax_tns);
		
		// small fix, if price == -1, make it maxDouble to enable below comparison
		// since we are looking for cheapest of the three
		if (price_TOR == -1) price_TOR = Double.MAX_VALUE;
		if (price_VAN == -1) price_VAN = Double.MAX_VALUE;
		if (price_HAL == -1) price_HAL = Double.MAX_VALUE;
		
		if (price_TOR != -1 && price_TOR <= price_VAN && price_TOR <= price_HAL)
		{
			return 1; //Toronto is cheapest
		}
		else if (price_VAN != -1 && price_VAN <= price_TOR && price_VAN <= price_HAL)
		{
			return 2; //Vancouver is cheapest
		}
		else if (price_HAL != -1 && price_HAL <= price_TOR && price_HAL <= price_VAN)
		{
			return 3; //Halifax is cheapest
		}
		return 0;
	}
	
	// Places the order for a specific itemNumber and quantity, provided the wholesaler choice
	public static String place_order(String itemNumber, int tns_choice, int quantity) throws Exception
	{
		String tns = "";
		if (tns_choice == 1) tns = store_Toronto_tns;
		if (tns_choice == 2) tns = store_Vancouver_tns;
		if (tns_choice == 3) tns = store_Halifax_tns;
		
		SOAPMessage msg = MessageFactory.newInstance().createMessage();
		MimeHeaders header = msg.getMimeHeaders();
		header.addHeader("SOAPAction", "");
		SOAPPart soap = msg.getSOAPPart();
		SOAPEnvelope envelope = soap.getEnvelope();
		SOAPBody body = envelope.getBody();	
		
		SOAPElement root = body.addChildElement("order");
		root.addChildElement("itemNumber").addTextNode(itemNumber);
		root.addChildElement("quantity").addTextNode(""+quantity);
		root.addChildElement("key").addTextNode(key);
		
		SOAPConnection sc = SOAPConnectionFactory.newInstance().createConnection();
		SOAPMessage resp = sc.call(msg, new URL(tns));
		sc.close();
		
		org.w3c.dom.Node node = resp.getSOAPPart().getEnvelope().getBody().getElementsByTagName("orderReturn").item(0);
		return node.getTextContent();		
	}

	// Returns the item name for a given itemNumber
	public static String check_name(String itemNumber, int tns_choice) throws Exception
	{
		String tns = "";
		
		if (tns_choice == 1) tns = store_Toronto_tns;
		if (tns_choice == 2) tns = store_Vancouver_tns;
		if (tns_choice == 3) tns = store_Halifax_tns;
		
		SOAPMessage msg = MessageFactory.newInstance().createMessage();
		MimeHeaders header = msg.getMimeHeaders();
		header.addHeader("SOAPAction", "");
		SOAPPart soap = msg.getSOAPPart();
		SOAPEnvelope envelope = soap.getEnvelope();
		SOAPBody body = envelope.getBody();	
		
		SOAPElement root = body.addChildElement("getName");
		root.addChildElement("itemNumber").addTextNode(itemNumber);
		
		SOAPConnection sc = SOAPConnectionFactory.newInstance().createConnection();
		SOAPMessage resp = sc.call(msg, new URL(tns));
		sc.close();
		
		org.w3c.dom.Node node = resp.getSOAPPart().getEnvelope().getBody().getElementsByTagName("getNameReturn").item(0);
		return node.getTextContent();		
	}

	// Returns the price of an item from a specified wholesaler
	// Returns -1 if the wholesaler does not have the product
	public static double check_price(String itemNumber, int tns_choice) throws Exception
	{
		String tns = "";
		
		if (tns_choice == 1) tns = store_Toronto_tns;
		if (tns_choice == 2) tns = store_Vancouver_tns;
		if (tns_choice == 3) tns = store_Halifax_tns;
		
		SOAPMessage msg = MessageFactory.newInstance().createMessage();
		MimeHeaders header = msg.getMimeHeaders();
		header.addHeader("SOAPAction", "");
		SOAPPart soap = msg.getSOAPPart();
		SOAPEnvelope envelope = soap.getEnvelope();
		SOAPBody body = envelope.getBody();	
		
		SOAPElement root = body.addChildElement("quote");
		root.addChildElement("itemNumber").addTextNode(itemNumber);
		SOAPConnection sc = SOAPConnectionFactory.newInstance().createConnection();
		SOAPMessage resp = sc.call(msg, new URL(tns));
		sc.close();
		
		org.w3c.dom.Node node = resp.getSOAPPart().getEnvelope().getBody().getElementsByTagName("quoteReturn").item(0);
		double result = -1;
		try
		{
			result = Double.parseDouble(node.getTextContent());
		} catch (Exception e)
		{
			System.out.println(e);
		}		
		return result;	
	}
	
	// Returns the price of an item from a specified wholesaler
	// Returns -1 if the wholesaler does not have the product
	public static double check_price(String itemNumber, String tns) throws Exception
	{
		SOAPMessage msg = MessageFactory.newInstance().createMessage();
		MimeHeaders header = msg.getMimeHeaders();
		header.addHeader("SOAPAction", "");
		SOAPPart soap = msg.getSOAPPart();
		SOAPEnvelope envelope = soap.getEnvelope();
		SOAPBody body = envelope.getBody();	
		
		SOAPElement root = body.addChildElement("quote");
		root.addChildElement("itemNumber").addTextNode(itemNumber);		
		SOAPConnection sc = SOAPConnectionFactory.newInstance().createConnection();
		SOAPMessage resp = sc.call(msg, new URL(tns));
		sc.close();
		
		org.w3c.dom.Node node = resp.getSOAPPart().getEnvelope().getBody().getElementsByTagName("quoteReturn").item(0);
		double result = -1;
		try
		{
			result = Double.parseDouble(node.getTextContent());
		} catch (Exception e)
		{
			System.out.println(e);
		}
		
		return result;		
	}
	
	
}