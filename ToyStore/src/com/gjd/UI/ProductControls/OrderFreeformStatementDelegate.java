package com.gjd.UI.ProductControls;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.gjd.model.DatabaseObjects.Vendor;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.SQLUtil;
import com.vaadin.data.util.sqlcontainer.query.FreeformStatementDelegate;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;

public class OrderFreeformStatementDelegate implements FreeformStatementDelegate
{
	
	private static final long serialVersionUID = 2096254224895263940L;
	private List<Filter> filters;
	private List<OrderBy> orderBys;

	private Vendor vendor;
	public OrderFreeformStatementDelegate(Vendor vendor)
	{
		this.vendor = vendor;
	}

	
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
		StringBuffer query = new StringBuffer("SELECT `Order`.*, Product.product_name, Product.MSRP, Brand.brand_name ");
		query.append("FROM `Order` ");
		query.append("JOIN  Product ON `Order`.SKU = `Product`.SKU ");
		query.append("JOIN Brand ON `Product`.brand_id = `Brand`.brand_id ");

		query.append(getWhere(sh));

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

	private Object getWhere(StatementHelper sh)
	{
		
		if (filters == null)
		{
			if (vendor == null)
			{
				return "";
			}
			else
			{
				return " WHERE `Order`.vendor_id = " + vendor.getId() + " ";
			}
		}
		else
		{
			String where = QueryBuilder.getWhereStringForFilters(filters, sh);
		
			if (where.contains("WHERE"))
			{
				return where + " AND `Order`.vendor_id = " + vendor.getId() + " ";
			}
			return " WHERE `Order`.vendor_id = " + vendor.getId() + " ";
		}
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
		StringBuffer query = new StringBuffer("SELECT COUNT(*) FROM `Order` JOIN  Product ON `Order`.SKU = `Product`.SKU JOIN Brand ON `Product`.brand_id = `Brand`.brand_id ");

		query.append(getWhere(sh));

		System.out.println(query.toString());
		sh.setQueryString(query.toString());
		return sh;
	}

	@Override
	public StatementHelper getContainsRowQueryStatement(Object... keys) throws UnsupportedOperationException
	{
		StatementHelper sh = new StatementHelper();
		StringBuffer query = new StringBuffer("SELECT * FROM `Order` WHERE order_id = ?");
		sh.addParameterValue(keys[0]);
		sh.setQueryString(query.toString());
		System.out.println(query.toString());
		return sh;
	}
}
