package com.gjd.UI.Admin;

import java.io.Serializable;

import com.gjd.model.DatabaseObjects.Store;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;

public class StoreManager extends TabSheet implements Serializable{

	private static final long serialVersionUID = 2505327117442756951L;
	
	private Label storeTitle;

	private Store store;
	
	public StoreManager(Store store) {
		this.store = store;
		buildMainLayout();
	}

	
	private void buildMainLayout() {
		storeTitle = new Label(store.getName());
		addTab(storeTitle, "Overview");


		
		addTab(new StoreHoursControl(store), "Hours");
		addTab(new AddressControl(store.getAddress()), "Address");
		
	}

}
