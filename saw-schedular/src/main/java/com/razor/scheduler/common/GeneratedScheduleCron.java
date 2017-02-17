/**
 * 
 */
package com.razor.scheduler.common;

import java.io.Serializable;

/**
 * This class is used to hold the latest 5 schedules time to publish the report
 * @author surendra.rajaneni
 *
 */
public class GeneratedScheduleCron implements Serializable {

	private static final long serialVersionUID = 9036015934310273470L;
	private Integer serailNum;
	private String generatedCronExpressionDate;
	/**
	 * @return the serailNum
	 */
	public Integer getSerailNum() {
		return serailNum;
	}
	/**
	 * @param serailNum the serailNum to set
	 */
	public void setSerailNum(Integer serailNum) {
		this.serailNum = serailNum;
	}
	/**
	 * @return the generatedCronExpressionDate
	 */
	public String getGeneratedCronExpressionDate() {
		return generatedCronExpressionDate;
	}
	/**
	 * @param generatedCronExpressionDate the generatedCronExpressionDate to set
	 */
	public void setGeneratedCronExpressionDate(String generatedCronExpressionDate) {
		this.generatedCronExpressionDate = generatedCronExpressionDate;
	}
	
}
