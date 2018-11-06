package com.synchronoss.saw.scheduler.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.synchronoss.saw.scheduler.job.BisCronJob;
import com.synchronoss.saw.scheduler.job.BisSimpleJob;
import com.synchronoss.saw.scheduler.modal.BISSchedulerJobDetails;
import com.synchronoss.saw.scheduler.modal.FetchByCategoryBean;
import com.synchronoss.saw.scheduler.modal.ScheduleKeys;
import com.synchronoss.saw.scheduler.modal.SchedulerResponse;
import com.synchronoss.saw.scheduler.service.JobService;

import io.swagger.annotations.Api;


@Api
@RestController
@RequestMapping("/bisscheduler/")
public class BISJobController {

    private static final Logger logger = LoggerFactory.getLogger(JobController.class);
  
 
    @Autowired
	@Lazy
    JobService<BISSchedulerJobDetails> bisService;

    @RequestMapping(value ="schedule",method = RequestMethod.POST)
    public SchedulerResponse schedule(@RequestBody BISSchedulerJobDetails jobDetail){
        logger.info("JobController schedule() start here.");

        //Job Name is mandatory
        if(jobDetail.getJobName() == null || jobDetail.getJobName().trim().equals("")){
            return getServerResponse(ServerResponseCode.JOB_NAME_NOT_PRESENT, false);
        }
        ScheduleKeys scheduleKeys = new ScheduleKeys();
        scheduleKeys.setGroupName(jobDetail.getJobGroup());
        scheduleKeys.setJobName(jobDetail.getJobName());
       // scheduleKeys.setCategoryId(jobDetail.getCategoryID());
        logger.info("Check if job Name is unique");
        if(!bisService.isJobWithNamePresent(scheduleKeys)){

            if(jobDetail.getCronExpression() == null || jobDetail.getCronExpression().trim().equals("")){
                logger.info("Simple job ");
                boolean status = bisService.scheduleOneTimeJob(jobDetail, BisSimpleJob.class);
                if(status){
                    return getServerResponse(ServerResponseCode.SUCCESS, bisService.getAllJobs(jobDetail.getJobGroup()
                            ,jobDetail.getEntityId()));
                }else{
                    return getServerResponse(ServerResponseCode.ERROR, false);
                }
                
            } else {
                logger.info("Cron Trigger ");
                boolean status =  bisService.scheduleCronJob(jobDetail,BisCronJob.class);
                if(status){
                    return getServerResponse(ServerResponseCode.SUCCESS, bisService.getAllJobs(jobDetail.getJobGroup()
                            ,jobDetail.getEntityId()));
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
        bisService.unScheduleJob(schedule);
    }

    @RequestMapping(value ="delete",method = RequestMethod.POST)
    public SchedulerResponse delete(@RequestBody ScheduleKeys schedule ) {
        logger.info("JobController delete() method");

        if(bisService.isJobWithNamePresent(schedule)){
            boolean isJobRunning = bisService.isJobRunning(schedule);

            if(!isJobRunning){
                boolean status = bisService.deleteJob(schedule);
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

        if(bisService.isJobWithNamePresent(schedule)){

            boolean isJobRunning = bisService.isJobRunning(schedule);

            if(!isJobRunning){
                boolean status = bisService.pauseJob(schedule);
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

        if(bisService.isJobWithNamePresent(schedule)){
            String jobState = bisService.getJobState(schedule);

            if(jobState.equals("PAUSED")){
                logger.info("Job current state is PAUSED, Resuming job...");
                boolean status = bisService.resumeJob(schedule);

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
    public SchedulerResponse updateJob(@RequestBody BISSchedulerJobDetails jobDetail){
        logger.info("JobController updateJob() method ");

        //Job Name is mandatory
        if(jobDetail.getJobName() == null || jobDetail.getJobName().trim().equals("")){
            return getServerResponse(ServerResponseCode.JOB_NAME_NOT_PRESENT, false);
        }
        ScheduleKeys scheduleKeys = new ScheduleKeys();
        scheduleKeys.setGroupName(jobDetail.getJobGroup());
        scheduleKeys.setJobName(jobDetail.getJobName());
        scheduleKeys.setCategoryId(jobDetail.getEntityId());

        //Edit Job
        if(bisService.isJobWithNamePresent(scheduleKeys)){
            
            if(jobDetail.getCronExpression() == null || jobDetail.getCronExpression().trim().equals("")){
                //Single Trigger
                boolean status = bisService.updateOneTimeJob(jobDetail);
                if(status){
                    return getServerResponse(ServerResponseCode.SUCCESS, bisService.getAllJobs(jobDetail.getJobGroup()
                            ,jobDetail.getEntityId()));
                }else{
                    return getServerResponse(ServerResponseCode.ERROR, false);
                }
                
            }else{
                //Cron Trigger
                boolean status = bisService.updateCronJob(jobDetail);
                if(status){
                    return getServerResponse(ServerResponseCode.SUCCESS, bisService.getAllJobs(jobDetail.getJobGroup()
                            ,jobDetail.getEntityId()));
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

        List<Map<String, Object>> list = bisService.getAllJobs(schedule.getGroupkey()
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
        
        Map<String, Object> status = bisService.getJobDetails(schedule);
        return getServerResponse(ServerResponseCode.SUCCESS, status);
    }

    @RequestMapping(value ="isJobRunning",method = RequestMethod.POST)
    public SchedulerResponse isJobRunning(@RequestBody ScheduleKeys schedule) {
        logger.info("JobController isJobRunning() method");

        boolean status = bisService.isJobRunning(schedule);
        return getServerResponse(ServerResponseCode.SUCCESS, status);
    }

    @RequestMapping(value ="jobState",method = RequestMethod.POST)
    public SchedulerResponse getJobState(@RequestBody ScheduleKeys schedule) {
        logger.info("JobController getJobState() method");

        String jobState = bisService.getJobState(schedule);
        return getServerResponse(ServerResponseCode.SUCCESS, jobState);
    }

    @RequestMapping(value ="stop",method = RequestMethod.POST)
    public SchedulerResponse stopJob(@RequestBody ScheduleKeys schedule) {
        logger.info("JobController stopJob() method");

        if(bisService.isJobWithNamePresent(schedule)){

            if(bisService.isJobRunning(schedule)){
                boolean status = bisService.stopJob(schedule);
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

        if(bisService.isJobWithNamePresent(schedule)){

            if(!bisService.isJobRunning(schedule)){
                boolean status = bisService.startJobNow(schedule);

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
