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

import Utilitiy.Utility;
import model.*;

/**
 * Servlet implementation class Start
 */
@WebServlet("/eFoods/*")
public class eFoods extends HttpServlet {
	String HST = "";
	String shipping = "";
	String discountRate = "";
	String discountAt = "";
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
			retrieveServletContextParams();
			fru.retrieveBlobs(this.getServletContext().getRealPath("/png/"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void retrieveServletContextParams() {
		if (this.getServletContext().getAttribute("clientList") == null) {
			HashMap<String, Integer> list = new HashMap<String, Integer>();
			this.getServletContext().setAttribute("clientList", list);
		}
		if (this.getServletContext().getAttribute("orderNum") == null) {
			int orderNum = 1;
			this.getServletContext().setAttribute("orderNum", orderNum);
		}
		HST = this.getServletContext().getInitParameter("HST");
		shipping = this.getServletContext().getInitParameter("Shipping");
		discountRate = this.getServletContext().getInitParameter("discountRate");
		discountAt = this.getServletContext().getInitParameter("discountAt");
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

			if (session.getAttribute("emptyCart") != null) {
				request.setAttribute("emptyCart", session.getAttribute("emptyCart"));
				session.setAttribute("emptyCart", null);
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
	 */
	private void login(String uri, FoodRus model, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException,
			SQLException {
		RequestDispatcher rd;
		HttpSession session = request.getSession();
		ClientBean tmp;

		if (request.getParameter("loginButton") != null) {
			boolean loggedIn;
			String accountNumber = request.getParameter("accountNumber");
			request.setAttribute("accountNumber", accountNumber);
			String password = request.getParameter("password");

			if ((tmp = model.checkClient(accountNumber, password)) != null) {
				HashMap<String, Integer> clients = (HashMap<String, Integer>) this.getServletContext().getAttribute("clientList");
				if (clients == null) {
					// should never enter here, but okay...
					System.out.println("Clients list is null.");
				}
				loggedIn = true;
				if (!clients.containsKey(tmp.getName())) {
					System.out.println(tmp.getName() + " is being set to 1");
					clients.put(tmp.getName(), 1);
					this.getServletContext().setAttribute("clientList", clients);
				}

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
			if (request.getAttribute("returnTo") == null) session.setAttribute("returnTo", (String) request.getHeader("referer"));
			else session.setAttribute("returnTo", request.getAttribute("returnTo"));
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
		HashMap<String, Integer> basket = (HashMap<String, Integer>) session.getAttribute("basket");
		RequestDispatcher rd;

		if (request.getParameter("addedIDandQty") != null) {
			String updatedIDandQty = request.getParameter("addedIDandQty");
			System.out.println(updatedIDandQty);
			String[] splits = updatedIDandQty.split(";");

			String key = splits[0];
			int quantity = Integer.parseInt(splits[1]);
			if (basket.containsKey(key)) basket.put(key, (basket.get(key) + quantity));
			else basket.put(key, quantity);

			String itemName = model.getItemName(key);
			String finalMessage = quantity + " " + itemName + " added to Cart";
			request.setAttribute("addtoCartNotificaton", finalMessage);
		}

		String category = Utility.getCategory(uri);
		List<CategoryBean> catBean = model.retrieveCategories();
		request.setAttribute("catBean", catBean);
		List<ItemBean> itemList = model.retrieveItems(category);
		request.setAttribute("itemList", itemList);
		request.setAttribute("category", category);
		rd = getServletContext().getRequestDispatcher("/views/itemPage.jspx");
		rd.forward(request, response);
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
	 */

	private void cart(String uri, FoodRus model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession();
		HashMap<String, Integer> basket = (HashMap<String, Integer>) session.getAttribute("basket");

		if (request.getParameter("updateQuantity") != null) {
			String updatedIDandQty = request.getParameter("updatedIDandQty");
			System.out.println(updatedIDandQty);
			String[] splits = updatedIDandQty.split(";");

			String key = splits[0];
			int quantity = Integer.parseInt(splits[1]);
			if (quantity == 0) basket.remove(key);
			else basket.put(key, quantity);
		}
		CartBean cartBean = model.generateShopppingCart(basket, (ClientBean) request.getSession().getAttribute("client"), HST , shipping, discountAt, discountRate );
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

		ClientBean client = (ClientBean) session.getAttribute("client");
		HashMap<String, Integer> basket = (HashMap<String, Integer>) session.getAttribute("basket");
		HashMap<String, Integer> clients = (HashMap<String, Integer>) this.getServletContext().getAttribute("clientList");

		if (client == null) {
			request.setAttribute("signInRequired", true);
			request.setAttribute("returnTo", (String) request.getHeader("referer"));
			session.setAttribute("returnTo", request.getAttribute("returnTo"));
			login(uri, model, request, response);
		} else if (basket == null || basket.isEmpty()) {
			session.setAttribute("emptyCart", true);
			response.sendRedirect(this.getServletContext().getContextPath() + "/eFoods");
		} else {
			int orderNum = (Integer) this.getServletContext().getAttribute("orderNum");

			CartBean cartBean = model.generateShopppingCart(basket, client, HST, shipping, discountAt, discountRate );
			int poNum = clients.get(client.getName());
			System.out.println("Client poNum is: " + poNum);

			String accountNumber = cartBean.customer.getNumber() + "";
			String filename = "/POs/po" + accountNumber + "_" + poNum + ".xml";
			String filePath = this.getServletContext().getRealPath(filename);
			request.setAttribute("filename", filename);
			poNum++;
			clients.put(client.getName(), poNum);
			System.out.println("Incrememnting to: " + poNum);
			poNum = clients.get(client.getName());
			System.out.println("Client poNum is: " + poNum);
			this.getServletContext().setAttribute("clientList", clients);

			boolean res = model.export(orderNum, cartBean, filePath);
			request.setAttribute("checkoutOk", res);
			session.setAttribute("basket", null);

			this.getServletContext().setAttribute("orderNum", ++orderNum);
			rd = getServletContext().getRequestDispatcher("/views/checkout.jspx");
			rd.forward(request, response);
		}
	}
}
