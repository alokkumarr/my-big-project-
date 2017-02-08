package com.razor.raw.core.pojo;

import java.io.Serializable;

/**
 *	@author surendra.rajaneni 
 *	This class is used to hold the information about the
 *  respective columns retrieves from DB
 */
public class ViewMetaDataColumn implements Serializable {

	private static final long serialVersionUID = 3576714008388198634L;

	private String name;
	private String label;
	private String type;
	private String className;
	private boolean columnNullable;
	private boolean constraints;
	private String columnSize;

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label
	 *            the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	

	/**
	 * @return the columnSize
	 */
	public String getColumnSize() {
		return columnSize;
	}

	/**
	 * @param columnSize the columnSize to set
	 */
	public void setColumnSize(String columnSize) {
		this.columnSize = columnSize;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @param className
	 *            the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @return the columnNullable
	 */
	public boolean isColumnNullable() {
		return columnNullable;
	}

	/**
	 * @param columnNullable
	 *            the columnNullable to set
	 */
	public void setColumnNullable(boolean columnNullable) {
		this.columnNullable = columnNullable;
	}

	/**
	 * @return the constraints
	 */
	public boolean isConstraints() {
		return constraints;
	}

	/**
	 * @param constraints
	 *            the constraints to set
	 */
	public void setConstraints(boolean constraints) {
		this.constraints = constraints;
	}

}
