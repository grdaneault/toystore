package com.gjd.model;

import java.sql.*;
import java.util.HashMap;

import com.gjd.model.DatabaseObjects.Address;
import com.gjd.model.DatabaseObjects.Brand;
import com.gjd.model.DatabaseObjects.Customer;
import com.gjd.model.DatabaseObjects.DayHour;
import com.gjd.model.DatabaseObjects.ProductType;
import com.gjd.model.DatabaseObjects.Store;
import com.gjd.model.DatabaseObjects.USState;
import com.gjd.model.DatabaseObjects.Vendor;

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
	
	private Connection conn;

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
            conn = DriverManager.getConnection("jdbc:mysql://" + DatabaseCredentials.DB_HOST + "/" + DatabaseCredentials.DB_DB, DatabaseCredentials.DB_USER, DatabaseCredentials.DB_PASS);
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
				
				ResultSet rs = pst.executeQuery();
				a.setId(rs.getInt(1));
				return true;
			}
			else
			{
				PreparedStatement pst = conn.prepareStatement("UPDATE Address SET line_1 = ?, line_2 = ?, city = ?, state_id = ?, zip = ? LIMIT 1;");
				pst.setString(1, a.getLine1());
				pst.setString(2, a.getLine2());
				pst.setString(3, a.getCity());
				pst.setInt(4, a.getState().getId());
				pst.setString(5, a.getZip());
				
				int rows = pst.executeUpdate();
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
					
					ResultSet rs = pst.executeQuery();
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
				PreparedStatement pst = conn.prepareStatement("INSERT INTO Brand (brand_name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
				pst.setString(1, brand.getName());
				
				ResultSet rs = pst.executeQuery();
				brand.setId(rs.getInt(1));
				return true;
			}
			else
			{
				PreparedStatement pst = conn.prepareStatement("UPDATE Brand SET brand_name = ?WHERE brand_id = ? LIMIT 1");
				pst.setString(1, brand.getName());
				
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
}
