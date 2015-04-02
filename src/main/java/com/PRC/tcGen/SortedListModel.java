package com.PRC.tcGen;

import javax.swing.*;

import java.util.*;

public class SortedListModel extends AbstractListModel<Employee>
{
	private static final long serialVersionUID = -4135323763794129180L;

	TreeSet<Employee> model;

	public SortedListModel()
	{
		model = new TreeSet<Employee>();
	}

	public int getSize()
	{
		return model.size();
	}

	public Employee getElementAt(int index)
	{
		return (Employee)model.toArray()[index];
	}

	public void add(Employee element) {
		if (model.add(element)) {
			fireContentsChanged(this, 0, getSize());
		}
	}

	public TreeSet<Employee> getModel ()
	{
		return model;
	}

	public void addAll(SortedListModel elementModel)
	{
		TreeSet<Employee> elemSet = elementModel.getModel();
		model.addAll(elemSet);
		fireContentsChanged(this, 0, getSize());
	}

	public void clear()
	{
		model.clear();
		fireContentsChanged(this, 0, getSize());
	}

	public boolean contains(Employee element)
	{
		return model.contains(element);
	}

	public Employee firstElement()
	{
		return model.first();
	}

	public Iterator<Employee> iterator()
	{
		return model.iterator();
	}

	public Employee lastElement()
	{
		return model.last();
	}

	public boolean removeElement(Employee element)
	{
		boolean removed = model.remove(element);
		if (removed) {
			fireContentsChanged(this, 0, getSize());
		}
		return removed;
	}
}
