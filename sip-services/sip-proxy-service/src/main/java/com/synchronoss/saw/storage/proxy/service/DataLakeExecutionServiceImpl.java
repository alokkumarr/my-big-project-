package com.synchronoss.saw.storage.proxy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.saw.dl.spark.DLSparkQueryBuilder;
import com.synchronoss.saw.model.DataSecurityKey;
import com.synchronoss.saw.model.SipQuery;
import com.synchronoss.saw.storage.proxy.model.ExecuteAnalysisResponse;
import com.synchronoss.saw.storage.proxy.model.ExecutionType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.constraints.NotNull;
import org.apache.hadoop.fs.FileStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sncr.bda.core.file.HFileOperations;

@Service
public class DataLakeExecutionServiceImpl implements DataLakeExecutionService {

  private static final Logger logger = LoggerFactory.getLogger(DataLakeExecutionServiceImpl.class);

  @Value("${executor.streamPath}")
  @NotNull
  private String streamBasePath;

  @Value("${executor.wait-time}")
  private Integer dlReportWaitTime;

  @Value("${executor.preview-output-location}")
  @NotNull
  private String previewOutputLocation;

  @Value("${executor.publish-schedule-output-location}")
  @NotNull
  private String pubSchOutputLocation;

  @Value("${executor.preview-rows-limit}")
  private Integer dlPreviewRowLimit;

  /**
   * This Method is used to execute data lake report.
   *
   * @param sipQuery SipQuery.
   * @param size limit for execution data.
   * @param dataSecurityKey DataSecurityKey.
   * @param executionType ExecutionType.
   * @param designerEdit designer edit.
   * @param executionId executionId.
   * @return ExecuteAnalysisResponse
   */
  public ExecuteAnalysisResponse executeDataLakeReport(
      SipQuery sipQuery,
      Integer size,
      DataSecurityKey dataSecurityKey,
      ExecutionType executionType,
      Boolean designerEdit,
      String executionId,
      Integer page,
      Integer pageSize)
      throws Exception {
    List<Object> result = null;

    String query = null;

    if (designerEdit == true) {
      DLSparkQueryBuilder dlQueryBuilder = new DLSparkQueryBuilder();
      query = dlQueryBuilder.buildDskDataQuery(sipQuery, dataSecurityKey);
    } else {
      query = sipQuery.getQuery();
    }

    // Required parameters
    String semanticId = sipQuery.getSemanticId();

    int limit = size;

    ExecutorQueueManager queueManager = new ExecutorQueueManager(executionType, streamBasePath);
    queueManager.sendMessageToStream(semanticId, executionId, limit, query);

    waitForResult(executionId, dlReportWaitTime);
    return getDataLakeExecutionData(executionId, page, pageSize, executionType);
  }

  private void waitForResult(String resultId, Integer retries) {
    retries = retries == null ? 60 : retries;
    if (!executionCompleted(resultId)) {
      waitForResultRetry(resultId, retries);
    }
  }

  private Boolean executionCompleted(String resultId) {
    String mainPath = streamBasePath != null ? streamBasePath : "/main";
    String path = mainPath + File.separator + "saw-transport-executor-result-" + resultId;
    try {
      HFileOperations.readFile(path);
    } catch (FileNotFoundException e) {
      return false;
    }
    try {
      //      HFileOperations.deleteEnt(path);
    } catch (Exception e) {
      logger.error("cannot get the file in path" + path);
    }
    return true;
  }

