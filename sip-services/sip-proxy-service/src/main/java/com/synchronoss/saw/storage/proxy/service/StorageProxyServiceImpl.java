package com.synchronoss.saw.storage.proxy.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.mapr.db.Admin;
import com.mapr.db.FamilyDescriptor;
import com.mapr.db.MapRDB;
import com.mapr.db.Table;
import com.mapr.db.TableDescriptor;
import com.google.gson.JsonElement;
import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.es.ESResponseParser;
import com.synchronoss.saw.es.ElasticSearchQueryBuilder;
import com.synchronoss.saw.es.QueryBuilderUtil;
import com.synchronoss.saw.es.SIPAggregationBuilder;
import com.synchronoss.saw.model.DataSecurityKey;
import com.synchronoss.saw.model.Field;
import com.synchronoss.saw.model.SipQuery;
import com.synchronoss.saw.storage.proxy.StorageProxyUtils;
import com.synchronoss.saw.storage.proxy.model.ExecuteAnalysisResponse;
import com.synchronoss.saw.storage.proxy.model.ExecutionResponse;
import com.synchronoss.saw.storage.proxy.model.ExecutionResult;
import com.synchronoss.saw.storage.proxy.model.ExecutionType;
import com.synchronoss.saw.storage.proxy.model.StorageProxy;
import com.synchronoss.saw.storage.proxy.model.StorageProxy.Action;
import com.synchronoss.saw.storage.proxy.model.StorageProxy.ResultFormat;
import com.synchronoss.saw.storage.proxy.model.StoreField;
import com.synchronoss.saw.storage.proxy.model.response.ClusterAliasesResponse;
import com.synchronoss.saw.storage.proxy.model.response.ClusterIndexResponse;
import com.synchronoss.saw.storage.proxy.model.response.CountESResponse;
import com.synchronoss.saw.storage.proxy.model.response.CreateAndDeleteESResponse;
import com.synchronoss.saw.storage.proxy.model.response.Hit;
import com.synchronoss.saw.storage.proxy.model.response.SearchESResponse;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.ojai.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sncr.bda.base.MaprConnection;

@Service
public class StorageProxyServiceImpl implements StorageProxyService {

  private static final Logger logger = LoggerFactory.getLogger(StorageProxyServiceImpl.class);

  private final String tempResultTable = "onetime";
  private final String executionResultTable = "executionResult";
  private static final String METASTORE = "services/metadata";

  @Value("${schema.file}")
  private String schemaFile;

  @Value("${metastore.base}")
  @NotNull
  private String basePath;

  @Value("${metastore.ttl-for-onetime}")
  private long timeToLive;

  @Value("${metastore.execution-result-limit}")
  @NotNull
  private long configExecutionLimit;

  @Value("${executor.preview-rows-limit}")
  private Integer previewRowLimit;

  @Value("${executor.publish-rows-limit}")
  private Integer publishRowLimit;

  private String dateFormat = "yyyy-mm-dd hh:mm:ss";
  private String QUERY_REG_EX = ".*?(size|from).*?(\\d+).*?(from|size).*?(\\d+)";
  private String SIZE_REG_EX = ".*?(size).*?(\\d+)";
  @Autowired private StorageConnectorService storageConnectorService;
  @Autowired private DataLakeExecutionService dataLakeExecutionService;

  private int size;
  private String tablePath;

  @PostConstruct
  public void init() {
    tablePath = basePath + File.separator + METASTORE + File.separator + tempResultTable;
    logger.trace("Create Table path :" + tablePath);

    /* Initialize the previews MapR-DB table */
    try (Admin admin = MapRDB.newAdmin()) {
      if (!admin.tableExists(tablePath)) {
        logger.info("Creating previews table: {}", tablePath);
        TableDescriptor table = MapRDB.newTableDescriptor(tablePath);
        FamilyDescriptor family = MapRDB.newDefaultFamilyDescriptor().setTTL(timeToLive);
        table.addFamily(family);
        admin.createTable(table).close();
      }
    }
  }

