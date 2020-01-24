package com.synchronoss.saw.observe.service;

import static com.synchronoss.sip.utils.SipCommonUtils.checkForPrivateCategory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.analysis.service.AnalysisService;
import com.synchronoss.saw.exceptions.SipCreateEntityException;
import com.synchronoss.saw.exceptions.SipDeleteEntityException;
import com.synchronoss.saw.exceptions.SipJsonValidationException;
import com.synchronoss.saw.exceptions.SipReadEntityException;
import com.synchronoss.saw.exceptions.SipUpdateEntityException;
import com.synchronoss.saw.observe.ObserveUtils;
import com.synchronoss.saw.observe.model.Content;
import com.synchronoss.saw.observe.model.Observe;
import com.synchronoss.saw.observe.model.ObserveResponse;
import com.synchronoss.saw.observe.model.store.Filter;
import com.synchronoss.saw.observe.model.store.Filter.Condition;
import com.synchronoss.saw.observe.model.store.MetaDataStoreStructure.Action;
import com.synchronoss.saw.observe.model.store.MetaDataStoreStructure.Category;
import com.synchronoss.saw.observe.model.store.Query;
import com.synchronoss.saw.observe.model.store.Query.Conjunction;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sncr.bda.cli.Request;
import sncr.bda.metastore.PortalDataSetStore;

@Service
public class ObserveServiceImpl implements ObserveService {

  private static final Logger logger = LoggerFactory.getLogger(ObserveServiceImpl.class);

  @Value("${metastore.base}")
  private String basePath;

  @Autowired
  AnalysisService analysisService;

  /*
   * 24-hr date time format. E.g.: 2018-05-25 13:08:25
   */
  private DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @Override
  public ObserveResponse addDashboard(Observe node)
      throws SipJsonValidationException, SipCreateEntityException {
    ObserveResponse response = new ObserveResponse();
    node.setCreatedAt(format.format(new Date()));
    try {
      logger.trace(
          "Before invoking request to MaprDB JSON store :{}",
          ObserveUtils.node2JsonString(
              node, basePath, node.getEntityId(), Action.CREATE, Category.USER_INTERFACE));
      Request request =
          new Request(
              ObserveUtils.node2JsonString(
                  node, basePath, node.getEntityId(), Action.CREATE, Category.USER_INTERFACE));
      request.process();
      response.setId(node.getEntityId());
      response = ObserveUtils.prepareResponse(node, "Entity is created successfully");
    } catch (Exception ex) {
      logger.error("Problem on the storage while creating an entity", ex);
      throw new SipCreateEntityException("Problem on the storage while creating an entity.");
    }
    logger.debug(Response, response.toString());
    return response;
  }

  @Override
  public ObserveResponse getDashboardbyCriteria(Observe node)
      throws SipJsonValidationException, SipReadEntityException {
    ObserveResponse response = new ObserveResponse();
    try {
      response.setId(node.getEntityId());

      // Get the data for the given dataset id using PortalDatasetStore
      PortalDataSetStore store = new PortalDataSetStore(basePath);
      String id = node.getEntityId();

      String jsonStringFromStore = store.read(id).toString();
      response.setMessage(SUCCESS);
      ObjectMapper mapper = new ObjectMapper();
      Observe observeData = mapper.readValue(jsonStringFromStore, Observe.class);
      response =
          ObserveUtils.prepareResponse(observeData, SUCCESS);
    } catch (Exception ex) {
      logger.error("While retrieving it has been found that Entity does not exist. {}", ex);
      throw new SipReadEntityException(
          "While retrieving it has been found that Entity does not exist.");
    }
    logger.debug(Response, response.toString());
    return response;
  }

  @Override
  public ObserveResponse updateDashboard(Observe node)
      throws SipJsonValidationException, SipUpdateEntityException {
    ObserveResponse response = new ObserveResponse();
    node.setUpdatedAt(format.format(new Date()));
    try {
      Request request =
          new Request(
              ObserveUtils.node2JsonString(
                  node, basePath, node.getEntityId(), Action.UPDATE, Category.USER_INTERFACE));
      request.process();
      response.setId(node.getEntityId());
      response = ObserveUtils.prepareResponse(node, "Entity is updated successfully");
    } catch (Exception ex) {
      logger.error("Entity does not exist to update.", ex);
      throw new SipUpdateEntityException("Entity does not exist to update.");
    }
    logger.debug(Response, response.toString());
    return response;
  }

