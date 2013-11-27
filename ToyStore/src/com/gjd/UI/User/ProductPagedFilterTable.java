package com.gjd.UI.User;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Locale;

import org.tepi.filtertable.paged.PagedFilterTable;

import com.gjd.ProductFilterGenerator;
import com.gjd.model.DatabaseConnection;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.server.Sizeable.Unit;


public class ProductPagedFilterTable extends PagedFilterTable<SQLContainer>
{
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
			setConverter("price", new BigDecimalConverter());
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		setEditable(true);
		setImmediate(true);
		setBuffered(false);
	}
}