  @Override
  public StorageProxy execute(StorageProxy proxy) throws Exception {
    logger.trace("Validating Schema is started");
    Boolean validate = StorageProxyUtils.jsonSchemaValidate(proxy, schemaFile);
    logger.trace("Validating Schema is finished");
    StorageProxy response = null;
    if (validate) {
      String storageType = proxy.getStorage().value();
      switch (storageType) {
        case "ES":
          String action = proxy.getAction().value();
          if (action.equals(Action.CREATE.value())
              || action.equals(Action.DELETE.value())
              || action.equals(Action.PIVOT.value())
              || action.equals(Action.COUNT.value())
              || action.equals(Action.SEARCH.value())
              || action.equals(Action.AGGREGATE.value())
              || action.equals(Action.CATINDICES.value())
              || action.equals(Action.MAPPINGALIASES.value())
              || action.equals(Action.CATALIASES.value())) {

            if (action.equals(Action.CREATE.value())
                || action.equals(Action.DELETE.value())
                || action.equals(Action.COUNT.value())
                || action.equals(Action.CATINDICES.value())
                || action.equals(Action.MAPPINGALIASES.value())
                || action.equals(Action.CATALIASES.value())) {
              Preconditions.checkArgument(
                  !(proxy.getResultFormat().value().equals(ResultFormat.TABULAR.value())),
                  "The result format for above operations cannot be in tabular format");
              proxy.setResultFormat(ResultFormat.JSON);
              switch (action) {
                case "create":
                  CreateAndDeleteESResponse createResponse =
                      (CreateAndDeleteESResponse)
                          storageConnectorService.createDocument(proxy.getQuery(), proxy);
                  proxy.setStatusMessage("created with an id :" + createResponse.getId());
                  List<Object> indexData = new ArrayList<>();
                  indexData.add(createResponse);
                  proxy.setResponseTime(new SimpleDateFormat(dateFormat).format(new Date()));
                  proxy.setData(indexData);
                  break;
                case "count":
                  CountESResponse countResponse =
                      (CountESResponse)
                          storageConnectorService.countDocument(proxy.getQuery(), proxy);
                  proxy.setStatusMessage(
                      "Total number of documents are :" + countResponse.getCount());
                  List<Object> countData = new ArrayList<>();
                  countData.add(countResponse);
                  proxy.setResponseTime(new SimpleDateFormat(dateFormat).format(new Date()));
                  proxy.setData(countData);
                  break;
                case "delete":
                  CreateAndDeleteESResponse deleteResponse =
                      (CreateAndDeleteESResponse)
                          storageConnectorService.deleteDocumentById(proxy.getEntityId(), proxy);
                  proxy.setStatusMessage("deleted with an id :" + deleteResponse.getId());
                  List<Object> deleteData = new ArrayList<>();
                  deleteData.add(deleteResponse);
                  proxy.setResponseTime(new SimpleDateFormat(dateFormat).format(new Date()));
                  proxy.setData(deleteData);
                  break;
                case "catIndices":
                  List<ClusterIndexResponse> clusterIndexResponses =
                      storageConnectorService.catClusterIndices(proxy);
                  List<Object> listOfIndices = new ArrayList<>();
                  for (ClusterIndexResponse clusterIndexResponse : clusterIndexResponses) {
                    listOfIndices.add(clusterIndexResponse.getIndex());
                  }
                  proxy.setResponseTime(new SimpleDateFormat(dateFormat).format(new Date()));
                  proxy.setData(listOfIndices);
                  break;
                case "catAliases":
                  List<ClusterAliasesResponse> clusterAliasesResponses =
                      storageConnectorService.catClusterAliases(proxy);
                  List<Object> listOfAliases = new ArrayList<>();
                  for (ClusterAliasesResponse clusterAliasResponse : clusterAliasesResponses) {
                    listOfAliases.add(clusterAliasResponse.getAlias());
                  }
                  proxy.setResponseTime(new SimpleDateFormat(dateFormat).format(new Date()));
                  proxy.setData(listOfAliases);
                  break;
                case "mappingsIndices":
                  List<StoreField> mappingIndexResponse =
                      storageConnectorService.getMappingbyIndex(proxy);
                  List<Object> mappinglist = new ArrayList<>();
                  for (StoreField storeField : mappingIndexResponse) {
                    mappinglist.add(storeField);
                  }
                  mappinglist.add(mappingIndexResponse);
                  proxy.setResponseTime(new SimpleDateFormat(dateFormat).format(new Date()));
                  proxy.setData(mappinglist);
                  break;
                case "mappingsAliases":
                  StorageProxy mappingAliasesResponse =
                      storageConnectorService.getMappingbyAlias(proxy);
                  proxy.setResponseTime(new SimpleDateFormat(dateFormat).format(new Date()));
                  proxy.setData(mappingAliasesResponse.getData());
                  break;
              }
            } // Action only to support JSON format
            else {
              switch (action) {
                case "pivot":
                  if (proxy.getSqlBuilder() != null) {
                    Preconditions.checkArgument(proxy.getQuery() != null, "Query cannot be null.");
                    String query = proxy.getQuery();
                    int size = 0;
                    proxy.setPageSize(0);
                    proxy.setPageNum(0);
                    if (proxy.getQuery().contains("size") && !query.contains("from")) {
                      Pattern p =
                          Pattern.compile(QUERY_REG_EX, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
                      Matcher m = p.matcher(query);
                      if (m.find()) {
                        String fromSize_1 = m.group(1).trim();
                        String fromSize_1_Num = m.group(2);
                        String fromSize_2_Num = m.group(4);
                        if (fromSize_1.equals("size")) {
                          size = Integer.parseInt(fromSize_1_Num);
                        } else {
                          size = Integer.parseInt(fromSize_2_Num);
                        }
                      } // parsing of size & from
                      if (size == 0) {
                        SearchESResponse<?> sncrPivotResponse =
                            (SearchESResponse<?>)
                                storageConnectorService.searchDocuments(proxy.getQuery(), proxy);
                        if (proxy.getResultFormat().value().equals(ResultFormat.JSON.value())) {
                          logger.debug(
                              "Data from Aggregation :" + sncrPivotResponse.getAggregations());
                          ObjectMapper mapperObj = new ObjectMapper();
                          String jsonString =
                              mapperObj.writeValueAsString(sncrPivotResponse.getAggregations());
                          JsonNode objectNode =
                              mapperObj.readTree("{ \"data\":" + jsonString + "}");
                          PivotSNCRFlattener pivotSNCRFlattener =
                              new PivotSNCRFlattener(proxy.getSqlBuilder());
                          List<Object> flatData = pivotSNCRFlattener.parseData(objectNode);
                          proxy.setData(flatData);
                          proxy.setStatusMessage("Data has been retrieved & has been flattened.");
                          logger.debug(
                              "Data from Aggregation converted into Flat Data " + flatData);
                        } else {
                          logger.debug(
                              "Data from Aggregation :" + sncrPivotResponse.getAggregations());
                          ObjectMapper mapperObj = new ObjectMapper();
                          String jsonString =
                              mapperObj.writeValueAsString(sncrPivotResponse.getAggregations());
                          JsonNode objectNode =
                              mapperObj.readTree("{ \"data\":" + jsonString + "}");
                          PivotSNCRFlattener pivotSNCRFlattener =
                              new PivotSNCRFlattener(proxy.getSqlBuilder());
                          List<Map<String, Object>> flatData =
                              pivotSNCRFlattener.parseDataMap(objectNode);
                          List<Object> data = new ArrayList<>();
                          List<Object> tabularData =
                              StorageProxyUtils.getTabularFormat(flatData, StorageProxyUtils.COMMA);
                          for (Object obj : tabularData) {
                            data.add(obj);
                          }
                          proxy.setData(data);
                          proxy.setStatusMessage("Data has been retrieved & has been flattened.");
                          logger.debug(
                              "Data from Aggregation into JSON Format "
                                  + sncrPivotResponse.getAggregations());
                        }
                      } else {
                        proxy.setStatusMessage("size cannot be greater or less than zero.");
                      }
                    } else {
                      proxy.setStatusMessage(
                          "Please provide size with value 'size=0' & it shall not conti 'from' parameter in query.");
                      proxy.setPageSize(0);
                      proxy.setPageNum(0);
                    }
                  } else {
                    proxy.setStatusMessage(
                        "To process the action type of sncrpivot, sqlBuilder is mandatory");
                  }
                  break;
                case "search":
                  Preconditions.checkArgument(proxy.getQuery() != null, "Query cannot be null.");
                  String query = proxy.getQuery();
                  if (query.contains("size") && query.contains("from")) {
                    Pattern p =
                        Pattern.compile(QUERY_REG_EX, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
                    Matcher m = p.matcher(query);
                    if (m.find()) {
                      String fromSize_1 = m.group(1).trim();
                      String fromSize_1_Num = m.group(2);
                      String fromSize_2_Num = m.group(4);
                      if (fromSize_1.equals("size")) {
                        proxy.setPageSize(Integer.parseInt(fromSize_1_Num));
                        proxy.setPageNum(Integer.parseInt(fromSize_2_Num));
                      } else {
                        proxy.setPageSize(Integer.parseInt(fromSize_2_Num));
                        proxy.setPageNum(Integer.parseInt(fromSize_1_Num));
                      }
                    } // parsing of size & from
                    if (proxy.getPageSize() <= 50000) {
                      SearchESResponse<?> searchResponse =
                          (SearchESResponse<?>)
                              storageConnectorService.searchDocuments(proxy.getQuery(), proxy);
                      long actualCount =
                          searchResponse.getHits() != null
                              ? searchResponse.getHits().getTotal()
                              : 0;
                      if (actualCount > 0) {
                        proxy.setStatusMessage(
                            "Number of documents found for provided query :" + actualCount);
                        List<Hit<?>> hits = searchResponse.getHits().getHits();
                        List<Object> data = new ArrayList<>();
                        if (proxy.getResultFormat().value().equals(ResultFormat.JSON.value())) {
                          for (Hit<?> hit : hits) {
                            data.add(hit.getSource());
                          }
                          proxy.setData(data);
                        } // this block only for JSON format
                        else {
                          List<Map<String, Object>> dataHits = new ArrayList<>();
                          for (Hit<?> hit : hits) {
                            dataHits.add(hit.getSource());
                          }
                          List<Object> tabularData =
                              StorageProxyUtils.getTabularFormat(dataHits, StorageProxyUtils.COMMA);
                          for (Object obj : tabularData) {
                            data.add(obj);
                          }
                        } // this block only for Tabular format
                        proxy.setData(data);
                      } else {
                        proxy.setStatusMessage(
                            "There are no documents available with the provided query.");
                      }
                    } // end of check for the size 50000
                    else {
                      proxy.setStatusMessage("The size cannot be greater than 50000");
                    }
                  } else {
                    proxy.setStatusMessage("Please provide size or both parameter in query.");
                    proxy.setPageSize(0);
                    proxy.setPageNum(0);
                  }
                  break;
                case "aggregate":
                  Preconditions.checkArgument(proxy.getQuery() != null, "Query cannot be null.");
                  String aggregateQuery = proxy.getQuery();
                  setSize(0);
                  proxy.setPageSize(0);
                  proxy.setPageNum(0);
                  if (aggregateQuery.contains("size")) {
                    Pattern p =
                        Pattern.compile(SIZE_REG_EX, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
                    Matcher m = p.matcher(aggregateQuery);
                    if (m.find()) {
                      String fromSize_1 = m.group(1).trim();
                      String fromSize_1_Num = m.group(2);

                      if (fromSize_1.equals("size")) {
                        setSize(Integer.parseInt(fromSize_1_Num));
                      } else {
                        setSize(0);
                      } // parsing of size & from
                    }
                    SearchESResponse<?> sncrPivotResponse =
                        (SearchESResponse<?>)
                            storageConnectorService.searchDocuments(proxy.getQuery(), proxy);
                    if (proxy.getResultFormat().value().equals(ResultFormat.JSON.value())) {
                      logger.debug("Data from Aggregation :" + sncrPivotResponse.getAggregations());
                      if (sncrPivotResponse.getAggregations() != null) {
                        proxy.setAggregationData(sncrPivotResponse.getAggregations());
                      }
                      // This else block is for special case to handle if the same request expects
                      // both aggregate & search i.e. ANALYZE module
                      else {
                        List<Hit<?>> hits = sncrPivotResponse.getHits().getHits();
                        List<Object> data = new ArrayList<>();
                        for (Hit<?> hit : hits) {
                          data.add(hit.getSource());
                        }
                        proxy.setData(data);
                      }
                    } else {
                      proxy.setStatusMessage(
                          "Aggregate action's does not support flattening of data yet.");
                    }

                  } // end of aggregate size block
                  else {
                    proxy.setStatusMessage("Please provide size parameter in query.");
                    proxy.setPageSize(0);
                    proxy.setPageNum(0);
                  }
                  break;
              } // end of action
            } // // end of action operation else block
          } // end of action operation  if block
          else {
            proxy.setStatusMessage(
                "This " + action + " is not yet supported by StorageType :" + storageType);
          }
          break;

        case "DL":
          proxy.setStatusMessage("Not supported. This feature is yet to be implemented");
          break;
          // Below are the steps for future implementation to support other storage implementation
          // a)
          // TODO: Storage Type : DL
          // TODO: Validate Spark Query (executing API & prepare the response with for validation
          // using grammar file)
          //
          // (https://github.com/apache/spark/blob/master/sql/catalyst/src/main/antlr4/org/apache/spark/sql/catalyst/parser/SqlBase.g4)
          // TODO: Execute Query or perform create, delete, update operation & Prepare response
          // based on the specification (either JSON or Tabular)
          // TODO: Convert data either into tabular or JSON

        case "RDMS":
          proxy.setStatusMessage("Not supported. This feature is yet to be implemented");
          break;
          // TODO: Storage Type : RDBMS (MYSQL/Oracle)
          // b)
          // TODO: Validate PL/SQL Query (executing API & prepare the response with for validation
          // using grammar file)
          //       (https://github.com/antlr/grammars-v4/tree/master/mysql)
          //       (https://github.com/antlr/grammars-v4/tree/master/plsql)
          // TODO: Execute Query or perform create, delete, update operation & Prepare response
          // based on the specification (either JSON or Tabular)
          // TODO: Convert data either into tabular or JSON

          /* case  "METADATA":
          String actionMetadata = proxy.getAction().value();
          if (actionMetadata.equals(Action.CREATE.value()) || actionMetadata.equals(Action.READ.value()) || actionMetadata.equals(Action.UPDATE.value())
              || actionMetadata.equals(Action.DELETE.value())){
            switch (actionMetadata) {
              case "create" : proxy= storageProxyMetaDataService.createEntryInMetaData(proxy); break;
              case "update" : proxy= storageProxyMetaDataService.updateEntryFromMetaData(proxy);break;
              case "delete" : proxy= storageProxyMetaDataService.deleteEntryFromMetaData(proxy);break;
              case "read" :   proxy= storageProxyMetaDataService.readEntryFromMetaData(proxy);break;
            }
          }
          else {
            proxy.setStatusMessage("This "+actionMetadata+" is not yet supported by StorageType :" + storageType);
          } */
      } // end of switch statement
      response =
          StorageProxyUtils.prepareResponse(
              proxy, proxy != null ? proxy.getStatusMessage() : "data is retrieved.");
    } // end of schema validation if block
    else {
      response = StorageProxyUtils.prepareResponse(proxy, "provided JSON input is not valid.");
    }
    return response;
  }

  @Override
  public List<Object> execute(
      SipQuery sipQuery,
      Integer size,
      DataSecurityKey dataSecurityKey,
      ExecutionType executionType,
      String analysisType,
      Boolean designerEdit)
      throws Exception {
    List<Object> result = null;
    if (analysisType != null && analysisType.equalsIgnoreCase("report")) {
      final String executionId = UUID.randomUUID().toString();
      ExecuteAnalysisResponse response;
      response =
          dataLakeExecutionService.executeDataLakeReport(
              sipQuery, size, dataSecurityKey, executionType, designerEdit, executionId,null,null);
      result = (List<Object>) (response.getData());
    } else {
      result = executeESQueries(sipQuery, size, dataSecurityKey);
    }

    return result;
  }

  /**
   * This Method is used to execute analysis.
   *
   * @param analysis Analysis.
   * @param size Integer.
   * @param dataSecurityKey DataSecurityKey.
   * @param executionType ExecutionType.
   * @return ExecuteAnalysisResponse
   */
  @Override
  public ExecuteAnalysisResponse executeAnalysis(
      Analysis analysis,
      Integer size,
      Integer page,
      Integer pageSize,
      DataSecurityKey dataSecurityKey,
      ExecutionType executionType)
      throws Exception {
    String analysisType = analysis.getType();
    Boolean designerEdit = analysis.getDesignerEdit() == null ? false : analysis.getDesignerEdit();
    SipQuery sipQuery = analysis.getSipQuery();
    final String executionId = UUID.randomUUID().toString();
    ExecuteAnalysisResponse response;
    if (analysisType != null && analysisType.equalsIgnoreCase("report")) {
      if (size == null) {
        switch (executionType) {
          case onetime:
            size = previewRowLimit;
            break;
          case regularExecution:
            size = publishRowLimit;
            break;
          case preview:
            size = previewRowLimit;
            break;
          case publish:
            size = publishRowLimit;
            break;
        }
      }
      response =
          dataLakeExecutionService.executeDataLakeReport(
              sipQuery,
              size,
              dataSecurityKey,
              executionType,
              designerEdit,
              executionId,
              page,
              pageSize);
    } else {
      response = new ExecuteAnalysisResponse();
      List<Object> objList = executeESQueries(sipQuery, size, dataSecurityKey);
      response.setExecutionId(executionId);

      // return only requested data, only for FE
      List<Object> pagingData = pagingData(page, pageSize, objList);
      response.setData(pagingData != null && pagingData.size() > 0 ? pagingData : objList);
      response.setTotalRows(objList != null ? objList.size() : 0L);
    }
    return response;
  }

  private List<Object> executeESQueries(
      SipQuery sipQuery, Integer size, DataSecurityKey dataSecurityKey) throws Exception {
    List<Object> result = null;
    ElasticSearchQueryBuilder elasticSearchQueryBuilder = new ElasticSearchQueryBuilder();
    List<Field> dataFields = sipQuery.getArtifacts().get(0).getFields();
    if (dataSecurityKey == null) {
      logger.info("DataSecurity key is not set !!");
    } else {
      logger.info("DataSecurityKey : " + dataSecurityKey.toString());
    }
    boolean isPercentage =
        dataFields.stream()
            .anyMatch(
                dataField ->
                    dataField.getAggregate() != null
                        && dataField
                            .getAggregate()
                            .value()
                            .equalsIgnoreCase(Field.Aggregate.PERCENTAGE.value()));
    if (isPercentage) {
      SearchSourceBuilder searchSourceBuilder =
          elasticSearchQueryBuilder.percentagePriorQuery(sipQuery);
      JsonNode percentageData =
          storageConnectorService.ExecuteESQuery(
              searchSourceBuilder.toString(), sipQuery.getStore());
      elasticSearchQueryBuilder.setPriorPercentages(
          sipQuery.getArtifacts().get(0).getFields(), percentageData);
    }
    String query;
    query = elasticSearchQueryBuilder.buildDataQuery(sipQuery, size, dataSecurityKey);
    logger.trace("ES -Query {} " + query);
    JsonNode response = storageConnectorService.ExecuteESQuery(query, sipQuery.getStore());
    List<Field> aggregationFields = SIPAggregationBuilder.getAggregationField(dataFields);
    ESResponseParser esResponseParser = new ESResponseParser(dataFields, aggregationFields);
    if (response.get("aggregations") != null)
      result = esResponseParser.parseData(response.get("aggregations"));
    else result = QueryBuilderUtil.buildReportData(response, dataFields);
    return result;
  }

  /**
   * This Method is used to save the ExecutionResult.
   *
   * @param executionResult Execution Result.
   * @return boolean
   */
  @Override
  public Boolean saveDslExecutionResult(ExecutionResult executionResult) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      ExecutionResultStore executionResultStore =
          new ExecutionResultStore(executionResultTable, basePath);
      executionResultStore.create(
          executionResult.getExecutionId(), objectMapper.writeValueAsString(executionResult));
      return true;
    } catch (Exception e) {
      logger.error("Error occurred while storing the execution result data");
      logger.error(e.getMessage());
    }
    return false;
  }

  /**
   * This Method is used to save the ExecutionResult.
   *
   * @param dslQueryId query Id.
   * @return boolean
   */
  @Override
  public List<?> fetchDslExecutionsList(String dslQueryId) {
    try {
      // Create executionResult table if doesn't exists.
      new ExecutionResultStore(executionResultTable, basePath);
      MaprConnection maprConnection = new MaprConnection(basePath, executionResultTable);
      String fields[] = {
        "executionId",
        "dslQueryId",
        "status",
        "startTime",
        "finishedTime",
        "executedBy",
        "executionType"
      };
      ObjectMapper objectMapper = new ObjectMapper();
      ObjectNode node = objectMapper.createObjectNode();
      ObjectNode objectNode = node.putObject("$eq");
      objectNode.put("dslQueryId", dslQueryId);

      List<?> executionLists =
          maprConnection.runMaprDBQuery(fields, node.toString(), "finishedTime", 5);
      // method call to be asynchronossly
      CompletableFuture.runAsync(
          () -> {
            StorageProxyUtil.deleteJunkExecutionResult(
                dslQueryId, configExecutionLimit, basePath, executionResultTable);
            dataLakeExecutionService.cleanDataLakeData();
          });
      return executionLists;
    } catch (Exception e) {
      logger.error("Error occurred while storing the execution result data", e);
    }
    return null;
  }

  @Override
  public ExecutionResponse fetchExecutionsData(
      String executionId, ExecutionType executionType, Integer page, Integer pageSize) {
    ExecutionResponse executionResponse = new ExecutionResponse();
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      String tableName =
          checkTempExecutionType(executionType) ? tempResultTable : executionResultTable;
      ExecutionResultStore executionResultStore = new ExecutionResultStore(tableName, basePath);
      MaprConnection maprConnection = new MaprConnection(basePath, tableName);
      Document doc = executionResultStore.readDocumet(executionId);
      logger.info("Doc : " + doc.asJsonString());
      objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
      ExecutionResult executionResult =
          objectMapper.readValue(doc.asJsonString(), ExecutionResult.class);
      Long totalCount = getTotalRows(doc, null);
      executionResponse.setTotalRows(totalCount);

      // paginated execution data
      Object data =
          maprConnection.fetchPagingData(
              "data", executionResult.getExecutionId(), page, pageSize, totalCount.intValue());
      executionResponse.setData(data != null ? data : executionResult.getData());

      executionResponse.setExecutedBy(executionResult.getExecutedBy());
      executionResponse.setAnalysis(executionResult.getAnalysis());
    } catch (Exception e) {
      logger.error("Error occurred while fetching the execution result data", e);
    }
    return executionResponse;
  }

