package com.synchronoss.saw.analysis.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.synchronoss.saw.analysis.metadata.AnalysisMetadata;
import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.analysis.service.migrationservice.AnalysisSipDslConverter;
import com.synchronoss.saw.analysis.service.migrationservice.ChartConverter;
import com.synchronoss.saw.analysis.service.migrationservice.GeoMapConverter;
import com.synchronoss.saw.analysis.service.migrationservice.PivotConverter;
import com.synchronoss.saw.util.FieldNames;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class MigrateAnalysis {
  private static final Logger logger = LoggerFactory.getLogger(MigrateAnalysis.class);

  public String migrationDirectory = "/opt/migration/";
  public String migrationStatusFile = "migrationStatus.json";

  private AnalysisMetadata analysisMetadataStore = null;
  private String listAnalysisUrl;
  private String tableName;
  private String basePath;

  public MigrateAnalysis() {}

  /**
   * Parameterized constructor.
   *
   * @param tableName MAPR DB table
   * @param basePath MAPR DB location
   * @param listAnalysisUri List analysis API endpoint
   * @param statusFilePath Output location for migration status
   */
  public MigrateAnalysis(
      String tableName, String basePath, String listAnalysisUri, String statusFilePath) {

    this.basePath = basePath;
    this.tableName = tableName;
    this.listAnalysisUrl = listAnalysisUri;
    //    this.statusFilePath = statusFilePath;
  }

  /**
   * Converts analysis definition in binary table to new SIP DSL format.
   *
   * @param tableName - Analysis metastore table name
   * @param basePath - Table path
   * @param listAnalysisUri - API to get list of existing analysis
   */
  public void convertBinaryToJson(String tableName, String basePath, String listAnalysisUri)
      throws Exception {
    logger.trace("Migration process will begin here");
    HttpHeaders requestHeaders = new HttpHeaders();
    analysisMetadataStore = new AnalysisMetadata(tableName, basePath);
    requestHeaders.set("Content-type", MediaType.APPLICATION_JSON_UTF8_VALUE);
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    HttpEntity<?> requestEntity = new HttpEntity<Object>(semanticNodeQuery(), requestHeaders);
    logger.debug("Analysis server URL {}", listAnalysisUri + "/analysis");
    String url = listAnalysisUri + "/analysis";
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity analysisBinaryData =
        restTemplate.exchange(url, HttpMethod.POST, requestEntity, JsonNode.class);
    if (analysisBinaryData.getBody() != null) {
      logger.debug("Analysis data = " + analysisBinaryData.getBody());
      JsonNode jsonNode = (JsonNode) analysisBinaryData.getBody();
      JsonElement jelement = new com.google.gson.JsonParser().parse(jsonNode.toString());
      JsonObject analysisBinaryObject = jelement.getAsJsonObject();
      JsonArray analysisList =
          analysisBinaryObject.get("contents").getAsJsonObject().getAsJsonArray("analyze");


      JsonObject migrationStatus = convertAllAnalysis(analysisList);

      saveMigrationStatus(migrationStatus, migrationDirectory, migrationStatusFile);
    }
  }

  /**
   * Migrates a list of analysis to new SIP DSL Structure and writes to mapr db json table.
   *
   * @param analysisList List of old analysis definitions
   * @return
   */
  private JsonObject convertAllAnalysis(JsonArray analysisList) {
    JsonObject migrationStatus = new JsonObject();
    JsonArray analysisStatus = new JsonArray();

    migrationStatus.addProperty("totalAnalysis", analysisList.size());

    AtomicInteger successfulMigration = new AtomicInteger();
    AtomicInteger failedMigration = new AtomicInteger();

    (analysisList)
        .forEach(
            analysisElement -> {
              JsonObject migrationStatusObject = new JsonObject();
              JsonObject analysisObject = analysisElement.getAsJsonObject();
              String analysisId = analysisObject.get(FieldNames.ID).getAsString();

              migrationStatusObject.addProperty("analysisId", analysisId);
              migrationStatusObject.add("type", analysisObject.get("type"));
              Analysis analysis = null;

              try {
                analysis = convertOldAnalysisObjtoSipDsl(analysisElement.getAsJsonObject());
                logger.info("Inserting analysis " + analysis.getId() + " into json store");
                Gson gson = new GsonBuilder().create();
                JsonElement parsedAnalysis = gson.toJsonTree(analysis, Analysis.class);
                analysisMetadataStore.create(analysis.getId(), parsedAnalysis);

                migrationStatusObject.addProperty("migrationStatus", true);
                migrationStatusObject.addProperty("message", "Success");
                successfulMigration.incrementAndGet();
              } catch (JsonProcessingException exception) {
                logger.error("Unable to convert analysis to json");

                migrationStatusObject.addProperty("migrationStatus", false);
                migrationStatusObject.addProperty("message", exception.getMessage());
                failedMigration.incrementAndGet();
              } catch (Exception exception) {
                if (analysis != null) {
                  logger.error("Unable to process analysis " + analysis.getId());
                } else {
                  logger.error("Unable to process analysis");
                }

                migrationStatusObject.addProperty("migrationStatus", false);
                migrationStatusObject.addProperty("message", exception.getMessage());
                failedMigration.incrementAndGet();
              }

              analysisStatus.add(migrationStatusObject);
            });

    migrationStatus.addProperty("success", successfulMigration.get());
    migrationStatus.addProperty("failed", failedMigration.get());

    migrationStatus.add("analysisStatus", analysisStatus);
    return migrationStatus;
  }

  /**
   * Converts old analysis object to new SIP DSL definition.
   *
   * @param analysisObject Single analysis object in old definition
   * @return
   */
  private Analysis convertOldAnalysisObjtoSipDsl(JsonObject analysisObject) {
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
        throw new UnsupportedOperationException("ES Report migration not supported yet");
      case "report":
        throw new UnsupportedOperationException("DL Report migration not supported yet");
      case "map":
        converter = new GeoMapConverter();
        break;
      default:
        logger.error("Unknown report type");
        break;
    }

    if (converter != null) {
      analysis = converter.convert(analysisObject);
    } else {
      logger.error("Unknown report type");
    }

    return analysis;
  }

  /**
   * Saves migration status to a file.
   *
   * @param migrationStatus Migration status JSON object
   * @param migrationDirectory Directory into which the migration status file needs to be written
   * @param migrationStatusFile Output file location
   * @return
   */
  private boolean saveMigrationStatus(
      JsonObject migrationStatus, String migrationDirectory, String migrationStatusFile) {
    boolean status = true;

    String migrationStatusPath = migrationDirectory + "/" + migrationStatusFile;
    try {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      PrintWriter out = new PrintWriter(migrationStatusPath);
      out.println(gson.toJson(migrationStatus));
      out.close();

    } catch (Exception exception) {
      logger.error(
          "Error occurred while writing the status to location: " + migrationStatusPath,
          exception.getMessage());

      status = false;
    }

    return status;
  }

  private String semanticNodeQuery() {
    return "{\n"
        + "   \"contents\":{\n"
        + "      \"keys\":[\n"
        + "         {\n"
        + "            \"module\":\"ANALYZE\"\n"
        + "         }\n"
        + "      ],\n"
        + "      \"action\":\"export\"\n"
        + "   }\n"
        + "}";
  }

  /**
   * Main function.
   *
   * @param args - command line args
   * @throws IOException - In case of file errors
   */
  public static void main1(String[] args) throws IOException {
    String analysisFile = args[0];
    System.out.println("Convert analysis from file = " + analysisFile);

    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    File jsonFile = new File(analysisFile);
    JsonObject jsonObject = gson.fromJson(new FileReader(jsonFile), JsonObject.class);

    JsonObject analyzeObject =
        jsonObject.getAsJsonObject("contents").getAsJsonArray("analyze").get(0).getAsJsonObject();

    MigrateAnalysis ma = new MigrateAnalysis();

    Analysis analysis = ma.convertOldAnalysisObjtoSipDsl(analyzeObject);

    System.out.println(gson.toJson(analysis, Analysis.class));
  }

  /**
   * Main function.
   *
   * @param args Command-line args
   * @throws IOException In-case of file error
   */
  public static void main(String[] args) throws IOException {
    String analysisFile = args[0];
    System.out.println("Convert analysis from file = " + analysisFile);

    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    File jsonFile = new File(analysisFile);

    JsonObject analysisBinaryObject = gson.fromJson(new FileReader(jsonFile), JsonObject.class);

    JsonArray analysisList =
        analysisBinaryObject.get("contents").getAsJsonObject().getAsJsonArray("analyze");

    MigrateAnalysis ma = new MigrateAnalysis();

    JsonObject migrationStatus = ma.convertAllAnalysis(analysisList);
    String migrationStatusPath = ma.migrationDirectory + "/" + ma.migrationStatusFile;
    PrintWriter out = new PrintWriter(migrationStatusPath);
    out.println(gson.toJson(migrationStatus));
    out.close();
  }
}
