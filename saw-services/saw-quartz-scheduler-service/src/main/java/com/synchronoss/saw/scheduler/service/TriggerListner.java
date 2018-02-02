package com.synchronoss.saw.scheduler.service;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TriggerListner implements TriggerListener {

    private static final Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);

    /**
     * @return
     */
    @Override
    public String getName() {
        return "globalTrigger";
    }

    /**
     *
     * @param trigger
     * @param context
     */
    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        logger.info("TriggerListner.triggerFired()");
    }

    /**
     *
     * @param trigger
     * @param context
     * @return
     */
    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        logger.info("TriggerListner.vetoJobExecution()");
        return false;
    }

    /**
     *
     * @param trigger
     */
    @Override
    public void triggerMisfired(Trigger trigger) {
        logger.info("TriggerListner.triggerMisfired()");
        String jobName = trigger.getJobKey().getName();
        logger.debug("Job name: " + jobName + " is misfired");
        
    }

    /**
     *
     * @param trigger
     * @param context
     * @param triggerInstructionCode
     */
    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context, CompletedExecutionInstruction triggerInstructionCode) {
        logger.info("TriggerListner.triggerComplete()");
    }
}
