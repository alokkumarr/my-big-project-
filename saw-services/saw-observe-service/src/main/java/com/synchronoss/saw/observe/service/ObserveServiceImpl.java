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
import com.synchronoss.saw.observe.model.store.Filter;
import com.synchronoss.saw.observe.model.store.Filter.Condition;
import com.synchronoss.saw.observe.model.store.MetaDataStoreStructure.Action;
import com.synchronoss.saw.observe.model.store.MetaDataStoreStructure.Category;
import com.synchronoss.saw.observe.model.store.Query;
import com.synchronoss.saw.observe.model.store.Query.Conjunction;
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
      logger.trace("Before invoking request to MaprDB JSON store :{}", ObserveUtils.node2JsonString(node, basePath, node.getEntityId(),
          Action.CREATE, Category.USER_INTERFACE));
      Request request = new Request(ObserveUtils.node2JsonString(node, basePath, node.getEntityId(),
          Action.CREATE, Category.USER_INTERFACE));
      request.process();
      response.setId(node.getEntityId());
      response = ObserveUtils.prepareResponse(node, "Entity is created successfully");
    } catch (Exception ex) {
     logger.error("Problem on the storage while creating an entity", ex);
      throw new CreateEntitySAWException("Problem on the storage while creating an entity.");
    }
    logger.debug("Response : "+ response.toString());
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
      logger.error("While retrieving it has been found that Entity does not exist.", ex);
      throw new ReadEntitySAWException("While retrieving it has been found that Entity does not exist.");
    }
    logger.debug("Response : "+ response.toString());
    return response;
  }



  @Override
  public ObserveResponse updateDashboard(Observe node)
      throws JSONValidationSAWException, UpdateEntitySAWException {
    ObserveResponse response = new ObserveResponse();
    node.setUpdatedAt(format.format(new Date()));
    try {
      Request request = new Request(ObserveUtils.node2JsonString(node, basePath, node.getEntityId(),
          Action.UPDATE, Category.USER_INTERFACE));
      request.process();
      response.setId(node.getEntityId());
      response =ObserveUtils.prepareResponse(node, "Entity is updated successfully");
    } catch (Exception ex) {
      logger.error("Entity does not exist to update.", ex);
      throw new UpdateEntitySAWException("Entity does not exist to update.");
    }
    logger.debug("Response : "+ response.toString());
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
      response =ObserveUtils.prepareResponse(node, "Entity is deleted successfully.");
    } catch (Exception ex) {
      logger.error("Entity does not exist to delete.", ex.getCause());
      throw new DeleteEntitySAWException("Entity does not exist to delete");
    }
    logger.debug("Response : "+ response.toString());
    return response;
  }
  
  @Override
  public ObserveResponse getDashboardbyCategoryId(Observe node)
      throws JSONValidationSAWException, ReadEntitySAWException {
    ObserveResponse response = new ObserveResponse();
    try {
      Query query = new Query();
      query.setConjunction(Conjunction.AND);
      List<Filter> filters = new ArrayList<>();
      Filter filter_category = new Filter();
      filter_category.setFieldPath("categoryId");
      filter_category.setCondition(Condition.EQ);
      filter_category.setValue(node.getCategoryId());
      filters.add(filter_category);
      if(node.getCreatedBy()!=null){
        Filter filter_user = new Filter();
        filter_user.setFieldPath("createdBy");
        filter_user.setCondition(Condition.EQ);
        filter_user.setValue(node.getCreatedBy());
        filters.add(filter_user);
      }
      query.setFilter(filters);
      String searchQuery= ObserveUtils.node2JsonString(node, basePath, node.getEntityId(), Action.SEARCH, Category.USER_INTERFACE, query);
      logger.debug("Search Query getDashboardbyCategoryId :" + searchQuery);
      Request request = new Request(searchQuery);
      request.process();
      if (request.getSearchResultJsonArray() !=null && request.getSearchResultJsonArray().size()>0){
      JsonElement resultArray = request.getSearchResultJsonArray();
      logger.debug("Entity has been retrieved successfully :"+ resultArray.toString());
      Content content = new Content();
      List<Observe> observeList = new ArrayList<Observe>();
      ObjectMapper mapper = new ObjectMapper();
      if (resultArray.isJsonArray()){
        for (int i=0,j=1;i<resultArray.getAsJsonArray().size();i++,j++){
          logger.debug("Inside resultArray.isJsonArray() ");
          logger.debug(" element.isJsonArray() :"+ resultArray.getAsJsonArray().get(i).isJsonArray());
          logger.debug(" element.isJsonObject() :"+ resultArray.getAsJsonArray().get(i).getAsJsonObject().getAsJsonObject(String.valueOf(j)));
          String jsonString = resultArray.getAsJsonArray().get(i).getAsJsonObject().getAsJsonObject(String.valueOf(j)).toString();
          Observe observeData = mapper.readValue(jsonString, Observe.class);
          observeList.add(observeData);}
      }
      content.setObserve(observeList);
      response.setContents(content);
      response.setMessage("Entity has been retrieved successfully");
      }
      else {
        response.setMessage("There is no data avaiable for the category Id & user Id" + node.getCategoryId() + ":" + node.getCreatedBy());
      }
    } catch (Exception ex) {
      logger.error("While retrieving it has been found that Entity does not exist.", ex);
      throw new ReadEntitySAWException("While retrieving it has been found that Entity does not exist.");
    }
    logger.debug("Response : "+ response.toString());
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
    //String jsonElementString= ObserveUtils.node2JsonString(observeDataRead, "hdfs:///main", "portalData:301", Action.CREATE, Category.USER_INTERFACE);
    
    Query query = new Query();
    query.setConjunction(Conjunction.AND);
    List<Filter> filters = new ArrayList<>();
    Filter filter_category = new Filter();
    filter_category.setFieldPath("userData.categoryId");
    filter_category.setCondition(Condition.EQ);
    filter_category.setValue("observeData");
    filters.add(filter_category);
      Filter filter_user = new Filter();
      filter_user.setFieldPath("userData.tags[]");
      filter_user.setCondition(Condition.EQ);
      filter_user.setValue("saurav");
      filters.add(filter_user);
      query.setFilter(filters);
      Observe searchNode = new Observe();
      searchNode.setCategoryId("observeData");
      searchNode.setCreatedBy("saurav");
    //String searchQuery= ObserveUtils.node2JsonString(searchNode, "hdfs:///main", "portalData:301", Action.SEARCH, Category.USER_INTERFACE, query);
    //Request requestData = new Request(searchQuery);
    //requestData.process();
    String jsonObject = "{\"id\":\"\\\"07cf603b-1b84-444c-8d2c-9c78b4c3a7b9::PortalDataSet::1513978534415\\\"\",\"1\":{\"_id\":\"07cf603b-1b84-444c-8d2c-9c78b4c3a7b9::PortalDataSet::1513978534415\",\"entityId\":\"07cf603b-1b84-444c-8d2c-9c78b4c3a7b9::PortalDataSet::1513978534415\",\"categoryId\":\"observeData\",\"name\":\"string\",\"description\":\"string\",\"createdBy\":\"saurav\",\"updatedBy\":\"string\",\"createdAt\":\"2017-35-22 09:35:34\",\"updatedAt\":\"string\",\"tiles\":[{\"type\":\"analysis\",\"id\":\"string\",\"cols\":\"3\",\"rows\":\"4\",\"x\":\"5\",\"y\":\"6\"}]}}";
    String jsonArray = "[{\"id\":\"\\\"07cf603b-1b84-444c-8d2c-9c78b4c3a7b9::PortalDataSet::1513978534415\\\"\",\"1\":{\"_id\":\"07cf603b-1b84-444c-8d2c-9c78b4c3a7b9::PortalDataSet::1513978534415\",\"entityId\":\"07cf603b-1b84-444c-8d2c-9c78b4c3a7b9::PortalDataSet::1513978534415\",\"categoryId\":\"observeData\",\"name\":\"string\",\"description\":\"string\",\"createdBy\":\"saurav\",\"updatedBy\":\"string\",\"createdAt\":\"2017-35-22 09:35:34\",\"updatedAt\":\"string\",\"tiles\":[{\"type\":\"analysis\",\"id\":\"string\",\"cols\":\"3\",\"rows\":\"4\",\"x\":\"5\",\"y\":\"6\"}]}},{\"id\":\"\\\"3298e606-e848-4439-ba67-3da4dc6d6958::PortalDataSet::1513980276331\\\"\",\"2\":{\"_id\":\"3298e606-e848-4439-ba67-3da4dc6d6958::PortalDataSet::1513980276331\",\"entityId\":\"3298e606-e848-4439-ba67-3da4dc6d6958::PortalDataSet::1513980276331\",\"categoryId\":\"observeData\",\"name\":\"string\",\"description\":\"string\",\"createdBy\":\"saurav\",\"updatedBy\":\"string\",\"createdAt\":\"2017-04-22 10:04:36\",\"updatedAt\":\"string\",\"tiles\":[{\"type\":\"analysis\",\"id\":\"string\",\"cols\":\"3\",\"rows\":\"4\",\"x\":\"5\",\"y\":\"6\"}]}}]";
    JsonParser gsonP = new JsonParser();
    JsonElement elementO = gsonP.parse(jsonObject);
    JsonElement elementA = gsonP.parse(jsonArray);
    System.out.println(elementO.isJsonObject());
    System.out.println(elementO.getAsJsonObject().get("1"));
    System.out.println(elementA.getAsJsonArray().get(0).getAsJsonObject().get("1").toString());
    Observe latest = mapper.readValue(elementA.getAsJsonArray().get(0).getAsJsonObject().get("1").toString(), Observe.class);
    System.out.println(mapper.writeValueAsString(latest));
  }
}
