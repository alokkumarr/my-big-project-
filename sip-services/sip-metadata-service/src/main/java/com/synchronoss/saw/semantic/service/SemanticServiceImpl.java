package com.synchronoss.saw.semantic.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.json.JsonSanitizer;
import com.synchronoss.saw.exceptions.SipCreateEntityException;
import com.synchronoss.saw.exceptions.SipDeleteEntityException;
import com.synchronoss.saw.exceptions.SipJsonValidationException;
import com.synchronoss.saw.exceptions.SipReadEntityException;
import com.synchronoss.saw.exceptions.SipUpdateEntityException;
import com.synchronoss.saw.semantic.model.DataSet;
import com.synchronoss.saw.semantic.model.request.BackCompatibleStructure;
import com.synchronoss.saw.semantic.model.request.Content;
import com.synchronoss.saw.semantic.model.request.SemanticNode;
import com.synchronoss.saw.semantic.model.request.SemanticNodes;
import com.synchronoss.saw.util.SipMetadataUtils;
import com.synchronoss.sip.utils.RestUtil;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sncr.bda.cli.MetaDataStoreRequestAPI;
import sncr.bda.datasets.conf.DataSetProperties;
import sncr.bda.store.generic.schema.Action;
import sncr.bda.store.generic.schema.Category;
import sncr.bda.store.generic.schema.Filter;
import sncr.bda.store.generic.schema.Filter.Condition;
import sncr.bda.store.generic.schema.MetaDataStoreStructure;
import sncr.bda.store.generic.schema.Query;
import sncr.bda.store.generic.schema.Query.Conjunction;

@Service
public class SemanticServiceImpl implements SemanticService {

  private static final Logger logger = LoggerFactory.getLogger(SemanticServiceImpl.class);

  @Value("${metastore.base}")
  @NotNull
  private String basePath;

  @Value("${semantic.workbench-url}")
  @NotNull
  private String workbenchURl;

  @Value("${semantic.transport-metadata-url}")
  @NotNull
  private String transportUri;

  @Value("${semantic.binary-migration-requires}")
  @NotNull
  private boolean migrationRequires;

  @Value("${semantic.migration-metadata-home}")
  @NotNull
  private String migrationMetadataHome;

  @Value("${sip-security.dsk-url}")
  @NotNull
  private String securityDskUrl;

  @Autowired  private RestUtil restUtil;

  private RestTemplate restTemplate = null;

  private static final String COLUMN_NAME = "columnName";
  private static final String DISPLAY_NAME = "displayName";

  /**
   * This method provides an entry point to migration service.
   *
   * @throws Exception exception
   */
  /*@PostConstruct
  public void init() throws Exception {
    restTemplate = restUtil.restTemplate();
    if (migrationRequires) {
      logger.trace("Migration initiated.. " + migrationRequires);
      migrationService.convertHBaseBinaryToMaprdbStore(transportUri, basePath,
          migrationMetadataHome);
    }
    logger.trace("Migration ended..");
  }*/

  @Override
  public SemanticNode addSemantic(SemanticNode node)
      throws SipJsonValidationException, SipCreateEntityException {
    logger.trace("Adding semantic with an Id : {}", node.get_id());
    SemanticNode responseNode = new SemanticNode();
    SemanticNode newSemanticNode = null;
    node.setCreatedBy(node.getUsername());
    ObjectMapper mapper = new ObjectMapper();
    try {
      List<MetaDataStoreStructure> structure =
          SipMetadataUtils.node2JsonObject(
              node, basePath, node.get_id(), Action.create, Category.Semantic);
      logger.trace(
          "Before invoking request to MaprDB JSON store :{}", mapper.writeValueAsString(structure));
      if (node.getRepository() == null) {
        node = setRepository(node);
      }
      MetaDataStoreRequestAPI requestMetaDataStore = new MetaDataStoreRequestAPI(structure);
      requestMetaDataStore.process();
      responseNode.setId(node.get_id());
      responseNode.setCreatedBy(node.getCreatedBy());
      responseNode.setSaved(true);
      responseNode.setStatusMessage("Entity is created successfully");
      newSemanticNode = new SemanticNode();
      org.springframework.beans.BeanUtils.copyProperties(responseNode, newSemanticNode, "_id");
    } catch (Exception ex) {
      logger.error("Problem on the storage while creating an entity", ex);
      throw new SipCreateEntityException("Problem on the storage while creating an entity.", ex);
    }
    logger.trace("Response : " + node.toString());
    return newSemanticNode;
  }

