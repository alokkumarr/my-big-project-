package com.synchronoss.saw.storage.proxy.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.synchronoss.saw.storage.proxy.StorageProxyUtils;
import com.synchronoss.saw.storage.proxy.exceptions.CreateEntitySAWException;
import com.synchronoss.saw.storage.proxy.exceptions.ReadEntitySAWException;
import com.synchronoss.saw.storage.proxy.exceptions.UpdateEntitySAWException;
import com.synchronoss.saw.storage.proxy.model.StorageProxy;
import com.synchronoss.saw.storage.proxy.model.StorageProxyNode;
import sncr.bda.cli.MetaDataStoreRequestAPI;
import sncr.bda.store.generic.schema.Action;
import sncr.bda.store.generic.schema.Category;
import sncr.bda.store.generic.schema.Filter;
import sncr.bda.store.generic.schema.Filter.Condition;
import sncr.bda.store.generic.schema.MetaDataStoreStructure;
import sncr.bda.store.generic.schema.Query;
import sncr.bda.store.generic.schema.Query.Conjunction;

@Service
public class StorageProxyMetaDataServiceImpl implements StorageProxyMetaDataService {

  private static final Logger logger =
      LoggerFactory.getLogger(StorageProxyMetaDataServiceImpl.class);

  @Value("${metastore.base}")
  @NotNull
  private String basePath;
  
  private String dateFormat = "yyyy-MM-dd HH:mm:ss";

  @Override
   public StorageProxy createEntryInMetaData(StorageProxy storageProxy) throws CreateEntitySAWException {
     StorageProxyUtils.checkMandatoryFields(storageProxy);
     logger.trace("createEntryInMetaData starts here:{}", storageProxy);
     String id = generateId();
     StorageProxy responseNode = new StorageProxy();
     storageProxy.setRequestedTime(new SimpleDateFormat(dateFormat).format(new Date()));
     ObjectMapper mapper = new ObjectMapper();
     try {
       List<MetaDataStoreStructure> structure = StorageProxyUtils.node2JSONObject(storageProxy, basePath,
           id, Action.create, Category.StorageProxy);
       logger.trace("Before invoking request to MaprDB JSON store :{}",
           mapper.writeValueAsString(structure));
      MetaDataStoreRequestAPI requestMetaDataStore = new MetaDataStoreRequestAPI(structure);
      requestMetaDataStore.process();
       BeanUtils.copyProperties(responseNode, storageProxy);
       responseNode.setStatusMessage("Entity " + id + " has been created successfully.");
       responseNode.setRequestedTime(new SimpleDateFormat(dateFormat).format(new Date()));
     } catch (Exception ex) {
       logger.error("Problem on the storage while creating an entity", ex);
       throw new CreateEntitySAWException("Problem on the storage while creating an entity.", ex);
     }
      logger.trace("createEntryInMetaData ends here:{}", storageProxy);
      return responseNode;
    }

  @Override
  public StorageProxy readEntryFromMetaData(StorageProxy storageProxy) throws ReadEntitySAWException {
    Preconditions.checkArgument(storageProxy.getEntityId() != null, "Id is mandatory attribute.");
    logger.trace("readEntryFromMetaData starts here:{}", storageProxy);
    logger.trace("reading semantic from the store with an Id : {}", storageProxy.getEntityId());
    StorageProxy nodeRetrieved = null;
    try {
      List<MetaDataStoreStructure> structure = StorageProxyUtils.node2JSONObject(storageProxy, basePath,
          storageProxy.getEntityId(), Action.read, Category.StorageProxy);
      logger.trace("Before invoking request to MaprDB JSON store :{}", structure);
      MetaDataStoreRequestAPI requestMetaDataStore = new MetaDataStoreRequestAPI(structure);
      requestMetaDataStore.process();
      String jsonStringFromStore = requestMetaDataStore.getResult().toString();
      ObjectMapper mapper = new ObjectMapper();
      nodeRetrieved = mapper.readValue(jsonStringFromStore, StorageProxy.class);
      logger.trace("Id: {}", nodeRetrieved.get_id());
      nodeRetrieved.setStatusMessage("Entity has retrieved successfully");
    } catch (Exception ex) {
      throw new ReadEntitySAWException("Problem on the storage while reading an entity", ex);
    }
    logger.trace("readEntryFromMetaData ends here:{}", storageProxy);
    return nodeRetrieved;
  }

