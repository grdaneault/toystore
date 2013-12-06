package com.gjd.UI.User;

import java.sql.SQLException;
import java.util.ArrayList;

import com.gjd.model.DatabaseConnection;
import com.gjd.model.DatabaseObjects.Customer;
import com.gjd.model.DatabaseObjects.PaymentType;
import com.gjd.model.DatabaseObjects.Purchase;
import com.gjd.model.DatabaseObjects.PurchaseItem;
import com.gjd.model.DatabaseObjects.Store;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;


public class CheckoutWindow extends Window
{
	private static final long serialVersionUID = -7577601931312157341L;
	private Purchase purchase;
	private Store store;
	
	private CheckoutListener checkoutListener;
	
	public void setCheckoutListener(CheckoutListener listener)
	{
		checkoutListener = listener;
	}
	
	public CheckoutWindow(Purchase p)
	{
		this(p, null);
	}
	
	public CheckoutWindow(Purchase p, final Customer customer)
	{
		super("Checkout");
		this.purchase = p;
		store = p.getStore();
		
		VerticalLayout checkoutLayout = new VerticalLayout();
		
		final TextField customerId = new TextField("Enter your Customer ID (If you have one)");
		if (!(customer == null || customer == store.getGeneric()))
		{
			customerId.setVisible(false);
			customerId.setValue("" + customer.getId());
		}
		
		ComboBox paymentType = new ComboBox("Enter payment method");
		ArrayList<PaymentType> paymentTypes = DatabaseConnection.getInstance().getPaymentTypes();
		for (PaymentType t : paymentTypes)
		{
			paymentType.addItem(t);
		}
		
		paymentType.setNullSelectionAllowed(false);
		paymentType.setNewItemsAllowed(false);
		
		paymentType.addValueChangeListener(new ValueChangeListener()
		{
			
			private static final long serialVersionUID = 3402814026590724607L;

			@Override
			public void valueChange(ValueChangeEvent event)
			{
				purchase.setPaymentType((PaymentType) event.getProperty().getValue());
			}
		});
		paymentType.setValue(paymentTypes.get(0));
		
		Button completeCheckout = new Button("Complete Checkout");
		completeCheckout.addClickListener(new ClickListener()
		{
			private static final long serialVersionUID = -1078535270251989627L;

			@Override
			public void buttonClick(ClickEvent event)
			{
				DatabaseConnection.getInstance().beginTransaction();
				
				Customer c;
				if (customerId.getValue().equals(""))
				{
					c = store.getGeneric();
				}
				else
				{
					try
					{
						
						c = customer != null ? customer : DatabaseConnection.getInstance().getCustomerById(Integer.valueOf(customerId.getValue()));
					}
					catch (SQLException ex)
					{
						ex.printStackTrace();
						Notification.show("Unknown Customer", "Unable to find customer with ID " + customerId.getValue(), Type.ERROR_MESSAGE);
						DatabaseConnection.getInstance().endTransaction();
						return;
					}
				}
				
				
				purchase.setCustomer(c);
				
				boolean good = true;
				purchase.calculateTotal();
				good = good && DatabaseConnection.getInstance().createOrder(purchase);
				
				for (PurchaseItem pi : purchase.getItems())
				{
					int remainingQuantity = DatabaseConnection.getInstance().getAvailableQuantity(pi.getProduct(), pi.getPurchase().getStore());
					if (remainingQuantity >= pi.getQuantity())
					{
						good = good && DatabaseConnection.getInstance().savePurchaseItem(pi);
						if (!good)
						{
							Notification.show("Unable to complete the purchase", "Error adding item " + pi.getProduct(), Type.ERROR_MESSAGE);
							break;
						}
					}
					else
					{
						good = false;
						Notification.show("Unable to complete the purchase", "Insufficient quantity of " + pi.getProduct(), Type.ERROR_MESSAGE);
						break;
					}
				}
				
				boolean updateTotal = DatabaseConnection.getInstance().updatePurchaseTotal(purchase);
				
				if (good && updateTotal)
				{
					Notification.show("Thank You, " + c.getFirst(), "Have a nice day", Type.HUMANIZED_MESSAGE);
					checkoutListener.successfulCheckout(CheckoutWindow.this);
					close();
				}
				else
				{
					if (!good)
					{
						Notification.show("Error", "Could not add all items", Type.ERROR_MESSAGE);
					}
					
					else if (!updateTotal)
					{
						Notification.show("Error", "Could not update order total", Type.ERROR_MESSAGE);					
					}
					DatabaseConnection.getInstance().rollback();
				}
				
				DatabaseConnection.getInstance().endTransaction();
			}
		});
		
		checkoutLayout.addComponent(customerId);
		checkoutLayout.addComponent(paymentType);
		checkoutLayout.addComponent(completeCheckout);
		checkoutLayout.setSpacing(true);
		checkoutLayout.setMargin(true);
		
		setContent(checkoutLayout);
		setWidth(325, Unit.PIXELS);
		center();
	}
}
