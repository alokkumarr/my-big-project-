package com.razor.raw.core.dao.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.razor.raw.core.pojo.Datasource;

public class DataSourceMapper implements RowMapper<Datasource>{
	public enum dataSourceCols {
		RAW_DATA_SOURCE_ID, TENANT_ID, PRODUCT_ID, DBTYPE, SERVER_NAME, DB_NAME, SCHEMA_NAME, USER_NAME, PASSWORD, PORT, DRIVER_CLASS, CUSTOMER_FTP_ID,CONNECTION_TYPE
	};
	
	private ResultSetMetaData columnMetaData = null;
	
	@Override
	public Datasource mapRow(ResultSet rs, int rowNum) throws SQLException {
		Datasource datasource = new Datasource();
		if (columnMetaData == null) {
			columnMetaData = rs.getMetaData();
		}
		if (columnMetaData != null) {
			for (int i = 1; i <= columnMetaData.getColumnCount(); i++) {
				String colName = columnMetaData.getColumnName(i);
				
				switch (dataSourceCols.valueOf(colName)) {
					case RAW_DATA_SOURCE_ID:
						datasource.setDataSourceId(rs.getLong(colName));
						break;
					case TENANT_ID:
						datasource.setTenantID(rs.getString(colName));
						break;
					case PRODUCT_ID:
						datasource.setProductID(rs.getString(colName));
						break;
					case DBTYPE:
						datasource.setDbType(rs.getString(colName));
						break;
					case SERVER_NAME:
						datasource.setServerName(rs.getString(colName));
						break;
					case DB_NAME:
						datasource.setDbName(rs.getString(colName));
						break;
					case SCHEMA_NAME:
						datasource.setSchemaName(rs.getString(colName));
						break;
					case USER_NAME:
						datasource.setUsername(rs.getString(colName));
						break;
					case PASSWORD:
						datasource.setPassword(rs.getString(colName));
						break;
					case PORT:
						datasource.setPort(rs.getString(colName));
						break;
					case DRIVER_CLASS:
						datasource.setDriverClass(colName);
						break;
					case CONNECTION_TYPE:
						datasource.setConnectionType(rs.getString(colName));
						break;
					default:
						break;
				}
			}
		}
		return datasource;
	}
}



