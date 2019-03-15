package com.synchronoss.saw.logs.controller;

import com.synchronoss.saw.logs.entities.SipJobEntity;
import com.synchronoss.saw.logs.models.SipJobDetails;
import com.synchronoss.saw.logs.repository.SipJobDataRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;




@Api(value = "This Controller provides job level"
    + "operations for sip ingestion in synchronoss analytics platform ")
@RestController
@RequestMapping(value = "/ingestion/batch")
public class SipJobsLogController {

  @Autowired
  SipJobDataRepository jobRepository;
  private static final Logger logger = LoggerFactory
      .getLogger(SipJobsLogController.class);
  
  /**
   * Returns list of job logs by jobType type such as
   * BIS or Analysis..etc
   * 
   * @param jobType type of job
   * @return logs
   */
  @ApiOperation(value = "Retrive job logs", nickname = "retriveJobLogs", notes = "",
      response = SipJobDetails.class, responseContainer = "List")
  @RequestMapping(value = "jobLogs/{jobType}", method = RequestMethod.GET)
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Request has been succeeded without any error"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
          @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator"),
          @ApiResponse(code = 400, message = "Bad request"),
          @ApiResponse(code = 201, message = "Created"),
          @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 415,
              message = "Unsupported Type. " + "Representation not supported for the resource")})
  public List<SipJobDetails> logsByJobType(@PathVariable("jobType") String jobType) {
    logger.info("fetching job logs");
    List<SipJobEntity> jobLogs = this.jobRepository.findByjobType(jobType);
    logger.info("job logs fetching done");
    return jobLogs.stream().map(sipJobEntity -> {
      SipJobDetails sipJobDto = new SipJobDetails();
      try {
        BeanUtils.copyProperties(sipJobEntity, sipJobDto);
      } catch (Exception exception) {
        logger.error("exception during copying " 
            + "properties to DTO" + exception.getMessage());
      }
      return sipJobDto;
    }).collect(Collectors.toList());

  }

}
