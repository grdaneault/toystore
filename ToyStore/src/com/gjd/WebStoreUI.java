package com.gjd;

import javax.servlet.annotation.WebServlet;

import com.gjd.UI.ProductControls.ProductPagedFilterTable;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Theme("toystore")
public class WebStoreUI extends UI implements Command
{

	@WebServlet(value = "/WebStore/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = WebStoreUI.class, widgetset = "com.gjd.widgetset.ToystoreWidgetset")
	public static class Servlet extends VaadinServlet {
	}
	
	private String username = null;
	
	private VerticalLayout mainContent;
	private MenuItem home;
	private MenuItem account;
	
	protected void init(VaadinRequest request)
	{
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(false);
		setContent(layout);
		
		Label headerLbl = new Label("<h1>Online Toy Store!</h1>" + request.getPathInfo());
		headerLbl.setContentMode(ContentMode.HTML);
		headerLbl.addStyleName("store_header");
		layout.addComponent(headerLbl);
		
		MenuBar menu = new MenuBar();
		home = menu.addItem("Home", this); 
		MenuItem categories = menu.addItem("Browse", null, null);
		categories.addItem("By Product Type", null);
		categories.addItem("By Brand", null);
		account = menu.addItem("Log In", new Command()
		{
			
			public void menuSelected(MenuItem selectedItem)
			{
				if (username == null)
				{
					username = "Greg Daneault";
					account.setText("Log Out");
				}
				else
				{
					account.setText("Log In");
					username = null;
				}
			}
		});
		menu.setWidth("100%");
		layout.addComponent(menu);
		
		mainContent = new VerticalLayout();
		layout.addComponent(mainContent);
	}
	
	private Component loadBrowseByBrand()
	{
		return new ProductPagedFilterTable();
	}

	@Override
	public void menuSelected(MenuItem selectedItem)
	{
		if (selectedItem == home)
		{
			mainContent.removeAllComponents();
			mainContent.addComponent(loadBrowseByBrand());
		}
	}

}
