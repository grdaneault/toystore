package com.gjd.model.DatabaseObjects;

import java.io.Serializable;
import java.sql.Time;
import java.util.HashMap;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Store implements Serializable{

	private static final long serialVersionUID = 5634288723940653173L;
	
	private int id;
	
	@NotNull
	@Size(min=0, max=255)
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

	@SuppressWarnings("deprecation")
    public void setHours(HashMap<Character, DayHour> hours) {
            this.hours = hours;
            for (int i = 0; i < DayHour.DAY_STRING.length(); i++)
            {
                    if (hours.get(DayHour.DAY_STRING.charAt(i)) == null)
                    {
                            hours.put(DayHour.DAY_STRING.charAt(i), new DayHour(this, DayHour.DAY_STRING.charAt(i), new Time(0, 0, 0), new Time(0, 0, 0), true));
                    }
            }
    }

	public boolean isNew() {
		return id == -1;
	}
}
