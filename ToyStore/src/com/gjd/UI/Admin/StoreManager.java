package com.gjd.UI.Admin;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;

import com.gjd.UI.ProductControls.ProductControl;
import com.gjd.model.DatabaseConnection;
import com.gjd.model.DatabaseObjects.PurchaseItem;
import com.gjd.model.DatabaseObjects.Store;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;

public class StoreManager extends TabSheet implements Serializable{

	private static final long serialVersionUID = 2505327117442756951L;
	
	private TextField storeTitle;

	private Store store;
	
	public StoreManager(Store store) {
		this.store = store;
		tabs = new ArrayList<Component>();
		buildMainLayout();
	}

	private ArrayList<Component> tabs;

	private StoreHoursControl storeHoursControl;

	private void buildMainLayout()
	{
		
		FormLayout overview = new FormLayout();
		overview.setSpacing(true);
		overview.setMargin(true);
		
		BeanItem<Store> item = new BeanItem<Store>(store);
		
		storeTitle = new TextField(item.getItemProperty("name"));
		storeTitle.setRequired(true);
		storeTitle.addValidator(new BeanValidator(Store.class, "name"));
		storeTitle.setValue(store.getName());
		storeTitle.setWidth("250px");
		Button save = new Button("Save");
		save.addClickListener(new ClickListener()
		{
			private static final long serialVersionUID = 5626218707866388387L;

			@Override
			public void buttonClick(ClickEvent event)
			{
				if (DatabaseConnection.getInstance().saveStore(store))
				{
					Notification n = new Notification("Success", "Store name saved", Notification.Type.TRAY_NOTIFICATION);
					n.setDelayMsec(500);
					n.show(Page.getCurrent());
				}
				else
				{
					Notification n = new Notification("Error", "Unable to save store.", Notification.Type.TRAY_NOTIFICATION);
					n.setDelayMsec(500);
					n.show(Page.getCurrent());
				}
			}
		});
		HorizontalLayout storeName = new HorizontalLayout(storeTitle, save);
		storeName.setSpacing(true);
		storeName.setCaption("Store Title");
		overview.addComponent(storeName);
		
		BigDecimal sales = DatabaseConnection.getInstance().getSalesForStore(store.getId());
		NumberFormat formatter = NumberFormat.getCurrencyInstance();
		Label totalSales = new Label(formatter.format(sales.longValue()));
		totalSales.setCaption("Total Sales");
		
		overview.addComponent(totalSales);

		PurchaseItem pi = DatabaseConnection.getInstance().getMostCommonProductForStore(store.getId());
		
		if (pi != null)
		{
			ProductControl pc = new ProductControl(pi.getProduct());
			pc.setCaption("Most Popuplar Product");
	
			Label sold = new Label("" + pi.getQuantity());
			sold.setCaption("Quantity Sold");

			overview.addComponent(pc);
			overview.addComponent(sold);
		}
		else
		{
			Label pc = new Label("No Data");
			pc.setCaption("Most Popuplar Product");

			Label sold = new Label("No Data");
			sold.setCaption("Quantity Sold");

			overview.addComponent(pc);
			overview.addComponent(sold);
			
		}
		
		
		
		
		System.out.println(store);
		
		storeHoursControl = new StoreHoursControl(store);
		tabs.add(storeHoursControl);
		addTab(overview, "Overview");
		addTab(storeHoursControl, "Hours");
		addTab(new AddressManager(store.getAddress()), "Address");
		addTab(new StoreInventoryManager(store), "Inventory");
		
	}
}
