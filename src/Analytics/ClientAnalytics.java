package Analytics;

import java.util.HashMap;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

/**
 * Application Lifecycle Listener implementation class ClientAnalytics
 *
 */
@WebListener
public class ClientAnalytics implements HttpSessionAttributeListener {

    /**
     * Default constructor. 
     */
    public ClientAnalytics() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see HttpSessionAttributeListener#attributeRemoved(HttpSessionBindingEvent)
     */
    public void attributeRemoved(HttpSessionBindingEvent arg0) {
        // TODO Auto-generated method stub
    }

	/**
     * @see HttpSessionAttributeListener#attributeAdded(HttpSessionBindingEvent)
     */
    public void attributeAdded(HttpSessionBindingEvent arg0) {
        listen(arg0);
    }

	/**
     * @see HttpSessionAttributeListener#attributeReplaced(HttpSessionBindingEvent)
     */
    public void attributeReplaced(HttpSessionBindingEvent arg0) {
    	listen(arg0);
    }
	
    private void listen(HttpSessionBindingEvent event){
    	
    	HttpSession session = event.getSession();
    	if(event.getName().equals("freshVisit")){
    		System.out.println("Fresh Visit");
    		if(session.getAttribute("freshVisitTime") == null){
	    		session.setAttribute("freshVisitTime", System.currentTimeMillis());
	    	}
    	} else if(event.getName().equals("itemAddedToCart")){
    		Long averageTimeToAddToCart = (System.currentTimeMillis() - (Long)session.getAttribute("freshVisitTime")) / 1000;
    		HashMap<String, Long> addMap = (HashMap<String, Long>) session.getServletContext().getAttribute("averageAddHashmap");
    		if(!addMap.containsKey(session.getId())){
    			addMap.put(session.getId(), averageTimeToAddToCart);
    		}
    		
    		System.out.println("Adding to Cart Times");
    		for(String key: addMap.keySet())
    			System.out.println(key + " " + addMap.get(key));
    		
    	} else if(event.getName().equals("checkout")){
    		Long averageTimeToCheckout = (System.currentTimeMillis() - (Long)session.getAttribute("freshVisitTime")) / 1000;
    		HashMap<String, Long> checkoutmap = (HashMap<String, Long>) session.getServletContext().getAttribute("checkOutAddHashmap");
    		if(!checkoutmap.containsKey(session.getId())){
    			checkoutmap.put(session.getId(), averageTimeToCheckout);
    		}
    		
    		System.out.println("Checkout Times");
    		for(String key: checkoutmap.keySet())
    			System.out.println(key + " " + checkoutmap.get(key));
    	}
    }
}
