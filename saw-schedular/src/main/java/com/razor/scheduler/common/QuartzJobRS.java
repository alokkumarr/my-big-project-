/**
 * 
 */
package com.razor.scheduler.common;

import java.io.Serializable;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.CronTriggerImpl;

/**
 * 
 * @author surendra.rajaneni
 *
 */
public interface QuartzJobRS extends Serializable {
	
	public JobDetailImpl retrieveJobDetails();
	
	public CronTriggerImpl retrieveCronTriggerImpl();
}
