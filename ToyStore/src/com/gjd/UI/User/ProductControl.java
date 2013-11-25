package com.gjd.UI.User;

import java.text.NumberFormat;

import org.tepi.filtertable.FilterTable;

import com.gjd.model.DatabaseObjects.Product;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;


public class ProductControl extends HorizontalLayout
{

	private static final long serialVersionUID = -5331089059096368442L;
	
	public ProductControl(Product p)
	{
		Image image = new Image();
		image.setSource(new ThemeResource("images/products/" + p.getImage()));
		
		Label name = new Label(String.format("<strong>%s</strong>", p.getName()));
		name.setContentMode(ContentMode.HTML);
		name.addStyleName("productname");
		NumberFormat formatter = NumberFormat.getCurrencyInstance();
		
		Label price = new Label(formatter.format(p.getPrice()));
		Label weight = new Label("Weight: " + p.getWeight());
		
		VerticalLayout productInfo = new VerticalLayout(name, price, weight);
		productInfo.setSpacing(true);
		
		addComponent(image);
		addComponent(productInfo);
		
	}
}
