package com.synchronoss.saw.semantic.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.synchronoss.saw.semantic.SAWSemanticUtils;
import com.synchronoss.saw.semantic.exceptions.CreateEntitySAWException;
import com.synchronoss.saw.semantic.exceptions.DeleteEntitySAWException;
import com.synchronoss.saw.semantic.exceptions.JSONValidationSAWException;
import com.synchronoss.saw.semantic.exceptions.ReadEntitySAWException;
import com.synchronoss.saw.semantic.exceptions.UpdateEntitySAWException;
import com.synchronoss.saw.semantic.model.DataSet;
import com.synchronoss.saw.semantic.model.request.BackCompatibleStructure;
import com.synchronoss.saw.semantic.model.request.Content;
import com.synchronoss.saw.semantic.model.request.SemanticNode;
import com.synchronoss.saw.semantic.model.request.SemanticNodes;
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
  private String transportURI;

  @Value("${semantic.binary-migration-requires}")
  @NotNull
  private boolean migrationRequires;

  @Value("${semantic.migration-metadata-home}")
  @NotNull
  private String migrationMetadataHome;

  //@PostConstruct
  private void init() throws Exception {
    if (migrationRequires) {
      new MigrationService().convertHBaseBinaryToMaprDBStore(transportURI, basePath, migrationMetadataHome);
    }
  }

  @Override
  public SemanticNode addSemantic(SemanticNode node)
      throws JSONValidationSAWException, CreateEntitySAWException {
    logger.trace("Adding semantic with an Id : {}", node.get_id());
    SemanticNode responseNode = new SemanticNode();
    SemanticNode newSemanticNode = null;
    node.setCreatedBy(node.getUsername());
    ObjectMapper mapper = new ObjectMapper();
    try {
      List<MetaDataStoreStructure> structure = SAWSemanticUtils.node2JSONObject(node, basePath,
          node.get_id(), Action.create, Category.Semantic);
      logger.trace("Before invoking request to MaprDB JSON store :{}",
          mapper.writeValueAsString(structure));
      node = setRepository(node);
      MetaDataStoreRequestAPI requestMetaDataStore = new MetaDataStoreRequestAPI(structure);
      requestMetaDataStore.process();
      responseNode.setId(node.get_id());
      responseNode.setCreatedBy(node.getCreatedBy());
      responseNode.setSaved(true);
      responseNode.setStatusMessage("Entity is created successfully");
      newSemanticNode = new SemanticNode();
      org.springframework.beans.BeanUtils.copyProperties(responseNode, newSemanticNode,"_id");
    } catch (Exception ex) {
      logger.error("Problem on the storage while creating an entity", ex);
      throw new CreateEntitySAWException("Problem on the storage while creating an entity.", ex);
    }
    logger.trace("Response : " + node.toString());
    return newSemanticNode;
  }

  /**
   * This method to set the physicalLocation, format & name under repository section.
   * when it is from DataLake
   * @param node
   * @return
   * @throws IOException
   * @throws JsonProcessingException
   */
  private SemanticNode setRepository(SemanticNode semanticNode) throws JsonProcessingException, IOException
  {
    logger.trace("Setting repository starts here..");
    String requestURL = workbenchURl + "/internal/workbench/projects/" + semanticNode.getProjectCode() + "/datasets/";
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
      for (String dataSetId: semanticNode.getParentDataSetIds()) {
        logger.trace("Request URL to pull DataSet Details : " + requestURL + dataSetId);
        RestTemplate restTemplate = new RestTemplate();
        dataSet = restTemplate.getForObject(requestURL + dataSetId, DataSet.class);
        node = objectMapper.readTree(objectMapper.writeValueAsString(dataSet));
        rootNode = (ObjectNode) node;
        systemNode = (ObjectNode) rootNode.get(DataSetProperties.System.toString());
        physicalLocation = systemNode.get(DataSetProperties.PhysicalLocation.toString()).asText();
        dataSetName = systemNode.get(DataSetProperties.Name.toString()).asText();
        dataSetFormat = systemNode.get(DataSetProperties.Format.toString()).asText();
        repoNode = objectMapper.createObjectNode();
        repoNode.put(DataSetProperties.Name.toString(), dataSetName);
        repoNode.put(DataSetProperties.Format.toString(), dataSetFormat);
        repoNode.put(DataSetProperties.PhysicalLocation.toString(), physicalLocation + Path.SEPARATOR);
        dataSetDetailsObject.add(repoNode);
        respository.add(repoNode);
   }
    semanticNode.setRepository(dataSetDetailsObject);
    logger.trace("Setting repository ends here.");
    logger.trace("Semantic node after adding repository for DL type: " + objectMapper.writeValueAsString(semanticNode));
    return semanticNode;
  }


  @Override
  public SemanticNode readSemantic(SemanticNode node)
      throws JSONValidationSAWException, ReadEntitySAWException {
    Preconditions.checkArgument(node.get_id() != null, "Id is mandatory attribute.");
    logger.trace("reading semantic from the store with an Id : {}", node.get_id());
    SemanticNode nodeRetrieved = null;
    SemanticNode newSemanticNode = null;
    try {
      List<MetaDataStoreStructure> structure = SAWSemanticUtils.node2JSONObject(node, basePath,
          node.get_id(), Action.read, Category.Semantic);
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
      org.springframework.beans.BeanUtils.copyProperties(nodeRetrieved, newSemanticNode,"_id");
    } catch (Exception ex) {
      throw new ReadEntitySAWException("Problem on the storage while reading an entity", ex);
    }
    return newSemanticNode;
  }
  @Override
  public SemanticNode updateSemantic(SemanticNode node)
      throws JSONValidationSAWException, UpdateEntitySAWException {
    Preconditions.checkArgument(node.get_id() != null, "Id is mandatory attribute.");
    logger.trace("updating semantic from the store with an Id : {}", node.get_id());
    Preconditions.checkArgument(node.getUpdatedBy() != null, "Updated by mandatory attribute.");
    SemanticNode responseNode = new SemanticNode();
    SemanticNode newSemanticNode = null;
    node.setUpdatedBy(node.getUpdatedBy());
    try {
      List<MetaDataStoreStructure> structure = SAWSemanticUtils.node2JSONObject(node, basePath,
          node.get_id(), Action.update, Category.Semantic);
      logger.trace("Before invoking request to MaprDB JSON store :{}", structure);
      MetaDataStoreRequestAPI requestMetaDataStore = new MetaDataStoreRequestAPI(structure);
      requestMetaDataStore.process();
      responseNode.setId(node.get_id());
      responseNode.setUpdatedBy(node.getUpdatedBy());
      responseNode.setSaved(true);
      responseNode.setStatusMessage("Entity has been updated successfully");
      newSemanticNode = new SemanticNode();
      org.springframework.beans.BeanUtils.copyProperties(responseNode, newSemanticNode,"_id");
    } catch (Exception ex) {
      throw new UpdateEntitySAWException("Problem on the storage while updating an entity", ex);
    }
    return newSemanticNode;
  }

  @Override
  public SemanticNode deleteSemantic(SemanticNode node)
      throws JSONValidationSAWException, DeleteEntitySAWException {
    Preconditions.checkArgument(node.get_id() != null, "Id is mandatory attribute.");
    logger.trace("Deleting semantic from the store with an Id : {}", node.get_id());
    SemanticNode responseObject = new SemanticNode();
    SemanticNode newSemanticNode= new SemanticNode();
    try {
      List<MetaDataStoreStructure> structure = SAWSemanticUtils.node2JSONObject(node, basePath,
          node.get_id(), Action.delete, Category.Semantic);
      logger.trace("Before invoking request to MaprDB JSON store :{}", structure);
      MetaDataStoreRequestAPI requestMetaDataStore = new MetaDataStoreRequestAPI(structure);
      requestMetaDataStore.process();
      responseObject.setId(node.get_id());
    } catch (Exception ex) {
      throw new UpdateEntitySAWException("Problem on the storage while updating an entity", ex);
    }
    return newSemanticNode;
  }


  @Override
  public SemanticNodes search(SemanticNode node, Map<String, String> headers)
      throws JSONValidationSAWException, ReadEntitySAWException {
    logger.trace("search criteria :{}", node);
    SemanticNodes responseNode = new SemanticNodes();
    if (headers.get("x-customercode")!=null) {
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
        filterCreated
            .setValue(node.getCreatedBy() != null ? node.getCreatedBy() : node.getUsername());
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
      Filter filterProjectCode = new Filter();
      if (node.getProjectCode() != null) {
        filterProjectCode.setFieldPath("projectCode");
        filterProjectCode.setCondition(Condition.EQ);
        filterProjectCode.setValue(node.getProjectCode());
        filters.add(filterProjectCode);
      }
      query.setFilter(filters);
      String searchQuery = SAWSemanticUtils.node2JsonString(node, basePath, node.get_id(),
          Action.search, Category.Semantic, query);
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
            logger.debug(" element.isJsonObject() :" + resultArray.getAsJsonArray().get(i)
                .getAsJsonObject().getAsJsonObject(String.valueOf(j)));
            String jsonString = resultArray.getAsJsonArray().get(i).getAsJsonObject()
                .getAsJsonObject(String.valueOf(j)).toString();
            SemanticNode semanticNode = mapper.readValue(jsonString, SemanticNode.class);
            logger.trace("Id: {}", semanticNode.get_id());
            // This is extra field copy of _id field to support both backend & frontend
            semanticNode.setId(semanticNode.get_id());
            semanticNode.setStatusMessage("Entity has retrieved successfully");
            SemanticNode newSemanticNode = new SemanticNode();
            org.springframework.beans.BeanUtils.copyProperties(semanticNode, newSemanticNode,
               "_id");
            semanticNodes.add(newSemanticNode);
          }
        }
        responseNode.setSemanticNodes(semanticNodes);
      } else {
        throw new ReadEntitySAWException("There is no data avaiable for the given criteria");
      }
    } catch (Exception ex) {
      logger.error("While retrieving it has been found that Entity does not exist.", ex);
      throw new ReadEntitySAWException(
          "While retrieving it has been found that Entity does not exist.");
    }
    return responseNode;
  }


  @Override
  public BackCompatibleStructure list(SemanticNode node, Map<String, String> headers)
      throws JSONValidationSAWException, ReadEntitySAWException {
    logger.trace("search criteria :{}", node);
    BackCompatibleStructure structure = new BackCompatibleStructure();
    Content  content = new Content();
    List<Content> contents = new ArrayList<>();
    if (headers.get("x-customercode")!=null) {
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
        filterCreated
            .setValue(node.getCreatedBy() != null ? node.getCreatedBy() : node.getUsername());
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
      Filter filterProjectCode = new Filter();
      if (node.getProjectCode()!= null) {
        filterProjectCode.setFieldPath("projectCode");
        filterProjectCode.setCondition(Condition.EQ);
        filterProjectCode.setValue(node.getProjectCode());
        filters.add(filterProjectCode);
      }
      query.setFilter(filters);
      String searchQuery = SAWSemanticUtils.node2JsonString(node, basePath, node.get_id(),
          Action.search, Category.Semantic, query);
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
            logger.trace(" element.isJsonObject() :" + resultArray.getAsJsonArray().get(i)
                .getAsJsonObject().getAsJsonObject(String.valueOf(j)));
            String jsonString = resultArray.getAsJsonArray().get(i).getAsJsonObject()
                .getAsJsonObject(String.valueOf(j)).toString();

            SemanticNode semanticNodeTemp = mapper.readValue(jsonString, SemanticNode.class);
            logger.trace("Id: {}", semanticNodeTemp.get_id());
            // This is extra field copy of _id field to support both backend & frontend
            semanticNodeTemp.setId(semanticNodeTemp.get_id());
            semanticNodeTemp.setStatusMessage("Entity has been retrieved successfully");
            SemanticNode newSemanticNode = new SemanticNode();
            org.springframework.beans.BeanUtils.copyProperties(semanticNodeTemp, newSemanticNode,
                "dataSetId", "dataSecurityKey","artifacts","_id");
            semanticNodes.add(newSemanticNode);
          }
        }
        content.setAnalyze(semanticNodes);
        contents.add(content);
        structure.setContents(contents);
      } else {
        throw new ReadEntitySAWException("There is no data avaiable for the given criteria");
      }
    } catch (Exception ex) {
      logger.error("While retrieving it has been found that Entity does not exist.", ex);
      throw new ReadEntitySAWException(
          "While retrieving it has been found that Entity does not exist");
    }
    return structure;
  }
  public static void main(String[] args) {

  }
}
