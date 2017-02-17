package com.razor.raw.core.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	public static final String STYLE_DEFAULT = "MM/dd/yyyy";
	public static final String STYLE_TIMESTAMP_DEFAULT = "MM/dd/yyyy HH:mm:ss";
	

	public static synchronized String getDateAsString(String format, Date date) {
		String sDate = null;
		if (format == null || date == null) {
			return sDate;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		sDate = sdf.format(date);
		return sDate;
	}

	public static synchronized String getDateAsStringFromLong(String format, long date) {
		String sDate = null;
		if (format == null || date == 0) {
			return sDate;
		}

		SimpleDateFormat sdf = new SimpleDateFormat(format);

		sDate = sdf.format(new Date(date));
		return sDate;
	}

	public static synchronized Date getDateFromString(String format,
			String sDate) throws ParseException {
		Date date = null;
		if (format == null || sDate == null) {
			return date;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		date = sdf.parse(sDate);
		return date;
	}

	public static synchronized String getConvertedDate(java.sql.Date origDate) 
	{
		if(origDate == null) 
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		return sdf.format(origDate);
	}
	
	/*public static void main(String[] args) {
		System.out.println(getDateAsStringFromLong("MM-dd-yyyy", 1400610600000L));

	}*/
}
