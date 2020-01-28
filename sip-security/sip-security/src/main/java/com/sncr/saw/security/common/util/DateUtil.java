package com.sncr.saw.security.common.util;

import java.text.FieldPosition;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Utility class for Date related functions
 * @author seshukumar.tv
 * @date Jun 1, 2007
 */
public class DateUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(DateUtil.class.getName());

	public static final String PATTERN_MMDDYYYY = "MM/dd/yyyy";

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
      LOGGER.debug("Error converting date " + dateString
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
      LOGGER.debug("Error converting date " + dateString
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
          LOGGER.error("Error getting no. of days - From Date: {} To Date: {}", p_FROM_Date,  p_THRU_Date);
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
        //Change your date format in here.
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format( now);
    }


    public static Date getDateFromString( String strDate, String pattern) {
        java.util.Date date = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat( pattern);
            date = simpleDateFormat.parse( strDate);
        }
        catch( Exception ex) {
          LOGGER.error("Unable to parse date string: {}", ex);
        }
        return date;
    }
 
    public static boolean isValidDate(String date, String pattern)
    {
        LOGGER.debug("Date Passed is : {}", date);
        Date newDate = getDateFromString(date,pattern);
        LOGGER.debug( "Date formatted to Date format: {}", newDate);
        String testDate = getDateString(newDate, pattern);
        LOGGER.debug( "Test Date : {}", testDate);
        if(date.equals(testDate))
        {
            LOGGER.debug( "Returning true ");
            return true;
        }
        else
        {
            LOGGER.debug( "Returning false ");
            return false;
        }
    }
}
