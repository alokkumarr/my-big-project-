/**
 * 
 */
package com.razor.raw.core.jms;

import java.io.Serializable;

import com.razor.raw.core.req.ReportReq;

/**
 * 
 * @author surendra.rajaneni
 *
 */
public interface IQueueSender extends Serializable {

	/**
	 * The method defination take as reference variable of the interface
	 * which will be used to mark at the consumer side
	 * @param reportObject
	 */
	public void sendMessage(ReportReq reportObject);
}
