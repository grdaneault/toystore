package com.gjd.model.DatabaseObjects;

import java.sql.Time;

public class DayHour {
	private Store store;
	private char day;
	private Time open;
	private Time close;
	
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
	public DayHour(Store store, char day, Time open, Time close) {
		this.store = store;
		this.day = day;
		this.open = open;
		this.close = close;
	}
}
