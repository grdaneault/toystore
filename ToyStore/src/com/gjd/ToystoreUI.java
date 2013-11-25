package com.gjd;

import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;

import com.gjd.UI.Admin.StoreManager;
import com.gjd.UI.User.ProductTable;
import com.gjd.model.DatabaseConnection;
import com.gjd.model.DatabaseObjects.Store;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Theme("toystore")
public class ToystoreUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = ToystoreUI.class)
	public static class Servlet extends VaadinServlet {
	}
	
	int i = 0;
	private StoreManager storeManager;
	
	@Override
	protected void init(VaadinRequest request) {
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		setContent(layout);
		
		HorizontalLayout header = new HorizontalLayout();
		
		Label headerLbl = new Label("<h1>Toy Store!</h1>");
		ComboBox storeSelect = new ComboBox();
		
		try
		{
		storeSelect.addItem(DatabaseConnection.getInstance().getStoreById(1));
		storeSelect.addItem(DatabaseConnection.getInstance().getStoreById(2));
		storeSelect.addItem(DatabaseConnection.getInstance().getStoreById(3));
		
		storeSelect.setNewItemsAllowed(false);
		storeSelect.setFilteringMode(FilteringMode.CONTAINS);
		}
		catch (SQLException ex)
		{
			ex.printStackTrace(System.err);
			storeSelect.setEnabled(false);
		}
		
		storeSelect.setImmediate(true);
		storeSelect.addValueChangeListener(new ValueChangeListener()
		{
			
			public void valueChange(ValueChangeEvent event)
			{
				Store s = (Store) event.getProperty().getValue();
				storeManager.changeStore(s);
			}
		});
		headerLbl.setContentMode(ContentMode.HTML);
		
		header.addComponent(headerLbl);
		header.addComponent(storeSelect);
		layout.addComponent(header);
		
		try
		{
			//final Address address = DatabaseConnection.getInstance().getAddressById(1);
			//AddressManager am = new AddressManager(address);
			//layout.addComponent(am);
			
			final Store store = DatabaseConnection.getInstance().getStoreById(1);
			System.out.println(store);
			storeManager = new StoreManager(store);
			layout.addComponent(storeManager);
			
			Button button = new Button("Click Me");
			button.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					i += 1;
						//Store s = DatabaseConnection.getInstance().getStoreById(i);
						//layout.addComponent(new Label("Thank you for clicking (" + i + "): "));
						//layout.addComponent(new Label("Store " + s.toString()));
						//layout.addComponent(new Label(address.toString()));
						//System.out.println(address);
					
				}
			});
			//layout.addComponent(button);
		}
		catch (SQLException ex)
		{
			ex.printStackTrace(System.err);
		}
		
		
		
		
		ProductTable products = new ProductTable();
		products.setPageLength(10);
		layout.addComponent(products);
		layout.addComponent(products.createControls());
		
	}

}