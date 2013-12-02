package com.gjd.UI.Admin;

import java.util.Collection;

import org.vaadin.hene.popupbutton.PopupButton;

import com.gjd.UI.ProductControls.PopupEditorColumnGenerator;
import com.gjd.UI.ProductControls.ProductPagedFilterTable;
import com.gjd.model.DatabaseConnection;
import com.gjd.model.DatabaseObjects.Store;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;


public class StoreInventoryManager extends VerticalLayout
{
	private static final long serialVersionUID = 1204318750562593352L;

	private Store store;
	private ProductPagedFilterTable products;

	public StoreInventoryManager(Store store)
	{
		this.store = store;
		DatabaseConnection.getInstance().ensureStoreInventory(store.getId());
		products = new ProductPagedFilterTable(new InventoryFreeformStatementDelegate(store.getId()));
		
		products.setVisibleColumns("SKU", "product_name", "weight", "MSRP", "price", "quantity", "desired_quantity", "reorder_threshold", "vendor_name", "brand_name", "type_name");
		products.setColumnHeaders("SKU", "product name", "weight", "MSRP", "price", "quantity", "desired quantity", "reorder threshold", "vendor name", "brand name", "type name");
		addComponent(products);
		addComponent(products.createControls());
		
		products.addPopupColumn(new PopupEditorColumnGenerator.ProductSaveHandler(), "product_name", "weight", "MSRP");
		products.addPopupColumn(new PopupEditorColumnGenerator.InventorySaveHandler(), "price", "quantity", "desired_quantity", "reorder_threshold");
		products.setSelectable(true);
		products.setMultiSelect(true);
		final Button updateSelected = new Button("With Selected...");
		final Window w = new Window("With Selected...");
		//w.setModal(true);
		w.setContent(createSetButtons());
		w.center();
		updateSelected.addClickListener(new ClickListener()
		{
			
			private static final long serialVersionUID = -513000343800620234L;

			@Override
			public void buttonClick(ClickEvent event)
			{
				getUI().addWindow(w);
			}
		});
		
		Button createAllReorders = new Button("Automaticaly Create Reorders");
		createAllReorders.addClickListener(new ClickListener()
		{
			
			@Override
			public void buttonClick(ClickEvent event)
			{
					
				int store = StoreInventoryManager.this.store.getId();
				int orders = DatabaseConnection.getInstance().createAllStoreOrders(store);
				if (orders < 0)
				{
					Notification.show("Error", "Could not create orders", Type.ERROR_MESSAGE);
				}
				else
				{
					Notification.show("Success", orders +  " orders created", Type.HUMANIZED_MESSAGE);
				}
			}
		});
		HorizontalLayout dataControls = new HorizontalLayout(updateSelected, createAllReorders);
		dataControls.setSpacing(true);
		addComponent(dataControls);
	}

	private Component createSetButtons()
	{
		VerticalLayout setAllButtons = new VerticalLayout();
		setAllButtons.setMargin(true);
		setAllButtons.setSpacing(true);
		
		setAllButtons.addComponent(createSetAllButton("Desired Quantity", "desired_quantity"));
		setAllButtons.addComponent(createSetAllButton("Quantity", "quantity"));
		setAllButtons.addComponent(createSetAllButton("Reorder Threshold", "reorder_threshold"));
		
		Button createReorder = new Button("Create Reorders");
		createReorder.addClickListener(new ClickListener()
		{
			
			@Override
			public void buttonClick(ClickEvent event)
			{
					
				int orders = 0;
				for (Object item : (Collection<Object>) products.getValue())
				{
					int sku = (Integer) products.getItem(item).getItemProperty("SKU").getValue();
					int store = (Integer) products.getItem(item).getItemProperty("store_id").getValue();
					int temp = DatabaseConnection.getInstance().createOrder(sku, store);
					if (temp < 0)
					{
						Notification.show("Error", "Could not create order for item " + sku, Type.ERROR_MESSAGE);
					}
					else
					{
						orders += temp;
					}
				}
				if (orders > 0)
				{
					Notification.show("Success", orders +  " orders created", Type.HUMANIZED_MESSAGE);
				}
				else
				{
					Notification.show("Success", "No orders created", Type.HUMANIZED_MESSAGE);
				}
			}
		});
		
		setAllButtons.addComponent(createReorder);
		return setAllButtons;
	}

	private Component createSetAllButton(final String caption, final String column)
	{
		final PopupButton setAll = new PopupButton("Set Selected " + caption);
		final TextField newval = new TextField();
		Button setAllSave = new Button("Save");
		
		setAllSave.addClickListener(new ClickListener()
		{
			
			@Override
			public void buttonClick(ClickEvent event)
			{
				boolean good = true;
				for (Object item : (Collection<Object>) products.getValue())
				{
					int sku = (Integer) products.getItem(item).getItemProperty("SKU").getValue();
					int store = (Integer) products.getItem(item).getItemProperty("store_id").getValue();
					good = good && DatabaseConnection.getInstance().saveInventoryField(sku, store, column, newval.getValue());
					products.getItem(item).getItemProperty(column).setValue(Integer.valueOf(newval.getValue()));
				}
				if (good)
				{
					Notification.show("Success", caption + "updated", Type.HUMANIZED_MESSAGE);
					setAll.setPopupVisible(false);
				}
				else
				{
					Notification.show("Error", "Could not update all " + caption, Type.ERROR_MESSAGE);
				}
				
				products.refreshRowCache();
			}
		});
		
		VerticalLayout setAllLayout = new VerticalLayout(newval, setAllSave);
		setAll.setContent(setAllLayout);
		setAllLayout.setSpacing(true);
		
		return setAll;
	}
	
	
}
