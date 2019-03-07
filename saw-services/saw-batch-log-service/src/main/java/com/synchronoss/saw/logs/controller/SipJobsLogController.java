package com.synchronoss.saw.logs.controller;

import com.synchronoss.saw.logs.entities.SipJobEntity;
import com.synchronoss.saw.logs.models.SipJobDetails;
import com.synchronoss.saw.logs.repository.SipJobDataRepository;

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


@RestController
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
  @RequestMapping(value = "jobLogs/{jobType}", method = RequestMethod.GET)
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
