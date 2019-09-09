package com.synchronoss.saw.alert.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.saw.alert.modal.AlertCount;
import com.synchronoss.saw.alert.modal.AlertCount.GroupBy;
import com.synchronoss.saw.alert.modal.AlertCountResponse;
import com.synchronoss.saw.alert.modal.AlertResult;
import com.synchronoss.saw.alert.modal.AlertRuleDetails;
import com.synchronoss.saw.alert.modal.AlertRuleResponse;
import com.synchronoss.saw.alert.modal.AlertSeverity;
import com.synchronoss.saw.alert.modal.AlertStatesResponse;
import com.synchronoss.saw.alert.util.AlertUtils;
import com.synchronoss.saw.model.Aggregate;
import com.synchronoss.saw.model.Model.Operator;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sncr.bda.base.MaprConnection;

@Service
public class AlertServiceImpl implements AlertService {
  private static final Logger logger = LoggerFactory.getLogger(AlertServiceImpl.class);

  private static final String ID = "id";
  private static final String NAME = "name";

  @Value("${metastore.base}")
  @NotNull
  private String basePath;

  @Value("${metastore.alertRulesTable}")
  @NotNull
  private String alertRulesMetadata;

  @Value("${metastore.alertResults}")
  @NotNull
  private String alertTriggerLog;

  /**
   * Create Alert rule.
   *
   * @param alert Alert
   * @return Alert
   */
  @Override
  public AlertRuleDetails createAlertRule(
      @NotNull(message = "Alert definition cannot be null") @Valid AlertRuleDetails alert,
      Ticket ticket) {
    logger.info("Inside create alert rule");
    MaprConnection connection = new MaprConnection(basePath, alertRulesMetadata);
    String id = UUID.randomUUID().toString();
    alert.setAlertRulesSysId(id);
    alert.setCustomerCode(ticket.getCustCode());
    Long createdTime = System.currentTimeMillis();
    alert.setCreatedTime(createdTime);
    connection.insert(id, alert);
    return alert;
  }

  /**
   * Update Alert Rule.
   *
   * @param alertRuleDetails AlertRuleDetails
   * @return Alert
   */
  @Override
  public AlertRuleDetails updateAlertRule(
      @NotNull(message = "Alert definition cannot be null") @Valid
          AlertRuleDetails alertRuleDetails,
      String alertRuleId,
      Ticket ticket) {
    MaprConnection connection = new MaprConnection(basePath, alertRulesMetadata);
    Long modifiedTime = System.currentTimeMillis();
    alertRuleDetails.setModifiedTime(modifiedTime);
    connection.update(alertRuleId, alertRuleDetails);
    return alertRuleDetails;
  }

  /**
   * Fetch all available alerts for the customer.
   *
   * @param ticket Ticket Id
   * @return AlertRulesDetails
   */
  @Override
  public AlertRuleResponse retrieveAllAlerts(Integer pageNumber, Integer pageSize, Ticket ticket) {
    MaprConnection connection = new MaprConnection(basePath, alertRulesMetadata);
    ObjectMapper objectMapper = new ObjectMapper();
    ObjectNode node = objectMapper.createObjectNode();
    ObjectNode objectNode = node.putObject(MaprConnection.EQ);
    objectNode.put("customerCode", ticket.getCustCode());
    List<JsonNode> alertLists =
        connection.runMaprDbQueryWithFilter(node.toString(), pageNumber, pageSize, "createdTime");
    List<AlertRuleDetails> alertRuleList = AlertUtils.convertJsonListToAlertRuleList(alertLists);
    Long noOfRecords = connection.getCountForQueryWithFilter(node.toString());
    AlertRuleResponse alertRuleResponse = new AlertRuleResponse();
    alertRuleResponse.setAlertRuleDetailsList(alertRuleList);
    alertRuleResponse.setNumberOfRecords(noOfRecords);
    return alertRuleResponse;
  }

  /**
   * Delete Alert Rule.
   *
   * @param alertRuleId Alert rule Id
   */
  @Override
  public Boolean deleteAlertRule(
      @NotNull(message = "Alert Id cannot be null") @NotNull String alertRuleId, Ticket ticket) {
    MaprConnection connection = new MaprConnection(basePath, alertRulesMetadata);
    return connection.deleteById(alertRuleId);
  }

  /**
   * Get Alert Rule.
   *
   * @param alertRuleId Alert rule Id
   * @return
   */
  @Override
  public AlertRuleDetails getAlertRule(
      @NotNull(message = "alertRuleID cannot be null") @NotNull String alertRuleId, Ticket ticket) {
    MaprConnection connection = new MaprConnection(basePath, alertRulesMetadata);
    JsonNode document = connection.findById(alertRuleId);
    ObjectMapper objectMapper = new ObjectMapper();
    AlertRuleDetails alertRule = null;
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    try {
      alertRule = objectMapper.treeToValue(document, AlertRuleDetails.class);
    } catch (JsonProcessingException e) {
      logger.error("error occured while converting json to alertRuledetails  ");
      e.printStackTrace();
    }
    return alertRule;
  }

