package com.synchronoss.saw.observe.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.saw.observe.ObserveUtils;
import com.synchronoss.saw.observe.exceptions.CreateEntitySAWException;
import com.synchronoss.saw.observe.exceptions.DeleteEntitySAWException;
import com.synchronoss.saw.observe.exceptions.JSONValidationSAWException;
import com.synchronoss.saw.observe.exceptions.ReadEntitySAWException;
import com.synchronoss.saw.observe.exceptions.UpdateEntitySAWException;
import com.synchronoss.saw.observe.model.Observe;
import com.synchronoss.saw.observe.model.ObserveResponse;
import com.synchronoss.saw.observe.model.store.MetaDataStoreStructure;
import com.synchronoss.saw.observe.model.store.MetaDataStoreStructure.Action;
import com.synchronoss.saw.observe.model.store.MetaDataStoreStructure.Category;
import com.synchronoss.saw.store.cli.Request;

@Service
class ObserveServiceImpl implements ObserveService{
  
  private static final Logger logger = LoggerFactory.getLogger(ObserveServiceImpl.class);

  @Value("${metastore.base}")
  private String basePath;

  
  @Override
  public ObserveResponse addDashboard(Observe node) throws JSONValidationSAWException, CreateEntitySAWException {
    // TODO: Audit logging into Store is pending
    ObserveResponse response = new ObserveResponse();
    List<Observe> observe = new ArrayList<>();
    try {
      response.setId(node.getId());
      Request request = new Request(ObserveUtils.node2JsonString(node, basePath, node.getId(), Action.CREATE, Category.USER_INTERFACE));
      request.process();
      observe.add(node);
      response.setObserve(observe);
      response.setMessage("Entity is created successfully");
    }
    catch (Exception ex)
    {
      throw new CreateEntitySAWException("Problem on the storage while creating an entity");
    }
    logger.debug("Response : {}", response.toString());
    return response;
  }

  @Override
  public ObserveResponse getDashboardbyCriteria(Observe node)
      throws JSONValidationSAWException, ReadEntitySAWException {
 // TODO: Audit logging into Store is pending
    ObserveResponse response = new ObserveResponse();
    try {
      response.setId(node.getId());
      Request request = new Request(ObserveUtils.node2JsonString(node, basePath, node.getId(), Action.READ, Category.USER_INTERFACE));
      request.process();
      // TODO: Need to know the structure after retrieval.
      response.setMessage("Entity is created successfully");
    }
    catch (Exception ex)
    {
      throw new ReadEntitySAWException("Exception occured while retrieving it from storage");
    }
    logger.debug("Response : {}", response.toString());
    return response;
  }

  

  @Override
  public ObserveResponse updateDashboard(Observe node) throws JSONValidationSAWException, UpdateEntitySAWException {
    ObserveResponse response = new ObserveResponse();
    List<Observe> observe = new ArrayList<>();
    try {
      response.setId(node.getId());
      Request request = new Request(ObserveUtils.node2JsonString(node, basePath, node.getId(), Action.UPDATE, Category.USER_INTERFACE));
      request.process();
      observe.add(node);
      response.setObserve(observe);
      response.setMessage("Entity is updated successfully");
    }
    catch (Exception ex)
    {
      throw new UpdateEntitySAWException("Entity does not exist");
    }
    logger.debug("Response : {}", response.toString());
    return response;
 }

  @Override
  public ObserveResponse deleteDashboard(Observe node) throws JSONValidationSAWException, DeleteEntitySAWException {
    ObserveResponse response = new ObserveResponse();
    List<Observe> observe = new ArrayList<>();
    try 
    {
      response.setId(node.getId());
      Request request = new Request(ObserveUtils.node2JsonString(node, basePath, node.getId(), Action.DELETE, Category.USER_INTERFACE));
      request.process();
      // TODO: Can read back the data from DB before delete & set it to response
      observe.add(node);
      response.setObserve(observe);
      response.setMessage("Entity is deleted successfully");
    }
    catch (Exception ex)
    {
      throw new DeleteEntitySAWException("Entity does not exist");
    }
    logger.debug("Response : {}", response.toString());
    return response;
 }
 

  @Override
  public ObserveResponse generateId() throws JSONValidationSAWException {

    String id = UUID.randomUUID().toString() + delimiter + PortalDataSet + delimiter
        + System.currentTimeMillis();
    ObserveResponse response = new ObserveResponse();
    response.setId(id);
    return response;
  }

  public static void main(String[] args) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    MetaDataStoreStructure metaDataStoreStructure = new MetaDataStoreStructure();
    metaDataStoreStructure.setId("id_1");
    metaDataStoreStructure.setAction(Action.CREATE);
    metaDataStoreStructure.setCategory(com.synchronoss.saw.observe.model.store.MetaDataStoreStructure.Category.DATA_POD);
    List<Observe> observeList = new ArrayList<>();
    Observe observe = new Observe();
    observe.setId("id_1");
    observe.setCategoryId("category_Id");
    observeList.add(observe);
    metaDataStoreStructure.setSource(observe);
    MetaDataStoreStructure metaDataStoreStructure_1 = new MetaDataStoreStructure();
    metaDataStoreStructure_1.setId("id_2");
    metaDataStoreStructure_1.setAction(Action.CREATE);
    metaDataStoreStructure_1.setCategory(com.synchronoss.saw.observe.model.store.MetaDataStoreStructure.Category.DATA_POD);
    Observe observe_1 = new Observe();
    observe.setId("id_2");
    metaDataStoreStructure_1.setSource(observe_1);
    System.out.println(mapper.writeValueAsString(metaDataStoreStructure_1));
    List<MetaDataStoreStructure> storelist = new ArrayList<>();
    storelist.add(metaDataStoreStructure_1);
    storelist.add(metaDataStoreStructure);
    System.out.println(mapper.writeValueAsString(storelist));
    
  }

}
