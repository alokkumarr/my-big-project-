package com.razor.raw.core.dao.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.razor.raw.core.common.util.DateUtil;
import com.razor.raw.core.pojo.PublishedReport;

public class PublishedReportMapper implements RowMapper<PublishedReport>{
	private enum publishedReportCols {
		RAW_PUBLISHED_REPORTS_ID , RAW_REPORTS_ID , RAW_REPORT_CATEGORY_ID , TENANT_ID , PRODUCT_ID , REPORT_NAME , REPORT_DESCRIPTION, REPORT_FORMAT, REPORT_LOCATION ,DISPLAY_STATUS , IS_SCHEDULED , 
		CREATED_USER , CREATED_DATE
	};
	
	private ResultSetMetaData columnMetaData = null;
	
	@Override
	public PublishedReport mapRow(ResultSet rs, int rowNum) throws SQLException {
		PublishedReport pubReport = new PublishedReport();
		if (columnMetaData == null) {
			columnMetaData = rs.getMetaData();
		}
		if (columnMetaData != null) {
			for (int i = 1; i <= columnMetaData.getColumnCount(); i++) {
				String colName = columnMetaData.getColumnName(i);
				switch (publishedReportCols.valueOf(colName)) {
					case RAW_PUBLISHED_REPORTS_ID : 
						pubReport.setPublishReportId(rs.getLong(colName));
						break; 
					case RAW_REPORTS_ID : 
						pubReport.setReportId(rs.getLong(colName));
						break; 
					case RAW_REPORT_CATEGORY_ID : 
						pubReport.setReportCategoryId(rs.getLong(colName));
						break; 
					case TENANT_ID : 
						pubReport.setTenantId(rs.getString(colName));
						break; 
					case PRODUCT_ID : 
						pubReport.setProductId(rs.getString(colName));
						break; 
					case REPORT_NAME : 
						pubReport.setReportName(rs.getString(colName));
					case REPORT_DESCRIPTION : 
						pubReport.setReportDescription(rs.getString(colName));
					case REPORT_FORMAT :
						pubReport.setFormat(rs.getString(colName));
					case REPORT_LOCATION : 
						pubReport.setReportLocaton(rs.getString(colName));
						break; 
					case DISPLAY_STATUS : 
						if(rs.getString(colName) != null)
							pubReport.setDisplayStatus(rs.getString(colName).equalsIgnoreCase("Y") ? true : false);
						break; 
					case IS_SCHEDULED : 
						if(rs.getString(colName) != null)
							pubReport.setScheduled(rs.getString(colName).equalsIgnoreCase("Y") ? true : false);
						break; 
					case CREATED_USER : 
						pubReport.setCreatedUser(rs.getString(colName));
						break; 
					case CREATED_DATE:
						if (rs.getTimestamp(colName) != null)
							pubReport.setCreatedDate(DateUtil.getDateAsString(
									DateUtil.STYLE_TIMESTAMP_DEFAULT, rs.getTimestamp(colName)));
						break;
					default:
						break;
					}
				}
		}
		return pubReport;
	}
}



