package com.gjd.UI.Admin;

import com.gjd.model.DatabaseObjects.Store;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

public class StoreHoursControl extends VerticalLayout {
	
	private static final long serialVersionUID = -8210211534615886381L;
	private Store store;
	private StoreHoursTable hours;
	
	public StoreHoursControl(Store store)
	{
		this.store = store;
		
		Label hoursHeader = new Label("<h2>Hours</h2>");
		hoursHeader.setContentMode(ContentMode.HTML);
		
		hours = new StoreHoursTable(this.store);
		addComponent(hoursHeader);
		addComponent(hours);
		setSpacing(true);
		setMargin(true);
		
		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setMargin(true);
		buttons.setSpacing(true);
		Button save = new Button("Save");
		save.addClickListener(new ClickListener() {
			
			private static final long serialVersionUID = 4541796747480718962L;

			@Override
			public void buttonClick(ClickEvent event) {
				hours.save();
			}
		});
		
		Button reset = new Button("Reset");
		reset.addClickListener(new ClickListener() {
			
			private static final long serialVersionUID = -5783527501063803721L;

			@Override
			public void buttonClick(ClickEvent event) {
				hours.reset();
			}
		});
		buttons.addComponent(save);
		buttons.addComponent(reset);
		
		addComponent(buttons);
		
		setSpacing(true);
	}

	public void setStore(Store s)
	{
		store = s;
		hours.setStore(s);
	}

}
