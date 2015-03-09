package com.PRC.tcGen;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

public class EmployeeGroup extends JPanel implements ActionListener
{
	private static final long serialVersionUID = -6932061067756922392L;

	private DefaultListModel<Employee> employeeGroupModel;
	private JList<Employee> employeeJList;

	private JLabel listLabel;
	private JButton addEmployeeButton, removeEmployeeButton;
	private JTextField newEmployeeName, newEmployeeID;

	public EmployeeGroup (String label) {

		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(layout);

		listLabel = new JLabel(label);
		JLabel nameLabel = new JLabel("Employee Name");
		JLabel idLabel = new JLabel("Employee ID");

		//Create Model and JList
		employeeGroupModel = new DefaultListModel<Employee>();
		employeeJList = new JList<Employee>(employeeGroupModel);

		employeeJList.setVisibleRowCount(5);
		employeeJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane employeeGroupPane = new JScrollPane(employeeJList);

		newEmployeeName = new JTextField("", 10);
		newEmployeeName.setMaximumSize(new Dimension(Integer.MAX_VALUE,
		newEmployeeName.getPreferredSize().height));

		newEmployeeID = new JTextField("", 10);
		newEmployeeID.setMaximumSize(new Dimension(Integer.MAX_VALUE,
		newEmployeeID.getPreferredSize().height));

		addEmployeeButton = new JButton("+");
		addEmployeeButton.addActionListener(this);

		removeEmployeeButton = new JButton("-");
		removeEmployeeButton.addActionListener(this);

		// Group add and remove buttons
		JPanel addRemoveButtons = new JPanel();
		addRemoveButtons.add(addEmployeeButton);
		addRemoveButtons.add(removeEmployeeButton);

		this.add(listLabel);
		this.add(employeeGroupPane);
		this.add(nameLabel);
		this.add(newEmployeeName);
		this.add(idLabel);
		this.add(newEmployeeID);
		this.add(addRemoveButtons);
	}

	public void addEmployee(String employeeName, Integer employeeID)
	{
		Employee employee = new Employee(employeeName, employeeID);
		employeeGroupModel.addElement(employee);
	}

	public DefaultListModel<Employee> getModel ()
	{
		return employeeGroupModel;
	}

	public void actionPerformed(ActionEvent e)
	{

		if (e.getSource() == addEmployeeButton)
		{
			String emName = newEmployeeName.getText();
			int emID = Integer.parseInt(newEmployeeID.getText());
			Employee employee = new Employee(emName, emID);
			employeeGroupModel.addElement(employee);
		}
		else if (e.getSource() == removeEmployeeButton)
		{
			employeeGroupModel.removeElement(employeeJList.getSelectedValue());
		}
	}
}
