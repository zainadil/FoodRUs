package model;

public class ItemBean {

	private String number;
	private String name;
	private double price;
	private int qty;
	private int onOrder;
	private int reOrder;
	private int catId;
	private int supId;
	private double CostPrice;
	private String unit;
	private double extendedPrice;

	public ItemBean() {

	}

	public ItemBean(String itemNumber, String name, double price, int quantity, int onOrder, int reOrder, int categoryID, int supplierID,
			double costPrice, String unit) {
		setNumber(itemNumber);
		setName(name);
		setPrice(price);
		setQty(quantity);
		setOnOrder(onOrder);
		setReOrder(reOrder);
		setCatID(categoryID);
		setSupID(supplierID);
		setCostPrice(costPrice);
		setUnit(unit);
	}

	public double getExtendedPrice() {
		return extendedPrice;
	}

	public void setExtendedPrice(double extendedPrice) {
		this.extendedPrice = extendedPrice;
	}

	public String toString() {
		return "ItemBean [number=" + number + ", name=" + name + ", price=" + price + ", qty=" + qty + ", onOrder=" + onOrder + ", reOrder="
				+ reOrder + ", catId=" + catId + ", supId=" + supId + ", CostPrice=" + CostPrice + ", unit=" + unit + "]";
	}

	public String getNumber() {
		return number.toUpperCase();
	}

	public void setNumber(String number) {
		this.number = number.toUpperCase();
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

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
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

	public int getCatID() {
		return catId;
	}

	public void setCatID(int catID) {
		this.catId = catID;
	}

	public int getSupID() {
		return supId;
	}

	public void setSupID(int supID) {
		this.supId = supID;
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
