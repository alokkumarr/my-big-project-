/**
 * 
 */
package com.razor.raw.core.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * @author gsan0003
 *
 */
public class DrillView implements Serializable {

	private static final long serialVersionUID = 1323005611933219005L;
	
	private String  viewName;
	private String  tableSchema;
	private List<Column> columnList;
	
	public String getViewName() {
		return viewName;
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	 
	public String getTableSchema() {
		return tableSchema;
	}
	public void setTableSchema(String tableSchema) {
		this.tableSchema = tableSchema;
	}
	public List<Column> getColumnList() {
		return columnList;
	}
	public void setColumnList(List<Column> columnList) {
		this.columnList = columnList;
	}

}
