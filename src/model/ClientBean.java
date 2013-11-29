package model;

public class ClientBean 
{
	public String rating;
	public String name;
	public int number;
	
	public String toString() {
		return "ClientBean [rating=" + rating + ", name=" + name + ", number="
				+ number + "]";
	}

	public ClientBean(String rating,String name, int number) 
	{
		setRating(rating);
		setName(name);
		setNumber(number);
	}

	public ClientBean() {
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
	
}//end ClientBean Class
