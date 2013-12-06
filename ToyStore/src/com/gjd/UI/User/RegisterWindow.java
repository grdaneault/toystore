package com.gjd.UI.User;

import com.gjd.UI.Admin.AddressControl;
import com.gjd.model.DatabaseConnection;
import com.gjd.model.DatabaseObjects.Customer;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;



public class RegisterWindow extends Window
{
	private static final long serialVersionUID = -7328285865695459372L;

	public RegisterWindow()
	{
		super("Register New Customer");
		
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);

		Label header = new Label("<h2>Enter User Information</h2>");
		header.setContentMode(ContentMode.HTML);
		layout.addComponent(header);

		Label basicHeader = new Label("<h3>Basic Info</h3>");
		basicHeader.setContentMode(ContentMode.HTML);
		layout.addComponent(basicHeader);
		
		final Customer c = new Customer();
		UserInfoControl uic = new UserInfoControl(c);
		layout.addComponent(uic);
		
		Label addressHeader = new Label("<h3>Address</h3>");
		addressHeader.setContentMode(ContentMode.HTML);
		layout.addComponent(addressHeader);
		
		AddressControl ac = new AddressControl(c.getAddress());
		layout.addComponent(ac);
		
		Button saveCustomer = new Button("Save Customer");
		saveCustomer.addClickListener(new ClickListener()
		{
			
			private static final long serialVersionUID = -6577946249099541492L;

			@Override
			public void buttonClick(ClickEvent event)
			{
				System.out.println(c);
				try
				{
					if (DatabaseConnection.getInstance().saveCustomer(c))
					{
						Notification.show("Welcome " + c.getFirst(), "Customer #" + c.getId() + " Added Successfully", Type.HUMANIZED_MESSAGE);
						close();
					}
					else
					{
						Notification.show("Unable to register customer", "", Type.ERROR_MESSAGE);
					}
				}
				catch (RuntimeException re)
				{
					Notification.show("Unable to register customer", re.getMessage(), Type.ERROR_MESSAGE);
				}
			}
		});
		
		layout.addComponent(saveCustomer);

		setModal(true);
		center();
		setContent(layout);
	}
}
