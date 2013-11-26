package com.gjd.widgetset.client.widgethelper;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.communication.ServerRpc;

public interface WidgetHelperServerRpc extends ServerRpc {

	// TODO example API
	public void clicked(MouseEventDetails mouseDetails);

}
