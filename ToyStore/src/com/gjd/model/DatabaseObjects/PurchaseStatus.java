package com.gjd.model.DatabaseObjects;

public class PurchaseStatus {
	int id;
	String name;
	
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}

	public PurchaseStatus (int id, String name) {
		this.id = id;
		this.name = name;
	}
}
