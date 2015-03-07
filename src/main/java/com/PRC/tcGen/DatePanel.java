package com.PRC.tcGen;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;

import javax.swing.JPanel;

import org.jdatepicker.impl.DateComponentFormatter;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

public class DatePanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private UtilDateModel dateModel;
	private JDatePanelImpl datePanel;
	private JDatePickerImpl datePicker;
	private Properties p;

	public DatePanel ()
	{
		dateModel = new UtilDateModel();
		setDateModelNextSunday(dateModel);
		dateModel.setSelected(true);
		p = new Properties();
		p.put("text.today", "Today");
		p.put("text.month", "Month");
		p.put("text.year", "Year");

		datePanel = new JDatePanelImpl(dateModel, p);
		// This constructor was super annoying to find.. had to read source at 
		// github/jdatepicker/jdatepicker branch 1.3.x DateComponentFormatter.Java
		datePicker = new JDatePickerImpl(datePanel, new DateComponentFormatter());

		add(datePicker);
	}

	private void setDateModelNextSunday (UtilDateModel dateModel)
	{
		Calendar now = new GregorianCalendar();
		Calendar nextSunday = new GregorianCalendar(
			now.get(Calendar.YEAR),
			now.get(Calendar.MONTH),
			now.get(Calendar.DAY_OF_MONTH)
		);
		while (nextSunday.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
		{
			nextSunday.add(Calendar.DAY_OF_WEEK, +1);
		}

		dateModel.setDate(
			nextSunday.get(Calendar.YEAR),
			nextSunday.get(Calendar.MONTH),
			nextSunday.get(Calendar.DAY_OF_MONTH)
		);
	}
	
}

