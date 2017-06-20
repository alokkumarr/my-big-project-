/*
 * Copyright (C) 2007 Razorsight. All Rights Reserved. The source code for this program is not published or otherwise
 * divested of its trade secrets, irrespective of what has been deposited with the U.S. Copyright Office.
 */
package com.sncr.saw.security.common.util;

import java.text.FieldPosition;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

/*
 * Utility class for Date related functions
 * @author seshukumar.tv
 * @date Jun 1, 2007
 */
public class DateUtil {
	private static Logger logger = Logger.getLogger(DateUtil.class
			.getName());
	public static final String PATTERN_MMDDYYYY = "MM/dd/yyyy";
	public static final String PATTERN_YYYYMMDD = "yyyyMMdd";
	public static final String PATTERN_DDMMYYYY_SLASH_SEPERATOR = "dd/MM/yyyy";

	public static String getDateString(Date date) {
		return getDateString(date, PATTERN_MMDDYYYY);
	}

	public static String getDateString(Date date, String pattern) {
		String dateString = null;
		if (date != null && pattern != null) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			StringBuffer stringBuffer = new StringBuffer();
			simpleDateFormat.format(date, stringBuffer, new FieldPosition(0));
			dateString = stringBuffer.toString();
		}
		return dateString;
	}

	public static Date convertStringToDate(String dateString) {
		return convertStringToDate(dateString, PATTERN_MMDDYYYY);
	}

	public static Date convertStringToDate(String dateString, String pattern) {
		SimpleDateFormat sf = new SimpleDateFormat(pattern);
		try {
			return sf.parse(dateString);
		} catch (ParseException e) {
			logger.debug("Error converting date " + dateString
					+ " from String type to Date type");
		}
		return null;
	}

	/*
	 * converts date from one format to another returns: date object in required
	 * format
	 */
	public static Date changeFormat(String fromFormat, String toFormat,
			String dateValue) {
		SimpleDateFormat sf = new SimpleDateFormat(fromFormat);
		SimpleDateFormat sf1 = new SimpleDateFormat(toFormat);
		String dateString = dateValue;
		Date date = null;
		try {
			date = sf.parse(dateValue);
			dateString = sf1.format(date);
			date = sf1.parse(dateString);
		} catch (ParseException e) {
			logger.debug("Error converting date " + dateString
					+ " from format: " + fromFormat + ", to format: "
					+ toFormat);
		}
		return date;
	}

    public static int getNumberOfDays( String p_FROM_Date, String p_THRU_Date) {
        int p_No_Of_Days = 0;
        try {

            SimpleDateFormat simp_df = new SimpleDateFormat( PATTERN_MMDDYYYY);
            ParsePosition parpos = new ParsePosition( 0);
            Date d1 = simp_df.parse( p_FROM_Date, parpos);
            parpos.setIndex( 0);
            Date d2 = simp_df.parse( p_THRU_Date, parpos);

            long p_Milli = d2.getTime() - d1.getTime();
            p_No_Of_Days = (int) (p_Milli / (1000 * 60 * 60 * 24));
        }
        catch( Exception ex) {
            logger.debug("Error getting no. of days - From Date: " + p_FROM_Date
                            + " To Date: " + p_THRU_Date);

            ex.printStackTrace();
        }
        return p_No_Of_Days;
    }

    public static Date addDaystoDate( Date aDate, int noOfDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( aDate);
        calendar.add( Calendar.DATE, noOfDays);
        return calendar.getTime();
    }

    public static String getSysDate (){
        String currentDate = getSysDate(null);
        return currentDate;
    }
    
    public static String getSysDate(String pattern) {
        if (null == pattern)
            pattern = PATTERN_MMDDYYYY;
        
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);//Change your date format in here.
        String display = sdf.format( now);
        return display;
    }


    public static Date getDateFromString( String strDate, String pattern) {
        java.util.Date date = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat( pattern);
            date = simpleDateFormat.parse( strDate);
        }
        catch( Exception ex) {
            ex.printStackTrace();
            logger.debug("Unable to parse date string", ex);
        }
        return date;
    }
 
    public static boolean isValidDate(String date, String pattern)
    {
        logger.debug("Date Passed is : " + date);
        Date newDate = getDateFromString(date,pattern);
        logger.debug( "Date formatted to Date format: " + newDate);
        String testDate = getDateString(newDate, pattern);
        logger.debug( "Test Date : " + testDate);
        if(date.equals(testDate))
        {
            logger.debug( "Returning true ");
            return true;
        }
        else
        {
            logger.debug( "Returning false ");
            return false;
        }
    }
    
    /**
	 * main method for dirty testing
	 * 
	 * @param args
	 */
	/*public static void main(String[] args) {
		logger.debug("Date String: "
				+ getDateString(new Date(), PATTERN_YYYYMMDD));
		logger.debug("Date String: "
				+ getDateString(new Date(), PATTERN_DDMMYYYY_SLASH_SEPERATOR));
	}*/
}
