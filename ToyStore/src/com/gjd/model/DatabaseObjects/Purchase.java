package com.gjd.model.DatabaseObjects;

import java.util.HashMap;

public class Purchase {
	private int id;
	private Store store;
	private Customer customer;
	private PaymentType paymentType;
	private double total;
	private PurchaseStatus purchaseStatus;
	private HashMap<Product, Integer> products;
	
	
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
	
	
	public Purchase(int id, Store store, Customer customer,
			PaymentType paymentType, double total, PurchaseStatus purchaseStatus) {
		this.id = id;
		this.store = store;
		this.customer = customer;
		this.paymentType = paymentType;
		this.total = total;
		this.purchaseStatus = purchaseStatus;
	}

	
	
	
}
