package com.razor.raw.utility.common;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.razor.raw.core.common.CommonConstants;

public class CommonValidatorUtility {
	public static boolean isValidDrilDate(String strDate){
		SimpleDateFormat sdf = new SimpleDateFormat(CommonConstants.DRIL_DATE_PATTERN);
		boolean valid = false;
		if(strDate != null){
			try {
				Date date = sdf.parse(strDate);
				valid = true;
			} catch (Exception e) {
				valid = false;
			}
		}
		return valid;
	}
	
	public static boolean isValidNumber(String num){
		try {
			BigDecimal bigDecimal = new BigDecimal(num);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	public static boolean isValidInteger(String num){
		try {
			int num1 = Integer.parseInt(num);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	
	public static boolean isValidDateRange(String dateRangeStr){
		String[] dateRange = dateRangeStr.split("\\|\\|\\|");
		return (isValidDrilDate(dateRange[0]) && isValidDrilDate(dateRange[1]));
	}
	
	
	public static boolean isValidNumberRange(String dateRangeStr){
		String[] dateRange = dateRangeStr.split("\\|\\|\\|");
		return (isValidNumber(dateRange[0]) && isValidNumber(dateRange[1]));
	}
	
	
	public static boolean isValidIntegerRange(String dateRangeStr){
		String[] dateRange = dateRangeStr.split("\\|\\|\\|");
		return (isValidInteger(dateRange[0]) && isValidInteger(dateRange[1]));
	}
	
	
	
}
