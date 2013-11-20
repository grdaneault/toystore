package com.gjd.model.DatabaseObjects;

import java.io.Serializable;
import java.util.HashMap;

public class Store implements Serializable{

	private static final long serialVersionUID = 5634288723940653173L;
	
	private int id;
	private String name;
	private Address address;
	
	private HashMap<Character, DayHour> hours;
	
	public Store()
	{
		this.id = -1;
	}
	
	public Store(int id, String name, Address address)
	{
		this.id = id;
		this.name = name;
		this.address = address;
	}

	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public Address getAddress() {
		return address;
	}
	
	
	public HashMap<Character, DayHour> getHours() {
		return hours;
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

	
	public String toString()
	{
		return "Store " + id + ": " + name;
	}

	public void setHours(HashMap<Character, DayHour> hours) {
		this.hours = hours;
	}

	public boolean isNew() {
		return id == -1;
	}
}
