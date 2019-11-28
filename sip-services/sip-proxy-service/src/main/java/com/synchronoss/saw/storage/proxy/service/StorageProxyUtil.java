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
import com.synchronoss.bda.sip.jwt.token.DataSecurityKeys;
import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.bda.sip.jwt.token.TicketDSKDetails;
import com.synchronoss.saw.es.GlobalFilterResultParser;
import com.synchronoss.saw.model.Artifact;
import com.synchronoss.saw.model.DataSecurityKeyDef;
import com.synchronoss.saw.model.Field;
import com.synchronoss.saw.model.SipQuery;
import com.synchronoss.saw.model.globalfilter.GlobalFilter;
import com.synchronoss.saw.storage.proxy.model.SemanticNode;
import com.synchronoss.sip.utils.RestUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import sncr.bda.base.MaprConnection;
import sncr.bda.core.file.HFileOperations;

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
   * @param jsonNode
   * @param globalFilter
   * @return
   */
  public static JsonNode buildGlobalFilterData(JsonNode jsonNode, GlobalFilter globalFilter) {
    GlobalFilterResultParser globalFilterResultParser = new GlobalFilterResultParser(globalFilter);
    JsonNode jsonNode1 = jsonNode.get("global_filter_values");
    Map<String, Object> result = globalFilterResultParser.jsonNodeParser(jsonNode1);
    result.put("esRepository", globalFilter.getEsRepository());
    ObjectMapper mapper = new ObjectMapper();
    return mapper.valueToTree(result);
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
    List<DataSecurityKeyDef> dskDefList = new ArrayList<>();
    if (dskList != null && !dskList.isEmpty()) {
      dskList.forEach(dsk -> {
        DataSecurityKeyDef dskDef = new DataSecurityKeyDef();
        dskDef.setName(dsk.getName());
        dskDef.setValues(dsk.getValues());
        dskDefList.add(dskDef);
      });
    }
    return dskDefList;
  }

  /**
   * Fetch the DSK details by master login Id
   *
   * @param securityServiceUrl
   * @param masterLoginId
   * @param restUtil
   * @return list of dsk details
   */
  public static DataSecurityKeys getDSKDetailsByUser(String securityServiceUrl, String masterLoginId, RestUtil restUtil) {
		DataSecurityKeys dataSecurityKeys = null;
  	try {
      RestTemplate restTemplate = restUtil.restTemplate();
      String url = securityServiceUrl.concat("/dsk?userId=").concat(masterLoginId);
      logger.trace("SIP security url to fetch DSK details :", url);

			dataSecurityKeys = restTemplate.getForObject(url, DataSecurityKeys.class);
    } catch (Exception ex) {
      logger.error("Error while fetching DSK details by user", ex.getMessage());
    }
    return dataSecurityKeys;
  }

  /**
   * This will fetch the SIP query from metadata and provide.
   *
   * @param semanticId
   * @return SipQuery
   */
  public static SipQuery getSipQuery(
      String semanticId,
      String metaDataServiceExport,
      HttpServletRequest request,
      RestUtil restUtil) {
    logger.info(
        "URI being prepared"
            + metaDataServiceExport
            + "/internal/semantic/workbench/"
            + semanticId);
    SipQuery semanticSipQuery = new SipQuery();
    if (semanticId != null) {
      try {
        SemanticNode semanticNode = fetchSemantic(semanticId, metaDataServiceExport, restUtil);
        List<Object> artifactList = semanticNode.getArtifacts();
        logger.info("artifact List: " + artifactList);

        List<Artifact> artifacts = new ArrayList<>();

        for (Object artifact : artifactList) {
          List<Field> fields = new ArrayList<>();
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
        logger.error("Sip query not fetched from semantic" + ex.getMessage());
      }
    }
    return semanticSipQuery;
  }

    /**
     *  Return the semantic Node based on semantic ID .
     * @param semanticId
     * @param metaDataServiceUrl
     * @param restUtil
     * @return
     */
  public static SemanticNode fetchSemantic(String semanticId, String  metaDataServiceUrl , RestUtil restUtil)
  {
      RestTemplate restTemplate = restUtil.restTemplate();

      String url = metaDataServiceUrl + "/internal/semantic/workbench/" + semanticId;
      logger.debug("SIP query url for analysis fetch : " + url);

      return restTemplate.getForObject(url, SemanticNode.class);
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
      String fields[] = {"executionId", "analysis.type"};

      ObjectMapper objectMapper = new ObjectMapper();
      ObjectNode node = objectMapper.createObjectNode();
      ObjectNode objectNode = node.putObject("$eq");
      objectNode.put("dslQueryId", dslQueryId);

      // Mapr DB has limitation of limit clause which is 5k, so added default limit to fetch sorted
      // record based upon given field
      List<JsonNode> elements =
          maprConnection.runMaprDBQuery(fields, node.toString(), "finishedTime", 5000);

      logger.debug("List of execution ids needs to be cleaned ...!" + elements);

      configExecution = configExecution < elements.size() ? configExecution : elements.size();
      if (elements != null && elements.size() > 0) {
        List<String> junkList =
            elements.stream()
                .skip(configExecution)
                .map(jsonNode -> jsonNode.get("executionId").asText())
                .collect(Collectors.toList());

        logger.debug("List of execution ids needs to be cleaned from es ...!" + junkList);
        dataLakeJunkIds =
            elements.stream()
                .skip(configExecution)
                .filter(jsonNode -> jsonNode.get("analysis").get("type").equals("report"))
                .map(jsonNode -> jsonNode.get("executionId").asText())
                .collect(Collectors.toList());

        logger.debug("List of execution ids needs to be cleaned from data lake...!" + dataLakeJunkIds);

        String tablePath = basePath + File.separator + METASTORE + File.separator + tableName;
        Table table = MapRDB.getTable(tablePath);
        resultStore.bulkDelete(table, junkList);
      }
    } catch (Exception e) {
      logger.error("Error occurred while purging execution result the execution Ids : {}", e);
    }
  }

  public static List<String> getDataLakeJunkIds() {
    return dataLakeJunkIds;
  }

  /**
   * @param mainNode
   * @param updateNode
   * @return
   */
  public static JsonNode merge(JsonNode mainNode, JsonNode updateNode) {
    Iterator<String> fieldNames = updateNode.fieldNames();
    while (fieldNames.hasNext()) {
      String fieldName = fieldNames.next();
      JsonNode jsonNode = mainNode.get(fieldName);
      if (jsonNode != null) {
        if (jsonNode.isObject()) {
          merge(jsonNode, updateNode.get(fieldName));
        } else if (jsonNode.isArray()) {
          for (int i = 0; i < jsonNode.size(); i++) {
            merge(jsonNode.get(i), updateNode.get(fieldName).get(i));
          }
        }
      } else {
        if (mainNode instanceof ObjectNode) {
          // Overwrite field
          JsonNode value = updateNode.get(fieldName);
          if (value.isNull()) {
            continue;
          }
          if (value.isIntegralNumber() && value.toString().equals("0")) {
            continue;
          }
          if (value.isFloatingPointNumber() && value.toString().equals("0.0")) {
            continue;
          }
          ((ObjectNode) mainNode).put(fieldName, value);
        }
      }
    }
    return mainNode;
  }

  /**
   * Create required path if they do not exist.
   *
   * @param retries number of retries.
   * @throws Exception when unable to create directory path.
   */
  public static void createDirIfNotExists(String path, int retries) throws Exception {
   try {
        if (!HFileOperations.exists(path))
      HFileOperations.createDir(path);
    } catch (Exception e) {
      if (retries == 0) {
        logger.error("unable to create path : " + path);
      }
      Thread.sleep(5 * 1000);
      createDirIfNotExists(path, retries - 1);
    }
  }

  public static List<String> getArtifactsNames(SipQuery sipQuery) {
    List<String> artifactNames = new ArrayList<>();
    List<Artifact> artifactList = sipQuery.getArtifacts();
    if (artifactList != null && !artifactList.isEmpty()) {
      artifactList.stream().forEach(artifact -> artifactNames.add(artifact.getArtifactsName().toUpperCase()));
    }
    return artifactNames;
  }
}
