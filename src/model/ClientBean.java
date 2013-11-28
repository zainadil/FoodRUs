package model;

public class ClientBean 
{
	public String rating;
	public String password;
	public String name;
	public int number;
	
	public ClientBean(String rating, String password, String name, int number) 
	{
		setRating(rating);
		setPassword(password);
		setName(name);
		setNumber(number);
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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
