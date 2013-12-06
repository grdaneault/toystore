package com.gjd.UI.ProductControls;

import java.text.NumberFormat;

import com.gjd.model.DatabaseObjects.Product;
import com.gjd.model.DatabaseObjects.Purchase;
import com.gjd.model.DatabaseObjects.PurchaseItem;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;


public class ProductControl extends HorizontalLayout
{

	private static final long serialVersionUID = -5331089059096368442L;
	
	public ProductControl(Product p)
	{
		this(p, null);
		
	}

	public ProductControl(final Product p, final Purchase purchase)
	{
		setSpacing(true);
		setMargin(true);
		
		Image image = new Image();
		image.setSource(new ThemeResource("images/products/" + p.getImage()));
		
		Label name = new Label(String.format("<strong>%s</strong>", p.getName()));
		name.setContentMode(ContentMode.HTML);
		name.addStyleName("productname");
		NumberFormat formatter = NumberFormat.getCurrencyInstance();
		
		Label price = new Label(formatter.format(p.getPrice()));
		Label weight = new Label("Weight: " + p.getWeight());
		
		VerticalLayout productInfo = new VerticalLayout(name, price, weight);

		if (purchase != null)
		{
			Button add = new Button("Add to Cart");
			add.addClickListener(new ClickListener()
			{
				
				private static final long serialVersionUID = 2779809715695003704L;
				
				@Override
				public void buttonClick(ClickEvent event)
				{
					purchase.addPurchaseItem(new PurchaseItem(p, 1, purchase));
				}
			});
			productInfo.addComponent(add);
		}
		
		productInfo.setSpacing(true);
		
		addComponent(image);
		addComponent(productInfo);
	}
}
