package com.gjd.model.DatabaseObjects;

public class PaymentType {
	int id;
	String name;
	
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}

	public PaymentType(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public String toString()
	{
		return name;
	}
}
