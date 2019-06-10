package com.synchronoss.saw.analysis.service.migrationservice;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.synchronoss.saw.analysis.metadata.AnalysisMetadata;
import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.analysis.service.AnalysisServiceImpl;
import com.synchronoss.saw.exceptions.SipReadEntityException;
import com.synchronoss.saw.exceptions.SipUpdateEntityException;
import com.synchronoss.saw.model.Artifact;
import com.synchronoss.saw.model.SipQuery;
import com.synchronoss.saw.util.FieldNames;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ojai.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SemanticIdMigrationUtility {

  private static ObjectMapper MAPPER = new ObjectMapper();

  private AnalysisMetadata semanticMetadataStore = null;
  private AnalysisMetadata analysisMetadataStore = null;

  private static String metadataTable;

  private static String basePath;

  private AnalysisServiceImpl analysisService;

  private static String tableName;

  /**
   * Main method to be called from a script.
   *
   * @param args Require no args for this
   */
  public static void main(String[] args) {
    basePath = args[0];
    metadataTable = args[1];
    tableName = args[2];
    SemanticIdMigrationUtility uos = new SemanticIdMigrationUtility();
    uos.updateAnalysisWithSemanticInfo();
  }

  /** Update the existing Analysis in maprDB to match semantic. */
  public void updateAnalysisWithSemanticInfo() {
    analysisService = new AnalysisServiceImpl();
    System.out.println("Start semantic id migration");
    List<Analysis> analyses = getAllAnalyses();
    Map<String, String> semanticMap = getMetaData();
    if (analyses != null && analyses.size() > 0 && semanticMap != null && !semanticMap.isEmpty()) {
      for (Analysis analysis : analyses) {
        updatedSemanticId(analysis, semanticMap);
      }
    }
    System.out.println("Ends semantic id migration");
  }

  /**
   * Return the semantic id for the migration.
   *
   * @param analysis Analysis
   * @param semanticMap Map of semantic id and artifact name
   */
  private void updatedSemanticId(Analysis analysis, Map<String, String> semanticMap) {
    String analysisSemanticId =
        analysis != null && analysis.getSemanticId() != null ? analysis.getSemanticId() : null;
    if (analysisSemanticId != null && semanticMap != null && !semanticMap.isEmpty()) {
      for (Map.Entry<String, String> entry : semanticMap.entrySet()) {
        if (analysisSemanticId.equalsIgnoreCase(entry.getValue())) {
          break;
        } else {
          String semanticArtifactName = entry.getKey();
          SipQuery sipQuery = analysis.getSipQuery();
          if (sipQuery != null) {
            List<Artifact> artifacts = sipQuery.getArtifacts();
            if (artifacts != null && artifacts.size() != 0) {
              String artifactName = artifacts.get(0).getArtifactsName();
              if (semanticArtifactName.equalsIgnoreCase(artifactName)) {
                analysis.setSemanticId(entry.getKey());
                if (updateAnalysis(analysis)) {
                  System.out.println(
                      String.format(
                          "Successfully Updated analysis id [%s] with semantic id - [%s]",
                          analysis.getId(), entry.getValue()));
                } else {
                  System.out.println(
                      String.format(
                          "Failed writing to maprDB!! Analysis id : {%s}", analysis.getId()));
                }
                break;
              }
            }
          }
        }
      }
    }
  }

  /**
   * Return all the semantic details with key/value.
   *
   * @return Map with semantic id and artifact name.
   */
  private Map<String, String> getMetaData() {
    Map<String, String> semanticMap = new HashMap<>();
    List<JsonObject> objDocs = new ArrayList<>();
    try {
      semanticMetadataStore = new AnalysisMetadata(metadataTable, basePath);
      List<Document> docs = semanticMetadataStore.searchAll();
      System.out.println("Semantic Metadata Document : -- >>" + docs.size());
      if (docs != null && !docs.isEmpty() && docs.size() > 0) {
        System.out.println("Inside Semantic Metadata Document.");
        for (Document d : docs) {
          MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
          objDocs.add(toJsonElement(d).getAsJsonObject());
        }
      }
    } catch (Exception e) {
      System.out.println(
          String.format("Exception occurred while fetching Semantic definition. %s", e));
    }

    for (JsonObject semanticData : objDocs) {
      String id = semanticData.get("_id").getAsString();
      if (id != null) {
        JsonArray artifacts = semanticData.getAsJsonArray(FieldNames.ARTIFACTS);
        if (artifacts != null) {
          String artifactName =
              artifacts.get(0).getAsJsonObject().get(FieldNames.ARTIFACT_NAME).getAsString();
          if (artifactName != null) {
            semanticMap.put(artifactName, id);
          }
        }
      }
    }
    System.out.println("Semantcic map {} " + semanticMap.size());
    return semanticMap;
  }

  /**
   * To parse to json.
   *
   * @param doc Document
   * @return JsonElement
   */
  private JsonElement toJsonElement(Document doc) {
    String json = doc.asJsonString();
    com.google.gson.JsonParser jsonParser = new com.google.gson.JsonParser();
    return jsonParser.parse(json);
  }

  /**
   * Convert to JsonElement.
   *
   * @param jsonString Json String
   * @return JsonElement
   */
  public static JsonElement toJsonElement(String jsonString) {
    com.google.gson.JsonParser jsonParser = new com.google.gson.JsonParser();
    JsonElement jsonElement;
    try {
      jsonElement = jsonParser.parse(jsonString);
      System.out.println("json element parsed successfully");
      return jsonElement;
    } catch (JsonParseException jse) {
      System.out.println(
          String.format(
              "Can't parse String to Json, JsonParseException occurred!\n %s",
              jse.getStackTrace().toString()));
      return null;
    }
  }

  /**
   * Retrieves all analyses from Mapr DB.
   *
   * @return List of Analysis
   */
  public List<Analysis> getAllAnalyses() {
    List<Document> doc = null;
    List<Analysis> objDocs = new ArrayList<>();
    try {
      analysisMetadataStore = new AnalysisMetadata(tableName, basePath);
      doc = analysisMetadataStore.searchAll();
      if (doc == null) {
        return null;
      }
      for (Document d : doc) {
        MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objDocs.add(MAPPER.readValue(d.asJsonString(), Analysis.class));
      }
    } catch (Exception e) {
      System.out.println(
          String.format(
              "Exception occurred while fetching analysis by category for userId. %s",
              e.getStackTrace().toString()));
      throw new SipReadEntityException(
          "Exception occurred while fetching analysis by category for userId", e);
    }
    System.out.println("Document objDocs size {} " + objDocs.size());
    return objDocs;
  }

  /**
   * Update Analysis def with matching semantic id.
   *
   * @param analysis Analysis
   * @return Analysis obj
   * @throws SipUpdateEntityException SipUpdateEntityException
   */
  public boolean updateAnalysis(Analysis analysis) throws SipUpdateEntityException {

    analysis.setModifiedTime(Instant.now().toEpochMilli());
    try {
      JsonElement parsedAnalysis = toJsonElement(MAPPER.writeValueAsString(analysis));
      analysisMetadataStore = new AnalysisMetadata(tableName, basePath);
      analysisMetadataStore.update(analysis.getId(), parsedAnalysis);
    } catch (Exception e) {
      System.out.println(
          String.format(
              "Exception occurred while updating analysis. %s", e.getStackTrace().toString()));
      throw new SipUpdateEntityException("Exception occurred while updating analysis");
    }
    return true;
  }
}
