package com.razor.scheduler.jobs.report;

import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.razor.raw.core.jms.IQueueSender;
import com.razor.raw.core.pojo.OutputFile;
import com.razor.raw.core.req.ReportReq;
import com.razor.scheduler.common.SchedularConstants;

/**
 * Is used for handle the Report Schedule Job
 * It prepare the report request object {@code:ReportReq} and send to ActiveMQ message
 * @see:IQueueSender for message implementation {@code:ReportRequestSender}
 * @author surendra.rajaneni
 *
 */
public class ReportDefinationJob implements Job {

	private static final Logger logger = LoggerFactory.getLogger(ReportDefinationJob.class);
	@Autowired
	@Qualifier("reportRequestSender")
	IQueueSender iQueueSender;	

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		logger.debug("Schedule Report JOB started " + this.getClass().getName());
		Map dataMap = context.getJobDetail().getJobDataMap();
		ReportDefinition reportDefination = (ReportDefinition) dataMap.get(SchedularConstants.JOB_DATA_MAP_ID.toString());
		ReportReq reportReq = new ReportReq();
		reportReq.setName(reportDefination.getReportName());
		reportReq.setDescription(reportDefination.getReportDesc());
		reportReq.setPublishedBy(reportDefination.getUsername());
		reportReq.setEmailId(reportDefination.getEmailId());
		logger.debug("Reading XML File : isCopyToFTP"+ reportDefination.isCopyToFTP());
		reportReq.setCopyToFTP(reportDefination.isCopyToFTP());
		reportReq.setAttachReportToMail(reportDefination.isAttachReportToMail());
		reportReq.setTenantId(reportDefination.getTenantId());
		reportReq.setProductId(reportDefination.getProductID());
		reportReq.setScheduled(true);
		OutputFile outFile = new OutputFile();
		outFile.setOutputFormat(reportDefination.getFileNameFormat());
		outFile.setOutputFileName(reportDefination.getReportFileNameTobeGenerated());
		reportReq.setOutputFile(outFile);
		reportReq.setReport(reportDefination.getReport());
		iQueueSender.sendMessage(reportReq);
		logger.debug("Schedule Report JOB Completed " + this.getClass().getName());
	}

	
}
