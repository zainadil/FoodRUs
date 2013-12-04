package Adverts;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.FoodRus;
import model.ItemBean;

/**
 * Servlet Filter implementation class Adverts
 */
@WebFilter(dispatcherTypes = {
				DispatcherType.REQUEST, 
				DispatcherType.FORWARD
		}
					, urlPatterns = { "/Adverts" }, servletNames = { "Controller.eFoods" })
public class Adverts implements Filter {
	
	private static final String recommended = "2002H712";
	private static final String addedItem = "2910h019";
	
    /**
     * Default constructor. 
     */
    public Adverts() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)res;
		
		if(request.getParameter("addedIDandQty") != null){
			
			HttpSession session = request.getSession();
			
			String itemID = request.getParameter("addedIDandQty").split(";")[0];
			if(itemID.compareToIgnoreCase(addedItem) == 0){
				FoodRus model = (FoodRus)request.getServletContext().getAttribute("fru");
				try {
					//System.out.println(recommended);
					List<ItemBean> advertItems = model.retrieveItemsBySearch(recommended);
					request.setAttribute("adverts",advertItems);
					
				} catch (Exception e) {
					System.out.println("Error in Filter: " + e.getLocalizedMessage());
				}
			}
		}
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
