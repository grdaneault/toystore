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
import com.gjd.model.DatabaseObjects.Product;
import com.gjd.model.DatabaseObjects.ProductType;
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
	 * @param store_id
	 * @return the Store
	 * @throws SQLException
	 */
	public Store getStoreById(int store_id) throws SQLException
	{
		HashMap<Character, DayHour> hours = new HashMap<Character, DayHour>();
		
		
		ResultSet rs = getOneById("Store", "store_id", store_id);
		if (rs == null) return null;
		Store s = new Store(store_id, rs.getString("store_name"), getAddressById(rs.getInt("address_id")));

		rs = getById("DayHours", "store_id", store_id);
		
		while(rs.next())
		{
			DayHour h = new DayHour(s, rs.getString("day").charAt(0), rs.getTime("open"), rs.getTime("close"));
			hours.put(h.getDay(), h);
		}
		
		s.setHours(hours);
		
		return s;
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
		ResultSet rs = getOneById("Product", "product_id", product_id);
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
			PreparedStatement pst = conn.prepareStatement("SELECT SKU, COUNT(*) as Popularity FROM PurchaseItems GROUP BY SKU ORDER BY Popularity DESC LIMIT ?");
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

	public JDBCConnectionPool getPool() {
		return connPool;
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
}
