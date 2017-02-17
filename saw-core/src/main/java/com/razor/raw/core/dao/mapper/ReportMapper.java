package com.razor.raw.core.dao.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.razor.raw.core.common.util.DateUtil;
import com.razor.raw.core.pojo.Report;

public class ReportMapper implements RowMapper<Report> {
	public enum reportCols {
		RAW_REPORTS_ID, RAW_PRODUCT_VIEWS_ID, RAW_REPORT_CATEGORY_ID, TENANT_ID, PRODUCT_ID, REPORT_NAME, REPORT_DESCRIPTION, REPORT_QUERY, REPORT_QUERY_TYPE, DISPLAY_STATUS, DESIGNER_QUERY, CREATED_USER, MODIFIED_USER, CREATED_DATE, MODIFIED_DATE
	}
	
	private ResultSetMetaData columnMetaData = null;
	
	@Override
	public Report mapRow(ResultSet rs, int rowNum) throws SQLException {
		Report report = new Report();
	
		if (columnMetaData == null) {
			columnMetaData = rs.getMetaData();
		}
		if (columnMetaData != null) {
			for (int i = 1; i <= columnMetaData.getColumnCount(); i++) {

				String colName = columnMetaData.getColumnName(i);
				switch (reportCols.valueOf(colName)) {
				case RAW_REPORTS_ID:
					report.setReportId(rs.getLong(colName));
					break;
				case RAW_PRODUCT_VIEWS_ID:
					report.setProductViewsId(rs.getLong(colName));
					break;
				case RAW_REPORT_CATEGORY_ID:
					report.setReportCategoryId(rs.getLong(colName));
					break;
				case TENANT_ID:
					report.setTenantId(rs.getString(colName));
					break;
				case PRODUCT_ID:
					report.setProductId(rs.getString(colName));
					break;
				case REPORT_NAME:
					report.setReportName(rs.getString(colName));
					break;
				case REPORT_DESCRIPTION:
					report.setReportDescription(rs.getString(colName));
					break;
				case REPORT_QUERY:
					report.setReportQuery(rs.getString(colName));
					break;
				case REPORT_QUERY_TYPE:
					report.setReportQueryType(rs.getString(colName));
					break;
				case DISPLAY_STATUS:
					report.setDisplayStatus(rs.getString(colName));
					break;
				case DESIGNER_QUERY:
					if(rs.getString(colName) != null)
					report.setDesignerQuery((rs.getString(colName)
							.equalsIgnoreCase("Y") ? true : false));
					break;
				case CREATED_USER:
					report.setCreatedUser(rs.getString(colName));
					break;
				case MODIFIED_USER:
					report.setModifiedUser(rs.getString(colName));
					break;
				case CREATED_DATE:
					if (rs.getDate(colName) != null)
						report.setCreatedDate(DateUtil.getDateAsString(
								DateUtil.STYLE_DEFAULT, rs.getDate(colName)));
					break;
				case MODIFIED_DATE:
					if (rs.getDate(colName) != null)
						report.setModifiedDate(DateUtil
								.getDateAsString(DateUtil.STYLE_DEFAULT,
										rs.getDate(colName)));
					break;
				default:
					break;
				}
			
			}
		}
		return report;
	}

}
