package com.synchronoss.saw.analysis.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.analysis.service.migrationservice.AnalysisSipDslConverter;
import com.synchronoss.saw.analysis.service.migrationservice.ChartConverter;
import com.synchronoss.saw.analysis.service.migrationservice.EsReportConverter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
  private String existingBinaryAnalysisPath = "/services/metadata/analysis_metadata";

  /**
   * Converts analysis definition in binary table to new SIP DSL format.
   *
   * @param analysisStoreBasePath - Binary store path
   * @param listAnalysisUri - API to get list of existing analysis
   * @param migrationMetadataHome - Binary table path
   */
  public void convertBinaryToJson(
      String analysisStoreBasePath, String listAnalysisUri, String migrationMetadataHome) {
    logger.trace("migration process will begin here");
    HttpHeaders requestHeaders = new HttpHeaders();

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

      List<Analysis> dslAnalysis = new ArrayList<>();
      for (JsonElement analysisElement : analysisList) {
        Analysis analysis = convertOldAnalysisObjtoSipDsl(analysisElement.getAsJsonObject());

        dslAnalysis.add(analysis);
      }

      // TODO: Bulk add analysis to mapr db store
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
        logger.warn("Not implemented yet");
        break;
      case "esReport":
        converter = new EsReportConverter();
        break;
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
    System.out.println("Convert analysis");

    Gson gson = new Gson();
    ClassLoader classLoader = MigrateAnalysis.class.getClassLoader();
    File jsonFile = new File(classLoader.getResource("sample-esreport.json").getPath());
    JsonObject jsonObject = gson.fromJson(new FileReader(jsonFile), JsonObject.class);

    MigrateAnalysis ma = new MigrateAnalysis();

    Analysis analysis = ma.convertOldAnalysisObjtoSipDsl(jsonObject);

    System.out.println(gson.toJsonTree(analysis, Analysis.class));
  }
}
