package com.gjd.UI.ProductControls;

import java.math.BigDecimal;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

public class BigDecimalConverter implements Converter<String, BigDecimal>
{

	private static final long serialVersionUID = 2200345867255171806L;

	@Override
	public BigDecimal convertToModel(String value, Class<? extends BigDecimal> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException
	{
		try
		{
			return BigDecimal.valueOf(Long.valueOf(value));
		}
		catch (NumberFormatException ex)
		{
			throw new ConversionException("Could not parse BigDecimal");
		}

	}

	@Override
	public String convertToPresentation(BigDecimal value, Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException
	{
		return value.toPlainString();
	}

	@Override
	public Class<BigDecimal> getModelType()
	{
		return BigDecimal.class;
	}

	@Override
	public Class<String> getPresentationType()
	{
		return String.class;
	}

}