  ExecutionResult fetchLastExecutionResult(String dslQueryId, MaprConnection maprConnection) {
    try {
      String fields[] = {
        "executionId",
        "dslQueryId",
        "status",
        "startTime",
        "finishedTime",
        "executedBy",
        "executionType",
        "data",
        "analysis"
      };
      if (maprConnection == null) {
        maprConnection = new MaprConnection(basePath, executionResultTable);
      }
      ObjectMapper objectMapper = new ObjectMapper();
      ObjectNode node = objectMapper.createObjectNode();
      ObjectNode objectNode = node.putObject("$eq");
      objectNode.put("dslQueryId", dslQueryId);

      List<JsonNode> elements =
          maprConnection.runMaprDBQuery(fields, node.toString(), "finishedTime", 1);
      // its last execution for the for Query Id , So consider 0 index.
      objectMapper.treeToValue(elements.get(0), ExecutionResult.class);
      ExecutionResult executionResult =
          objectMapper.treeToValue(elements.get(0), ExecutionResult.class);
      return executionResult;
    } catch (Exception e) {
      logger.error("Error occurred while fetching the execution result data", e);
    }
    return null;
  }

  @Override
  public ExecutionResponse fetchLastExecutionsData(
      String dslQueryId, ExecutionType executionType, Integer page, Integer pageSize) {
    ExecutionResponse executionResponse = new ExecutionResponse();
    try {
      String tableName =
          checkTempExecutionType(executionType) ? tempResultTable : executionResultTable;
      MaprConnection maprConnection = new MaprConnection(basePath, tableName);
      ExecutionResult executionResult=fetchLastExecutionResult(dslQueryId,maprConnection);
      List<Object> objList=(List<Object>)executionResult.getData();
			Long totalRows = getTotalRows(null, objList);
			executionResponse.setTotalRows(totalRows);


			// paginated execution data
      Object data =
          maprConnection.fetchPagingData(
              "data", executionResult.getExecutionId(), page, pageSize, totalRows.intValue());
      executionResponse.setData(data != null ? data : executionResult.getData());
      executionResponse.setExecutedBy(executionResult.getExecutedBy());
      executionResponse.setAnalysis(executionResult.getAnalysis());
    } catch (Exception e) {
      logger.error("Error occurred while fetching the execution result data", e);
    }
    return executionResponse;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  @Override
  public Boolean saveTtlExecutionResult(ExecutionResult executionResult) {
    try {
      String tableName =
          checkTempExecutionType(executionResult.getExecutionType())
              ? tempResultTable
              : executionResultTable;
      ExecutionResultStore executionResultStore = new ExecutionResultStore(tableName, basePath);

      ObjectMapper mapper = new ObjectMapper();
      /* Locate the ttl data in MapR-DB */
      Table table = MapRDB.getTable(tablePath);
      logger.trace("Table created for temporary basis :" + table.getName());
      executionResultStore.create(
          table, executionResult.getExecutionId(), mapper.writeValueAsString(executionResult));
      logger.trace("Record inserted successfully .... " + true);
      return true;
    } catch (Exception ex) {
      logger.error("Error occurred while storing the execution result data", ex);
    }
    return false;
  }

  /**
   * Check for temp execution type.
   *
   * @param executionType
   * @return boolean
   */
  private boolean checkTempExecutionType(ExecutionType executionType) {
    return executionType != null
        ? executionType.equals(ExecutionType.onetime)
            || executionType.equals(ExecutionType.preview)
            || executionType.equals(ExecutionType.regularExecution)
        : false;
  }
  /**
   * Count the total number of rows.
   *
   * @param doc
   * @param objList
   * @return long number of row count.
   */
  private long getTotalRows(Document doc, List<Object> objList) {
    try {
      if (doc != null) {
        List<Object> totalRows = doc.getList("data");
        logger.debug("Total number of rows :" + totalRows.size());
        return totalRows.size() > 0 ? totalRows.size() : 0l;
      } else if (objList != null && objList.size() > 0) {
        logger.debug("Total number of rows :" + objList.size());
        return objList.size();
      }
    } catch (Exception ex) {
      logger.error("Error while count the total rows : {}", ex);
    }
    return 0l;
  }

  /**
   * Return List<Object> of paginated data object.
   *
   * @param page
   * @param pageSize
   * @return
   */
  private List<Object> pagingData(Integer page, Integer pageSize, List<Object> dataObj) {
    logger.trace("Page :" + page + " pageSize :" + pageSize + " Data Size :" + dataObj.size());
    // pagination logic
    if (page != null && pageSize != null && dataObj != null && dataObj.size() > 0) {
      int startIndex, endIndex;
      pageSize = pageSize > dataObj.size() ? dataObj.size() : pageSize;
      if (page != null && page > 1) {
        startIndex = (page - 1) * pageSize;
        endIndex = startIndex + pageSize;
      } else {
        startIndex = page != null && page > 0 ? (page - 1) : 0;
        endIndex = startIndex + pageSize;
      }
      logger.trace("Start Index :" + startIndex + " Endindex :" + endIndex);
      return dataObj.subList(startIndex, endIndex);
    }
    return null;
  }

  @Override
  public ExecutionResponse fetchDataLakeExecutionData(
      String executionId, Integer pageNo, Integer pageSize, ExecutionType executionType) {
    ExecutionResponse executionResponse;

    logger.info("Fetch Execution Data for Data Lake report");
    ExecuteAnalysisResponse excuteResp;
    if ((executionType == ExecutionType.onetime
        || executionType == ExecutionType.preview
        || executionType == ExecutionType.regularExecution)) {
      executionResponse = new ExecutionResponse();
      excuteResp =
          dataLakeExecutionService.getDataLakeExecutionData(
              executionId, pageNo, pageSize, executionType,null);
    } else {
      executionResponse = fetchExecutionsData(executionId, executionType, pageNo, pageSize);
      /*here for schedule and publish we are reading data from the same location in DL, so directly
      I am sending publish  as Ui is sending information for only oneTimeExecution,we can send
      schedule as well as */
      excuteResp =
          dataLakeExecutionService.getDataLakeExecutionData(
              executionId, pageNo, pageSize, ExecutionType.publish,null);
    }
    executionResponse.setData(excuteResp.getData());
    executionResponse.setTotalRows(excuteResp.getTotalRows());
    return executionResponse;
  }

  @Override
  public ExecutionResponse fetchLastExecutionsDataForDL(
      String analysisId, Integer pageNo, Integer pageSize) {
    logger.info("Fetching last execution data for DL report");
    ExecutionResult result = fetchLastExecutionResult(analysisId, null);
    ExecutionResponse executionResponse = new ExecutionResponse();
    if (result != null) {
      ExecuteAnalysisResponse executionData =
          dataLakeExecutionService.getDataLakeExecutionData(
              result.getExecutionId(), pageNo, pageSize, result.getExecutionType(),null);
      executionResponse.setData(executionData.getData());
      executionResponse.setTotalRows(executionData.getTotalRows());
      executionResponse.setAnalysis(result.getAnalysis());
      executionResponse.setExecutedBy(result.getExecutedBy());
    }

    return executionResponse;
  }
}
