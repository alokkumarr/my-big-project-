package com.synchronoss.saw.analysis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.synchronoss.saw.analysis.metadata.AnalysisMetadata;
import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.analysis.service.migrationservice.AnalysisSipDslConverter;
import com.synchronoss.saw.analysis.service.migrationservice.ChartConverter;
import com.synchronoss.saw.analysis.service.migrationservice.DlReportConverter;
import com.synchronoss.saw.analysis.service.migrationservice.EsReportConverter;
import com.synchronoss.saw.analysis.service.migrationservice.GeoMapConverter;
import com.synchronoss.saw.analysis.service.migrationservice.MigrationStatus;
import com.synchronoss.saw.analysis.service.migrationservice.MigrationStatusObject;
import com.synchronoss.saw.analysis.service.migrationservice.PivotConverter;
import com.synchronoss.saw.exceptions.MissingFieldException;
import com.synchronoss.saw.util.FieldNames;
import com.synchronoss.saw.util.HBaseUtils;
import com.synchronoss.saw.util.SipMetadataUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.validation.constraints.NotNull;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.ojai.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MigrateAnalysis {
  private static final Logger logger = LoggerFactory.getLogger(MigrateAnalysis.class);

  private AnalysisMetadata analysisMetadataStore = null;

  @Value("${analysis.binary-migration-required}")
  @NotNull
  private boolean migrationRequired;

  @Value("${metastore.base}")
  @NotNull
  private String basePath;

  @Value("${metastore.analysis}")
  private String tableName;

  @Value("${metastore.migration}")
  private String migrationStatusTable;

  @Value("${metastore.metadataTable}")
  private String metadataTable;

  @Value("${analysis.get-analysis-url}")
  @NotNull
  private String listAnalysisUrl;

  @Value("${metastore.analysis-metadata-path}")
  @NotNull
  private String binaryAnalysisMetadata;

  private static ObjectMapper objectMapper = new ObjectMapper();

  public MigrateAnalysis() {}

  /** Converts analysis definition in binary table to new SIP DSL format. */
  public void convertBinaryToJson() throws Exception {
    logger.trace("Migration process will begin here");
    analysisMetadataStore = new AnalysisMetadata(tableName, basePath);

    JsonArray jsonArray = getAllAnalysis();
    if (jsonArray != null && jsonArray.size() > 0) {
      MigrationStatus migrationStatus = convertAllAnalysis(jsonArray);
      logger.info("Total number of Files for migration : {}", migrationStatus.getTotalAnalysis());
      logger.info("Number of Files Successfully Migrated {}: ", migrationStatus.getSuccessCount());
      logger.info("Number of Files Successfully Migrated {}: ", migrationStatus.getFailureCount());
    }
  }

  /**
   * Migrates a list of analysis to new SIP DSL Structure and writes to mapr db json table.
   *
   * @param analysisList List of old analysis definitions
   * @return
   */
  private MigrationStatus convertAllAnalysis(JsonArray analysisList) {
    MigrationStatus migrationStatus = new MigrationStatus();
    List<MigrationStatusObject> analysisStatus = new ArrayList<>();

    migrationStatus.setTotalAnalysis(analysisList.size());

    AtomicInteger successfulMigration = new AtomicInteger();
    AtomicInteger failedMigration = new AtomicInteger();

    (analysisList)
        .forEach(
            analysisElement -> {
              MigrationStatusObject migrationStatusObject = new MigrationStatusObject();
              JsonObject analysisObject = analysisElement.getAsJsonObject();
              String analysisId = analysisObject.get(FieldNames.ID).getAsString();

              migrationStatusObject.setAnalysisId(analysisId);
              migrationStatusObject.setType(analysisObject.get("type").getAsString());
              Analysis analysis = null;

              try {
                analysis = convertOldAnalysisObjtoSipDsl(analysisElement.getAsJsonObject());
                if (analysis.getSemanticId() != null) {
                  analysis.getSipQuery().setSemanticId(analysis.getSemanticId());
                }
                logger.info("Inserting analysis " + analysis.getId() + " into json store");
                JsonElement parsedAnalysis =
                    SipMetadataUtils.toJsonElement(objectMapper.writeValueAsString(analysis));
                Document document = analysisMetadataStore.searchById(analysis.getId());
                if (document == null && document.isEmpty()) {
                  analysisMetadataStore.create(analysis.getId(), parsedAnalysis);
                }

                migrationStatusObject.setAnalysisId(analysis.getId());
                logger.info(
                    "Analysis Id in inner block : " + migrationStatusObject.getAnalysisId());
                migrationStatusObject.setType(analysis.getType());
                migrationStatusObject.setAnalysisMigrated(true);
                migrationStatusObject.setMessage("Success");
                migrationStatusObject.setExecutionsMigrated(false);
                // Migration of Executions is done via proxy-service
                successfulMigration.incrementAndGet();
              } catch (JsonProcessingException | MissingFieldException exception) {
                logger.error("Unable to convert analysis to json ", exception);

                migrationStatusObject.setAnalysisMigrated(false);
                migrationStatusObject.setMessage(exception.getMessage());
                migrationStatusObject.setExecutionsMigrated(false);
                failedMigration.incrementAndGet();
              } catch (Exception exception) {
                if (analysis != null) {
                  logger.error("Unable to process analysis " + analysis.getId(), exception);
                } else {
                  logger.error("Unable to process analysis", exception);
                }

                migrationStatusObject.setAnalysisMigrated(false);
                migrationStatusObject.setMessage(exception.getMessage());
                migrationStatusObject.setExecutionsMigrated(false);
                failedMigration.incrementAndGet();
              }

              if (saveMigrationStatus(migrationStatusObject, migrationStatusTable, basePath)) {
                logger.info("Successfully written the migration status to MaprDB..!!");
                logger.debug("Written Id = " + migrationStatusObject.getAnalysisId());
              }
              analysisStatus.add(migrationStatusObject);
            });

    migrationStatus.setSuccessCount(successfulMigration.get());
    migrationStatus.setFailureCount(failedMigration.get());

    migrationStatus.setMigrationStatus(analysisStatus);
    return migrationStatus;
  }

  /**
   * Converts old analysis object to new SIP DSL definition.
   *
   * @param analysisObject Single analysis object in old definition
   * @return
   */
  private Analysis convertOldAnalysisObjtoSipDsl(JsonObject analysisObject) throws Exception {
    Analysis analysis = null;

    String analysisType = analysisObject.get("type").getAsString();

    AnalysisSipDslConverter converter = null;

    switch (analysisType) {
      case "chart":
        converter = new ChartConverter();
        break;
      case "pivot":
        converter = new PivotConverter();
        break;
      case "esReport":
        converter = new EsReportConverter();
        break;
      case "report":
        converter = new DlReportConverter();
        break;
      case "map":
        converter = new GeoMapConverter();
        break;
      default:
        logger.error("Unknown analysis type");
        break;
    }

    if (converter != null) {
      analysis = converter.convert(analysisObject);
    } else {
      logger.error("Unknown analysis type");
    }

    return analysis;
  }

  /**
   * Saves migration status to a file.
   *
   * @param migrationStatus Migration status JSON object
   * @return
   */
  private boolean saveMigrationStatus(
      MigrationStatusObject migrationStatus, String migrationStatusTable, String basePath) {
    boolean status = true;

    String id = migrationStatus.getAnalysisId();
    logger.debug("Started Writing into MaprDB, id : " + id);
    logger.debug("Object body to be written : " + new Gson().toJson(migrationStatus));
    logger.debug(
        "Details coming to saveMigration : \n migrationStatusTable = "
            + migrationStatusTable
            + "basePath = "
            + basePath);
    try {
      AnalysisMetadata analysisMetadataStore1 =
          new AnalysisMetadata(migrationStatusTable, basePath);
      logger.debug("Connection established with MaprDB..!!");
      logger.info("Started Writing the status into MaprDB, id : " + id);
      analysisMetadataStore1.update(id, new Gson().toJson(migrationStatus));
    } catch (Exception e) {
      logger.error(e.getMessage());
      logger.error(
          "Error occurred while writing the status to location: " + migrationStatus,
          e.getMessage());

      status = false;
    }

    return status;
  }

  /**
   * Get list of all analysis to remove transport service dependency.
   *
   * @return JsonArray
   */
  public JsonArray getAllAnalysis() {
    JsonArray arrayElements = new JsonArray();
    HBaseUtils utils = new HBaseUtils();
    try {
      Connection connection = utils.getConnection();
      String tablePath = basePath + binaryAnalysisMetadata;
      logger.info("Binary store table path : " + tablePath);
      Table table = utils.getTable(connection, tablePath);

      // Instantiating the Scan class
      Scan scan = new Scan();
      scan.addColumn(Bytes.toBytes("_source"), Bytes.toBytes("content"));
      ResultScanner results = table.getScanner(scan);

      // Reading values from scan result
      for (Result result = results.next(); result != null; result = results.next()) {
        byte[] contentBinary = result.getValue("_source".getBytes(), "content".getBytes());
        if (contentBinary != null && contentBinary.length > 0) {
          com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
          JsonElement element = parser.parse(new String(contentBinary));
          logger.debug("Content binary store data....!!" + element);
          arrayElements.add(element);
        }
      }
    } catch (Exception ex) {
      logger.error("Error while fetch analysis from hbase.", ex);
    } finally {
      utils.closeConnection();
    }
    logger.info("Array size : " + arrayElements.size());
    return arrayElements;
  }

  /**
   * Invokes binary to json migration for analysis metadata.
   *
   * @throws Exception In case of errors
   */
  public void start() throws Exception {
    if (migrationRequired) {
      logger.info("Migration initiated");
      convertBinaryToJson();
    }
    logger.info("Migration ended..");
  }
}
