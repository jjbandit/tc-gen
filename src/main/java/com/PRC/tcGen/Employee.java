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
	@Override
	public String toString()
	{
		return employeeName + " - " + employeeID;
	}
}
