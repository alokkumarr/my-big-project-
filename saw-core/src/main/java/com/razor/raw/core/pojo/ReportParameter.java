package com.razor.raw.core.pojo;

import java.io.Serializable;
import java.util.Date;

public class ReportParameter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5971853759731929645L;
	int parameterIndex = 0;
	String parameterName = null;
	String parameterType = null;
	String defaultValue = null;
	String value = null;		Date dateValue;

	/**	 * @return the dateValue	 */	public Date getDateValue() {		return dateValue;	}	/**	 * @param dateValue the dateValue to set	 */	public void setDateValue(Date dateValue) {		this.dateValue = dateValue;	}	public ReportParameter(int parameterIndex) {
		this.parameterIndex = parameterIndex;
	}

	public int getParameterIndex() {
		return parameterIndex;
	}

	public void setParameterIndex(int parameterIndex) {
		this.parameterIndex = parameterIndex;
	}

	public String getParameterName() {
		if (parameterName == null) {
			parameterName = "Parameter " + parameterIndex;
		}
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getParameterType() {
		return parameterType;
	}

	public void setParameterType(String parameterType) {
		this.parameterType = parameterType;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getValue() {
		if (value == null && defaultValue != null) {
			value = defaultValue;
		}
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
