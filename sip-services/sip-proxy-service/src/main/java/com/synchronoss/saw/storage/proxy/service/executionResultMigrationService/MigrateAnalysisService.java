package com.synchronoss.saw.storage.proxy.service.executionResultMigrationService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.synchronoss.saw.model.SipQuery;
import com.synchronoss.saw.storage.proxy.model.ExecutionResult;
import com.synchronoss.saw.storage.proxy.model.ExecutionType;
import com.synchronoss.saw.storage.proxy.service.ChartResultMigration;
import com.synchronoss.saw.storage.proxy.service.StorageProxyService;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.*;

/**
 * @author Alok.KumarR
 * @since 3.3.0
 */
@Service
public class MigrateAnalysisService {
  private static final Logger LOGGER = LoggerFactory.getLogger(MigrateAnalysisService.class);

  private final Set<String> executionIds = new HashSet<>();

  private String tablePath = "/var/sip/services/metadata/analysis_results";

  @Autowired private StorageProxyService proxyService;

  @Autowired private HBaseUtil hBaseUtil;

  @Autowired
  private QueryDefinitionConverter queryDefinitionConverter;

  @PostConstruct
  private void init() throws Exception {
    // convertBinaryStoreToDslJsonStore();
  }

  public void convertBinaryStoreToDslJsonStore1() throws Exception {
    String type = "Scheduled";
    ObjectMapper mapper = new ObjectMapper();

    File file = new ClassPathResource("old-pivot-execution-data.json").getFile();

    JsonNode jsonNode = mapper.readValue(file, JsonNode.class);

    String executedBy = jsonNode.get("executedBy").asText();

    Object dslExecutionResult = convertOldExecutionToDSLExecution("pivot", jsonNode);

    LOGGER.trace("DSL execution result : " + dslExecutionResult.toString());
    file = new ClassPathResource("sample-dsl-query-data.json").getFile();

    ObjectNode node = mapper.readValue(file, ObjectNode.class);
    String dslQueryId = node.get("analysisId").asText();
    Long startTime = new Date().getTime();
    JsonNode sipNode = node.get("analysis").get("sipQuery");
    // SipQuery sipQuery = mapper.convertValue(sipNode, SipQuery.class);

    LOGGER.trace("DSL execution result store start here.");
    // saveDSLJsonExecution(dslQueryId, type, executedBy, sipQuery, dslQueryId,
    // startedTime,finishedTime, executionStatus, dslExecutionResult);
  }

  /**
   * Save the DSL execution result.
   *
   * @param executionId
   * @param executionType
   * @param executedBy
   * @param sipQuery
   * @param dslQueryId
   * @param finishedTime
   * @param executionStatus
   * @param dslExecutionResult
   */
  public void saveDSLJsonExecution(
      String executionId,
      String executionType,
      String executedBy,
      SipQuery sipQuery,
      String dslQueryId,
      Long finishedTime,
      String executionStatus,
      Object dslExecutionResult) {
    ExecutionResult executionResult = new ExecutionResult();
    executionResult.setExecutionId(executionId);
    executionResult.setDslQueryId(dslQueryId);
    executionResult.setSipQuery(sipQuery);
    executionResult.setStartTime(finishedTime);
    executionResult.setFinishedTime(finishedTime);
    executionResult.setData(dslExecutionResult);
    executionResult.setExecutionType(ExecutionType.fromValue(executionType));
    executionResult.setStatus(executionStatus);
    executionResult.setExecutedBy(executedBy);
    proxyService.saveDslExecutionResult(executionResult);
  }

  /**
   * This method to convert old execution result to new DSL execution results.
   *
   * @param type
   * @param jsonNode
   * @return @{@link Object}
   */
  public Object convertOldExecutionToDSLExecution(String type, JsonNode jsonNode) {
    List<Object> objectList = new ArrayList<>();
    Object dataConverter;
    switch (type) {
      case "chart":
        dataConverter = new com.synchronoss.saw.storage.proxy.service.ChartResultMigration();
        objectList = ((ChartResultMigration) dataConverter).parseData(jsonNode);
        break;
      case "pivot":
        dataConverter = new PivotResultMigration();
        objectList = ((PivotResultMigration) dataConverter).parseData(jsonNode);
        break;
      case "esReport":
        throw new UnsupportedOperationException("ES Report migration not supported yet");
      case "report":
        throw new UnsupportedOperationException("DL Report migration not supported yet");
      case "map":
        throw new UnsupportedOperationException("DL Report migration not supported yet");
      default:
        LOGGER.error("Unknown report type");
        break;
    }
    return objectList;
  }

