package com.PRC.tcGen;

import javax.swing.*;

import java.util.*;

public class SortedListModel extends AbstractListModel<Employee> {
	private static final long serialVersionUID = -4135323763794129180L;

	// Define a SortedSet
  TreeSet<Employee> model;

  public SortedListModel() {
    // Create a TreeSet
    // Store it in SortedSet variable
    model = new TreeSet<Employee>();
  }

  // ListModel methods
  public int getSize() {
    // Return the model size
    return model.size();
  }

	public Employee getElementAt(int index) {
    // Return the appropriate element
    return  (Employee)model.toArray()[index];
  }

  // Other methods
  public void add(Employee element) {
    if (model.add(element)) {
      fireContentsChanged(this, 0, getSize());
    }
  }

  public void addAll(Employee elements[]) {
    Collection<Employee> c = Arrays.asList(elements);
    model.addAll(c);
    fireContentsChanged(this, 0, getSize());
  }

  public void clear() {
    model.clear();
    fireContentsChanged(this, 0, getSize());
  }

  public boolean contains(Employee element) {
    return model.contains(element);
  }

  public Employee firstElement() {
    // Return the appropriate element
    return model.first();
  }

  public Iterator<Employee> iterator() {
    return model.iterator();
  }

  public Employee lastElement() {
    // Return the appropriate element
    return model.last();
  }

  public boolean removeElement(Employee element) {
    boolean removed = model.remove(element);
    if (removed) {
      fireContentsChanged(this, 0, getSize());
    }
    return removed;
  }
}
