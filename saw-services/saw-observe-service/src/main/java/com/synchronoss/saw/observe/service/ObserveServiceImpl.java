package com.synchronoss.saw.observe.service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.saw.observe.ObserveUtils;
import com.synchronoss.saw.observe.exceptions.CreateEntitySAWException;
import com.synchronoss.saw.observe.exceptions.DeleteEntitySAWException;
import com.synchronoss.saw.observe.exceptions.JSONValidationSAWException;
import com.synchronoss.saw.observe.exceptions.ReadEntitySAWException;
import com.synchronoss.saw.observe.exceptions.UpdateEntitySAWException;
import com.synchronoss.saw.observe.model.Content;
import com.synchronoss.saw.observe.model.Observe;
import com.synchronoss.saw.observe.model.ObserveResponse;
import com.synchronoss.saw.observe.model.store.MetaDataStoreStructure.Action;
import com.synchronoss.saw.observe.model.store.MetaDataStoreStructure.Category;
import com.synchronoss.saw.store.cli.Request;

@Service
class ObserveServiceImpl implements ObserveService {

  private static final Logger logger = LoggerFactory.getLogger(ObserveServiceImpl.class);

  @Value("${metastore.base}")
  private String basePath;

  private DateFormat format = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

  @Override
  public ObserveResponse addDashboard(Observe node)
      throws JSONValidationSAWException, CreateEntitySAWException {
    ObserveResponse response = new ObserveResponse();
    node.setCreatedAt(format.format(new Date()));
    try {
      response.setId(node.getEntityId());
      Request request = new Request(ObserveUtils.node2JsonString(node, basePath, node.getEntityId(),
          Action.CREATE, Category.USER_INTERFACE));
      request.process();
      response = ObserveUtils.prepareResponse(node, "Entity is created successfully");
    } catch (Exception ex) {
      throw new CreateEntitySAWException("Problem on the storage while creating an entity");
    }
    logger.debug("Response : {}", response.toString());
    return response;
  }

  @Override
  public ObserveResponse getDashboardbyCriteria(Observe node)
      throws JSONValidationSAWException, ReadEntitySAWException {
    ObserveResponse response = new ObserveResponse();
    try {
      response.setId(node.getEntityId());
      Request request = new Request(ObserveUtils.node2JsonString(node, basePath, node.getEntityId(),
          Action.READ, Category.USER_INTERFACE));
      request.process();
      String jsonStringFromStore = request.getResult().toString();
      response.setMessage("Entity has been retrieved successfully");
      ObjectMapper mapper = new ObjectMapper();
      Observe observeData = mapper.readValue(jsonStringFromStore, Observe.class);
      response = ObserveUtils.prepareResponse(observeData, "Entity has been retrieved successfully");
    } catch (Exception ex) {
      throw new ReadEntitySAWException("Exception occured while retrieving it from storage");
    }
    logger.debug("Response : {}", response.toString());
    return response;
  }



  @Override
  public ObserveResponse updateDashboard(Observe node)
      throws JSONValidationSAWException, UpdateEntitySAWException {
    ObserveResponse response = new ObserveResponse();
    node.setUpdatedAt(format.format(new Date()));
    try {
      response.setId(node.getEntityId());
      Request request = new Request(ObserveUtils.node2JsonString(node, basePath, node.getEntityId(),
          Action.UPDATE, Category.USER_INTERFACE));
      request.process();
      response =ObserveUtils.prepareResponse(node, "Entity is updated successfully");
    } catch (Exception ex) {
      throw new UpdateEntitySAWException("Entity does not exist");
    }
    logger.debug("Response : {}", response.toString());
    return response;
  }

  @Override
  public ObserveResponse deleteDashboard(Observe node)
      throws JSONValidationSAWException, DeleteEntitySAWException {
    ObserveResponse response = new ObserveResponse();
    node.setUpdatedAt(format.format(new Date()));
    try {
      response.setId(node.getEntityId());
      Request request = new Request(ObserveUtils.node2JsonString(node, basePath, node.getEntityId(),
          Action.DELETE, Category.USER_INTERFACE));
      request.process();
      response =ObserveUtils.prepareResponse(node, "Entity is deleted successfully");
    } catch (Exception ex) {
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

  public static void main(String[] args) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    String jsonStringStore =
        "{\n  \"_id\": \"id:portalDataSet::201\",\n  \"entityId\": \"string\",\n  \"name\": \"string\",\n  \"description\": \"string\",\n  \"createdBy\": \"string\",\n  \"updatedBy\": \"string\",\n  \"createdAt\": \"string\",\n  \"updatedAt\": \"string\",\n  \"options\" : [],\n  \"tiles\": [\n    {\n      \"type\": \"analysis\",\n      \"id\": \"analysisId - string\",\n      \"cols\": 3,\n      \"rows\": 4,\n      \"x\": 5,\n      \"y\": 6,\n      \"options\": \"\"\n    }\n  ],\n  \"filters\": []\n}";
    Observe observeData = mapper.readValue(jsonStringStore, Observe.class);
    System.out.println(observeData.getEntityId());
    ObserveResponse response = new ObserveResponse();
    response.setMessage("Data has been set successfully.");
    response.setId(observeData.get_id());
    List<Observe> observeListNew = new ArrayList<>();
    observeListNew.add(observeData);
    Content contents = new Content();
    contents.setObserve(observeListNew);
    response.setContents(contents);
    System.out.println(mapper.writeValueAsString(response));
  }
}
