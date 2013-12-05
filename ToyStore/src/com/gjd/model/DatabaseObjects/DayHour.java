package com.gjd.model.DatabaseObjects;

import java.io.Serializable;
import java.sql.Time;

@SuppressWarnings("deprecation")
public class DayHour implements Serializable
{
	private static final long serialVersionUID = -3212421679974875839L;

	public static final String DAY_STRING = "MTWRFSU";
	
	public static final String CLOSED = "CLOSED";
	public static final String MONDAY = "Monday";
	public static final String TUESDAY = "Tuesday";
	public static final String WEDNESDAY = "Wednesday";
	public static final String THURSDAY = "Thursday";
	public static final String FRIDAY = "Friday";
	public static final String SATURDAY = "Saturday";
	public static final String SUNDAY = "Sunday";
	
	private Store store;
	private char day;
	private Time open;
	private Time close;
	
	private boolean isNew = false;
	
	private String open_string;
	private String close_string;
	
	public Store getStore() {
		return store;
	}
	public char getDay() {
		return day;
	}
	public Time getOpen() {
		return open;
	}
	public Time getClose() {
		return close;
	}
	
	public boolean isClosed()
	{
		return open.getHours() == close.getHours() && open.getMinutes() == close.getMinutes() && open.getSeconds() == close.getSeconds();
	}
	
	public String getDayString()
	{
		switch(day)
		{
		case 'M':
			return MONDAY;
		case 'T':
			return TUESDAY;
		case 'W':
			return WEDNESDAY;
		case 'R':
			return THURSDAY;
		case 'F':
			return FRIDAY;
		case 'S':
			return SATURDAY;
		case 'U':
			return SUNDAY;
		}
		return null;
	}
	
	public String getOpen_string() {
		return isClosed() ? CLOSED : String.format("%02d:%02d", open.getHours(), open.getMinutes());
	}
	public void setOpen_string(String open_string) {
		if (open_string.equals("CLOSED"))
		{
			open = new Time(0, 0, 0);
		}
		else
		{
			String[] split = open_string.split(":");
			open = new Time(Integer.parseInt(split[0]), Integer.parseInt(split[1]), 0);
		}
	}
	public String getClose_string() {
		return isClosed() ? CLOSED :String.format("%02d:%02d", close.getHours(), close.getMinutes());
	}
	public void setClose_string(String close_string)
	{
		if (close_string.equals("CLOSED"))
		{
			close = new Time(0, 0, 0);
		}
		else
		{
			String[] split = close_string.split(":");
			close = new Time(Integer.parseInt(split[0]), Integer.parseInt(split[1]), 0);
		}
	}
	public DayHour(Store store, char day, Time open, Time close) {
		this.store = store;
		this.day = day;
		this.open = open;
		this.close = close;
	}
	public DayHour(Store store, char day, Time open, Time close, boolean isNew) {
		this.store = store;
		this.day = day;
		this.open = open;
		this.close = close;
		this.isNew = isNew;
	}
	public boolean isNew() {
		return isNew;
	}
	
	public String toString()
	{
		return getDayString() + ": " + getOpen_string() + " - " + getClose_string();
	}
}