  @Override
  public Boolean addDskToSipSecurity(SemanticNode node, HttpServletRequest request) {
    try {
      ObjectMapper mapper = new ObjectMapper();

      // Call the create DSK sip security API
      String semanticId = node.get_id();

      List<Object> artifacts = node.getArtifacts();

      ArrayNode dskFields = prepareDskFields(mapper, artifacts);
      logger.info("DSK Fields: " + dskFields);

      if (dskFields.size() != 0) {

        String url = securityDskUrl + "/auth/admin/dsk/fields?" + "semanticId=" + semanticId;

        String authToken = request.getHeader("Authorization");

        logger.info("Save DSK eligible keys in sip security :" + url);

        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", authToken);
        HttpEntity<ArrayNode> requestEntity = new HttpEntity<>(dskFields, header);

        restUtil.restTemplate().exchange(url, HttpMethod.POST, requestEntity, String.class);

        return true;
      } else {
        logger.info("No fields are DSK eligible");

        return true;
      }
    } catch (Exception ex) {
      logger.error("Error occurred while adding DSK fields: " + ex.getMessage(), ex);
      return false;
    }
  }

  @Override
  public Boolean updateDskInSipSecurity(SemanticNode node, HttpServletRequest request) {
    try {
      ObjectMapper mapper = new ObjectMapper();

      // Call the update DSK sip security API
      String semanticId = node.get_id();

      List<Object> artifacts = node.getArtifacts();

      ArrayNode dskFields = prepareDskFields(mapper, artifacts);

      logger.info("DSK Fields: " + dskFields);

      // Note: In case of update, the API needs to be called even if the list of DSK
      // eligible fields are empty, becasue if the list of fields is empty, the fields
      // for that particular semantic id, all the existing fields have to be removed.

      String url = securityDskUrl + "/auth/admin/dsk/fields?" + "semanticId=" + semanticId;

      String authToken = request.getHeader("Authorization");

      logger.info("Update DSK eligible keys in sip security :" + url);

      HttpHeaders header = new HttpHeaders();
      header.set("Authorization", authToken);
      HttpEntity<ArrayNode> requestEntity = new HttpEntity<>(dskFields, header);
      restUtil.restTemplate().exchange(url, HttpMethod.PUT, requestEntity, String.class);

      return true;
    } catch (Exception ex) {
      logger.error("Error occurred while updating DSK fields: " + ex.getMessage(), ex);
      return false;
    }
  }

  @Override
  public Boolean deleteDskInSipSecurity(String semanticId, HttpServletRequest request) {
    // Call sip-security delete DSK api here
    try {
      String url = securityDskUrl + "/auth/admin/dsk/fields?" + "semanticId=" + semanticId;

      String authToken = request.getHeader("Authorization");

      logger.info("Update DSK eligible keys in sip security :" + url);

      HttpHeaders header = new HttpHeaders();
      header.set("Authorization", authToken);
      HttpEntity<?> requestEntity = new HttpEntity<>(header);

      Boolean status = true;
      restUtil.restTemplate().exchange(url, HttpMethod.DELETE, requestEntity, String.class);

      return status;
    } catch (Exception ex) {
      logger.error("Error occurred while deleting DSK fields: " + ex.getMessage(), ex);
      return false;
    }
  }

