package com.synchronoss.saw.storage.proxy.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.synchronoss.bda.sip.jwt.TokenParser;
import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.bda.sip.jwt.token.TicketDSKDetails;
import com.synchronoss.saw.model.Artifact;
import com.synchronoss.saw.model.DataSecurityKeyDef;
import com.synchronoss.saw.model.Field;
import com.synchronoss.saw.model.SipQuery;
import com.synchronoss.saw.storage.proxy.model.SemanticNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class StorageProxyUtil {

  private static final Logger logger = LoggerFactory.getLogger(StorageProxyUtil.class);
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
}
