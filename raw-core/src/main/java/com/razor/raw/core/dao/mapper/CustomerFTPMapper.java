package com.razor.raw.core.dao.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.razor.raw.core.dao.mapper.DataSourceMapper.dataSourceCols;
import com.razor.raw.core.pojo.CustomerFTP;
import com.razor.raw.core.pojo.Datasource;
import com.razor.raw.core.pojo.SftpDto;
public class CustomerFTPMapper implements RowMapper<CustomerFTP>{
	
	public enum CustomerFTPCols {
		RAW_CUSTOMER_FTP_ID, TENANT_ID, PRODUCT_ID, IS_FTP_ENABLED, IS_SFTP_ON, FTP_HOST, FTP_PORT, FTP_USER, FTP_PASSWORD, FTP_FOLDER 
	}
		
	private ResultSetMetaData columnMetaData = null;

	@Override
	public CustomerFTP mapRow(ResultSet rs, int rowNum) throws SQLException {
		CustomerFTP custFtp = new CustomerFTP();
		if (columnMetaData == null) {
			columnMetaData = rs.getMetaData();
		}
		if (columnMetaData != null) {
			for (int i = 1; i <= columnMetaData.getColumnCount(); i++) {
				String colName = columnMetaData.getColumnName(i);
				
				switch (CustomerFTPCols.valueOf(colName)) {
					case RAW_CUSTOMER_FTP_ID:
						custFtp.setCustomerFtpId(rs.getLong(colName));
						break;
					case TENANT_ID:
						custFtp.setTenantId(rs.getString(colName));
						break;
					case PRODUCT_ID:
						custFtp.setProductId(rs.getString(colName));
						break;
					case FTP_HOST:
						custFtp.setHost(rs.getString(colName));
						break;
					case FTP_USER:
						custFtp.setuName(rs.getString(colName));
						break;
					case FTP_PASSWORD:
						custFtp.setPassword(rs.getString(colName));
						break;
					case IS_SFTP_ON:
						if(rs.getString(colName) != null)
						custFtp.setSftpOn((rs.getString(colName)
								.equalsIgnoreCase("Y") ? true : false));
						break;
					case IS_FTP_ENABLED:
						if(rs.getString(colName) != null)
						custFtp.setFtpEnable((rs.getString(colName)
								.equalsIgnoreCase("Y") ? true : false));
						break;
					case FTP_FOLDER:
						custFtp.setFolder(rs.getString(colName));
						break;
					case FTP_PORT:
						custFtp.setPort(rs.getInt(colName));
						break;
					default:
						break;
				}
			}
		}
		return custFtp;
	}

}
