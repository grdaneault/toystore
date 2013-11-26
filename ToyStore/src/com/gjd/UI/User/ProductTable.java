package com.gjd.UI.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import com.gjd.model.DatabaseConnection;
import com.gjd.model.DatabaseObjects.Brand;
import com.gjd.model.DatabaseObjects.Product;
import com.gjd.model.DatabaseObjects.ProductType;
import com.gjd.model.DatabaseObjects.Vendor;
import com.jensjansson.pagedtable.PagedTable;
import com.vaadin.data.Item;
import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.SQLUtil;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.data.util.sqlcontainer.query.FreeformStatementDelegate;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Image;
import com.vaadin.ui.Table;

public class ProductTable extends PagedTable
{

	private static final long serialVersionUID = -434160667448238351L;
	private ProductFreeformStatementDelegate delegate;
	private SQLContainer sc;
	
	public ProductTable()
	{
		super();
		setWidth("500px");
		// SELECT * FROM `Product` JOIN `Vendor` ON `Product`.vendor_id =
		// `Vendor`.vendor_id JOIN `Brand` ON `Product`.brand_id =
		// `Brand`.brand_id JOIN `ProductType` ON `Product`.type_id =
		// `ProductType`.type_id WHERE 1
		FreeformQuery productQuery = new FreeformQuery("SELECT Product.* , Vendor.vendor_name as vendor_name, Brand.brand_name as brand_name, ProductType.type_name as type_name FROM  `Product` JOIN  `Vendor` ON  `Product`.vendor_id =  `Vendor`.vendor_id JOIN  `Brand` ON  `Product`.brand_id =  `Brand`.brand_id JOIN  `ProductType` ON  `Product`.type_id =  `ProductType`.type_id", DatabaseConnection.getInstance()
				.getPool(), "SKU");
		
		delegate = new ProductFreeformStatementDelegate();
		productQuery.setDelegate(delegate);

		try
		{
			sc = new SQLContainer(productQuery);
			setContainerDataSource(sc);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		addGeneratedColumn("image", new ColumnGenerator()
		{
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId)
			{
				return new Image((String) getItem(itemId).getItemProperty("product_name").getValue(), new ThemeResource("images/products/" + (String) getItem(itemId).getItemProperty(columnId).getValue()));
			}
		});
		addGeneratedColumn("product", new ColumnGenerator()
		{
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId)
			{
				Item i = getItem(itemId);
				
				// Store properties
				int SKU = (Integer) i.getItemProperty("SKU").getValue();
				String name = (String) i.getItemProperty("product_name").getValue(); 
				String image = (String) i.getItemProperty("image").getValue(); 
				double weight = (Double) i.getItemProperty("weight").getValue(); 
				float MSRP = Float.parseFloat(i.getItemProperty("MSRP").getValue().toString()); 
				float price = Float.parseFloat(i.getItemProperty("price").getValue().toString()); 
				
				// Vendor properties
				int vend_id = (Integer) i.getItemProperty("vendor_id").getValue();
				String vend_name = (String) i.getItemProperty("vendor_name").getValue(); 
				Vendor v = new Vendor(vend_id, vend_name, null);

				// Brand properties
				int brand_id = (Integer) i.getItemProperty("brand_id").getValue();
				String brand_name = (String) i.getItemProperty("brand_name").getValue(); 
				Brand b = new Brand(brand_id, brand_name);
				
				// Product Type properties
				int pytpe_id = (Integer) i.getItemProperty("vendor_id").getValue();
				String ptype_name = (String) i.getItemProperty("vendor_name").getValue(); 
				ProductType ptype = new ProductType(pytpe_id, ptype_name);
				
				
				
				Product p = new Product(SKU, name, image, weight, MSRP, price, ptype, b, v);
				return new ProductControl(p);
			}
		});
		
		setVisibleColumns("product");
		setColumnCollapsingAllowed(true);
		setColumnCollapsed("SKU", true);
		
