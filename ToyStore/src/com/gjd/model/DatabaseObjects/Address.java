package com.gjd.model.DatabaseObjects;

import java.io.Serializable;

import javax.validation.constraints.Size;

import com.sun.istack.internal.NotNull;

public class Address implements Serializable{
	
	private static final long serialVersionUID = -8423156319718345438L;
	
	
	private int id;
	
	@NotNull
	@Size(max=255)
	private String line1;
	
	@Size(max=255)
	private String line2;
	
	@NotNull
	@Size(min=5, max=10)
	private String zip;
	
	@NotNull
	@Size(max=40)
	private String city;
	
	@NotNull
	private USState state;
	
	/**
	 * Creates a new Address Entry.  ID is -1 for new objects
	 */
	public Address()
	{
		id = -1;
	}
	
	/**
	 * Creates an address loaded form the database
	 * 
	 * @param id
	 * @param line_1
	 * @param line_2
	 * @param city
	 * @param state_id
	 * @param zip
	 */
	public Address(int id, String line_1, String line_2, String city, int state_id, String zip)
	{
		this(id, line_1, line_2, city, USState.getState(state_id), zip);
	}
	
	public Address(int id, String line_1, String line_2, String city, USState state, String zip)
	{
		this.id = id;
		this.line1 = line_1;
		this.line2 = line_2 == null ? "" : line_2;
		this.city = city;
		this.state = state;
		this.zip = zip;
	}
	
	public int getId() {
		return id;
	}
	public String getLine1() {
		return line1;
	}
	public String getLine2() {
		return line2;
	}
	public String getCity() {
		return city;
	}
	public USState getState() {
		return state;
	}
	public String getZip() {
		return zip;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public void setLine1(String line_1) {
		this.line1 = line_1;
	}

	public void setLine2(String line_2) {
		this.line2 = line_2;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setState(USState state) {
		this.state = state;
	}

	public boolean isNew() {
		return false;
	}
	
	public String toString()
	{
		return line1 + "\n" + (line2 == null ? "" : line2) + "\n" + city + " " + state.toString() + ", " + zip; 
	}
	
}
