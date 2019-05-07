package sncr.xdf.transformer.jexl;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * @author pradeep.tripathi
 * 
 */
public class DataManipulationUtil {
	public static final String DATE_PATTERNS[] = { "yyyy-MM-dd HH:mm:ss",
			"yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd HH:mm:ss.SSSSSS",
			"yyyy-MM-dd'T'HH:mm:ss.SSSSSS", "yyyy-MM-dd'T'HH:mm:ss.SSSS",
			"yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
			"yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm.ss'Z'",
			"yyyy-MM-dd'T'HH:mm:ssXXX", "yyyy-MM-dd' 'HH:mm:ss" };
	public static final String DEFAULT_DATE_VALUE = "1900-01-01 00:00:00";
	public static final String UTC_ETC = "Etc/UTC";
	public static final char PLUS_SIGN = '+';
	public static final char MINUS_SIGN = '-';
	public static final String COLON = ":";

        private static final char[] hexArray = "0123456789ABCDEF".toCharArray();
        private static final String RND_NULL_PREFIX="_!rndNull";
	private static MessageDigest md;


	public DataManipulationUtil() {
		try {
			md = MessageDigest.getInstance("MD5");
                } catch(Exception e) {
			md = null;
		}
	}
	
	public static String RandomNULL(int seed) {
		return  RND_NULL_PREFIX + Long.toString(System.nanoTime() & seed);
        }	

	public static String RandomNULL(String prefix, int seed) {
		return  prefix + Long.toString(System.nanoTime() & seed);
        }	

	public static String MD5(String value) throws Exception {
		if(md == null) {
			throw new Exception("MD5 algorithm is not available");
		}
		return 	BytesToHex(md.digest(value.getBytes()));
	}

	public static String StringToHex(String value) {
		return BytesToHex(value.getBytes());
	}

	private static String BytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for ( int j = 0; j < bytes.length; j++ ) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	/**
	* Calculate the absolute difference between two Date without
	* regard for time offsets
	*
	**/
	public static long getTimeDifferenceInSeconds(String d1, String fmt1, String d2, String fmt2) throws Exception {

	        SimpleDateFormat sfd1 = new SimpleDateFormat(fmt1);
        	SimpleDateFormat sfd2 = new SimpleDateFormat(fmt2);

	        Calendar cal = Calendar.getInstance();
        	cal.setTimeZone(TimeZone.getTimeZone("UTC"));
	        cal.setTime(sfd1.parse(d1));
	        long t1 = cal.getTimeInMillis();
        	cal.setTime(sfd2.parse(d2));
	        long diff = Math.abs(cal.getTimeInMillis() - t1);
        	return diff / 1000;
    	}

    	/**
	* Convert UTC timestamp to any Time Zone timestamp wit respect of DST
	*  dateTime - string timestamp to be converted (concidered in UTC Time Zone)
        *  format - format of dateTime string
	*  destTzId - desired destination time zone string id list of all ids :http://joda-time.sourceforge.net/timezones.html
	* time zone id should be country specific e.g. America/New_York, not general one e.g. EST in order to DST calculated correctly
	**/
	public static String convertFromUTC(String dateTime, String format, String destTzId){
	        String finalDateTimeString = dateTime;
        	DateTimeZone destTz = DateTimeZone.forID(destTzId);
	        DateTimeZone srcTz = DateTimeZone.UTC;

	        DateTimeFormatter formatter = DateTimeFormat.forPattern(format);
	        LocalDateTime localDateTime  = formatter.parseLocalDateTime(finalDateTimeString);
        	DateTime utcDateTime = new DateTime(srcTz).withDate(localDateTime.toLocalDate()).withTime(localDateTime.toLocalTime());

	        DateTime finalDateTime = utcDateTime.toDateTime(destTz);
        	return finalDateTime.toString(formatter);
    	}

