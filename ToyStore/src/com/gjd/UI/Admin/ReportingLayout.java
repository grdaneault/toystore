package com.gjd.UI.Admin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.gjd.model.DatabaseConnection;
import com.gjd.model.DatabaseObjects.Brand;
import com.gjd.model.DatabaseObjects.Product;
import com.gjd.model.DatabaseObjects.ProductType;
import com.gjd.model.DatabaseObjects.Purchase;
import com.gjd.model.DatabaseObjects.PurchaseItem;
import com.gjd.model.DatabaseObjects.Store;
import com.gjd.model.DatabaseObjects.USState;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ReportingLayout extends VerticalLayout
{

	private static final long serialVersionUID = -7175355427758623564L;

	public ReportingLayout()
	{
		setMargin(true);
		setSpacing(true);

		top20store();
		top20state();
		top5sales();
		brandOutsellsCompetitor();
		commonProductTypeAssociations();
	}

	private void top20store()
	{
		final Button top20store = new Button("Top 20 Products by Store");
		top20store.addClickListener(new ClickListener()
		{

			private static final long serialVersionUID = 653533718745415543L;

			@Override
			public void buttonClick(ClickEvent event)
			{
				Window report = new Window(top20store.getCaption());

				VerticalLayout reportBody = new VerticalLayout();
				
				ComboBox storeSelect = new ComboBox("Store Select");
				ArrayList<Store> stores = DatabaseConnection.getInstance().getStoreList();
				for (Store s : stores)
				{
					storeSelect.addItem(s);
				}

				final Table products = new Table();
				products.setPageLength(20);
				final IndexedContainer productsContainer = new IndexedContainer();
				productsContainer.addContainerProperty("product", String.class, "");
				productsContainer.addContainerProperty("quantity", Integer.class, 0);
				products.setContainerDataSource(productsContainer);
				storeSelect.setImmediate(true);
				storeSelect.setBuffered(false);
				storeSelect.addValueChangeListener(new ValueChangeListener()
				{

					private static final long serialVersionUID = 3253016635566235003L;

					@SuppressWarnings("unchecked")
					@Override
					public void valueChange(ValueChangeEvent event)
					{
						Store store = (Store) event.getProperty().getValue();
						System.out.println("RAI" + productsContainer.removeAllItems());

						Collection<PurchaseItem> productsList = DatabaseConnection.getInstance().getTopSellers(20,
								store.getId());
						for (PurchaseItem pi : productsList)
						{
							productsContainer.addItem(pi);
							productsContainer.getItem(pi).getItemProperty("product")
									.setValue(pi.getProduct().getName());
							productsContainer.getItem(pi).getItemProperty("quantity").setValue(pi.getQuantity());
						}
						
						products.refreshRowCache();
					}

				});
				storeSelect.setNullSelectionAllowed(false);
				storeSelect.setValue(stores.get(0));

				reportBody.addComponent(storeSelect);
				reportBody.addComponent(products);

				reportBody.setSpacing(true);
				reportBody.setMargin(true);
				report.setContent(reportBody);
				report.setWidth(400, Unit.PIXELS);
				report.center();
				report.setModal(true);

				getUI().addWindow(report);
			}
		});

		addComponent(top20store);
	}

	private void top20state()
	{
		final Button top20state = new Button("Top 20 Products by State");
		top20state.addClickListener(new ClickListener()
		{

			private static final long serialVersionUID = 6532833718745415543L;

			@Override
			public void buttonClick(ClickEvent event)
			{
				final Window report = new Window(top20state.getCaption());

				VerticalLayout reportBody = new VerticalLayout();
				NativeSelect stateSelect = new NativeSelect("State Select");
				try
				{
					DatabaseConnection.getInstance().loadStates();
				}
				catch (SQLException ex)
				{
					Notification.show("Error loading state list", "", Type.ERROR_MESSAGE);
				}
				List<USState> states = USState.getAllStates();
				for (USState s : states)
				{
					stateSelect.addItem(s);
				}

				final Table products = new Table();
				products.setPageLength(20);
				final IndexedContainer productsContainer = new IndexedContainer();
				productsContainer.addContainerProperty("product", String.class, "");
				productsContainer.addContainerProperty("quantity", Integer.class, 0);
				products.setContainerDataSource(productsContainer);

				stateSelect.addValueChangeListener(new ValueChangeListener()
				{

					private static final long serialVersionUID = 3253016631566235003L;

					@SuppressWarnings("unchecked")
					@Override
					public void valueChange(ValueChangeEvent event)
					{
						USState state = (USState) event.getProperty().getValue();
						productsContainer.removeAllItems();

						Collection<PurchaseItem> productsList = DatabaseConnection.getInstance().getTopSellersByState(
								20, state.getId());
						for (PurchaseItem pi : productsList)
						{
							productsContainer.addItem(pi);
							productsContainer.getItem(pi).getItemProperty("product")
									.setValue(pi.getProduct().getName());
							productsContainer.getItem(pi).getItemProperty("quantity").setValue(pi.getQuantity());
						}
						
						products.refreshRowCache();
						report.center();
					}

				});
				stateSelect.setNullSelectionAllowed(false);
				stateSelect.setValue(states.get(0));

				reportBody.addComponent(stateSelect);
				reportBody.addComponent(products);
				reportBody.setSpacing(true);
				reportBody.setMargin(true);
				report.setContent(reportBody);
				report.setWidth(400, Unit.PIXELS);
				report.center();
				report.setModal(true);
				getUI().addWindow(report);
			}
		});

		addComponent(top20state);
	}

	private void top5sales()
	{
		final Button top5sales = new Button("Top 5 Stores by Sales");
		top5sales.addClickListener(new ClickListener()
		{

			private static final long serialVersionUID = 6532833718745415543L;

			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event)
			{
				final Window report = new Window(top5sales.getCaption());

				VerticalLayout reportBody = new VerticalLayout();

				Table stores = new Table();
				stores.setPageLength(5);
				final IndexedContainer productsContainer = new IndexedContainer();
				productsContainer.addContainerProperty("store", String.class, "");
				productsContainer.addContainerProperty("sales", Double.class, 0);
				stores.setContainerDataSource(productsContainer);

				Collection<Purchase> purchaseList = DatabaseConnection.getInstance().getTopStores(5);
				for (Purchase p : purchaseList)
				{
					productsContainer.addItem(p);
					productsContainer.getItem(p).getItemProperty("store").setValue(p.getStore().toString());
					productsContainer.getItem(p).getItemProperty("sales").setValue(p.getTotal());
				}

				report.center();

				reportBody.addComponent(stores);
				reportBody.setSpacing(true);
				reportBody.setMargin(true);
				report.setContent(reportBody);
				report.setWidth(500, Unit.PIXELS);
				report.center();
				report.setModal(true);
				getUI().addWindow(report);
			}
		});

		addComponent(top5sales);
	}

	private void brandOutsellsCompetitor()
	{
		final Button brandOutsellsCompetitor = new Button("Brand Outsells Competitor");
		brandOutsellsCompetitor.addClickListener(new ClickListener()
		{

			private static final long serialVersionUID = 6532833718745415543L;

			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event)
			{
				final Window report = new Window(brandOutsellsCompetitor.getCaption());

				VerticalLayout reportBody = new VerticalLayout();

				Brand x = new Brand(1, "Lego");
				Brand y = new Brand(4, "Mega Bloks");
				Table lMB = creatXOutselssYTable(x, y);

				Brand a = new Brand(2, "Valve");
				Brand b = new Brand(3, "EA");
				Table vEA = creatXOutselssYTable(a, b);

				report.center();

				reportBody.addComponent(lMB);
				reportBody.addComponent(vEA);
				reportBody.setSpacing(true);
				reportBody.setMargin(true);
				report.setContent(reportBody);
				report.setWidth(450, Unit.PIXELS);
				report.center();
				report.setModal(true);
				getUI().addWindow(report);
			}
		});

		addComponent(brandOutsellsCompetitor);
	}

	@SuppressWarnings("unchecked")
	private Table creatXOutselssYTable(Brand x, Brand y)
	{
		Table stores = new Table(x.getName() + " VS. " + y.getName());
		stores.setPageLength(10);
		final IndexedContainer productsContainer = new IndexedContainer();
		productsContainer.addContainerProperty("store", String.class, "");
		productsContainer.addContainerProperty(x.getName(), Integer.class, 0);
		productsContainer.addContainerProperty(y.getName(), Integer.class, 0);
		stores.setContainerDataSource(productsContainer);

		Collection<Purchase> productsList = DatabaseConnection.getInstance().getStoresWhereBrandXMoreThanY(x, y);
		for (Purchase p : productsList)
		{
			productsContainer.addItem(p);
			productsContainer.getItem(p).getItemProperty("store").setValue(p.getStore().toString());
			productsContainer.getItem(p).getItemProperty(x.getName()).setValue(p.getItems().get(0).getQuantity());
			productsContainer.getItem(p).getItemProperty(y.getName()).setValue(p.getItems().get(1).getQuantity());
		}
		return stores;
	}

	private void commonProductTypeAssociations()
	{
		final Button brandOutsellsCompetitor = new Button("Commonly Associateed Product Types");
		brandOutsellsCompetitor.addClickListener(new ClickListener()
		{

			private static final long serialVersionUID = 6532833718745415543L;

			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event)
			{
				final Window report = new Window(brandOutsellsCompetitor.getCaption());

				VerticalLayout reportBody = new VerticalLayout();

				final TextField SKU = new TextField("Enter SKU");
				SKU.setImmediate(true);
				SKU.setBuffered(false);
				final Label item = new Label();
				
				
				SKU.addValidator(new Validator()
				{

					private static final long serialVersionUID = 4269301908520606593L;

					@Override
					public void validate(Object value) throws InvalidValueException
					{
						try
						{
							System.out.println(value);
							int id = Integer.valueOf(value.toString());
							Product p = DatabaseConnection.getInstance().getProductById(id);
							if (p == null)
							{
								throw new InvalidValueException("Unknown SKU");
							}
							else
							{
								item.setValue(p.getName());
							}
						}
						catch (NumberFormatException ex)
						{
							throw new InvalidValueException("Unable to parse SKU as Integer");
						}
						catch (SQLException e)
						{
							throw new InvalidValueException("Unable to check SKU");
						}
					}
				});
				
				final Table productTypes = new Table();
				productTypes.setPageLength(20);
				final IndexedContainer typeContainer = new IndexedContainer();
				typeContainer.addContainerProperty("product type", String.class, "");
				productTypes.setContainerDataSource(typeContainer);
				
				SKU.addValueChangeListener(new ValueChangeListener()
				{
					
					private static final long serialVersionUID = -6511836017507810205L;

					@Override
					public void valueChange(ValueChangeEvent event)
					{
						if (SKU.isValid())
						{
							productTypes.setVisible(true);
							int id = Integer.valueOf(SKU.getValue());
							Collection<ProductType> types = DatabaseConnection.getInstance().getCommonAssociatedProductTypes(id);
							
							typeContainer.removeAllItems();
							for (ProductType t : types)
							{
								System.out.println(t);
								typeContainer.addItem(t);
								typeContainer.getItem(t).getItemProperty("product type").setValue(t.getName());
							}
						}
					}
				});
				
				productTypes.setVisible(false);

				reportBody.addComponent(SKU);
				reportBody.addComponent(item);
				reportBody.addComponent(productTypes);
				reportBody.setSpacing(true);
				reportBody.setMargin(true);
				report.setContent(reportBody);
				report.setWidth(450, Unit.PIXELS);
				report.center();
				report.setModal(true);
				getUI().addWindow(report);
			}
		});

		addComponent(brandOutsellsCompetitor);
	}
}
