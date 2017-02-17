/*
 * FILE            : LogManager.java
 *
 * PACKAGE         : package com.nisco.sf.util.logger;
 *
 * AUTHOR          : A.Chandra Prasad Reddy - chandra.reddy@niscompany.com
 *
 * DATE            : Mar 7, 2005
 *
 * VERSION         : 1.0
 *
 * ABSTRACT        : A class that wraps the Log4J API.<p>
 *
 * This class is basically a thin wrapper over Log4j, so if
 * you understand how to use Log4j, you should understand how
 * to use this logger.<p>
 *
 * <pre>
 * Example Usage:
 *     // if only message has to be logged
 *     LogManager.log( LogManager.CATEGORY_ADRAP,
 *                      LogManager.PRIORITY_DEBUG,
 *                      "This is my log message.");
 *
 *     // if message along with an exception has to be logged
 *     LogManager.log( LogManager.CATEGORY_ADRAP,
 *                      LogManager.PRIORITY_DEBUG,
 *                      "This is my log message.", ex);
 *
 * </pre>
 *
 * <li>The log4j is configured using the log4j.properties file in the classpath.
 * <li>Make sure to put the log4j.properties file in the classpath.
 *
 * See the <a href="http://jakarta.apache.org/log4j/docs/manual.html">
 * Log4J manual</a> for more detail.<p>
 *
 * @see org.apache.log4j
 *
 * HISTORY         :
 *                  creddy [Mar 7, 2005] - Created first cut of code.
 *
 */

package com.razor.raw.logging;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


public class LogManager {

    /**
     * The constant which maps to Priority.DEBUG priority.
     */
    public static final int LEVEL_DEBUG = 1;

    /**
     * The constant which maps to Priority.INFO priority.
     */
    public static final int LEVEL_INFO = 2;

    /**
     * The constant which maps to Priority.WARN priority.
     */
    public static final int LEVEL_WARN = 3;

    /**
     * The constant which maps to Priority.ERROR priority.
     */
    public static final int LEVEL_ERROR = 4;

    /**
     * The constant which maps to Priority.FATAL priority.
     */
    public static final int LEVEL_FATAL = 5;

    /**
     * The default log category
     */
    public static final String CATEGORY_DEFAULT = "RRM";
    
    public static final String CATEGORY_ES = "ES";

    public static final String CATEGORY_DATABASE = "DB";

    // Category added for SSO related logs
    public static final String CATEGORY_SSO = "SSO";
    
    
    public static final String CATEGORY_REPORT = "REPORT";
    public static final String CATEGORY_EMAIL = "EMAIL";

    public static final String CATEGORY_DAO = "DAO";
    
    private static LogManager logManager = new LogManager();

    /**
     * A private constructor to restrict the user from creating the instance of
     * this class.
     */
    private LogManager() {
        try {
			initializeLog4j();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static LogManager getInstance(){
    	if(logManager == null){
    		logManager = new LogManager();
    	}
    	return logManager;
    }

    /**
     * The method to initialize the log4j. This method makes use of the
     * log4j.properties file, so, make sure to put the file in the classpath.
     * @throws Exception 
     */
    @SuppressWarnings("deprecation")
	private void initializeLog4j() throws Exception {

        try {
            // Now loading the properties of log4j from the properties file
            // specified.
            InputStream inputStream = null;
            // Now loading the properties of log4j from the properties file
            // specified.
            URL url = Thread.currentThread().getContextClassLoader().getResource(
                            LogConstants.LOG4J_PROPERTIES);

            if( url == null) {
                url = (new File( LogConstants.LOG4J_PROPERTIES)).toURL();
            }
            inputStream = url.openStream();

            Properties logProperties = new Properties();
            logProperties.load( inputStream);
            // Initializing log4j with the properties.
            PropertyConfigurator.configure( logProperties);
        }
        catch( IOException ex) {
            throw new Exception( ex.toString());
        }
        catch( NullPointerException ex) {
            throw new Exception( ex.toString());
        }

    }

    /**
     * The proxy method to log4j.log() method. The idea is that all the classes
     * should pass on the message to this method to be logged in log4j. Message
     * is mandatory. If category is null then root category will be used to log
     * the message. If priority is not valid then priority equivalent to DEBUG
     * is used.
     * 
     * @param String
     *            Category of the message.
     * @param int
     *            Priority of the message like LogManager.DEBUG etc.
     * @param String
     *            Message
     */
	public static void log(String category, int priority, String message) {
		log(category, priority, message.trim(), null);
	}

    /**
     * The proxy method to log4j.log() method. The idea is that all the classes
     * should pass on the message to this method to be logged in log4j. Message
     * and priority are mandatory. If category is null then root category will
     * be used to log the message. If priority is not valid then priority
     * equivalent to DEBUG is used.This is an overloaded method to accept the
     * Throwable exception object also and log the exception.
     * 
     * @param String
     *            Category of the message.
     * @param int
     *            Priority of the message like LogManager.DEBUG etc.
     * @param String
     *            Message
     * @param Throwable
     */
    public static void log( String category, int level, String message, Throwable ex) {

        if( message == null) {

            return;
        }
        Category tempCat = null;

        Level tempPriority = getLevel( level);

        if( tempPriority != null) {

            tempCat = getCategory( category);

            if( tempCat.isEnabledFor( tempPriority)) {

                tempCat.log( tempPriority, message.trim(), ex);
            }
        }
        // to avoid memory leak
        tempCat = null;
        tempPriority = null;
    }

    /**
     * Helper method to get the Category object corresponding to category name.
     * 
     * @param String
     *            The category name.
     * @return Category
     */
    private static Logger getCategory( String category) {

        Logger retVal = Logger.getLogger( category);

        if( category == null) {

            retVal = Logger.getRootLogger();
        }

        return retVal;

    }

    /**
     * Helper method to get the Priority object corresponding to priority name.
     * 
     * @param String
     *            The name of the priority.
     * @return Priority
     */
    private static Level getLevel( int level) {

        Level retVal = null;

        switch( level) {
            case LEVEL_DEBUG:
                retVal = Level.DEBUG;
                break;
            case LEVEL_INFO:
                retVal = Level.INFO;
                break;
            case LEVEL_WARN:
                retVal = Level.WARN;
                break;
            case LEVEL_ERROR:
                retVal = Level.ERROR;
                break;
            case LEVEL_FATAL:
                retVal = Level.FATAL;
                break;
            default:
                retVal = Level.DEBUG;
                break;
        }

        return retVal;
    }
    
    /**
     * This method is used to stream stackTrace to log file
     * @return String : printStackTraces
     * @throws Exception 
     */
	public static String printStackTrace(Exception e) {
		StringWriter writerStr = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writerStr);
		e.printStackTrace(printWriter);
		String stackTraceStr = writerStr.toString();
		try {
			writerStr.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		printWriter.close();
		return "  :"+stackTraceStr;
	}
}
