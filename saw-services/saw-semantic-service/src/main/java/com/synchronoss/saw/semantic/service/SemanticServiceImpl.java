package com.synchronoss.saw.semantic.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.synchronoss.saw.semantic.SAWSemanticUtils;
import com.synchronoss.saw.semantic.exceptions.CreateEntitySAWException;
import com.synchronoss.saw.semantic.exceptions.DeleteEntitySAWException;
import com.synchronoss.saw.semantic.exceptions.JSONValidationSAWException;
import com.synchronoss.saw.semantic.exceptions.ReadEntitySAWException;
import com.synchronoss.saw.semantic.exceptions.UpdateEntitySAWException;
import com.synchronoss.saw.semantic.model.Action.Verb;
import com.synchronoss.saw.semantic.model.NodeCategory;
import com.synchronoss.saw.semantic.model.request.SemanticNode;
import com.synchronoss.saw.semantic.model.request.SemanticNodes;
import sncr.bda.cli.MetaDataStoreRequestAPI;
import sncr.bda.store.generic.schema.Action;
import sncr.bda.store.generic.schema.Category;
import sncr.bda.store.generic.schema.Filter;
import sncr.bda.store.generic.schema.Filter.Condition;
import sncr.bda.store.generic.schema.MetaDataStoreStructure;
import sncr.bda.store.generic.schema.Query;
import sncr.bda.store.generic.schema.Query.Conjunction;


public class SemanticServiceImpl implements SemanticService {

  private static final Logger logger = LoggerFactory.getLogger(SemanticServiceImpl.class);

  private DateFormat format = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

  @Value("${metastore.base}")
  @NotNull
  private String basePath;

  @Override
  public SemanticNode addSemantic(SemanticNode node)
      throws JSONValidationSAWException, CreateEntitySAWException {
    logger.trace("Adding semantic with an Id : {}", node.get_id());
    node.setCreatedAt(format.format(new Date()));
    node.setCreatedBy(node.getUsername());
    try {
      NodeCategory nodeCategory = new NodeCategory();
      nodeCategory.setNodeCategory(nodeCategoryConvention);
      nodeCategory.setId(node.get_id());
      com.synchronoss.saw.semantic.model.Action internalAction =
          new com.synchronoss.saw.semantic.model.Action();
      internalAction.setVerb(Verb.CREATE);
      internalAction.setContent(node);
      nodeCategory.setAction(internalAction);
      List<MetaDataStoreStructure> structure = SAWSemanticUtils.node2JSONObject(nodeCategory, basePath, node.get_id(), Action.CREATE, Category.SEMANTIC);
      logger.trace("Before invoking request to MaprDB JSON store :{}", structure);
      MetaDataStoreRequestAPI requestAPI = new MetaDataStoreRequestAPI(structure);
      requestAPI.process();
      node.setStatusMessage("Entity is created successfully");
    } catch (Exception ex) {
      logger.error("Problem on the storage while creating an entity", ex);
      throw new CreateEntitySAWException("Problem on the storage while creating an entity.", ex);
    }
    logger.debug("Response : " + node.toString());
    return node;
  }

  

  @Override
  public SemanticNode readSemantic(SemanticNode node)
      throws JSONValidationSAWException, ReadEntitySAWException {
    Preconditions.checkArgument(node.get_id()!=null, "Id is mandatory attribute.");
    logger.trace("reading semantic from the store with an Id : {}", node.get_id());
    SemanticNode responseObject = new SemanticNode();
    responseObject.set_id(node.get_id());
    try {
      NodeCategory nodeCategory = new NodeCategory();
      nodeCategory.setNodeCategory(nodeCategoryConvention);
      nodeCategory.setId(node.get_id());
      com.synchronoss.saw.semantic.model.Action internalAction = new com.synchronoss.saw.semantic.model.Action();
      internalAction.setVerb(Verb.READ);
      internalAction.setContent(node);
      nodeCategory.setAction(internalAction);
      List<MetaDataStoreStructure> structure = SAWSemanticUtils.node2JSONObject(nodeCategory, basePath, node.get_id(), Action.READ, Category.SEMANTIC);
      MetaDataStoreRequestAPI requestAPI = new MetaDataStoreRequestAPI(structure);
      requestAPI.process();
      String jsonStringFromStore = requestAPI.getResult().toString();
      responseObject.setStatusMessage("Entity has been retrieved successfully");
      ObjectMapper mapper = new ObjectMapper();
      NodeCategory nodeCategoryData = mapper.readValue(jsonStringFromStore, NodeCategory.class);
      responseObject = mapper.readValue(nodeCategoryData.getAction().getContent().toString(), SemanticNode.class);
    }
    catch (Exception ex) {
      throw new ReadEntitySAWException("Problem on the storage while reading an entity", ex);
    }
    return responseObject;
  }

