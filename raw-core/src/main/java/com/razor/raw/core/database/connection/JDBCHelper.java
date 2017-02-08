package com.razor.raw.core.database.connection;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.razor.raw.core.common.CommonConstants;
import com.razor.raw.core.pojo.Datasource;


public class JDBCHelper {
	
	static Properties properties = null;
	
	public static Connection getConnection(Datasource dataSourceAttribute) throws SQLException {
		
		if(CommonConstants.DB_TYPE_SQLSERVER.equalsIgnoreCase(dataSourceAttribute.getDbType()))
		{
			
			return null;
		}
		
		else if(CommonConstants.DB_TYPE_ORACLE.equalsIgnoreCase(dataSourceAttribute.getDbType()))
		{
			return null;
		}else if(CommonConstants.DB_TYPE_NETEZZA.equalsIgnoreCase(dataSourceAttribute.getDbType()))
		{
			return null;
		}else if(CommonConstants.DB_TYPE_MYSQL.equalsIgnoreCase(dataSourceAttribute.getDbType()))
		{
			/*MysqlConnectionPoolDataSource mysqlDatasource = new MysqlConnectionPoolDataSource();
			mysqlDatasource.setUser(dataSourceAttribute.getUsername());
			mysqlDatasource.setPassword(dataSourceAttribute.getPassword());
			mysqlDatasource.setServerName(mysqlDatasource.getServerName());
			mysqlDatasource.setDatabaseName(dataSourceAttribute.getDbName());
			mysqlDatasource.setPort(Integer.valueOf(mysqlDatasource.getPort()));
			return mysqlDatasource.getConnection();*/
			return null;
		}
		return null;
	}
	
	public static Connection getRRMDataConnection()
	{
		Connection connection = null;
		if(properties == null)
			properties = loadProperties();
		Datasource datasource = new Datasource();
		datasource.setDbType(properties.getProperty("rrm.db.dbType"));
		datasource.setServerName(properties.getProperty("rrm.db.serverName"));
		datasource.setDbName(properties.getProperty("rrm.db.dbName"));
		datasource.setPort(properties.getProperty("rrm.db.port"));
		datasource.setUsername(properties.getProperty("rrm.db.username"));
		datasource.setPassword(properties.getProperty("rrm.db.password"));
		datasource.setDriverClass(properties.getProperty("rrm.db.driverClass"));
		try {
			connection =  getConnection(datasource);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return connection;
	}
	private static Properties loadProperties() {
		Properties properties = new Properties();
		try {
			//properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("/config/db.properties"));
			properties.load(new FileInputStream("D:\\Work\\codebase\\git\\RIR\\ReportCore\\src\\main\\resources\\config\\db.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}

/*	public static void main(String[] args) {
		Datasource datasource = new Datasource();
		datasource.setDbType(CommonConstants.DB_TYPE_MYSQL);
		datasource.setServerName("localhost");
		datasource.setDbName("rir");
		datasource.setPort("3306");
		datasource.setUsername("root");
		datasource.setPassword("");
		
		try {
			Connection connection = getConnection(datasource);
			System.out.println(connection);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(getRRMDataConnection());
	}*/
}