  @Override
  public StorageProxy updateEntryFromMetaData(StorageProxy storageProxy) throws UpdateEntitySAWException {
    Preconditions.checkArgument(storageProxy.getEntityId()!= null, "Id is mandatory attribute.");
    logger.trace("updateEntryFromMetaData starts here:{}", storageProxy);
    logger.trace("updating storage an Id : {}", storageProxy.getEntityId());
    StorageProxy responseNode = new StorageProxy();
    Preconditions.checkArgument(storageProxy.getRequestBy() != null, "Updated by mandatory attribute.");
    storageProxy.setRequestedTime(new SimpleDateFormat(dateFormat).format(new Date()));
    try {
      List<MetaDataStoreStructure> structure = StorageProxyUtils.node2JSONObject(storageProxy, basePath,
          storageProxy.getEntityId(), Action.update, Category.StorageProxy);
      logger.trace("Before invoking request to MaprDB JSON store :{}", structure);
      MetaDataStoreRequestAPI requestMetaDataStore = new MetaDataStoreRequestAPI(structure);
      requestMetaDataStore.process();
      BeanUtils.copyProperties(responseNode, storageProxy);
      responseNode.setStatusMessage("Entity has been updated successfully");
    } catch (Exception ex) {
      throw new UpdateEntitySAWException("Problem on the storage while updating an entity", ex);
    }
    logger.trace("updateEntryFromMetaData ends here:{}", storageProxy);
    return responseNode;
  }

  @Override
  public StorageProxy deleteEntryFromMetaData(StorageProxy storageProxy) throws Exception {
    Preconditions.checkArgument(storageProxy.getEntityId() != null, "Id is mandatory attribute.");
    logger.trace("deleteEntryFromMetaData starts here:{}", storageProxy);
    logger.trace("Deleting storage from the store with an Id : {}", storageProxy.get_id());
    StorageProxy responseObject = new StorageProxy();
    try {
      List<MetaDataStoreStructure> structure = StorageProxyUtils.node2JSONObject(storageProxy, basePath,
          storageProxy.getEntityId(), Action.delete, Category.StorageProxy);
      logger.trace("Before invoking request to MaprDB JSON store :{}", structure);
      MetaDataStoreRequestAPI requestMetaDataStore = new MetaDataStoreRequestAPI(structure);
      requestMetaDataStore.process();
      responseObject.set_id(storageProxy.getEntityId());
      responseObject.setStatusMessage("Entity has been deleted successfully.");
    } catch (Exception ex) {
      throw new UpdateEntitySAWException("Problem on the storage while updating an entity", ex);
    }
    logger.trace("deleteEntryFromMetaData ends here:{}", storageProxy);
    return responseObject;
  }

  @Override
  public StorageProxyNode searchEntryFromMetaData(StorageProxy node) throws Exception {
    logger.trace("searchEntryFromMetaData criteria :{}", node);
    StorageProxyNode responseNode = new StorageProxyNode();
    try {
      Query query = new Query();
      query.setConjunction(Conjunction.AND);
      List<Filter> filters = new ArrayList<>();

      Filter filterModule = new Filter();
      if (node.getModuleName() != null) {
        filterModule.setFieldPath("moduleName");
        filterModule.setCondition(Condition.EQ);
        filterModule.setValue(node.getModuleName());
        filters.add(filterModule);
      }
      Filter filterProjectCode = new Filter();
      if (node.getProductCode() != null) {
        filterProjectCode.setFieldPath("projectCode");
        filterProjectCode.setCondition(Condition.EQ);
        filterProjectCode.setValue(node.getProductCode());
        filters.add(filterProjectCode);
      }
      query.setFilter(filters);
      String searchQuery = StorageProxyUtils.node2JSONString(node, basePath, node.get_id(),
          Action.search, Category.StorageProxy, query);
      logger.debug("Search Query to get the semantic :" + searchQuery);
      MetaDataStoreRequestAPI requestMetaDataStore = new MetaDataStoreRequestAPI(searchQuery);
      requestMetaDataStore.process();
      List<StorageProxy> storageNodes = new ArrayList<StorageProxy>();
      
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
            StorageProxy semanticNode = mapper.readValue(jsonString, StorageProxy.class);
            logger.trace("Id: {}", semanticNode.get_id());
            semanticNode.setStatusMessage("Entity has retrieved successfully");
            storageNodes.add(semanticNode);
          }
        }
        responseNode.setProxy(storageNodes);
      } else {
        throw new ReadEntitySAWException("There is no data avaiable for the given criteria");
      }
      }
      catch (Exception ex) {
        logger.error("While retrieving it has been found that Entity does not exist.", ex);
        throw new ReadEntitySAWException(
            "While retrieving it has been found that Entity does not exist.");
      }
    return responseNode;
  }


}
