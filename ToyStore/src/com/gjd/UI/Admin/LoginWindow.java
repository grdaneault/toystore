package com.gjd.UI.Admin;

import java.util.ArrayList;

import com.gjd.UI.User.SuccessfulLoginListener;
import com.gjd.model.DatabaseConnection;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;


public class LoginWindow extends Window implements ClickListener
{
	private static final long serialVersionUID = 7717119289324090368L;

	
	private String nameField;
	private String idField;
	private String table;
	private String title;

	private TextField name;
	private PasswordField pass;
	private Label info;
	
	private ArrayList<SuccessfulLoginListener> loginListeners;


	private Label head;


	private Button login;

	
	public LoginWindow(String title, String table, String nameField, String idField)
	{
		super("Login");
		
		this.nameField = nameField;
		this.idField = idField;
		this.table = table;
		this.title = title;
		
		createLayout();
		setModal(true);
		setWidth(300, Unit.PIXELS);
		setDraggable(false);
		center();
		
	}



	private void createLayout()
	{
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);
		
		head = new Label("<h2>" + title + " Login</h2>");
		head.setContentMode(ContentMode.HTML);
		layout.addComponent(head);
		
		info = new Label();
		layout.addComponent(info);

		name = new TextField(title + " Name");
		name.setRequired(true);
		layout.addComponent(name);

		pass = new PasswordField(title + " ID");
		pass.setRequired(true);
		layout.addComponent(pass);
		
		login = new Button("Login");
		login.addClickListener(this);
		layout.addComponent(login);
		
		setContent(layout);
		
	}

	public void addLoginListener(SuccessfulLoginListener l)
	{
		if (loginListeners == null)
		{
			loginListeners = new ArrayList<SuccessfulLoginListener>();
		}
		this.loginListeners.add(l);
	}
	
	public void removeLoginListener(SuccessfulLoginListener l)
	{
		if (loginListeners != null)
		{
			loginListeners.remove(l);
		}
	}
	


	@Override
	public void buttonClick(ClickEvent event)
	{
		boolean good = DatabaseConnection.getInstance().checkRecordExists(table, nameField, name.getValue(), idField, pass.getValue());

		if (good)
		{
			if (loginListeners != null)
			{
				for (SuccessfulLoginListener l : loginListeners)
				{
					l.successfulLogin(this);
				}
			}
			info.setValue("");
		}
		else
		{
			info.setValue("Unable to find name/id in database");
			info.addStyleName("errorLabel");
		}
		
	}

	
	public String getUser()
	{
		return name.getValue();
	}

	public String getPass()
	{
		return pass.getValue();
	}
}