  @Override
  public SemanticNode updateSemantic(SemanticNode node) throws JSONValidationSAWException, UpdateEntitySAWException {
    Preconditions.checkArgument(node.get_id()!=null, "Id is mandatory attribute.");
    logger.trace("updating semantic from the store with an Id : {}", node.get_id());
    SemanticNode responseObject = new SemanticNode();
    responseObject.set_id(node.get_id());
    node.setUpdatedBy(node.getUsername());
    node.setUpdatedAt(format.format(new Date()));
    try {
      NodeCategory nodeCategory = new NodeCategory();
      nodeCategory.setNodeCategory(nodeCategoryConvention);
      nodeCategory.setId(node.get_id());
      com.synchronoss.saw.semantic.model.Action internalAction = new com.synchronoss.saw.semantic.model.Action();
      internalAction.setVerb(Verb.UPDATE);
      internalAction.setContent(node);
      nodeCategory.setAction(internalAction);
      List<MetaDataStoreStructure> structure = SAWSemanticUtils.node2JSONObject(nodeCategory, basePath, node.get_id(), Action.UPDATE, Category.SEMANTIC);
      MetaDataStoreRequestAPI requestAPI = new MetaDataStoreRequestAPI(structure);
      requestAPI.process();
      String jsonStringFromStore = requestAPI.getResult().toString();
      responseObject.setStatusMessage("Entity has been updated successfully");
      ObjectMapper mapper = new ObjectMapper();
      NodeCategory nodeCategoryData = mapper.readValue(jsonStringFromStore, NodeCategory.class);
      responseObject = mapper.readValue(nodeCategoryData.getAction().getContent().toString(), SemanticNode.class);
    }
    catch (Exception ex) {
      throw new UpdateEntitySAWException("Problem on the storage while updating an entity", ex);
    }
    return responseObject;
  }

  @Override
  public SemanticNode deleteSemantic(SemanticNode node)
      throws JSONValidationSAWException, DeleteEntitySAWException {
    Preconditions.checkArgument(node.get_id()!=null, "Id is mandatory attribute.");
    logger.trace("Deleting semantic from the store with an Id : {}", node.get_id());
    SemanticNode responseObject = new SemanticNode();
    responseObject.set_id(node.get_id());
    try {
      NodeCategory nodeCategory = new NodeCategory();
      nodeCategory.setNodeCategory(nodeCategoryConvention);
      nodeCategory.setId(node.get_id());
      com.synchronoss.saw.semantic.model.Action internalAction = new com.synchronoss.saw.semantic.model.Action();
      internalAction.setVerb(Verb.DELETE);
      internalAction.setContent(node);
      nodeCategory.setAction(internalAction);
      List<MetaDataStoreStructure> structure = SAWSemanticUtils.node2JSONObject(nodeCategory, basePath, node.get_id(), Action.UPDATE, Category.SEMANTIC);
      MetaDataStoreRequestAPI requestAPI = new MetaDataStoreRequestAPI(structure);
      requestAPI.process();
      responseObject.setStatusMessage("Entity has been updated successfully");
    }
    catch (Exception ex) {
      throw new UpdateEntitySAWException("Problem on the storage while updating an entity", ex);
    }
    return responseObject;
  }

