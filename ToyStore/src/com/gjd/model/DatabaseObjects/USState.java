package com.gjd.model.DatabaseObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;


public class USState implements Serializable {
	
	private static final long serialVersionUID = 1414480606086462696L;
	
	private static TreeMap<Integer, USState> states = new TreeMap<Integer, USState>();
	
	public USState(int id, String abbreviation, String name)
	{
		this.id = id;
		this.abbreviation = abbreviation;
		this.name = name;
	}
	
	public static USState getState(int id)
	{
		return states.get(id);
	}
	
	public static void setState(USState state)
	{
		states.put(state.id, state);
	}
	
	public int getId() {
		return id;
	}
	public String getAbbreviation() {
		return abbreviation;
	}
	public String getName() {
		return name;
	}
	
	private int id;
	private String abbreviation;
	private String name;

	public static List<USState> getAllStates() {
		List<USState> stateList = new ArrayList<USState>(50);
		for (int id : states.descendingKeySet())
		{
			stateList.add(0, states.get(id));
		}
		return stateList;
	}
	
	public String toString()
	{
		return name;
	}
}
