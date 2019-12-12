package com.synchronoss.saw.scheduler.controller;

import static com.synchronoss.sip.utils.SipCommonUtils.authValidation;
import static com.synchronoss.sip.utils.SipCommonUtils.validatePrivilege;

import com.synchronoss.bda.sip.jwt.TokenParser;
import com.synchronoss.bda.sip.jwt.token.Products;
import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.saw.scheduler.job.CronJob;
import com.synchronoss.saw.scheduler.job.SimpleJob;
import com.synchronoss.saw.scheduler.modal.FetchByCategoryBean;
import com.synchronoss.saw.scheduler.modal.ScheduleKeys;
import com.synchronoss.saw.scheduler.modal.SchedulerJobDetail;
import com.synchronoss.saw.scheduler.modal.SchedulerResponse;
import com.synchronoss.saw.scheduler.service.JobService;
import com.synchronoss.sip.utils.Privileges.PrivilegeNames;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Api(
		value =
				"The controller provides operations of scheduler for synchronoss analytics platform ")
@ApiResponses(
		value = {
				@ApiResponse(code = 202, message = "Request has been accepted without any error"),
				@ApiResponse(code = 400, message = "Bad Request"),
				@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
				@ApiResponse(
						code = 403,
						message = "Accessing the resource you were trying to reach is forbidden"),
				@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
				@ApiResponse(code = 500, message = "Internal server Error. Contact System administrator")
		})
public class JobController extends BaseJobController{

	private static final Logger logger = LoggerFactory.getLogger(JobController.class);
	private static final int TOKEN = 7;

	@Autowired
	@Lazy
	JobService<SchedulerJobDetail> jobService;

	@RequestMapping(value ="schedule",method = RequestMethod.POST)
	@ResponseBody
	public SchedulerResponse schedule(@RequestBody SchedulerJobDetail jobDetail,
				HttpServletResponse response,
        @RequestHeader("Authorization") String authToken) throws IOException {
		logger.info("JobController schedule() start here.");
		String auth = authToken;
		logger.debug("Auth = "+auth);
		jobDetail.setAuth(auth);
		String token = null;

		if (!authValidation(authToken)) {
			logger.error("Invalid authentication token");
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response
					.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
			return getServerResponse(HttpStatus.UNAUTHORIZED.value(), false);
		}

		Ticket authTicket = TokenParser.retrieveTicket(auth.substring(TOKEN));
		if (authTicket == null) {
			logger.error("Invalid authentication token");
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response
					.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
			return getServerResponse(HttpStatus.UNAUTHORIZED.value(), false);
		}
		ArrayList<Products> productList = authTicket.getProducts();
		Long category = Long.parseLong(jobDetail.getCategoryID());
		if (!validatePrivilege(productList, category, PrivilegeNames.EXPORT)) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response
					.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
			logger.error("Invalid authentication token, Unauthorized access!!");
			return getServerResponse(HttpStatus.UNAUTHORIZED.value(), false);
		}
		//Job Name is mandatory
		if(jobDetail.getJobName() == null || jobDetail.getJobName().trim().equals("")){
			response.sendError(HttpStatus.BAD_REQUEST.value(),HttpStatus.BAD_REQUEST.getReasonPhrase());
			return getServerResponse(ServerResponseCode.JOB_NAME_NOT_PRESENT, false);
		}
    boolean isValidDispatch =
        !CollectionUtils.isEmpty(jobDetail.getFtp())
            || !CollectionUtils.isEmpty(jobDetail.getEmailList())
            || !CollectionUtils.isEmpty(jobDetail.getS3());

