package com.synchronoss.saw.storage.proxy.service.executionResultMigrationService;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.synchronoss.saw.analysis.metadata.AnalysisMetadata;
import com.synchronoss.saw.analysis.service.migrationservice.MigrationStatusObject;
import com.synchronoss.saw.model.SipQuery;
import com.synchronoss.saw.storage.proxy.model.ExecutionResult;
import com.synchronoss.saw.storage.proxy.model.ExecutionType;
import com.synchronoss.saw.storage.proxy.service.StorageProxyService;
import com.synchronoss.saw.storage.proxy.service.productSpecificModuleService.ProductModuleMetaStore;
import com.synchronoss.saw.util.SipMetadataUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import com.synchronoss.sip.utils.RestUtil;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.ojai.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author Alok.KumarR
 * @since 3.3.0
 */
@Service
public class MigrateAnalysisService {
  private static final Logger LOGGER = LoggerFactory.getLogger(MigrateAnalysisService.class);

  @Value("${metastore.base}")
  @NotNull
  private String basePath;

  @Value("${metastore.migration}")
  @NotNull
  private String migrationStatusTable;

  @Value("${metastore.binary-store-path}")
  @NotNull
  private String binaryTablePath;

  @Value("${metadata.service.transporthost}")
  private String proxyAnalysisUrl;

  @Value("${metadata.service.execution-migration-flag}")
  private boolean migrationFlag;

  @Autowired private RestUtil restUtil;

  @Autowired private HBaseUtil hBaseUtil;

  @Autowired private StorageProxyService proxyService;

  @Autowired private MigrateExecutions migrateExecutions;

  private Map<String, Boolean> migratedAnalysis;

  private AnalysisMetadata analysisMetadataStore = null;

  MigrationStatusObject migrationStatusObject = new MigrationStatusObject();

  @PostConstruct
  private void init() {
    if (migrationFlag) {
      LOGGER.info("Execution Result Migration set to true, Starting Migration !!");
      convertBinaryStoreToDslJsonStore();
      LOGGER.info("Execution Migration completed !! ");
    }
  }

  /** Conversion from binary store to jason store */
  public void convertBinaryStoreToDslJsonStore() {
    List<MigrationStatusObject> mgObj = getMigratedAnalysis();
    Map<String, Boolean> analysisIds = extractAnalysisId(mgObj);
    LOGGER.debug("Total number analysis migrated : " + analysisIds.size());
    if (analysisIds != null && !analysisIds.isEmpty()) {
      try {
        // base check - open Hbase connection
        Connection connection = hBaseUtil.getConnection();
        for (Map.Entry<String, Boolean> entry : analysisIds.entrySet()) {
          Boolean flag = entry.getValue();
          String analysisId = entry.getKey();
          if (analysisId != null && !flag) {
            LOGGER.debug("Fetch execution Ids for migration.!");
            Set<String> executionIds = getExecutionIds(analysisId);
            LOGGER.debug("Total count of execution Ids : ", executionIds.size());
            if (!executionIds.isEmpty()) {
              readExecutionResultFromBinaryStore(executionIds, analysisId, connection);
            }
          }
        }

      } catch (Exception ex) {
        LOGGER.error("{ Stack Trace }" + ex);
      } finally {
        // finally close connection after all execution
        hBaseUtil.closeConnection();
      }
    }
  }

