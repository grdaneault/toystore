package com.gjd.UI.Admin;

import java.sql.SQLException;

import com.gjd.model.DatabaseConnection;
import com.gjd.model.DatabaseObjects.Brand;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class BrandControl extends VerticalLayout
{
	SQLContainer bc;
	
	public class RemoveColumnGenerator implements ColumnGenerator
	{

		@Override
		public Object generateCell(Table source, Object itemId, Object columnId)
		{
			final Object item = itemId;
			final Button remove = new Button("X");
			remove.addClickListener(new ClickListener()
			{
				@Override
				public void buttonClick(ClickEvent event)
				{
					try
					{
						bc.removeItem(item);
						bc.commit();
					}
					catch (SQLException ex)
					{
						ex.printStackTrace(System.err);
					}
				}
			});
			return remove;
		}

	}

	private static final long serialVersionUID = 7949836029759808474L;

	private Brand brand;
	private TextField name;

	public BrandControl(Brand b)
	{
		this.brand = b == null ? new Brand(-1, "") : b;
		setMargin(true);
		setSpacing(true);
		BeanItem<Brand> item = new BeanItem<Brand>(this.brand);
		name = new TextField(item.getItemProperty("name"));
		name.setImmediate(true);

		try
		{
			TableQuery brands = new TableQuery("Brand", DatabaseConnection.getInstance().getPool());
			bc = new SQLContainer(brands);
			Table brandTable = new Table("Brands", bc);
			brandTable.setColumnCollapsingAllowed(true);
			brandTable.setColumnCollapsed("brand_id", true);
			brandTable.setColumnCollapsible("brand_name", false);
			brandTable.setSelectable(true);
			brandTable.setWidth("300px");
			
			//brandTable.addContainerProperty("remove", Button.class, null);
			brandTable.addGeneratedColumn("remove", new RemoveColumnGenerator());
			brandTable.setColumnHeaders("ID", "Brand Name", "Remove");

			brandTable.addItemClickListener(new ItemClickListener()
			{

				private static final long serialVersionUID = -671413768192261312L;

				@Override
				public void itemClick(ItemClickEvent event)
				{
					name.setValue((String) event.getItem().getItemProperty("brand_name").getValue());
				}
			});

			// bc.getItem(nb).getItemProperty("brand_name").setValue("Autogen");
			// bc.commit();
			Button addBrand = new Button("New Brand");
			addBrand.addClickListener(new ClickListener()
			{

				private static final long serialVersionUID = -3848256903363909965L;

				@SuppressWarnings("unchecked")
				@Override
				public void buttonClick(ClickEvent event)
				{
					Object newBrand = bc.addItem();
					bc.getItem(newBrand).getItemProperty("brand_name").setValue("New Brand");
					try
					{
						bc.commit();
					}
					catch (UnsupportedOperationException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					catch (SQLException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			addComponent(addBrand);
			addComponent(brandTable);

		}
		catch (Exception ex)
		{
			ex.printStackTrace(System.err);
		}

		addComponent(name);

		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setMargin(true);
		buttons.setSpacing(true);
		Button save = new Button("Add");
		save.addClickListener(new ClickListener()
		{

			private static final long serialVersionUID = 4392009671146429000L;

			@Override
			public void buttonClick(ClickEvent event)
			{
				DatabaseConnection.getInstance().saveBrand(brand);
				DatabaseConnection.getInstance().commit();

			}
		});

		Button reset = new Button("Reset");
		reset.addClickListener(new ClickListener()
		{

			private static final long serialVersionUID = 5741755040045435705L;

			@Override
			public void buttonClick(ClickEvent event)
			{
			}
		});
		buttons.addComponent(save);
		buttons.addComponent(reset);

		addComponent(buttons);

	}
}
