package com.PRC.tcGen;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class TimecardGenerator extends JPanel implements ActionListener
{
	private static final long serialVersionUID = -867058275401540869L;
	public String version = "0.2.1";

	private static JFrame frame;

	// keep track of what template were working from
	public Workbook templateBook;

	// Create a model to keep track of the number of employee groups initialized
	public DefaultListModel<EmployeeGroup> employeeGroupList;

	private String templateFileDir, templateFileName;

	// UI Junk
	private BoxLayout layout;

		private JButton openButton, exitButton, buildTimecardsButton;
		private JFileChooser fc;
		private DatePanel datePanel;

		private JMenuBar menuBar;
		private JMenu menu;
		private JMenuItem infoMenuItem, instructionMenuItem;


		public TimecardGenerator()
		{
			// Initialize a list to keep track of how many groups there are
			employeeGroupList = new DefaultListModel<EmployeeGroup>();

			layout = new BoxLayout(this, BoxLayout.X_AXIS);
			this.setLayout(layout);

			//Create a file chooser
			fc = new JFileChooser();
			fc.addChoosableFileFilter( new FileNameExtensionFilter("Excel file", "xlsx"));

			initMenuBar();
			initButtonPanel();
		}

		public void initMenuBar ()
		{
			menuBar = new JMenuBar();
			menu = new JMenu("Help");
			menuBar.add(menu);

			infoMenuItem = new JMenuItem("Info");
			infoMenuItem.addActionListener(this);

			instructionMenuItem = new JMenuItem("Instructions");
			instructionMenuItem.addActionListener(this);

			menu.add(infoMenuItem);
			menu.add(instructionMenuItem);

			frame.setJMenuBar(menuBar);
		}

		public void initButtonPanel ()
		{
			//Create the open button.  We use the image from the JLF
		//Graphics Repository (but we extracted it from the jar).
		openButton = new JButton("Open Template");
		openButton.addActionListener(this);

		//Create the save button.  We use the image from the JLF
		//Graphics Repository (but we extracted it from the jar).
		exitButton = new JButton("Save & Close");
		exitButton.addActionListener(this);

		// Create the build button
		buildTimecardsButton = new JButton("Build TCs");
		buildTimecardsButton.addActionListener(this);

		// Create the date picker -- my custom class to initialize
		// the date picker ui element
		datePanel = new DatePanel();

		// Self promotion
		JLabel sp1 = new JLabel("Built and maintained by Jesse Hughes (C) 2015");
		JLabel sp2 = new JLabel("www.jessejames.ca");


		//For layout purposes, put the buttons in a separate panel
		Box vBox = Box.createVerticalBox();

		vBox.add(Box.createVerticalStrut(15));
		openButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		vBox.add(openButton);

		vBox.add(Box.createVerticalStrut(15));
		buildTimecardsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		vBox.add(buildTimecardsButton);

		vBox.add(Box.createVerticalStrut(15));
		exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		vBox.add(exitButton);

		vBox.add(Box.createVerticalStrut(15));
		datePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		vBox.add(datePanel);

		vBox.add(Box.createVerticalStrut(15));
		sp1.setAlignmentX(Component.CENTER_ALIGNMENT);
		vBox.add(sp1);
		sp2.setAlignmentX(Component.CENTER_ALIGNMENT);
		vBox.add(sp2);

		//Add the buttons and the log to this panel.
		add(vBox);
	}

	public void initEmployeeGroups(Workbook workbook)
	{
		// check if there's a roster sheet already
		int rosterSheetIndex = workbook.getSheetIndex("Roster");

		// if there is, go to town initializing stuff
		if (rosterSheetIndex > -1) {

			// set the roster to be hidden
			workbook.setSheetHidden(rosterSheetIndex, Workbook.SHEET_STATE_HIDDEN);
			// then send it to the back of the workbook
			workbook.setSheetOrder("Roster", workbook.getNumberOfSheets() - 1 );

			// clear container
			removeAll();
			//Add the buttons back in
			initButtonPanel();
			// Add a separator
			add(Box.createHorizontalStrut(10));

			// and the number of template sheets in the workbook
			Integer numTemplates = workbook.getNumberOfSheets() - 1;

			// Loop though all cells starting with the first row,
			// get the cell in the row directly below
			// parse into our employee list
			int count = 0;
			// for each template sheet skip to the next row of employees
			while (count < numTemplates) {
				String groupLabel = workbook.getSheetName(count);

				// Get the row containing names for the current iteration
				Sheet rosterSheet = workbook.getSheet("Roster");
				Row nameRow = rosterSheet.getRow(count*2);
				Row iDRow = rosterSheet.getRow((count*2) + 1);

				// create new employeeGroup -- my gui class with an Employee model
				EmployeeGroup el = new EmployeeGroup(groupLabel, nameRow, iDRow);
				// add the new group to a list for serializing the employees later
				employeeGroupList.addElement(el);

				add(el);
				add(Box.createHorizontalStrut(3));
				this.revalidate();
				count++;
			}

		} else {
			// Didn't find a roster sheet?  Create it and recurse
			workbook.createSheet("Roster");
			initEmployeeGroups(workbook);
		}

		// repack the frame
		frame.pack();
	}

	public Workbook readExcelFile(File timecardTemplateFile)
		throws IOException, InvalidFormatException
	{
		InputStream stream = new FileInputStream(timecardTemplateFile);
		Workbook wb = WorkbookFactory.create(stream);
		return wb;
	}

	public void writeExcelFile(Workbook workbook, String path, String fileName) throws FileNotFoundException
	{
		FileOutputStream fileOut = new FileOutputStream(path + fileName);
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

	public void serializeGroupList ()
		throws IOException
	{
		// return out if there's no workbook to serialize to
		if (templateBook == null) {return;}

		// We're going to completely remove the rosterSheet and insert a blank one
		// because it's easier than iterating though all the cells and clearing them
		int rosterSheetIndex = templateBook.getSheetIndex("Roster");
		templateBook.removeSheetAt(rosterSheetIndex);
		templateBook.createSheet("Roster");

		// set the sheet to be hidden
		templateBook.setSheetHidden(rosterSheetIndex, Workbook.SHEET_STATE_HIDDEN);

		// Get the roster sheet
		Sheet rosterSheet = templateBook.getSheet("Roster");

		int index = 0;
		int i = employeeGroupList.size();
		// Loop through each group in the GroupList
		while (index < i)
		{
			// get each employeeGroup in the groupList
			EmployeeGroup group = employeeGroupList.elementAt(index);
			SortedListModel employeeGroup = group.getModel();

			// iterate through each employee
			int numEmployees = employeeGroup.getSize();
			int count = 0;
			while (count < numEmployees)
			{
				String employeeName = employeeGroup.getElementAt(count).getFullName();
				int employeeID = employeeGroup.getElementAt(count).getID();

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
		writeExcelFile(templateBook, templateFileDir, templateFileName);
	}

	public void addEmployeeToBook (Employee employee, Workbook workbook, int templateSheetIndex)
	{
		// get the template sheet for the current employee group
		Sheet templateSheet = workbook.cloneSheet(templateSheetIndex);
		templateSheet.getPrintSetup().setLandscape(true);
		templateSheet.getPrintSetup().setPaperSize(PrintSetup.LETTER_PAPERSIZE);
		templateSheet.getPrintSetup().setScale((short)80);

		// Get the employee informatino
		String employeeName = employee.getFullName();
		String pageName = employee.getInvertedName();
		int employeeID = employee.getID();

		// Append a counter to employees with identical names
		int count = 1;
		while (workbook.getSheet(pageName) != null) {
			pageName = employee.getInvertedName() + " (" + count + ")";
			count++;
		}
		// Set sheet fields
		workbook.setSheetName(workbook.getNumberOfSheets() - 1, pageName);
		setEmployeeData(employeeName, employeeID, templateSheet);
	}

	public void dateTemplateSheets(Workbook wb)
	{
		// We can use the total number of sheets because we ripped out the roster sheet
		int numTemplates = wb.getNumberOfSheets();
		// Constants representing indexes of columns to insert runs of dates in
		int[] daysOfWeek = new int[] {6, 7, 8, 9, 10, 11, 12};
		// Constants representing the indexes of rows to insert runs of dates in
		int[] dateRows = new int[] {7, 10, 13, 16, 19, 22, 25, 28};

		// Loop through each template
		int templateIndex = 0;
		while (templateIndex < numTemplates)
		{

			// Start by getting the date from the datepanel
			Calendar cal = datePanel.getCal();
			int dayOfWeek = cal.get(Calendar.DAY_OF_MONTH);

			// And the sheet were working with
			Sheet s = wb.getSheetAt(templateIndex);

			for (int dow : daysOfWeek)
			{
				for (int row : dateRows)
				{
					Row r = s.getRow(row);
					if (r != null)
					{
						Cell c = s.getRow(row).getCell(dow, Row.CREATE_NULL_AS_BLANK);
						c.setCellValue(dayOfWeek);
					} else {
						r = s.createRow(row);
						Cell c = r.getCell(dow, Row.CREATE_NULL_AS_BLANK);
						c.setCellValue(dayOfWeek);
					}
				}
				// Increment the calendar we pulled from the datepanel UI element
				cal.add(Calendar.DATE, 1);
				// And get the new date integer
				dayOfWeek = cal.get(Calendar.DAY_OF_MONTH);
			}

			// set the first period field
			Row rOne = s.getRow(1);
			if (rOne != null)
			{
				rOne.getCell(10, Row.CREATE_NULL_AS_BLANK).setCellValue(datePanel.getCal());

				// Set second period field
				Calendar c = (Calendar) datePanel.getCal().clone();
				c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
				rOne.getCell(12, Row.CREATE_NULL_AS_BLANK).setCellValue(c);

			} else {
				rOne = s.createRow(1);
				rOne.getCell(10, Row.CREATE_NULL_AS_BLANK).setCellValue(datePanel.getCal());

				// Set second period field
				Calendar c = (Calendar) datePanel.getCal().clone();
				c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
				rOne.getCell(12, Row.CREATE_NULL_AS_BLANK).setCellValue(c);
			}

			// Iterate
			templateIndex++;
		}
	}

	public String getSaveFileName ()
	{
		String cDate;
		String returnString = "";
		SimpleDateFormat f = new SimpleDateFormat("M-dd-Y");

		returnString = returnString.concat("Aquatic Timecards ");

		Calendar c = datePanel.getCal();
		cDate = f.format(c.getTime());
		returnString = returnString.concat(cDate);
		returnString = returnString.concat(" to ");

		c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		cDate = f.format(c.getTime());
		returnString = returnString.concat(cDate);

		return returnString;
	}

	public void buildTimecards()
	{
		// keep track of the workbook we're building
		Workbook outBook;
		try
		{
			outBook = readExcelFile(new File(templateFileDir + templateFileName));
		}
		catch(IOException ex)
		{return;}
		catch(InvalidFormatException ex)
		{return;}

		// Rip out roster sheet because we're not going to need it
		int rosterSheetIndex = outBook.getSheetIndex("Roster");
		outBook.removeSheetAt(rosterSheetIndex);

		// First we insert date runs into the template cards
		dateTemplateSheets(outBook);

		int index = 0;
		int numTemplates = employeeGroupList.size();
		SortedListModel sortedEmployees = new SortedListModel();
		// Loop through each group in the GroupList
		// adding each set to a new sortedList
		while (index < numTemplates)
		{
			// get employeeGroup
			EmployeeGroup group = employeeGroupList.elementAt(index);

			// Make each employee in the group aware of what template it belongs to
			group.setTemplateIndex(index);

			// Sort the employees by adding them to a sorted list
			sortedEmployees.addAll(group.getModel());
			index++;
		}

		// iterate through each employee
		int numEmployees = sortedEmployees.getSize();
		int curEmployee = 0;
		while (curEmployee < numEmployees)
		{
			Employee e = sortedEmployees.getElementAt(curEmployee);
			addEmployeeToBook(e, outBook, e.getTemplateIndex());
			curEmployee++;
		}

		// Remove template sheets and roster
		int sheetCount = 0;
		while (sheetCount < numTemplates)
		{
			outBook.removeSheetAt(0);
			sheetCount++;
		}

		try
		{
			// getSaveFileName returns the plaintext name of the file so we have to process
			// it a little more
			String svString = "";
			svString = svString.concat(getSaveFileName());
			svString = svString.concat(".xlsx");
			writeExcelFile(outBook, templateFileDir, svString);
		}
		catch (FileNotFoundException ex)
		{}
	}

	public void setEmployeeData (String employeeName, int employeeID, Sheet sheet)
	{
		Row nameRow = sheet.getRow(3);
		if (nameRow != null)
		{
			Cell nameCell = nameRow.getCell(2, Row.CREATE_NULL_AS_BLANK);
			nameCell.setCellValue(employeeName);
		} else {
			nameRow = sheet.createRow(3);
			Cell nameCell = nameRow.getCell(2, Row.CREATE_NULL_AS_BLANK);
			nameCell.setCellValue(employeeName);
		}

		Row idRow = sheet.getRow(2);
		if (idRow != null)
		{
			Cell idCell = idRow.getCell(2, Row.CREATE_NULL_AS_BLANK);
			idCell.setCellValue(employeeID);
		} else {
			idRow = sheet.createRow(2);
			Cell idCell = idRow.getCell(2, Row.CREATE_NULL_AS_BLANK);
			idCell.setCellValue(employeeID);
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		//Handle open button action
		if (e.getSource() == openButton)
		{
			// disable UI
			openButton.setEnabled(false);
			buildTimecardsButton.setEnabled(false);
			exitButton.setEnabled(false);
			// set current directory to where the file was run from
			try
			{
				fc.setCurrentDirectory(new java.io.File("").getCanonicalFile());
			}
			catch (IOException ex)
			{}

			// Open the filechooser
			int returnVal = fc.showOpenDialog(TimecardGenerator.this);
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				File file = fc.getSelectedFile();

					// Set workbook objects
					try
					{
					templateBook = readExcelFile(file);
					}
					catch (IllegalArgumentException ex)
					{
						JOptionPane.showMessageDialog(null, "Please choose a file with extension .xlsx", "Try again", JOptionPane.INFORMATION_MESSAGE);

						// Re-enable UI
						openButton.setEnabled(true);
						buildTimecardsButton.setEnabled(true);
						exitButton.setEnabled(true);
						return;
					}
					catch (IOException ex)
					{}
					catch (InvalidFormatException ex)
					{}

					templateFileName = file.getName();
					// append a file delimiter because we're only planning on using
					// this to save files with
					templateFileDir = file.getParent().concat(File.separator);

					// empty the groupList so we can re-initialize it
					employeeGroupList.removeAllElements();
					// init that shit!
					initEmployeeGroups(templateBook);
			}

			// filechooser EXIT_OPTION do nothing
			else
			{
			}
			// Re-enable UI
			openButton.setEnabled(true);
			buildTimecardsButton.setEnabled(true);
			exitButton.setEnabled(true);
		}
			// Handle buildTC button
		else if (e.getSource() == buildTimecardsButton)
		{
			openButton.setEnabled(false);
			buildTimecardsButton.setEnabled(false);
			exitButton.setEnabled(false);

			buildTimecards();

			// Re-enable UI
			openButton.setEnabled(true);
			buildTimecardsButton.setEnabled(true);
			exitButton.setEnabled(true);
		}
		//Handle exit button action
		else if (e.getSource() == exitButton)
		{
			try
			{
				serializeGroupList();
			}
			catch (IOException ex)
			{}
			System.exit(0);
		}
		else if (e.getSource() == infoMenuItem)
		{
			showInfo();
		}
		else if (e.getSource() == instructionMenuItem)
		{
			showInstructions();
		}
	}

	private void showInfo()
	{
		JOptionPane.showMessageDialog(frame,
			"If you find this program useful and would like to find out more, \n"
			+ "or you would like to contribute to its development, \n"
			+ "please visit www.jessejames.ca/projects/tc-gen \n"
			+ "\n"
			+ "Version " + version + "\n"
			+ "For the latest version visit www.jessejames.ca/projects/tc-gen \n"
			+ "\n"
			+ "This is Free and Open Source software, distributed under GNU GPL v3. \n"
			+ "Jesse Hughes, (C) 2015. \n"
		);
	}

	private void showInstructions()
	{
		JOptionPane.showMessageDialog(frame,
			"For instructions on using this software please visit \n"
			+ "www.jessejames.ca/projects/tc-gen/readme \n"
		);
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
				// frame.setSize(300, 200);

				//Display the window.
				frame.pack();
				frame.setVisible(true);
		}

	public void repack ()
	{
		frame.pack();
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
