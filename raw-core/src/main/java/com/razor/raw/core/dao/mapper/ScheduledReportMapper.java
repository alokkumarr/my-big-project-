package com.razor.raw.core.dao.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.razor.raw.core.common.util.DateUtil;
import com.razor.raw.core.pojo.ScheduledReport;

public class ScheduledReportMapper implements RowMapper<ScheduledReport> {
	public enum reportCols {
		RAW_REPORTS_ID, RAW_REPORT_CATEGORY_ID, TENANT_ID, PRODUCT_ID, REPORT_NAME, REPORT_DESCRIPTION,REPORT_SCHEDULED_DT,JOB_NAME,JOB_GROUP	}
	
	private ResultSetMetaData columnMetaData = null;
	
	@Override
	public ScheduledReport mapRow(ResultSet rs, int rowNum) throws SQLException {
		ScheduledReport scheduledReport = new ScheduledReport();
	
		if (columnMetaData == null) {
			columnMetaData = rs.getMetaData();
		}
		if (columnMetaData != null) {
			for (int i = 1; i <= columnMetaData.getColumnCount(); i++) {
				String colName = columnMetaData.getColumnName(i);
				switch (reportCols.valueOf(colName)) {
				case RAW_REPORTS_ID:
					scheduledReport.setReportId(rs.getLong(colName));
					break;
				case RAW_REPORT_CATEGORY_ID:
					scheduledReport.setReportCategoryId(rs.getLong(colName));
					break;
				case TENANT_ID:
					scheduledReport.setTenantId(rs.getString(colName));
					break;
				case PRODUCT_ID:
					scheduledReport.setProductId(rs.getString(colName));
					break;
				case REPORT_NAME:
					scheduledReport.setReportName(rs.getString(colName));
					break;
				case REPORT_DESCRIPTION:
					scheduledReport.setReportDescription(rs.getString(colName));
					break;
				case REPORT_SCHEDULED_DT:
					if (rs.getDate(colName) != null)
					scheduledReport.setLastModDate(DateUtil.getDateAsString(
							DateUtil.STYLE_TIMESTAMP_DEFAULT, rs.getTimestamp(colName)));
					break;
				case JOB_NAME:
					scheduledReport.setJobName(rs.getString(colName));
					break;
				case JOB_GROUP:
					scheduledReport.setJobGroup(rs.getString(colName));
					break;					
				default:
					break;
				}
			
			}
		}
		return scheduledReport;
	}

}