  @Override
  public SemanticNodes search(SemanticNode node)
      throws JSONValidationSAWException, ReadEntitySAWException {
      logger.trace("search criteria :{}", node);
      SemanticNodes responseNode = new SemanticNodes();
      try {
        Query query = new Query();
        query.setConjunction(Conjunction.AND);
        List<Filter> filters = new ArrayList<>();
        Filter filterCreated = new Filter();
        if (node.getCreatedBy()!=null || node.getUsername()!=null) {
          filterCreated.setFieldPath("username");
          filterCreated.setCondition(Condition.EQ);
          filterCreated.setValue(node.getCreatedBy()!=null? node.getCreatedBy() : node.getUsername());
          filters.add(filterCreated);
        }
        Filter filterCustomerCode = new Filter();
        if (node.getCustomerCode()!=null) {
          filterCustomerCode.setFieldPath("customerCode"); 
          filterCustomerCode.setCondition(Condition.EQ);
          filterCustomerCode.setValue(node.getCustomerCode());
          filters.add(filterCreated);
        }
        Filter filterModule = new Filter();
        if (node.getModule()!=null) {
          filterModule.setFieldPath("module"); 
          filterModule.setCondition(Condition.EQ);
          filterModule.setValue(node.getModule().value());
          filters.add(filterModule);
        }
        Filter filterDSK = new Filter();
        if (node.getDataSecurityKey()!=null) {
          filterDSK.setFieldPath("dataSecurityKey"); 
          filterDSK.setCondition(Condition.EQ);
          filterDSK.setValue(node.getDataSecurityKey());
          filters.add(filterDSK);
        }
        Filter filterMetricName = new Filter();
        if (node.getMetricName()!=null) {
          filterMetricName.setFieldPath("metricName"); 
          filterMetricName.setCondition(Condition.EQ);
          filterMetricName.setValue(node.getMetricName());
          filters.add(filterMetricName);
        }
        Filter filterProjectCode = new Filter();
        if (node.getMetricName()!=null) {
          filterProjectCode.setFieldPath("projectCode"); 
          filterProjectCode.setCondition(Condition.EQ);
          filterProjectCode.setValue(node.getProjectCode());
          filters.add(filterProjectCode);
        }
        query.setFilter(filters);
        String searchQuery= SAWSemanticUtils.node2JsonString(node, basePath, node.get_id(), Action.SEARCH, Category.SEMANTIC, query);
        logger.debug("Search Query to get the semantic :" + searchQuery);
        MetaDataStoreRequestAPI requestAPI = new MetaDataStoreRequestAPI(searchQuery);
        requestAPI.process();
        List<SemanticNode> semanticNodes  = new ArrayList<SemanticNode>();
        ObjectMapper mapper = new ObjectMapper();
        if (requestAPI.getSearchResultJsonArray() !=null && requestAPI.getSearchResultJsonArray().size()>0){
          JsonElement resultArray = requestAPI.getSearchResultJsonArray();
          logger.debug("Entity has been retrieved successfully :"+ resultArray.toString());
          if (resultArray.isJsonArray()){
            for (int i=0,j=1;i<resultArray.getAsJsonArray().size();i++,j++){
              logger.debug("Inside resultArray.isJsonArray() ");
              logger.debug(" element.isJsonArray() :"+ resultArray.getAsJsonArray().get(i).isJsonArray());
              logger.debug(" element.isJsonObject() :"+ resultArray.getAsJsonArray().get(i).getAsJsonObject().getAsJsonObject(String.valueOf(j)));
              String jsonString = resultArray.getAsJsonArray().get(i).getAsJsonObject().getAsJsonObject(String.valueOf(j)).toString();
              NodeCategory nodeCategory = mapper.readValue(jsonString, NodeCategory.class);
              SemanticNode semanticNode = mapper.readValue(mapper.writeValueAsString(nodeCategory.getAction().getContent()),SemanticNode.class);
              semanticNodes.add(semanticNode);
              }
          }
          responseNode.setSemanticNodes(semanticNodes); 
        }
        else {
          throw new ReadEntitySAWException("There is no data avaiable for the given criteria");}
      }catch (Exception ex) {
          logger.error("While retrieving it has been found that Entity does not exist.", ex);
          throw new ReadEntitySAWException("While retrieving it has been found that Entity does not exist.");
        }
    return responseNode;
  }



