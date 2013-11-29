package Controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
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
		
		FoodRus fru = new FoodRus();
		HashMap<String, Integer> cart = new HashMap<String, Integer>();
		this.getServletContext().setAttribute("fru", fru);
		this.getServletContext().setAttribute("cart", cart);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try
		{
			FoodRus model = (FoodRus) this.getServletContext().getAttribute("fru");
			RequestDispatcher rd;
			HttpSession session = request.getSession();
			String pageURI = request.getRequestURI();
			request.setAttribute("loggedIn", session.getAttribute("loggedIn"));
			if (pageURI.contains("Category")) {
				category(pageURI, model, request, response);
			} else if (pageURI.contains("Login")){
				login(pageURI,  model, request, response);
			} else if (pageURI.contains("Logout")){
				logout(pageURI,  model, request, response);
			} else if (pageURI.contains("Cart")){
				cart(pageURI,  model, request, response);
			} else {
				rd = getServletContext().getRequestDispatcher("/views/homePage.jspx");
				rd.forward(request, response);
			} 
		}
		catch (Exception e)
		{
			//Why we silence problem? No Good.
			System.out.println("hello " +e);
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

	private void logout(String pageURI, FoodRus model,
			HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();

		System.out.println("Logging Out...");
		//TO-DO implement logout functionality
		// Logging out should clear all session activities? including cart?
		if (request.getAttribute("loggedIn") != null)
			session.removeAttribute("loggedIn");
		//		response.sendRedirect(request.getHeader("referer")); //sendBack to Referer
		response.sendRedirect(this.getServletContext().getContextPath() + "/eFoods");
	}
	private void login(String uri, FoodRus model, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		RequestDispatcher rd;
		HttpSession session = request.getSession();
		ClientBean tmp;
		if(request.getParameter("loginButton") != null){
			System.out.println("Came in here");
			boolean loggedIn;
			String accountNumber = request.getParameter("accountNumber");
			request.setAttribute("accountNumber", accountNumber);
			String password = request.getParameter("password");
				
			if( (tmp = model.checkClient(accountNumber, password)) != null){
				loggedIn = true;
				request.setAttribute("loggedIn", loggedIn);
				session.setAttribute("loggedIn",  true);
				session.setAttribute("client", tmp);
				response.sendRedirect((String) session.getAttribute("returnTo"));
			} else {
				loggedIn=false;
				request.setAttribute("loggedIn", loggedIn);
				rd = getServletContext().getRequestDispatcher("/views/loginPage.jspx");
				rd.forward(request, response);
			}
		} else {
			session.setAttribute("returnTo", (String) request.getHeader("referer"));
			rd = getServletContext().getRequestDispatcher("/views/loginPage.jspx");
			rd.forward(request, response);
		}
	}
	
	private void category(String uri, FoodRus model, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException{
		
		HttpSession session = request.getSession();
		RequestDispatcher rd;
		
		if(uri.contains("Order")){
			if(request.getParameter("addToCart") != null){
				
				String itemID = request.getParameter("itemID");
				int ItemQuantity = Integer.parseInt(request.getParameter("quantity"));
				HashMap<String, Integer> cart = (HashMap<String, Integer>) this.getServletContext().getAttribute("cart");
				
				if(cart.containsKey(itemID)){
					cart.put(itemID, cart.get(itemID) + ItemQuantity);
				} else cart.put(itemID, ItemQuantity);
//				if( request.getAttribute("loggedIn") != null)
//				{
//					model.generateShopppingCart(cart, (ClientBean) session.getAttribute("client"));
//				}
				session.setAttribute("cart", cart);
				response.sendRedirect(this.getServletContext().getContextPath() + "/eFoods/Category/Meat");
				
			} else{
				System.out.println("Doesn't contain order");
				String itemID = getItemID(uri);
				ItemBean item = model.retrieveItem(itemID);
				request.setAttribute("item",item);
				rd = getServletContext().getRequestDispatcher("/views/item.jspx");
				rd.forward(request, response);
			}
		} else {
			
			List<CategoryBean> catBean = model.retrieveCategories();
			request.setAttribute("catBean", catBean);
			List<ItemBean> itemList = model.retrieveItems(getCategory(uri));
			request.setAttribute("itemList", itemList);
			
			//Check if userLogged in
			if(session.getAttribute("loggedIn") != null)
				request.setAttribute("loggedIn", true);
			
		    rd = getServletContext().getRequestDispatcher("/views/itemPage.jspx");
			rd.forward(request, response);
		}
	}
	
	private void cart(String uri, FoodRus model, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException{
		
		HashMap<String, Integer> cart = (HashMap<String, Integer>) this.getServletContext().getAttribute("cart");
		

		RequestDispatcher rd;
		rd = getServletContext().getRequestDispatcher("/views/cartPage.jspx");
		rd.forward(request, response);
	}
	
	private String getItemID(String uri){
		Matcher matcher = Pattern.compile("(?<=Order/).*").matcher(uri);
		matcher.find();
		return matcher.group();
	}
	
	private String getCategory(String uri){	
		String rv = "";
		if(uri.toUpperCase().contains("MEAT"))
			rv = "Meat";
		else if(uri.toUpperCase().contains("CEREAL"))
			rv = "Cereal";
		else if(uri.toUpperCase().contains("ICECREAM"))
			rv = "Ice Cream";
		else if(uri.toUpperCase().contains("CHEESE"))
			rv = "Cheese";
		return rv;
	}	
}