  /**
   * This method to set the physicalLocation, format & name under repository section. when it is
   * from DataLake.
   *
   * @throws Exception exception
   */
  private SemanticNode setRepository(SemanticNode semanticNode) throws Exception {
    logger.trace("Setting repository starts here..");
    String requestUrl =
        workbenchURl
            + "/internal/workbench/projects/"
            + semanticNode.getProjectCode()
            + "/datasets/";
    List<Object> dataSetDetailsObject = new ArrayList<>();
    DataSet dataSet = null;
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    JsonNode node = null;
    ObjectNode rootNode = null;
    ObjectNode systemNode = null;
    String physicalLocation = null;
    String dataSetName = null;
    String dataSetFormat = null;
    ObjectNode repoNode = null;
    ArrayNode respository = objectMapper.createArrayNode();
    restTemplate = restUtil.restTemplate();
    for (String dataSetId : semanticNode.getParentDataSetIds()) {
      logger.trace("Request URL to pull DataSet Details : " + requestUrl + dataSetId);
      dataSet = restTemplate.getForObject(requestUrl + dataSetId, DataSet.class);
      String datasetStr = objectMapper.writeValueAsString(dataSet);
      String sanitizedDatasetStr = JsonSanitizer.sanitize(datasetStr);
      node = objectMapper.readTree(sanitizedDatasetStr);
      rootNode = (ObjectNode) node;
      systemNode = (ObjectNode) rootNode.get(DataSetProperties.System.toString());
      physicalLocation = systemNode.get(DataSetProperties.PhysicalLocation.toString()).asText();
      dataSetName = systemNode.get(DataSetProperties.Name.toString()).asText();
      dataSetFormat = systemNode.get(DataSetProperties.Format.toString()).asText();
      repoNode = objectMapper.createObjectNode();
      repoNode.put(DataSetProperties.Name.toString(), dataSetName);
      repoNode.put(DataSetProperties.Format.toString(), dataSetFormat);
      repoNode.put(
          DataSetProperties.PhysicalLocation.toString(), physicalLocation + Path.SEPARATOR);
      dataSetDetailsObject.add(repoNode);
      respository.add(repoNode);
    }
    semanticNode.setRepository(dataSetDetailsObject);
    logger.trace("Setting repository ends here.");
    logger.trace(
        "Semantic node after adding repository for DL type: "
            + objectMapper.writeValueAsString(semanticNode));
    return semanticNode;
  }

  @Override
  public SemanticNode readSemantic(SemanticNode node)
      throws SipJsonValidationException, SipReadEntityException {
    Preconditions.checkArgument(node.get_id() != null, "Id is mandatory attribute.");
    logger.trace("reading semantic from the store with an Id : {}", node.get_id());
    SemanticNode nodeRetrieved = null;
    SemanticNode newSemanticNode = null;
    try {
      List<MetaDataStoreStructure> structure =
          SipMetadataUtils.node2JsonObject(
              node, basePath, node.get_id(), Action.read, Category.Semantic);
      logger.trace("Before invoking request to MaprDB JSON store :{}", structure);
      MetaDataStoreRequestAPI requestMetaDataStore = new MetaDataStoreRequestAPI(structure);
      requestMetaDataStore.process();
      String jsonStringFromStore = requestMetaDataStore.getResult().toString();
      ObjectMapper mapper = new ObjectMapper();
      nodeRetrieved = mapper.readValue(jsonStringFromStore, SemanticNode.class);
      logger.trace("Id: {}", nodeRetrieved.get_id());
      nodeRetrieved.setId(nodeRetrieved.get_id());
      nodeRetrieved.setStatusMessage("Entity has retrieved successfully");
      newSemanticNode = new SemanticNode();
      org.springframework.beans.BeanUtils.copyProperties(nodeRetrieved, newSemanticNode, "_id");
    } catch (Exception ex) {
      throw new SipReadEntityException("Problem on the storage while reading an entity", ex);
    }
    return newSemanticNode;
  }

  @Override
  public SemanticNode updateSemantic(SemanticNode node, Map<String, String> headers)
      throws SipJsonValidationException, SipUpdateEntityException {
    Preconditions.checkArgument(node.get_id() != null, "Id is mandatory attribute.");
    logger.trace("updating semantic from the store with an Id : {}", node.get_id());
    SemanticNode responseNode = new SemanticNode();
    SemanticNode newSemanticNode = null;
    ObjectMapper objectMapper = new ObjectMapper();
    if (headers.get("x-userName") != null) {
      node.setUpdatedBy(headers.get("x-userName"));
      logger.trace(headers.get("x-userName"));
    }
    try {
      List<MetaDataStoreStructure> structure =
          SipMetadataUtils.node2JsonObject(
              node, basePath, node.get_id(), Action.update, Category.Semantic);
      logger.trace("Before invoking request to MaprDB JSON store :{}", structure);
      MetaDataStoreRequestAPI requestMetaDataStore = new MetaDataStoreRequestAPI(structure);
      requestMetaDataStore.process();
      responseNode.setId(node.get_id());
      responseNode.setUpdatedBy(node.getUpdatedBy());
      responseNode.setSaved(true);
      responseNode.setStatusMessage("Entity has been updated successfully");
      newSemanticNode = new SemanticNode();
      org.springframework.beans.BeanUtils.copyProperties(responseNode, newSemanticNode, "_id");
    } catch (Exception ex) {
      throw new SipUpdateEntityException("Problem on the storage while updating an entity", ex);
    }
    return newSemanticNode;
  }

