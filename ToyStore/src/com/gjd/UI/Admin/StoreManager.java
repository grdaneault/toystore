package com.gjd.UI.Admin;

import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.text.DefaultEditorKit.InsertTabAction;

import com.gjd.model.DatabaseObjects.Store;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;

public class StoreManager extends TabSheet implements Serializable{

	private static final long serialVersionUID = 2505327117442756951L;
	
	private Label storeTitle;

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
	
	private void buildMainLayout() {
		storeTitle = new Label(store.getName());
		System.out.println(store);
		
		storeHoursControl = new StoreHoursControl(store);
		tabs.add(storeHoursControl);
		storeTab = addTab(storeTitle, "Overview");
		hoursTab = addTab(storeHoursControl, "Hours");
		addressTab = addTab(new AddressManager(store.getAddress()), "Address");
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
