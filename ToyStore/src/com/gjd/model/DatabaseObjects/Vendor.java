package com.gjd.model.DatabaseObjects;

public class Vendor {
	private int id;
	private String name;
	private Address address;
	
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public Address getAddress() {
		return address;
	}
	
	
	public void setId(int id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	
	public Vendor(int id, String name, Address address) {
		this.id = id;
		this.name = name;
		this.address = address;
	}
	
	public boolean isNew() {
		return id == -1;
	}
}

