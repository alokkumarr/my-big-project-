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
import javax.validation.constraints.NotNull;
import org.ojai.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UpdateOrphanSemantic {
  private static final Logger logger = LoggerFactory.getLogger(UpdateOrphanSemantic.class);
  private AnalysisMetadata semanticMedatadataStore = null;
  private static ObjectMapper objectMapper = new ObjectMapper();

  @Value("${metastore.metadataTable}")
  private String metadataTable;

  @Value("${metastore.base}")
  @NotNull
  private String basePath;

  private AnalysisServiceImpl analysisService;

  /**
   * Main method to be called from a script.
   *
   * @param args Require no args for this
   */
  public static void main(String[] args) {
    AnalysisServiceImpl analysisService = new AnalysisServiceImpl();
    UpdateOrphanSemantic uos = new UpdateOrphanSemantic();
    List<Analysis> analyses = analysisService.getAllAnalyses();
    Map<String, String> semanticMap = uos.getMetaData();
    for (Analysis analysis : analyses) {
      uos.updatedSemanticId(analysis, semanticMap);
    }
  }

  /**
   * Update the existing Analysis in maprDB to match semantic.
   *
   * @param analysis Analysis
   * @return boolean value
   */
  public boolean updateAnalysisWithSemanticInfo(Analysis analysis) {
    analysisService = new AnalysisServiceImpl();
    // Calling internally, and Authentication is also not used in AnalysisServiceImpl class
    Analysis returnedAnalysis = analysisService.updateAnalysis(analysis, null);
    return returnedAnalysis != null ? true : false;
  }

  /**
   * Return the semantic id for the migration.
   *
   * @param analysis Analysis
   * @param semanticMap Map of semantic id and artifact name
   */
  private void updatedSemanticId(Analysis analysis, Map<String, String> semanticMap) {
    logger.debug("Semantic Map = {}", semanticMap);
    logger.debug("SipQuery definition = {}", analysis);
    String analysisSemanticId =
        analysis != null && analysis.getSemanticId() != null ? analysis.getSemanticId() : null;
    logger.debug("Analysis semantic id = {}", analysisSemanticId);
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
              logger.debug("Artifact name = {}", artifactName);
              if (semanticArtifactName.equalsIgnoreCase(artifactName)) {
                logger.info(
                    "Semantic id updated from {} to {}",
                    analysis.getSemanticId(),
                    entry.getValue());
                analysis.setSemanticId(entry.getKey());
                if (updateAnalysisWithSemanticInfo(analysis)) {
                  logger.debug(
                      "Successfully Updated analysis id {} with semantic id - {} ",
                      analysis.getId(),
                      entry.getValue());
                } else {
                  logger.error("Failed writing to maprDB!! Analysis id : {} ", analysis.getId());
                }
                break;
              }
            } else {
              logger.warn("Artifacts is not present");
            }
          } else {
            logger.warn("sipQuery not present");
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

    List<Document> docs = null;

    List<JsonObject> objDocs = new ArrayList<>();

    try {

      semanticMedatadataStore = new AnalysisMetadata(metadataTable, basePath);

      docs = semanticMedatadataStore.searchAll();

      if (docs == null) {

        return null;
      }

      for (Document d : docs) {

        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        objDocs.add(toJsonElement(d).getAsJsonObject());
      }

    } catch (Exception e) {

      logger.error("Exception occurred while fetching Semantic definition", e);
    }
    for (JsonObject semanticData : objDocs) {

      logger.debug("Semantic Metadata = " + semanticData);

      String id = semanticData.get("_id").getAsString();
      logger.debug("Semantic ID = " + id);

      if (id != null) {
        JsonArray artifacts = semanticData.getAsJsonArray(FieldNames.ARTIFACTS);
        logger.debug("Artifacts = " + artifacts);

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
