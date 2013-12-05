package com.gjd.UI;

import com.gjd.widgetset.client.widgethelper.WidgetHelperClientRpc;
import com.gjd.widgetset.client.widgethelper.WidgetHelperServerRpc;
import com.vaadin.shared.MouseEventDetails;
import com.gjd.widgetset.client.widgethelper.WidgetHelperState;

public class WidgetHelper extends com.vaadin.ui.AbstractComponent {

	private static final long serialVersionUID = -1892771208721419223L;
	private WidgetHelperServerRpc rpc = new WidgetHelperServerRpc() {
		private static final long serialVersionUID = -4190187352221469325L;
		private int clickCount = 0;

		public void clicked(MouseEventDetails mouseDetails) {
			// nag every 5:th click using RPC
			if (++clickCount % 5 == 0) {
				getRpcProxy(WidgetHelperClientRpc.class).alert(
						"Ok, that's enough!");
			}
			// update shared state
			getState().text = "You have clicked " + clickCount + " times";
		}
	};  

	public WidgetHelper() {
		registerRpc(rpc);
	}

	@Override
	public WidgetHelperState getState() {
		return (WidgetHelperState) super.getState();
	}
}
