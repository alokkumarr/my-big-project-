/**
 * 
 */
package com.razor.raw.logging;

/**
 * 
 * @author surendra.rajaneni
 *
 */
public class LogConstants {
	// qualified package path name for report content tree
	public final static String JAXB_PACKAGE_CONTENT="com.app.razor.report.process";
	
	// SQL Server 2008 database buffering technique while fetching the data from db
	public final static String SQL_SERVER_BUFFERING="adaptive";
	
	// Inventory Analytics SQL DataType
	public final static String SQL_DATE_TYPE="Datetime";
	public final static String SQL_STRING_TYPE="String";
	public final static String SQL_NUMERIC_TYPE="Numeric";
	public final static String SQL_INTEGER_TYPE="Integer";
	public final static String SQL_DOUBLE_TYPE="Double";
	public final static String SQL_LONG_TYPE="Long";
	public final static String SQL_DATE_TIME="Datetime";
	public final static String CURRENCY="Currency";
	
	
	public final static String DATE_PATTERN="MM/dd/yyyy";
	public final static String DATETIME_PATTERN="MM/dd/yyyy";
	public final static String DATETIME_PATTERN_COLUMN="MM/dd/yyyy";
	public final static String DOLLAR_SIGN="$";
	public final static String DECIMAL_PATTERN="###,###,###,##.##";
	public static final String LOG4J_PROPERTIES = "log4j/log4j.properties";
	
		
	public static final String REPORT = "REPORT";
	public static final String REPORT_PRODUCT_ID = "RRM";
	
	public static final String DB_TYPE_ORACLE = "oracle";
	public static final String DB_TYPE_SQLSERVER = "sqlserver";
	public static final String DB_TYPE_NETEZZA = "netezza";
	public static final String DB_TYPE_MYSQL = "mysql";
	
	public static final String CSV_FILE_FORMAT = ".csv";
	
	public static final String DATA_LAKE_CONNECTION_PREFIX="jdbc:drill";
	public static final String DELIMITER_COLON=":";
	public static final String DELIMITER_SEMI_COLON=";";
}
