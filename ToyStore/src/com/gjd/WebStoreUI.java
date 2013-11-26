package com.gjd;

import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;

import org.tepi.filtertable.FilterGenerator;
import org.tepi.filtertable.FilterTable;
import org.tepi.filtertable.paged.PagedFilterTable;

import com.gjd.UI.User.ProductTable;
import com.gjd.UI.User.ProductTable.ProductFreeformStatementDelegate;
import com.gjd.model.DatabaseConnection;
import com.gjd.model.DatabaseObjects.Brand;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
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
		
		HorizontalLayout header = new HorizontalLayout();
		
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
	
	private Component loadHomePage()
	{
		ProductTable products = new ProductTable();
		products.sortByPopularity(5);
		return products;
	}
	
	private Component loadBrowseByBrand()
	{
		FreeformQuery productQuery = new FreeformQuery("SELECT * FROM Product", DatabaseConnection.getInstance()
				.getPool(), "SKU");
		
		ProductTable p = new ProductTable();
		ProductFreeformStatementDelegate delegate = p.new ProductFreeformStatementDelegate();
		productQuery.setDelegate(delegate);

		PagedFilterTable<SQLContainer> table = new PagedFilterTable<SQLContainer>();
		try
		{
			SQLContainer sc = new SQLContainer(productQuery);
			table.setContainerDataSource(sc);
			table.setFilterBarVisible(true);
			
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		
		return table;
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