  @Override
  public SemanticNode deleteSemantic(SemanticNode node)
      throws SipJsonValidationException, SipDeleteEntityException {
    Preconditions.checkArgument(node.get_id() != null, "Id is mandatory attribute.");
    logger.trace("Deleting semantic from the store with an Id : {}", node.get_id());
    SemanticNode responseObject = new SemanticNode();
    SemanticNode newSemanticNode = new SemanticNode();
    try {
      List<MetaDataStoreStructure> structure =
          SipMetadataUtils.node2JsonObject(
              node, basePath, node.get_id(), Action.delete, Category.Semantic);
      logger.trace("Before invoking request to MaprDB JSON store :{}", structure);
      MetaDataStoreRequestAPI requestMetaDataStore = new MetaDataStoreRequestAPI(structure);
      requestMetaDataStore.process();
      responseObject.setId(node.get_id());
    } catch (Exception ex) {
      throw new SipUpdateEntityException("Problem on the storage while updating an entity", ex);
    }

    String semanticId = node.get_id();

    // Call delete DSK sip-security API

    return newSemanticNode;
  }

  @Override
  public SemanticNodes search(SemanticNode node, Map<String, String> headers)
      throws SipJsonValidationException, SipReadEntityException {
    logger.trace("search criteria :{}", node);
    SemanticNodes responseNode = new SemanticNodes();
    if (headers.get("x-customercode") != null) {
      node.setCustomerCode(headers.get("x-customercode"));
      logger.trace(headers.get("x-customercode"));
    }
    try {
      Query query = new Query();
      query.setConjunction(Conjunction.AND);
      List<Filter> filters = new ArrayList<>();
      Filter filterCreated = new Filter();
      if (node.getCreatedBy() != null || node.getUsername() != null) {
        filterCreated.setFieldPath("username");
        filterCreated.setCondition(Condition.EQ);
        filterCreated.setValue(
            node.getCreatedBy() != null ? node.getCreatedBy() : node.getUsername());
        filters.add(filterCreated);
      }
      Filter filterCustomerCode = new Filter();
      if (node.getCustomerCode() != null) {
        filterCustomerCode.setFieldPath("customerCode");
        filterCustomerCode.setCondition(Condition.EQ);
        filterCustomerCode.setValue(node.getCustomerCode());
        filters.add(filterCreated);
      }
      Filter filterModule = new Filter();
      if (node.getModule() != null) {
        filterModule.setFieldPath("module");
        filterModule.setCondition(Condition.EQ);
        filterModule.setValue(node.getModule().value());
        filters.add(filterModule);
      }
      Filter filterDataSecurity = new Filter();
      if (node.getDataSecurityKey() != null) {
        filterDataSecurity.setFieldPath("dataSecurityKey");
        filterDataSecurity.setCondition(Condition.EQ);
        filterDataSecurity.setValue(node.getDataSecurityKey());
        filters.add(filterDataSecurity);
      }
      Filter filterMetricName = new Filter();
      if (node.getMetricName() != null) {
        filterMetricName.setFieldPath("metricName");
        filterMetricName.setCondition(Condition.EQ);
        filterMetricName.setValue(node.getMetricName());
        filters.add(filterMetricName);
      }
      query.setFilter(filters);
      String searchQuery =
          SipMetadataUtils.node2JsonString(
              node, basePath, node.get_id(), Action.search, Category.Semantic, query);
      logger.debug("Search Query to get the semantic :" + searchQuery);
      MetaDataStoreRequestAPI requestMetaDataStore = new MetaDataStoreRequestAPI(searchQuery);
      requestMetaDataStore.process();
      List<SemanticNode> semanticNodes = new ArrayList<SemanticNode>();

      ObjectMapper mapper = new ObjectMapper();
      if (requestMetaDataStore.getSearchResultJsonArray() != null
          && requestMetaDataStore.getSearchResultJsonArray().size() > 0) {
        JsonElement resultArray = requestMetaDataStore.getSearchResultJsonArray();
        logger.debug("Entity has been retrieved successfully :" + resultArray.toString());
        if (resultArray.isJsonArray()) {
          for (int i = 0, j = 1; i < resultArray.getAsJsonArray().size(); i++, j++) {
            logger.debug("Inside resultArray.isJsonArray() ");
            logger.debug(
                " element.isJsonArray() :" + resultArray.getAsJsonArray().get(i).isJsonArray());
            logger.debug(
                " element.isJsonObject() :"
                    + resultArray
                        .getAsJsonArray()
                        .get(i)
                        .getAsJsonObject()
                        .getAsJsonObject(String.valueOf(j)));
            String jsonString =
                resultArray
                    .getAsJsonArray()
                    .get(i)
                    .getAsJsonObject()
                    .getAsJsonObject(String.valueOf(j))
                    .toString();
            SemanticNode semanticNode = mapper.readValue(jsonString, SemanticNode.class);
            logger.trace("Id: {}", semanticNode.get_id());
            // This is extra field copy of _id field to support both backend & frontend
            semanticNode.setId(semanticNode.get_id());
            semanticNode.setStatusMessage("Entity has retrieved successfully");
            SemanticNode newSemanticNode = new SemanticNode();
            org.springframework.beans.BeanUtils.copyProperties(
                semanticNode, newSemanticNode, "_id");
            semanticNodes.add(newSemanticNode);
          }
        }
        responseNode.setSemanticNodes(semanticNodes);
      } else {
        responseNode.setSemanticNodes(semanticNodes);
      }
    } catch (Exception ex) {
      logger.error("While retrieving it has been found that Entity does not exist.", ex);
      throw new SipReadEntityException(
          "While retrieving it has been found that Entity does not exist.");
    }
    return responseNode;
  }

