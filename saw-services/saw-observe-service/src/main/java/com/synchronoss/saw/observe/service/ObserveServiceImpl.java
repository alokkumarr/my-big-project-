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
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
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
public class ObserveServiceImpl implements ObserveService {

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
  public String generateId() throws JSONValidationSAWException {
    String id = UUID.randomUUID().toString() + delimiter + PortalDataSet + delimiter
        + System.currentTimeMillis();
    
    return id;
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
    String jsonString ="{\"contents\":{\"observe\":[{\"_id\":\"id:portalDataSet::201\",\"entityId\":\"string\",\"categoryId\":\"string\",\"name\":\"string\",\"description\":\"string\",\"createdBy\":\"string\",\"updatedBy\":\"string\",\"createdAt\":\"string\",\"updatedAt\":\"string\",\"options\":[],\"tiles\":[{\"type\":\"analysis\",\"id\":\"analysisId - string\",\"cols\":3,\"rows\":4,\"x\":5,\"y\":6,\"options\":\"\"}],\"filters\":[]}]}}";
    System.out.println(mapper.writeValueAsString(ObserveUtils.getObserveNode(jsonString, "contents")));
    String jsonStringStoreRead = "{\"_id\": \"40139212-e078-4013-90dd-452347d460dd::PortalDataSet::1513718861739\", \"entityId\":\"40139212-e078-4013-90dd-452347d460dd::PortalDataSet::1513718861739\",\"categoryId\":\"string\",\"name\":\"string\",\"description\":\"string\",\"createdBy\":\"string\",\"updatedBy\":\"string\",\"createdAt\":\"2017-27-19 04:27:41\",\"updatedAt\":\"string\",\"tiles\":[{\"type\":\"analysis\",\"id\":\"string\",\"cols\":3,\"rows\":4,\"x\":5,\"y\":6}]}";
    Observe observeDataRead = mapper.readValue(jsonStringStoreRead, Observe.class);
    System.out.println(observeDataRead.get_id());
    JsonParser parser = new JsonParser();
    JsonElement element = parser.parse(jsonStringStoreRead);
    System.out.println(element.isJsonObject());
    System.out.println();
  }
}
