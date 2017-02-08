package com.razor.raw.re.report.process;

import java.io.Serializable;




public class ReportParameterViewImpl implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 2074310679054409627L;
	private int parameterIndex = 0;
	private String parameterName;
	private String parameterType;
	private String defaultValue;
	private String value ;

	public ReportParameterViewImpl(int parameterIndex) {
		this.parameterIndex = parameterIndex;
	}

	/**
	 * @return the parameterIndex
	 */
	public int getParameterIndex() {
		return parameterIndex;
	}

	/**
	 * @param parameterIndex the parameterIndex to set
	 */
	public void setParameterIndex(int parameterIndex) {
		this.parameterIndex = parameterIndex;
	}

	/**
	 * @return the parameterName
	 */
	public String getParameterName() {
		return parameterName;
	}

	/**
	 * @param parameterName the parameterName to set
	 */
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	/**
	 * @return the parameterType
	 */
	public String getParameterType() {
		return parameterType;
	}

	/**
	 * @param parameterType the parameterType to set
	 */
	public void setParameterType(String parameterType) {
		this.parameterType = parameterType;
	}

	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	

}
