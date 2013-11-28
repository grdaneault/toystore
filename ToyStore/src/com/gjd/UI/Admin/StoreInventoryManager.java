package com.gjd.UI.Admin;

import java.util.Collection;

import org.vaadin.hene.popupbutton.PopupButton;

import com.gjd.UI.ProductControls.PopupEditorColumnGenerator;
import com.gjd.UI.ProductControls.ProductPagedFilterTable;
import com.gjd.model.DatabaseConnection;
import com.gjd.model.DatabaseObjects.Store;
import com.vaadin.shared.ui.MultiSelectMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.PopupView.Content;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;


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
		final PopupButton setAll = new PopupButton("Set All Desired Quantities");
		final TextField newval = new TextField();
		Button setAllSave = new Button("Set All Desired Quantities");
		
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
					good = good && DatabaseConnection.getInstance().saveInventoryField(sku, store, "desired_quantity", newval.getValue());
					products.getItem(item).getItemProperty("desired_quantity").setValue(Integer.valueOf(newval.getValue()));
				}
				if (good)
				{
					Notification.show("Success", "Desired quantities updated", Type.HUMANIZED_MESSAGE);
					setAll.setPopupVisible(false);
				}
				else
				{
					Notification.show("Error", "Could not update all desired quantities", Type.ERROR_MESSAGE);
				}
				
				products.refreshRowCache();
			}
		});
		VerticalLayout setAllLayout = new VerticalLayout(newval, setAllSave);
		setAll.setContent(setAllLayout);
		setAllLayout.setSpacing(true);
		HorizontalLayout dataControls = new HorizontalLayout(setAll);
		addComponent(dataControls);
	}
	
	
}