	public static String dateOffset(String date, String format, String offset,
			String offsetFmt) throws Exception {
		String dateOffsetValue = null;
		try {
			Calendar calendar = new GregorianCalendar();
			SimpleDateFormat srcFormat = new SimpleDateFormat(format);
			calendar.setTime(srcFormat.parse(date));

			Calendar calendar2 = new GregorianCalendar();
			calendar2.setLenient(false);
			SimpleDateFormat offsetFormat = new SimpleDateFormat(offsetFmt);
			calendar2.setTime(offsetFormat.parse(offset.substring(1)));

			int multiplier = 1;
			switch (offset.charAt(0)) {
			case PLUS_SIGN:
				multiplier = 1;
				break;
			case MINUS_SIGN:
				multiplier = -1;
				break;
			default:
				throw new Exception(
						"Please specify the operation '+' or '-' in dateOffset value");
			}
			int hour = calendar2.get(Calendar.AM_PM) > 0 ? calendar2
					.get(Calendar.HOUR) + 12 : calendar2.get(Calendar.HOUR);
			calendar.add(Calendar.HOUR, multiplier * hour);
			calendar.add(Calendar.MINUTE,
					multiplier * calendar2.get(Calendar.MINUTE));
			calendar.add(Calendar.SECOND,
					multiplier * calendar2.get(Calendar.SECOND));
			calendar.add(Calendar.DATE,
					multiplier * calendar2.get(Calendar.DAY_OF_YEAR)
							- multiplier);

			SimpleDateFormat destFormatConvert = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			dateOffsetValue = destFormatConvert.format(calendar.getTime());

		} catch (ParseException ex) {
			throw new Exception(
					"Unable to parse the given date format", ex);
		} catch (Exception e) {
			throw new Exception(
					"Exception thrown while performing the dateOffset calculation",
					e);
		}
		return dateOffsetValue;
	}

	public static String dateFormatConvert(String date, String sFormat,
			String dFormat) throws Exception {
		SimpleDateFormat srcFormat = new SimpleDateFormat(sFormat);
		SimpleDateFormat destFormat = new SimpleDateFormat(dFormat);
		String datevalue = null;

		if ((date != null && !(date.trim().toLowerCase()
				.equalsIgnoreCase("null"))) && (!"".equals(date.trim()))) {
			try {
				datevalue = destFormat.format(srcFormat.parse(date));
			} catch (ParseException e) {
				try {
					datevalue = (destFormat.format(DateUtils.parseDate(date,
							DATE_PATTERNS))).toString();
				} catch (ParseException ex) {
					throw new Exception(
							"Exception thrown while converting the date format type which is not supported currently",
							e);
				}
			}
		} else {
			datevalue = DEFAULT_DATE_VALUE;
		}
		return datevalue;
	}
	
	public static String  round(String val, int roundNum ) throws Exception {
		try {
			BigDecimal a = new BigDecimal(val);
			return a.setScale(roundNum, BigDecimal.ROUND_HALF_EVEN).toString();
		} catch (Exception e) {
			throw new Exception(
					"Exception thrown while rounding the value which is not supported currently ",
					e);
		}
	}

	/**
	 * This method is used to get the maximum of two dates
	 * @param date1
	 * @param date2
	 * @param format
	 * @return
	 * @throws ParseException
	 */
	public static String maxDate(String date1, String date2, String format) throws ParseException
	{
		SimpleDateFormat incomingDateFormat = null;
		String retDate = "";
		String date1st = "";
		String time1st = "";
		String date2nd = "";
		String time2nd = "";
		String datetime1 = "";
		String datetime2 = "";
		Date dateObj1 = null;
	    Date dateObj2 = null;
		
		if (format!=null)
		{
			incomingDateFormat = new SimpleDateFormat(format);
		}
		else 
		{
		 throw new NullArgumentException("Argument format is null");	
		}
		
		if (StringUtils.countMatches(date1, "-")==1 && StringUtils.countMatches(date2, "-")==1)
		{
			date1st = date1.split("-")[0];
			time1st = date1.split("-")[1];
			date2nd = date2.split("-")[0];
			time2nd = date2.split("-")[1];
			datetime1 = date1st+time1st;
			datetime2 = date2nd+time2nd;
			dateObj1 = incomingDateFormat.parse(datetime1);
			dateObj2 = incomingDateFormat.parse(datetime2);
			if (dateObj1.compareTo(dateObj2)>0)
			{
				retDate = date1;
			}
			else 
			{
				retDate = date2;
			}
		}
		else 
		{
			dateObj1 = incomingDateFormat.parse(date1);
			dateObj2 = incomingDateFormat.parse(date2);
			if (dateObj1.compareTo(dateObj2)>0)
			{
				retDate = date1;
			}
			else 
			{
				retDate = date2;
			}
		}
		return retDate;
	}

