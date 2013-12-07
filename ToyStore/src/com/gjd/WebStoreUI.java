package com.gjd;

import java.sql.SQLException;
import java.util.Collection;

import javax.servlet.annotation.WebServlet;

import com.gjd.UI.Admin.LoginWindow;
import com.gjd.UI.ProductControls.ProductControl;
import com.gjd.UI.ProductControls.ProductFreeformStatementDelegate;
import com.gjd.UI.ProductControls.ProductPagedFilterTable;
import com.gjd.UI.User.CheckoutListener;
import com.gjd.UI.User.CheckoutWindow;
import com.gjd.UI.User.PurchaseTable;
import com.gjd.UI.User.RegisterWindow;
import com.gjd.UI.User.SuccessfulLoginListener;
import com.gjd.model.DatabaseConnection;
import com.gjd.model.DatabaseObjects.Brand;
import com.gjd.model.DatabaseObjects.Customer;
import com.gjd.model.DatabaseObjects.Product;
import com.gjd.model.DatabaseObjects.ProductType;
import com.gjd.model.DatabaseObjects.Purchase;
import com.gjd.model.DatabaseObjects.Store;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Theme("toystore")
public class WebStoreUI extends UI implements Command
{
	
	private static final int WEB_STORE_ID = 8;

	
	@WebServlet(value = "/WebStore/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = WebStoreUI.class, widgetset = "com.gjd.widgetset.ToystoreWidgetset")
	public static class Servlet extends VaadinServlet {
	}
	
	private Customer customer;
	private Store store;
	private VerticalLayout mainContent;
	private MenuItem home;
	private MenuItem account;
	
	private Purchase purchase;
	
	protected void init(VaadinRequest request)
	{
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(false);
		setContent(layout);
		
		try
		{
			store = DatabaseConnection.getInstance().getStoreById(WEB_STORE_ID);
			customer = store.getGeneric();
			purchase = new Purchase(store);
		}
		catch (SQLException e)
		{
			store = null;
			customer = null;
		}
		
		Label headerLbl = new Label("<h1>Online Toy Store</h1>");
		headerLbl.setContentMode(ContentMode.HTML);
		headerLbl.addStyleName("store_header");
		layout.addComponent(headerLbl);
		
		MenuBar menu = new MenuBar();
		home = menu.addItem("Home", this); 
		MenuItem categories = menu.addItem("Browse", null, null);
		categories.addItem("By Product Type", new Command()
		{
			@Override
			public void menuSelected(MenuItem selectedItem)
			{
				browseByProductType();
			}
		});
		categories.addItem("By Brand", new Command()
		{
			
			@Override
			public void menuSelected(MenuItem selectedItem)
			{
				browseByBrand();
			}
		});
		categories.addItem("Show All", new Command()
		{
			
			@Override
			public void menuSelected(MenuItem selectedItem)
			{
				showAll();
			}
		});
		
		account = menu.addItem("Log In", new Command()
		{
			
			public void menuSelected(MenuItem selectedItem)
			{
				if (customer == store.getGeneric())
				{
					account.setText("Log In");
					customer = store.getGeneric();
					LoginWindow lw = new LoginWindow("Login", "Customer", "email", "customer_id");
					getUI().addWindow(lw);
					lw.addLoginListener(new SuccessfulLoginListener()
					{
						
						@Override
						public void successfulLogin(LoginWindow loginWindow)
						{
							try
							{
								customer = DatabaseConnection.getInstance().getCustomerById(Integer.valueOf(loginWindow.getPass()));
								loginWindow.close();
								account.setText("Log Out " + customer.getFirst());
							}
							catch (SQLException e)
							{
								Notification.show("Error", "Could not load user profile", Type.ERROR_MESSAGE);
								e.printStackTrace();
							}
						}
					});
				}
				else
				{
					Notification.show("Goodbye " + customer.getFirst(), "Come back soon!", Type.HUMANIZED_MESSAGE);
					account.setText("Log In");
					customer = store.getGeneric();
				}
			}
		});
		
		menu.addItem("Cart", new Command()
		{
			@Override
			public void menuSelected(MenuItem selectedItem)
			{
				showCart();
			}
		});
		
		menu.addItem("Create Account", new Command()
		{
			
			@Override
			public void menuSelected(MenuItem selectedItem)
			{
				RegisterWindow rw = new RegisterWindow();
				getUI().addWindow(rw);
			}
		});
		
		
		menu.setWidth("100%");
		layout.addComponent(menu);
		
		mainContent = new VerticalLayout();
		mainContent.setSpacing(true);
		mainContent.setMargin(true);
		layout.addComponent(mainContent);
		
		home();
	}
	
	protected void showCart()
	{
		mainContent.removeAllComponents();
		final PurchaseTable purchaseTable = new PurchaseTable(purchase);
		purchaseTable.setVisibleColumns("product", "price", "quantity", "update quantity", "total price", "remove");
		
		Button clear = new Button("Clear Cart");
		clear.addClickListener(new ClickListener()
		{
			
			@Override
			public void buttonClick(ClickEvent event)
			{
				purchaseTable.removeAllItems();
				purchase = new Purchase(store);
				purchaseTable.setColumnFooter("quantity", "0");
				purchaseTable.setColumnFooter("total price", "0");
				Notification.show("Cart Cleared", "", Type.HUMANIZED_MESSAGE);
			}
		});
		
		Button checkout = new Button("Checkout");
		checkout.addClickListener(new ClickListener()
		{
			
			@Override
			public void buttonClick(ClickEvent event)
			{
				if (purchaseTable.getContainerDataSource().size() == 0)
				{
					Notification.show("No items selected for purchase", "", Type.WARNING_MESSAGE);
				}
				else
				{
					createCheckoutWindow();
				}
			}
		});
		
		HorizontalLayout cartOptions = new HorizontalLayout(clear, checkout);
		cartOptions.setMargin(true);
		cartOptions.setSpacing(true);
		
		mainContent.addComponent(purchaseTable);
		mainContent.addComponent(cartOptions);
	}

	protected void createCheckoutWindow()
	{
		CheckoutWindow cw = new CheckoutWindow(purchase, customer);
		cw.setCheckoutListener(new CheckoutListener()
		{
			
			@Override
			public void successfulCheckout(CheckoutWindow window)
			{
				purchase = new Purchase(store);
				home();
			}
		});
		getUI().addWindow(cw);		
	}

	protected void browseByProductType()
	{
		try
		{
			Table productTypes = new Table();
			SQLContainer sc = new SQLContainer(new TableQuery("ProductType", DatabaseConnection.getInstance().getPool()));
			productTypes.setContainerDataSource(sc);
			productTypes.setVisibleColumns("type_name");
			productTypes.setColumnHeaders("Product Type");
			productTypes.setWidth(200, Unit.PIXELS);
			productTypes.setSelectable(true);
			
			productTypes.addItemClickListener(new ItemClickListener()
			{
				
				@Override
				public void itemClick(ItemClickEvent event)
				{
					Filter f = new Compare.Equal("type_id", event.getItem().getItemProperty("type_id").getValue());
					createProductTable(f);
				}
			});
			
			mainContent.removeAllComponents();
			mainContent.addComponent(productTypes);
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	protected void browseByBrand()
	{
		try
		{
			Table brand = new Table();
			SQLContainer sc = new SQLContainer(new TableQuery("Brand", DatabaseConnection.getInstance().getPool()));
			brand.setContainerDataSource(sc);
			brand.setVisibleColumns("brand_name");
			brand.setColumnHeaders("Brand");
			brand.setWidth(200, Unit.PIXELS);
			brand.setSelectable(true);
			
			brand.addItemClickListener(new ItemClickListener()
			{
				
				@Override
				public void itemClick(ItemClickEvent event)
				{
					Filter f = new Compare.Equal("brand_id", event.getItem().getItemProperty("brand_id").getValue());
					createProductTable(f);
				}
			});
			
			mainContent.removeAllComponents();
			mainContent.addComponent(brand);
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
	}

	protected void createProductTable(Filter f)
	{
		mainContent.removeAllComponents();
		ProductPagedFilterTable ppft = new ProductPagedFilterTable(new ProductFreeformStatementDelegate());
		ppft.addGeneratedColumn("product", new CustomTable.ColumnGenerator()
		{
			@Override
			public Object generateCell(CustomTable source, Object itemId, Object columnId)
			{
				int sku = (Integer)source.getItem(itemId).getItemProperty("SKU").getValue();
				String name = source.getItem(itemId).getItemProperty("product_name").getValue().toString();
				String image = source.getItem(itemId).getItemProperty("image").getValue().toString();
				double weight = (Double)source.getItem(itemId).getItemProperty("weight").getValue();
				float price = Float.valueOf(source.getItem(itemId).getItemProperty("price").getValue().toString());
				ProductType type = new ProductType((Integer)source.getItem(itemId).getItemProperty("type_id").getValue(), source.getItem(itemId).getItemProperty("type_name").getValue().toString());
				Brand brand = new Brand((Integer)source.getItem(itemId).getItemProperty("brand_id").getValue(), source.getItem(itemId).getItemProperty("brand_name").getValue().toString());
				Product p = new Product(sku, name, image, weight, price, price, type, brand, null);
				ProductControl pc = new ProductControl(p, purchase);
				
				return pc;
			}
		});
		
		ppft.setFilterBarVisible(false);
		ppft.getContainerDataSource().sort(new String[] { "product_name" }, new boolean[] {true} );
		
		ppft.setVisibleColumns("product");
		
		if (f != null)
		{
			ppft.getContainerDataSource().addContainerFilter(f);
		}
		HorizontalLayout controls = ppft.createControls();
		controls.setMargin(true);
		controls.setSpacing(true);
		
		mainContent.addComponent(ppft);
		mainContent.addComponent(controls);
	}

	protected void showAll()
	{
		createProductTable(null);
	}

	@Override
	public void menuSelected(MenuItem selectedItem)
	{
		if (selectedItem == home)
		{
			home();
			
			
			
			
		}
	}

	private void home()
	{
		mainContent.removeAllComponents();
		Label content = new Label("<h2>Welcome!</h2>");
		content.setContentMode(ContentMode.HTML);
		Label message = new Label("<p>Welcome to the Online Toy Store!  We hope you can find everything you are looking for.  If you have a customer account with us, you can log in using the menu above and entering your email as the username and your customer ID as the password.  Thank you for your business!</p>");
		message.setContentMode(ContentMode.HTML);
		
		mainContent.addComponent(content);
		mainContent.addComponent(message);
		
		Label popItemHeader = new Label("<h2>Popular Items</h2>");
		popItemHeader.setContentMode(ContentMode.HTML);
		mainContent.addComponent(popItemHeader);
		
		Collection<Product> popularProductSKUs = DatabaseConnection.getInstance().getTopSellers(5);
		
		Table popularProducts = new Table();
		popularProducts.addContainerProperty("product", ProductControl.class, null);
		for (Product sku : popularProductSKUs)
		{
			popularProducts.addItem(DatabaseConnection.getInstance().getProductByIdForStore(sku.getSKU(), WEB_STORE_ID));
		}
		popularProducts.addGeneratedColumn("product", new ColumnGenerator()
		{
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId)
			{
				return new ProductControl((Product) itemId, purchase);
			}
		});
		
		popularProducts.setWidth(600, Unit.PIXELS);
		
		mainContent.addComponent(popularProducts);
	}

}
