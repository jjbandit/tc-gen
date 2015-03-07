package com.PRC.tcGen;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

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
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TimecardGenerator extends JPanel implements ActionListener {

	private static final long serialVersionUID = -867058275401540869L;
	private static JFrame frame;

	// UI Junk
	JButton openButton, exitButton;
	JFileChooser fc;
	BoxLayout layout;

	// keep track of what template were working from
	private XSSFWorkbook workbook;
	// Create a model to keep track of the number of employee groups initialized
	private DefaultListModel<EmployeeGroup> employeeGroupList;


	public TimecardGenerator() {

		// Initialize the list that keeps track of how many groups there are
		employeeGroupList = new DefaultListModel<EmployeeGroup>();

		layout = new BoxLayout(this, BoxLayout.X_AXIS);
		this.setLayout(layout);

		//Create a file chooser
		fc = new JFileChooser();

		//Create the open button.  We use the image from the JLF
		//Graphics Repository (but we extracted it from the jar).
		openButton = new JButton("Open a File...");
		openButton.addActionListener(this);

		//Create the save button.  We use the image from the JLF
		//Graphics Repository (but we extracted it from the jar).
		exitButton = new JButton("Save & Close");
		exitButton.addActionListener(this);

		//For layout purposes, put the buttons in a separate panel
		JPanel buttonPanel = new JPanel(); //use FlowLayout
		buttonPanel.add(openButton);
		buttonPanel.add(exitButton);

		//Add the buttons and the log to this panel.
		add(buttonPanel);
	}

	public XSSFWorkbook readExcelFile(File timecardTemplateFile)
			throws IOException, InvalidFormatException {
		InputStream stream = new FileInputStream(timecardTemplateFile);
		XSSFWorkbook template = new XSSFWorkbook(stream);
		return template;
	}

	public void writeExcelFile(XSSFWorkbook workbook) throws IOException,
			InvalidFormatException {
		FileOutputStream fileOut = new FileOutputStream(
				"target/testWorkbook.xlsx");
		workbook.write(fileOut);
		workbook.close();
		fileOut.close();
	}

	private String getLastSheetName(XSSFWorkbook workbook) {
		String lastSheetString = workbook.getSheetAt(
				workbook.getNumberOfSheets() - 1).getSheetName();
		return lastSheetString;
	}

	public void initEmployeeGroups(XSSFWorkbook workbook) {
		// check if there's a roster sheet already
		String lastSheetString = getLastSheetName(workbook);
		// if there is, go to town initializing stuff
		if (lastSheetString.equals("Roster")) {

			// get the roster sheet
			XSSFSheet rosterSheet = workbook.getSheet("Roster");
			// and the number of template sheets in the workbook
			Integer numTemplates = workbook.getNumberOfSheets() - 1;

			// Loop though all cells in the current row and get the cell in the
			// row directly below to parse into our employee list
			int count = 0;
			while (count < numTemplates) {
				// create new employeeGroup
				EmployeeGroup el = new EmployeeGroup();
				// add the new group to the groupList so we can serialize it later
				employeeGroupList.addElement(el);

				// If there's data in the row it's expecting the data in
				XSSFRow rosterSheetNameRow = rosterSheet.getRow(count*2);
				if (rosterSheetNameRow != null)
				{
					// Then loop through all the cells in that row
					// parsing the data into our new group
					for (Cell nameCell : rosterSheetNameRow) {
						DataFormatter df = new DataFormatter();
						String IDString = "";

						// Produce name string from cell, always exists
						String nameString = nameCell.getStringCellValue();

						// Produce ID string from cell, if one exists
						XSSFRow IDRow = rosterSheet.getRow((count*2) + 1);
						Integer IDColIndex = nameCell.getColumnIndex();
						if (IDRow != null)
						{
							XSSFCell IDCell = IDRow.getCell(IDColIndex);
							IDString = df.formatCellValue(IDCell);
						}
						el.addEmployee(nameString, IDString);
					}
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

	public void serializeGroupList () throws IOException
	{
		// We're going to completely remove the rosterSheet and insert a blank one
		// because it's easier than iterating though all the cells and clearing them
		int rosterSheetIndex = workbook.getNumberOfSheets() - 1;
		workbook.removeSheetAt(rosterSheetIndex);
		workbook.createSheet("Roster");

		// Get the roster sheet
		Sheet rosterSheet = workbook.getSheet("Roster");

		int index = 0;
		int i = employeeGroupList.size();
		System.out.println(i);
		// Loop through each group in the GroupList
		while (index < i)
		{
			// get each employeeGroup in the groupList
			EmployeeGroup group = employeeGroupList.elementAt(index);
			DefaultListModel<Employee> employeeGroup = group.getModel();

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
				IDRow.createCell(count).setCellValue(employeeID);

				count++;
			}
			index++;
		}

		FileOutputStream fileOut = new FileOutputStream("aNewWorkbook.xlsx");
		workbook.write(fileOut);
		fileOut.close();
	}

	public void actionPerformed(ActionEvent e) {
		//Handle open button action
		if (e.getSource() == openButton) {

			fc.setCurrentDirectory(new java.io.File("").getAbsoluteFile());
			int returnVal = fc.showOpenDialog(TimecardGenerator.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				try {

					// this is the global private instance variable
					workbook = readExcelFile(file);
					// empty the groupList so we can re-initialize it
					employeeGroupList.removeAllElements();
					// init that shit!!
					initEmployeeGroups(workbook);

					// TODO Not doing much here.. should probably do something about this
					// like show some error boxes if it's not an xlsx file or something
				} catch (IOException ioe) {
				} catch (InvalidFormatException ife) {
				} finally {
				}
			} else {
			}

			//Handle exit button action.
		} else if (e.getSource() == exitButton) {
			try
			{
				serializeGroupList();
			}
			catch (IOException ex)
			{}
			System.out.println("Exiting!");
			System.exit(0);
		}
	}

	/**
	* Create the GUI and show it.  For thread safety,
	* this method should be invoked from the
	* event dispatch thread.
	*/
	private static void createAndShowGUI() {
		//Create and set up the window.
		frame = new JFrame("TimecardGenerator");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				//Add content to the window.
				frame.add(new TimecardGenerator());

				//Display the window.
				frame.pack();
				frame.setVisible(true);
		}

		public static void main(String[] args) {
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
