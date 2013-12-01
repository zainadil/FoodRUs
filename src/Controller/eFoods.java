package Controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.*;

/**
 * Servlet implementation class Start
 */
@WebServlet("/eFoods/*")
public class eFoods extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public eFoods() {
		super();
	}

	@Override
	public void init() throws ServletException {
		
		try {
			FoodRus fru = new FoodRus();
			this.getServletContext().setAttribute("fru", fru);
//			fru.retrieveBlobs(this.getServletContext().getRealPath("/png/"));
//			String PoNumberFileName = "/POs/numberOfPosPerClient.txt";
//			this.getServletContext().setAttribute("PoNumberFileName", this.getServletContext().getRealPath(PoNumberFileName));
//			checkPoFileExists(PoNumberFileName);
			HashMap<String, Integer> poNumbers = new HashMap<String, Integer>();
			this.getServletContext().setAttribute("poNumbers", poNumbers);
			
		} catch (Exception e) {
			e.printStackTrace();
		} 

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			FoodRus model = (FoodRus) this.getServletContext().getAttribute("fru");
			RequestDispatcher rd;
			HttpSession session = request.getSession();
			String pageURI = request.getRequestURI();

			// check to see if the basket for the session exists, and then load
			// it into the request. (We use this in JSPX)
			if (session.getAttribute("basket") == null) {
				HashMap<String, Integer> basket = new HashMap<String, Integer>();
				session.setAttribute("basket", basket);
			}
			// check to see if the client is logged in or not, load it into the
			// request. (We use this in JSPX)
			if (session.getAttribute("client") != null) {
				request.setAttribute("client", session.getAttribute("client"));
				request.setAttribute("loggedIn", session.getAttribute("loggedIn"));
			}

			// Determine what the Controller should do and where it should be
			// passed to.
			if (pageURI.contains("Category")) {
				category(pageURI, model, request, response);
			} else if (pageURI.contains("Login")) {
				login(pageURI, model, request, response);
			} else if (pageURI.contains("Logout")) {
				logout(pageURI, model, request, response);
			} else if (pageURI.contains("Cart")) {
				cart(pageURI, model, request, response);
			} else if (pageURI.contains("Checkout")) {
				checkout(pageURI, model, request, response);
			} else { // Always fall back to the homepage
				rd = getServletContext().getRequestDispatcher("/views/homePage.jspx");
				rd.forward(request, response);
			}
		} catch (Exception e) {
			// Why we silence problem? No Good.
			System.out.println("Error Caught: " + e);
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

	/***
	 * The client has chosen to logout, check to see if the client is even
	 * logged in (which it should be) and log out. End session.
	 * 
	 * @param pageURI
	 * @param model
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 * 
	 *             TO-DO: Business decision to be made: Should logging out get
	 *             rid of basket or not? Would logging out, save the basket to a
	 *             cookie? and Logging in, load the basket from cookie if it
	 *             exists?
	 */
	private void logout(String pageURI, FoodRus model, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		session.removeAttribute("client");
		System.out.println("Logging Out by..");
		if (request.getAttribute("loggedIn") != null) session.removeAttribute("loggedIn");
		response.sendRedirect(this.getServletContext().getContextPath() + "/eFoods");
	}

	/**
	 * The client has chosen to login. Forward them to the loginPage and see
	 * what they do. (Store the page they came from) If they choose to click on
	 * the loginButton, then we should retrieve the fields from the form. We
	 * should check credentials, if valid it should return to you a
	 * "ClientBean". Redirect to the page they came from. else, if invalid
	 * credentials, keep them on the loginPage and display an error.
	 * 
	 * Note: In ClientBean we do not want to store the password of the user.
	 * 
	 * @param pageURI
	 * @param model
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 * @throws SQLException
	 * 
	 * 
	 */
	private void login(String uri, FoodRus model, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException,
			SQLException {
		RequestDispatcher rd;
		HttpSession session = request.getSession();
		
		ClientBean tmp;
		
		System.out.println("Came in here");
		
		if (request.getParameter("loginButton") != null) {
			boolean loggedIn;
			String accountNumber = request.getParameter("accountNumber");
			request.setAttribute("accountNumber", accountNumber);
			String password = request.getParameter("password");
			
			if ((tmp = model.checkClient(accountNumber, password)) != null) {
				loggedIn = true;
				request.setAttribute("loggedIn", loggedIn);
				session.setAttribute("loggedIn", true);
				session.setAttribute("client", tmp);
				if (session.getAttribute("returnTo") == null) response.sendRedirect(this.getServletContext().getContextPath() + "/eFoods");
				else response.sendRedirect((String) session.getAttribute("returnTo"));
			} else {
				loggedIn = false;
				request.setAttribute("loggedInError", true);
				rd = getServletContext().getRequestDispatcher("/views/loginPage.jspx");
				rd.forward(request, response);
			}
		} else {
			if (request.getAttribute("returnTo") == null )
				session.setAttribute("returnTo", (String) request.getHeader("referer"));
			else
				session.setAttribute("returnTo", request.getAttribute("returnTo"));
			rd = getServletContext().getRequestDispatcher("/views/loginPage.jspx");
			rd.forward(request, response);
		}
	}

	/**
	 * Client has gone to the category page, here we will forward them
	 * automatically to the itemPage.jspx which displays all items of a specific
	 * category. Once the client picks which item they want to purchase or look
	 * at, they will be sent to an item.jspx, where we will wait for them to
	 * addToBasket. Once they add to Basket, we create a HashMap<ItemName,
	 * Quantity>, and update the quantity as necessary.
	 * 
	 * Again we want to keep track of where they came from, and then redirect
	 * them to it once they've added to basket.
	 * 
	 * @param uri
	 * @param model
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	private void category(String uri, FoodRus model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession();
		RequestDispatcher rd;

		if (uri.contains("Order")) {

			if (request.getParameter("addToBasket") != null) {
				String itemID = request.getParameter("itemID");
				int ItemQuantity = Integer.parseInt(request.getParameter("quantity"));
				if (session.getAttribute("basket") == null) throw new Exception();
				HashMap<String, Integer> basket = (HashMap<String, Integer>) session.getAttribute("basket");
				if (basket.containsKey(itemID)) {
					basket.put(itemID, basket.get(itemID) + ItemQuantity);
				} else basket.put(itemID, ItemQuantity);
				session.setAttribute("basket", basket);

				if (session.getAttribute("returnTo") == null) response.sendRedirect(this.getServletContext().getContextPath() + "/eFoods");
				response.sendRedirect((String) session.getAttribute("returnTo"));

			} else {
				session.setAttribute("returnTo", request.getHeader("referer"));
				String itemID = getItemID(uri);
				ItemBean item = model.retrieveItem(itemID);
				request.setAttribute("item", item);
				rd = getServletContext().getRequestDispatcher("/views/item.jspx");
				rd.forward(request, response);
			}
		} else {
			// Do we need this?
			List<CategoryBean> catBean = model.retrieveCategories();
			request.setAttribute("catBean", catBean);
			List<ItemBean> itemList = model.retrieveItems(getCategory(uri));
			request.setAttribute("itemList", itemList);

			rd = getServletContext().getRequestDispatcher("/views/itemPage.jspx");
			rd.forward(request, response);
		}
	}

	/**
	 * Client has chosen to go to the Cart Page (Basket Page). Here the HashMap
	 * from above is displayed, if null then it is shown as empty.
	 * 
	 * Client has the option to continue shopping or to proceed to checkout.
	 * 
	 * The page will also allow modification/updating their cart prior to
	 * purchasing.
	 * 
	 * @param uri
	 * @param model
	 * @param request
	 * @param response
	 * @throws Exception
	 * 
	 */

	private void cart(String uri, FoodRus model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		HttpSession session = request.getSession();
		HashMap<String, Integer> basket = (HashMap<String, Integer>) session.getAttribute("basket");
		
		if(request.getParameter("updateQuantity") != null){
			String updatedIDandQty = request.getParameter("updatedIDandQty");
			System.out.println(updatedIDandQty);
			String[] splits = updatedIDandQty.split(";");
			
			String key = splits[0];
			int quantity = Integer.parseInt(splits[1]);
			if(quantity == 0)
				basket.remove(key);
			else basket.put(key, quantity);
		}
		
		CartBean cartBean = model.generateShopppingCart(basket, (ClientBean) request.getSession().getAttribute("client"));
		request.setAttribute("sCart", cartBean);
		RequestDispatcher rd;

		rd = getServletContext().getRequestDispatcher("/views/cartPage.jspx");
		rd.forward(request, response);
	}

	/***
	 * The client should be sent to a confirmation page of their purchase order,
	 * hence the checkout page. This should also generate the XML necessary for
	 * the B2B aspect.
	 * 
	 * @param uri
	 * @param model
	 * @param request
	 * @param response
	 * @throws Exception
	 * 
	 *             To-Do: Add checkoutPage.jspx and redirect to it. Add
	 *             implementation to Submit P/O which will create the XML
	 *             necessary.
	 * 
	 *             Model.export should NOT be called without a CLIENT otherwise
	 *             major issue.
	 */
	private void checkout(String uri, FoodRus model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		RequestDispatcher rd;
		if (session.getAttribute("client") == null) {
			request.setAttribute("signInRequired", true);
			request.setAttribute("returnTo", (String) request.getHeader("referer"));
			session.setAttribute("returnTo",request.getAttribute("returnTo"));
			login(uri, model, request, response);
		} else {
			CartBean cartBean = model.generateShopppingCart((HashMap<String, Integer>) session.getAttribute("basket"),
					(ClientBean) session.getAttribute("client"));
			HashMap<String, Integer> basket = (HashMap<String, Integer>) session.getAttribute("basket");
			if(basket.size()==0)
			{
				request.setAttribute("noCart", "true");
			}			
			else{
				
//			check if file has been created -- dt know how to do that since we are using a filewriter... 
//			but it it hasnt been created add: (will display that the order wasn't processed)
//			if() request.setAttribute("checkoutError", "true");
//			else
//			check if client exists in PoNumberFileName - if not, append to the file with quantity
//			String qty = this.checkPoQty((String)this.getServletContext().getAttribute("PoNumberFileName"), accountNumber);			

			HashMap<String, Integer> poNumbers = (HashMap<String, Integer>) this.getServletContext().getAttribute("poNumbers");
			String accountNumber = Integer.toString(cartBean.customer.getNumber());
			int qty=1;
			System.out.println("poNumbers.containsKey(accountNumber) : " + poNumbers.containsKey(accountNumber));
			if(poNumbers.size()==0)
			{
				poNumbers.put(accountNumber, qty);
				this.getServletContext().setAttribute("poNumbers", poNumbers);
			}
			else{
				System.out.println("im hereeeeeee sucka");
				System.out.println("poNumbers.containsKey(accountNumber) : " + poNumbers.containsKey(accountNumber));
				if(poNumbers.containsKey(accountNumber))
				{
					System.out.println("it contains the key, update the qty");
					qty = poNumbers.get(accountNumber);
					qty++;
					System.out.println(poNumbers.get(accountNumber));
					poNumbers.put(accountNumber, qty);
					this.getServletContext().setAttribute("poNumbers", poNumbers);
				}
				else
				{
					poNumbers.put(accountNumber, qty);
					this.getServletContext().setAttribute("poNumbers", poNumbers);
				}
				
				System.out.println(poNumbers.get(accountNumber));
			}
			
			
			String filename = "/POs/po" + accountNumber +"_"+ poNumbers.get(accountNumber) +".xml"; 
			String filePath = this.getServletContext().getRealPath(filename);

			request.setAttribute("filename", filename);
			
			model.export(cartBean, filePath);
			
			//null the cart so that they have nothing in their basket 
//			HashMap<String, Integer> sCart = (HashMap<String, Integer>) session.getAttribute("basket");
//			sCart = null;
//			session.setAttribute("sCart", sCart);
//			request.setAttribute("noCart", true);
			
			request.setAttribute("checkoutError", "false");

			
			}
			
			rd = getServletContext().getRequestDispatcher("/views/checkout.jspx");
			rd.forward(request, response);
		}
		

	}

	private String getItemID(String uri) {
		Matcher matcher = Pattern.compile("(?<=Order/).*").matcher(uri);
		matcher.find();
		return matcher.group();
	}

	private String getCategory(String uri) {
		String rv = "";
		if (uri.toUpperCase().contains("MEAT")) rv = "Meat";
		else if (uri.toUpperCase().contains("CEREAL")) rv = "Cereal";
		else if (uri.toUpperCase().contains("ICECREAM")) rv = "Ice Cream";
		else if (uri.toUpperCase().contains("CHEESE")) rv = "Cheese";
		return rv;
	}
	
	private String checkPoQty(String fileDirectory, String accountNumber) throws IOException
	{
		System.out.println(fileDirectory);
		String quantity = null;
	    BufferedReader br = new BufferedReader(new FileReader(fileDirectory)); 
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileDirectory, true));
        String str = br.readLine();

        if(str == null)
        {
        	str = accountNumber + ",1";
        	quantity = "1";
        	bw.write(str, 0, str.length());
            bw.newLine();
        }
        else{
	        while(br.readLine()!= null){
	        	String clientNumber = str.substring(0,str.indexOf(","));
	        	System.out.println("theres a next line to read! ");
	        	if(clientNumber.equals(accountNumber)){
	        		System.out.println("client = account!!!!!!!!!!");
	        		System.out.println("accountNumber: " + accountNumber);
	        		System.out.println("clientNumber: " + clientNumber);

	        		bw = new BufferedWriter(new FileWriter(fileDirectory));
			        int qty = Integer.parseInt(str.substring(str.indexOf(",")+1,str.length()));
		        	qty++;
		        	System.out.println("quantity for client " + clientNumber + " is:" + qty );
		        	quantity = Integer.toString(qty);
		        	System.out.println("quantity: " + quantity);
		        }
	        	else{
	        		System.out.println("immmm hereeeee ");
	        		bw = new BufferedWriter(new FileWriter(fileDirectory, true));
	        		bw.newLine();
	        		System.out.println("clientNumber : " + clientNumber);
	        		str = accountNumber + ",1";
	            	quantity = "1";
	            	System.out.println("quantity: " + quantity);
	            	
	        	}
	        	
	        }
	        str = accountNumber + "," + quantity;
	        System.out.println("str"+ str);
	        bw.write(str, 0, str.length());
            bw.newLine();
            bw.flush();
        
        }
        br.close();
        bw.close();
        return quantity;
	}
	
	
	private boolean checkPoFileExists(String PoNumberFileName) throws IOException 
	{		
//		System.out.println("this.getServletContext().getRealPath(PoNumberFileName) : " + this.getServletContext().getRealPath(PoNumberFileName));
		File file = new File(this.getServletContext().getRealPath(PoNumberFileName));
		boolean exists = false;
  
        if (!file.exists()){	
        	file.createNewFile(); 
        	exists = true; 
        }
        if (exists)
            System.out.println("Empty File successfully created");
        else
            System.out.println("ur an idiot, the file already exists. Path to it: " + this.getServletContext().getRealPath(PoNumberFileName));
		
		return exists;
				
	}
}
