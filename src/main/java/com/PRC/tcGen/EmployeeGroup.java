package com.PRC.tcGen;

import java.awt.Color;
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

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class EmployeeGroup extends JPanel implements ActionListener
{
	private static final long serialVersionUID = -6932061067756922392L;

	private DefaultListModel<Employee> employeeGroupModel;
	private JList<Employee> employeeJList;

	private JLabel listLabel, nameLabel, idLabel;
	private JButton addEmployeeButton, removeEmployeeButton;
	private JTextField newEmployeeName, newEmployeeID;

	public EmployeeGroup (String label) {

		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(layout);

		listLabel = new JLabel(label);
		nameLabel = new JLabel("Employee Name");
		idLabel = new JLabel("Employee ID");

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
			// Validate name is not null
			String emName = newEmployeeName.getText();
			if (emName.isEmpty())
			{
				nameLabel.setForeground(Color.RED);
			}
			else
			{
				nameLabel.setForeground(Color.BLACK);
			}


			// Validate the employee id is an integer
			int emID = 0;
			try {
				emID = Integer.parseInt(newEmployeeID.getText());
				idLabel.setForeground(Color.BLACK);
			}
			catch (NumberFormatException ex)
			{
				idLabel.setForeground(Color.RED);
			}

			// Wow this is ugly!
		if (!emName.isEmpty() && emID > 0)
			{
				Employee employee = new Employee(emName, emID);
				employeeGroupModel.addElement(employee);
			}
		}
		else if (e.getSource() == removeEmployeeButton)
		{
			employeeGroupModel.removeElement(employeeJList.getSelectedValue());
		}
	}
}
