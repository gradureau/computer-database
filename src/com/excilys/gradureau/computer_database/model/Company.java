package com.excilys.gradureau.computer_database.model;

public class Company {
	
	private Long id;
	private String name;
	
	@Override
	public String toString() {
		return "Company [id=" + id + ", name=" + name + "]";
	}
	
	public Company() {
	}
	public Company(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
