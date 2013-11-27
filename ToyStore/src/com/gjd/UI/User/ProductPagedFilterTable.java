package com.gjd.UI.User;

import java.sql.SQLException;

import org.tepi.filtertable.paged.PagedFilterTable;

import com.gjd.ProductFilterGenerator;
import com.gjd.model.DatabaseConnection;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.PopupView.Content;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;


public class ProductPagedFilterTable extends PagedFilterTable<SQLContainer>
{
	private class PopupEditorColumnGenerator implements ColumnGenerator
	{

		private static final long serialVersionUID = 6055189039283724770L;

		@Override
		public Object generateCell(final CustomTable source, final Object itemId, final Object columnId)
		{
			final String start = source.getItem(itemId).getItemProperty(columnId).getValue().toString();
			final int sku = (Integer)source.getItem(itemId).getItemProperty("SKU").getValue();
			PopupView editor = new PopupView(new Content()
			{
				private static final long serialVersionUID = 8497887992724226734L;
				private TextField value = new TextField("Value", start);
				
				public Component getPopupComponent()
				{
					VerticalLayout layout = new VerticalLayout();
					layout.setSpacing(true);
					layout.setMargin(true);
					layout.setSizeUndefined();
					layout.addComponent(value);
					value.setWidth(250, Unit.PIXELS);
					Button save = new Button("Save");
					save.addClickListener(new ClickListener()
					{
						private static final long serialVersionUID = -3718904408100794881L;

						public void buttonClick(ClickEvent event)
						{
							if (DatabaseConnection.getInstance().saveProductField(sku, columnId.toString(), value.getValue()))
							{
								Notification.show("Success", columnId.toString() + " updated.", Type.HUMANIZED_MESSAGE);
							}
							else
							{
								Notification.show("Error", columnId.toString() + " could not be updated.", Type.WARNING_MESSAGE);
							}
						}
					});
					
					Button reset = new Button("Reset");
					reset.addClickListener( new ClickListener()
					{
						
						private static final long serialVersionUID = 8928136573905541974L;

						@Override
						public void buttonClick(ClickEvent event)
						{
							value.setValue(start);
						}
					});
					HorizontalLayout buttons = new HorizontalLayout(save, reset);
					buttons.setSpacing(true);
					buttons.setMargin(new MarginInfo(true, false, false, false));
					layout.addComponent(buttons);
					
					return layout;
				}
				
				
				
				@Override
				public String getMinimizedValueAsHTML()
				{
					// TODO Auto-generated method stub
					return value.getValue();
				}
			});
			editor.setSizeUndefined();
			return editor;
		}
	}

	private static final long serialVersionUID = 5201294428716703556L;

	public ProductPagedFilterTable()
	{
		super();
		FreeformQuery productQuery = new FreeformQuery("SELECT * FROM Product", DatabaseConnection.getInstance()
				.getPool(), "SKU");
		
		ProductFreeformStatementDelegate delegate = new ProductFreeformStatementDelegate();
		productQuery.setDelegate(delegate);

		setFilterDecorator(new ProductFilterTableDecorator());
		setFilterGenerator(new ProductFilterGenerator(this));
		
		try
		{
			SQLContainer sc = new SQLContainer(productQuery);

			setContainerDataSource(sc);
			setVisibleColumns("SKU", "product_name", "weight", "image", "MSRP", "price", "vendor_name", "brand_name", "type_name");
			setColumnHeaders("SKU", "Product Name", "Weight", "Image", "MSRP", "Price", "Vendor Name", "Brand Name", "Product Type");
			setFilterBarVisible(true);
			setColumnCollapsingAllowed(true);
			setWidth(100, Unit.PERCENTAGE);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		addGeneratedColumn("weight", new PopupEditorColumnGenerator());
		addGeneratedColumn("price", new PopupEditorColumnGenerator());
		addGeneratedColumn("MSRP", new PopupEditorColumnGenerator());
		addGeneratedColumn("product_name", new PopupEditorColumnGenerator());

		setConverter("price", new BigDecimalConverter());
		setEditable(false);
	}
}
