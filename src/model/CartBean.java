package model;

import java.util.Date;
import java.util.List;

public class CartBean 
{
	public int id;
	public Date submitted;
	public ClientBean customer;
	public List<ItemBean> items;
	public double total;
	public double shipping;
	public double HST;
	public double grandTotal;
	
	public CartBean()
	{
		
	}
	
	public CartBean(int id, Date submitted, ClientBean customer,
			List<ItemBean> items, double total, double shipping, double hST,
			double grandTotal) {
		this.id = id;
		this.submitted = submitted;
		this.customer = customer;
		this.items = items;
		this.total = total;
		this.shipping = shipping;
		HST = hST;
		this.grandTotal = grandTotal;
	}
	
	public String toString() {
		String s = "", i = "";
		if (customer != null) {
			s = customer.toString();

		}
		for (ItemBean a : items) {
			i += a.toString();
		}
		return "CartBean [id=" + id + ", submitted=" + submitted
				+ ", customer=" + s + ", items=" + 
				i + ", total="
				+ total + ", shipping=" + shipping + ", HST=" + HST
				+ ", grandTotal=" + grandTotal + "]";
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getSubmitted() {
		return submitted;
	}
	public void setSubmitted(Date submitted) {
		this.submitted = submitted;
	}
	public ClientBean getCustomer() {
		return customer;
	}
	public void setCustomer(ClientBean customer) {
		this.customer = customer;
	}
	public List<ItemBean> getItems() {
		return items;
	}
	public void setItems(List<ItemBean> items) {
		this.items = items;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public double getShipping() {
		return shipping;
	}
	public void setShipping(double shipping) {
		this.shipping = shipping;
	}
	public double getHST() {
		return HST;
	}
	public void setHST(double hST) {
		HST = hST;
	}
	public double getGrandTotal() {
		return grandTotal;
	}
	public void setGrandTotal(double grandTotal) {
		this.grandTotal = grandTotal;
	}	
}//end ClientBean Class