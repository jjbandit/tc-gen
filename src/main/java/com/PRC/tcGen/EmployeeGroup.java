package com.PRC.tcGen;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.PRC.tcGen.Employee;

public class EmployeeGroup extends JPanel implements ActionListener
{
	private static final long serialVersionUID = -6932061067756922392L;

	private SortedListModel employeeGroupModel;
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
		employeeGroupModel = new SortedListModel();
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

		removeListButton = new JButton("Remove Template");
		removeListButton.setMargin(new Insets(0,3,0,3));
		removeListButton.addActionListener(this);

		// Group add and remove buttons
		JPanel addRemoveButtons = new JPanel();
		addRemoveButtons.add(addEmployeeButton);
		addRemoveButtons.add(removeEmployeeButton);

		// Center everything
		listLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		employeeGroupPane.setAlignmentX(Component.CENTER_ALIGNMENT);
		nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		newEmployeeName.setAlignmentX(Component.CENTER_ALIGNMENT);
		idLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		newEmployeeID.setAlignmentX(Component.CENTER_ALIGNMENT);
		addRemoveButtons.setAlignmentX(Component.CENTER_ALIGNMENT);
		removeListButton.setAlignmentX(Component.CENTER_ALIGNMENT);

		// Add everything
		this.add(listLabel);
		this.add(employeeGroupPane);
		this.add(nameLabel);
		this.add(newEmployeeName);
		this.add(idLabel);
		this.add(newEmployeeID);
		this.add(addRemoveButtons);
		this.add(removeListButton);
	}

	public void addEmployeeRowsToGroup (Row nameRow, Row IDRow)
	{
		if (nameRow == null || IDRow == null) { return; }
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
		Employee emp = new Employee(employeeName, employeeID);
		employeeGroupModel.add(emp);
	}

	public SortedListModel getModel ()
	{
		return employeeGroupModel;
	}

	public void removeList ()
	{
		TimecardGenerator tcBuilder = (TimecardGenerator) this.getParent();
		// ew - remove the sheet in the template file
		tcBuilder.templateBook.removeSheetAt(tcBuilder.templateBook.getSheetIndex(listLabel.getText()));
		tcBuilder.employeeGroupList.removeElement(this);
		employeeGroupModel.clear();
		this.removeAll();
		tcBuilder.repack();
	}

	public void setTemplateIndex (int index)
	{
		// This method is nessicary for building the timecards. It calls setTemplateIndex
		// on each Employee so we know what template sheet to use when building timecards
		

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
				employeeGroupModel.add(employee);
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
				"Are you sure you want to remove this template and it's employees?",
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
