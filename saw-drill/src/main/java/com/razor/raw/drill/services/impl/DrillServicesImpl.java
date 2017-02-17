package com.razor.raw.drill.services.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.drill.jdbc.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.razor.raw.core.common.CommonConstants;
import com.razor.raw.core.pojo.Datasource;
import com.razor.raw.core.pojo.DrillView;
import com.razor.raw.core.pojo.Parameter;
import com.razor.raw.core.pojo.Report;
import com.razor.raw.drill.services.DrillServices;


/**
 * @author sunil.belakeri
 *
 * 
 */
@Service
public class DrillServicesImpl implements DrillServices{
	private static final Logger logger = LoggerFactory.getLogger(DrillServicesImpl.class);
	SimpleDateFormat simpleDateFormatDrill = new SimpleDateFormat(CommonConstants.DRIL_DATE_PATTERN);
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CommonConstants.DATE_PATTERN);

	/**
	 * @param Datasource
	 */
	public Connection getConnection(Datasource dataSource) {
		logger.debug("DrillServicesImpl - getConnection - START");
		logger.debug("DrillServicesImpl - getConnection -dataSource :" +dataSource);
		logger.debug("DrillServicesImpl - getConnection - server name-"+ dataSource.getServerName());
		logger.debug("DrillServicesImpl - getConnection - port- "+ dataSource.getPort());
		String connectionTypeString = "";
		
		if (dataSource.getConnectionType()!=null && dataSource.getConnectionType().equalsIgnoreCase("zookeeper"))
		{
			connectionTypeString = "zk=" + dataSource.getServerName()
				+ CommonConstants.DELIMITER_COLON + dataSource.getPort();
		}
			// default connection type :: Direct drillBit connection
		else 
			{
			connectionTypeString = "drillbit="+dataSource.getServerName()
		        +CommonConstants.DELIMITER_COLON + dataSource.getPort(); 
			}
		String schemaName = "schema=" + dataSource.getSchemaName();
		
		String dataLakeUrl = CommonConstants.DATA_LAKE_CONNECTION_PREFIX
				+ CommonConstants.DELIMITER_COLON + schemaName
				+ CommonConstants.DELIMITER_SEMI_COLON
				+ connectionTypeString;	
		//
		logger.debug("DrillServicesImpl - getConnection - schema name: "+schemaName);
		logger.debug("DrillServicesImpl - getConnection - data lake url: "+dataLakeUrl);
		logger.debug("DrillServicesImpl - getConnection - user name :" +dataSource.getUsername());
		logger.debug("DrillServicesImpl - getConnection - password :" +dataSource.getPassword());
		Connection connection = null;
		try {
			Class.forName("org.apache.drill.jdbc.Driver");
			connection = DriverManager.getConnection(dataLakeUrl , dataSource.getUsername(),dataSource.getPassword());	
		} catch (SQLException e) {
			logger.error("DrillServicesImpl - getConnection -Issue while getting connection to Apache DRILL with URL "+dataLakeUrl, e);
		} catch (ClassNotFoundException e) {
			logger.error("DrillServicesImpl - getConnection -Issue while getting connection to Apache DRILL with URL "+dataLakeUrl, e);
			e.printStackTrace();
		}
		logger.debug("DrillServicesImpl - getConnection - getConnection - end");
		return connection;
	}

	/* (non-Javadoc)
	 * @see com.razor.raw.drill.services.DrillServices#closeConnection()
	 */
	public void closeConnection(Connection connection,PreparedStatement preparedStatement,ResultSet resultSet) {
		
		if (resultSet != null) {
	        try {
	        	resultSet.close();
	        } catch (SQLException e) {
	        	logger.debug("Not able to close resultSet ",e);
	        	e.printStackTrace();
	        }
	    }
	    if (preparedStatement != null) {
	        try {
	        	preparedStatement.close();
	        } catch (SQLException e) {
	        	logger.debug("Not able to close prepared statement ");
	        	e.printStackTrace();
	        }
	    }
	    if (connection != null) {
	        try {
	        	connection.close();
	        } catch (SQLException e) {
	        	logger.debug( "Not able to close prepared statement ");
	        	e.printStackTrace();
	        }
	    }
		
		
	}

	/**
	 * @see com.razor.raw.drill.services.DrillServices#executeQueryByReport()
	 * @param Report
	 * @param recordLimit
	 * @param connection
	 * 
	 */
	public ResultSet executeQueryByReport(Report report, int recordLimit, Connection connection) throws Exception{
		logger.debug("DrillServicesImpl - executeQueryByReport - START");
		/*
		 * Get all NULL
		 * Get all booleans
		 * 
		 */
				
		PreparedStatement prepareStatement = null;
		ResultSet resultSetObject = null;
		try{
			String queryType = report.getReportQueryType();
			String reportSQL = report.getReportQuery();
			reportSQL = reportSQL.replaceAll("<BR/>", " ");
			reportSQL = reportSQL.replaceAll("<br/>", " ");
			reportSQL = reportSQL.concat(" LIMIT "+recordLimit);
			logger.debug("DrillServicesImpl - executeQueryByReport - Query:"+reportSQL);
			logger.debug("DrillServicesImpl - executeQueryByReport - Records Limit:"+recordLimit);
			if(report.getParameters() != null){
				
				//Sort parameters on INDEX
				SortedMap<Integer,Parameter> sortedParams = new TreeMap<Integer,Parameter>();
			
				for(Parameter param:report.getParameters().getParameter()){
					sortedParams.put(param.getIndex(), param);
				}
				
				for (Map.Entry<Integer,Parameter> paramMapValue:sortedParams.entrySet()) {
					reportSQL=reCreateQueryWithParamater(reportSQL,paramMapValue.getValue());
					logger.debug("DrillServicesImpl - executeQueryByReport - reportSQL : "+reportSQL);
					if(reportSQL==null){
						logger.debug("DrillServicesImpl - executeQueryByReport - Exception occured while executing query on Drill");
						return null;
					}
				}
			}
			logger.debug("DrillServicesImpl - executeQueryByReport - Query Execution Started");
			prepareStatement = connection.prepareStatement(reportSQL ,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			resultSetObject = prepareStatement.executeQuery();
			logger.debug("DrillServicesImpl - executeQueryByReport - Query Execution Completed");
		}catch(Exception e){
			logger.error("DrillServicesImpl - executeQueryByReport -Exception " , e);
			throw e;
		}

		logger.debug("DrillServicesImpl - executeQueryByReport - END");
		return resultSetObject;
	}

	/**
	 * @see com.razor.raw.drill.services.DrillServices#executeQueryByReport()
	 * @param schemaName
	 * @param connection
	 * 
	 */
	public ResultSet getViewList(String schemaName, Connection connection){
		logger.debug("DrillServicesImpl - getViewList - START");
		PreparedStatement prepareStatement = null;
		ResultSet resultSetObject = null;
		try{
			//schemaName = "mba.default";
			String reportSQL = "select * from INFORMATION_SCHEMA.`TABLES` WHERE TABLE_SCHEMA='"+schemaName+"' AND TABLE_TYPE ='VIEW'";
			logger.debug("DrillServicesImpl - getViewList - Query:"+reportSQL);
			logger.debug("DrillServicesImpl - getViewList - Query Execution Started");
			prepareStatement = connection.prepareStatement(reportSQL ,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			resultSetObject = prepareStatement.executeQuery();
			logger.debug("DrillServicesImpl - getViewList - Query Execution Completed");
		}catch(Exception e){
			logger.error("DrillServicesImpl - getViewList -Exception " , e);
		}
		logger.debug("DrillServicesImpl - getViewList - END");
		return resultSetObject;
	}

	public ResultSet getViewColumns(DrillView drillView, Connection connection) {
		logger.debug("DrillServicesImpl - getViewColumns - START");
		PreparedStatement prepareStatement = null;
		ResultSet resultSetObject = null;
		try{
			String reportSQL = "select * from INFORMATION_SCHEMA.`COLUMNS` WHERE TABLE_NAME ='"+drillView.getViewName()+"' and table_schema='"+drillView.getTableSchema()+"'";
			logger.debug("DrillServicesImpl - getViewColumns - Query:"+reportSQL);
			logger.debug("DrillServicesImpl - getViewColumns - Query Execution Started");
			prepareStatement = connection.prepareStatement(reportSQL ,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			resultSetObject = prepareStatement.executeQuery();
			logger.debug("DrillServicesImpl - getViewColumns - Query Execution Completed");
			
		}catch(Exception e){
			logger.error("DrillServicesImpl - getViewColumns -Exception " , e);
		}
		logger.debug("DrillServicesImpl - executeQueryByReport - END");
		return resultSetObject;
	}
	
	public long getTotalCount(Report report, Connection connection) {
		logger.debug("DrillServicesImpl - getTotalCount - START");
		PreparedStatement prepareStatement = null;
		ResultSet resultSetObject = null;
		long totalCount=0;
		try{
			
			String queryType = report.getReportQueryType();
			String reportSQL = report.getReportQuery();
			reportSQL = reportSQL.replaceAll("<BR/>", " ");
			reportSQL = reportSQL.replaceAll("<br/>", " ");
			
			
			reportSQL = "SELECT COUNT(1) AS TOTAL_COUNT FROM ( " + reportSQL + " ) RRM_TEMP";
			logger.debug("DrillServicesImpl - getTotalCount -  Query : "+reportSQL);
			
			
			if(report.getParameters() != null){
				
				//Sort parameters on INDEX
				SortedMap<Integer,Parameter> sortedParams = new TreeMap<Integer,Parameter>();
			
				for(Parameter param:report.getParameters().getParameter()){
					sortedParams.put(param.getIndex(), param);
				}
				
				for (Map.Entry<Integer,Parameter> paramMapValue:sortedParams.entrySet()) {
					reportSQL=reCreateQueryWithParamater(reportSQL,paramMapValue.getValue());
					if(reportSQL==null){
						logger.debug( "DrillServicesImpl - getTotalCount - Exception occured while executing query on Drill in "+ this.getClass().getName());
						return 0;
					}
				}
			}
			
			if(reportSQL!=null){
				logger.debug("DrillServicesImpl - getTotalCount -  getTotalCount Query Execution Started : "+reportSQL );
				
			}
			
			prepareStatement = connection.prepareStatement(reportSQL ,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			resultSetObject = prepareStatement.executeQuery();
			while(resultSetObject.next()){
				totalCount=resultSetObject.getInt("TOTAL_COUNT");
			}
			logger.debug("DrillServicesImpl - getTotalCount -  getTotalCount Query Execution Completed");

		}catch(Exception e){
			logger.error("DrillServicesImpl - getTotalCount - Exception", e);
		}
		finally
		{
			if (resultSetObject != null) {
		        try {
		        	resultSetObject.close();
		        } catch (SQLException e) {
		        	logger.debug("Not able to close resultSet ",e);
		        	e.printStackTrace();
		        }
		    }
		    if (prepareStatement != null) {
		        try {
		        	prepareStatement.close();
		        } catch (SQLException e) {
		        	logger.debug("Not able to close prepared statement ");
		        	e.printStackTrace();
		        }
		    }
			
		}
		
		logger.debug("DrillServicesImpl - getTotalCount - END - totalCount : "+totalCount);
		return totalCount;
		
		
	}
	private boolean isValidSimpleDate(String strDate){
		SimpleDateFormat sdf = new SimpleDateFormat(CommonConstants.SIMPLE_DATE_PATTERN);
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
	
	
	/*
	 * Takes a query String and substitutes supplied parameter at a given index.
	 * @param reportSQL
	 * @param param
	 * @return
	 */
			
	private String reCreateQueryWithParamater(String reportSQL, Parameter param) throws Exception{

		String value = null;

		if (param.getValue() != null) {
			value = param.getValue();
		} 
		if (value != null) {

			if (CommonConstants.SQL_STRING_TYPE.equalsIgnoreCase(param
					.getType())) {
				value = CommonConstants.SINGLE_QUOTES + value
						+ CommonConstants.SINGLE_QUOTES;
			}else if (CommonConstants.SQL_SIMPLE_DATE_TYPE.equalsIgnoreCase(param
					.getType())) {
				if(!isValidSimpleDate(value)){
					throw new Exception("Invalid data type in the parameter list");
				}
					
				value = CommonConstants.SINGLE_QUOTES + value
						+ CommonConstants.SINGLE_QUOTES;
			}else if (CommonConstants.SQL_DATE_RANGE_TYPE
					.equalsIgnoreCase(param.getType())
					|| CommonConstants.SQL_NUMBER_RANGE_TYPE
							.equalsIgnoreCase(param.getType())) {
				if (value != null) {
					String[] numRange = value.split("\\|\\|\\|");
					String queryNum = "  " + numRange[0] + " AND "
							+ numRange[1];
					value = queryNum;
				}
			} else if (CommonConstants.SQL_INTEGER_TYPE.equalsIgnoreCase(param
					.getType())
					|| CommonConstants.SQL_NUMBER_TYPE.equalsIgnoreCase(param
							.getType())
					|| CommonConstants.SQL_DATE_TYPE.equalsIgnoreCase(param
							.getType())) {
				if(!isValidNumber(value)){
					throw new Exception("Invalid data type in the parameter list");
				}
				value = value;
			} else if (CommonConstants.SQL_BOOLEAN_TYPE.equalsIgnoreCase(param
					.getType())) {
				// TODO: Need to implement
			}
		}

		else {
			logger.debug("One of the given paramater is NULL and hence can't proceed further in "
					+ this.getClass().getName());
			return null;
		}

		return reportSQL.replaceFirst("\\?", value);

	}
	
	
	public String getDrillDatePattern(Date date1)
	{
		
		return (date1 == null) ? null : simpleDateFormatDrill.format(date1);
	}
	
	public Date getStringToDate(String dateVal)
	{
		try {
			return (StringUtils.isEmpty(dateVal)) ? null : simpleDateFormat.parse(dateVal);
		} catch (ParseException e) {
			logger.error("DrillServicesImpl - getStringToDate - error ",e);
			return null;
		}
		
	}
	
	public static void main(String args[]){
	       System.out.println("Sheth----------------------------------------------------------");
	              //System.out.println(System.getProperties().get("key-one"));
	              Datasource dataSource = new Datasource();
	              System.out.println("Sheth----------------------------------------------------------");
	              //dataSource.setDbType(CommonConstants.DB_TYPE_DATALAKE);

	              dataSource.setPort("5181");
	              System.out.println("Sheth----------------------------------------------------------");
	              dataSource.setSchemaName("mba.mba");

	              dataSource.setServerName("vm-maprdev01-dc.razorsight.com");
	              System.out.println("Sheth----------------------------------------------------------");
	              dataSource.setUsername("admin");
	              dataSource.setPassword("admin");

	              DrillServices dl= new DrillServicesImpl();
	              System.out.println("Sheth----------------------------------------------------------");
	              Connection conn= dl.getConnection(dataSource);
	              System.out.println("Shwetha----------------------------------------------------------1");
	              ResultSet rs = null;
	              //Report rp = new Report();
	              try{
	              //rp.setReportQuery("SELECT * FROM cp.`employee.json` LIMIT 10");
	              //rp.setReportQueryType("VIEW");
	              Statement stmt = conn.createStatement();
	         /* Perform a select on data in the classpath storage plugin. */
	         String sql = "select employee_id, first_name,last_name,salary FROM cp.`employee.json` where salary > 30000 and position_id=2";
	         rs = stmt.executeQuery(sql);
	         System.out.println("EmployeeID" + " "+ "First Name " +"Last Name " + " "+ "Salary");
	         System.out.println("-------------------------------------------------------------");
	       
	              
	        
	         while(rs.next()) {
	                 int employeeId  = rs.getInt("employee_id");
	               String firstName = rs.getString("first_name");
	               String lastName = rs.getString("last_name");
	               String salary = rs.getString("salary");
	               System.out.println(employeeId+ ":  "+ firstName+ " :  "+ lastName + " :  "+ salary);
	         }
	        }catch (Exception e){
	              e.printStackTrace();
	        }
	       /**    Parameters params = new Parameters();
	              List<Parameter> paramsList = new ArrayList<Parameter>();
	              
	              
	              Parameter param= new Parameter();
	              
	              param.setIndex(4);
	              param.setName("VENDOR_NAME");
	              param.setType(CommonConstants.SQL_STRING_TYPE);
	              param.setValue("AT&T-ATSLT");
	              
	              
	              Parameter param2= new Parameter();
	              
	              param2.setIndex(0);
	              param2.setName("VENDOR_NAME");
	              param2.setType(CommonConstants.SQL_BOOLEAN_TYPE);
	              param2.setValue("true");
	              
	              Parameter param3= new Parameter();
	              
	              param3.setIndex(1);
	              param3.setName("VENDOR_NAME");
	              param3.setType(CommonConstants.SQL_NUMBER_TYPE);
	              param3.setValue(null);
	              
	              Parameter param4= new Parameter();
	              
	              param4.setIndex(2);
	              param4.setName("VENDOR_NAME");
	              param4.setType(CommonConstants.SQL_INTEGER_TYPE);
	              param4.setValue("435672");
	              
	              
	              paramsList.add(param);
	              paramsList.add(param2);
	              paramsList.add(param3);
	              paramsList.add(param4);
	              
	              params.setParameter(paramsList);
	              rp.setParameters(params);**/
	              //System.out.println("Shwetha----------------------------------------------------------2");
	              //System.out.println(dl.getTotalCount(rp, conn));
	              
	              
	              //ResultSet rs=dl.executeQueryByReport(rp, 10, conn);
	              //System.out.println("Shwetha----------------------------------------------------------3");
	              //try {
	              //     while(rs.next()){
	              //            System.out.println(rs.getString(1));
	              //     }
	              //} catch (SQLException e) {
	                     // TODO Auto-generated catch block
	              //     e.printStackTrace();
	              //}
	              
	              dl.closeConnection(conn, null, rs);
	              
	       }

}
