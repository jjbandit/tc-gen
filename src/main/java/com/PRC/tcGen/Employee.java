package com.PRC.tcGen;
public class Employee
{
	private String employeeName;
	private String employeeID;

	public Employee(String name, String ID)
	{
		this.employeeName = name;
		this.employeeID = ID;
	}

	public String getName ()
	{
		return this.employeeName;
	}
	public String getID ()
	{
		return this.employeeID;
	}
	@Override
	public String toString()
	{
		return employeeName + " - " + employeeID;
	}
}
