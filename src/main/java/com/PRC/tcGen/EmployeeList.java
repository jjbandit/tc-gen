package com.PRC.tcGen;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

public class EmployeeList extends JPanel implements ActionListener
{
	private static final long serialVersionUID = -6932061067756922392L;

	private DefaultListModel<String> employeeListModel;
	private JList<String> employeeJList;

	private JButton addEmployee;
	private JTextField newEmployeeName, newEmployeeID;
	// private DefaultListModel<Integer> employeeIDModel;
	// private JList<Integer> employeeIDJlist;

	public EmployeeList () {

		// GridLayout layout = new GridLayout(0,2);
		// this.setLayout(layout);

		//Create Model and JList
		employeeListModel = new DefaultListModel<String>();
		employeeJList = new JList<String>(employeeListModel);

		employeeJList.setVisibleRowCount(5);
		employeeJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane employeeListPane = new JScrollPane(employeeJList);

		// //Create Model and JList
		// employeeIDModel = new DefaultListModel<Integer>();
		// employeeIDJlist = new JList<Integer>(employeeIDModel);

		// employeeIDJlist.setVisibleRowCount(5);
		// employeeIDJlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// JScrollPane employeeIDListPane = new JScrollPane(employeeIDJlist);

		newEmployeeName = new JTextField("", 10);
		newEmployeeID = new JTextField("", 10);
		addEmployee = new JButton("+");
		addEmployee.addActionListener(this);

		// this.add(employeeIDListPane);
		this.add(employeeListPane);
		this.add(newEmployeeName);
		this.add(newEmployeeID);
		this.add(addEmployee);
	}


	public void actionPerformed(ActionEvent e)
	{
	
		if(e.getSource() == addEmployee)
		{
			String emName = newEmployeeName.getText();
			String emID = newEmployeeID.getText();
			employeeListModel.addElement(emName + " - " + emID);
		}
	}
}