  /**
   * Read all execution result
   *
   * @param executionIds
   */
  public void readExecutionResultFromBinaryStore(
      Set<String> executionIds, String analysisId, Connection connection) {
    LOGGER.debug("Fetch execution start here with this table :" + basePath + binaryTablePath);

    try {
      for (String executionId : executionIds) {
        Table table = hBaseUtil.getTable(connection, basePath + binaryTablePath);
        Get get = new Get(Bytes.toBytes(executionId));
        Result result = table.get(get);
        JsonParser parser = new JsonParser();

        byte[] contentBinary = result.getValue("_source".getBytes(), "content".getBytes());
        if (contentBinary != null && contentBinary.length > 0) {
          JsonObject content = parser.parse(new String(contentBinary)).getAsJsonObject();

          String type = null;
          if (content.has("type")
              && content.get("type") != null
              && !content.get("type").isJsonNull()) {
            type = content.get("type").getAsString();
          }
          LOGGER.debug("Contents from Binary Store : " + content.toString() +" and Type : " + type);
          if (type != null && type.matches("pivot|chart")) {
            analysisId = analysisId != null ? analysisId : content.get("id").getAsString();
            JsonElement executedByElement = content.get("executedBy");
            String executedBy =
                executedByElement != null && !executedByElement.isJsonNull()
                    ? executedByElement.getAsString()
                    : null;

            JsonElement executionTypeElement = content.get("executionType");
            String executionType =
                executionTypeElement != null && !executionTypeElement.isJsonNull()
                    ? executionTypeElement.getAsString()
                    : null;

            JsonElement executionResultElement = content.get("execution_result");
            String executionStatus =
                executionResultElement != null && !executionResultElement.isJsonNull()
                    ? executionResultElement.getAsString()
                    : null;

            JsonElement executionFinishTsElement = content.get("execution_finish_ts");

            String executionFinishTs =
                executionFinishTsElement != null && !executionFinishTsElement.isJsonNull()
                    ? executionFinishTsElement.getAsString()
                    : null;
            Long finishTs = executionFinishTs != null ? Long.valueOf(executionFinishTs) : 0L;

            JsonObject queryBuilder = null;
            JsonElement queryBuilderElement = content.get("queryBuilder");
            if (queryBuilderElement != null && !queryBuilderElement.isJsonNull()) {
              queryBuilder = new JsonObject();
              queryBuilder.add("queryBuilder", content.get("queryBuilder"));
            }
            SipQuery sipQuery =
                queryBuilder != null ? migrateExecutions.migrate(queryBuilder) : null;

            migrationStatusObject.setAnalysisId(analysisId);
            migrationStatusObject.setAnalysisMigrated(true);
            migrationStatusObject.setType(type);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode dataNode = null, queryNode = null;
            byte[] dataObject = result.getValue("_objects".getBytes(), "data".getBytes());
            if (dataObject != null && dataObject.length > 0) {
              dataNode = objectMapper.readTree(new String(dataObject));
              LOGGER.debug("Data Json Node which need to parsed for pivot/chart : {}", dataNode);
            }

            byte[] contentObject = result.getValue("_source".getBytes(), "content".getBytes());
            if (contentObject != null && contentObject.length > 0) {
              JsonNode jsonNode = objectMapper.readTree(new String(contentObject));
              queryNode =
                  jsonNode != null && !jsonNode.isNull() ? jsonNode.get("queryBuilder") : null;
              LOGGER.debug("Query Node which need to parsed for pivot/chart : {}", queryNode);
            }

            Object dslExecutionResult = null;
            if (dataNode != null && queryNode != null) {
                dslExecutionResult = convertOldExecutionToDSLExecution(type, dataNode, queryNode);
            }
            LOGGER.debug("dslExecutionResult : {}", dslExecutionResult);

            // store the execution result in json store
            saveDSLJsonExecution(
                executionId,
                executionType,
                executedBy,
                sipQuery,
                analysisId,
                finishTs,
                executionStatus,
                dslExecutionResult);
            migrationStatusObject.setExecutionsMigrated(true);
            migrationStatusObject.setMessage("Success");
            if (saveMigrationStatus(migrationStatusObject, migrationStatusTable, basePath)) {
              LOGGER.info(
                  "Migration result saved successfully !! : {}"
                      + migrationStatusObject.isExecutionsMigrated());
            } else {
              LOGGER.error("Unable to write update AnalysisMigration table!!");
            }
          } else {
            JsonElement queryBuilderElement = content.get("queryBuilder");
            String format = "is missing";
            if (type == null) {
              format = "type " + format;
            }
            if (queryBuilderElement == null) {
              format = ",query builder " + format;
            }
            migrationStatusObject.setExecutionsMigrated(false);
            migrationStatusObject.setMessage(format);
            saveMigrationStatus(migrationStatusObject, migrationStatusTable, basePath);
          }
        }
      }
    } catch (Exception ex) {
      LOGGER.error("Execution failed due to missing data." + ex);
      migrationStatusObject.setExecutionsMigrated(false);
      migrationStatusObject.setMessage("Failed while migration: " + ex.getMessage());
      saveMigrationStatus(migrationStatusObject, migrationStatusTable, basePath);
    }
  }

