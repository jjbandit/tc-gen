package com.PRC.tcGen;

import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

public class EmployeeList extends JPanel implements ActionListener
{
	private DefaultListModel<String> employeeListModel;
	private JList<String> employeeJList;

	private Button addEmployee;
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
		this.add(employeeListPane);

		// //Create Model and JList
		// employeeIDModel = new DefaultListModel<Integer>();
		// employeeIDJlist = new JList<Integer>(employeeIDModel);

		// employeeIDJlist.setVisibleRowCount(5);
		// employeeIDJlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// JScrollPane employeeIDListPane = new JScrollPane(employeeIDJlist);
		// this.add(employeeIDListPane);

	}


	public void actionPerformed(ActionEvent e)
	{
	
		if(e.getSource() == addEmployee)
		{
			employeeListModel.addElement("A shiny new Employee");  // TODO ADD STUFF TO THE LIST
		}
	}
}
