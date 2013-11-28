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

import model.*;

/**
 * Servlet implementation class Start
 */
@WebServlet("/eFoods")
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
			if (request.getParameter("category") != null) 
			{
					//System.out.println("in category");
					String category = request.getParameter("category");

					List<CategoryBean> catBean = model.retrieveCategories();
					//System.out.println("List size" + catBean.size());
					request.setAttribute("catBean", catBean);
					
					//System.out.println("category is: " + category);
					List<ItemBean> itemList = model.retrieveItems(category);
					request.setAttribute("itemList", itemList);

				    rd = getServletContext().getRequestDispatcher("/views/itemPage.jspx");
					rd.forward(request, response);
					//ideally go to category servlet
			}
			else if (request.getParameter("loginPage") != null)
			{
				rd = getServletContext().getRequestDispatcher("/views/loginPage.jspx");
				rd.forward(request, response);
				//ideally go to its own servelet

			} else if(request.getParameter("login") != null){
				 checkLogin(model, request, response);
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
	
	
	private void checkLogin(FoodRus model, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		RequestDispatcher rd;
		boolean loggedIn;
		String accountNumber = request.getParameter("accountNumber");
		request.setAttribute("accountNumber", accountNumber);
		String password = request.getParameter("password");
			
		if( model.checkCredentials(accountNumber, password)){
			loggedIn = true;
			request.setAttribute("loggedIn", loggedIn);
			rd = getServletContext().getRequestDispatcher("/views/homePage.jspx");
			rd.forward(request, response);
		} else {
			loggedIn=false;
			request.setAttribute("loggedIn", loggedIn);
			rd = getServletContext().getRequestDispatcher("/views/loginPage.jspx");
			rd.forward(request, response);
		}
	}
	
}