  public void convertBinaryStoreToDslJsonStore() {
    LOGGER.info("Fetch all start execution Id.!");
    fetchExecutionIdFromBinaryStore();

    LOGGER.info("Total count of execution Ids : " + executionIds.size());
    LOGGER.info("Continue with execution result for execution Ids.");
    if (!executionIds.isEmpty()) {
      for (String executionId : executionIds) {
        if (executionId != null) {
          LOGGER.info("Process execution result start here for one executionId : " + executionId);
          readExecutionResultFromBinaryStore(executionId);
        }
      }
    }
  }

  /** Retrieve all execution Id from binary store */
  public void fetchExecutionIdFromBinaryStore() {
    LOGGER.info("Fetch analysis start here");
    try {
      Connection connection = hBaseUtil.getConnection();
      Table table = hBaseUtil.getTable(connection, tablePath);
      ResultScanner results = hBaseUtil.getResultScanner(table);
      if (results != null) {
        for (Result result : results) {
          executionIds.add(new String(result.getValue("_search".getBytes(), "id".getBytes())));
        }
      }
    } catch (Exception ex) {
      LOGGER.error(ex.getMessage());
    } finally {
      hBaseUtil.closeConnection();
    }
  }

  public void readExecutionResultFromBinaryStore(String executionId) {
    LOGGER.info("Fetch execution start here");
    try {
      Connection connection = hBaseUtil.getConnection();
      Table table = hBaseUtil.getTable(connection, tablePath);
      Get get = new Get(Bytes.toBytes(executionId));
      Result result = table.get(get);
      JsonParser parser = new JsonParser();

      JsonObject content =
          parser
              .parse(new String(result.getValue("_source".getBytes(), "content".getBytes())))
              .getAsJsonObject();

      LOGGER.info("Contents Id from Binary Store :" + content.toString());

      String executionResult = content.get("execution_result").toString();
      LOGGER.info("executionResult :" + executionResult);

      String type = content.get("type").toString();
      LOGGER.info("type :" + type);

      String executionType = content.get("executionType").toString();
      LOGGER.info("executionType :" + executionType);

      String analysisId = content.get("id").toString();
      LOGGER.info("analysisId :" + analysisId);

      String executionFinishTs = content.get("execution_finish_ts").toString();
      LOGGER.info("executionFinishTs :" + executionFinishTs);

      JsonObject queryBuilder = new JsonObject();
      queryBuilder.add("queryBuilder", content.get("queryBuilder"));
      LOGGER.info("queryBuilder from the binary store :" + queryBuilder);

      String executedBy = content.get("executedBy").toString();
      LOGGER.info("executedBy :" + executedBy);

      String executionDataAsString = new String(result.getValue("_objects".getBytes(), "data".getBytes()));
      LOGGER.info(
          "ExecutionData from Binary Store as executionDataAsString :" + executionDataAsString);

/*
      JsonArray executionData =
          parser
              .parse(new String(result.getValue("_objects".getBytes(), "data".getBytes())))
              .getAsJsonArray();

      JsonObject object = executionData.getAsJsonObject();
      LOGGER.info("ExecutionData from Binary Store :" + object.toString());
*/

      LOGGER.info("SipQuery building start here");
      SipQuery sipQuery = queryDefinitionConverter.convert(queryBuilder);
      LOGGER.info("SipQuery from the binary store :" + sipQuery.getArtifacts().size());

    } catch (Exception ex) {
      LOGGER.error(ex.getMessage());
    } finally {
      hBaseUtil.closeConnection();
    }
  }

  /* public static void main(String[] args) throws Exception{
    Configuration config = HBaseConfiguration.create();
    HBaseAdmin admin = new HBaseAdmin(config);

    // verifying the exist of the table
    boolean bool = admin.tableExists("analysis_results");
    System.out.println("analysis_results table exists: " + bool);
  } */
}
