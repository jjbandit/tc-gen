package com.PRC.tcGen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class EmployeeGroup extends JPanel implements ActionListener
{
	private static final long serialVersionUID = -6932061067756922392L;

	private DefaultListModel<Employee> employeeGroupModel;
	private JList<Employee> employeeJList;

	private JLabel listLabel, nameLabel, idLabel;
	private JButton addEmployeeButton, removeEmployeeButton, removeListButton;
	private JTextField newEmployeeName, newEmployeeID;

	public EmployeeGroup (String label, Row nameRow, Row iDRow)
	{
		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(layout);

		listLabel = new JLabel(label);
		nameLabel = new JLabel("Employee Name");
		idLabel = new JLabel("Employee ID");

		//Create Model and JList
		employeeGroupModel = new DefaultListModel<Employee>();
		addEmployeeRowsToGroup(nameRow, iDRow);
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

		removeListButton = new JButton("X");
		removeListButton.setMargin(new Insets(0,3,0,3));
		removeListButton.addActionListener(this);

		// Group label and removeList buttons
		JPanel header = new JPanel();
		header.add(listLabel);
		header.add(removeListButton);

		// Group add and remove buttons
		JPanel addRemoveButtons = new JPanel();
		addRemoveButtons.add(addEmployeeButton);
		addRemoveButtons.add(removeEmployeeButton);

		this.add(header);
		this.add(employeeGroupPane);
		this.add(nameLabel);
		this.add(newEmployeeName);
		this.add(idLabel);
		this.add(newEmployeeID);
		this.add(addRemoveButtons);
	}

	public void addEmployeeRowsToGroup (Row nameRow, Row IDRow)
	{
		// loop through all the cells in Row r
		// parsing the data into the group
		for (Cell nameCell : nameRow) {
			int employeeID;

			// Produce name string from cell, always exists
			String nameString = nameCell.getStringCellValue();

			// Produce ID string from cell, if one exists
			Integer IDColIndex = nameCell.getColumnIndex();
			if (IDRow != null)
			{
				Cell IDCell = IDRow.getCell(IDColIndex);
				employeeID = (int) IDCell.getNumericCellValue();
				addEmployee(nameString, employeeID);
			}
		}
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

	public void removeList ()
	{
		TimecardGenerator tcBuilder = (TimecardGenerator) this.getParent();
		// ew - remove the sheet in the template file
		tcBuilder.templateBook.removeSheetAt(tcBuilder.templateBook.getSheetIndex(listLabel.getText()));
		tcBuilder.employeeGroupList.removeElement(this);
		employeeGroupModel.removeAllElements();
		this.removeAll();
		tcBuilder.repack();
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
			try
			{
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
		else if (e.getSource() == removeListButton)
		{
			int confirm = JOptionPane.showConfirmDialog(
				this.getParent(),
				"Remove this template and it's staff members?",
				"Confirm",
				JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION)
			{
				removeList();
			} else {
			}
		}
	}
}
