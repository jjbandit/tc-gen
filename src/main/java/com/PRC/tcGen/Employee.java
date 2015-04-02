package com.PRC.tcGen;

public class Employee implements Comparable<Employee>
{
	private String employeeName;
	private int employeeID;
	private int templateIndex;

	public Employee(String name, Integer ID)
	{
		this.employeeName = name;
		this.employeeID = ID;
	}

	public String getFullName ()
	{
		return this.employeeName;
	}

	public String getLastName ()
	{
		return this.getFullName().substring(this.getFullName().lastIndexOf(" ") + 1);
	}

	public String getFirstName ()
	{
		String name = this.getFullName();
		if (name.indexOf(" ") > -1) // If the name is more than one word
		{
			return name.substring(0, name.indexOf(" ")); // Return first word
		} else {
			return name; // Otherwise return the 1 word name
		}
	}

	public String getInvertedName ()
	{
		if (getLastName().equals(getFirstName()))
		{
			return getLastName();
		} else {
			return getLastName() + ", "  + getFirstName();
		}
	}

	public int getID ()
	{
		return this.employeeID;
	}

	public int getTemplateIndex ()
	{
		return templateIndex;
	}

	public void setTemplateIndex (int index)
	{
		templateIndex = index;
	}

	@Override
	public String toString()
	{
		return this.getInvertedName() + " - " + employeeID;
	}

	public int compareTo(Employee compObj)
	{
		int compResult = this.getLastName().compareToIgnoreCase(compObj.getLastName());

		if (compResult == 0) {
			return 1;
		} else {
			return compResult;
		}
	}
}
