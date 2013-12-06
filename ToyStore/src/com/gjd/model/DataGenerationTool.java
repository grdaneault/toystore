package com.gjd.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.gjd.model.DatabaseObjects.Customer;
import com.gjd.model.DatabaseObjects.PaymentType;
import com.gjd.model.DatabaseObjects.Purchase;
import com.gjd.model.DatabaseObjects.PurchaseItem;
import com.gjd.model.DatabaseObjects.Store;


public class DataGenerationTool
{

	private static int NUM_PURCHASES = 100;
	
	private static int MIN_ITEMS_PER_PURCHASE = 1;
	private static int MAX_ITEMS_PER_PURCHASE = 10;
	private static int MAX_QUANTITY_PER_ITEM  = 3;
	
	private static int MIN_PRODUCT_ID = 1;
	private static int MAX_PRODUCT_ID = 59;
	
	private static int MIN_STORE_ID = 1;
	private static int MAX_STORE_ID = 7;
	
	private static int MIN_CUST_ID = 0;
	private static int MAX_CUST_ID = 31;
	
	private static ArrayList<PaymentType> pTypes;
	
	private static Random rand = new Random();
	
	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException
	{
		DatabaseConnection conn = DatabaseConnection.getInstance();
		pTypes = conn.getPaymentTypes();
		
		List<String> argList = Arrays.asList(args);
		
		if (argList.contains("refill"))
		{
			refillStores(conn);
		}
		
		if (argList.contains("createPurchases"))
		{
			createPurchases(conn);
		}
	}

	private static void createPurchases(DatabaseConnection conn) throws SQLException
	{
		for (int i = 0; i < NUM_PURCHASES; i++)
		{
			conn.beginTransaction();
			int storeId = randBetween(MIN_STORE_ID, MAX_STORE_ID);
			Store s = conn.getStoreById(storeId);
			Purchase p = new Purchase(s);
			
			
			Customer c = null;
			int cust = randBetween(-MAX_CUST_ID / 2, MAX_CUST_ID);
			if (cust <= 0)
			{
				c = s.getGeneric();
			}
			else
			{
				c = conn.getCustomerById(cust);
			}
			
			p.setCustomer(c);
			
			int items = randBetween(MIN_ITEMS_PER_PURCHASE, MAX_ITEMS_PER_PURCHASE);
			
			for (int j = 0; j < items; j++)
			{
				PurchaseItem pi = null; 
				while (pi == null)
				{
					pi = PurchaseItem.create(randBetween(MIN_PRODUCT_ID, MAX_PRODUCT_ID), s.getId(), randBetween(1, MAX_QUANTITY_PER_ITEM), p);
				}
				
				int maxQ = conn.getAvailableQuantity(pi.getProduct(), p.getStore());
				if (pi.getQuantity() > maxQ)
				{
					pi.setQuantity(maxQ);
				}
				
				p.addPurchaseItem(pi);
			}
			
			p.calculateTotal();
			
			p.setPaymentType(pTypes.get(randBetween(0, pTypes.size() - 1)));
			
			boolean good = true;
			good = good && DatabaseConnection.getInstance().createOrder(p);
			
			for (PurchaseItem pi : p.getItems())
			{
				int remainingQuantity = DatabaseConnection.getInstance().getAvailableQuantity(pi.getProduct(), pi.getPurchase().getStore());
				if (remainingQuantity >= pi.getQuantity())
				{
					good = good && DatabaseConnection.getInstance().savePurchaseItem(pi);
					if (!good)
					{
						System.out.println("Insufficient quantity");
					}
				}
				else
				{
					good = false;
				}
			}
			good = good && DatabaseConnection.getInstance().updatePurchaseTotal(p);
			
			if (!good)
			{
				conn.rollback();
				i--;
			}
			
			conn.endTransaction();
			
			if (i % 25 == 0)
			{
				refillStores(conn);
			}
			
			System.err.println("Added transaction " + i + " with " + p.getItems().size() + " items totaling " + p.getTotal() + " at store " + p.getStore().getId() + " paid with " + p.getPaymentType().getName() + " by " + c.getFirst());
		}
	}

	private static void refillStores(DatabaseConnection conn)
	{
		for (int i = MIN_STORE_ID; i <= MAX_STORE_ID; i++)
		{
			conn.createAllStoreOrders(i);
			conn.fillAllOrdersForStore(i);
		}
	}
	
	public static int randBetween(int low, int high)
	{
		return rand.nextInt(high - low + 1) + low;
	}

}
