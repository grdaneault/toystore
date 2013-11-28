package com.gjd.UI.ProductControls;

import com.vaadin.ui.CustomTable;


/**
 * Abstraction of the save functionality to allow reuse of the popup editor 
 *  
 * @author Greg
 *
 */
public interface PopupEditorSaveHandler
{
	
	/**
	 * Save the data
	 * 
	 * @param source
	 * @param itemId
	 * @param columnId
	 * @return
	 */
	public boolean save(CustomTable source, Object itemId, Object columnId, Object value);

}
