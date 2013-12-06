package com.gjd.UI.User;

import com.gjd.model.DatabaseConnection;
import com.gjd.model.DatabaseObjects.Product;
import com.gjd.model.DatabaseObjects.Purchase;
import com.gjd.model.DatabaseObjects.PurchaseItem;
import com.gjd.model.DatabaseObjects.Store;
import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;


public class PurchaseTable extends Table
{
	private static final long serialVersionUID = 553281855208153969L;
	private Purchase purchase;
	private BeanItemContainer<PurchaseItem> purchaseItemContainer;

	public PurchaseTable()
	{
		super();
		
		purchaseItemContainer = new BeanItemContainer<PurchaseItem>(PurchaseItem.class);
		setContainerDataSource(purchaseItemContainer);
		addGeneratedColumn("price", new ColumnGenerator()
		{
			
			private static final long serialVersionUID = 5902005864001448539L;

			@SuppressWarnings("unchecked")
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId)
			{
				return ((BeanItem<PurchaseItem>)source.getItem(itemId)).getBean().getProduct().getPrice();
			}
		});

		addGeneratedColumn("total price", new ColumnGenerator()
		{
			
			private static final long serialVersionUID = 6086336109042776838L;

			@SuppressWarnings("unchecked")
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId)
			{
				return ((BeanItem<PurchaseItem>)source.getItem(itemId)).getBean().getProduct().getPrice() * (Integer)source.getItem(itemId).getItemProperty("quantity").getValue();
			}
		});

		addGeneratedColumn("remove", new ColumnGenerator()
		{
			
			private static final long serialVersionUID = -5429247824394781387L;

			@Override
			public Object generateCell(final Table source, final Object itemId, Object columnId)
			{
				Button remove = new Button("Remove");
				remove.addClickListener(new ClickListener()
				{
					
					private static final long serialVersionUID = 2376220701383780588L;

					@Override
					public void buttonClick(ClickEvent event)
					{
						source.removeItem(itemId);
					}
				});
				return remove;
			}
		});
		
		addGeneratedColumn("update quantity", new ColumnGenerator()
		{

			private static final long serialVersionUID = 250735805775736760L;

			@Override
			public Object generateCell(final Table source, final Object itemId, Object columnId)
			{
				final TextField quantity = new TextField();
				quantity.setValue(source.getItem(itemId).getItemProperty("quantity").getValue().toString());
				quantity.addValidator(new Validator()
				{
					
					private static final long serialVersionUID = -2287527479541676825L;

					@Override
					public void validate(Object value) throws InvalidValueException
					{
						try
						{
							int qty = Integer.valueOf(value.toString());
							Store s = new Store();
							Product p = new Product();
							if (qty <= 0)
							{
								throw new InvalidValueException("Quantity must be greater than 0");
							}
							else if (qty > DatabaseConnection.getInstance().getAvailableQuantity(p, s));
							{
								throw new InvalidValueException("Desired quantity exceeds available quantity");
							}
						}
						catch(NumberFormatException ex)
						{
							throw new InvalidValueException("Unable to parse quantity as integer");
						}
					}
				});
				
				quantity.addValueChangeListener(new ValueChangeListener()
				{
					
					private static final long serialVersionUID = 1L;

					@SuppressWarnings("unchecked")
					@Override
					public void valueChange(com.vaadin.data.Property.ValueChangeEvent event)
					{
						source.getItem(itemId).getItemProperty("quantity").setValue(Integer.valueOf(quantity.getValue()));
					}
				});
				
				return quantity;
			}
		});
		
		setVisibleColumns("product", "price", "quantity", "total price", "remove");
		setFooterVisible(true);
		setColumnFooter("product", "Total");
		setColumnFooter("quantity", "0");
		setColumnFooter("total price", "0");
	}
	
	public PurchaseTable(Purchase p)
	{
		this();
		this.purchase = p;
		
		for (PurchaseItem pi : purchase.getItems())
		{
			purchaseItemContainer.addBean(pi);
		}
	}
}
