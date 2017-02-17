package com.razor.raw.core.dao.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.razor.raw.core.common.util.DateUtil;
import com.razor.raw.core.pojo.ReportCategory;

public class ReportCategoryMapper implements RowMapper<ReportCategory>{
	private enum reportCategoryCols {
		RAW_REPORT_CATEGORY_ID, TENANT_ID, PRODUCT_ID, REPORT_CATEGORY_NAME, REPORT_CATEGORY_DESCRIPTION, REPORT_SUPER_CATEGORY_ID, DISPLAY_STATUS, RESTRICTED, CREATED_USER, MODIFIED_USER, CREATED_DATE, MODIFIED_DATE
	};
	
	private ResultSetMetaData columnMetaData = null;
	@Override
	public ReportCategory mapRow(ResultSet rs, int rowNum) throws SQLException {
		ReportCategory reportCat = new ReportCategory();
		if (columnMetaData == null) {
			columnMetaData = rs.getMetaData();
		}
		if (columnMetaData != null) {
			for (int i = 1; i <= columnMetaData.getColumnCount(); i++) {
				String colName = columnMetaData.getColumnName(i);
				switch (reportCategoryCols.valueOf(colName)) {
					case RAW_REPORT_CATEGORY_ID : 
						reportCat.setReportCategoryId(rs.getLong(colName));
						break; 
					case TENANT_ID : 
						reportCat.setTenantId(rs.getString(colName));
						break; 
					case PRODUCT_ID : 
						reportCat.setProductId(rs.getString(colName));
						break; 
					case REPORT_CATEGORY_NAME : 
						reportCat.setReportCategoryName(rs.getString(colName));
						break; 
					case REPORT_CATEGORY_DESCRIPTION : 
						reportCat.setReportCategoryDescription(rs.getString(colName));
						break; 
					case REPORT_SUPER_CATEGORY_ID : 
						reportCat.setReportSuperCategoryId(rs.getLong(colName));
						break; 
					case DISPLAY_STATUS : 
						if(rs.getString(colName) != null)
						reportCat.setDisplayStatus((rs.getString(colName)
								.equalsIgnoreCase("Y") ? true : false));
						break; 
					case RESTRICTED : 
						if(rs.getString(colName) != null)
						reportCat.setRestricted((rs.getString(colName)
								.equalsIgnoreCase("Y") ? true : false));
						break; 
					case CREATED_USER : 
						reportCat.setCreatedUser(rs.getString(colName));
						break; 
					case MODIFIED_USER : 
						reportCat.setModifiedUser(rs.getString(colName));
						break; 
						
					case CREATED_DATE:
						if (rs.getDate(colName) != null)
							reportCat.setCreatedDate(DateUtil.getDateAsString(
									DateUtil.STYLE_DEFAULT, rs.getDate(colName)));
						break;
					case MODIFIED_DATE:
						if (rs.getDate(colName) != null)
							reportCat.setModifiedDate(DateUtil
									.getDateAsString(DateUtil.STYLE_DEFAULT,
											rs.getDate(colName)));
						break;
					default:
						break;
					}
				}
		}
		return reportCat;
	}
}



