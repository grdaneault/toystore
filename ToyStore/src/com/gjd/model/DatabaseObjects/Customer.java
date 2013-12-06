package com.gjd.model.DatabaseObjects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Customer {
	
	private int id;
	
	@NotNull
	@Size(max = 45)
	private String first;
	
	@Size(min = 1, max = 4) // allow NMN.
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
	
	public Customer()
	{
		id = -1;
		address = new Address();
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

	public boolean isNew()
	{
		return id == -1;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	
	public void setFirst(String first)
	{
		this.first = first;
	}

	
	public void setMi(String mi)
	{
		this.mi = mi;
	}

	
	public void setLast(String last)
	{
		this.last = last;
	}

	
	public void setAddress(Address address)
	{
		this.address = address;
	}

	
	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	
	public void setEmail(String email)
	{
		this.email = email;
	}
	
	public String toString()
	{
		return first + " " + mi + " " + last + ": " + phone + "/" + email;
	}
}
