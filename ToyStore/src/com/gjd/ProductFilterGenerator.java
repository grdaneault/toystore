package com.gjd;

import java.lang.reflect.Method;
import java.math.BigDecimal;

import org.tepi.filtertable.FilterGenerator;
import org.tepi.filtertable.numberfilter.NumberFilterPopup;
import org.tepi.filtertable.numberfilter.NumberInterval;
import org.tepi.filtertable.paged.PagedFilterTable;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Field;

public class ProductFilterGenerator implements FilterGenerator
{

	private final PagedFilterTable<SQLContainer> table;

	public ProductFilterGenerator(PagedFilterTable<SQLContainer> table)
	{
		this.table = table;
	}

	@Override
	public AbstractField<?> getCustomFilterComponent(Object propertyId)
	{
		if (table.getContainerDataSource().getType(propertyId) == BigDecimal.class)
		{
			NumberFilterPopup nfp = new NumberFilterPopup(table.getFilterDecorator());
			return nfp;
		}
		return null;
	}

	@Override
	public Filter generateFilter(Object propertyId, Field<?> originatingField)
	{
		if (table.getContainerDataSource().getType(propertyId) == BigDecimal.class)
		{
			try
			{
				NumberInterval interval = ((NumberFilterPopup) originatingField).getValue();
				if (interval == null)
				{
					/* Number interval is empty -> no filter */
					return null;
				}
				if (table.getFilterGenerator() != null)
				{
					Filter newFilter = table.getFilterGenerator().generateFilter(propertyId, interval);
					if (newFilter != null)
					{
						return newFilter;
					}
				}
				String ltValue = interval.getLessThanValue();
				String gtValue = interval.getGreaterThanValue();
				String eqValue = interval.getEqualsValue();
				Class<?> clazz = BigDecimal.class;

				Method valueOf;

				// We use reflection to get the vaueOf method of the
				// container
				// datatype
				if (eqValue != null)
				{
					return new Compare.Equal(propertyId, BigDecimal.valueOf(Long.valueOf(eqValue)));
				}
				else if (ltValue != null && gtValue != null)
				{
					return new And(new Compare.Less(propertyId, BigDecimal.valueOf(Long.valueOf(ltValue))),
							new Compare.Greater(propertyId, BigDecimal.valueOf(Long.valueOf(gtValue))));
				}
				else if (ltValue != null)
				{
					return new Compare.Less(propertyId, BigDecimal.valueOf(Long.valueOf(ltValue)));
				}
				else if (gtValue != null)
				{
					return new Compare.Greater(propertyId, BigDecimal.valueOf(Long.valueOf(gtValue)));
				}
				return null;
			}
			catch (Exception ex)
			{
				return null;
			}
		}
		return null;

	}

	@Override
	public Filter generateFilter(Object propertyId, Object value)
	{
		if (propertyId.equals("price"))
		{
			
		}
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void filterRemoved(Object propertyId)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public Filter filterGeneratorFailed(Exception reason, Object propertyId, Object value)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void filterAdded(Object propertyId, Class<? extends Filter> filterType, Object value)
	{
		// TODO Auto-generated method stub
		
	}
}