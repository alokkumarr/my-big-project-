package com.synchronoss.saw.scheduler.controller;

import com.synchronoss.saw.scheduler.job.CronJob;
import com.synchronoss.saw.scheduler.job.SimpleJob;
import com.synchronoss.saw.scheduler.modal.JobDetail;
import com.synchronoss.saw.scheduler.modal.SchedulerResponse;
import com.synchronoss.saw.scheduler.service.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/scheduler/")
public class JobController {

	private static final Logger logger = LoggerFactory.getLogger(JobController.class);
	@Autowired
	@Lazy
	JobService jobService;

	@RequestMapping(value ="schedule",method = RequestMethod.POST)
	public SchedulerResponse schedule(@RequestBody JobDetail jobDetail){
		logger.info("JobController schedule() start here.");

		//Job Name is mandatory
		if(jobDetail.getJobName() == null || jobDetail.getJobName().trim().equals("")){
			return getServerResponse(ServerResponseCode.JOB_NAME_NOT_PRESENT, false);
		}

		logger.info("Check if job Name is unique");
		if(!jobService.isJobWithNamePresent(jobDetail.getJobName())){

			if(jobDetail.getCronExpression() == null || jobDetail.getCronExpression().trim().equals("")){
				logger.info("Simple job ");
				boolean status = jobService.scheduleOneTimeJob(jobDetail.getJobName(), SimpleJob.class,
						jobDetail.getJobScheduleTime());
				if(status){
					return getServerResponse(ServerResponseCode.SUCCESS, jobService.getAllJobs());
				}else{
					return getServerResponse(ServerResponseCode.ERROR, false);
				}
				
			} else {
				logger.info("Cron Trigger ");
				boolean status = jobService.scheduleCronJob(jobDetail.getJobName(), CronJob.class,
						jobDetail.getJobScheduleTime(),
						jobDetail.getCronExpression());
				if(status){
					return getServerResponse(ServerResponseCode.SUCCESS, jobService.getAllJobs());
				}else{
					return getServerResponse(ServerResponseCode.ERROR, false);
				}				
			}
		}else{
			return getServerResponse(ServerResponseCode.JOB_WITH_SAME_NAME_EXIST, false);
		}
	}

	@RequestMapping(value ="unschedule",method = RequestMethod.POST)
	public void unschedule(@RequestParam("jobName") String jobName) {
		logger.info("JobController unschedule() method");
		jobService.unScheduleJob(jobName);
	}

	@RequestMapping(value ="delete",method = RequestMethod.DELETE)
	public SchedulerResponse delete(@RequestParam("jobName") String jobName) {
        logger.info("JobController delete() method");

		if(jobService.isJobWithNamePresent(jobName)){
			boolean isJobRunning = jobService.isJobRunning(jobName);

			if(!isJobRunning){
				boolean status = jobService.deleteJob(jobName);
				if(status){
					return getServerResponse(ServerResponseCode.SUCCESS, true);
				}else{
					return getServerResponse(ServerResponseCode.ERROR, false);
				}
			}else{
				return getServerResponse(ServerResponseCode.JOB_ALREADY_IN_RUNNING_STATE, false);
			}
		}else{
			//Job doesn't exist
			return getServerResponse(ServerResponseCode.JOB_DOESNT_EXIST, false);
		}
	}

	@RequestMapping(value ="pause",method = RequestMethod.POST)
	public SchedulerResponse pause(@RequestParam("jobName") String jobName) {
		logger.info( "JobController pause() method");

		if(jobService.isJobWithNamePresent(jobName)){

			boolean isJobRunning = jobService.isJobRunning(jobName);

			if(!isJobRunning){
				boolean status = jobService.pauseJob(jobName);
				if(status){
					return getServerResponse(ServerResponseCode.SUCCESS, true);
				}else{
					return getServerResponse(ServerResponseCode.ERROR, false);
				}			
			}else{
				return getServerResponse(ServerResponseCode.JOB_ALREADY_IN_RUNNING_STATE, false);
			}

		}else{
			//Job doesn't exist
			return getServerResponse(ServerResponseCode.JOB_DOESNT_EXIST, false);
		}		
	}

	@RequestMapping(value ="resume",method = RequestMethod.POST)
	public SchedulerResponse resume(@RequestParam("jobName") String jobName) {
        logger.info("JobController resume() method");

		if(jobService.isJobWithNamePresent(jobName)){
			String jobState = jobService.getJobState(jobName);

			if(jobState.equals("PAUSED")){
				logger.info("Job current state is PAUSED, Resuming job...");
				boolean status = jobService.resumeJob(jobName);

				if(status){
					return getServerResponse(ServerResponseCode.SUCCESS, true);
				}else{
				    logger.info("Error occurred while resuming the job ");
					return getServerResponse(ServerResponseCode.ERROR, false);
				}
			}else{
                logger.info("Job is not in PAUSED state");
				return getServerResponse(ServerResponseCode.JOB_NOT_IN_PAUSED_STATE, false);
			}

		}else{
			logger.info("Job doesn't exist");
			return getServerResponse(ServerResponseCode.JOB_DOESNT_EXIST, false);
		}
	}