  private void waitForResultRetry(String resultId, Integer retries) {
    if (retries == 0) {
      throw new RuntimeException("Timed out waiting for result: " + resultId);
    }
    logger.info("Waiting for result: {}", resultId);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      logger.error("error occurred during thread sleep ");
    }
    waitForResult(resultId, retries - 1);
  }

  /**
   * This Method is returns the datalake execution result.
   *
   * @param executionId executionId.
   * @param pageNo page number.
   * @param pageSize size of the page.
   * @param executionType ExecutionType.
   * @return ExecuteAnalysisResponse
   */
  public ExecuteAnalysisResponse getDataLakeExecutionData(
      String executionId, Integer pageNo, Integer pageSize, ExecutionType executionType) {
    logger.info("Inside getting executionData for executionId {}", executionId);
    try {
      List list = new ArrayList<String>();
      Stream resultStream = list.stream();
      String outputLocation = null;
      if (executionType == (ExecutionType.onetime)
          || executionType == (ExecutionType.preview)
          || executionType == (ExecutionType.regularExecution)) {
        outputLocation = previewOutputLocation + File.separator + "preview-" + executionId;
      } else {
        outputLocation = pubSchOutputLocation + File.separator + "output-" + executionId;
      }
      logger.debug("output location for dfDl report:{}", outputLocation);
      FileStatus[] files = HFileOperations.getFilesStatus(outputLocation);
      for (FileStatus fs : files) {
        if (fs.getPath().getName().endsWith(".json")) {
          String path = outputLocation + File.separator + fs.getPath().getName();
          InputStream stream = HFileOperations.readFileToInputStream(path);
          BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
          resultStream = java.util.stream.Stream.concat(resultStream, reader.lines());
        }
      }
      List<Object> objList =
          prepareDataFromStream(resultStream, dlPreviewRowLimit, pageNo, pageSize);
      Long recordCount = getRecordCount(outputLocation);
      ExecuteAnalysisResponse response = new ExecuteAnalysisResponse();
      response.setData(objList);
      response.setTotalRows(recordCount);
      response.setExecutionId(executionId);
      return response;

    } catch (Exception e) {
      logger.error("Exception while reading results for Dl reports: {}", e);
      throw new RuntimeException("Exception while reading results for Dl reports" + e);
    }
  }

  private List<JsonNode> prepareDataFromStream(
      Stream<String> dataStream, Integer limit, Integer pageNo, Integer pageSize) {
    List<JsonNode> data = new ArrayList<>();
    ObjectMapper mapper = new ObjectMapper();
    if (pageNo == null || pageSize == null) {
      limit = limit != null ? limit : 1000;
      dataStream
          .limit(limit)
          .forEach(
              (element) -> {
                try {
                  JsonNode jsonNode = mapper.readTree(element);
                  data.add((jsonNode));
                } catch (Exception e) {
                  logger.error("error occured while parsing element to json node");
                }
              });
      return data;
    } else {
      int startIndex = (pageNo-1) * pageSize;
      dataStream
          .skip(startIndex)
          .limit(pageSize)
          .forEach(
              (element) -> {
                try {
                  JsonNode jsonNode = mapper.readTree(element);
                  data.add((jsonNode));
                } catch (Exception e) {
                  logger.info("error occured while parsing element to json node");
                }
              });
      logger.debug("Data from the stream  " + data);
      return data;
    }
  }

  private Long getRecordCount(String outputLocation) throws Exception {
      logger.info("Getting record count reading results for Dl reports: {}");
    ObjectMapper mapper = new ObjectMapper();
    InputStream inputStream = null;
    Long count = null;
    FileStatus[] files = HFileOperations.getFilesStatus(outputLocation);
    logger.debug("inside getRecordCount for DL report");
    for (FileStatus fs : files) {
      if (fs.getPath().getName().endsWith("recordCount")) {
        String path = outputLocation + File.separator + fs.getPath().getName();
        inputStream = HFileOperations.readFileToInputStream(path);
        break;
      }
    }
    BufferedReader bufferReader = new BufferedReader(new InputStreamReader(inputStream));
    List<String> list = bufferReader.lines().collect(Collectors.toList());
    if (list.size() > 0) {
      String countString = list.get(0);
      JsonNode jsonNode = mapper.readTree(countString);
      count = jsonNode.get("recordCount").asLong();
      logger.debug("count of the record : {}", count);
    }
    return count;
  }
}
