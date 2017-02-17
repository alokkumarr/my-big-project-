package com.razor.raw.generation.rest.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.razor.raw.core.pojo.Columns;
import com.razor.raw.core.pojo.Parameters;
import com.razor.raw.utility.beans.TenantIDProdID;
import com.razor.raw.core.pojo.Parameter;
import com.razor.raw.core.pojo.Column;

public class DesignerSaveReportBean implements Serializable {

		private static final long serialVersionUID = -938402806605089596L;
		private long  reportId;
		private long  productViewsId  ;
		private long  reportCategoryId;
		private String reportCategoryName;
		private ArrayList<TenantIDProdID> tenantIDProdIDs;
		private String  reportName;
		private String  reportDescription;
		private String  reportQuery;
		private String  reportQueryType;
		private String  displayStatus;
		private boolean  designerQuery = false;
		private String  modifiedUser;
		private String  createdDate;
		private String  modifiedDate;
		private List<Parameter> parameters;
		private List<Column> columns;
		private boolean parametarised = false;
		private boolean scheduled = false;
		private ArrayList<String> reportSuperCategoryList;
		
		
		

		
		/**
		 * @return the tenantIDProdIDs
		 */
		public ArrayList<TenantIDProdID> getTenantIDProdIDs() {
			return tenantIDProdIDs;
		}
		/**
		 * @param tenantIDProdIDs the tenantIDProdIDs to set
		 */
		public void setTenantIDProdIDs(ArrayList<TenantIDProdID> tenantIDProdIDs) {
			this.tenantIDProdIDs = tenantIDProdIDs;
		}
		/**
		 * @return the reportSuperCategoryList
		 */
		public ArrayList<String> getReportSuperCategoryList() {
			return reportSuperCategoryList;
		}
		/**
		 * @param reportSuperCategoryList the reportSuperCategoryList to set
		 */
		public void setReportSuperCategoryList(ArrayList<String> reportSuperCategoryList) {
			this.reportSuperCategoryList = reportSuperCategoryList;
		}
		/**
		 * @return the parametarised
		 */
		public boolean isParametarised() {
			return parametarised;
		}
		/**
		 * @param parametarised the parametarised to set
		 */
		public void setParametarised(boolean parametarised) {
			this.parametarised = parametarised;
		}
		/**
		 * @return the scheduled
		 */
		public boolean isScheduled() {
			return scheduled;
		}
		/**
		 * @param scheduled the scheduled to set
		 */
		public void setScheduled(boolean scheduled) {
			this.scheduled = scheduled;
		}
		
		public List<Parameter> getParameters() {
			return parameters;
		}
		public void setParameters(List<Parameter> parameters) {
			this.parameters = parameters;
		}
		public List<Column> getColumns() {
			return columns;
		}
		public void setColumns(List<Column> columns) {
			this.columns = columns;
		}
		/**
		 * 
		 * @return the reportId
		 */
		public long getReportId() {
			return reportId;
		}
		/**
		 * 
		 * @param reportId the reportId to set
		 */
		public void setReportId(long reportId) {
			this.reportId = reportId;
		}
		/**
		 * 
		 * @return the productViewsId
		 */
		public long getProductViewsId() {
			return productViewsId;
		}
		/**
		 * 
		 * @param productViewsId the productViewsId to set
		 */
		public void setProductViewsId(long productViewsId) {
			this.productViewsId = productViewsId;
		}
		/**
		 * 
		 * @return the reportCategoryId
		 */
		public long getReportCategoryId() {
			return reportCategoryId;
		}
		/**
		 * 
		 * @param reportCategoryId the reportCategoryId to set
		 */
		public void setReportCategoryId(long reportCategoryId) {
			this.reportCategoryId = reportCategoryId;
		}
		/**
		 * return the reportCategoryName
		 */
		public String getReportCategoryName() {
			return reportCategoryName;
		}
		/**
		 * 
		 * @param reportCategoryName
		 */
		public void setReportCategoryName(String reportCategoryName) {
			this.reportCategoryName = reportCategoryName;
		}
		
		public String getReportName() {
			return reportName;
		}
		public void setReportName(String reportName) {
			this.reportName = reportName;
		}
		public String getReportDescription() {
			return reportDescription;
		}
		public void setReportDescription(String reportDescription) {
			this.reportDescription = reportDescription;
		}
		public String getReportQuery() {
			return reportQuery;
		}
		public void setReportQuery(String reportQuery) {
			this.reportQuery = reportQuery;
		}
		public String getReportQueryType() {
			return reportQueryType;
		}
		public void setReportQueryType(String reportQueryType) {
			this.reportQueryType = reportQueryType;
		}
		public String getDisplayStatus() {
			return displayStatus;
		}
		public void setDisplayStatus(String displayStatus) {
			this.displayStatus = displayStatus;
		}
		
		public boolean isDesignerQuery() {
			return designerQuery;
		}
		public void setDesignerQuery(boolean designerQuery) {
			this.designerQuery = designerQuery;
		}
		public String getModifiedUser() {
			return modifiedUser;
		}
		public void setModifiedUser(String modifiedUser) {
			this.modifiedUser = modifiedUser;
		}
		public String getCreatedDate() {
			return createdDate;
		}
		public void setCreatedDate(String createdDate) {
			this.createdDate = createdDate;
		}
		public String getModifiedDate() {
			return modifiedDate;
		}
		public void setModifiedDate(String modifiedDate) {
			this.modifiedDate = modifiedDate;
		}
		
}
