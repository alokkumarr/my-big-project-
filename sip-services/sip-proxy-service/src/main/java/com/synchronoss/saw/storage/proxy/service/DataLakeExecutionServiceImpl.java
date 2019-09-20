package com.synchronoss.saw.storage.proxy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.synchronoss.saw.dl.spark.DLSparkQueryBuilder;
import com.synchronoss.saw.model.DataSecurityKey;
import com.synchronoss.saw.model.DataSecurityKeyDef;
import com.synchronoss.saw.model.SipQuery;
import com.synchronoss.saw.storage.proxy.model.ExecuteAnalysisResponse;
import com.synchronoss.saw.storage.proxy.model.ExecutionType;
import com.synchronoss.saw.storage.proxy.model.SemanticNode;
import com.synchronoss.sip.utils.RestUtil;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
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

  @Value("${execution.preview-rows-limit}")
  private Integer dlPreviewRowLimit;

  @Value("${metadata.service.host}")
  private String metaDataServiceExport;

  @Autowired private RestUtil restUtil;

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
    String queryShownTOUser=null;

    if (designerEdit) {
      query = sipQuery.getQuery();
      queryShownTOUser = query;
      logger.info(" Query to shown user : " + queryShownTOUser);
      query = dskForManualQuery(sipQuery,query,dataSecurityKey);
      logger.info(" Query after dsk : "+ query);
    } else {
      DLSparkQueryBuilder dlQueryBuilder = new DLSparkQueryBuilder();
      query = dlQueryBuilder.buildDskDataQuery(sipQuery, dataSecurityKey);
      queryShownTOUser = dlQueryBuilder.buildDataQuery(sipQuery);
    }
    sipQuery.setQuery(queryShownTOUser);
    // Required parameters
    String semanticId = sipQuery.getSemanticId();

    int limit = size;

    if (query == null) {
      throw new RuntimeException("Query cannot be null");
    }

    if (semanticId == null) {
      throw new RuntimeException("semanticId cannot be null");
    }

    if (executionId == null) {
      throw new RuntimeException("executionId ID  cannot be null");
    }

    ExecutorQueueManager queueManager = new ExecutorQueueManager(executionType, streamBasePath);
    queueManager.sendMessageToStream(semanticId, executionId, limit, query);

    waitForResult(executionId, dlReportWaitTime);
    return getDataLakeExecutionData(executionId, page, pageSize, executionType,queryShownTOUser);
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
      HFileOperations.deleteEnt(path);
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
      String executionId, Integer pageNo, Integer pageSize, ExecutionType executionType,String query) {
    logger.info("Inside getting executionData for executionId {}", executionId);
    ExecuteAnalysisResponse response = new ExecuteAnalysisResponse();
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
      logger.debug("output location for Dl report:{}", outputLocation);
      FileStatus[] files = HFileOperations.getFilesStatus(outputLocation);
      if (files != null) {
        for (FileStatus fs : files) {
          if (fs.getPath().getName().endsWith(".json")) {
            String path = outputLocation + File.separator + fs.getPath().getName();
            InputStream stream = HFileOperations.readFileToInputStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            resultStream = java.util.stream.Stream.concat(resultStream, reader.lines());
          }
        }
      }
      else{
          throw new RuntimeException("Unable to fetch the data");
      }
      List<Object> objList =
          prepareDataFromStream(resultStream, dlPreviewRowLimit, pageNo, pageSize);
      Long recordCount = getRecordCount(outputLocation);
      response.setData(objList);
      response.setTotalRows(recordCount);
      response.setExecutionId(executionId);
      response.setQuery(query);
      return response;

    } catch (Exception e) {
      logger.error("Exception while reading results for Dl reports: {}", e);
      throw new RuntimeException("Exception while reading results for Dl reports " + e);
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
    if (files != null) {
      for (FileStatus fs : files) {
        if (fs.getPath().getName().endsWith("recordCount")) {
          String path = outputLocation + File.separator + fs.getPath().getName();
          inputStream = HFileOperations.readFileToInputStream(path);
          break;
        }
      }
    } else {
      throw new RuntimeException("Unable to fetch the recordCount");
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

  /**
   * Cleanup data lake of execution result data.
   */
  @Override
  public void cleanDataLakeData() {
    List<String> dlJunkExecutionList = StorageProxyUtil.getDataLakeJunkIds();
    if (dlJunkExecutionList != null && dlJunkExecutionList.size() > 0) {
      dlJunkExecutionList.forEach(junkId -> {
        try {
          String outputLocation = pubSchOutputLocation + File.separator + "output-" + junkId;
          HFileOperations.deleteEnt(outputLocation);
        } catch (Exception ex) {
          logger.error("Error occurred while deleting data lake data : {}", ex);
        }
      });
    }
  }

  public String dskForManualQuery(
      SipQuery sipQuery, String query, DataSecurityKey dataSecurityKey) {
    String dskFilter = " (Select * from ";
    String tempStr = dskFilter;
    boolean flag = false;

    if (dataSecurityKey.getDataSecuritykey() != null
        && dataSecurityKey.getDataSecuritykey().size() != 0) {
      logger.info("DSK :" + dataSecurityKey.getDataSecuritykey().get(0).getName());

      List<String> semanticArtifactNames = getArtifactNames(sipQuery);
      logger.info("ArtifactNames = "+semanticArtifactNames);
      for (String artifactName : semanticArtifactNames) {
        flag = false;
        dskFilter = " (Select * from ";
        for (DataSecurityKeyDef dsk : dataSecurityKey.getDataSecuritykey()) {
          String[] col = dsk.getName().split("\\.");

          if (artifactName.equalsIgnoreCase(col[0])) {
            flag = true;
            if (dskFilter.equalsIgnoreCase(tempStr)) dskFilter = dskFilter.concat(col[0]);
            if (!dskFilter.contains("WHERE")) {
              dskFilter = dskFilter.concat(" WHERE " + dsk.getName() + " in (");
            } else {
              dskFilter = dskFilter.concat(" AND " + dsk.getName() + " in (");
            }
            List<String> values = dsk.getValues();
            int initFlag = 0;
            for (String value : values) {
              dskFilter = initFlag != 0 ? dskFilter.concat(", ") : dskFilter;
              dskFilter = dskFilter.concat("'" + value + "'");
              initFlag++;
            }
            dskFilter = dskFilter.concat(")");
          }
        }

        if (flag) {
          dskFilter = dskFilter.concat(" ) as " + artifactName + " ");
          query = query + " ";
          String artName = " " + artifactName + " ";
          logger.info("Query As is : "+ query);
          logger.info("artName : "+artName);
          logger.info("dskFilter str = "+dskFilter);
          logger.info("Logged query : "+ query.toUpperCase().replaceAll(artName.toUpperCase(), dskFilter));
          query = query.toUpperCase().replaceAll(artName.toUpperCase(), dskFilter);
        }
      }
    }

    logger.info("DSK applied Query : "+ query);
    return query;
  }

  /**
   * This will fetch the artifactNames from metadata and provide.
   *
   * @param sipQuery
   * @return List of String
   */
  public List<String> getArtifactNames(
      SipQuery sipQuery) {
    RestTemplate restTemplate = restUtil.restTemplate();

    String url = metaDataServiceExport + "/internal/semantic/workbench/" + sipQuery.getSemanticId();
    logger.debug("SIP query url for analysis fetch : " + url);
    SemanticNode semanticNode = restTemplate.getForObject(url, SemanticNode.class);
    List<String> artifactNames = new ArrayList<>();
    List<Object> artifactList = semanticNode.getArtifacts();
    for (Object artifact : artifactList) {
      Gson gson = new Gson();
      logger.info("Gson String " + gson.toJson(artifact));
      JsonObject artifactObj = gson.toJsonTree(artifact).getAsJsonObject();
      artifactNames.add(artifactObj.get("artifactName").getAsString());
    }
    return artifactNames;
  }
}
