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

		TextField mi = new TextField("Middle Initial");
		first.setRequired(true);
		first.setPropertyDataSource(item.getItemProperty("mi"));
		first.addValidator(new BeanValidator(Customer.class, "mi"));

		TextField last = new TextField("Last Name");
		first.setRequired(true);
		first.setPropertyDataSource(item.getItemProperty("last"));
		first.addValidator(new BeanValidator(Customer.class, "last"));

		TextField phone = new TextField("Phone Number");
		first.setRequired(true);
		first.setPropertyDataSource(item.getItemProperty("phone"));
		first.addValidator(new BeanValidator(Customer.class, "phone"));

		TextField email = new TextField("Email Address");
		first.setRequired(true);
		first.setPropertyDataSource(item.getItemProperty("email"));
		first.addValidator(new BeanValidator(Customer.class, "email"));
		
		addComponent(first);
		addComponent(mi);
		addComponent(last);
		addComponent(phone);
		addComponent(email);
		
	}
}
