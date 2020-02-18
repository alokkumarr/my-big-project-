package com.synchronoss.sip.alert.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.sip.alert.exceptions.SipAlertRunTimeExceptions;
import com.synchronoss.sip.alert.modal.AlertCount;
import com.synchronoss.sip.alert.modal.AlertCount.GroupBy;
import com.synchronoss.sip.alert.modal.AlertCountResponse;
import com.synchronoss.sip.alert.modal.AlertFilter;
import com.synchronoss.sip.alert.modal.AlertResult;
import com.synchronoss.sip.alert.modal.AlertRuleDetails;
import com.synchronoss.sip.alert.modal.AlertRuleResponse;
import com.synchronoss.sip.alert.modal.AlertSeverity;
import com.synchronoss.sip.alert.modal.AlertStatesFilter;
import com.synchronoss.sip.alert.modal.AlertStatesResponse;
import com.synchronoss.sip.alert.modal.MonitoringType;
import com.synchronoss.sip.alert.service.evaluator.EvaluatorListener;
import com.synchronoss.saw.model.Aggregate;
import com.synchronoss.saw.model.Field.Type;
import com.synchronoss.saw.model.Model.Operator;
import com.synchronoss.saw.model.Model.Preset;
import com.synchronoss.saw.util.BuilderUtil;
import com.synchronoss.saw.util.DynamicConvertor;

import com.synchronoss.sip.alert.util.AlertUtils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.ojai.exceptions.OjaiException;
import org.ojai.store.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sncr.bda.base.MaprConnection;
import sncr.bda.store.generic.schema.Sort;

@Service
public class AlertServiceImpl implements AlertService {
  private static final Logger LOGGER = LoggerFactory.getLogger(AlertServiceImpl.class);

  private static final String ID = "id";
  private static final String NAME = "name";
  private static final String CREATED_TIME = "createdTime";
  private static final String DATE_FORMAT = "dd-MM-yyyy";
  private static final String CUSTOMER_CODE = "customerCode";

  @Value("${sip.service.metastore.base}")
  @NotNull
  private String basePath;

  @Value("${sip.service.metastore.alertRulesTable}")
  @NotNull
  private String alertRulesMetadata;

  @Value("${sip.service.metastore.alertResults}")
  @NotNull
  private String alertTriggerLog;

  @Autowired
  EvaluatorListener evaluatorListener;

  @PostConstruct
  public void init() {
    try {
      MaprConnection alertRuleTableConnection = new MaprConnection(basePath, alertRulesMetadata);
      MaprConnection alertResultTableConnection = new MaprConnection(basePath, alertTriggerLog);
    } catch (OjaiException e) {
      LOGGER.error("Error occurred while setup tables {}", e);
    }
  }

  /**
   * Create Alert rule.
   *
   * @param alert  Alert
   * @param ticket Ticket
   * @return AlertRuleDetails
   */
  @Override
  public AlertRuleDetails createAlertRule(
      @NotNull(message = "Alert definition cannot be null") @Valid AlertRuleDetails alert,
      Ticket ticket) {
    LOGGER.info("Inside create alert rule");
    MaprConnection connection = new MaprConnection(basePath, alertRulesMetadata);
    String id = UUID.randomUUID().toString();
    alert.setAlertRulesSysId(id);
    Long createdTime = System.currentTimeMillis();
    alert.setCreatedTime(createdTime);
    alert.setCreatedBy(ticket.getUserFullName());
    alert.setCustomerCode(ticket.getCustCode());
    connection.insert(id, alert);
    return alert;
  }

  /**
   * Update Alert Rule.
   *
   * @param alertRuleDetails AlertRuleDetails
   * @param alertRuleId      alertRuleId
   * @param ticket           Ticket
   * @return AlertRuleDetails
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
    alertRuleDetails.setUpdatedBy(ticket.getUserFullName());
    alertRuleDetails.setCustomerCode(ticket.getCustCode());
    alertRuleDetails.setAlertRulesSysId(alertRuleId);
    connection.update(alertRuleId, alertRuleDetails);
    return alertRuleDetails;
  }

  /**
   * Fetch all available alerts for the customer.
   *
   * @param ticket     Ticket Id
   * @param pageNumber pageNumber
   * @param pageSize   pageSize
   * @return AlertRuleResponse
   */
  @Override
  public AlertRuleResponse retrieveAllAlerts(Integer pageNumber, Integer pageSize, Ticket ticket) {
    MaprConnection connection = new MaprConnection(basePath, alertRulesMetadata);
    ObjectMapper objectMapper = new ObjectMapper();
    ObjectNode node = objectMapper.createObjectNode();
    ObjectNode objectNode = node.putObject(MaprConnection.EQ);
    objectNode.put(CUSTOMER_CODE, ticket.getCustCode());
    List<AlertRuleDetails> alertRuleList =
        connection.runMaprDbQueryWithFilter(
            node.toString(), pageNumber, pageSize, CREATED_TIME, AlertRuleDetails.class);
    Long noOfRecords = connection.runMapDbQueryForCount(node.toString());
    AlertRuleResponse alertRuleResponse = new AlertRuleResponse();
    alertRuleResponse.setAlertRuleDetailsList(alertRuleList);
    alertRuleResponse.setNumberOfRecords(noOfRecords);
    return alertRuleResponse;
  }

