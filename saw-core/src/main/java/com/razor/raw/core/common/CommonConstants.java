/**
 * 
 */
package com.razor.raw.core.common;

/**
 * @author saurav.paul
 *
 */
public class CommonConstants {
	// qualified package path name for report content tree
	public final static String JAXB_PACKAGE_CONTENT="com.app.razor.report.process";
	
	// SQL Server 2008 database buffering technique while fetching the data from db
	public final static String SQL_SERVER_BUFFERING="adaptive";
	
	// Inventory Analytics SQL DataType
	public final static String SQL_DATE_TYPE="DATETIME";
	public final static String SQL_DATE_RANGE_TYPE="DATE_RANGE";
	public final static String SQL_STRING_TYPE="STRING";
	public final static String SQL_SIMPLE_DATE_TYPE="DATE";
	public final static String SQL_NUMBER_TYPE="NUMBER";
	public final static String SQL_NUMBER_RANGE_TYPE="NUMBER_RANGE";
	public final static String SQL_INTEGER_TYPE="INTEGER";
	public final static String SQL_INTEGER_RANGE_TYPE="INTEGER_RANGE";
	
	
	public final static String CURRENCY="Currency";
	public final static String SIMPLE_DATE_PATTERN="yyyy-MM-dd";
	public final static String DATE_PATTERN="MM/dd/yyyy";
	public final static String DRIL_DATE_PATTERN="yyyyMMdd";
	public final static String DATETIME_PATTERN="MM/dd/yyyy";
	public final static String DATETIME_PATTERN_LOG="MM/dd/yyyy HH:mm:ss";
	public final static String DATETIME_PATTERN_COLUMN="MM/dd/yyyy";
	public final static String DOLLAR_SIGN="$";
	public final static String DECIMAL_PATTERN="###,###,###,##.##";
	public static final String LOG4J_PROPERTIES = "resources/log4j/log4j.properties";
	
		
	public static final String REPORT = "REPORT";
	public static final String REPORT_PRODUCT_ID = "RRM";
	
	public static final String DB_TYPE_ORACLE = "oracle";
	public static final String DB_TYPE_SQLSERVER = "sqlserver";
	public static final String DB_TYPE_NETEZZA = "netezza";
	public static final String DB_TYPE_MYSQL = "mysql";
	
	public static final String CSV_FILE_FORMAT = ".csv";
	public static final String XLSX_FILE_FORMAT = ".xlsx";
	public static final String XLS_FILE_FORMAT = ".xls";
	
	public static final String CSV_DB_FORMAT = "CSV";
	public static final String XLSX_DB_FORMAT = "EXCEL";
	
	
	public static final String DATA_LAKE_CONNECTION_PREFIX="jdbc:drill";
	public static final String DELIMITER_COLON=":";
	public static final String DELIMITER_SEMI_COLON=";";
	
	public static final String SINGLE_QUOTES="'";
	public static final String SQL_BOOLEAN_TYPE="boolean";

	public static final String TRUE_IND="Y";
	public static final String FALSE_IND="N";
	
	public static final String DATA_SPLITER_REGEX="\\|\\|\\|";
	public static final String DATA_SPLITER="|||";
	
}
