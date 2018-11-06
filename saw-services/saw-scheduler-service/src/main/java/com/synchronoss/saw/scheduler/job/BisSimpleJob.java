package com.synchronoss.saw.scheduler.job;

import org.quartz.InterruptableJob;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.synchronoss.saw.scheduler.modal.SchedulerJobDetail;
import com.synchronoss.saw.scheduler.service.AnalysisService;
import com.synchronoss.saw.scheduler.service.JobService;

public class BisSimpleJob extends QuartzJobBean implements InterruptableJob{

    private static final Logger logger = LoggerFactory.getLogger(SimpleJob.class);
    private volatile boolean toStopFlag = true;

    protected final static String JOB_DATA_MAP_ID="JOB_DATA_MAP";
    
    


    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDetail jobDetail =
                jobExecutionContext.getJobDetail();
        JobKey key = jobDetail.getKey();
        logger.info("Simple Job started with key :" + key.getName() + ", Group :"+key.getGroup()
                + " , Thread Name :"+Thread.currentThread().getName());

        SchedulerJobDetail job = (SchedulerJobDetail) jobDetail.getJobDataMap().get(JOB_DATA_MAP_ID);

       

        logger.info("Thread: "+ Thread.currentThread().getName() +" stopped.");
    }

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        logger.info("Stopping thread... ");
        toStopFlag = false;
    }

}
