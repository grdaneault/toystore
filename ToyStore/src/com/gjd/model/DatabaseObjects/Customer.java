package com.gjd.model.DatabaseObjects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Customer {
	
	private int id;
	
	@NotNull
	@Size(max = 45)
	private String first;
	
	private String mi;
	
	@NotNull
	@Size(max = 45)
	private String last;
	
	private Address address;
	
	@NotNull
	@Size(max = 45)
	private String phone;
	
	@NotNull
	@Size(max = 255)
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
