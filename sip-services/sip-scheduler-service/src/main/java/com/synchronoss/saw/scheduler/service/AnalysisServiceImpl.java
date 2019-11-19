package com.synchronoss.saw.scheduler.service;

import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.analysis.response.AnalysisResponse;
import com.synchronoss.saw.scheduler.modal.DSLExecutionBean;
import com.synchronoss.saw.scheduler.modal.SchedulerJobDetail;
import com.synchronoss.saw.scheduler.service.ImmutableDispatchBean.Builder;
import com.synchronoss.sip.utils.RestUtil;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AnalysisServiceImpl implements AnalysisService {

  private static final Logger logger = LoggerFactory.getLogger(AnalysisServiceImpl.class);

  @Value("${saw-analysis-service-url}")
  private String analysisUrl;

  @Value("${sip-analysis-proxy-url}")
  private String proxyAnalysisUrl;

  @Value("${sip-metadata-service-url}")
  private String metadataAnalysisUrl;

  @Value("${saw-dispatch-service-url}")
  private String dispatchUrl;

  @Value("${sip-dispatch-row-limit}")
  private int dispatchRowLimit;

  @Autowired
  private RestUtil restUtil;

  private RestTemplate restTemplate;

  private final Logger log = LoggerFactory.getLogger(getClass().getName());

  @PostConstruct
  public void init() throws Exception {
    restTemplate = restUtil.restTemplate();
  }

  public void scheduleDispatch(SchedulerJobDetail analysis) {
    if (analysis.getDescription() == null) analysis.setDescription("");

    // in case if reading recipients list raises exception. Don't skip the scheduler processing
    String recipients = null;
    try {
      recipients = prepareStringFromList(analysis.getEmailList());
    } catch (Exception e) {
      logger.error("Error reading recipients list: " + e.getMessage());
      recipients = "";
    }

    // in case if reading of ftp servers raises exception. Don't skip the scheduler processing
    String ftpServers = null;
    try {
      ftpServers = prepareStringFromList(analysis.getFtp());
    } catch (Exception e) {
      logger.error("Error reading ftp servers list: " + e.getMessage());
      ftpServers = "";
    }

    String s3List = null;
    try {
      if (analysis.getS3() != null) {
        s3List = prepareStringFromList(analysis.getS3());
        logger.debug("S3 List = " + s3List);
      }
    } catch (Exception e) {
      logger.error("Error reading s3 List: " + e.getMessage());
      s3List = "";
    }
    Boolean isZipRequired = analysis.getZip();
    String[] latestExecution;

    latestExecution = fetchLatestFinishedTime(analysis.getAnalysisID());

    logger.debug("latestExecution : " + latestExecution[1]);
    Date date = latestExecution != null ? new Date(Long.parseLong(latestExecution[1])) : new Date();
    DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ssZ");
    format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
    String formatted = format.format(date);
    DispatchBean execution;

    Builder executionBuilder =
        ImmutableDispatchBean.builder()
            .description(analysis.getDescription())
            .fileType(analysis.getFileType())
            .name(analysis.getAnalysisName())
            .userFullName(analysis.getUserFullName())
            .metricName(analysis.getMetricName())
            .jobGroup(analysis.getJobGroup())
            .publishedTime(formatted)
            .zip(isZipRequired);

    if (!recipients.equals("")) {
      executionBuilder.emailList(recipients).fileType(analysis.getFileType());
    }

    if (!ftpServers.equals("")) {
      executionBuilder.ftp(ftpServers);
    }

    if (!s3List.equals("")) {
      executionBuilder.s3(s3List);
    }

    execution = executionBuilder.build();

    String[] param = new String[3];
    param[0] = analysis.getAnalysisID();
    param[1] = latestExecution != null && latestExecution.length > 0 ? latestExecution[0] : null;
    param[2] = analysis.getType();
    String url = dispatchUrl + "/{analysisId}/executions/{executionId}/dispatch/{type}";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<DispatchBean> entity = new HttpEntity<>(execution, headers);

    if (latestExecution.length > 0 && latestExecution[0] != null) {
      restTemplate.postForObject(url, entity, String.class, param);
    }
  }

  private String prepareStringFromList(List<String> source) {
    return String.join(",", source);
  }

  private String[] fetchLatestFinishedTime(String analysisId) {
    logger.debug("New dsl analysis start here..");
    String url = proxyAnalysisUrl + "/{analysisId}/executions";
    logger.debug("proxyAnalysisUrl :" + proxyAnalysisUrl);
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      DSLExecutionBean[] dslExecutionBeans =
          restTemplate.getForObject(url, DSLExecutionBean[].class, analysisId);
      logger.debug("DSLExecutionBean response :" + dslExecutionBeans.length);

      String latestExecutionID = null;
      String latestFinish = null;
      if (dslExecutionBeans != null && dslExecutionBeans.length > 0) {
        // Initialize latestExecution.
        latestExecutionID = dslExecutionBeans[0].getExecutionId();
        logger.debug("latestExecutionID :" + latestExecutionID);
        latestFinish = dslExecutionBeans[0].getFinishedTime();
        logger.debug("latestFinish :" + latestFinish);
        for (DSLExecutionBean executionBean : dslExecutionBeans) {
          if (Long.parseLong(executionBean.getFinishedTime()) > Long.parseLong(latestFinish)
              && (executionBean.getStatus() == null
              || executionBean.getStatus().equalsIgnoreCase("Success"))) {
            latestExecutionID = executionBean.getExecutionId();
            latestFinish = executionBean.getFinishedTime();
          }
        }
      }
      String[] val = new String[2];
      logger.debug("latestExecutionID :" + latestExecutionID);
      logger.debug("latestFinish :" + latestFinish);
      val[0] = latestExecutionID;
      val[1] = latestFinish;
      return val;
    } catch (Exception ex) {
      logger.debug(ex.getMessage());
    }
    return null;
  }

  @Override
  public void executeDslAnalysis(String analysisId, String userId) {
    String dslUrl = metadataAnalysisUrl + "/" + analysisId;
    logger.trace("URL for request body : ", dslUrl);
    AnalysisResponse analysisResponse = restTemplate.getForObject(dslUrl, AnalysisResponse.class);

    Analysis analysis = analysisResponse.getAnalysis();
    logger.trace("Analysis request body : ", analysisResponse.getAnalysis());

    String url = proxyAnalysisUrl
          + "/execute?id="
          + analysisId
          + "&size="
          + dispatchRowLimit
          + "&executionType=scheduled"
          + "&userId="+ userId;

    logger.info("Execute URL for dispatch :", url);
    HttpEntity<?> requestEntity = new HttpEntity<>(analysis);

    restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
  }
}
