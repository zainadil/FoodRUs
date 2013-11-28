package Controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

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
		try
		{
			FoodRus model = (FoodRus) this.getServletContext().getAttribute("fru");
			RequestDispatcher rd;
			HttpSession session = request.getSession();
			String pageURI = request.getRequestURI();
			request.setAttribute("loggedIn", session.getAttribute("loggedIn"));
			if (pageURI.contains("Category")) 
			{
					List<CategoryBean> catBean = model.retrieveCategories();
					request.setAttribute("catBean", catBean);
					List<ItemBean> itemList = model.retrieveItems("Meat");
					request.setAttribute("itemList", itemList);
				    rd = getServletContext().getRequestDispatcher("/views/itemPage.jspx");
					rd.forward(request, response);
			}
			else if (pageURI.contains("Login")){
				login(pageURI,  model, request, response);
			} else if (pageURI.contains("Logout")){
				logout(pageURI,  model, request, response);
			} else if (pageURI.contains("Cart")){
				rd = getServletContext().getRequestDispatcher("/views/homePage.jspx");
				rd.forward(request, response);
			} else {
				rd = getServletContext().getRequestDispatcher("/views/homePage.jspx");
				rd.forward(request, response);
			} 
		}
		catch (Exception e)
		{
			
			
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
		//implement logout functionality
		if (request.getAttribute("loggedIn") != null)
			session.removeAttribute("loggedIn");

		response.sendRedirect(this.getServletContext().getContextPath() + "/eFoods");
	}
	private void login(String uri, FoodRus model, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		RequestDispatcher rd;
		HttpSession session = request.getSession();
		
		if(request.getParameter("loginButton") != null){
			//Login Button has been pressed.
			System.out.println("Came in here");
			boolean loggedIn;
			String accountNumber = request.getParameter("accountNumber");
			request.setAttribute("accountNumber", accountNumber);
			String password = request.getParameter("password");
				
			if( model.checkCredentials(accountNumber, password)){
				loggedIn = true;
				request.setAttribute("loggedIn", loggedIn);
				session.setAttribute("loggedIn",  true);
				response.sendRedirect(this.getServletContext().getContextPath() + "/eFoods");
			} else {
				loggedIn=false;
				request.setAttribute("loggedIn", loggedIn);
				rd = getServletContext().getRequestDispatcher("/views/loginPage.jspx");
				rd.forward(request, response);
			}
		} else {
			rd = getServletContext().getRequestDispatcher("/views/loginPage.jspx");
			rd.forward(request, response);
		}
	}
	
	private void category(String uri, FoodRus model, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException{
		
		HttpSession session = request.getSession();
		
		RequestDispatcher rd;
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
	
	private void cart(String uri, FoodRus model, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException{
		RequestDispatcher rd;
		rd = getServletContext().getRequestDispatcher("/views/cartPage.jspx");
		rd.forward(request, response);
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
