package com.gjd.model.DatabaseObjects;

import java.util.ArrayList;

public class Purchase {
	private int id;
	private Store store;
	private Customer customer;
	private PaymentType paymentType;
	private double total;
	private PurchaseStatus purchaseStatus;
	private ArrayList<PurchaseItem> items = new ArrayList<PurchaseItem>();
	
	
	public int getId() {
		return id;
	}
	public Store getStore() {
		return store;
	}
	public Customer getCustomer() {
		return customer;
	}
	public PaymentType getPaymentType() {
		return paymentType;
	}
	public double getTotal() {
		return total;
	}
	public PurchaseStatus getPurchaseStatus() {
		return purchaseStatus;
	}
	
	
	public ArrayList<PurchaseItem> getItems()
	{
		return items;
	}
	
	public void setItems(ArrayList<PurchaseItem> items)
	{
		this.items = items;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public void setStore(Store store)
	{
		this.store = store;
	}
	
	public void setCustomer(Customer customer)
	{
		this.customer = customer;
	}
	
	public void setPaymentType(PaymentType paymentType)
	{
		this.paymentType = paymentType;
	}
	
	public void setTotal(double total)
	{
		this.total = total;
	}
	
	public void setPurchaseStatus(PurchaseStatus purchaseStatus)
	{
		this.purchaseStatus = purchaseStatus;
	}
	
	public Purchase(int id, Store store, Customer customer,
			PaymentType paymentType, double total, PurchaseStatus purchaseStatus) {
		this.id = id;
		this.store = store;
		this.customer = customer;
		this.paymentType = paymentType;
		this.total = total;
		this.purchaseStatus = purchaseStatus;
	}
	
	public Purchase(Store store)
	{
		this.store = store;
		this.id = -1;
	}
	
	public void calculateTotal()
	{
		total = 0;
		for (PurchaseItem p : items)
		{
			total += p.getProduct().getPrice() * p.getQuantity();
		}
	}
	
	public void addPurchaseItem(PurchaseItem pi)
	{
		items.add(pi);
	}
	public int getTotalItems()
	{
		int itemCount = 0;
		for (PurchaseItem p : items)
		{
			itemCount += p.getQuantity();
		}
		
		return itemCount;
	}
	
}
