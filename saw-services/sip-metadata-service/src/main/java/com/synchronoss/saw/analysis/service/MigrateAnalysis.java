package com.synchronoss.saw.analysis.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
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
import com.synchronoss.saw.analysis.service.migrationservice.DlReportConverter;
import com.synchronoss.saw.analysis.service.migrationservice.EsReportConverter;
import com.synchronoss.saw.analysis.service.migrationservice.GeoMapConverter;
import com.synchronoss.saw.analysis.service.migrationservice.PivotConverter;
import com.synchronoss.saw.util.SipMetadataUtils;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

    AnalysisMetadata analysisMetadataStore = new AnalysisMetadata(tableName, basePath);

    requestHeaders.set("Content-type", MediaType.APPLICATION_JSON_UTF8_VALUE);
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    HttpEntity<?> requestEntity = new HttpEntity<Object>(semanticNodeQuery(), requestHeaders);
    logger.debug("Analysis server URL {}", listAnalysisUri + "/analysis");

    String url = listAnalysisUri + "/analysis";
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity analysisBinaryData =
        restTemplate.exchange(url, HttpMethod.GET, requestEntity, Analysis.class);

    if (analysisBinaryData.getBody() != null) {
      Gson gson = new GsonBuilder().create();
      logger.debug("Analysis data = " + analysisBinaryData.getBody());

      // TODO: Check if this works
      JsonObject analysisBinaryObject =
          gson.toJsonTree(analysisBinaryData.getBody()).getAsJsonObject();

      JsonArray analysisList =
          analysisBinaryObject.get("contents").getAsJsonObject().getAsJsonArray("analyze");

      (analysisList)
          .forEach(
              analysisElement -> {
                Analysis analysis =
                    convertOldAnalysisObjtoSipDsl(analysisElement.getAsJsonObject());
                try {

                  logger.info("Inserting analysis " + analysis.getId() + " into json store");
                  JsonElement parsedAnalysis =
                      SipMetadataUtils.toJsonElement(objectMapper.writeValueAsString(analysis));
                  analysisMetadataStore.create(analysis.getId(), parsedAnalysis);
                } catch (JsonProcessingException exception) {
                  logger.error("Unable to convert analysis to json");
                } catch (Exception ex) {
                  if (analysis != null) {
                    logger.error("Unable to process analysis " + analysis.getId());
                  } else {
                    logger.error("Unable to process analysis");
                  }
                }
              });
    }
  }

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
        converter = new EsReportConverter();
        break;
      case "report":
        converter = new DlReportConverter();
        break;
      case "map":
        converter = new GeoMapConverter();
      default:
        logger.error("Unknown chart type");
        break;
    }

    if (converter != null) {
      analysis = converter.convert(analysisObject);
    } else {
      logger.error("Unknown chart type");
    }

    return analysis;
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
  public static void main(String[] args) throws IOException {
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
}
