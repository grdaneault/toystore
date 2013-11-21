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

public class ProductTypeManager extends VerticalLayout
{

	SQLContainer pTypeContainer;

	public class RemoveColumnGenerator implements ColumnGenerator
	{

		private static final long serialVersionUID = 1643311692916369231L;

		@Override
		public Object generateCell(Table source, Object itemId, Object columnId)
		{
			final Object item = itemId;
			final Button remove = new Button("X");
			remove.addClickListener(new ClickListener()
			{

				/**
				 * 
				 */
				private static final long serialVersionUID = 6129335528597265495L;

				@Override
				public void buttonClick(ClickEvent event)
				{
					try
					{
						pTypeContainer.removeItem(item);
						pTypeContainer.commit();
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

	public ProductTypeManager()
	{
		setMargin(true);
		setSpacing(true);

		try
		{
			Label header = new Label("<h2>Product Types</h2>");
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
		TableQuery brands = new TableQuery("ProductType", DatabaseConnection.getInstance().getPool());
		pTypeContainer = new SQLContainer(brands);
		Table pTypeTable = new Table();
		pTypeTable.setContainerDataSource(pTypeContainer);

		// Hide the ID column (by default)
		pTypeTable.setColumnCollapsingAllowed(true);
		pTypeTable.setColumnCollapsed("type_id", true);
		pTypeTable.setColumnCollapsible("type_name", false);

		pTypeTable.setWidth("300px");
		// Enable editing (and the associated SQL Magic ;) )
		pTypeTable.setEditable(true);

		// Add remove buttons
		pTypeTable.addGeneratedColumn("remove", new RemoveColumnGenerator());

		// Set the headers
		pTypeTable.setColumnHeaders("ID", "Product Type", "Remove");
		return pTypeTable;
	}

	private HorizontalLayout createButtonLayout()
	{
		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setSpacing(true);
		Button addProductType = new Button("New Product Type");
		addProductType.addClickListener(new ClickListener()
		{

			private static final long serialVersionUID = -3848256903363909965L;

			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event)
			{
				Object newBrand = pTypeContainer.addItem();
				pTypeContainer.getItem(newBrand).getItemProperty("type_name").setValue("New Product Type");
				try
				{
					pTypeContainer.commit();
					Notification n = new Notification("Success", "Brand created", Notification.Type.TRAY_NOTIFICATION);
					n.setDelayMsec(500);
					n.show(Page.getCurrent());
				}
				catch (SQLException e)
				{
					Notification n = new Notification("Error", "Error creating product type:" + e.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
					n.show(Page.getCurrent());
					e.printStackTrace();
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
					pTypeContainer.commit();
					Notification n = new Notification("Success", "Product types saved", Notification.Type.TRAY_NOTIFICATION);
					n.setDelayMsec(500);
					n.show(Page.getCurrent());
				}
				catch (SQLException e)
				{
					Notification n = new Notification("Error", "Error updating product types" + e.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
					n.show(Page.getCurrent());
					e.printStackTrace();
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
					pTypeContainer.rollback();
					Notification n = new Notification("Success", "Product types reset", Notification.Type.TRAY_NOTIFICATION);
					n.setDelayMsec(500);
					n.show(Page.getCurrent());
				}
				catch (SQLException e)
				{
					Notification n = new Notification("Error", "Error rolling back transaction:" + e.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
					n.show(Page.getCurrent());
					e.printStackTrace();
				}
			}
		});

		buttons.addComponent(addProductType);
		buttons.addComponent(save);
		buttons.addComponent(reset);

		return buttons;
	}
}
