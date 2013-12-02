package com.gjd.UI.Admin;

import java.sql.SQLException;

import com.gjd.model.DatabaseConnection;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.VerticalLayout;

public class BrandManager extends VerticalLayout
{

	SQLContainer brandContainer;

	public class RemoveColumnGenerator implements ColumnGenerator
	{

		private static final long serialVersionUID = -1189223015895769902L;

		@Override
		public Object generateCell(Table source, Object itemId, Object columnId)
		{
			final Object item = itemId;
			final Button remove = new Button("X");
			remove.addClickListener(new ClickListener()
			{

				private static final long serialVersionUID = -8605828896793166762L;

				@Override
				public void buttonClick(ClickEvent event)
				{
					try
					{
						brandContainer.removeItem(item);
						brandContainer.commit();
						Notification n = new Notification("Success", "Brand removed", Notification.Type.TRAY_NOTIFICATION);
						n.setDelayMsec(500);
						n.show(Page.getCurrent());
					}
					catch (SQLException e)
					{
						Notification n = new Notification("Error", "Error removing brand:" + e.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
						n.show(Page.getCurrent());
						e.printStackTrace();
					}
				}
			});
			return remove;
		}
	}

	private static final long serialVersionUID = 7949836029759808474L;
	private Table brandTable;

	public BrandManager()
	{
		setMargin(true);
		setSpacing(true);

		try
		{
			Label header = new Label("<h2>Brands</h2>");
			header.setContentMode(ContentMode.HTML);

			Table brandTable = createBrandTable();
			HorizontalLayout buttons = createButtonLayout();
			
			addComponent(header);
			addComponent(buttons);
			addComponent(brandTable);
		}
		catch (Exception ex)
		{
			ex.printStackTrace(System.err);
		}

	}

	private Table createBrandTable() throws SQLException
	{
		TableQuery brands = new TableQuery("Brand", DatabaseConnection.getInstance().getPool());
		brandContainer = new SQLContainer(brands);
		brandTable = new Table();
		brandTable.setContainerDataSource(brandContainer);

		// Hide the ID column (by default)
		brandTable.setColumnCollapsingAllowed(true);
		brandTable.setColumnCollapsed("brand_id", true);
		brandTable.setColumnCollapsible("brand_name", false);

		brandTable.setWidth("500px");
		// Enable editing (and the associated SQL Magic ;) )
		brandTable.setEditable(true);

		// Add remove buttons
		brandTable.addGeneratedColumn("remove", new RemoveColumnGenerator());

		// Set the headers
		brandTable.setColumnHeaders("ID", "Brand Name", "Remove");
		return brandTable;
	}

	private HorizontalLayout createButtonLayout()
	{
		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setSpacing(true);
		Button addBrand = new Button("New Brand");
		addBrand.addClickListener(new ClickListener()
		{

			private static final long serialVersionUID = -3848256903363909965L;

			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event)
			{
				Object newBrand = brandContainer.addItem();
				brandContainer.getItem(newBrand).getItemProperty("brand_name").setValue("New Brand");
				try
				{
					brandContainer.commit();
					brandTable.refreshRowCache();
					Notification.show("Success", "Brand created", Notification.Type.HUMANIZED_MESSAGE);
				}
				catch (SQLException e)
				{
					Notification.show("Error", "Error creating brand:" + e.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
				}
			}
		});

		Button save = new Button("Save");
		save.addClickListener(new ClickListener()
		{

			private static final long serialVersionUID = 4392009671146429000L;

			@Override
			public void buttonClick(ClickEvent event)
			{
				try
				{
					brandContainer.commit();
					Notification.show("Success", "Brands saved", Notification.Type.HUMANIZED_MESSAGE);
				}
				catch (SQLException e)
				{
					Notification.show("Error", "Error updating brands" + e.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
				}
			}
		});

		Button reset = new Button("Reset");
		reset.addClickListener(new ClickListener()
		{

			private static final long serialVersionUID = 5741755040045435705L;

			@Override
			public void buttonClick(ClickEvent event)
			{
				try
				{
					brandContainer.rollback();
					Notification.show("Success", "Brands reset", Notification.Type.HUMANIZED_MESSAGE);
				}
				catch (SQLException e)
				{
					Notification.show("Error", "Error rolling back transaction:" + e.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
				}
			}
		});

		buttons.addComponent(addBrand);
		buttons.addComponent(save);
		buttons.addComponent(reset);

		return buttons;
	}
}
