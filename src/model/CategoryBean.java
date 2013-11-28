package model;

import java.sql.Blob;

public class CategoryBean {
	
	private int id;
	private String name;
	private String description;
	private Blob image;
	
	//Empty Constructor
	public CategoryBean(){
		
	}
	
	public CategoryBean(int id, String name, String desc, Blob image){
		setId(id);
		setName(name);
		setDescription(desc);
		setImage(image);
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Blob getImage() {
		return image;
	}
	public void setImage(Blob image) {
		this.image = image;
	}
}
