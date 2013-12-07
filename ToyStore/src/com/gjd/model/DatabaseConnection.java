package com.gjd.model;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.gjd.model.DatabaseObjects.Address;
import com.gjd.model.DatabaseObjects.Brand;
import com.gjd.model.DatabaseObjects.Customer;
import com.gjd.model.DatabaseObjects.DayHour;
import com.gjd.model.DatabaseObjects.PaymentType;
import com.gjd.model.DatabaseObjects.Product;
import com.gjd.model.DatabaseObjects.ProductType;
import com.gjd.model.DatabaseObjects.Purchase;
import com.gjd.model.DatabaseObjects.PurchaseItem;
import com.gjd.model.DatabaseObjects.Store;
import com.gjd.model.DatabaseObjects.USState;
import com.gjd.model.DatabaseObjects.Vendor;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;

public class DatabaseConnection {
	
	private static DatabaseConnection instance;
	
	public static DatabaseConnection getInstance()
	{
		if (instance == null)
		{
			instance = new DatabaseConnection();
			try {
				instance.loadStates();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return instance;
	}
	
	public boolean commit()
	{
		try
		{
			conn.commit();
			return true;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	private Connection conn;
	private JDBCConnectionPool connPool;
	

	private ResultSet getOneById(String table, String column, int id) throws SQLException
	{
		String query = "SELECT * FROM " + table + " WHERE " + column + " = ? LIMIT 1";
		PreparedStatement pst = conn.prepareStatement(query);
		System.out.println(query);
		pst.setInt(1, id);
		ResultSet rs = pst.executeQuery();
		try
		{
			rs.next();
			return rs;
		}
		catch (SQLException ex)
		{
			return null;
		}
	}

	private ResultSet getById(String table, String column, int id) throws SQLException
	{
		PreparedStatement pst = conn.prepareStatement("SELECT * FROM " + table + " WHERE " + column + " = ?");
		pst.setInt(1, id);
		ResultSet rs = pst.executeQuery();
		return rs;
	}
	
	private DatabaseConnection()
	{
		try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connPool = new SimpleJDBCConnectionPool("com.mysql.jdbc.Driver", "jdbc:mysql://" + DatabaseCredentials.DB_HOST + "/" + DatabaseCredentials.DB_DB, DatabaseCredentials.DB_USER, DatabaseCredentials.DB_PASS);
            conn = connPool.reserveConnection();
            conn.setAutoCommit(true);
        } catch (Exception ex) {
        	System.err.println("Fatal error:  unable to create connection.");
        	ex.printStackTrace(System.err);
        	System.exit(1);
        }
	}
	
	public boolean beginTransaction()
	{
		try
		{
			conn.setAutoCommit(false);
			return true;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public boolean endTransaction()
	{
		try
		{
			conn.commit();
			conn.setAutoCommit(true);
			return true;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public boolean rollback()
	{
		try
		{
			conn.rollback();
			return true;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public JDBCConnectionPool getPool() {
		return connPool;
	}

	public void loadStates() throws SQLException
	{
		PreparedStatement pst = conn.prepareStatement("SELECT * FROM State");
		ResultSet rs = pst.executeQuery();
		while(rs.next())
		{
			USState state = new USState(rs.getInt("state_id"), rs.getString("state_abbr"), rs.getString("state_name"));
			USState.setState(state);
		}
	}
	
	public USState getStateById(int state_id) throws SQLException
	{
		ResultSet rs = getOneById("States", "state_id", state_id);
		if (rs == null) return null;
		return new USState(rs.getInt("state_id"), rs.getString("state_abbr"), rs.getString("state_name"));
	}
	
	public Address getAddressById(int address_id) throws SQLException
	{
		ResultSet rs = getOneById("Address", "address_id", address_id);
		if (rs == null) return null;
		return new Address(rs.getInt("address_id"), rs.getString("line_1"), rs.getString("line_2"), rs.getString("city"), rs.getInt("state_id"), rs.getString("zip"));
	}
	
	/**
	 * Gets the Store object for a given ID.
	 * Populates the Hours the store is open and full address 
	 * 
	 * @param storeId
	 * @return the Store
	 * @throws SQLException
	 */
	public Store getStoreById(int storeId) throws SQLException
	{
		HashMap<Character, DayHour> hours = new HashMap<Character, DayHour>();
		
		
		ResultSet rs = getOneById("Store", "store_id", storeId);
		if (rs == null) return null;
		Store s = new Store(storeId, rs.getString("store_name"), getAddressById(rs.getInt("address_id")), getCustomerById(rs.getInt("customer_id")));

		rs = getById("DayHours", "store_id", storeId);
		
		while(rs.next())
		{
			DayHour h = new DayHour(s, rs.getString("day").charAt(0), rs.getTime("open"), rs.getTime("close"));
			hours.put(h.getDay(), h);
		}
		
		s.setHours(hours);
		
		return s;
	}
	
	public ArrayList<Store> getStoreList()
	{
		ArrayList<Store> stores = new ArrayList<Store>();
		try
		{
			PreparedStatement pst = conn.prepareStatement("SELECT * FROM Store");
			ResultSet rs = pst.executeQuery();
			
			while(rs.next())
			{
				Store s = new Store(rs.getInt("store_id"), rs.getString("store_name"), getAddressById(rs.getInt("address_id")), getCustomerById(rs.getInt("customer_id")));
				stores.add(s);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return stores;
	}
	
	public Customer getCustomerById(int cust_id) throws SQLException
	{
		ResultSet rs = getOneById("Customer", "customer_id", cust_id);
		if (rs == null) return null;
		return new Customer(cust_id, rs.getString("cname_first"), rs.getString("cname_mi"), rs.getString("cname_last"), getAddressById(rs.getInt("address_id")), rs.getString("phone"), rs.getString("email"));
	}
	
	public ProductType getProductTypeById(int prod_type_id) throws SQLException
	{
		ResultSet rs = getOneById("ProductType", "product_type_id", prod_type_id);
		if (rs == null) return null;
		return new ProductType(rs.getInt("product_type_id"), rs.getString("product_type_name"));
	}
	
	public int getCountOf(String table)
	{
		try
		{
			PreparedStatement pst = conn.prepareStatement("SELECT count(*) FROM  " + table);
			ResultSet rs = pst.executeQuery();
			return rs.getInt(1);
		}
		catch (SQLException ex)
		{
			ex.printStackTrace(System.err);
			return -1;
		}
	}
	
	public Product getProductById(int product_id) throws SQLException
	{
		//TODO:  actually load brand, vendor, product type
		ResultSet rs = getOneById("Product", "SKU", product_id);
		if (rs == null) return null;
		return new Product(rs.getInt("SKU"), rs.getString("product_name"), rs.getString("image"), rs.getDouble("weight"), rs.getFloat("MSRP"), rs.getFloat("price"), null, null, null);
	}
	
	
	/**
	 * Saves an address to the database
	 * 
	 * New addresses will be populated with their actual ID from the database
	 * Existing addresses will have all their fields updated.
	 * 
	 * @param a - The address
	 * @return True if successful
	 */
	public boolean saveAddress(Address a)
	{
		try
		{
			if (a.isNew())
			{
				PreparedStatement pst = conn.prepareStatement("INSERT INTO Address (line_1, line_2, city, state_id, zip) VALUES (?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
				pst.setString(1, a.getLine1());
				pst.setString(2, a.getLine2());
				pst.setString(3, a.getCity());
				pst.setInt(4, a.getState().getId());
				pst.setString(5, a.getZip());
				
				pst.executeUpdate();
				ResultSet rs = pst.getGeneratedKeys(); 
				rs.next();
				a.setId(rs.getInt(1));
				return true;
			}
			else
			{
				PreparedStatement pst = conn.prepareStatement("UPDATE Address SET line_1 = ?, line_2 = ?, city = ?, state_id = ?, zip = ? WHERE address_id = ? LIMIT 1;");
				pst.setString(1, a.getLine1());
				pst.setString(2, a.getLine2());
				pst.setString(3, a.getCity());
				pst.setInt(4, a.getState().getId());
				pst.setString(5, a.getZip());
				pst.setInt(6, a.getId());
				
				int rows = pst.executeUpdate();
				System.out.println(rows + " Rows affected.\n" + a.toString());
				return rows == 1;
			}
		} 
		catch (SQLException ex)
		{
			ex.printStackTrace(System.err);
			return false;
		}
		catch (NullPointerException ex)
		{
			throw new RuntimeException("Invalid State Specified", ex);
		}
	}
	
	/**
	 * Saves a store to the database.  Also saves the Address associated with the store.
	 * 
	 * New stores will be populated to with their actual ID.
	 * Existing stores will be updated in the database.
	 * 
	 * @param s - The store
	 * @return True if successful.  False if either store or address could not be updated
	 */
	public boolean saveStore(Store s)
	{
		try
		{
			if (saveAddress(s.getAddress()))
			{
				if (s.isNew())
				{

					PreparedStatement pst = conn.prepareStatement("INSERT INTO Store (store_name, address_id) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
					pst.setString(1, s.getName());
					pst.setInt(2, s.getAddress().getId());
					
					pst.executeUpdate();
					ResultSet rs = pst.getGeneratedKeys(); 
					rs.next();
					s.setId(rs.getInt(1));
					return true;
				}
				else
				{
					PreparedStatement pst = conn.prepareStatement("UPDATE Store SET store_name = ?, address_id = ? WHERE store_id = ? LIMIT 1");
					pst.setString(1, s.getName());
					pst.setInt(2, s.getAddress().getId());
					pst.setInt(3, s.getId());
					
					int rows = pst.executeUpdate();
					return rows == 1;
				}
			}
			else
			{
				return false;
			}
		}
		catch (SQLException ex)
		{
			ex.printStackTrace(System.err);
			return false;
		}
	}

	public boolean saveDayHour(DayHour dh)
	{
		try
		{
			System.out.println(dh);
			System.out.println(dh.isNew());
			PreparedStatement pst;
			if (dh.isNew())
			{
				pst = conn.prepareStatement("INSERT INTO DayHours (store_id, day, open, close) VALUES (?, ?, ?, ?) ");
				pst.setInt(1, dh.getStore().getId());
				pst.setString(2, dh.getDay() + "");
				pst.setTime(3, dh.getOpen());
				pst.setTime(4, dh.getClose());
			}
			else
			{
				pst = conn.prepareStatement("UPDATE DayHours SET open = ?, close = ? WHERE store_id = ? AND day = ?");
				pst.setTime(1, dh.getOpen());
				pst.setTime(2, dh.getClose());
				pst.setInt(3, dh.getStore().getId());
				pst.setString(4, dh.getDay() + "");
			}
			
			return 1 == pst.executeUpdate();
		} 
		catch (SQLException ex)
		{
			ex.printStackTrace(System.err);
			return false;
		}
	}

	public boolean saveBrand(Brand brand)
	{
		try
		{
			if (brand.isNew())
			{
				PreparedStatement pst = conn.prepareStatement("INSERT INTO Brand (brand_name) VALUES (?);", PreparedStatement.RETURN_GENERATED_KEYS);
				pst.setString(1, brand.getName());
				
				pst.executeUpdate();
				ResultSet rs = pst.getGeneratedKeys(); 
				rs.next();
				brand.setId(rs.getInt(1));
				return true;
			}
			else
			{
				PreparedStatement pst = conn.prepareStatement("UPDATE Brand SET brand_name = ? WHERE brand_id = ? LIMIT 1");
				pst.setString(1, brand.getName());
				pst.setInt(2, brand.getId());
				
				return 1 == pst.executeUpdate();
			}
		} 
		catch (SQLException ex)
		{
			ex.printStackTrace(System.err);
			return false;
		}
	}

	public boolean saveProductType(ProductType ptype)
	{
		try
		{
			if (ptype.isNew())
			{
				PreparedStatement pst = conn.prepareStatement("INSERT INTO Brand (brand_name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
				pst.setString(1, ptype.getName());
				
				ResultSet rs = pst.executeQuery();
				ptype.setId(rs.getInt(1));
				return true;
			}
			else
			{
				PreparedStatement pst = conn.prepareStatement("UPDATE Brand SET brand_name = ?WHERE brand_id = ? LIMIT 1");
				pst.setString(1, ptype.getName());
				
				return 1 == pst.executeUpdate();
			}
		} 
		catch (SQLException ex)
		{
			ex.printStackTrace(System.err);
			return false;
		}
	}
	
	public boolean saveCustomer(Customer c)
	{
		try
		{
			if (c.isNew())
			{
				if (c.getPhone() == null)
				{
					throw new RuntimeException("Must specify phone number");
				}
				
				if (c.getEmail() == null)
				{
					throw new RuntimeException("Must specify email address");
				}
				
				if (c.getMi() == null)
				{
					c.setMi("");
				}
				
				if (!c.getAddress().isNew() || (c.getAddress().isNew() && saveAddress(c.getAddress())))
				{
					PreparedStatement pst = conn.prepareStatement("INSERT INTO Customer (cname_first, cname_mi, cname_last, address_id, phone, email) VALUES (?, ?, ?, ?, ?, ?); ", Statement.RETURN_GENERATED_KEYS);
					pst.setString(1, c.getFirst());
					pst.setString(2, c.getMi());
					pst.setString(3, c.getLast());
					pst.setInt(4, c.getAddress().getId());
					pst.setString(5, c.getPhone());
					pst.setString(6, c.getEmail());
					
					pst.executeUpdate();
					
					ResultSet rs = pst.getGeneratedKeys();
					rs.next();
					c.setId(rs.getInt(1));
					
					return true;
				}			
			}
			else
			{
				if (saveAddress(c.getAddress()))
				{
					PreparedStatement pst = conn.prepareStatement("UPDATE Customer SET cname_first = ?, cname_mi = ?, cname_last = ?, address_id = ?, phone = ?, email = ? WHERE customer_id = ? LIMIT 1;");
					pst.setString(1, c.getFirst());
					pst.setString(2, c.getMi());
					pst.setString(3, c.getLast());
					pst.setInt(4, c.getAddress().getId());
					pst.setString(5, c.getPhone());
					pst.setString(6, c.getEmail());
					pst.setInt(7, c.getId());
				
					return 1 == pst.executeUpdate();
				}
			}
		}
		catch (NullPointerException ex)
		{
			ex.printStackTrace(System.err);
		}
		catch (SQLException ex)
		{
			ex.printStackTrace(System.err);
		}
		return false;
	}

	public boolean saveVendor(Vendor vend)
	{
		try
		{
			if (saveAddress(vend.getAddress()))
			{
				if (vend.isNew())
				{

					PreparedStatement pst = conn.prepareStatement("INSERT INTO Vendor (vendor_name, address_id) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
					pst.setString(1, vend.getName());
					pst.setInt(2, vend.getAddress().getId());
					
					ResultSet rs = pst.executeQuery();
					vend.setId(rs.getInt(1));
					return true;
				}
				else
				{
					PreparedStatement pst = conn.prepareStatement("UPDATE Vendor SET vendor_name = ?, address_id = ? WHERE vendor_id = ? LIMIT 1;");
					pst.setString(1, vend.getName());
					pst.setInt(2, vend.getAddress().getId());
					pst.setInt(3, vend.getId());
					int rows = pst.executeUpdate();
					return rows == 1;
				}
			}
			else
			{
				return false;
			}
		} 
		catch (SQLException ex)
		{
			ex.printStackTrace(System.err);
			return false;
		}
	}
	
	public Customer lookupCustomer(String first, String last)
	{
		try
		{
			PreparedStatement pst = conn.prepareStatement("SELECT * FROM Customer WHERE cname_first = ? and cname_last = ?");
			pst.setString(1, first);
			pst.setString(2, last);
			
			ResultSet rs = pst.executeQuery();
			
			Address addr = getAddressById(rs.getInt("address_id"));
			return new Customer(rs.getInt("customer_id"), first, rs.getString("cname_mi"), last, addr, rs.getString("phone"), rs.getString("email"));
		}
		catch (SQLException ex)
		{
			ex.printStackTrace(System.err);
			return null;
		}
	}
	
	public Collection<Product> getTopSellers(int limit)
	{
		Collection<Product> top = new ArrayList<Product>(limit);
		
		try
		{
			PreparedStatement pst = conn.prepareStatement("SELECT SKU, SUM(quantity) as Popularity FROM PurchaseItems GROUP BY SKU ORDER BY Popularity DESC LIMIT ?");
			pst.setInt(1, limit);
			
			ResultSet rs = pst.executeQuery();
			
			while (rs.next())
			{
				Product p = new Product();
				p.setSKU(rs.getInt("SKU"));
				top.add(p);
			}
		}
		catch (SQLException ex)
		{
			ex.printStackTrace(System.err);
		}
		
		return top;
	}
	
	public Collection<PurchaseItem> getTopSellers(int limit, int storeId)
	{
		Collection<PurchaseItem> top = new ArrayList<PurchaseItem>(limit);
		
		try
		{
			PreparedStatement pst = conn.prepareStatement("SELECT PurchaseItems.SKU, product_name, SUM(quantity) as Popularity FROM PurchaseItems JOIN Purchase on PurchaseItems.purchase_id = Purchase.purchase_id JOIN Product ON PurchaseItems.SKU = Product.SKU WHERE store_id = ? GROUP BY SKU ORDER BY Popularity DESC LIMIT ?");
			pst.setInt(1, storeId);
			pst.setInt(2, limit);
			
			ResultSet rs = pst.executeQuery();
			
			while (rs.next())
			{
				Product p = new Product();
				p.setSKU(rs.getInt("SKU"));
				p.setName(rs.getString("product_name"));
				top.add(new PurchaseItem(p, rs.getInt("Popularity"), null));
			}
		}
		catch (SQLException ex)
		{
			ex.printStackTrace(System.err);
		}
		
		return top;
	}
	
	public Collection<PurchaseItem> getTopSellersByState(int limit, int stateId)
	{
		Collection<PurchaseItem> top = new ArrayList<PurchaseItem>(limit);
		
		try
		{
			PreparedStatement pst = conn.prepareStatement("SELECT PurchaseItems.SKU, product_name, SUM(quantity) as Popularity FROM PurchaseItems JOIN Purchase on PurchaseItems.purchase_id = Purchase.purchase_id JOIN Product ON PurchaseItems.SKU = Product.SKU JOIN Store ON Store.store_id = Purchase.store_id JOIN Address ON Store.address_id = Address.address_id WHERE Address.state_id = ? GROUP BY SKU ORDER BY Popularity DESC LIMIT ?");
			pst.setInt(1, stateId);
			pst.setInt(2, limit);
			
			ResultSet rs = pst.executeQuery();
			
			while (rs.next())
			{
				Product p = new Product();
				p.setSKU(rs.getInt("SKU"));
				p.setName(rs.getString("product_name"));
				top.add(new PurchaseItem(p, rs.getInt("Popularity"), null));
			}
		}
		catch (SQLException ex)
		{
			ex.printStackTrace(System.err);
		}
		
		return top;
	}

	public boolean saveProductWeight(int sku, double weight)
	{
		return saveProductField(sku, "Weight", weight);
	}

	public boolean saveProductPrice(int sku, BigDecimal bd)
	{
		return saveProductField(sku, "Price", bd);
	}
	
	public boolean saveProductMSRP(int sku, BigDecimal bd)
	{
		return saveProductField(sku, "MSRP", bd);
	}
	
	public boolean saveProductField(int sku, String column, Object o)
	{
		try
		{
			PreparedStatement pst = conn.prepareStatement("UPDATE Product SET " + column + " = ? WHERE SKU = ? LIMIT 1;");
			pst.setObject(1, o);
			pst.setInt(2, sku);
			
			int rows = pst.executeUpdate();
			System.out.println(rows);
			return rows == 1;
		}
		catch (SQLException ex)
		{
			ex.printStackTrace(System.err);
			return false;
		}
	}

	public boolean saveInventoryField(int sku, int store, String column, Object value)
	{
		try
		{
			PreparedStatement pst = conn.prepareStatement("UPDATE Inventory SET " + column + " = ? WHERE SKU = ? AND store_id = ? LIMIT 1;");
			pst.setObject(1, value);
			pst.setInt(2, sku);
			pst.setInt(3, store);
			
			int rows = pst.executeUpdate();
			return rows == 1;
		}
		catch (SQLException ex)
		{
			ex.printStackTrace(System.err);
			return false;
		}
	}

	public int getProductCount()
	{
		try
		{
			PreparedStatement pst = conn.prepareStatement("SELECT COUNT(*) as count FROM Product");
			ResultSet rs = pst.executeQuery();
			rs.next();
			return rs.getInt("count");
		}
		catch (SQLException ex)
		{
			ex.printStackTrace(System.err);
			return 0;
		}
	}
			
	/**
	 * Used to ensure that when modifying a store's inventory they have entries for every product
	 * 
	 * @param store_id
	 */
	public void ensureStoreInventory(int store_id)
	{
		try
		{
			PreparedStatement pst = conn.prepareStatement(
					"INSERT IGNORE INTO Inventory( SKU, store_id, price, quantity, desired_quantity, reorder_threshold ) " +
					"SELECT SKU, ? AS store_id, price, 0 AS quantity, 0 AS desired_quantity, 0 AS reorder_threshold " +
					" FROM Product");
			pst.setInt(1, store_id);
			
			pst.executeUpdate();
		}
		catch (SQLException ex)
		{
			ex.printStackTrace(System.err);
		}
	}

	public int createOrder(int sku, int store_id)
	{
		try
		{
			StringBuilder query = new StringBuilder("INSERT INTO `Order` (date, filled, quantity, SKU, store_id, vendor_id) ");
			query.append("SELECT * FROM (");
			query.append("SELECT NOW( ) AS date, 0 AS filled, (desired_quantity - quantity - IFNULL( ");
			query.append("(SELECT SUM( quantity ) FROM  `Order` WHERE SKU = Inventory.SKU AND filled = 0 ) ");
			query.append(" , 0)) AS quantity, Inventory.SKU, store_id, vendor_id ");
			query.append("FROM  `Inventory` JOIN Product ON Product.SKU = Inventory.SKU ");
			query.append("WHERE store_id = ? ");
			query.append("AND (desired_quantity - quantity - IFNULL( ");
			query.append("(SELECT SUM( quantity ) FROM  `Order` WHERE SKU = Inventory.SKU AND filled = 0 AND store_id = ?) ");
			query.append(", 0)) > 0 ");
			query.append("AND Inventory.SKU = ? ");
			query.append(") as temp WHERE quantity > 0");
			System.out.println(query);
			PreparedStatement pst = conn.prepareStatement(query.toString());
			
			pst.setInt(1, store_id);
			pst.setInt(2, store_id);
			pst.setInt(3, sku);
			
			return pst.executeUpdate();
		}
		catch (SQLException ex)
		{
			ex.printStackTrace(System.err);
			return -1;
		}
		
	}
	
	public int createAllStoreOrders(int store_id)
	{
		try
		{
			beginTransaction();
			StringBuilder query = new StringBuilder("INSERT INTO `Order` (date, filled, quantity, SKU, store_id, vendor_id) ");
			query.append("SELECT * FROM (");
			query.append("SELECT NOW( ) AS date, 0 AS filled, (desired_quantity - quantity - IFNULL( ");
			query.append("(SELECT SUM( quantity ) FROM  `Order` WHERE SKU = Inventory.SKU AND filled = 0 ) ");
			query.append(" , 0)) AS quantity, Inventory.SKU, store_id, vendor_id ");
			query.append("FROM  `Inventory` JOIN Product ON Product.SKU = Inventory.SKU ");
			query.append("WHERE store_id = ? ");
			query.append("AND (desired_quantity - quantity - IFNULL( ");
			query.append("(SELECT SUM( quantity ) FROM  `Order` WHERE SKU = Inventory.SKU AND filled = 0 AND store_id = ?) ");
			query.append(", 0)) > 0 ");
			query.append(") as temp WHERE quantity > 0");
			System.out.println(query);
			PreparedStatement pst = conn.prepareStatement(query.toString());
			
			System.out.println(store_id);
			pst.setInt(1, store_id);
			pst.setInt(2, store_id);
			
			int n = pst.executeUpdate();
			endTransaction();
			return n;
		}
		catch (SQLException ex)
		{
			ex.printStackTrace(System.err);
			rollback();
			endTransaction();
			return -1;
		}
		
	}

	public boolean checkRecordExists(String table, Object... where)
	{
		try
		{
			StringBuilder query = new StringBuilder("SELECT COUNT(*) FROM ");
			query.append(table);
			query.append(" WHERE ");
			for (int i = 0; i < where.length; i+= 2)
			{
				if (i != 0)
				{
					query.append(" AND ");
				}
				
				query.append(where[i]);
				query.append(" = ? ");
			}
			
			System.out.println(query.toString());
			PreparedStatement pst = conn.prepareStatement(query.toString());
			
			for (int i = 0; i < where.length / 2; i++)
			{
				pst.setObject(i + 1, where[i * 2 + 1]);
			}
			
			ResultSet rs = pst.executeQuery();
			rs.next();
			
			return rs.getInt(1) == 1;
		}
		catch (SQLException ex)
		{
			ex.printStackTrace(System.err);
			return false;
		}
		
	}

	public Vendor getVendorById(int vendorId) throws SQLException
	{
		ResultSet rs = getOneById("Vendor", "vendor_id", vendorId);
		if (rs == null) return null;
		return new Vendor(rs.getInt("vendor_id"), rs.getString("vendor_name"), rs.getInt("address_id"));
	}

	public boolean fillOrder(int orderId)
	{
		try
		{
			PreparedStatement pst = conn.prepareStatement("UPDATE `Order` SET filled = true WHERE order_id = ? LIMIT 1");
			pst.setInt(1, orderId);
			
			int n = pst.executeUpdate();
			System.out.println(n + " rows updated in order fill.");
			return 1 == n;
		}
		catch (SQLException ex)
		{
			ex.printStackTrace(System.err);
			return false;
		}
	}

	public void fillAllOrdersForStore(int storeId)
	{
		try
		{
			PreparedStatement pst = conn.prepareStatement("UPDATE `Order` SET filled = 1 WHERE store_id = ? AND filled = 0");
			//PreparedStatement pst = conn.prepareStatement("SELECT * FROM `Order` WHERE filled = 0 AND store_id = ?");
			pst.setInt(1, storeId);
			//pst.set
			int n = pst.executeUpdate();
			System.out.println(n + " rows updated in order fill for store " + storeId);
		}
		catch (SQLException ex)
		{
			ex.printStackTrace(System.err);
		}
		
	}

	public boolean createOrder(Purchase purchase)
	{
		try
		{
			PreparedStatement pst = conn.prepareStatement("INSERT INTO `Purchase` (store_id, customer_id, payment_type_id, total, date) VALUES (?, ?, ?, ?, NOW() )", Statement.RETURN_GENERATED_KEYS);
			
			pst.setInt(1, purchase.getStore().getId());
			pst.setInt(2, purchase.getCustomer().getId());
			pst.setInt(3,  purchase.getPaymentType().getId());
			pst.setObject(4, purchase.getTotal());
			
			pst.executeUpdate();
			ResultSet rs = pst.getGeneratedKeys(); 
			rs.next();
			purchase.setId(rs.getInt(1));
			return true;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return false;
	}

	/**
	 * Helper method for the physical store UI that gets the price of the product at a given store
	 * 
	 * @param SKU
	 * @param storeId
	 * @return
	 */
	public Product getProductByIdForStore(int SKU, int storeId)
	{
		try
		{
			PreparedStatement pst = conn.prepareStatement("SELECT Product.SKU, image, weight, product_name, MSRP, Inventory.price, Product.type_id, type_name FROM Product JOIN Inventory ON Product.SKU = Inventory.SKU JOIN ProductType ON ProductType.type_id = Product.type_id WHERE Inventory.store_id = ? AND Product.SKU = ? AND Inventory.desired_quantity != 0 LIMIT 1;");
			pst.setInt(1, storeId);
			pst.setInt(2, SKU);
			
			ResultSet rs = pst.executeQuery();
			if (rs.next())
			{
				return new Product(rs.getInt("SKU"), rs.getString("product_name"), rs.getString("image"), rs.getDouble("weight"), rs.getFloat("MSRP"), rs.getFloat("price"), new ProductType(rs.getInt("type_id"), rs.getString("type_name")), null, null);
			}
			
			System.out.println("No matching item found, not stocked? (" + SKU + " @ " + storeId + ")");
			return null;
			
		}
		catch (SQLException ex)
		{
			ex.printStackTrace(System.err);
		}
		return null;
	}

	public int getMaxProductSKU()
	{
		try
		{
			PreparedStatement pst = conn.prepareStatement("SELECT SKU FROM Product ORDER BY SKU DESC LIMIT 1");
			ResultSet rs = pst.executeQuery();
			rs.next();
			
			return rs.getInt(1);
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
			return 0;
		}
	}

	public ArrayList<PaymentType> getPaymentTypes()
	{
		ArrayList<PaymentType> types = new ArrayList<PaymentType>();

		try
		{
			PreparedStatement pst = conn.prepareStatement("SELECT * FROM PaymentType");
			ResultSet rs = pst.executeQuery();

			while (rs.next())
			{
				types.add(new PaymentType(rs.getInt("payment_type_id"), rs.getString("payment_type_name")));
			}
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		
		return types;
	}

	public boolean savePurchaseItem(PurchaseItem pi)
	{
		try
		{
			PreparedStatement pst = conn.prepareStatement("INSERT INTO PurchaseItems (purchase_id, SKU, quantity, price) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity)");
			pst.setInt(1, pi.getPurchase().getId());
			pst.setInt(2, pi.getProduct().getSKU());
			pst.setInt(3, pi.getQuantity());
			pst.setFloat(4, pi.getProduct().getPrice());
			
			if (1 == pst.executeUpdate())
			{
				return  decrementQuantity(pi);
			}
			else
			{
				throw new RuntimeException("Too many rows updated?!");
			}
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean updatePurchaseTotal(Purchase p)
	{
		
		try
		{
			PreparedStatement pst = conn.prepareStatement("UPDATE `Purchase` SET total = (SELECT SUM(price * quantity) FROM `PurchaseItems` WHERE purchase_id = ?) WHERE purchase_id = ?");
			pst.setInt(1, p.getId());
			pst.setInt(2, p.getId());
			
			return (1 == pst.executeUpdate());
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	private boolean decrementQuantity(PurchaseItem pi)
	{
		try
		{
			PreparedStatement pst = conn.prepareStatement("UPDATE Inventory SET quantity = (quantity - " + pi.getQuantity() + ") WHERE SKU = ? AND store_id = ? LIMIT 1 ;");
			pst.setInt(1, pi.getProduct().getSKU());
			pst.setInt(2, pi.getPurchase().getStore().getId());
			
			return 1 == pst.executeUpdate();
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public int getAvailableQuantity(Product product, Store store)
	{
		try
		{
			PreparedStatement pst = conn.prepareStatement("SELECT quantity FROM Inventory WHERE SKU = ? AND store_id = ? LIMIT 1;");
			pst.setInt(1, product.getSKU());
			pst.setInt(2, store.getId());
			
			ResultSet rs = pst.executeQuery();
			rs.next();
			return rs.getInt(1);
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		return 0;
	}
	
	public BigDecimal getSalesForStore(int storeId)
	{
		try
		{
			PreparedStatement pst = conn.prepareStatement("SELECT SUM(quantity * price) as total_sales FROM PurchaseItems JOIN Purchase ON Purchase.purchase_id = PurchaseItems.purchase_id WHERE store_id = ?");
			pst.setInt(1, storeId);
			ResultSet rs = pst.executeQuery();
			rs.next();
			BigDecimal sales = rs.getBigDecimal(1);
			return sales == null ? BigDecimal.ZERO : sales;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return BigDecimal.ZERO;
	}
	
	public PurchaseItem getMostCommonProductForStore(int storeId)
	{
		try
		{
			PreparedStatement pst = conn.prepareStatement("SELECT SUM(quantity) as `count`, SKU FROM `PurchaseItems` JOIN Purchase ON Purchase.purchase_id = PurchaseItems.purchase_id WHERE store_id = ? GROUP BY SKU ORDER BY `count` DESC LIMIT 1");
			pst.setInt(1, storeId);
			ResultSet rs = pst.executeQuery();
			rs.next();
			int SKU = rs.getInt("SKU");
			int quantity = rs.getInt("count");
			
			Product p = getProductByIdForStore(SKU, storeId);
			PurchaseItem pi = new PurchaseItem(p, quantity, null);
			return pi;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}

	public Collection<Purchase> getTopStores(int limit)
	{
		Collection<Purchase> stores = new ArrayList<Purchase>(limit);
		try
		{
			PreparedStatement pst = conn.prepareStatement("SELECT store_id, SUM(total) as total FROM Purchase GROUP BY store_id ORDER BY total DESC LIMIT ?");
			pst.setInt(1,  limit);
			
			ResultSet rs = pst.executeQuery();
			while(rs.next())
			{
				stores.add(new Purchase(0, getStoreById(rs.getInt("store_id")), null, null, rs.getDouble("total"), null));
			}
					
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		
		return stores;
	}
	
	/**
	 * Computes the stores where one brand outperforms another
	 * 
	 * (Note!  Horrid bastardization of the Purchase object! Do not use for real data)
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Collection<Purchase> getStoresWhereBrandXMoreThanY(Brand x, Brand y)
	{
		Collection<Purchase> stores = new ArrayList<Purchase>();
		StringBuilder query = new StringBuilder();
		query.append("SELECT x.store_id, x.brand_x, y.brand_y ");
		query.append("FROM ( ");

		query.append("SELECT SUM( quantity ) brand_x, brand_name, store_id ");
		query.append("FROM PurchaseItems ");
		query.append("JOIN Product ON PurchaseItems.SKU = Product.SKU ");
		query.append("JOIN Brand ON Brand.brand_id = Product.brand_id ");
		query.append("JOIN Purchase ON Purchase.purchase_id = PurchaseItems.purchase_id ");
		query.append("WHERE Product.brand_id = ? ");
		query.append("GROUP BY store_id ");
		query.append(") AS x, ( ");

		query.append("SELECT SUM( quantity ) brand_y, brand_name, store_id ");
		query.append("FROM PurchaseItems ");
		query.append("JOIN Product ON PurchaseItems.SKU = Product.SKU ");
		query.append("JOIN Brand ON Brand.brand_id = Product.brand_id ");
		query.append("JOIN Purchase ON Purchase.purchase_id = PurchaseItems.purchase_id ");
		query.append("WHERE Product.brand_id = ? ");
		query.append("GROUP BY store_id ");
		query.append(") AS y ");
		
		query.append("WHERE x.store_id = y.store_id ");
		query.append("AND x.brand_x > y.brand_y ");
		
		try
		{
			PreparedStatement pst = conn.prepareStatement(query.toString());
			pst.setInt(1,  x.getId());
			pst.setInt(2,  y.getId());
			
			ResultSet rs = pst.executeQuery();
			while(rs.next())
			{
				Purchase p = new Purchase(0, getStoreById(rs.getInt("store_id")), null, null, 0, null);
				PurchaseItem xpi = new PurchaseItem(null, rs.getInt("brand_x"), p);
				PurchaseItem ypi = new PurchaseItem(null, rs.getInt("brand_y"), p);
				p.addPurchaseItem(xpi);
				p.addPurchaseItem(ypi);
				
				stores.add(p);
			}
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		
		return stores;
		
	}
	
	public Collection<ProductType> getCommonAssociatedProductTypes(int SKU)
	{
		ArrayList<ProductType> types = new ArrayList<ProductType>(3);
		StringBuilder query = new StringBuilder();
		query.append("SELECT Product.type_id, ProductType.type_name, SUM( quantity ) freq ");
		query.append("FROM PurchaseItems ");
		query.append("JOIN Product ON PurchaseItems.SKU = Product.SKU ");
		query.append("JOIN ProductType ON Product.type_id = ProductType.type_id ");
		query.append("WHERE purchase_id ");
		query.append("IN ( ");
		query.append(" ");
		query.append("SELECT purchase_id ");
		query.append("FROM PurchaseItems ");
		query.append("WHERE PurchaseItems.SKU = ? ");
		query.append(") ");
		query.append("GROUP BY type_id ");
		query.append("ORDER BY freq DESC  ");
		query.append("LIMIT 3 ");
		
		try
		{
			PreparedStatement pst = conn.prepareStatement(query.toString());
			pst.setInt(1,  SKU);
			
			ResultSet rs = pst.executeQuery();
			
			while(rs.next())
			{
				types.add(new ProductType(rs.getInt("type_id"), rs.getString("type_name")));
			}
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return types;
	}
}
