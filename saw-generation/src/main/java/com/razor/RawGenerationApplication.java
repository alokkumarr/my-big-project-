package com.razor;

import java.io.File;
import java.util.Date;

import org.quartz.SchedulerException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.util.FileSystemUtils;

import com.razor.raw.core.pojo.Report;
import com.razor.scheduler.common.QuartzScheduleJobDetails;
import com.razor.scheduler.jobs.report.ReportDefinationJob;
import com.razor.scheduler.jobs.report.ReportDefinition;
import com.razor.scheduler.service.ScheduleService;

@SpringBootApplication
public class RawGenerationApplication extends SpringBootServletInitializer{
	
	
	
	@Bean
	public TomcatEmbeddedServletContainerFactory tomcatEmbeddedServletContainerFactory() {
	    return new TomcatEmbeddedServletContainerFactory();
	}
	
/*	@Autowired
	private RemoteTokenServices remoteTokenServices;
	
	
	*//**
	 * @return the remoteTokenServices
	 *//*
	public RemoteTokenServices getRemoteTokenServices() {
		return remoteTokenServices;
	}


	*//**
	 * @param remoteTokenServices the remoteTokenServices to set
	 *//*
	public void setRemoteTokenServices(RemoteTokenServices remoteTokenServices) {
		this.remoteTokenServices = remoteTokenServices;
	}*/


	@Override
	protected SpringApplicationBuilder configure(
			SpringApplicationBuilder builder) {
		// TODO Auto-generated method stub
		return builder.sources(RawGenerationApplication.class);
	}
	
	
	/*
		@Bean // Strictly speaking this bean is not necessary as boot creates a default
	    JmsListenerContainerFactory<?> myJmsContainerFactory(ConnectionFactory connectionFactory) {
	        SimpleJmsListenerContainerFactory factory = new SimpleJmsListenerContainerFactory();
	        factory.setConnectionFactory(connectionFactory);
	        return factory;
	    }
*/
	    public static void main(String[] args) {
	        // Clean out any ActiveMQ data from a previous run
	        FileSystemUtils.deleteRecursively(new File("activemq-data"));

	        // Launch the application
	        ConfigurableApplicationContext context = SpringApplication.run(RawGenerationApplication.class, args);
	        WebSecurityConfig config = context.getBean(WebSecurityConfig.class);
	        RemoteTokenServices remoteTokenServices = context.getBean(RemoteTokenServices.class);
	        System.out.println("----------------------------------------------------------------------->>>>>>>>>>>>>>>>>>..");
	        System.out.println("WebSecurityConfig - "+config);
	        System.out.println("remoteTokenServices - "+remoteTokenServices);
	        
	      /*  ReportRepository reportRepository  = context.getBean(ReportRepository.class);
	        Report report = reportRepository.getReportByReportId(1);
	        ScheduleService service = context.getBean(ScheduleService.class);
	        ReportRequestSender reportRequestSender = context.getBean(ReportRequestSender.class);
	        ReportReq reportReq = new ReportReq();
	        reportReq.setAttachReportToMail(true);
	        reportReq.setCopyToFTP(true);
	        reportReq.setDescription("description");
	        reportReq.setEmailId("surendra.rajaneni@razorsight.com");
	        reportReq.setName("name");
	        OutputFile outputFile = new OutputFile();
	        outputFile.setOutputFileName("file_name");
	        outputFile.setOutputFormat(".XLSX");
	        reportReq.setOutputFile(outputFile);
	        reportReq.setProductId("DEV");
	        reportReq.setPublishedBy("surendra");
	        reportReq.setReport(report);
	        reportReq.setTenantId("PGI");
	        reportRequestSender.sendMessage(reportReq);*/
	      /*  try {
				simpleTest(service,report);
			} catch (SchedulerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        */
	    }
	    
	    
	    public static void simpleTest(ScheduleService service, Report report) throws SchedulerException
	    {
	    	ReportDefinition reportDefinition = new ReportDefinition(); 
	    	QuartzScheduleJobDetails<ReportDefinition> quartzJobDetails = new QuartzScheduleJobDetails<ReportDefinition>(reportDefinition);

			String cronExpressionGen = "0 0/1 * * * ?";//new CronExpressionBuilder().getCronExpression(this);
			//LogManager.log(LogManager.CATEGORY_DEFAULT,LogManager.LEVEL_DEBUG,"cronExpressionGen:"+cronExpressionGen);
			//LogManager.log(LogManager.CATEGORY_DEFAULT,LogManager.LEVEL_DEBUG,"Report ID:"+viewReportModel.getMdReportMainId());
			if (cronExpressionGen != null && !cronExpressionGen.equals("")) {
				quartzJobDetails.setQuartzCronExpression(cronExpressionGen);
				quartzJobDetails.setJobclassName(ReportDefinationJob.class);
				quartzJobDetails.setJobGroup("PGI"); 
				quartzJobDetails.setJobName("3"); 
				reportDefinition.setFileNameFormat(".xlxs");
				reportDefinition.setReportFileNameTobeGenerated("ReportName");
				reportDefinition.setReportDesc("getDescription");
				reportDefinition.setReport(report);
				reportDefinition.setTenantId("PGI");
				reportDefinition.setProductID("DEV");
				reportDefinition.setUsername("SURE");
				reportDefinition.setEmailId("surendra.rajaneni@razorsight.com");
				reportDefinition.setCopyToFTP(true);
				reportDefinition.setAttachReportToMail(true);
				//viewReportModel.getReportDefinition().setScheduleType(getSchedulerType());
				//quartzJobDetails.setOtherRequiredInfo(otherInformation);
				if("once".equalsIgnoreCase("ONCE")){
					quartzJobDetails.setSimpleTriggerDate(new Date());
					//Scheduler scheduler = FacesUtils.getQuartzSchedular();
					//LogManager.log(LogManager.CATEGORY_DEFAULT, LogManager.LEVEL_DEBUG, "Scheduler :" + scheduler);
					/*service.scheduleJob(quartzJobDetails.retrieveJobDetails(),
							quartzJobDetails.retrieveSimpleTriggerImpl());*/
					service.setSimpleTrigger(quartzJobDetails);
				} else {
					//Scheduler scheduler = FacesUtils.getQuartzSchedular();
					//LogManager.log(LogManager.CATEGORY_DEFAULT, LogManager.LEVEL_DEBUG, "Scheduler :" + scheduler);
					/*service.scheduleJob(quartzJobDetails.retrieveJobDetails(),
							quartzJobDetails.retrieveCronTriggerImpl());*/
					service.setCronTrigger(quartzJobDetails);
				}
	    }
	    }
	   

}
