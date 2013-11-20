package com.gjd.UI.Admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.gjd.model.DatabaseObjects.DayHour;
import com.gjd.model.DatabaseObjects.Store;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Table;

public class StoreHoursControl extends Table {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5336597613370172508L;
	private Store store;
	
	@SuppressWarnings("unchecked")
	public StoreHoursControl(Store store)
	{
		super("Hours");
		this.store = store;
	
		Container storeHourContainer = new IndexedContainer();
		storeHourContainer.addContainerProperty("day", String.class, "");
		storeHourContainer.addContainerProperty("open", NativeSelect.class, null);
		storeHourContainer.addContainerProperty("close", NativeSelect.class, null);
		
		Map<Character, DayHour> sh = this.store.getHours();
		
		for (int i = 0; i < DayHour.DAY_STRING.length(); i++)
		{
			final DayHour dh = sh.get(DayHour.DAY_STRING.charAt(i));
			
			storeHourContainer.addItem(dh);
			BeanItemContainer<String> timeBeans = new BeanItemContainer<String>(String.class, times);
			
			final NativeSelect openSelect = new NativeSelect("open");
			openSelect.setContainerDataSource(timeBeans);
			openSelect.setImmediate(true);
			openSelect.setBuffered(false);
			openSelect.setValue(dh.isClosed() ? "CLOSED" : dh.getOpen_string());
			
			final NativeSelect closeSelect = new NativeSelect("close");
			closeSelect.setContainerDataSource(timeBeans);
			closeSelect.setImmediate(true);
			closeSelect.setBuffered(false);
			closeSelect.setValue(dh.isClosed() ? "CLOSED" : dh.getClose_string());
			
			closeSelect.addValueChangeListener(new ValueChangeListener() {
				
				private static final long serialVersionUID = 6070480830705550015L;

				@Override
				public void valueChange(Property.ValueChangeEvent event)
				{
					if (event.getProperty().getValue().equals("CLOSED"))
					{
						if (!openSelect.getValue().equals("CLOSED"))
						{
							openSelect.setValue("CLOSED");
						}
						
						dh.setClose_string("00:00");
					}
					else
					{
						dh.setClose_string((String)event.getProperty().getValue());
					}
				}
			});
			openSelect.addValueChangeListener(new ValueChangeListener() {
				
				private static final long serialVersionUID = -2439862384225230639L;

				@Override
				public void valueChange(Property.ValueChangeEvent event)
				{
					if (event.getProperty().getValue().equals("CLOSED"))
					{
						if (!closeSelect.getValue().equals("CLOSED"))
						{
							closeSelect.setValue("CLOSED");
						}
						
						dh.setOpen_string("00:00");
					}
					else
					{
						dh.setOpen_string((String)event.getProperty().getValue());
					}
				}
			});
			
			storeHourContainer.getItem(dh).getItemProperty("day").setValue(dh.getDayString());
			storeHourContainer.getItem(dh).getItemProperty("open").setValue(openSelect);
			storeHourContainer.getItem(dh).getItemProperty("close").setValue(closeSelect);
		}

		setContainerDataSource(storeHourContainer);
		setSelectable(false);
		setEditable(false);
		setBuffered(false);
		setImmediate(true);
		setPageLength(7);
	}
	
	private static final List<String> times;
	
	static
	{
		times = new ArrayList<String>();
		final String closed = "CLOSED";
		times.add(closed);
		for (int h = 0; h < 24; h++)
		{
			for (int m = 0; m < 60; m += 15)
			{
				times.add(String.format("%02d:%02d", h, m));
			}
		}
	}
}
