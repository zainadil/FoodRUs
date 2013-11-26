package model;

public class ItemBean {
	
	private String itemNumber;
	private String name;
	private double price;
	private int quantity;
	private int onOrder;
	private int reOrder;
	private int categoryID;
	private int supplierID;
	private double CostPrice;
	private String unit;
	
	public ItemBean(){
		
	}
	
	public ItemBean(String itemNumber, String name, double price, int quantity, int onOrder, int reOrder, int categoryID, int supplierID, double costPrice, String unit){
		setItemNumber(itemNumber);
		setName(name);
		setPrice(price);
		setQuantity(quantity);
		setOnOrder(onOrder);
		setReOrder(reOrder);
		setCategoryID(categoryID);
		setSupplierID(supplierID);
		setCostPrice(costPrice);
		setUnit(unit);
	}
	
	
	public String getItemNumber() {
		return itemNumber;
	}
	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public int getOnOrder() {
		return onOrder;
	}
	public void setOnOrder(int onOrder) {
		this.onOrder = onOrder;
	}
	public int getReOrder() {
		return reOrder;
	}
	public void setReOrder(int reOrder) {
		this.reOrder = reOrder;
	}
	public int getCategoryID() {
		return categoryID;
	}
	public void setCategoryID(int categoryID) {
		this.categoryID = categoryID;
	}
	public int getSupplierID() {
		return supplierID;
	}
	public void setSupplierID(int supplierID) {
		this.supplierID = supplierID;
	}
	public double getCostPrice() {
		return CostPrice;
	}
	public void setCostPrice(double costPrice) {
		CostPrice = costPrice;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
}
