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
		this.getServletContext().setAttribute("fru", fru);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			FoodRus model = (FoodRus) this.getServletContext().getAttribute("fru");
			RequestDispatcher rd;
			HttpSession session = request.getSession();
			String pageURI = request.getRequestURI();
			request.setAttribute("loggedIn", session.getAttribute("loggedIn"));
			if (session.getAttribute("basket") == null) {
				HashMap<String, Integer> basket = new HashMap<String, Integer>();
				session.setAttribute("basket", basket);
			}

			if (pageURI.contains("Category")) {
				category(pageURI, model, request, response);
			} else if (pageURI.contains("Login")) {
				login(pageURI, model, request, response);
			} else if (pageURI.contains("Logout")) {
				logout(pageURI, model, request, response);
			} else if (pageURI.contains("Cart")) {
				cart(pageURI, model, request, response);
			} else {
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

	private void logout(String pageURI, FoodRus model, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();

		System.out.println("Logging Out...");
		if (request.getAttribute("loggedIn") != null) session.removeAttribute("loggedIn");
		response.sendRedirect(this.getServletContext().getContextPath() + "/eFoods");
	}

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
				response.sendRedirect((String) session.getAttribute("returnTo"));
			} else {
				loggedIn = false;
				request.setAttribute("loggedInError", loggedIn);
				rd = getServletContext().getRequestDispatcher("/views/loginPage.jspx");
				rd.forward(request, response);
			}
		} else {
			session.setAttribute("returnTo", (String) request.getHeader("referer"));
			rd = getServletContext().getRequestDispatcher("/views/loginPage.jspx");
			rd.forward(request, response);
		}
	}

	private void category(String uri, FoodRus model, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException,
			SQLException {

		HttpSession session = request.getSession();
		RequestDispatcher rd;

		if (uri.contains("Order")) {

			if (request.getParameter("addToBasket") != null) {
				String itemID = request.getParameter("itemID");
				int ItemQuantity = Integer.parseInt(request.getParameter("quantity"));
				HashMap<String, Integer> basket = (HashMap<String, Integer>) session.getAttribute("basket");
				if (basket.containsKey(itemID)) {
					System.out.println("2");
					basket.put(itemID, basket.get(itemID) + ItemQuantity);
				} else basket.put(itemID, ItemQuantity);
				session.setAttribute("basket", basket);
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
			List<CategoryBean> catBean = model.retrieveCategories();
			request.setAttribute("catBean", catBean);
			List<ItemBean> itemList = model.retrieveItems(getCategory(uri));
			request.setAttribute("itemList", itemList);

			// Check if userLogged in
			if (session.getAttribute("loggedIn") != null) request.setAttribute("loggedIn", true);

			rd = getServletContext().getRequestDispatcher("/views/itemPage.jspx");
			rd.forward(request, response);
		}
	}

	private void cart(String uri, FoodRus model, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException,
			SQLException {
		
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
}
