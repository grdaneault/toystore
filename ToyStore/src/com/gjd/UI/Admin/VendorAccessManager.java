package com.gjd.UI.Admin;

import java.sql.SQLException;
import java.util.Collection;

import org.tepi.filtertable.paged.PagedFilterTable;

import com.gjd.UI.ProductControls.OrderFreeformStatementDelegate;
import com.gjd.UI.ProductControls.ProductFilterGenerator;
import com.gjd.UI.ProductControls.ProductFilterTableDecorator;
import com.gjd.UI.User.SuccessfulLoginListener;
import com.gjd.model.DatabaseConnection;
import com.gjd.model.DatabaseObjects.Vendor;
import com.vaadin.data.Item;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.CustomTable.ColumnGenerator;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;


public class VendorAccessManager extends VerticalLayout implements SuccessfulLoginListener, CloseListener
{

	private static final long serialVersionUID = -2648744801863658168L;
	
	private Vendor loggedIn;
	
	@Override
	public void successfulLogin(LoginWindow loginWindow)
	{
		try
		{
			loggedIn = DatabaseConnection.getInstance().getVendorById(Integer.valueOf(loginWindow.getPass()));
			loginWindow.close();
			createUI();
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			Notification.show("Error parsing password", "", Type.ERROR_MESSAGE);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Notification.show("Error retrieving information", "", Type.ERROR_MESSAGE);
		}
		
	}

	public void createUI()
	{
		try
		{
			final PagedFilterTable<SQLContainer> orders = new PagedFilterTable<SQLContainer>();

			FreeformQuery productQuery = new FreeformQuery("SELECT * FROM Order", DatabaseConnection.getInstance().getPool(), "order_id");
			productQuery.setDelegate(new OrderFreeformStatementDelegate(loggedIn));
			
			orders.setFilterDecorator(new ProductFilterTableDecorator());
			orders.setFilterGenerator(new ProductFilterGenerator(orders));
			
			
			SQLContainer sc = new SQLContainer(productQuery);
			orders.setContainerDataSource(sc);

			orders.setSelectable(true);
			orders.setMultiSelect(true);
			orders.setFilterBarVisible(true);
			orders.setColumnCollapsingAllowed(true);
			orders.setWidth(100, Unit.PERCENTAGE);
			orders.setVisibleColumns("order_id", "SKU", "product_name", "quantity", "date", "store_id", "filled", "brand_name", "MSRP");
			orders.setColumnHeaders("Order ID", "SKU", "Product Name", "Quantity", "Date", "Store", "Filled", "Brand", "MSRP");
			orders.setColumnCollapsed("brand", true);
			orders.setColumnCollapsed("MSRP", true);
			orders.setColumnReorderingAllowed(true);
			orders.addGeneratedColumn("filled", new ColumnGenerator()
			{
				
				private static final long serialVersionUID = -7474191228247498575L;

				@Override
				public Object generateCell(final CustomTable source, final Object itemId, final Object columnId)
				{
					boolean filled = (Boolean) source.getItem(itemId).getItemProperty(columnId).getValue();
					final int orderId = (Integer) source.getItem(itemId).getItemProperty("order_id").getValue();
					if (filled)
					{
						return new Label("Filled");
					}
					else
					{
						Button fill = new Button("Fill");
						fill.addClickListener(new ClickListener()
						{
							
							private static final long serialVersionUID = -5669655435882335184L;

							@SuppressWarnings("unchecked")
							@Override
							public void buttonClick(ClickEvent event)
							{
								if (DatabaseConnection.getInstance().fillOrder(orderId))
								{
									Notification.show("Success", "Order filled", Type.HUMANIZED_MESSAGE);
									source.getItem(itemId).getItemProperty(columnId).setValue(true);
									orders.refreshRowCache();
								}
								else
								{
									Notification.show("Error", "Could not fill order", Type.ERROR_MESSAGE);
								}
							}
						});
						
						return fill;
					}
				}
			});
			removeAllComponents();
			addComponent(orders);
			HorizontalLayout tableControls = orders.createControls();
			tableControls.setMargin(true);
			tableControls.setSpacing(true);
			
			HorizontalLayout otherControls = new HorizontalLayout();
			Button fillSelected = new Button("Fill Selected Orders");
			fillSelected.addClickListener(new ClickListener()
			{
				
				private static final long serialVersionUID = -5875137607657529381L;

				@SuppressWarnings("unchecked")
				@Override
				public void buttonClick(ClickEvent event)
				{
					boolean good = true;
					for (Object itemId : (Collection<Object>) orders.getValue())
					{
						int orderId = (Integer) orders.getItem(itemId).getItemProperty("order_id").getValue();
						good = good && DatabaseConnection.getInstance().fillOrder(orderId);
						if (good) 
						{
							orders.getItem(itemId).getItemProperty("filled").setValue(true);
						}
					}
					
					if (good)
					{
						Notification.show("Success", "Selected orders filled", Type.HUMANIZED_MESSAGE);
					}
					else
					{
						Notification.show("Error", "Could not ", Type.HUMANIZED_MESSAGE);
					}
					orders.refreshRowCache();
				}
			});
			
			otherControls.addComponent(fillSelected);
			
			addComponent(tableControls);
			addComponent(otherControls);
			
			
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	public void windowClose(CloseEvent e)
	{
		if (loggedIn == null)
		{
			Label error = new Label("<h2>You must log in to access this page</h2>");
			error.setContentMode(ContentMode.HTML);
			removeAllComponents();
			addComponent(error);
		}
	}
	
	public boolean isLoggedIn()
	{
		return loggedIn != null;
	}
	
	public void logOut()
	{
		loggedIn = null;
	}

	public Vendor getVendor()
	{
		return loggedIn;
	}

	public void login(Vendor vendor)
	{
		this.loggedIn = vendor;
	}
}