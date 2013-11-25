package com.gjd.model.DatabaseObjects;

import java.io.Serializable;

public class Product implements Serializable
{
	private static final long serialVersionUID = -5330515643290082235L;

	private int SKU;
	private String name;
	private String image;
	private double weight;
	private float MSRP;
	private float price;
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
	
	public float getMSRP()
	{
		return MSRP;
	}
	
	public void setMSRP(float mSRP)
	{
		MSRP = mSRP;
	}
	
	public float getPrice()
	{
		return price;
	}
	
	public void setPrice(float price)
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

	public Product(int SKU, String name, String image, double weight, float MSRP, float price, ProductType type,
			Brand brand, Vendor vendor)
	{
		this.SKU = SKU;
		this.name = name;
		this.image = image;
		this.weight = weight;
		this.MSRP = MSRP;
		this.price = price;
		this.type = type;
		this.brand = brand;
		this.vendor = vendor;
	}

	public Product()
	{
	}
}
