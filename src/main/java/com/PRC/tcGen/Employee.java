package com.PRC.tcGen;
public class Employee
{
	private String employeeName;
	private int employeeID;

	public Employee(String name, Integer ID)
	{
		this.employeeName = name;
		this.employeeID = ID;
	}

	public String getName ()
	{
		return this.employeeName;
	}
	public int getID ()
	{
		return this.employeeID;
	}
	@Override
	public String toString()
	{
		return employeeName + " - " + employeeID;
	}
}
