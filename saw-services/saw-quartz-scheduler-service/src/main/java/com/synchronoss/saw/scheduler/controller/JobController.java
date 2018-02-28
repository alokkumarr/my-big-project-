package com.synchronoss.saw.scheduler.controller;

import com.synchronoss.saw.scheduler.job.CronJob;
import com.synchronoss.saw.scheduler.job.SimpleJob;
import com.synchronoss.saw.scheduler.modal.FetchByCategoryBean;
import com.synchronoss.saw.scheduler.modal.ScheduleKeys;
import com.synchronoss.saw.scheduler.modal.SchedulerJobDetail;
import com.synchronoss.saw.scheduler.modal.SchedulerResponse;
import com.synchronoss.saw.scheduler.service.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

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
	public SchedulerResponse schedule(@RequestBody SchedulerJobDetail jobDetail){
		logger.info("JobController schedule() start here.");

		//Job Name is mandatory
		if(jobDetail.getJobName() == null || jobDetail.getJobName().trim().equals("")){
			return getServerResponse(ServerResponseCode.JOB_NAME_NOT_PRESENT, false);
		}
        ScheduleKeys scheduleKeys = new ScheduleKeys();
		scheduleKeys.setGroupName(jobDetail.getJobGroup());
		scheduleKeys.setJobName(jobDetail.getJobName());
		scheduleKeys.setCategoryId(jobDetail.getCategoryID());
		logger.info("Check if job Name is unique");
		if(!jobService.isJobWithNamePresent(scheduleKeys)){

			if(jobDetail.getCronExpression() == null || jobDetail.getCronExpression().trim().equals("")){
				logger.info("Simple job ");
				boolean status = jobService.scheduleOneTimeJob(jobDetail, SimpleJob.class);
				if(status){
					return getServerResponse(ServerResponseCode.SUCCESS, jobService.getAllJobs(jobDetail.getJobGroup()
                            ,jobDetail.getCategoryID()));
				}else{
					return getServerResponse(ServerResponseCode.ERROR, false);
				}
				
			} else {
				logger.info("Cron Trigger ");
				boolean status = jobService.scheduleCronJob(jobDetail,CronJob.class);
				if(status){
					return getServerResponse(ServerResponseCode.SUCCESS, jobService.getAllJobs(jobDetail.getJobGroup()
                            ,jobDetail.getCategoryID()));
				}else{
					return getServerResponse(ServerResponseCode.ERROR, false);
				}				
			}
		}else{
			return getServerResponse(ServerResponseCode.JOB_WITH_SAME_NAME_EXIST, false);
		}
	}

	@RequestMapping(value ="unschedule",method = RequestMethod.POST)
	public void unschedule(@RequestBody ScheduleKeys schedule) {
		logger.info("JobController unschedule() method");
		jobService.unScheduleJob(schedule);
	}

	@RequestMapping(value ="delete",method = RequestMethod.DELETE)
	public SchedulerResponse delete(@RequestBody ScheduleKeys schedule ) {
        logger.info("JobController delete() method");

		if(jobService.isJobWithNamePresent(schedule)){
			boolean isJobRunning = jobService.isJobRunning(schedule);

			if(!isJobRunning){
				boolean status = jobService.deleteJob(schedule);
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
	public SchedulerResponse pause(@RequestBody ScheduleKeys schedule) {
		logger.info( "JobController pause() method");

		if(jobService.isJobWithNamePresent(schedule)){

			boolean isJobRunning = jobService.isJobRunning(schedule);

			if(!isJobRunning){
				boolean status = jobService.pauseJob(schedule);
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
	public SchedulerResponse resume(@RequestBody ScheduleKeys schedule) {
        logger.info("JobController resume() method");

		if(jobService.isJobWithNamePresent(schedule)){
			String jobState = jobService.getJobState(schedule);

			if(jobState.equals("PAUSED")){
				logger.info("Job current state is PAUSED, Resuming job...");
				boolean status = jobService.resumeJob(schedule);

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
	public SchedulerResponse updateJob(@RequestBody SchedulerJobDetail jobDetail){
		logger.info("JobController updateJob() method ");

		//Job Name is mandatory
		if(jobDetail.getJobName() == null || jobDetail.getJobName().trim().equals("")){
			return getServerResponse(ServerResponseCode.JOB_NAME_NOT_PRESENT, false);
		}
        ScheduleKeys scheduleKeys = new ScheduleKeys();
        scheduleKeys.setGroupName(jobDetail.getJobGroup());
        scheduleKeys.setJobName(jobDetail.getJobName());
        scheduleKeys.setCategoryId(jobDetail.getCategoryID());

		//Edit Job
		if(jobService.isJobWithNamePresent(scheduleKeys)){
			
			if(jobDetail.getCronExpression() == null || jobDetail.getCronExpression().trim().equals("")){
				//Single Trigger
				boolean status = jobService.updateOneTimeJob(jobDetail.getJobName(), jobDetail.getJobScheduleTime());
				if(status){
					return getServerResponse(ServerResponseCode.SUCCESS, jobService.getAllJobs(jobDetail.getJobGroup()
                            ,jobDetail.getCategoryID()));
				}else{
					return getServerResponse(ServerResponseCode.ERROR, false);
				}
				
			}else{
				//Cron Trigger
				boolean status = jobService.updateCronJob(jobDetail.getJobName(), jobDetail.getJobScheduleTime(),
						jobDetail.getCronExpression());
				if(status){
					return getServerResponse(ServerResponseCode.SUCCESS, jobService.getAllJobs(jobDetail.getJobGroup()
                            ,jobDetail.getCategoryID()));
				}else{
					return getServerResponse(ServerResponseCode.ERROR, false);
				}				
			}

		}else{
			return getServerResponse(ServerResponseCode.JOB_DOESNT_EXIST, false);
		}
	}

	@RequestMapping(value ="jobs",method = RequestMethod.POST)
	public SchedulerResponse getAllJobs(@RequestBody FetchByCategoryBean schedule){
        logger.info("JobController getAllJobs() method");

		List<Map<String, Object>> list = jobService.getAllJobs(schedule.getGroupkey()
                ,schedule.getCategoryId());
		return getServerResponse(ServerResponseCode.SUCCESS, list);
	}

	@RequestMapping(value ="fetchJob",method = RequestMethod.POST)
	public SchedulerResponse getJobDetails(@RequestBody ScheduleKeys schedule){
		logger.info("JobController getJobDetails() method");

		//Job Name is mandatory
		if(schedule.getJobName() == null || schedule.getJobName().trim().equals("")){
			return getServerResponse(ServerResponseCode.JOB_NAME_NOT_PRESENT, false);
		}
		
		Map<String, Object> status = jobService.getJobDetails(schedule);
		return getServerResponse(ServerResponseCode.SUCCESS, status);
	}

	@RequestMapping(value ="isJobRunning",method = RequestMethod.POST)
	public SchedulerResponse isJobRunning(@RequestBody ScheduleKeys schedule) {
		logger.info("JobController isJobRunning() method");

		boolean status = jobService.isJobRunning(schedule);
		return getServerResponse(ServerResponseCode.SUCCESS, status);
	}

	@RequestMapping(value ="jobState",method = RequestMethod.POST)
	public SchedulerResponse getJobState(@RequestBody ScheduleKeys schedule) {
        logger.info("JobController getJobState() method");

		String jobState = jobService.getJobState(schedule);
		return getServerResponse(ServerResponseCode.SUCCESS, jobState);
	}

	@RequestMapping(value ="stop",method = RequestMethod.POST)
	public SchedulerResponse stopJob(@RequestBody ScheduleKeys schedule) {
        logger.info("JobController stopJob() method");

		if(jobService.isJobWithNamePresent(schedule)){

			if(jobService.isJobRunning(schedule)){
				boolean status = jobService.stopJob(schedule);
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
	public SchedulerResponse startJobNow(@RequestBody ScheduleKeys schedule) {
		logger.info("JobController startJobNow() method");

		if(jobService.isJobWithNamePresent(schedule)){

			if(!jobService.isJobRunning(schedule)){
				boolean status = jobService.startJobNow(schedule);

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