  /**
   * Get Alert Rules By Category.
   *
   * @param categoryId Category Id
   * @return
   */
  @Override
  public AlertRuleResponse getAlertRulesByCategory(
      @NotNull(message = "categoryId cannot be null") @NotNull String categoryId,
      Integer pageNumber,
      Integer pageSize,
      Ticket ticket) {
    logger.info("Inside get alert rule by category");
    MaprConnection connection = new MaprConnection(basePath, alertRulesMetadata);
    ObjectMapper objectMapper = new ObjectMapper();
    ObjectNode node = objectMapper.createObjectNode();
    ArrayNode arrayNode = node.putArray(MaprConnection.AND);
    ObjectNode node1 = objectMapper.createObjectNode();
    ObjectNode objectNode = node1.putObject(MaprConnection.EQ);
    objectNode.put("categoryId", categoryId);
    ObjectNode node2 = objectMapper.createObjectNode();
    ObjectNode objectNode1 = node2.putObject(MaprConnection.EQ);
    objectNode1.put("customerCode", ticket.getCustCode());
    arrayNode.add(node1);
    arrayNode.add(node2);
    logger.debug("Mapr Filter query for alert rule by category:{}", node.toString());
    List<JsonNode> alertLists =
        connection.runMaprDbQueryWithFilter(node.toString(), pageNumber, pageSize, "createdTime");
    List<AlertRuleDetails> alertList = AlertUtils.convertJsonListToAlertRuleList(alertLists);
    Long noOfRecords = connection.getCountForQueryWithFilter(node.toString());
    AlertRuleResponse alertRuleResponse = new AlertRuleResponse();
    alertRuleResponse.setNumberOfRecords(noOfRecords);
    alertRuleResponse.setAlertRuleDetailsList(alertList);
    return alertRuleResponse;
  }

  /**
   * Retrieve operator details.
   *
   * @param ticket ticket Id
   * @return String if matched
   */
  @Override
  public String retrieveOperatorsDetails(Ticket ticket) {
    JsonArray elements = new JsonArray();
    List<Operator> operatorList = Arrays.asList(Operator.values());
    for (Operator operator : operatorList) {
      JsonObject object = new JsonObject();
      String readableOperator = getReadableOperator(operator);
      if (readableOperator != null) {
        object.addProperty(ID, operator.value());
        object.addProperty(NAME, readableOperator);
        elements.add(object);
      }
    }
    return elements.toString();
  }

  @Override
  public String retrieveAggregations(Ticket ticket) {
    JsonArray elements = new JsonArray();
    List<Aggregate> aggregationList = Arrays.asList(Aggregate.values());
    for (Aggregate aggregation : aggregationList) {
      JsonObject object = new JsonObject();
      String readableOperator = getReadableAggregation(aggregation);
      if (readableOperator != null) {
        object.addProperty(ID, aggregation.value());
        object.addProperty(NAME, readableOperator);
        elements.add(object);
      }
    }
    return elements.toString();
  }

  /**
   * Get alert state by alert ID.
   *
   * @param alertRuleId alertRuleId
   * @param ticket Ticket
   * @return List of AlertStates
   */
  @Override
  public AlertStatesResponse getAlertStates(
      @NotNull(message = "alertRuleId cannot be null") String alertRuleId,
      Integer pageNumber,
      Integer pageSize,
      Ticket ticket) {
    AlertStatesResponse alertStatesResponse = new AlertStatesResponse();
    ObjectMapper objectMapper = new ObjectMapper();
    ObjectNode node = objectMapper.createObjectNode();
    ArrayNode arrayNode = node.putArray(MaprConnection.AND);
    ObjectNode node1 = objectMapper.createObjectNode();
    ObjectNode objectNode = node1.putObject(MaprConnection.EQ);
    objectNode.put("customerCode", ticket.getCustCode());
    ObjectNode node2 = objectMapper.createObjectNode();
    ObjectNode objectNode1 = node2.putObject(MaprConnection.EQ);
    objectNode1.put("alertRulesSysId", alertRuleId);
    arrayNode.add(node1);
    arrayNode.add(node2);
    logger.info("query :::" + node.toString());
    MaprConnection connection = new MaprConnection(basePath, alertTriggerLog);
    List<JsonNode> alertLists =
        connection.runMaprDbQueryWithFilter(node.toString(), pageNumber, pageSize, "createdTime");
    List<AlertResult> alertTriggerList = convertJsonListToAlertTriggerList(alertLists);
    Long noOfRecords = connection.getCountForQueryWithFilter(node.toString());
    alertStatesResponse.setAlertStatesList(alertTriggerList);
    alertStatesResponse.setMessage("Success");
    alertStatesResponse.setNumberOfRecords(noOfRecords);
    return alertStatesResponse;
  }

