package com.PRC.tcGen;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
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

	JButton openButton, exitButton;
	JFileChooser fc;
	BoxLayout layout;

	public TimecardGenerator() {

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
		exitButton = new JButton("Close");
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

	public void initEmployeeLists(XSSFWorkbook workbook) {
		String lastSheetString = getLastSheetName(workbook);
		if (lastSheetString.equals("Roster")) {
			System.out.println("found a Roster, populating GUI lists");
			XSSFSheet rosterSheet = workbook.getSheet(lastSheetString);
			int numTemplates = workbook.getNumberOfSheets() - 1;
			System.out.println(numTemplates);

			int count = 0;
			while (count < numTemplates) {
				System.out.println("yay" + count);
				EmployeeList el = new EmployeeList();

				try
				{
					XSSFRow rosterSheetNameRow = rosterSheet.getRow(count*2);
					// Loop though all cells in the current row and get the cell in the 
					// row directly below to parse into our employee list
					for (Cell nameCell : rosterSheetNameRow) {
						DataFormatter df = new DataFormatter();

						XSSFRow IDRow = rosterSheet.getRow((count*2) + 1);
						Integer IDColIndex = nameCell.getColumnIndex();
						XSSFCell IDCell = IDRow.getCell(IDColIndex);

						String IDString = df.formatCellValue(IDCell);
						String nameString = nameCell.getStringCellValue();

						el.addEmployee(nameString, IDString);
					}
				}
				catch(NullPointerException e)
				{System.out.println("whups, uninitialized list");}

				add(el);
				this.revalidate();
				count++;
			}

			try {
				// System.out.println(cell.getStringCellValue());
				// listModel.addElement(cell.getStringCellValue());
			} catch (IllegalStateException e) {
				// System.out.println((int)cell.getNumericCellValue());
				// listModel.addElement(cell.getNumericCellValue());
			} finally {
			}

		} else {
			System.out.println("!found a Roster, creating");
			workbook.createSheet("Roster");
			int numTemplates = workbook.getNumberOfSheets() - 1;

			int count = 0;
			while (count < numTemplates) {
				System.out.println("yay" + count);
				count++;
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		//Handle open button actionm.
		if (e.getSource() == openButton) {

			fc.setCurrentDirectory(new java.io.File("").getAbsoluteFile());
			int returnVal = fc.showOpenDialog(TimecardGenerator.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				try {

					XSSFWorkbook wb = readExcelFile(file);
					initEmployeeLists(wb);
					writeExcelFile(wb);

				} catch (IOException ioe) {
				} catch (InvalidFormatException ife) {
				} finally {
				}
			} else {
			}

			//Handle exit button action.
		} else if (e.getSource() == exitButton) {
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
