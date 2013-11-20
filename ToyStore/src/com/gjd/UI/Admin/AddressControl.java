package com.gjd.UI.Admin;

import java.sql.SQLException;
import java.util.List;

import org.apache.tomcat.dbcp.dbcp.DbcpException;

import com.gjd.model.DatabaseConnection;
import com.gjd.model.DatabaseObjects.Address;
import com.gjd.model.DatabaseObjects.USState;
import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;

public class AddressControl extends FormLayout {

	private static final long serialVersionUID = 1L;

	private Address address;

	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 */
	public AddressControl(Address address)
	{
		super();
		this.address = address;
		buildMainLayout();
	}

	private void buildMainLayout()
	{
		setSizeUndefined();
		BeanItem<Address> item = new BeanItem<Address>(address);
		BeanItemContainer<USState> stateBeans = new BeanItemContainer<USState>(USState.class);
		stateBeans.addAll(USState.getAllStates());
		
		TextField line1 = new TextField("Line 1", item.getItemProperty("line1"));
		line1.addValidator(new BeanValidator(Address.class, "line1"));
		line1.setImmediate(true);
		line1.setWidth("250px");
		line1.setValue(address.getLine1());
		addComponent(line1);
		line1.setRequired(true);
		
		TextField line2 = new TextField("Line 2", item.getItemProperty("line2"));
		line2.addValidator(new BeanValidator(Address.class, "line2"));
		line2.setWidth("250px");
		addComponent(line2);
		
		TextField city = new TextField("City", item.getItemProperty("city"));
		city.addValidator(new BeanValidator(Address.class, "city"));
		city.setWidth("250px");
		addComponent(city);
		city.setRequired(true);
		
		
		NativeSelect state = new NativeSelect("State", stateBeans);
		state.setPropertyDataSource(item.getItemProperty("state"));
		state.setWidth("250px");
		try
		{
			DatabaseConnection.getInstance().loadStates();
			List<USState> stateList = USState.getAllStates();
			for (USState s : stateList)
			{
				state.addItem(s);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			state.addItem("Error loading states.");
			state.setEnabled(false);
		}
		
		addComponent(state);
		state.setRequired(true);
		state.setNullSelectionAllowed(false);
		
		TextField zip = new TextField("Zip Code", item.getItemProperty("zip"));
		zip.addValidator(new BeanValidator(Address.class, "zip"));
		System.out.println(zip.isBuffered());
		zip.setWidth("250px");
		addComponent(zip);
		zip.setRequired(true);

	}
}