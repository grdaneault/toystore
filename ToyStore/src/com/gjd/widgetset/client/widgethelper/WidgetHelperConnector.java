package com.gjd.widgetset.client.widgethelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;

import com.gjd.UI.WidgetHelper;
import com.gjd.widgetset.client.widgethelper.WidgetHelperWidget;
import com.gjd.widgetset.client.widgethelper.WidgetHelperServerRpc;
import com.vaadin.client.communication.RpcProxy;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.client.MouseEventDetailsBuilder;
import com.gjd.widgetset.client.widgethelper.WidgetHelperClientRpc;
import com.gjd.widgetset.client.widgethelper.WidgetHelperState;
import com.vaadin.client.communication.StateChangeEvent;

@Connect(WidgetHelper.class)
public class WidgetHelperConnector extends AbstractComponentConnector {

	private static final long serialVersionUID = 3779142944216419317L;
	WidgetHelperServerRpc rpc = RpcProxy
			.create(WidgetHelperServerRpc.class, this);
	
	public WidgetHelperConnector() {
		registerRpc(WidgetHelperClientRpc.class, new WidgetHelperClientRpc() {
			private static final long serialVersionUID = -5585089037108212738L;

			public void alert(String message) {
				// TODO Do something useful
				Window.alert(message);
			}
		});

		// TODO ServerRpc usage example, do something useful instead
		getWidget().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				final MouseEventDetails mouseDetails = MouseEventDetailsBuilder
					.buildMouseEventDetails(event.getNativeEvent(),
								getWidget().getElement());
				rpc.clicked(mouseDetails);
			}
		});

	}

	@Override
	protected Widget createWidget() {
		return GWT.create(WidgetHelperWidget.class);
	}

	@Override
	public WidgetHelperWidget getWidget() {
		return (WidgetHelperWidget) super.getWidget();
	}

	@Override
	public WidgetHelperState getState() {
		return (WidgetHelperState) super.getState();
	}

	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		super.onStateChanged(stateChangeEvent);

		// TODO do something useful
		final String text = getState().text;
		getWidget().setText(text);
	}

}

