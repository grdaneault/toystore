package com.gjd.model.DatabaseObjects;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Brand implements Serializable {
	private static final long serialVersionUID = -1024881119536364421L;

	private int id;
	
	@NotNull
	@Size(max=100)
	private String name;
	
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Brand(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public boolean isNew() {
		return id == -1;
	}
}
