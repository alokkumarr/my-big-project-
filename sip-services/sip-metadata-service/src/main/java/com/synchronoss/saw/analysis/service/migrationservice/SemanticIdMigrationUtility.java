package com.synchronoss.saw.analysis.service.migrationservice;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.synchronoss.saw.analysis.metadata.AnalysisMetadata;
import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.analysis.service.AnalysisServiceImpl;
import com.synchronoss.saw.model.Artifact;
import com.synchronoss.saw.model.SipQuery;
import com.synchronoss.saw.util.FieldNames;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ojai.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SemanticIdMigrationUtility {

  private static final Logger LOGGER = LoggerFactory.getLogger(SemanticIdMigrationUtility.class);
  private static ObjectMapper MAPPER = new ObjectMapper();

  private AnalysisMetadata semanticMedatadataStore = null;

  private static String metadataTable;

  private static String basePath;

  private AnalysisServiceImpl analysisService;

  /**
   * Main method to be called from a script.
   *
   * @param args Require no args for this
   */
  public static void main(String[] args) {
    basePath = args[0];
    metadataTable = args[1];
    SemanticIdMigrationUtility uos = new SemanticIdMigrationUtility();
    uos.updateAnalysisWithSemanticInfo();
  }

  /**
   * Update the existing Analysis in maprDB to match semantic.
   *
   * @return boolean value
   */
  public boolean updateAnalysisWithSemanticInfo() {
    analysisService  = new AnalysisServiceImpl();
    List<Analysis> analyses = analysisService.getAllAnalyses();
    Map<String, String> semanticMap = getMetaData();
    if (analyses != null && analyses.size() > 0 && semanticMap != null && !semanticMap.isEmpty()) {
      for (Analysis analysis : analyses) {
        updatedSemanticId(analysis, semanticMap);
        Analysis returnedAnalysis = analysisService.updateAnalysis(analysis, null);
        return returnedAnalysis != null ? true : false;
      }
    }
    return false;
  }

  /**
   * Return the semantic id for the migration.
   *
   * @param analysis Analysis
   * @param semanticMap Map of semantic id and artifact name
   */
  private void updatedSemanticId(Analysis analysis, Map<String, String> semanticMap) {
    LOGGER.debug("Semantic Map = {}", semanticMap);
    LOGGER.debug("SipQuery definition = {}", analysis);
    String analysisSemanticId =
        analysis != null && analysis.getSemanticId() != null ? analysis.getSemanticId() : null;
    LOGGER.debug("Analysis semantic id = {}", analysisSemanticId);
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
              LOGGER.debug("Artifact name = {}", artifactName);
              if (semanticArtifactName.equalsIgnoreCase(artifactName)) {
                LOGGER.info(
                    "Semantic id updated from {} to {}",
                    analysis.getSemanticId(),
                    entry.getValue());
                analysis.setSemanticId(entry.getKey());
                if (updateAnalysisWithSemanticInfo()) {
                  LOGGER.debug(
                      "Successfully Updated analysis id {} with semantic id - {} ",
                      analysis.getId(),
                      entry.getValue());
                } else {
                  LOGGER.error("Failed writing to maprDB!! Analysis id : {} ", analysis.getId());
                }
                break;
              }
            } else {
              LOGGER.warn("Artifacts is not present");
            }
          } else {
            LOGGER.warn("sipQuery not present");
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
      semanticMedatadataStore = new AnalysisMetadata(metadataTable, basePath);
      List<Document> docs = semanticMedatadataStore.searchAll();
      if (docs != null && !docs.isEmpty() && docs.size() > 0) {
        for (Document d : docs) {
          MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
          objDocs.add(toJsonElement(d).getAsJsonObject());
        }
      }
    } catch (Exception e) {
      LOGGER.error("Exception occurred while fetching Semantic definition", e);
    }

    for (JsonObject semanticData : objDocs) {
      LOGGER.debug("Semantic Metadata = " + semanticData);
      String id = semanticData.get("_id").getAsString();
      LOGGER.debug("Semantic ID = " + id);
      if (id != null) {
        JsonArray artifacts = semanticData.getAsJsonArray(FieldNames.ARTIFACTS);
        LOGGER.debug("Artifacts = " + artifacts);
        if (artifacts != null) {
          String artifactName =
              artifacts.get(0).getAsJsonObject().get(FieldNames.ARTIFACT_NAME).getAsString();
          if (artifactName != null) {
            semanticMap.put(artifactName, id);
          }
        }
      }
    }
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
}