		setPageLength(10);
	}

	public class ProductFreeformStatementDelegate implements FreeformStatementDelegate
	{

		private List<Filter> filters;
		private List<OrderBy> orderBys;

		@Override
		public String getQueryString(int offset, int limit) throws UnsupportedOperationException
		{
			throw new UnsupportedOperationException("Use getQueryStatement method.");
		}

		@Override
		public String getCountQuery() throws UnsupportedOperationException
		{
			throw new UnsupportedOperationException("Use getCountStatement method.");
		}

		@Override
		public void setFilters(List<Filter> filters) throws UnsupportedOperationException
		{
			this.filters = filters;
		}

		@Override
		public void setOrderBy(List<OrderBy> orderBys) throws UnsupportedOperationException
		{
			this.orderBys = orderBys;
		}

		@Override
		public int storeRow(Connection conn, RowItem row) throws UnsupportedOperationException, SQLException
		{
			throw new UnsupportedOperationException("Not supporting adding");
		}

		@Override
		public boolean removeRow(Connection conn, RowItem row) throws UnsupportedOperationException, SQLException
		{
			throw new UnsupportedOperationException("Not supporting removing");
		}

		@Override
		public String getContainsRowQueryString(Object... keys) throws UnsupportedOperationException
		{
			throw new UnsupportedOperationException("Please use getContainsRowQueryStatement method.");
		}

		@Override
		public StatementHelper getQueryStatement(int offset, int limit) throws UnsupportedOperationException
		{
			StatementHelper sh = new StatementHelper();
			StringBuffer query = new StringBuffer(
					"SELECT Product.* , Vendor.vendor_name as vendor_name, Brand.brand_name as brand_name, ProductType.type_name as type_name ");
			query.append("FROM  `Product` ");
			query.append("JOIN  `Vendor` ON  `Product`.vendor_id =  `Vendor`.vendor_id ");
			query.append("JOIN  `Brand` ON  `Product`.brand_id =  `Brand`.brand_id ");
			query.append("JOIN  `ProductType` ON  `Product`.type_id =  `ProductType`.type_id ");

			if (filters != null)
			{
				query.append(QueryBuilder.getWhereStringForFilters(filters, sh));
			}

			query.append(getOrderByString());
			if (offset != 0 || limit != 0)
			{
				query.append(" LIMIT ").append(limit);
				query.append(" OFFSET ").append(offset);
			}

			System.out.println(query.toString());
			sh.setQueryString(query.toString());
			return sh;
		}

		private String getOrderByString()
		{
			StringBuffer orderBuffer = new StringBuffer("");
			if (orderBys != null && !orderBys.isEmpty())
			{
				orderBuffer.append(" ORDER BY ");
				OrderBy lastOrderBy = orderBys.get(orderBys.size() - 1);
				for (OrderBy orderBy : orderBys)
				{
					orderBuffer.append(SQLUtil.escapeSQL(orderBy.getColumn()));
					if (orderBy.isAscending())
					{
						orderBuffer.append(" ASC");
					}
					else
					{
						orderBuffer.append(" DESC");
					}
					if (orderBy != lastOrderBy)
					{
						orderBuffer.append(", ");
					}
				}
			}
			return orderBuffer.toString();
		}

		@Override
		public StatementHelper getCountStatement() throws UnsupportedOperationException
		{
			StatementHelper sh = new StatementHelper();
			StringBuffer query = new StringBuffer("SELECT COUNT(*) FROM Product JOIN  `Vendor` ON  `Product`.vendor_id =  `Vendor`.vendor_id JOIN  `Brand` ON  `Product`.brand_id =  `Brand`.brand_id JOIN  `ProductType` ON  `Product`.type_id =  `ProductType`.type_id ");
			if (filters != null)
			{
				query.append(QueryBuilder.getWhereStringForFilters(filters, sh));
			}
			sh.setQueryString(query.toString());
			return sh;
		}

		@Override
		public StatementHelper getContainsRowQueryStatement(Object... keys) throws UnsupportedOperationException
		{
			StatementHelper sh = new StatementHelper();
	        StringBuffer query = new StringBuffer("SELECT * FROM Product WHERE SKU = ?");
	        sh.addParameterValue(keys[0]);
	        sh.setQueryString(query.toString());
	        return sh;
		}

	}
	
	private class PopularityFilter implements Filter
	{
		private Collection<Product> topSellers;
		
		public PopularityFilter(Collection<Product> topSellers)
		{
			this.topSellers = topSellers;
		}
		

		@Override
		public boolean passesFilter(Object itemId, Item item) throws UnsupportedOperationException
		{
			String sku = item.getItemProperty("SKU").getValue().toString();
			
			for (Product p : topSellers)
			{
				if (sku.equals("" + p.getSKU()))
				{
					return true;
				}
			}
			
			return false;
		}

		@Override
		public boolean appliesToProperty(Object propertyId)
		{
			return propertyId.equals("SKU");
		}
	}

	public void sortByPopularity(int n)
	{
		Collection<Product> topSellers = DatabaseConnection.getInstance().getTopSellers(n);
		sc.addContainerFilter(new PopularityFilter(topSellers));
	}

}