  /**
   * Get list of pageable Alerts by time order.
   *
   * @param ticket Ticket
   * @return List of AlertStates, number of records.
   */
  @Override
  public AlertStatesResponse listAlertStates(Integer pageNumber, Integer pageSize, Ticket ticket) {
    MaprConnection connection = new MaprConnection(basePath, alertTriggerLog);
    ObjectMapper objectMapper = new ObjectMapper();
    ObjectNode node = objectMapper.createObjectNode();
    ObjectNode objectNode = node.putObject(MaprConnection.EQ);
    objectNode.put("customerCode", ticket.getCustCode());
    List<JsonNode> alertLists =
        connection.runMaprDbQueryWithFilter(node.toString(), pageNumber, pageSize, "createdTime");
    List<AlertResult> alertTriggerList = convertJsonListToAlertTriggerList(alertLists);
    Long noOfRecords = connection.getCountForQueryWithFilter(node.toString());
    AlertStatesResponse alertStatesResponse = new AlertStatesResponse();
    alertStatesResponse.setAlertStatesList(alertTriggerList);
    alertStatesResponse.setMessage("Success");
    alertStatesResponse.setNumberOfRecords(noOfRecords);
    return alertStatesResponse;
  }

  /**
   * It return readable operator name.
   *
   * @param operator Operator
   * @return String
   */
  private String getReadableOperator(Operator operator) {

    switch (operator) {
      case EQ:
        return "Equal To";
      case GT:
        return "Greater Than";
      case LT:
        return "Less Than";
      case GTE:
        return "Greater Than and Equal To";
      case LTE:
        return "Less Than and Equal To";
      case NEQ:
        return "Not Equal To";
        /**
         * case BTW: return "Between"; case SW: return "Start With"; case EW: return "End With";
         * case CONTAINS: return "Contains"; case ISIN: return "Is IN";
         */
      default:
        return null;
    }
  }

  /**
   * It return readable aggregation name.
   *
   * @param aggregation Aggregation
   * @return String
   */
  private String getReadableAggregation(Aggregate aggregation) {

    switch (aggregation) {
      case AVG:
        return "Average";
      case SUM:
        return "SUM";
      case MIN:
        return "Minimum";
      case MAX:
        return "Maximum";
      case COUNT:
        return "Count Values";
      default:
        return null;
    }
  }

  /**
   * It returns alert count for each day based on the preset value.
   *
   * @param alertCount AlertCount
   * @return AlertCountResponse
   */
  @Override
  public List<AlertCountResponse> alertCount(
      AlertCount alertCount,
      Integer pageNumber,
      Integer pageSize,
      String alertRuleSysId,
      Ticket ticket) {
    if (alertCount.getGroupBy() == null) {
      throw new IllegalArgumentException("GroupBy cannot be null");
    }
    Map<String, Long> epochTimeMap =
        AlertUtils.getEpochTimeForPreset(
            alertCount.getPreset(), alertCount.getStartTime(), alertCount.getEndTime());
    Long epochGte = epochTimeMap.get("startTime");
    Long epochLte = epochTimeMap.get("endTime");
    MaprConnection connection = new MaprConnection(basePath, alertTriggerLog);
    String query;
    if (alertCount.getGroupBy() == GroupBy.SEVERITY) {
      if (alertRuleSysId != null && !StringUtils.isEmpty(alertRuleSysId)) {
        query = getQueryForAlertCountByAlertRuleId(epochGte, epochLte, ticket, alertRuleSysId);
        List<JsonNode> result =
            connection.runMaprDbQueryWithFilter(query, pageNumber, pageSize, "createdTime");
        List<AlertResult> list = convertJsonListToAlertTriggerList(result);
        return groupByseverity(list);
      }
      query = getQueryForAlertCount(epochGte, epochLte, ticket);
      List<JsonNode> result =
          connection.runMaprDbQueryWithFilter(query, pageNumber, pageSize, "createdTime");
      List<AlertResult> list = convertJsonListToAlertTriggerList(result);
      return groupByseverity(list);

    } else {
      if (alertRuleSysId != null && !StringUtils.isEmpty(alertRuleSysId)) {
        query = getQueryForAlertCountByAlertRuleId(epochGte, epochLte, ticket, alertRuleSysId);
        List<JsonNode> result =
            connection.runMaprDbQueryWithFilter(query, pageNumber, pageSize, "createdTime");
        List<AlertResult> list = convertJsonListToAlertTriggerList(result);
        return groupByDate(list);
      }
      query = getQueryForAlertCount(epochGte, epochLte, ticket);
      List<JsonNode> result =
          connection.runMaprDbQueryWithFilter(query, pageNumber, pageSize, "createdTime");
      List<AlertResult> list = convertJsonListToAlertTriggerList(result);
      return groupByDate(list);
    }
  }

