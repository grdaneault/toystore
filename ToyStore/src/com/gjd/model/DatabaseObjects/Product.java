package com.gjd.model.DatabaseObjects;

import java.io.Serializable;

public class Product implements Serializable
{
	private static final long serialVersionUID = -5330515643290082235L;

	private int SKU;
	private String name;
	private String image;
	private double weight;
	private double MSRP;
	private double price;
	private ProductType type;
	private Brand brand;
	private Vendor vendor;
	
	public int getSKU()
	{
		return SKU;
	}
	
	public void setSKU(int sKU)
	{
		SKU = sKU;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getImage()
	{
		return image;
	}
	
	public void setImage(String image)
	{
		this.image = image;
	}
	
	public double getWeight()
	{
		return weight;
	}
	
	public void setWeight(double weight)
	{
		this.weight = weight;
	}
	
	public double getMSRP()
	{
		return MSRP;
	}
	
	public void setMSRP(double mSRP)
	{
		MSRP = mSRP;
	}
	
	public double getPrice()
	{
		return price;
	}
	
	public void setPrice(double price)
	{
		this.price = price;
	}
	
	public ProductType getType()
	{
		return type;
	}
	
	public void setType(ProductType type)
	{
		this.type = type;
	}
	
	public Brand getBrand()
	{
		return brand;
	}
	
	public void setBrand(Brand brand)
	{
		this.brand = brand;
	}
	
	public Vendor getVendor()
	{
		return vendor;
	}
	
	public void setVendor(Vendor vendor)
	{
		this.vendor = vendor;
	}
}
