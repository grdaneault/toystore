package com.gjd.UI.ProductControls;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.SQLUtil;
import com.vaadin.data.util.sqlcontainer.query.FreeformStatementDelegate;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;

public class ProductFreeformStatementDelegate implements FreeformStatementDelegate
{

	private static final long serialVersionUID = -3686697130823749358L;

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
			String where = QueryBuilder.getWhereStringForFilters(filters, sh);
			where = where.replace("\"brand_id\"", "`Product`.brand_id");
			where = where.replace("\"type_id\"", "`Product`.type_id");
			System.out.println(where);
			query.append(where);
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
			String where = QueryBuilder.getWhereStringForFilters(filters, sh);
			where = where.replace("\"brand_id\"", "`Product`.brand_id");
			where = where.replace("\"type_id\"", "`Product`.type_id");
			System.out.println(where);
			query.append(where);
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