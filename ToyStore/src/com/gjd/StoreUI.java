package com.gjd;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.annotation.WebServlet;

import com.gjd.UI.User.CheckoutListener;
import com.gjd.UI.User.CheckoutWindow;
import com.gjd.UI.User.PurchaseTable;
import com.gjd.UI.User.RegisterWindow;
import com.gjd.model.DatabaseConnection;
import com.gjd.model.DatabaseObjects.Customer;
import com.gjd.model.DatabaseObjects.PaymentType;
import com.gjd.model.DatabaseObjects.Product;
import com.gjd.model.DatabaseObjects.Purchase;
import com.gjd.model.DatabaseObjects.PurchaseItem;
import com.gjd.model.DatabaseObjects.Store;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
@Theme("toystore")
public class StoreUI extends UI
{

	@WebServlet(value = "/PhysicalStore/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = StoreUI.class, widgetset = "com.gjd.widgetset.ToystoreWidgetset")
	public static class Servlet extends VaadinServlet {
	}
	
	private Store store = null;
	
	private Purchase purchase;

	private VerticalLayout layout;

	private Label item;

	private Table order;
	
	protected void init(VaadinRequest request)
	{
		layout = new VerticalLayout();
		layout.setMargin(false);
		layout.setSpacing(false);
		setContent(layout);
		purchase = new Purchase(null);
		createUI();
	}
	
	private void createUI()
	{
		VerticalLayout content = new VerticalLayout();
		content.setMargin(true);
		content.setSpacing(true);
		
		Label headerLbl = new Label("<h1>Toy Store</h1>");

		ComboBox storeSelect = new ComboBox("Store Select");
		ArrayList<Store> stores = DatabaseConnection.getInstance().getStoreList();
		for (Store s : stores)
		{
			storeSelect.addItem(s);
		}
		
		
		storeSelect.addValueChangeListener(new ValueChangeListener()
		{
			
			@Override
			public void valueChange(ValueChangeEvent event)
			{
				store = (Store) event.getProperty().getValue();
				purchase.setStore(store);
			}
		});
		storeSelect.setNullSelectionAllowed(false);
		
		storeSelect.setValue(stores.get(0));
		
		headerLbl.setContentMode(ContentMode.HTML);
		headerLbl.addStyleName("store_header");
		layout.addComponent(headerLbl);
		content.addComponent(storeSelect);
		
		order = new PurchaseTable();

		HorizontalLayout addItemContainer = new HorizontalLayout();
		final TextField itemSKU = new TextField("SKU");
		itemSKU.setImmediate(true);
		itemSKU.setBuffered(false);
		itemSKU.addValidator(new Validator()
		{
			
			@Override
			public void validate(Object value) throws InvalidValueException
			{
				try
				{
					System.out.println(value);
					int SKU = Integer.valueOf(value.toString());
					Product p = DatabaseConnection.getInstance().getProductById(SKU);
					if (p == null)
					{
						throw new InvalidValueException("Unknown SKU");
					}
					else
					{
						item.setValue(p.getName());
					}
				}
				catch (NumberFormatException ex)
				{
					throw new InvalidValueException("Unable to parse SKU as Integer");
				}
				catch (SQLException e)
				{
					throw new InvalidValueException("Unable to check SKU");
				}
			}
		});
		itemSKU.setRequired(true);
		itemSKU.setImmediate(true);
		
		
		final TextField itemQuantity = new TextField("Quantity");
		itemQuantity.setRequired(true);
		itemQuantity.setImmediate(true);
		itemQuantity.setValue("1");
		
		Button add = new Button("Scan Item");
		add.setClickShortcut(KeyCode.ENTER);
		add.addClickListener(new ClickListener()
		{
			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event)
			{
				Product product = DatabaseConnection.getInstance().getProductByIdForStore(Integer.valueOf(itemSKU.getValue()), store.getId());
				System.out.println(product + " FROM STORE " + store);
				int quantity = Integer.valueOf(itemQuantity.getValue());
				PurchaseItem pi = new PurchaseItem(product, quantity, purchase);
				purchase.addPurchaseItem(pi);
				((BeanItemContainer<PurchaseItem>)order.getContainerDataSource()).addBean(pi);
				itemSKU.setValue("");
				itemSKU.focus();
				itemQuantity.setValue("1");
				
				purchase.calculateTotal();
				order.setColumnFooter("quantity", "" + purchase.getTotalItems());
				order.setColumnFooter("total price", "" + purchase.getTotal());
				
			}
		});

		Button checkout = new Button("Checkout");
		checkout.addClickListener(new ClickListener()
		{
			
			@Override
			public void buttonClick(ClickEvent event)
			{
				if (order.getContainerDataSource().size() == 0)
				{
					Notification.show("No items selected for purchase", "", Type.WARNING_MESSAGE);
				}
				else
				{
					createCheckoutWindow();
				}
			}
		});
		
		Button register = new Button("Register Customer");
		register.addClickListener(new ClickListener()
		{
			
			@Override
			public void buttonClick(ClickEvent event)
			{
				createRegisterWindow();
			}
		});
		

		Button clear = new Button("Clear Cart");
		clear.addClickListener(new ClickListener()
		{
			
			@Override
			public void buttonClick(ClickEvent event)
			{
				order.removeAllItems();
				purchase = new Purchase(store);
				order.setColumnFooter("quantity", "0");
				order.setColumnFooter("total price", "0");
				Notification.show("Cart Cleared", "", Type.HUMANIZED_MESSAGE);
			}
		});
		
		addItemContainer.addComponent(itemSKU);
		addItemContainer.addComponent(itemQuantity);
		addItemContainer.addComponent(add);
		addItemContainer.addComponent(checkout);
		addItemContainer.addComponent(clear);
		addItemContainer.addComponent(register);
		addItemContainer.setSpacing(true);
		addItemContainer.setComponentAlignment(itemSKU, Alignment.BOTTOM_LEFT);
		addItemContainer.setComponentAlignment(itemQuantity, Alignment.BOTTOM_LEFT);
		addItemContainer.setComponentAlignment(add, Alignment.BOTTOM_LEFT);
		addItemContainer.setComponentAlignment(checkout, Alignment.BOTTOM_LEFT);
		addItemContainer.setComponentAlignment(clear, Alignment.BOTTOM_LEFT);
		addItemContainer.setComponentAlignment(register, Alignment.BOTTOM_LEFT);
		
		item = new Label("");
		
		content.addComponent(addItemContainer);
		content.addComponent(item);
		content.addComponent(order);
		
		
		
		layout.addComponent(content);
	}

	private void createCheckoutWindow()
	{
		
		
		CheckoutWindow cw = new CheckoutWindow(purchase);
		cw.setCheckoutListener(new CheckoutListener()
		{
			
			@Override
			public void successfulCheckout(CheckoutWindow window)
			{
				order.removeAllItems();
				purchase = new Purchase(store);
				order.setColumnFooter("quantity", "0");
				order.setColumnFooter("total price", "0");
			}
		});
		getUI().addWindow(cw);
	}

	private void createRegisterWindow()
	{
		RegisterWindow rw = new RegisterWindow();
		getUI().addWindow(rw);
	}
}
