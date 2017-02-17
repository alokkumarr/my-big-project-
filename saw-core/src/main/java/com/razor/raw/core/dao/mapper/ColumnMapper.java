package com.razor.raw.core.dao.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.razor.raw.core.pojo.Column;

public class ColumnMapper implements RowMapper<Column>{
	private enum reportColumnCols {
		RAW_REPORT_COLUMNS_ID , RAW_REPORTS_ID , ALIGN , LABEL , NAME , ALIAS , PATTERN , SIZE , TYPE , GROUPBY , RENDERCRITERIA , SELECTED , AGGREGATE , SORTTYPE , CRITERIA , VIEWNAME , CRITERIAOPTION
	};
	
	private ResultSetMetaData columnMetaData = null;
	
	@Override
	public Column mapRow(ResultSet rs, int rowNum) throws SQLException {
		Column column = new Column();
		if (columnMetaData == null) {
			columnMetaData = rs.getMetaData();
		}
		if (columnMetaData != null) {
			for (int i = 1; i <= columnMetaData.getColumnCount(); i++) {
				String colName = columnMetaData.getColumnName(i);
				switch (reportColumnCols.valueOf(colName)) {
					case RAW_REPORT_COLUMNS_ID : 
						column.setRawReportColumnsId(rs.getLong(colName));
						break; 
					case RAW_REPORTS_ID : 
						column.setRawReportsId(rs.getLong(colName));
						break; 
					case ALIGN : 
						column.setAlign(rs.getString(colName));
						break; 
					case LABEL : 
						column.setLabel(rs.getString(colName));
						break; 
					case NAME : 
						column.setName(rs.getString(colName));
						break; 
					case ALIAS : 
						column.setAlias(rs.getString(colName));
						break; 
					case PATTERN : 
						column.setPattern(rs.getString(colName));
						break; 
					case SIZE : 
						column.setSize(rs.getString(colName));
						break; 
					case TYPE : 
						column.setType(rs.getString(colName));
						break; 
					case GROUPBY : 
						column.setGroupBy(rs.getString(colName));
						break; 
					case RENDERCRITERIA : 
						column.setRenderCriteria(rs.getString(colName));
						break; 
					case SELECTED : 
						if(rs.getString(colName) != null)
						column.setSelected(rs.getString(colName)
								.equalsIgnoreCase("Y") ? true : false);
						break; 
					case AGGREGATE : 
						column.setAggregate(rs.getString(colName));
						break; 
					case SORTTYPE : 
						column.setSortType(rs.getString(colName));
						break; 
					case CRITERIA : 
						column.setCriteria(rs.getString(colName));
						break; 
					case VIEWNAME : 
						column.setViewName(rs.getString(colName));
						break; 
					case CRITERIAOPTION : 
						column.setCriteriaOption(rs.getString(colName));
						break;
					default:
						break;
					}
				}
		}
		return column;
	}
}



