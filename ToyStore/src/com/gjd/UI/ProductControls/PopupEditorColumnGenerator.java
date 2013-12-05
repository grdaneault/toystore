package com.gjd.UI.ProductControls;

import com.gjd.model.DatabaseConnection;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomTable.ColumnGenerator;
import com.vaadin.ui.PopupView.Content;

public class PopupEditorColumnGenerator implements ColumnGenerator
{
	PopupEditorSaveHandler saveHandler;
	
	public PopupEditorColumnGenerator(PopupEditorSaveHandler saveHandler)
	{
		this.saveHandler = saveHandler;
	}
	

	private static final long serialVersionUID = 6055189039283724770L;

	@Override
	public Object generateCell(final CustomTable source, final Object itemId, final Object columnId)
	{
		final String start = source.getItem(itemId).getItemProperty(columnId).getValue().toString();
		final int sku = (Integer)source.getItem(itemId).getItemProperty("SKU").getValue();
		PopupView editor = new PopupView(new Content()
		{
			private static final long serialVersionUID = 8497887992724226734L;
			private TextField value = new TextField("Value", start);
			
			public Component getPopupComponent()
			{
				VerticalLayout layout = new VerticalLayout();
				layout.setSpacing(true);
				layout.setMargin(true);
				layout.setSizeUndefined();
				layout.addComponent(value);
				value.setWidth(250, Unit.PIXELS);
				Button save = new Button("Save");
				save.addClickListener(new ClickListener()
				{
					private static final long serialVersionUID = -2828426393197066513L;

					@Override
					public void buttonClick(ClickEvent event)
					{
						if (saveHandler.save(source, itemId, columnId, value.getValue()))
						{
							Notification.show("Success", columnId + " saved.", Type.HUMANIZED_MESSAGE);
						}
						else
						{
							Notification.show("Error", columnId + " could not be saved.", Type.ERROR_MESSAGE);
						}
					}
				});
				
				Button reset = new Button("Reset");
				reset.addClickListener( new ClickListener()
				{
					
					private static final long serialVersionUID = 8928136573905541974L;

					@Override
					public void buttonClick(ClickEvent event)
					{
						value.setValue(start);
					}
				});
				HorizontalLayout buttons = new HorizontalLayout(save, reset);
				buttons.setSpacing(true);
				buttons.setMargin(new MarginInfo(true, false, false, false));
				layout.addComponent(buttons);
				
				return layout;
			}
			
			
			
			@Override
			public String getMinimizedValueAsHTML()
			{
				// TODO Auto-generated method stub
				return value.getValue();
			}
		});
		editor.setSizeUndefined();
		return editor;
		
		
	}
	
	public static class ProductSaveHandler implements PopupEditorSaveHandler
	{
		
		@Override
		public boolean save(CustomTable source, Object itemId, Object columnId, Object value)
		{
			int sku = (Integer) source.getItem(itemId).getItemProperty("SKU").getValue();
			return DatabaseConnection.getInstance().saveProductField(sku, columnId.toString(), value);
		}
		
	}
	
	public static class InventorySaveHandler implements PopupEditorSaveHandler
	{
		
		@Override
		public boolean save(CustomTable source, Object itemId, Object columnId, Object value)
		{
			int sku = (Integer) source.getItem(itemId).getItemProperty("SKU").getValue();
			int store = (Integer) source.getItem(itemId).getItemProperty("store_id").getValue();
			return DatabaseConnection.getInstance().saveInventoryField(sku, store, columnId.toString(), value);
		}
		
	}
	
}