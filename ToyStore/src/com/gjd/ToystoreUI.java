package com.gjd;

import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;

import com.gjd.UI.Admin.BrandManager;
import com.gjd.UI.Admin.LoginWindow;
import com.gjd.UI.Admin.ProductTypeManager;
import com.gjd.UI.Admin.ReportingLayout;
import com.gjd.UI.Admin.StoreManager;
import com.gjd.UI.Admin.VendorAccessManager;
import com.gjd.UI.Admin.VendorControl;
import com.gjd.UI.User.SuccessfulLoginListener;
import com.gjd.model.DatabaseConnection;
import com.gjd.model.DatabaseObjects.Store;
import com.gjd.model.DatabaseObjects.Vendor;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Theme("toystore")
public class ToystoreUI extends UI implements Command {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = ToystoreUI.class, widgetset = "com.gjd.widgetset.ToystoreWidgetset")
	public static class Servlet extends VaadinServlet {
	}
	
	private VerticalLayout mainContent;
	
	private MenuItem storeSelect;
	private MenuItem vendorAdmin;
	private MenuItem brandAdmin;
	private MenuItem webStore;
	private MenuItem physicalStore;
	
	private Vendor vendor;
	private Store store;
	private Label headerLbl;
	private MenuItem productTypeAdmin;

	private MenuItem vendorAdminLogin;
	private MenuItem vendorAdminLogout;

	private MenuItem vendorAdminEdit;

	private MenuItem reports;
	
	@Override
	protected void init(VaadinRequest request) {
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(false);
		setContent(layout);
		
		mainContent = new VerticalLayout();
		mainContent.setSpacing(true);
		mainContent.setMargin(true);
		
		getPage().setTitle("Toy Store");
		headerLbl = new Label("<h1>Toy Store</h1>");
		headerLbl.addStyleName("store_header");
		
		MenuBar menu = new MenuBar();
		menu.setAutoOpen(true);
		menu.setWidth("100%");
		storeSelect  = menu.addItem("Store Manager", this);
		vendorAdmin = menu.addItem("Vendors", null);
		
		vendorAdminEdit = vendorAdmin.addItem("Manage Vendors", this);
		vendorAdminLogin = vendorAdmin.addItem("Vendor Access", this);
		vendorAdminLogout = vendorAdmin.addItem("Vendor Logout", this);
		vendorAdminLogout.setVisible(false);
		
		brandAdmin = menu.addItem("Brands", this);
		productTypeAdmin = menu.addItem("Product Types", this);
		reports = menu.addItem("Reports", this);

		webStore = menu.addItem("Launch Web Store", this);
		physicalStore = menu.addItem("Launch POS", this);
		
		headerLbl.setContentMode(ContentMode.HTML);
		
		
		mainContent.addComponent(new Label("Welcome to the Toy Store Master Interface.  Please choose a menu option to begin."));
		layout.addComponent(headerLbl);
		layout.addComponent(menu);
		layout.addComponent(mainContent);

	}

	@Override
	public void menuSelected(MenuItem selectedItem)
	{
		headerLbl.setValue("<h1>Toy Store</h1>");
		mainContent.removeAllComponents();
		if (selectedItem == storeSelect)
		{
			createStoreTable();
		}
		else if (selectedItem == vendorAdminEdit)
		{
			createVendorTable();
		}
		else if (selectedItem == vendorAdminLogin)
		{
			createVendorPage();
		}
		else if (selectedItem == vendorAdminLogout)
		{
			vendor = null;
			vendorAdminLogout.setVisible(false);
		}
		else if (selectedItem == brandAdmin)
		{
			createBrandTable();
		}
		else if (selectedItem == productTypeAdmin)
		{
			createProductTypeTable();
		}
		else if (selectedItem == webStore)
		{
			getPage().setLocation("WebStore");
		}
		else if (selectedItem == physicalStore)
		{
			getPage().setLocation("PhysicalStore");
		}
		else if (selectedItem == reports)
		{
			createReportsList();
		}
	}

	private void createReportsList()
	{
		mainContent.addComponent(new ReportingLayout());
	}

	private void createProductTypeTable()
	{
		getPage().setTitle("Product Type Editor");
		mainContent.addComponent(new ProductTypeManager());
		headerLbl.setValue("<h1>Toy Store</h1>");
		
	}

	private void createVendorTable()
	{
		getPage().setTitle("Vendor Editor");
		mainContent.addComponent(new VendorControl());
	}
	
	private void createVendorPage()
	{
		getPage().setTitle("Vendor Access");
		final VendorAccessManager vam = new VendorAccessManager();
		if (vendor == null)
		{
			LoginWindow loginWindow = new LoginWindow("Vendor", "Vendor", "vendor_name", "vendor_id");
			loginWindow.addLoginListener(vam);
			loginWindow.addLoginListener(new SuccessfulLoginListener()
			{
				
				public void successfulLogin(LoginWindow loginWindow)
				{
					vendorAdminLogout.setVisible(true);
					vendor = vam.getVendor();
				}
			});
			loginWindow.addCloseListener(vam);
			getUI().addWindow(loginWindow);

		}
		else
		{
			vam.login(vendor);
			vam.createUI();
		}
		
		mainContent.addComponent(vam);
	}

	private void createBrandTable()
	{
		getPage().setTitle("Brand Editor");
		mainContent.addComponent(new BrandManager());
	}

	private void createStoreTable()
	{
		getPage().setTitle("Store Selector");
		try
		{
			TableQuery stores = new TableQuery("Store", DatabaseConnection.getInstance().getPool());
			SQLContainer storeContainer = new SQLContainer(stores);
			Table storeTable = new Table("Select a store to modify");
			storeTable.setWidth("500px");
			storeTable.setContainerDataSource(storeContainer);
			storeTable.setVisibleColumns("store_id", "store_name");
			storeTable.setColumnHeaders("Store ID", "Store Name");
			storeTable.setSelectable(true);
			storeTable.addItemClickListener(new ItemClickListener()
			{
				
				@Override
				public void itemClick(ItemClickEvent event)
				{
					int store_id = (Integer) event.getItem().getItemProperty("store_id").getValue();
					System.out.println(event.getItem().getItemProperty("store_id").getValue());
					try
					{
						store = DatabaseConnection.getInstance().getStoreById(store_id);
						createStoreControl();
					}
					catch (SQLException e)
					{
						e.printStackTrace();
					}
				}

			});
			
			mainContent.removeAllComponents();
			mainContent.addComponent(storeTable);
			
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
	}

	private void createStoreControl()
	{
		mainContent.removeAllComponents();
		mainContent.addComponent(new StoreManager(store));
		headerLbl.setValue("<h1>Toy Store</h1><h2>" + store.getName() + "</h2>");
	}
}