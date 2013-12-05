package com.gjd;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.annotation.WebServlet;

import com.gjd.model.DatabaseConnection;
import com.gjd.model.DatabaseObjects.Product;
import com.gjd.model.DatabaseObjects.Purchase;
import com.gjd.model.DatabaseObjects.PurchaseItem;
import com.gjd.model.DatabaseObjects.Store;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
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
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

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
		Table order = new Table();
		final BeanItemContainer<PurchaseItem> purchaseItemContainer = new BeanItemContainer<PurchaseItem>(PurchaseItem.class);
		order.setContainerDataSource(purchaseItemContainer);
		order.addGeneratedColumn("price", new ColumnGenerator()
		{
			
			@SuppressWarnings("unchecked")
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId)
			{
				return ((BeanItem<PurchaseItem>)source.getItem(itemId)).getBean().getProduct().getPrice();
			}
		});
		
		order.addGeneratedColumn("total price", new ColumnGenerator()
		{
			
			@SuppressWarnings("unchecked")
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId)
			{
				return ((BeanItem<PurchaseItem>)source.getItem(itemId)).getBean().getProduct().getPrice() * (Integer)source.getItem(itemId).getItemProperty("quantity").getValue();
			}
		});
		
		order.setVisibleColumns("product", "price", "quantity", "total price");

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
		
		add.addClickListener(new ClickListener()
		{
			@Override
			public void buttonClick(ClickEvent event)
			{
				Product product = DatabaseConnection.getInstance().getProductByIdForStore(Integer.valueOf(itemSKU.getValue()), store.getId());
				System.out.println(product + " FROM STORE " + store);
				int quantity = Integer.valueOf(itemQuantity.getValue());
				PurchaseItem pi = new PurchaseItem(product, quantity, purchase);
				purchaseItemContainer.addBean(pi);
			}
		});
		
		addItemContainer.addComponent(itemSKU);
		addItemContainer.addComponent(itemQuantity);
		addItemContainer.addComponent(add);
		addItemContainer.setSpacing(true);
		addItemContainer.setComponentAlignment(itemSKU, Alignment.BOTTOM_LEFT);
		addItemContainer.setComponentAlignment(itemQuantity, Alignment.BOTTOM_LEFT);
		addItemContainer.setComponentAlignment(add, Alignment.BOTTOM_LEFT);
		
		item = new Label("");
		
		content.addComponent(addItemContainer);
		content.addComponent(item);
		content.addComponent(order);
		
		
		
		layout.addComponent(content);
	}
}
