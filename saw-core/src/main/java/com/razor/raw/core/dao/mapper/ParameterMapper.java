package com.razor.raw.core.dao.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.StringUtils;

import com.razor.raw.core.pojo.Lookup;
import com.razor.raw.core.pojo.Parameter;

public class ParameterMapper implements RowMapper<Parameter>{
	private enum reportParamCols {
		RAW_REPORT_PARAMETERS_ID, RAW_REPORTS_ID, LOOKUPS, DEFAULTVALUE, NAME, TYPE, VALUE, DISPLAY, OPTIONAL,EXEC_ORDER_INDEX
	}
	
	private ResultSetMetaData columnMetaData = null;
	
	@Override
	public Parameter mapRow(ResultSet rs, int rowNum) throws SQLException {
		Parameter parameter = new Parameter();
		if (columnMetaData == null) {
			columnMetaData = rs.getMetaData();
		}
		if (columnMetaData != null) {
			for (int i = 1; i <= columnMetaData.getColumnCount(); i++) {
				String colName = columnMetaData.getColumnName(i);
					switch (reportParamCols.valueOf(colName)) {
					case RAW_REPORT_PARAMETERS_ID:
						parameter.setRawReportParametersId(rs.getLong(colName));
						break;
					case RAW_REPORTS_ID:
						parameter.setRawReportsId(rs.getLong(colName));
						break;
					case LOOKUPS:
						String lookup = rs.getString(colName);
						List<Lookup> lookups = new ArrayList<Lookup>();
						if(!StringUtils.isEmpty(lookup))
						{
							String[] lookUps = lookup.split("\\|\\|\\|");
							for(String lookup1 : lookUps)
							{
								Lookup lookup2 = new Lookup(lookup1);
								lookups.add(lookup2);
							}
							
						}
						parameter.setLookup(lookups);
						break;
					case DEFAULTVALUE:
						parameter.setDefaultValue(rs.getString(colName));
						break;
					case NAME:
						parameter.setName(rs.getString(colName));
						break;
					case TYPE:
						parameter.setType(rs.getString(colName));
						break;
					case VALUE:
						parameter.setValue(rs.getString(colName));
						break;
					case DISPLAY:
						parameter.setDisplay(rs.getString(colName));
						break;
					case OPTIONAL:
						if(rs.getInt(colName) == 1){
							parameter.setOptional(true);
						}
						else{
							parameter.setOptional(false);
						}
						break;
					case EXEC_ORDER_INDEX:
						parameter.setIndex(rs.getInt(colName));
						break;
					default:
						break;
					}
				}
			
			}
		return parameter;
	}
		
}



