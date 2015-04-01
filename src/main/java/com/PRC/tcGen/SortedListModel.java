package com.PRC.tcGen;

import javax.swing.*;
import java.util.*;

public class SortedListModel<E> extends AbstractListModel<Object> {
	private static final long serialVersionUID = -4135323763794129180L;

	// Define a SortedSet
  SortedSet<String> model;

  public SortedListModel() {
    // Create a TreeSet
    // Store it in SortedSet variable
    model = new TreeSet<String>();
  }

  // ListModel methods
  public int getSize() {
    // Return the model size
    return model.size();
  }

  public Object getElementAt(int index) {
    // Return the appropriate element
    return model.toArray()[index];
  }

  // Other methods
  public void add(String element) {
    if (model.add(element)) {
      fireContentsChanged(this, 0, getSize());
    }
  }

  public void addAll(String elements[]) {
    Collection<String> c = Arrays.asList(elements);
    model.addAll(c);
    fireContentsChanged(this, 0, getSize());
  }

  public void clear() {
    model.clear();
    fireContentsChanged(this, 0, getSize());
  }

  public boolean contains(String element) {
    return model.contains(element);
  }

  public String firstElement() {
    // Return the appropriate element
    return model.first();
  }

  public Iterator<String> iterator() {
    return model.iterator();
  }

  public String lastElement() {
    // Return the appropriate element
    return model.last();
  }

  public boolean removeElement(String element) {
    boolean removed = model.remove(element);
    if (removed) {
      fireContentsChanged(this, 0, getSize());
    }
    return removed;
  }
}