  @Override
  public BackCompatibleStructure list(SemanticNode node, Map<String, String> headers)
      throws SipJsonValidationException, SipReadEntityException {
    logger.trace("search criteria :{}", node);
    BackCompatibleStructure structure = new BackCompatibleStructure();
    Content content = new Content();
    List<Content> contents = new ArrayList<>();
    if (headers.get("x-customercode") != null) {
      node.setCustomerCode(headers.get("x-customercode"));
      logger.trace("x-customercode:" + headers.get("x-customercode"));
    }
    try {
      Query query = new Query();
      query.setConjunction(Conjunction.AND);
      List<Filter> filters = new ArrayList<>();
      Filter filterCreated = new Filter();
      if (node.getCreatedBy() != null || node.getUsername() != null) {
        filterCreated.setFieldPath("username");
        filterCreated.setCondition(Condition.EQ);
        filterCreated.setValue(
            node.getCreatedBy() != null ? node.getCreatedBy() : node.getUsername());
        filters.add(filterCreated);
      }
      Filter filterCustomerCode = new Filter();
      if (node.getCustomerCode() != null) {
        filterCustomerCode.setFieldPath("customerCode");
        filterCustomerCode.setCondition(Condition.EQ);
        filterCustomerCode.setValue(node.getCustomerCode());
        filters.add(filterCustomerCode);
      }
      Filter filterModule = new Filter();
      if (node.getModule() != null) {
        filterModule.setFieldPath("module");
        filterModule.setCondition(Condition.EQ);
        filterModule.setValue(node.getModule().value());
        filters.add(filterModule);
      }
      Filter filterDataSecurity = new Filter();
      if (node.getDataSecurityKey() != null) {
        filterDataSecurity.setFieldPath("dataSecurityKey");
        filterDataSecurity.setCondition(Condition.EQ);
        filterDataSecurity.setValue(node.getDataSecurityKey());
        filters.add(filterDataSecurity);
      }
      Filter filterMetricName = new Filter();
      if (node.getMetricName() != null) {
        filterMetricName.setFieldPath("metricName");
        filterMetricName.setCondition(Condition.EQ);
        filterMetricName.setValue(node.getMetricName());
        filters.add(filterMetricName);
      }
      query.setFilter(filters);
      String searchQuery =
          SipMetadataUtils.node2JsonString(
              node, basePath, node.get_id(), Action.search, Category.Semantic, query);
      logger.trace("Search Query to get the semantic :" + searchQuery);
      MetaDataStoreRequestAPI requestMetaDataStore = new MetaDataStoreRequestAPI(searchQuery);
      requestMetaDataStore.process();
      List<Object> semanticNodes = new ArrayList<Object>();
      ObjectMapper mapper = new ObjectMapper();
      if (requestMetaDataStore.getSearchResultJsonArray() != null
          && requestMetaDataStore.getSearchResultJsonArray().size() > 0) {
        JsonElement resultArray = requestMetaDataStore.getSearchResultJsonArray();
        logger.trace("Entity has retrieved successfully :" + resultArray.toString());
        if (resultArray.isJsonArray()) {
          for (int i = 0, j = 1; i < resultArray.getAsJsonArray().size(); i++, j++) {
            logger.trace("Inside resultArray.isJsonArray() ");
            logger.trace(
                " element.isJsonArray() :" + resultArray.getAsJsonArray().get(i).isJsonArray());
            logger.trace(
                " element.isJsonObject() :"
                    + resultArray
                        .getAsJsonArray()
                        .get(i)
                        .getAsJsonObject()
                        .getAsJsonObject(String.valueOf(j)));
            String jsonString =
                resultArray
                    .getAsJsonArray()
                    .get(i)
                    .getAsJsonObject()
                    .getAsJsonObject(String.valueOf(j))
                    .toString();

            SemanticNode semanticNodeTemp = mapper.readValue(jsonString, SemanticNode.class);
            logger.trace("Id: {}", semanticNodeTemp.get_id());
            // This is extra field copy of _id field to support both backend & frontend
            semanticNodeTemp.setId(semanticNodeTemp.get_id());
            semanticNodeTemp.setStatusMessage("Entity has been retrieved successfully");
            SemanticNode newSemanticNode = new SemanticNode();
            org.springframework.beans.BeanUtils.copyProperties(
                semanticNodeTemp,
                newSemanticNode,
                "dataSetId",
                "dataSecurityKey",
                "artifacts",
                "_id");
            semanticNodes.add(newSemanticNode);
          }
        }
        content.setAnalyze(semanticNodes);
        contents.add(content);
        structure.setContents(contents);
      } else {
        structure.setContents(contents);
      }
    } catch (Exception ex) {
      logger.error("While retrieving it has been found that Entity does not exist.", ex);
      throw new SipReadEntityException(
          "While retrieving it has been found that Entity does not exist");
    }
    return structure;
  }

  ArrayNode prepareDskFields(ObjectMapper objectMapper, List<Object> artifacts) {
    ArrayNode dskFields = objectMapper.createArrayNode();

    for (Object artifactObj : artifacts) {
      logger.info("Artifact Obj = " + artifactObj);

      LinkedHashMap<String, Object> artifact = (LinkedHashMap<String, Object>) artifactObj;
      //      SemanticNodeArtifact artifact = (SemanticNodeArtifact) artifactObj;
      logger.info("Artifact = " + artifact);

      List<LinkedHashMap<String, Object>> columnsList =
          (List<LinkedHashMap<String, Object>>) artifact.get("columns");

      for (LinkedHashMap<String, Object> column : columnsList) {
        Boolean dskEligible = (Boolean)(column.get("dskEligible"));

        if (dskEligible != null && dskEligible == true) {
          ObjectNode dskField = dskFields.addObject();
          String columnName = column.get(COLUMN_NAME).toString();
          String displayName =
              column.get(DISPLAY_NAME) == null ? "" : column.get(DISPLAY_NAME).toString();
          dskField.put(COLUMN_NAME, columnName);
          dskField.put(DISPLAY_NAME, displayName);
        }
      }
    }

    return dskFields;
  }
}