  @Override
  public ObserveResponse deleteDashboard(Observe node)
      throws SipJsonValidationException, SipDeleteEntityException {
    ObserveResponse response = new ObserveResponse();
    node.setUpdatedAt(format.format(new Date()));
    try {
      response.setId(node.getEntityId());
      Request request =
          new Request(
              ObserveUtils.node2JsonString(
                  node, basePath, node.getEntityId(), Action.DELETE, Category.USER_INTERFACE));
      request.process();
      response = ObserveUtils.prepareResponse(node, "Entity is deleted successfully.");
    } catch (Exception ex) {
      logger.error("Entity does not exist to delete.", ex.getCause());
      throw new SipDeleteEntityException("Entity does not exist to delete");
    }
    logger.debug(Response, response.toString());
    return response;
  }

  @Override
  public ObserveResponse getDashboardbyCategoryId(Observe node)
      throws SipJsonValidationException, SipReadEntityException {
    ObserveResponse response = new ObserveResponse();
    try {
      Query query = new Query();
      query.setConjunction(Conjunction.AND);
      Filter filterCategory = new Filter();
      filterCategory.setFieldPath("categoryId");
      filterCategory.setCondition(Condition.EQ);
      filterCategory.setValue(node.getCategoryId());
      List<Filter> filters = new ArrayList<>();
      filters.add(filterCategory);
      if (node.getCreatedBy() != null) {
        Filter filterUser = new Filter();
        filterUser.setFieldPath("createdBy");
        filterUser.setCondition(Condition.EQ);
        filterUser.setValue(node.getCreatedBy());
        filters.add(filterUser);
      }
      query.setFilter(filters);
      String searchQuery =
          ObserveUtils.node2JsonString(
              node, basePath, node.getEntityId(), Action.SEARCH, Category.USER_INTERFACE, query);
      logger.debug("Search Query getDashboardbyCategoryId : {}", searchQuery);
      Request request = new Request(searchQuery);
      JsonObject searchResult = request.search();

      logger.debug("Search Result  {}", searchResult);

      if (searchResult != null && searchResult.has("result")) {
        JsonElement resultArray = searchResult.get("result");
        logger.debug("Entity has been retrieved successfully : {}", resultArray);
        Content content = new Content();
        List<Observe> observeList = new ArrayList<Observe>();
        ObjectMapper mapper = new ObjectMapper();
        if (resultArray.isJsonArray()) {
          for (int i = 0, j = 1; i < resultArray.getAsJsonArray().size(); i++, j++) {
            logger.debug("Inside resultArray.isJsonArray() ");
            logger.trace(
                " element.isJsonArray() : {}", resultArray.getAsJsonArray().get(i).isJsonArray());
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
            Observe observeData = mapper.readValue(jsonString, Observe.class);
            observeList.add(observeData);
          }
        }
        content.setObserve(observeList);
        response.setContents(content);
        response.setMessage(SUCCESS);
      } else {
        response.setMessage(
            "There is no data avaiable for the category Id & user Id"
                + node.getCategoryId()
                + ":"
                + node.getCreatedBy());
      }
    } catch (Exception ex) {
      logger.error("While retrieving it has been found that Entity does not exist. {}", ex);
      throw new SipReadEntityException(
          "While retrieving it has been found that Entity does not exist.");
    }
    logger.debug(Response, response.toString());
    return response;
  }

  @Override
  public boolean haveValidAnalysis(List<Object> observeTiles, Ticket ticket) {
    ObjectMapper mapper = new ObjectMapper();
    List<String> analysisId = new ArrayList<>();
    if (observeTiles != null && !observeTiles.isEmpty()) {
      observeTiles.stream().forEach(tile -> {
        JsonNode node = mapper.convertValue(tile, JsonNode.class);
        if (node.has("id")) {
          analysisId.add(node.get("id").asText());
        }
      });

      // validate the given analysis has valid user
      if (!analysisId.isEmpty()) {
        try {
          for (String id : analysisId) {
            Analysis analysis = analysisService.getAnalysis(id, ticket);
            Long privateCatForTicket = checkForPrivateCategory(ticket);
            privateCatForTicket = privateCatForTicket == null ? 0L : privateCatForTicket;
            logger.trace("Print the analysis {}", analysis);
            if (!(ticket.getCustCode() != null && analysis != null
                && ticket.getCustCode().equalsIgnoreCase(analysis.getCustomerCode()))) {
              return false;
            }
            Long userId = analysis != null ? analysis.getUserId() : 0L;
            if (privateCatForTicket == Long.valueOf(analysis.getCategory())
                && !(ticket.getUserId().equals(userId))) {
              return false;
            }
          }
        } catch (Exception ex) {
          logger.error("Error while checking the analysis for dashboard:  {}", ex.getMessage());
        }
      }
    }
    return true;
  }

  @Override
  public String generateId() throws SipJsonValidationException {
    String id =
        UUID.randomUUID().toString()
            + delimiter
            + PortalDataSet
            + delimiter
            + System.currentTimeMillis();

    return id;
  }
}
