package com.razor.raw.core.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author surendra.rajaneni This class is used to hold the information about the
 *         respective Tables/Views retrives from the DB
 */
public class ViewMetaDataEntity implements Serializable {

	private static final long serialVersionUID = 5597346620718917888L;

	private String viewName;
	private List<ViewMetaDataColumn> viewColumns = new ArrayList<ViewMetaDataColumn>();
	private boolean contraints;
	private boolean relationship;
	private String metaDataTableType;

	/**
	 * @return the relationship
	 */
	public boolean isRelationship() {
		return relationship;
	}

	/**
	 * @param relationship the relationship to set
	 */
	public void setRelationship(boolean relationship) {
		this.relationship = relationship;
	}

	/**
	 * @return the metaDataTableType
	 */
	public String getMetaDataTableType() {
		return metaDataTableType;
	}

	/**
	 * @param metaDataTableType
	 *            the metaDataTableType to set
	 */
	public void setMetaDataTableType(String metaDataTableType) {
		this.metaDataTableType = metaDataTableType;
	}

	/**
	 * @return the viewName
	 */
	public String getViewName() {
		return viewName;
	}

	/**
	 * @param viewName
	 *            the viewName to set
	 */
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	/**
	 * @return the viewColumns
	 */
	public List<ViewMetaDataColumn> getViewColumns() {
		return viewColumns;
	}

	/**
	 * @param viewColumns
	 *            the viewColumns to set
	 */
	public void setViewColumns(List<ViewMetaDataColumn> viewColumns) {
		this.viewColumns = viewColumns;
	}

	/**
	 * @return the contraints
	 */
	public boolean isContraints() {
		return contraints;
	}

	/**
	 * @param contraints
	 *            the contraints to set
	 */
	public void setContraints(boolean contraints) {
		this.contraints = contraints;
	}

}