  @Override
  public SemanticNodes list(SemanticNode node)
      throws JSONValidationSAWException, ReadEntitySAWException {
      logger.trace("search criteria :{}", node);
      SemanticNodes responseNode = new SemanticNodes();
      try {
        Query query = new Query();
        query.setConjunction(Conjunction.AND);
        List<Filter> filters = new ArrayList<>();
        Filter filterCreated = new Filter();
        if (node.getCreatedBy()!=null || node.getUsername()!=null) {
          filterCreated.setFieldPath("username");
          filterCreated.setCondition(Condition.EQ);
          filterCreated.setValue(node.getCreatedBy()!=null? node.getCreatedBy() : node.getUsername());
          filters.add(filterCreated);
        }
        Filter filterCustomerCode = new Filter();
        if (node.getCustomerCode()!=null) {
          filterCustomerCode.setFieldPath("customerCode"); 
          filterCustomerCode.setCondition(Condition.EQ);
          filterCustomerCode.setValue(node.getCustomerCode());
          filters.add(filterCreated);
        }
        Filter filterModule = new Filter();
        if (node.getModule()!=null) {
          filterModule.setFieldPath("module"); 
          filterModule.setCondition(Condition.EQ);
          filterModule.setValue(node.getModule().value());
          filters.add(filterModule);
        }
        Filter filterDSK = new Filter();
        if (node.getDataSecurityKey()!=null) {
          filterDSK.setFieldPath("dataSecurityKey"); 
          filterDSK.setCondition(Condition.EQ);
          filterDSK.setValue(node.getDataSecurityKey());
          filters.add(filterDSK);
        }
        Filter filterMetricName = new Filter();
        if (node.getMetricName()!=null) {
          filterMetricName.setFieldPath("metricName"); 
          filterMetricName.setCondition(Condition.EQ);
          filterMetricName.setValue(node.getMetricName());
          filters.add(filterMetricName);
        }
        Filter filterProjectCode = new Filter();
        if (node.getMetricName()!=null) {
          filterProjectCode.setFieldPath("projectCode"); 
          filterProjectCode.setCondition(Condition.EQ);
          filterProjectCode.setValue(node.getProjectCode());
          filters.add(filterProjectCode);
        }
        query.setFilter(filters);
        String searchQuery= SAWSemanticUtils.node2JsonString(node, basePath, node.get_id(), Action.SEARCH, Category.SEMANTIC, query);
        logger.debug("Search Query to get the semantic :" + searchQuery);
        MetaDataStoreRequestAPI requestAPI = new MetaDataStoreRequestAPI(searchQuery);
        requestAPI.process();
        List<SemanticNode> semanticNodes  = new ArrayList<SemanticNode>();
        ObjectMapper mapper = new ObjectMapper();
        if (requestAPI.getSearchResultJsonArray() !=null && requestAPI.getSearchResultJsonArray().size()>0){
          JsonElement resultArray = requestAPI.getSearchResultJsonArray();
          logger.debug("Entity has been retrieved successfully :"+ resultArray.toString());
          if (resultArray.isJsonArray()){
            for (int i=0,j=1;i<resultArray.getAsJsonArray().size();i++,j++){
              logger.debug("Inside resultArray.isJsonArray() ");
              logger.debug(" element.isJsonArray() :"+ resultArray.getAsJsonArray().get(i).isJsonArray());
              logger.debug(" element.isJsonObject() :"+ resultArray.getAsJsonArray().get(i).getAsJsonObject().getAsJsonObject(String.valueOf(j)));
              String jsonString = resultArray.getAsJsonArray().get(i).getAsJsonObject().getAsJsonObject(String.valueOf(j)).toString();
              NodeCategory nodeCategory = mapper.readValue(jsonString, NodeCategory.class);
              SemanticNode semanticNodeTemp = mapper.readValue(mapper.writeValueAsString(nodeCategory.getAction().getContent()),SemanticNode.class);
              SemanticNode semanticNode = new SemanticNode();
              org.springframework.beans.BeanUtils.copyProperties(semanticNodeTemp, semanticNode, "dataSetId","dataSecurityKey","esRepository", "repository", "artifacts");
              semanticNodes.add(semanticNode);
              }
          }
          responseNode.setSemanticNodes(semanticNodes); 
        }
        else {
          throw new ReadEntitySAWException("There is no data avaiable for the given criteria");}
      }catch (Exception ex) {
          logger.error("While retrieving it has been found that Entity does not exist.", ex);
          throw new ReadEntitySAWException("While retrieving it has been found that Entity does not exist.");
        }
    return responseNode;
  }


}

