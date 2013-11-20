package com.gjd.model.DatabaseObjects;

public class Customer {
	private int id;
	private String first;
	private String mi;
	private String last;
	private Address address;
	private String phone;
	private String email;

	/**
	 * @param id		Customer ID
	 * @param first		First Name
	 * @param mi		Middle Initial
	 * @param last		Last Name
	 * @param address	Customer Address
	 * @param phone		Phone Number
	 * @param email		Email Address
	 */
	public Customer(int id, String first, String mi, String last,
			Address address, String phone, String email) {
		this.id = id;
		this.first = first;
		this.mi = mi;
		this.last = last;
		this.address = address;
		this.phone = phone;
		this.email = email;
	}
	
	public int getId() {
		return id;
	}
	public String getFirst() {
		return first;
	}
	public String getMi() {
		return mi;
	}
	public String getLast() {
		return last;
	}
	public Address getAddress() {
		return address;
	}
	public String getPhone() {
		return phone;
	}
	public String getEmail() {
		return email;
	}
}