  private List<AlertCountResponse> groupByseverity(List<AlertResult> list) {
    List<AlertCountResponse> response = new ArrayList<AlertCountResponse>();
    Map<AlertSeverity, Long> groupByServrity =
        list.stream()
            .collect(Collectors.groupingBy(AlertResult::getAlertSeverity, Collectors.counting()));
    groupByServrity.forEach(
        (severity, count) -> {
          AlertCountResponse countResponse = new AlertCountResponse(null, count, severity);
          response.add(countResponse);
        });
    return response;
  }

  private List<AlertCountResponse> groupByDate(List<AlertResult> list) {
    List<AlertCountResponse> response = new ArrayList<AlertCountResponse>();
    Map<String, Long> groupByServrity =
        list.stream()
            .collect(
                Collectors.groupingBy(
                    alertTriggerLog -> {
                      Long startTime = alertTriggerLog.getStartTime();
                      Date date = new Date(startTime);
                      DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                      return df.format(date);
                    },
                    Collectors.counting()));
    groupByServrity.forEach(
        (date, count) -> {
          AlertCountResponse countResponse = new AlertCountResponse(date, count, null);
          response.add(countResponse);
        });
    return response;
  }

  private String getQueryForAlertCountByAlertRuleId(
      Long epochGte, Long epochLte, Ticket ticket, String alertRuleSysId) {
    ObjectMapper objectMapper = new ObjectMapper();
    ObjectNode node = objectMapper.createObjectNode();
    ObjectNode node1 = objectMapper.createObjectNode();
    ObjectNode objectNode = node1.putObject(MaprConnection.EQ);
    objectNode.put("customerCode", ticket.getCustCode());
    ObjectNode node2 = objectMapper.createObjectNode();
    ObjectNode objectNode1 = node2.putObject(MaprConnection.GTE);
    objectNode1.put("startTime", epochGte);
    ObjectNode node3 = objectMapper.createObjectNode();
    ObjectNode objectNode2 = node3.putObject(MaprConnection.LTE);
    objectNode2.put("startTime", epochLte);
    ObjectNode node4 = objectMapper.createObjectNode();
    ObjectNode objectNode3 = node4.putObject(MaprConnection.EQ);
    objectNode3.put("alertRulesSysId", alertRuleSysId);
    ArrayNode arrayNode = node.putArray(MaprConnection.AND);
    arrayNode.add(node1);
    arrayNode.add(node2);
    arrayNode.add(node3);
    arrayNode.add(node4);
    return node.toString();
  }

  private String getQueryForAlertCount(Long epochGte, Long epochLte, Ticket ticket) {
    ObjectMapper objectMapper = new ObjectMapper();
    ObjectNode node = objectMapper.createObjectNode();
    ObjectNode node1 = objectMapper.createObjectNode();
    ObjectNode objectNode = node1.putObject(MaprConnection.EQ);
    objectNode.put("customerCode", ticket.getCustCode());
    ObjectNode node2 = objectMapper.createObjectNode();
    ObjectNode objectNode1 = node2.putObject(MaprConnection.GTE);
    objectNode1.put("startTime", epochGte);
    ObjectNode node3 = objectMapper.createObjectNode();
    ObjectNode objectNode2 = node3.putObject(MaprConnection.LTE);
    objectNode2.put("startTime", epochLte);
    ArrayNode arrayNode = node.putArray(MaprConnection.AND);
    arrayNode.add(node1);
    arrayNode.add(node2);
    arrayNode.add(node3);
    return node.toString();
  }

  private List<AlertResult> convertJsonListToAlertTriggerList(List<JsonNode> alertLists) {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode jsonNode = mapper.createArrayNode().addAll(alertLists);
    ObjectReader reader =
        mapper
            .readerFor(new TypeReference<List<AlertResult>>() {})
            .without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    List<AlertResult> alertTriggerList = null;
    try {
      reader.without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
      alertTriggerList = reader.readValue(jsonNode);
      return alertTriggerList;
    } catch (IOException e) {
      logger.error("exeception e" + e);
      throw new RuntimeException("Exeception occured while parsing the results");
    }
  }
}
