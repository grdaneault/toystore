package com.gjd.UI.Admin;

import java.sql.SQLException;

import com.gjd.model.DatabaseConnection;
import com.gjd.model.DatabaseObjects.Address;
import com.vaadin.data.Property;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.PopupView.Content;
import com.vaadin.ui.PopupView.PopupVisibilityEvent;
import com.vaadin.ui.PopupView.PopupVisibilityListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.VerticalLayout;


public class AddressPopupColumnGenerator implements ColumnGenerator
{
	
	private static final long serialVersionUID = 415599163920920938L;

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
				private static final long serialVersionUID = -7260412276123972775L;
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
					return addressManager.getAddress().toHtmlString();
				}
			});
			
			editor.addPopupVisibilityListener(new PopupVisibilityListener()
			{
				
				private static final long serialVersionUID = -1515559206964402506L;

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
