package com.gjd.model.DatabaseObjects;

import com.gjd.model.DatabaseConnection;


public class PurchaseItem
{
	private Product product;
	private int quantity;
	private Purchase purchase;
	
	
	
	public Product getProduct()
	{
		return product;
	}
	
	public void setProduct(Product product)
	{
		this.product = product;
	}
	
	public int getQuantity()
	{
		return quantity;
	}
	
	public void setQuantity(int quantity)
	{
		this.quantity = quantity;
	}
	
	public Purchase getPurchase()
	{
		return purchase;
	}
	
	public void setPurchase(Purchase purchase)
	{
		this.purchase = purchase;
	}

	public PurchaseItem(Product product, int quantity, Purchase purchase)
	{
		this.product = product;
		this.quantity = quantity;
		this.purchase = purchase;
	}

	public PurchaseItem(int SKU, int storeId, int quantity, Purchase purchase)
	{
		this(null, quantity, purchase);
		product = DatabaseConnection.getInstance().getProductByIdForStore(SKU, storeId);
	}
	
	
}	
