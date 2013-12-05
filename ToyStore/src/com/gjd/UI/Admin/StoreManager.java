package com.gjd.UI.Admin;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;

import com.gjd.UI.ProductControls.ProductTable;
import com.gjd.model.DatabaseConnection;
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

	private Tab storeTab;

	private Tab hoursTab;

	private Tab addressTab;
	
	private Tab inventoryTab;
	
	private void buildMainLayout() {
		
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
		
		BigDecimal sales = DatabaseConnection.getInstance().getSalesForStore(store.getId());
		NumberFormat formatter = NumberFormat.getCurrencyInstance();
		
		Label totalSales = new Label(formatter.format(sales.longValue()));
		totalSales.setCaption("Total Sales");

		FormLayout overview = new FormLayout();
		overview.setSpacing(true);
		overview.setMargin(true);
		overview.addComponent(storeName);
		
		overview.addComponent(totalSales);
		
		
		System.out.println(store);
		
		storeHoursControl = new StoreHoursControl(store);
		tabs.add(storeHoursControl);
		storeTab = addTab(overview, "Overview");
		hoursTab = addTab(storeHoursControl, "Hours");
		addressTab = addTab(new AddressManager(store.getAddress()), "Address");
		
		ProductTable prodTab = new ProductTable();
		prodTab.setCaption("Currently only a listing of all products in the database");
		inventoryTab = addTab(new StoreInventoryManager(store), "Inventory");
		//addTab(new BrandControl(), "Brands");
		//addTab(new ProductTypeManager(), "Product Types");
		
	}
	
	public void changeStore(Store s)
	{
		System.out.println("Chainging to store " + s);
		this.store = s;
		int tab = getTabPosition(getTab(getSelectedTab()));
		removeTab(storeTab);
		removeTab(hoursTab);
		removeTab(addressTab);
		setSelectedTab(tab);
		//storeHoursControl.setStore(s);
		buildMainLayout();
	}

}
