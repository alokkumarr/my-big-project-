package com.razor.raw.core.pojo;

import java.io.Serializable;
import java.util.List;

public class ReportResult implements Serializable {

	private static final long serialVersionUID = -667600693707154226L;
	
	List<Column> columns = null;
	List<ReportResultItem> reportItems = null;	String queryErrorMessage = null;		private boolean aggrFnExists;
	/**	 * @return the aggrFnExists	 */	public boolean isAggrFnExists() {		return aggrFnExists;	}	/**	 * @param aggrFnExists the aggrFnExists to set	 */	public void setAggrFnExists(boolean aggrFnExists) {		this.aggrFnExists = aggrFnExists;	}	public List<Column> getColumns() {
		return columns;
	}
	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}
	public List<ReportResultItem> getReportItems() {
		return reportItems;
	}
	public void setReportItems(List<ReportResultItem> reportItems) {
		this.reportItems = reportItems;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getQueryErrorMessage() {
		return queryErrorMessage;
	}
	public void setQueryErrorMessage(String queryErrorMessage) {
		this.queryErrorMessage = queryErrorMessage;
	}

	
}
