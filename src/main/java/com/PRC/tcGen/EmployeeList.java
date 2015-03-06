package com.PRC.tcGen;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
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

	private DefaultListModel<Employee> employeeListModel;
	private JList<Employee> employeeJList;


	private JButton addEmployeeButton;
	private JTextField newEmployeeName, newEmployeeID;

	public EmployeeList () {

		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(layout);

		//Create Model and JList
		employeeListModel = new DefaultListModel<Employee>();
		employeeJList = new JList<Employee>(employeeListModel);

		employeeJList.setVisibleRowCount(5);
		employeeJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane employeeListPane = new JScrollPane(employeeJList);

		newEmployeeName = new JTextField("", 10);
		newEmployeeName.setMaximumSize(new Dimension(Integer.MAX_VALUE,
		newEmployeeName.getPreferredSize().height));

		newEmployeeID = new JTextField("", 10);
		newEmployeeID.setMaximumSize(new Dimension(Integer.MAX_VALUE,
		newEmployeeID.getPreferredSize().height));

		addEmployeeButton = new JButton("+");
		addEmployeeButton.addActionListener(this);

		this.add(employeeListPane);
		this.add(newEmployeeName);
		this.add(newEmployeeID);
		this.add(addEmployeeButton);
	}

	public void addEmployee(String employeeName, String employeeID)
	{
		Employee employee = new Employee(employeeName, employeeID);
		employeeListModel.addElement(employee);
	}

	public void actionPerformed(ActionEvent e)
	{
	
		if(e.getSource() == addEmployeeButton)
		{
			String emName = newEmployeeName.getText();
			String emID = newEmployeeID.getText();
			Employee employee = new Employee(emName, emID);
			employeeListModel.addElement(employee);
		}
	}
}
