package com.synchronoss.saw.observe.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
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
      logger.trace("Before invoking request to MaprDB JSON store :{}", ObserveUtils.node2JsonString(
          node, basePath, node.getEntityId(), Action.CREATE, Category.USER_INTERFACE));
      Request request = new Request(ObserveUtils.node2JsonString(node, basePath, node.getEntityId(),
          Action.CREATE, Category.USER_INTERFACE));
      request.process();
      response.setId(node.getEntityId());
      response = ObserveUtils.prepareResponse(node, "Entity is created successfully");
    } catch (Exception ex) {
      logger.error("Problem on the storage while creating an entity", ex);
      throw new CreateEntitySAWException("Problem on the storage while creating an entity.");
    }
    logger.debug("Response : " + response.toString());
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
      response =
          ObserveUtils.prepareResponse(observeData, "Entity has been retrieved successfully");
    } catch (Exception ex) {
      logger.error("While retrieving it has been found that Entity does not exist.", ex);
      throw new ReadEntitySAWException(
          "While retrieving it has been found that Entity does not exist.");
    }
    logger.debug("Response : " + response.toString());
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
      response = ObserveUtils.prepareResponse(node, "Entity is updated successfully");
    } catch (Exception ex) {
      logger.error("Entity does not exist to update.", ex);
      throw new UpdateEntitySAWException("Entity does not exist to update.");
    }
    logger.debug("Response : " + response.toString());
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
      response = ObserveUtils.prepareResponse(node, "Entity is deleted successfully.");
    } catch (Exception ex) {
      logger.error("Entity does not exist to delete.", ex.getCause());
      throw new DeleteEntitySAWException("Entity does not exist to delete");
    }
    logger.debug("Response : " + response.toString());
    return response;
  }

  @Override
  public ObserveResponse getDashboardbyCategoryId(Observe node)
      throws JSONValidationSAWException, ReadEntitySAWException {
    ObserveResponse response = new ObserveResponse();
    try {
      Query query = new Query();
      query.setConjunction(Conjunction.AND);
      final List<Filter> filters = new ArrayList<>();
      Filter filterCategory = new Filter();
      filterCategory.setFieldPath("categoryId");
      filterCategory.setCondition(Condition.EQ);
      filterCategory.setValue(node.getCategoryId());
      filters.add(filterCategory);
      if (node.getCreatedBy() != null) {
        Filter filterUser = new Filter();
        filterUser.setFieldPath("createdBy");
        filterUser.setCondition(Condition.EQ);
        filterUser.setValue(node.getCreatedBy());
        filters.add(filterUser);
      }
      query.setFilter(filters);
      String searchQuery = ObserveUtils.node2JsonString(node, basePath, node.getEntityId(),
          Action.SEARCH, Category.USER_INTERFACE, query);
      logger.debug("Search Query getDashboardbyCategoryId :" + searchQuery);
      Request request = new Request(searchQuery);
      request.process();
      if (request.getSearchResultJsonArray() != null
          && request.getSearchResultJsonArray().size() > 0) {
        JsonElement resultArray = request.getSearchResultJsonArray();
        logger.debug("Entity has been retrieved successfully :" + resultArray.toString());
        Content content = new Content();
        List<Observe> observeList = new ArrayList<Observe>();
        ObjectMapper mapper = new ObjectMapper();
        if (resultArray.isJsonArray()) {
          for (int i = 0, j = 1; i < resultArray.getAsJsonArray().size(); i++, j++) {
            logger.debug("Inside resultArray.isJsonArray() ");
            logger.debug(
                " element.isJsonArray() :" + resultArray.getAsJsonArray().get(i).isJsonArray());
            logger.debug(" element.isJsonObject() :" + resultArray.getAsJsonArray().get(i)
                .getAsJsonObject().getAsJsonObject(String.valueOf(j)));
            String jsonString = resultArray.getAsJsonArray().get(i).getAsJsonObject()
                .getAsJsonObject(String.valueOf(j)).toString();
            Observe observeData = mapper.readValue(jsonString, Observe.class);
            observeList.add(observeData);
          }
        }
        content.setObserve(observeList);
        response.setContents(content);
        response.setMessage("Entity has been retrieved successfully");
      } else {
        response.setMessage("There is no data avaiable for the category Id & user Id"
            + node.getCategoryId() + ":" + node.getCreatedBy());
      }
    } catch (Exception ex) {
      logger.error("While retrieving it has been found that Entity does not exist.", ex);
      throw new ReadEntitySAWException(
          "While retrieving it has been found that Entity does not exist.");
    }
    logger.debug("Response : " + response.toString());
    return response;
  }


  @Override
  public String generateId() throws JSONValidationSAWException {
    String id = UUID.randomUUID().toString() + delimiter + PortalDataSet + delimiter
        + System.currentTimeMillis();

    return id;
  }
}
