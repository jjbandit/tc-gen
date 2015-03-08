package com.PRC.tcGen;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFPrintSetup;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TimecardGenerator extends JPanel implements ActionListener
{
	private static final long serialVersionUID = -867058275401540869L;

	private static JFrame frame;

	// UI Junk
	private BoxLayout layout;

	private JButton openButton, exitButton, buildTimecardsButton;
	private JFileChooser fc;
	private DatePanel datePanel;

	// keep track of what template were working from
	private XSSFWorkbook templateBook;

	// keep track of the workbook we're building
	private XSSFWorkbook outBook;

	// Create a model to keep track of the number of employee groups initialized
	private DefaultListModel<EmployeeGroup> employeeGroupList;

	public TimecardGenerator()
	{
		// Initialize a list to keep track of how many groups there are
		employeeGroupList = new DefaultListModel<EmployeeGroup>();

		layout = new BoxLayout(this, BoxLayout.X_AXIS);
		this.setLayout(layout);

		//Create a file chooser
		fc = new JFileChooser();

		// Create the date picker -- my custom class to initialize
		// the date picker ui element
		datePanel = new DatePanel();

		initButtonPanel();
	}

	public void initButtonPanel ()
	{
		//Create the open button.  We use the image from the JLF
		//Graphics Repository (but we extracted it from the jar).
		openButton = new JButton("Open a File...");
		openButton.addActionListener(this);

		//Create the save button.  We use the image from the JLF
		//Graphics Repository (but we extracted it from the jar).
		exitButton = new JButton("Save & Close");
		exitButton.addActionListener(this);

		// Create the build button
		buildTimecardsButton = new JButton("Build TCs");
		buildTimecardsButton.addActionListener(this);

		//For layout purposes, put the buttons in a separate panel
		JPanel buttonPanel = new JPanel();
		BoxLayout b = new BoxLayout(buttonPanel, BoxLayout.Y_AXIS);
		buttonPanel.setLayout(b);
		buttonPanel.add(openButton);
		buttonPanel.add(buildTimecardsButton);
		buttonPanel.add(exitButton);
		buttonPanel.add(datePanel);

		//Add the buttons and the log to this panel.
		add(buttonPanel);
	}

	public XSSFWorkbook readExcelFile(File timecardTemplateFile)
		throws IOException, InvalidFormatException
	{
		InputStream stream = new FileInputStream(timecardTemplateFile);
		XSSFWorkbook template = new XSSFWorkbook(stream);
		return template;
	}

	public void writeExcelFile(XSSFWorkbook workbook)
		throws FileNotFoundException
	{
		FileOutputStream fileOut = new FileOutputStream(
			"target/testWorkbook.xlsx");
		try {
			workbook.write(fileOut);
			workbook.close();
			fileOut.close();
		}
		catch (IOException ex)
		{}
		finally
		{}

	}

	private String getLastSheetName(XSSFWorkbook workbook)
	{
		String lastSheetString = workbook.getSheetAt(
			workbook.getNumberOfSheets() - 1).getSheetName();
		return lastSheetString;
	}

	public void initEmployeeGroups(XSSFWorkbook workbook)
	{
		// check if there's a roster sheet already
		String lastSheetString = getLastSheetName(workbook);
		// if there is, go to town initializing stuff
		if (lastSheetString.equals("Roster")) {

			// clear container
			removeAll();
			//Add the buttons back in
			initButtonPanel();

			// get the roster sheet
			XSSFSheet rosterSheet = workbook.getSheet("Roster");
			// and the number of template sheets in the workbook
			Integer numTemplates = workbook.getNumberOfSheets() - 1;

			// Loop though all cells starting with the first row,
			// get the cell in the row directly below
			// parse into our employee list
			int count = 0;
			// for each template sheet skip to the next row of employees
			while (count < numTemplates) {
				// create new employeeGroup -- my gui class with an Employee model
				EmployeeGroup el = new EmployeeGroup();
				// add the new group to a list for serializing the employees later
				employeeGroupList.addElement(el);

				// Get the row containing names for the current iteration
				XSSFRow nameRow = rosterSheet.getRow(count*2);
				XSSFRow IDRow = rosterSheet.getRow((count*2) + 1);

				if (nameRow != null)
				{
					addEmployeeDataToGroup(nameRow, IDRow, el);
				}
				add(el);
				this.revalidate();
				count++;
			}

		} else {
			// Didn't find a roster sheet?  Create it and recurse
			workbook.createSheet("Roster");
			initEmployeeGroups(workbook);
		}
	}

	public void addEmployeeDataToGroup (Row nameRow, Row IDRow, EmployeeGroup group)
	{
		// loop through all the cells in Row r
		// parsing the data into the group
		for (Cell nameCell : nameRow) {
			DataFormatter df = new DataFormatter();
			String IDString = "";

			// Produce name string from cell, always exists
			String nameString = nameCell.getStringCellValue();

			// Produce ID string from cell, if one exists
			Integer IDColIndex = nameCell.getColumnIndex();
			if (IDRow != null)
			{
				Cell IDCell = IDRow.getCell(IDColIndex);
				IDString = df.formatCellValue(IDCell);
			}
			group.addEmployee(nameString, IDString);
		}
	}

	public void serializeGroupList ()
		throws IOException
	{
		// return out if there's no workbook to serialize to
		if (templateBook == null) {return;}

		// We're going to completely remove the rosterSheet and insert a blank one
		// because it's easier than iterating though all the cells and clearing them
		int rosterSheetIndex = templateBook.getNumberOfSheets() - 1;
		templateBook.removeSheetAt(rosterSheetIndex);
		templateBook.createSheet("Roster");

		// Get the roster sheet
		Sheet rosterSheet = templateBook.getSheet("Roster");

		int index = 0;
		int i = employeeGroupList.size();
		// Loop through each group in the GroupList
		while (index < i)
		{
			// get each employeeGroup in the groupList
			EmployeeGroup group = employeeGroupList.elementAt(index);
			DefaultListModel<Employee> employeeGroup = group.getModel();

			// iterate through each employee
			int numEmployees = employeeGroup.size();
			int count = 0;
			while (count < numEmployees)
			{
				String employeeName = employeeGroup.getElementAt(count).getName();
				String employeeID = employeeGroup.getElementAt(count).getID();

				// Set or create new row for employee names
				Row nameRow = rosterSheet.getRow(index*2);
				if (nameRow==null){
					nameRow = rosterSheet.createRow(index*2);
				}
				// set the name cell value
				nameRow.createCell(count).setCellValue(employeeName);

				// Get or create the row immedietly below the nameRow which contains IDs
				Row IDRow = rosterSheet.getRow((index*2)+1);
				if (IDRow == null)
				{
					IDRow = rosterSheet.createRow((index*2)+1);
				}
				// set the ID value
				IDRow.createCell(count).setCellValue(employeeID);

				// iterate
				count++;
			}
			index++;
		}

		// Save that shit!
		FileOutputStream fileOut = new FileOutputStream("aNewWorkbook.xlsx");
		templateBook.write(fileOut);
		fileOut.close();
	}

	public void addEmployeeToBook (Employee employee, Workbook workbook, int templateSheetIndex)
	{
		// get the template sheet for the current employee group
		Sheet templateSheet = workbook.cloneSheet(templateSheetIndex);
		templateSheet.getPrintSetup().setLandscape(true);
		templateSheet.getPrintSetup().setPaperSize(XSSFPrintSetup.LETTER_PAPERSIZE);
		templateSheet.getPrintSetup().setScale((short)80);

		// Get the employee informatino
		String employeeName = employee.getName();
		String employeeID = employee.getID();

		// Set sheet fields
		workbook.setSheetName(outBook.getNumberOfSheets() - 1, employeeName);
		setEmployeeData(employeeName, employeeID, templateSheet);
	}

	public void buildTimecards()
	{
		int index = 0;
		int numTemplates = employeeGroupList.size();
		// Loop through each group in the GroupList
		while (index < numTemplates)
		{
			// get each employeeGroup in the groupList
			EmployeeGroup group = employeeGroupList.elementAt(index);
			DefaultListModel<Employee> employeeGroup = group.getModel();

			// iterate through each employee
			int numEmployees = employeeGroup.size();
			int count = 0;
			while (count < numEmployees)
			{
				Employee e = employeeGroup.getElementAt(count);
				addEmployeeToBook(e, outBook, index);
				count++;
			}
			index++;
		}
		// Remove template sheets and roster
		int count = 0;
		while (count <= numTemplates)
		{
			outBook.removeSheetAt(0);
			count++;
		}

		try{
			writeExcelFile(outBook);
		}
		catch (FileNotFoundException ex)
		{}
	}

	public void setEmployeeData (String employeeName, String employeeID, Sheet sheet)
	{
		Row nameRow = sheet.getRow(3);
		Cell nameCell = nameRow.getCell(2);
		nameCell.setCellValue(employeeName);

		Row idRow = sheet.getRow(2);
		Cell idCell = idRow.getCell(2);
		idCell.setCellValue(employeeID);
	}

	public void actionPerformed(ActionEvent e)
	{
		//Handle open button action
		if (e.getSource() == openButton) {

			// set current directory to where the file was run from
			fc.setCurrentDirectory(new java.io.File("").getAbsoluteFile());

			// Open the filechooser
			int returnVal = fc.showOpenDialog(TimecardGenerator.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				try {

					// Set workbook objects
					templateBook = readExcelFile(file);
					outBook = readExcelFile(file);

					// empty the groupList so we can re-initialize it
					employeeGroupList.removeAllElements();
					// init that shit!
					initEmployeeGroups(templateBook);

					// TODO Not doing much here.. should probably do something about this
					// like show some error boxes if it's not an xlsx file or whatever
				} catch (IOException ex) {
				} catch (InvalidFormatException ex) {
				} finally {
				}
			} else {
			}


			//Handle exit button action
		} else if (e.getSource() == exitButton)
		{
			try
			{
				serializeGroupList();
			}
			catch (IOException ex)
			{}
			System.exit(0);


			// Handle buildTC button
		} else if (e.getSource() == buildTimecardsButton)
		{
			buildTimecards();
		}
	}

	/**
	* Create the GUI and show it.  For thread safety,
	* this method should be invoked from the
	* event dispatch thread.
	*/
	private static void createAndShowGUI()
	{
		//Create and set up the window.
		frame = new JFrame("TimecardGenerator");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				//Add content to the window.
				frame.add(new TimecardGenerator());

				//Display the window.
				frame.pack();
				frame.setVisible(true);
		}

	public static void main(String[] args)
	{
			//Schedule a job for the event dispatch thread:
			//creating and showing this application's GUI.
			SwingUtilities.invokeLater(new Runnable() {
					public void run() {
							//Turn off metal's use of bold fonts
							UIManager.put("swing.boldMetal", Boolean.FALSE);
							createAndShowGUI();
					}
			});
	}
}