  /**
   * Delete Alert Rule.
   *
   * @param alertRuleId Alert rule Id
   * @param ticket      Ticket
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
   * @param ticket      Ticket
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
      LOGGER.error("Error occurred while converting json to alertRuledetails");
      throw new SipAlertRunTimeExceptions("Error occurred while retrieving alertdetails :" + e);
    }
    return alertRule;
  }

  /**
   * Get Alert Rules By Category.
   *
   * @param categoryId Category Id
   * @param pageNumber pageNumber
   * @param pageSize   pageSize
   * @param ticket     Ticket
   * @return
   */
  @Override
  public AlertRuleResponse getAlertRulesByCategory(
      @NotNull(message = "categoryId cannot be null") @NotNull String categoryId,
      Integer pageNumber,
      Integer pageSize,
      Ticket ticket) {
    LOGGER.info("Inside get alert rule by category");
    MaprConnection connection = new MaprConnection(basePath, alertRulesMetadata);
    ObjectMapper objectMapper = new ObjectMapper();
    ObjectNode node = objectMapper.createObjectNode();
    ArrayNode arrayNode = node.putArray(MaprConnection.AND);
    ObjectNode node1 = objectMapper.createObjectNode();
    ObjectNode objectNode = node1.putObject(MaprConnection.EQ);
    objectNode.put("categoryId", categoryId);
    ObjectNode node2 = objectMapper.createObjectNode();
    ObjectNode objectNode1 = node2.putObject(MaprConnection.EQ);
    objectNode1.put(CUSTOMER_CODE, ticket.getCustCode());
    arrayNode.add(node1);
    arrayNode.add(node2);
    LOGGER.debug("Mapr Filter query for alert rule by category:{}", node.toString());
    List<AlertRuleDetails> alertList =
        connection.runMaprDbQueryWithFilter(
            node.toString(), pageNumber, pageSize, CREATED_TIME, AlertRuleDetails.class);
    Long noOfRecords = connection.runMapDbQueryForCount(node.toString());
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

  /**
   * Retrieve Aggregation details.
   *
   * @param ticket ticket Id
   * @return String if matched
   */
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
   * @param alertRuleSysId alertRuleSysId
   * @param pageNumber     pageNumber
   * @param pageSize       pageSize
   * @param ticket         Ticket
   * @return List of AlertStates
   */
  @Override
  public AlertStatesResponse getAlertStates(
      @NotNull(message = "alertRuleId cannot be null") String alertRuleSysId,
      Integer pageNumber,
      Integer pageSize,
      Ticket ticket) {
    AlertStatesResponse alertStatesResponse = new AlertStatesResponse();
    MaprConnection connection = new MaprConnection(basePath, alertTriggerLog);
    LOGGER.info("Inside states:");

    List<AlertFilter> alertFilters = new ArrayList<>();
    AlertFilter customerFilter =
        new AlertFilter(CUSTOMER_CODE, ticket.getCustCode(), Type.STRING, Operator.EQ);
    AlertFilter filerByRuleId =
        new AlertFilter(AlertUtils.ALERT_RULE_SYS_ID, alertRuleSysId, Type.STRING, Operator.EQ);
    alertFilters.add(customerFilter);
    alertFilters.add(filerByRuleId);
    String query = getMaprQueryForFilter(alertFilters);
    List<AlertResult> alertResultLists =
        connection.runMaprDbQueryWithFilter(
            query, pageNumber, pageSize, AlertUtils.START_TIME, AlertResult.class);
    Long noOfRecords = connection.runMapDbQueryForCount(query);
    alertStatesResponse.setAlertStatesList(alertResultLists);
    alertStatesResponse.setMessage("Success");
    alertStatesResponse.setNumberOfRecords(noOfRecords);
    return alertStatesResponse;
  }

  /**
   * Get list of pageable Alerts by time order and if specified filters by attributeValue.
   *
   * @param pageNumber pageNumber
   * @param pageSize   pageSize
   * @param ticket     Ticket
   * @param alertState AlertStatesFilter
   * @return List of AlertStates, number of records.
   */
  @Override
  public AlertStatesResponse listAlertStates(
      Integer pageNumber, Integer pageSize, Ticket ticket, Optional<AlertStatesFilter> alertState) {
    LOGGER.trace("Request body to list all alert states:{}", alertState);
    String query;
    List<AlertFilter> alertFilters;
    List<Sort> sorts = null;
    if (alertState != null && alertState.isPresent()) {
      AlertStatesFilter alertStatesFilter = alertState.get();
      alertFilters = alertStatesFilter.getFilters();
      sorts = alertStatesFilter.getSorts();
    } else {
      alertFilters = new ArrayList<>();
    }
    if (sorts == null || sorts.isEmpty()) {
      sorts = new ArrayList<>();
      Sort s = new Sort(AlertUtils.START_TIME, SortOrder.DESC);
      sorts.add(s);
    }
    AlertFilter customerFilter =
        new AlertFilter(CUSTOMER_CODE, ticket.getCustCode(), Type.STRING, Operator.EQ);
    alertFilters.add(customerFilter);
    query = getMaprQueryForFilter(alertFilters);
    LOGGER.trace("Mapr Query for the filter:{}", query);
    MaprConnection connection = new MaprConnection(basePath, alertTriggerLog);
    List<AlertResult> alertResultLists =
        connection.runMaprDbQuery(query, pageNumber, pageSize, sorts, AlertResult.class);
    Long noOfRecords = connection.runMapDbQueryForCount(query);
    AlertStatesResponse alertStatesResponse = new AlertStatesResponse();
    alertStatesResponse.setAlertStatesList(alertResultLists);
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
  @Override
  public String getReadableOperator(Operator operator) {

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
      case BTW:
        return "Between";
      /**
       * case SW: return "Start With"; case EW: return "End With"; case CONTAINS: return
       * "Contains"; case ISIN: return "Is IN";
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
   * It return readable operator name.
   *
   * @param monitoringType MonitoringType
   * @return String
   */
  private String getReadableMonitoringType(MonitoringType monitoringType) {

    switch (monitoringType) {
      case AGGREGATION_METRICS:
        return "Aggregation Metrics";
      case CONTINUOUS_MONITORING:
        return "Continuous Monitoring";
      /** case ROW_METRICS: return "Row Metrics"; */
      default:
        return null;
    }
  }

  /**
   * It returns alert count for each day based on the preset value.
   *
   * @param alertCount     AlertCount
   * @param pageNumber     pageNumber
   * @param pageSize       pageSize
   * @param alertRuleSysId alertRuleSysId
   * @param ticket         Ticket
   * @return AlertCountResponse
   */
  @Override
  public List<AlertCountResponse> alertCount(
      AlertCount alertCount,
      Integer pageNumber,
      Integer pageSize,
      String alertRuleSysId,
      Ticket ticket) {
    LOGGER.info("Inside Alert Count for group by :" + alertCount.getGroupBy());
    GroupBy groupBy = alertCount.getGroupBy();
    List<AlertFilter> alertFilters = alertCount.getFilters();
    Preconditions.checkArgument(groupBy != null, "Group By field cannot be null");
    Preconditions.checkArgument(alertFilters != null, "Date Filter is missing");
    AlertFilter customerFilter =
        new AlertFilter(CUSTOMER_CODE, ticket.getCustCode(), Type.STRING, Operator.EQ);
    alertFilters.add(customerFilter);
    if (alertRuleSysId != null) {
      AlertFilter filerByRuleId =
          new AlertFilter(AlertUtils.ALERT_RULE_SYS_ID, alertRuleSysId, Type.STRING, Operator.EQ);
      alertFilters.add(filerByRuleId);
    }

    String query = getMaprQueryForFilter(alertFilters);
    MaprConnection connection = new MaprConnection(basePath, alertTriggerLog);
    LOGGER.trace("Mapr Filter query for alert count:{}", query);
    List<AlertResult> result =
        connection.runMaprDbQueryWithFilter(
            query, pageNumber, pageSize, AlertUtils.START_TIME, AlertResult.class);
    switch (alertCount.getGroupBy()) {
      case SEVERITY:
        return groupByseverity(result);
      case ATTRIBUTEVALUE:
        return groupByAttributeValue(result);
      case DATE:
        return groupByDate(result);
      default:
        throw new SipAlertRunTimeExceptions("Unsupported group by field");
    }
  }

  @Override
  public Set<String> listAttribueValues(Ticket ticket) {
    MaprConnection connection = new MaprConnection(basePath, alertRulesMetadata);
    List<AlertFilter> alertFilters = new ArrayList<>();
    AlertFilter customerFilter =
        new AlertFilter(CUSTOMER_CODE, ticket.getCustCode(), Type.STRING, Operator.EQ);
    alertFilters.add(customerFilter);
    String query = getMaprQueryForFilter(alertFilters);

    return connection.runMaprQueryForDistinctValues("attributeValue", query);
  }

  @Override
  public String retrieveMonitoringType(Ticket ticket) {
    JsonArray elements = new JsonArray();
    List<MonitoringType> montoringTypeList = Arrays.asList(MonitoringType.values());
    for (MonitoringType monitoringType : montoringTypeList) {
      JsonObject object = new JsonObject();
      String readableOperator = getReadableMonitoringType(monitoringType);
      if (readableOperator != null) {
        object.addProperty(ID, monitoringType.value());
        object.addProperty(NAME, readableOperator);
        elements.add(object);
      }
    }
    return elements.toString();
  }

  private List<AlertCountResponse> groupByseverity(List<AlertResult> list) {
    List<AlertCountResponse> response = new ArrayList<AlertCountResponse>();
    Map<AlertSeverity, Long> groupByServrity =
        list.stream()
            .collect(Collectors.groupingBy(AlertResult::getAlertSeverity, Collectors.counting()));
    groupByServrity.forEach(
        (severity, count) -> {
          AlertCountResponse countResponse = new AlertCountResponse(null, count, severity, null);
          response.add(countResponse);
        });
    return response;
  }

  private List<AlertCountResponse> groupByAttributeValue(List<AlertResult> list) {
    List<AlertCountResponse> response = new ArrayList<AlertCountResponse>();
    Map<String, Long> groupByAttribute =
        list.stream()
            .filter((alertResult -> alertResult.getAttributeValue() != null))
            .collect(Collectors.groupingBy(AlertResult::getAttributeValue, Collectors.counting()));
    groupByAttribute.forEach(
        (attributeValue, count) -> {
          AlertCountResponse countResponse =
              new AlertCountResponse(null, count, null, attributeValue);
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
                    result -> {
                      Long startTime = result.getStartTime();
                      Date date = new Date(startTime);
                      DateFormat df = new SimpleDateFormat(DATE_FORMAT);
                      return df.format(date);
                    },
                    Collectors.counting()));
    groupByServrity.forEach(
        (date, count) -> {
          AlertCountResponse countResponse = new AlertCountResponse(date, count, null, null);
          response.add(countResponse);
        });
    return response;
  }

  private String getMaprQueryForFilter(List<AlertFilter> filters) {
    ObjectMapper objectMapper = new ObjectMapper();
    ObjectNode node = objectMapper.createObjectNode();
    ArrayNode arrayNode = node.putArray(MaprConnection.AND);
    for (AlertFilter filter : filters) {
      if (filter.getType() == null || filter.getType() == Type.STRING) {
        String value = String.valueOf(filter.getValue());
        ObjectNode node3 = objectMapper.createObjectNode();
        ObjectNode objectNode3 = node3.putObject(MaprConnection.EQ);
        objectNode3.put(filter.getFieldName(), value);
        arrayNode.add(node3);
      }
      if (filter.getType() == Type.DATE) {
        Long epochGte;
        Long epochLte;
        if (filter.getPreset() == Preset.NA) {
          epochGte = filter.getGte();
          epochLte = filter.getLte();
          Preconditions.checkArgument(
              epochGte != null, "From date is missing for custom date filter");
          Preconditions.checkArgument(
              epochLte != null, "To date is missing for custom date filter");
        } else {
          DynamicConvertor convertor = getDynamicConverter(filter);
          epochGte = getEpochFromDateTime(convertor.getGte());
          epochLte = getEpochFromDateTime(convertor.getLte());
        }
        ObjectNode innerNode = objectMapper.createObjectNode();
        ArrayNode betweenValues = innerNode.putArray(AlertUtils.START_TIME);
        betweenValues.add(epochGte);
        betweenValues.add(epochLte);
        ObjectNode outerNode = objectMapper.createObjectNode();
        outerNode.set(MaprConnection.BTW, innerNode);
        arrayNode.add(outerNode);
      }
    }

    return node.toString();
  }

  private DynamicConvertor getDynamicConverter(AlertFilter filter) {
    Preset preset = filter.getPreset();
    Preconditions.checkArgument(preset != null, "Preset is missing for the date filter");
    return BuilderUtil.dynamicDecipher(filter.getPreset().value());
  }

  /**
   * Return timestamp from the given date.
   *
   * @param date String
   * @return Long
   */
  private static Long getEpochFromDateTime(String date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime ldt = LocalDateTime.parse(date, formatter);
    ZoneId zoneId = ZoneId.systemDefault();
    return ldt.atZone(zoneId).toInstant().toEpochMilli();
  }
}