  /**
   * This method to convert old execution result to new DSL execution results.
   *
   * @param type
   * @param dataNode
   * @param queryNode
   * @return @{@link Object}
   */
  public Object convertOldExecutionToDSLExecution(
      String type, JsonNode dataNode, JsonNode queryNode) {
    List<Object> objectList = new ArrayList<>();
    Object dataConverter;
    try {
      switch (type) {
        case "chart":
          dataConverter = new ChartResultMigration();
          objectList = ((ChartResultMigration) dataConverter).parseData(dataNode, queryNode);
          break;
        case "pivot":
          dataConverter = new PivotResultMigration();
          objectList = ((PivotResultMigration) dataConverter).parseData(dataNode, queryNode);
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
    } catch (UnsupportedOperationException e) {
      LOGGER.error(e.getMessage());
    }
    return objectList;
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
    try {
      List<Object> objectList = new ArrayList<>();
      objectList.add(dslExecutionResult);

      ExecutionResult executionResult = new ExecutionResult();
      executionResult.setExecutionId(executionId);
      executionResult.setDslQueryId(dslQueryId);
      executionResult.setSipQuery(sipQuery);
      executionResult.setStartTime(finishedTime);
      executionResult.setFinishedTime(finishedTime);
      executionResult.setData(objectList);
      executionResult.setExecutionType(ExecutionType.valueOf(executionType));
      executionResult.setStatus(executionStatus);
      executionResult.setExecutedBy(executedBy);
      proxyService.saveDslExecutionResult(executionResult);
      LOGGER.info("Execution Result Stored successfully in jason Store.");
    } catch (Exception ex) {
      LOGGER.error(" {Stack trace} : " + ex);
      LOGGER.error("Error occurred during saving Execution Result :" + ex.getMessage());
    }
  }

  /**
   * Returns the list of Migrated Analysis results.
   *
   * @return
   */
  public List<MigrationStatusObject> getMigratedAnalysis() {
    LOGGER.debug("Inside getMigratedAnalysis() !!");
    List<Document> docs = null;
    ProductModuleMetaStore productModuleMetaStore = null;
    List<MigrationStatusObject> analysisList = new ArrayList<>();
    try {
      productModuleMetaStore = new ProductModuleMetaStore(migrationStatusTable, basePath);
      docs = productModuleMetaStore.searchAll();
      if (docs.isEmpty()) {
        LOGGER.info("No Analysis present for migration!!");
        return null;
      }
      ObjectMapper mapper = new ObjectMapper();
      mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
      for (Document d : docs) {
        analysisList.add(mapper.readValue(d.asJsonString(), MigrationStatusObject.class));
      }
    } catch (Exception e) {
      LOGGER.error("Exception occurred while reading the MaprDB table : {}", migrationStatusTable);
    }
    return analysisList;
  }

  /**
   * Returns Map of Analysis id and definition migration status flag.
   *
   * @param migrationStatusList
   * @return
   */
  public Map<String, Boolean> extractAnalysisId(List<MigrationStatusObject> migrationStatusList) {
    migratedAnalysis = new HashMap<>();
    for (MigrationStatusObject mso : migrationStatusList) {
      migratedAnalysis.put(mso.getAnalysisId(), mso.isAnalysisMigrated());
    }
    return migratedAnalysis;
  }

  /**
   * Saves migration status to a file.
   *
   * @param msObj
   * @param migrationStatusTable
   * @param basePath
   * @return
   */
  private boolean saveMigrationStatus(
      MigrationStatusObject msObj, String migrationStatusTable, String basePath) {
    boolean status = true;

    LOGGER.info("Started Writing into MaprDB, id : {} ", msObj.getAnalysisId());
    try {
      analysisMetadataStore = new AnalysisMetadata(migrationStatusTable, basePath);
      LOGGER.debug("Connection established with MaprDB..!!");
      LOGGER.info("Started Writing the status into MaprDB, id : {} ", msObj.getAnalysisId());
      ObjectMapper mapper = new ObjectMapper();
      JsonElement parsedMigrationStatus =
          SipMetadataUtils.toJsonElement(mapper.writeValueAsString(msObj));
      analysisMetadataStore.update(msObj.getAnalysisId(), parsedMigrationStatus);
    } catch (Exception e) {
      LOGGER.error("Error occurred while writing the status to location: {}", msObj.toString());
      LOGGER.error("stack trace : {}" + e.getMessage());

      status = false;
    }

    return status;
  }

  /**
   * Returns all executions for the analysis Id
   *
   * @param analysisId
   * @return
   */
  private Set<String> getExecutionIds(String analysisId) {
    RestTemplate restTemplate = restUtil.restTemplate();
    Set<String> executionIds = new HashSet<>();
    String url = proxyAnalysisUrl + "/{analysisId}/executions";

    String executionResult = restTemplate.getForObject(url, String.class, analysisId);
    Gson gson = new Gson();
    JsonObject executions = gson.fromJson(executionResult, JsonObject.class);
    if (executions != null && !executions.isJsonNull()) {
      JsonArray executionList = executions.getAsJsonArray("executions");
      if (executionList != null) {
        executionList.forEach(
            (JsonObject -> executionIds.add(JsonObject.getAsJsonObject().get("id").getAsString())));
      }
      LOGGER.info("Number of execution for analysis Id : " + executionIds.size());
    }
    return executionIds;
  }
}