	@RequestMapping(value ="update",method = RequestMethod.POST)
	public SchedulerResponse updateJob(@RequestParam("jobName") String jobName, 
			@RequestParam("jobScheduleTime") @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm") Date jobScheduleTime, 
			@RequestParam("cronExpression") String cronExpression){
		logger.info("JobController updateJob() method ");

		//Job Name is mandatory
		if(jobName == null || jobName.trim().equals("")){
			return getServerResponse(ServerResponseCode.JOB_NAME_NOT_PRESENT, false);
		}

		//Edit Job
		if(jobService.isJobWithNamePresent(jobName)){
			
			if(cronExpression == null || cronExpression.trim().equals("")){
				//Single Trigger
				boolean status = jobService.updateOneTimeJob(jobName, jobScheduleTime);
				if(status){
					return getServerResponse(ServerResponseCode.SUCCESS, jobService.getAllJobs());
				}else{
					return getServerResponse(ServerResponseCode.ERROR, false);
				}
				
			}else{
				//Cron Trigger
				boolean status = jobService.updateCronJob(jobName, jobScheduleTime, cronExpression);
				if(status){
					return getServerResponse(ServerResponseCode.SUCCESS, jobService.getAllJobs());
				}else{
					return getServerResponse(ServerResponseCode.ERROR, false);
				}				
			}
			
			
		}else{
			return getServerResponse(ServerResponseCode.JOB_DOESNT_EXIST, false);
		}
	}

	@RequestMapping(value ="jobs",method = RequestMethod.GET)
	public SchedulerResponse getAllJobs(){
        logger.info("JobController getAllJobs() method");

		List<Map<String, Object>> list = jobService.getAllJobs();
		return getServerResponse(ServerResponseCode.SUCCESS, list);
	}

	@RequestMapping(value ="fetchJob",method = RequestMethod.POST)
	public SchedulerResponse getJobDetails(@RequestParam("jobName") String jobName){
		logger.info("JobController getJobDetails() method");

		//Job Name is mandatory
		if(jobName == null || jobName.trim().equals("")){
			return getServerResponse(ServerResponseCode.JOB_NAME_NOT_PRESENT, false);
		}
		
		Map<String, Object> status = jobService.getJobDetails(jobName);
		return getServerResponse(ServerResponseCode.SUCCESS, status);
	}

	@RequestMapping(value ="isJobRunning",method = RequestMethod.POST)
	public SchedulerResponse isJobRunning(@RequestParam("jobName") String jobName) {
		logger.info("JobController isJobRunning() method");

		boolean status = jobService.isJobRunning(jobName);
		return getServerResponse(ServerResponseCode.SUCCESS, status);
	}

	@RequestMapping(value ="jobState",method = RequestMethod.POST)
	public SchedulerResponse getJobState(@RequestParam("jobName") String jobName) {
        logger.info("JobController getJobState() method");

		String jobState = jobService.getJobState(jobName);
		return getServerResponse(ServerResponseCode.SUCCESS, jobState);
	}

	@RequestMapping(value ="stop",method = RequestMethod.POST)
	public SchedulerResponse stopJob(@RequestParam("jobName") String jobName) {
        logger.info("JobController stopJob() method");

		if(jobService.isJobWithNamePresent(jobName)){

			if(jobService.isJobRunning(jobName)){
				boolean status = jobService.stopJob(jobName);
				if(status){
					return getServerResponse(ServerResponseCode.SUCCESS, true);
				}else{
					//Server error
					return getServerResponse(ServerResponseCode.ERROR, false);
				}

			}else{
				//Job not in running state
				return getServerResponse(ServerResponseCode.JOB_NOT_IN_RUNNING_STATE, false);
			}

		}else{
			//Job doesn't exist
			return getServerResponse(ServerResponseCode.JOB_DOESNT_EXIST, false);
		}
	}

	@RequestMapping(value ="start",method = RequestMethod.POST)
	public SchedulerResponse startJobNow(@RequestParam("jobName") String jobName) {
		logger.info("JobController startJobNow() method");

		if(jobService.isJobWithNamePresent(jobName)){

			if(!jobService.isJobRunning(jobName)){
				boolean status = jobService.startJobNow(jobName);

				if(status){
					//Success
					return getServerResponse(ServerResponseCode.SUCCESS, true);

				}else{
					//Server error
					return getServerResponse(ServerResponseCode.ERROR, false);
				}

			}else{
				//Job already running
				return getServerResponse(ServerResponseCode.JOB_ALREADY_IN_RUNNING_STATE, false);
			}

		}else{
			//Job doesn't exist
			return getServerResponse(ServerResponseCode.JOB_DOESNT_EXIST, false);
		}
	}

	public SchedulerResponse getServerResponse(int responseCode, Object data){
		SchedulerResponse serverResponse = new SchedulerResponse();
		serverResponse.setStatusCode(responseCode);
		serverResponse.setData(data);
		return serverResponse; 
	}
}
