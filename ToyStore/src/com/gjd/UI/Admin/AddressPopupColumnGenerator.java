package com.gjd.UI.Admin;

import java.sql.SQLException;

import com.gjd.model.DatabaseConnection;
import com.gjd.model.DatabaseObjects.Address;
import com.vaadin.data.Property;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.PopupView.PopupVisibilityListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PopupView.Content;
import com.vaadin.ui.PopupView.PopupVisibilityEvent;
import com.vaadin.ui.Table.ColumnGenerator;


public class AddressPopupColumnGenerator implements ColumnGenerator
{
	
	private SQLContainer dataContainer;

	public AddressPopupColumnGenerator(SQLContainer dataContainer)
	{
		this.dataContainer = dataContainer;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object generateCell(final Table source, final Object itemId, final Object columnId)
	{
		System.out.println(source.getItem(itemId));
		System.out.println(source.getItem(itemId).getItemProperty("address_id"));
		Property<Integer> addressProp = (Property<Integer>) source.getItem(itemId).getItemProperty("address_id");
		
		final int address_id = (addressProp.getValue() == null ? -1 : addressProp.getValue());

		try
		{
			final Address addr = address_id == -1 ? new Address() : DatabaseConnection.getInstance().getAddressById(address_id);
			
			PopupView editor = new PopupView(new Content()
			{
				private AddressManager addressManager = new AddressManager(addr);
				public Component getPopupComponent()
				{
					VerticalLayout layout = new VerticalLayout();
					layout.setSpacing(true);
					layout.setMargin(true);
					layout.setSizeUndefined();
					layout.addComponent(new AddressManager(addr));
					
					return layout;
				}
				
				
				
				@Override
				public String getMinimizedValueAsHTML()
				{
					// TODO Auto-generated method stub
					return addressManager.getAddress().toHtmlString();
				}
			});
			
			editor.addPopupVisibilityListener(new PopupVisibilityListener()
			{
				
				@Override
				public void popupVisibilityChange(PopupVisibilityEvent event)
				{
					System.out.println("Updating address ID to "  +addr.getId());
					source.getItem(itemId).getItemProperty(columnId).setValue(addr.getId());
				}
			});

			editor.setSizeUndefined();
			return editor;
		}
		catch (SQLException ex)
		{
			Notification.show("Error", "Could not load address", Type.ERROR_MESSAGE);
			ex.printStackTrace();
			return new Label("Error");
		}
	}

}
