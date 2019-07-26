package com.synchronoss.saw.storage.proxy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mapr.db.MapRDB;
import com.mapr.db.Table;
import com.synchronoss.bda.sip.jwt.TokenParser;
import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.bda.sip.jwt.token.TicketDSKDetails;
import com.synchronoss.saw.model.Artifact;
import com.synchronoss.saw.model.DataSecurityKeyDef;
import com.synchronoss.saw.model.Field;
import com.synchronoss.saw.model.SipQuery;
import com.synchronoss.saw.storage.proxy.model.SemanticNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import sncr.bda.base.MaprConnection;

public class StorageProxyUtil {

  private static final Logger logger = LoggerFactory.getLogger(StorageProxyUtil.class);

  private static final String METASTORE = "services/metadata";
  private static List<String> dataLakeJunkIds;

  /**
   * This method to validate jwt token then return the validated ticket for further processing.
   *
   * @param request HttpServletRequest
   * @return Ticket
   */
  public static Ticket getTicket(HttpServletRequest request) {
    try {
      String token = getToken(request);
      return TokenParser.retrieveTicket(token);
    } catch (IllegalAccessException | IOException e) {
      logger.error("Error occurred while fetching the alert details", e);
    }
    return null;
  }

  /**
   * Get JWT token details.
   *
   * @param req http Request
   * @return String
   * @throws IllegalAccessException If Authorization not found
   */
  public static String getToken(final HttpServletRequest req) throws IllegalAccessException {
    if (!("OPTIONS".equals(req.getMethod()))) {
      final String authHeader = req.getHeader("Authorization");
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        throw new IllegalAccessException("Missing or invalid Authorization header.");
      }
      return authHeader.substring(7); // The part after "Bearer "
    }
    return null;
  }

  /**
   * Iterate the ticket DskList to return DataSecurity Object.
   *
   * @param {@link List} of {@link TicketDSKDetails}
   * @return {@link List} of {@link DataSecurityKeyDef}
   */
  public static List<DataSecurityKeyDef> getDsks(List<TicketDSKDetails> dskList) {
    DataSecurityKeyDef dataSecurityKeyDef;
    List<DataSecurityKeyDef> dskDefList = new ArrayList<>();
    if (dskList != null && !dskList.isEmpty()) {
      for (TicketDSKDetails dsk : dskList) {
        dataSecurityKeyDef = new DataSecurityKeyDef();
        dataSecurityKeyDef.setName(dsk.getName());
        dataSecurityKeyDef.setValues(dsk.getValues());
        dskDefList.add(dataSecurityKeyDef);
      }
    }
    return dskDefList;
  }

  /**
   * This will fetch the SIP query from metadata and provide.
   *
   * @param sipQuery
   * @return SipQuery
   */
  public static SipQuery getSipQuery(
      SipQuery sipQuery, String metaDataServiceExport, HttpServletRequest request) {
    String semanticId = sipQuery != null ? sipQuery.getSemanticId() : null;
    logger.info(
        "URI being prepared"
            + metaDataServiceExport
            + "/internal/semantic/workbench/"
            + semanticId);
    SipQuery semanticSipQuery = new SipQuery();
    if (semanticId != null) {
      try {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String url = metaDataServiceExport + "/internal/semantic/workbench/" + semanticId;
        logger.debug("SIP query url for analysis fetch : " + url);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set("Host", request.getHeader("Host"));
        requestHeaders.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        requestHeaders.set("Content-type", MediaType.APPLICATION_JSON_VALUE);
        requestHeaders.set("Authorization", request.getHeader("Authorization"));

        HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

        ResponseEntity<SemanticNode> analysisResponse =
            restTemplate.exchange(url, HttpMethod.GET, requestEntity, SemanticNode.class);

        List<Object> artifactList = analysisResponse.getBody().getArtifacts();

        List<Artifact> artifacts = new ArrayList<>();
        List<Field> fields = new ArrayList<>();

        for (Object artifact : artifactList) {
          Artifact dslArtifact = new Artifact();
          Gson gson = new Gson();
          logger.info("Gson String " + gson.toJson(artifact));
          JsonObject artifactObj = gson.toJsonTree(artifact).getAsJsonObject();
          dslArtifact.setArtifactsName(artifactObj.get("artifactName").getAsString());
          JsonArray columns = artifactObj.getAsJsonArray("columns");
          for (JsonElement columnElement : columns) {
            JsonObject column = columnElement.getAsJsonObject();
            Field field = new Field();
            // Only columnName will be sufficient for applying DSK. If you are using this method to
            // extract complete sipQuery Obj, you can set the other variables of sipQuery based on
            // the needs and this doesn't affect DSK functionality.
            field.setColumnName(column.get("columnName").getAsString());
            field.setDisplayName(column.get("displayName").getAsString());
            fields.add(field);
          }
          dslArtifact.setFields(fields);
          artifacts.add(dslArtifact);
        }
        semanticSipQuery.setArtifacts(artifacts);

        logger.debug("Fetched SIP query for analysis : " + semanticSipQuery.toString());
      } catch (Exception ex) {
        logger.error("Sip query not fetched from semantic");
      }
    }
    return semanticSipQuery;
  }

  /**
   * This method will clean up junk execution result table from maprdb and data lake.
   *
   * @param dslQueryId
   * @param configExecution
   * @return
   */
  public static void deleteJunkExecutionResult(
      String dslQueryId, Long configExecution, String basePath, String tableName) {
    try {
      // Create executionResult table if doesn't exists.
      MaprConnection maprConnection = new MaprConnection(basePath, tableName);
      ExecutionResultStore resultStore = new ExecutionResultStore(tableName, basePath);
      String fields[] = {"executionId", "data"};

      ObjectMapper objectMapper = new ObjectMapper();
      ObjectNode node = objectMapper.createObjectNode();
      ObjectNode objectNode = node.putObject("$eq");
      objectNode.put("dslQueryId", dslQueryId);

      // Mapr DB has limitation of limit clause which is 5k, so added default limit to fetch sorted
      // record based upon given field
      List<JsonNode> elements =
          maprConnection.runMaprDBQuery(fields, node.toString(), "finishedTime", 5000);

      configExecution = configExecution < elements.size() ? configExecution : elements.size();
      if (elements != null && elements.size() > 0) {
        List<String> junkList =
            elements.stream()
                .skip(configExecution)
                .map(jsonNode -> jsonNode.get("executionId").asText())
                .collect(Collectors.toList());

        logger.debug("List of execution ids needs to be cleaned ...!" + junkList);
        dataLakeJunkIds =
            elements.stream()
                .skip(configExecution.longValue())
                .filter(jsonNode -> jsonNode.get("data") == null)
                .map(jsonNode -> jsonNode.get("executionId").asText())
                .collect(Collectors.toList());

        String tablePath = basePath + File.separator + METASTORE + File.separator + tableName;
        Table table = MapRDB.getTable(tablePath);
        // method call to be asynch.
        CompletableFuture.runAsync(() -> resultStore.bulkDelete(table, junkList));
      }
    } catch (Exception e) {
      logger.error("Error occurred while purging execution result the execution Ids : {}", e);
    }
  }

  public static List<String> getDataLakeJunkIds() {
    return dataLakeJunkIds;
  }
}
