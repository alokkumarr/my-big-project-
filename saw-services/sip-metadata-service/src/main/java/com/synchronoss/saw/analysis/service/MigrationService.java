package com.synchronoss.saw.analysis.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.model.*;
import com.synchronoss.saw.semantic.model.MetaDataObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;

public class MigrationService {
  private static final Logger logger = LoggerFactory.getLogger(MigrationService.class);
  private String existingBinaryAnalysisPath = "/services/metadata/analysis_metadata";


  public void convertBinaryToJson(String analysisStoreBasePath,
                                  String listAnalysisUri, String migrationMetadataHome) {
    logger.trace("migration process will begin here");
    HttpHeaders requestHeaders = new HttpHeaders();

    requestHeaders.set("Content-type", MediaType.APPLICATION_JSON_UTF8_VALUE);
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    HttpEntity<?> requestEntity =
        new HttpEntity<Object>(semanticNodeQuery(), requestHeaders);
    logger.debug("Analysis server URL {}", listAnalysisUri + "/analysis");


      String url = listAnalysisUri + "/analysis";
      RestTemplate restTemplate = new RestTemplate();
      ResponseEntity analysisBinaryData =
          restTemplate.exchange(url, HttpMethod.GET, requestEntity, Analysis.class);

      if (analysisBinaryData.getBody() != null) {
        Gson gson = new GsonBuilder().create();
        logger.debug("Analysis data = " + analysisBinaryData.getBody());

        //TODO: Check if this works
        JsonObject analysisBinaryObject = gson.toJsonTree(analysisBinaryData.getBody())
                                            .getAsJsonObject();

        JsonArray analysisList = analysisBinaryObject.get("contents")
                                .getAsJsonObject().getAsJsonArray("analyze");


        for(JsonElement analysisElement: analysisList) {
            JsonObject analysisObject = analysisElement.getAsJsonObject();
            Analysis analysis = createDSLAnalysis(analysisObject);

            // TODO: Call add analysis API
        }
      }
  }

  private Analysis createDSLAnalysis(JsonObject analysisObject) {
      Analysis analysis = new Analysis();

      analysis.setId(analysisObject.get("id").getAsString());
      analysis.setSemanticId(analysisObject.get("sementicId").getAsString());
      analysis.setName(analysisObject.get("name").getAsString());

      analysis.setType(analysisObject.get("type").getAsString());
      analysis.setChartType(analysisObject.get("chartType").getAsString());

      //TODO: Should be category id or category name?
      analysis.setCategory();

      analysis.setCustomerCode(analysisObject.get("customerCode").getAsString());
      analysis.setProjectCode(analysisObject.get("projectCode").getAsString());
      analysis.setModule(analysisObject.get("module").getAsString());

      analysis.setCreatedTime(analysisObject.get("createdTimestamp").getAsLong());
      analysis.setCreatedBy(analysisObject.get("username").getAsString());

      analysis.setModifiedTime(analysisObject.get("updatedTimestamp").getAsLong());
      analysis.setModifiedBy(analysisObject.get("updatedUserName").getAsString());


      // Extract artifact name from "artifacts"
      // NOTE: For charts and pivots, there will be only one object in artifacts. For reports,
      // there will be 2 objects
      String artifactName = null;

      JsonArray artifacts = analysisObject.getAsJsonArray("artifacts");

      // Handling artifact name for charts and pivots
      JsonObject artifact = artifacts.get(0).getAsJsonObject();
      artifactName = artifact.get("artifactName").getAsString();



      JsonElement sqlQueryBuilderElement = analysisObject.get("sqlBuilder");
      if (sqlQueryBuilderElement != null) {
          JsonObject sqlQueryBuilderObject = sqlQueryBuilderElement.getAsJsonObject();
          analysis.setSipQuery(generateSipQuery(artifactName, sqlQueryBuilderObject));
      }

      //TODO: Understand the dynamic parameters

      //TODO: Any additional parameters required???

      return analysis;
  }

  private SipQuery generateSipQuery(String artifactName, JsonObject sqlQueryBuilder,
                                    Store store) {
      SipQuery sipQuery = new SipQuery();

      sipQuery.setArtifacts(generateArtifactsList());

      String booleanCriteriaValue = sqlQueryBuilder.get("booleanCriteria").getAsString();
      SipQuery.BooleanCriteria booleanCriteria
          = SipQuery.BooleanCriteria.fromValue(booleanCriteriaValue);
      sipQuery.setBooleanCriteria(booleanCriteria);

      sipQuery.setFilters(generateFilters());
      sipQuery.setSorts(generateSorts());
      sipQuery.setStore(store);

      return sipQuery;
  }

  private List<Artifact> generateArtifactsList(JsonObject sqlBuilder) {
    List<Artifact> artifacts = new LinkedList<>();



    return artifacts;
  }

  private List<Filter> generateFilters() {
    List<Filter> filters = new LinkedList<>();

    return filters;
  }

  private List<Sort> generateSorts() {
    List<Sort> sorts = new LinkedList<>();

    return sorts;
  }

  // For charts, there will be dataFields and nodefields
  // For pivots, there will be rowfields, columnFIelds and dataFields
  private Artifact generateArtifact(String artifactName, JsonObject sqlBuilder) {
    Artifact artifact = new Artifact();

    artifact.setArtifactsName(artifactName);
    artifact.setFields(generateArtifactFields(sqlBuilder));

    return artifact;
  }

  private List<Field> generateArtifactFields (JsonObject sqlBuilder) {
    List<Field> fields = new LinkedList<>();

    return fields;
  }

  private Field generateArtifactField(JsonObject fieldObject) {
      Field field = new Field();

      if(fieldObject.has("columnName")) {
          field.setColumnName(fieldObject.get("columnName").getAsString());

          // For analysis migration, we will use column name as dataField
          field.setDataField(field.getColumnName());
      }
      if (fieldObject.has("displayName")) {
          field.setDisplayName(fieldObject.get("displayName").getAsString());
      }
      if (fieldObject.has("aliasName")) {
          field.setAlias(fieldObject.get("aliasName").getAsString());
      }


      //TODO: check regarding "checked" fields - Maps to area


      return field;
  }


  private String semanticNodeQuery() {
    return "{\n" +
      "   \"contents\":{\n" +
      "      \"keys\":[\n" +
      "         {\n" +
      "            \"module\":\"ANALYZE\"\n" +
      "         }\n" +
      "      ],\n" +
      "      \"action\":\"export\"\n" +
      "   }\n" +
      "}";
    }
}