    String type = jobDetail.getType();
    if (!isValidDispatch && !type.matches("chart|map")) {
      return getServerResponse(ServerResponseCode.ATLEAST_ONE_DISPATCHER_IS_MUST, false);
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
	@ResponseBody
	public void unschedule(@RequestBody ScheduleKeys schedule,
			HttpServletResponse response,
			@RequestHeader("Authorization") String authToken)
			throws IOException {
		if (!authValidation(authToken)) {
			logger.error("Invalid authentication token");
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response
					.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
			return;
		}
		Ticket authTicket = TokenParser.retrieveTicket(authToken.substring(TOKEN));
		ArrayList<Products> productList = authTicket.getProducts();
		Long category = Long.parseLong(schedule.getCategoryId());
		if (!validatePrivilege(productList, category, PrivilegeNames.DELETE)) {
			logger.error("Invalid authentication token, Unauthorized access!!");
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response
					.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
			return;
		}
		logger.info("JobController unschedule() method");
		jobService.unScheduleJob(schedule);
	}

	@RequestMapping(value ="delete",method = RequestMethod.POST)
	@ResponseBody
	public SchedulerResponse delete(@RequestBody ScheduleKeys schedule,
			HttpServletResponse response,
			@RequestHeader("Authorization") String authToken)
			throws IOException {
        logger.info("JobController delete() method");
		if (!authValidation(authToken)) {
			logger.error("Invalid authentication token");
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response
					.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
			return getServerResponse(HttpStatus.UNAUTHORIZED.value(), false);
		}

		Ticket authTicket = TokenParser.retrieveTicket(authToken.substring(TOKEN));
		ArrayList<Products> productList = authTicket.getProducts();
		Long category = Long.parseLong(schedule.getCategoryId());
		if (!validatePrivilege(productList, category, PrivilegeNames.DELETE)) {
			logger.error("Invalid authentication token, Unauthorized access!!");
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response
					.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
			return getServerResponse(HttpStatus.UNAUTHORIZED.value(), false);
		}

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
	@ResponseBody
	public SchedulerResponse pause(@RequestBody ScheduleKeys schedule,
			HttpServletResponse response,
			@RequestHeader("Authorization") String authToken)
			throws IOException {
		logger.info( "JobController pause() method");
		if (!authValidation(authToken)) {
			logger.error("Invalid authentication token");
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response
					.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
			return getServerResponse(HttpStatus.UNAUTHORIZED.value(), false);
		}

		Ticket authTicket = TokenParser.retrieveTicket(authToken.substring(TOKEN));
		ArrayList<Products> productList = authTicket.getProducts();
		Long category = Long.parseLong(schedule.getCategoryId());
		if (!validatePrivilege(productList, category, PrivilegeNames.EDIT)) {
			logger.error("Invalid authentication token, Unauthorized access!!");
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response
					.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
			return getServerResponse(HttpStatus.UNAUTHORIZED.value(), false);
		}

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
	@ResponseBody
	public SchedulerResponse resume(@RequestBody ScheduleKeys schedule,
			HttpServletResponse response,
			@RequestHeader("Authorization") String authToken)
			throws IOException {
        logger.info("JobController resume() method");
		if (!authValidation(authToken)) {
			logger.error("Invalid authentication token");
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response
					.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
			return getServerResponse(HttpStatus.UNAUTHORIZED.value(), false);
		}

		Ticket authTicket = TokenParser.retrieveTicket(authToken.substring(TOKEN));
		ArrayList<Products> productList = authTicket.getProducts();
		Long category = Long.parseLong(schedule.getCategoryId());
		if (!validatePrivilege(productList, category, PrivilegeNames.EDIT)) {
			logger.error("Invalid authentication token, Unauthorized access!!");
			return getServerResponse(HttpStatus.UNAUTHORIZED.value(), false);
		}

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
	@ResponseBody
	public SchedulerResponse updateJob(@RequestBody SchedulerJobDetail jobDetail,
			HttpServletResponse response,
			@RequestHeader("Authorization") String authToken)
			throws IOException {
		logger.info("JobController updateJob() method ");

		logger.debug("Job details  = " + jobDetail);
		if (!authValidation(authToken)) {
			logger.error("Invalid authentication token");
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response
					.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
			return getServerResponse(HttpStatus.UNAUTHORIZED.value(), false);
		}

		Ticket authTicket = TokenParser.retrieveTicket(authToken.substring(TOKEN));
		ArrayList<Products> productList = authTicket.getProducts();
		Long category = Long.parseLong(jobDetail.getCategoryID());
		if (!validatePrivilege(productList, category, PrivilegeNames.EDIT)) {
			logger.error("Invalid authentication token, Unauthorized access!!");
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response
					.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
			return getServerResponse(HttpStatus.UNAUTHORIZED.value(), false);
		}

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
				boolean status = jobService.updateOneTimeJob(jobDetail);
				if(status){
					return getServerResponse(ServerResponseCode.SUCCESS, jobService.getAllJobs(jobDetail.getJobGroup()
                            ,jobDetail.getCategoryID()));
				}else{
					return getServerResponse(ServerResponseCode.ERROR, false);
				}
				
			}else{
				//Cron Trigger
				boolean status = jobService.updateCronJob(jobDetail);
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
	@ResponseBody
	public SchedulerResponse getAllJobs(@RequestBody FetchByCategoryBean schedule,
			HttpServletResponse response,
			@RequestHeader("Authorization") String authToken)
			throws IOException {
        logger.info("JobController getAllJobs() method");
		if (!authValidation(authToken)) {
			logger.error("Invalid authentication token");
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response
					.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
			return getServerResponse(HttpStatus.UNAUTHORIZED.value(), false);
		}

		Ticket authTicket = TokenParser.retrieveTicket(authToken.substring(TOKEN));
		ArrayList<Products> productList = authTicket.getProducts();
		Long category = Long.parseLong(schedule.getCategoryId());
		if (!validatePrivilege(productList, category, PrivilegeNames.ACCESS)) {
			logger.error("Invalid authentication token, Unauthorized access!!");
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response
					.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
			return getServerResponse(HttpStatus.UNAUTHORIZED.value(), false);
		}

		List<Map<String, Object>> list = jobService.getAllJobs(schedule.getGroupkey()
                ,schedule.getCategoryId());
		return getServerResponse(ServerResponseCode.SUCCESS, list);
	}

	@RequestMapping(value ="fetchJob",method = RequestMethod.POST)
	@ResponseBody
	public SchedulerResponse getJobDetails(@RequestBody ScheduleKeys schedule,
			HttpServletResponse response,
			@RequestHeader("Authorization") String authToken)
			throws IOException {
		logger.info("JobController getJobDetails() method");

		if (!authValidation(authToken)) {
			logger.error("Invalid authentication token");
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response
					.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
			return getServerResponse(HttpStatus.UNAUTHORIZED.value(), false);
		}

		Ticket authTicket = TokenParser.retrieveTicket(authToken.substring(TOKEN));
		ArrayList<Products> productList = authTicket.getProducts();
		Long category = Long.parseLong(schedule.getCategoryId());
		if (!validatePrivilege(productList, category, PrivilegeNames.ACCESS)) {
			logger.error("Invalid authentication token, Unauthorized access!!");
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response
					.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
			return getServerResponse(HttpStatus.UNAUTHORIZED.value(), false);
		}

		//Job Name is mandatory
		if(schedule.getJobName() == null || schedule.getJobName().trim().equals("")){
			response.sendError(HttpStatus.BAD_REQUEST.value(),HttpStatus.BAD_REQUEST.getReasonPhrase());
			return getServerResponse(ServerResponseCode.JOB_NAME_NOT_PRESENT, false);
		}
		
		Map<String, Object> status = jobService.getJobDetails(schedule);
		return getServerResponse(ServerResponseCode.SUCCESS, status);
	}

	@RequestMapping(value ="isJobRunning",method = RequestMethod.POST)
	@ResponseBody
	public SchedulerResponse isJobRunning(@RequestBody ScheduleKeys schedule,
			HttpServletResponse response,
			@RequestHeader("Authorization") String authToken)
			throws IOException {
		logger.info("JobController isJobRunning() method");

		if (!authValidation(authToken)) {
			logger.error("Invalid authentication token");
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response
					.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
			return getServerResponse(HttpStatus.UNAUTHORIZED.value(), false);
		}

		Ticket authTicket = TokenParser.retrieveTicket(authToken.substring(TOKEN));
		ArrayList<Products> productList = authTicket.getProducts();
		Long category = Long.parseLong(schedule.getCategoryId());
		if (!validatePrivilege(productList, category, PrivilegeNames.ACCESS)) {
			logger.error("Invalid authentication token, Unauthorized access!!");
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response
					.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
			return getServerResponse(HttpStatus.UNAUTHORIZED.value(), false);
		}
		boolean status = jobService.isJobRunning(schedule);
		return getServerResponse(ServerResponseCode.SUCCESS, status);
	}

	@RequestMapping(value ="jobState",method = RequestMethod.POST)
	@ResponseBody
	public SchedulerResponse getJobState(@RequestBody ScheduleKeys schedule,
			HttpServletResponse response,
			@RequestHeader("Authorization") String authToken)
			throws IOException {
        logger.info("JobController getJobState() method");
		if (!authValidation(authToken)) {
			logger.error("Invalid authentication token");
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response
					.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
			return getServerResponse(HttpStatus.UNAUTHORIZED.value(), false);
		}

		Ticket authTicket = TokenParser.retrieveTicket(authToken.substring(TOKEN));
		ArrayList<Products> productList = authTicket.getProducts();
		Long category = Long.parseLong(schedule.getCategoryId());
		if (!validatePrivilege(productList, category, PrivilegeNames.ACCESS)) {
			logger.error("Invalid authentication token, Unauthorized access!!");
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response
					.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
			return getServerResponse(HttpStatus.UNAUTHORIZED.value(), false);
		}

		String jobState = jobService.getJobState(schedule);
		return getServerResponse(ServerResponseCode.SUCCESS, jobState);
	}

	@RequestMapping(value ="stop",method = RequestMethod.POST)
	@ResponseBody
	public SchedulerResponse stopJob(@RequestBody ScheduleKeys schedule,
			HttpServletResponse response,
			@RequestHeader("Authorization") String authToken)
			throws IOException {
        logger.info("JobController stopJob() method");
		if (!authValidation(authToken)) {
			logger.error("Invalid authentication token");
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response
					.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
			return getServerResponse(HttpStatus.UNAUTHORIZED.value(), false);
		}

		Ticket authTicket = TokenParser.retrieveTicket(authToken.substring(TOKEN));
		ArrayList<Products> productList = authTicket.getProducts();
		Long category = Long.parseLong(schedule.getCategoryId());
		if (!validatePrivilege(productList, category, PrivilegeNames.DELETE)) {
			logger.error("Invalid authentication token, Unauthorized access!!");
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response
					.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
			return getServerResponse(HttpStatus.UNAUTHORIZED.value(), false);
		}

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
	@ResponseBody
	public SchedulerResponse startJobNow(@RequestBody ScheduleKeys schedule,
			HttpServletResponse response,
			@RequestHeader("Authorization") String authToken)
			throws IOException {
		logger.info("JobController startJobNow() method");
		if (!authValidation(authToken)) {
			logger.error("Invalid authentication token");
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response
					.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
			return getServerResponse(HttpStatus.UNAUTHORIZED.value(), false);
		}

		Ticket authTicket = TokenParser.retrieveTicket(authToken.substring(TOKEN));
		ArrayList<Products> productList = authTicket.getProducts();
		Long category = Long.parseLong(schedule.getCategoryId());
		if (!validatePrivilege(productList, category, PrivilegeNames.EXPORT)) {
			logger.error("Invalid authentication token, Unauthorized access!!");
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response
					.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
			return getServerResponse(HttpStatus.UNAUTHORIZED.value(), false);
		}

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
