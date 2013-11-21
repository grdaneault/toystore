package com.gjd.UI.Admin;

import com.gjd.model.DatabaseObjects.Address;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class AddressManager extends VerticalLayout {
	
	private Address address;
	private AddressControl addressControl;
	
	public AddressManager(Address address)
	{
		this.address = address;
		
		Label header = new Label("<h2>Address</h2>");
		header.setContentMode(ContentMode.HTML);
		addComponent(header);
		setSpacing(true);
		setMargin(true);
		
		addressControl = new AddressControl(this.address);
		addComponent(addressControl);
		
		
		HorizontalLayout buttons = new HorizontalLayout();
		
		buttons.setMargin(true);
		buttons.setSpacing(true);
		Button save = new Button("Save");
		save.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				addressControl.save();
			}
		});
		
		Button reset = new Button("Reset");
		reset.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				addressControl.reset();
			}
		});
		buttons.addComponent(save);
		buttons.addComponent(reset);
		
		addComponent(buttons);
		
	}
	

}
