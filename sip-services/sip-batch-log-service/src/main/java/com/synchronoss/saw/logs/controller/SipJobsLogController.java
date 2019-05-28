package com.synchronoss.saw.logs.controller;

import com.synchronoss.saw.logs.entities.BisJobEntity;
import com.synchronoss.saw.logs.models.SipJobDetails;
import com.synchronoss.saw.logs.repository.SipJobDataRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
   * Returns list of job logs by jobType type such as SFTP or S3..etc
   * 
   * @param channelType type of channel
   * @return logs
   */
  @ApiOperation(value = "Retrive job logs", nickname = "retriveJobLogs", notes = "", 
      response = SipJobDetails.class, responseContainer = "List")
  @RequestMapping(value = "/logs/jobs/channelType/{channelType}", method = RequestMethod.GET)
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Request has been succeeded without any error"),
      @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
      @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator"),
      @ApiResponse(code = 400, message = "Bad request"),
      @ApiResponse(code = 201, message = "Created"),
      @ApiResponse(code = 401, message = "Unauthorized"),
      @ApiResponse(code = 415, message = "Unsupported Type. "
          + "Representation not supported for the resource") })
  public ResponseEntity<List<SipJobDetails>> logsByJobType(
      @PathVariable("channelType") String channelType,
      @ApiParam(value = "offset number", required = false)
      @RequestParam(name = "offset", defaultValue = "0") int offset,
      @ApiParam(value = "number of objects per page", required = false) 
      @RequestParam(name = "size", defaultValue = "10") int size,
      @ApiParam(value = "sort order", required = false) 
      @RequestParam(name = "sort", defaultValue = "desc") String sort,
      @ApiParam(value = "column name to be sorted", required = false) 
      @RequestParam(name = "column", defaultValue = "createdDate") 
      String column) {
    logger.info("fetching job logs");
    List<BisJobEntity> jobLogs = this.jobRepository.findByChannelType(channelType,
        PageRequest.of(offset, size, Sort.Direction.fromString(sort), column));
    logger.info("job logs fetching done");
    List<SipJobDetails> logs = jobLogs.stream().map(sipJobEntity -> {
      SipJobDetails sipJobDto = new SipJobDetails();
      try {
        BeanUtils.copyProperties(sipJobEntity, sipJobDto);
      } catch (Exception exception) {
        logger.error("exception during copying " + "properties to DTO"
            + exception.getMessage());
      }
      return sipJobDto;
    }).collect(Collectors.toList());
    
    return  new ResponseEntity<List<SipJobDetails>>(logs,HttpStatus.OK);

  }

  /**
   * Returns list of job logs by jobType type such as SFTP or S3..etc
   * 
   * @return logs
   */
  @ApiOperation(value = "Retrive job logs", nickname = "retriveJobLogs", notes = "", 
      response = SipJobDetails.class, responseContainer = "List")
  @RequestMapping(value = "/logs/jobs/{jobId}", method = RequestMethod.GET)
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Request has been succeeded without any error"),
      @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
      @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator"),
      @ApiResponse(code = 400, message = "Bad request"),
      @ApiResponse(code = 201, message = "Created"),
      @ApiResponse(code = 401, message = "Unauthorized"),
      @ApiResponse(code = 415, message = "Unsupported Type. "
          + "Representation not supported for the resource") })
  public  ResponseEntity<SipJobDetails> jobLogsById(@PathVariable("jobId") Long jobId) {
    logger.info("fetching job logs");
    Optional<BisJobEntity> jobLogsData = this.jobRepository.findById(jobId);
    BisJobEntity jobLog = null;
    SipJobDetails sipJobDto = null;
    ResponseEntity<SipJobDetails> response = null;
    if (jobLogsData.isPresent()) {
      sipJobDto = new SipJobDetails();
      jobLog = jobLogsData.get();
      logger.info("job logs fetching done");

      try {
        BeanUtils.copyProperties(jobLog, sipJobDto);
      } catch (Exception exception) {
        logger.error("exception during copying " + "properties to DTO"
            + exception.getMessage());
      }
      response =  ResponseEntity.ok(sipJobDto);
    } else {
      response =  new ResponseEntity<SipJobDetails>(HttpStatus.NOT_FOUND);
    }
    return response;

  }
  
  
  /**
   * Returns list of job logs by jobType type such as SFTP or S3..etc
   * 
   * @return logs
   */
  @ApiOperation(value = "Retrive job logs", nickname = "retriveJobLogs", notes = "", 
      response = SipJobDetails.class, responseContainer = "List")
  @RequestMapping(value = "/logs/jobs/{channelId}/{routeId}", method = RequestMethod.GET)
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Request has been succeeded without any error"),
      @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
      @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator"),
      @ApiResponse(code = 400, message = "Bad request"),
      @ApiResponse(code = 201, message = "Created"),
      @ApiResponse(code = 401, message = "Unauthorized"),
      @ApiResponse(code = 415, message = "Unsupported Type. "
          + "Representation not supported for the resource") })
  public ResponseEntity<List<SipJobDetails>> jobLogsByChanneIdAndRouteId(
      @PathVariable("channelId") Long channelId, 
      @PathVariable("channelId") Long routeId,
      @ApiParam(value = "offset number", required = false) @RequestParam(name = "offset",
        defaultValue = "0") int offset,
      @ApiParam(value = "number of objects per page", required = false) @RequestParam(name = "size",
        defaultValue = "10") int size,
      @ApiParam(value = "sort order", required = false) @RequestParam(name = "sort",
        defaultValue = "desc") String sort,
      @ApiParam(value = "column name to be sorted", required = false) @RequestParam(name = "column",
        defaultValue = "createdDate") String column) {
    logger.info("fetching job logs");
    List<BisJobEntity> jobLogs = this.jobRepository
        .findByBisChannelSysIdAndBisRouteSysId(channelId, routeId, 
        PageRequest.of(offset, size, Sort.Direction.fromString(sort), column));
    List<SipJobDetails> logs =  jobLogs.stream().map(sipJobEntity -> {
      SipJobDetails sipJobDto = new SipJobDetails();
      try {
        BeanUtils.copyProperties(sipJobEntity, sipJobDto);
      } catch (Exception exception) {
        logger.error("exception during copying " + "properties to DTO"
            + exception.getMessage());
      }
      return sipJobDto;
    }).collect(Collectors.toList());
    
    
    return new ResponseEntity<List<SipJobDetails>>(logs,HttpStatus.OK);
    

  }

}
