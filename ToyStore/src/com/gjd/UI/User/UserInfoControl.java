package com.gjd.UI.User;

import com.gjd.model.DatabaseObjects.Customer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;


public class UserInfoControl extends FormLayout
{
	private static final long serialVersionUID = -5870494361690180207L;

	private Customer customer;
	
	public UserInfoControl(Customer cust)
	{
		this.customer = cust;
		
		setSpacing(true);
		setMargin(true);
		
		setSizeUndefined();
		BeanItem<Customer> item = new BeanItem<Customer>(customer);
		
		TextField first = new TextField("First Name");
		first.setRequired(true);
		first.setPropertyDataSource(item.getItemProperty("first"));
		first.addValidator(new BeanValidator(Customer.class, "first"));
		first.setNullRepresentation("");
		first.setWidth(250, Unit.PIXELS);
		first.setImmediate(true);
		first.setBuffered(false);

		TextField mi = new TextField("Middle Initial");
		mi.setRequired(false);
		mi.setPropertyDataSource(item.getItemProperty("mi"));
		mi.addValidator(new BeanValidator(Customer.class, "mi"));
		mi.setNullRepresentation("");
		mi.setWidth(40, Unit.PIXELS);
		mi.setImmediate(true);
		mi.setBuffered(false);
		
		TextField last = new TextField("Last Name");
		last.setRequired(true);
		last.setPropertyDataSource(item.getItemProperty("last"));
		last.addValidator(new BeanValidator(Customer.class, "last"));
		last.setNullRepresentation("");
		last.setWidth(250, Unit.PIXELS);
		
		TextField phone = new TextField("Phone Number");
		phone.setRequired(true);
		phone.setPropertyDataSource(item.getItemProperty("phone"));
		phone.addValidator(new BeanValidator(Customer.class, "phone"));
		phone.setNullRepresentation("");
		phone.setWidth(100, Unit.PIXELS);
		
		TextField email = new TextField("Email Address");
		email.setRequired(true);
		email.setPropertyDataSource(item.getItemProperty("email"));
		email.addValidator(new BeanValidator(Customer.class, "email"));
		email.setNullRepresentation("");
		email.setWidth(250, Unit.PIXELS);
		
		addComponent(first);
		addComponent(mi);
		addComponent(last);
		addComponent(phone);
		addComponent(email);
		
	}
}