	public static String dateFormatConvert(String date, String sFormat,
			String dFormat,String defaultValue) throws Exception {
		SimpleDateFormat srcFormat = new SimpleDateFormat(sFormat);
		SimpleDateFormat destFormat = new SimpleDateFormat(dFormat);
		String datevalue = null;
		if ((date != null && !(date.trim().toLowerCase()
				.equalsIgnoreCase("null"))) && (!"".equals(date.trim()))) {
			try {
				
				datevalue = destFormat.format(srcFormat.parse(date));
				
			} catch (ParseException e) {
				try {
					if ((date != null && !(date.trim().toLowerCase()
							.equalsIgnoreCase("null"))) && (!"".equals(date.trim()))) {
				
					datevalue = (destFormat.format(DateUtils.parseDate(date,
							DATE_PATTERNS))).toString();
				}
				} catch (ParseException ex) {
					throw new Exception(
							"Exception thrown while converting the date format type which is not supported currently",
							e);
				}
			}
		} else {
			datevalue = defaultValue;
		}
		//System.out.println("finally return value :" + datevalue);
		return datevalue;
	}

	public static void main(String[] args) throws ParseException {
		System.out.println(maxDate("20130402-000000", "20150730-124314", "yyyyMMddHHmmss"));
		System.out.println(maxDate("2013-04-02 00:00:00", "2015-07-30 12:43:14", "yyyy-MM-dd HH:mm:ss"));
		System.out.println("============ Testing time zone conversion");
		System.out.println("Converted from UTC to America/Mexico_City : 2016-04-03 07:00:01 ---> " + convertFromUTC("2016-04-03 07:00:01", "YYYY-MM-dd HH:mm:ss", "America/Mexico_City" ));
		System.out.println("Converted from UTC to America/Mexico_City : 2016-04-03 08:00:01 ---> " + convertFromUTC("2016-04-03 08:00:01", "YYYY-MM-dd HH:mm:ss", "America/Mexico_City" ));
		System.out.println("Converted from UTC to America/Mexico_City : 2016-04-03 09:00:01 ---> " + convertFromUTC("2016-04-03 09:00:01", "YYYY-MM-dd HH:mm:ss", "America/Mexico_City" ));
		System.out.println("Converted from UTC to America/Mexico_City : 2016-10-30 06:00:01 ---> " + convertFromUTC("2016-10-30 06:00:01", "YYYY-MM-dd HH:mm:ss", "America/Mexico_City" ));
		System.out.println("Converted from UTC to America/Mexico_City : 2016-10-30 07:00:01 ---> " + convertFromUTC("2016-10-30 07:00:01", "YYYY-MM-dd HH:mm:ss", "America/Mexico_City" ));
		System.out.println("Converted from UTC to America/Mexico_City : 2016-10-30 08:00:01 ---> " + convertFromUTC("2016-10-30 08:00:01", "YYYY-MM-dd HH:mm:ss", "America/Mexico_City" ));
		System.out.println("============ America/Santiago no DST since 2014");
		System.out.println("Converted from UTC to America/Santiago : 2014-04-27 01:00:01 ---> " + convertFromUTC("2014-04-27 01:00:01", "YYYY-MM-dd HH:mm:ss", "America/Santiago" ));
		System.out.println("Converted from UTC to America/Santiago : 2014-04-27 02:00:01 ---> " + convertFromUTC("2014-04-27 02:00:01", "YYYY-MM-dd HH:mm:ss", "America/Santiago" ));
		System.out.println("Converted from UTC to America/Santiago : 2014-04-27 03:00:01 ---> " + convertFromUTC("2014-04-27 03:00:01", "YYYY-MM-dd HH:mm:ss", "America/Santiago" ));
		System.out.println("Converted from UTC to America/Santiago : 2016-04-27 01:00:01 ---> " + convertFromUTC("2016-04-27 01:00:01", "YYYY-MM-dd HH:mm:ss", "America/Santiago" ));
		System.out.println("Converted from UTC to America/Santiago : 2016-04-27 02:00:01 ---> " + convertFromUTC("2016-04-27 02:00:01", "YYYY-MM-dd HH:mm:ss", "America/Santiago" ));
		System.out.println("Converted from UTC to America/Santiago : 2016-04-27 03:00:01 ---> " + convertFromUTC("2016-04-27 03:00:01", "YYYY-MM-dd HH:mm:ss", "America/Santiago" ));

	}
}
