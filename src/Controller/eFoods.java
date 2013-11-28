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
	
	
	
	public eFoods() 
	{
		super();

	}



	@Override
	public void init() throws ServletException 
	{
		FoodRus fru = new FoodRus();
		this.getServletContext().setAttribute("fru", fru);
		
		

	}



	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
	
			FoodRus f = (FoodRus)this.getServletContext().getAttribute("fru");
			
			try 
			{
				//retrieving categories
				List<CategoryBean> catBean = f.retrieveCategories();
				System.out.println("List size" + catBean.size());
				request.setAttribute("catBean", catBean);
				
				String category = request.getParameter("category");
				System.out.println("category is: " + category);
				
				//first visit to homepage
				if (request.getParameter("category") == null)
				{
					
					System.out.println("im here in category == null");
					this.getServletContext().getRequestDispatcher("/views/homePage.jspx").forward(request, response);
				}
				
				//call model with the right category
//				String category2 = request.getParameter("category");
//				System.out.println("category after clicking is: " + category);
				category = request.getParameter("category");
				
				//category selected 
				if (category != null)
				{
					System.out.println("category is: " + category);
					List<ItemBean> itemList = f.retrieveItems(category);
					request.setAttribute("itemList", itemList);
					
					System.out.println("Item List size" + itemList.size());
					
					this.getServletContext().getRequestDispatcher("/views/itemPage.jspx").forward(request, response);
					
				}
				
				if (request.getParameter("login")!= null)
				{
					boolean loggedIn;
					System.out.println("forwarding to loginPage");
					this.getServletContext().getRequestDispatcher("/views/loginPage.jspx").forward(request, response);
					String referer = request.getHeader("Referer"); 
					
					//get params from browser
					String accountNumber = request.getParameter("accountNumber");
					request.setAttribute("accountNumber", accountNumber);
					String password = request.getParameter("password");
					request.setAttribute("password", password);
					
					//call model to check DB to see if user exists
					if(f.checkCredentials(accountNumber, password) == true)
					{	
						loggedIn = true;
						request.setAttribute("loggedIn", loggedIn);
						//go to last visited page
						
						response.sendRedirect(referer);
					}
					else
					{
						loggedIn=false;
						request.setAttribute("loggedIn", loggedIn);
					}
					
					
					//Once New Login button is pressed, forward to LAST VISITED PAGE				
				}

				if (request.getParameter("cart")!= null)
				{
					System.out.println("forwarding to cartPage");
					this.getServletContext().getRequestDispatcher("/views/cartPage.jspx").forward(request, response);
									
				}
			} catch (SQLException e) 
			{
				System.out.println("Category Bean List not created");
				e.printStackTrace();
			}
		}//end (ELSE) logo is not null

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

}
