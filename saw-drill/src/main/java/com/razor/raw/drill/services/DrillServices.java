package com.razor.raw.drill.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.razor.raw.core.pojo.Datasource;
import com.razor.raw.core.pojo.DrillView;
import com.razor.raw.core.pojo.Report;


/**
 * @author sunil.belakeri
 *
 * 
 */
public interface DrillServices {
	
	
	/**
	 * 
	 */
	public Connection getConnection(Datasource dataSource);
	
	
	public void closeConnection(Connection connection,PreparedStatement preparedStatement,ResultSet resultSet);
	
	/**
	 * @param
	 * @param
	 * @param
	 */
	public ResultSet executeQueryByReport(Report report,int recordLimit,Connection connection) throws Exception;

	/**
	 * 
	 * @param report
	 * @param connection
	 * @return
	 */
	public long getTotalCount(Report report, Connection connection);


	ResultSet getViewList(String schemaName, Connection connection);


	ResultSet getViewColumns(DrillView drillView, Connection connection);

}
