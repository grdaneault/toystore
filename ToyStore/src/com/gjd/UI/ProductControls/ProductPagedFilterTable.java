package com.gjd.UI.ProductControls;

import java.sql.SQLException;

import org.tepi.filtertable.paged.PagedFilterTable;

import com.gjd.model.DatabaseConnection;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.data.util.sqlcontainer.query.FreeformStatementDelegate;


public class ProductPagedFilterTable extends PagedFilterTable<SQLContainer>
{
	private static final long serialVersionUID = 5201294428716703556L;

	
	public ProductPagedFilterTable()
	{
		this(new ProductFreeformStatementDelegate());
	}
	
	public ProductPagedFilterTable(FreeformStatementDelegate delegate)
	{
		super();
		FreeformQuery productQuery = new FreeformQuery("SELECT * FROM Product", DatabaseConnection.getInstance()
				.getPool(), "SKU");
		
		productQuery.setDelegate(delegate);

		setFilterDecorator(new ProductFilterTableDecorator());
		setFilterGenerator(new ProductFilterGenerator(this));
		
		try
		{
			SQLContainer sc = new SQLContainer(productQuery);

			setContainerDataSource(sc);
			//setVisibleColumns("SKU", "product_name", "weight", "image", "MSRP", "price", "vendor_name", "brand_name", "type_name");
			//setColumnHeaders("SKU", "Product Name", "Weight", "Image", "MSRP", "Price", "Vendor Name", "Brand Name", "Product Type");
			setFilterBarVisible(true);
			setColumnCollapsingAllowed(true);
			setWidth(100, Unit.PERCENTAGE);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		/*
		addGeneratedColumn("weight", new PopupEditorColumnGenerator());
		addGeneratedColumn("price", new PopupEditorColumnGenerator());
		addGeneratedColumn("MSRP", new PopupEditorColumnGenerator());
		addGeneratedColumn("product_name", new PopupEditorColumnGenerator());

		setConverter("price", new BigDecimalConverter());*/
		setEditable(false);
	}
	
	public void addPopupColumn(PopupEditorSaveHandler saveHandler, String... columns)
	{
		for (int i = 0; i < columns.length; i++)
		{
			addGeneratedColumn(columns[i], new PopupEditorColumnGenerator(saveHandler));
		}
	}
}
