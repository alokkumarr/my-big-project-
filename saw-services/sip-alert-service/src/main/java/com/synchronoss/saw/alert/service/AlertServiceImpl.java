package com.synchronoss.saw.alert.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.saw.alert.entities.AlertCustomerDetails;
import com.synchronoss.saw.alert.entities.AlertRulesDetails;
import com.synchronoss.saw.alert.entities.DatapodDetails;
import com.synchronoss.saw.alert.modal.Alert;
import com.synchronoss.saw.alert.modal.Operator;
import com.synchronoss.saw.alert.repository.AlertCustomerRepository;
import com.synchronoss.saw.alert.repository.AlertDatapodRepository;
import com.synchronoss.saw.alert.repository.AlertRulesRepository;
import com.synchronoss.saw.alert.repository.AlertTriggerLog;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlertServiceImpl implements AlertService {
  private static final String ID = "id";
  private static final String NAME = "name";
  private static final String OPERATORS = "operators";

  @Autowired AlertRulesRepository alertRulesRepository;

  @Autowired AlertDatapodRepository alertDatapodRepository;

  @Autowired AlertTriggerLog alertTriggerLog;

  @Autowired AlertCustomerRepository alertCustomerRepository;

  /**
   * Create Alert rule.
   *
   * @param alert Alert
   * @return Alert
   */
  @Override
  public Alert createAlertRule(
      @NotNull(message = "Alert definition cannot be null") @Valid Alert alert, Ticket ticket) {

    Long alertCustomerSysId = null;
    Optional<AlertCustomerDetails> alertCustomerDetails =
        alertCustomerRepository.findByCustomerCode(ticket.getCustCode());
    if (alertCustomerDetails == null || !alertCustomerDetails.isPresent()) {
      alertCustomerSysId =
          createCustomerDetails(ticket.getUserFullName(), alert.getProduct(), ticket.getCustCode());
    }
    Optional<DatapodDetails> datapodDetail = alertDatapodRepository.findById(alert.getDatapodId());

    if (datapodDetail == null || !datapodDetail.isPresent()) {
      createDatapodDetails(
          alert.getDatapodId(),
          alert.getDatapodName(),
          ticket.getUserFullName(),
          alertCustomerSysId);
    }
    AlertRulesDetails alertRulesDetails = new AlertRulesDetails();
    alertRulesDetails.setRuleName(alert.getRuleName());
    alertRulesDetails.setAggregation(alert.getAggregation());
    alertRulesDetails.setAlertSeverity(alert.getAlertSeverity());
    alertRulesDetails.setDatapodId(alert.getDatapodId());
    alertRulesDetails.setMonitoringEntity(alert.getMonitoringEntity());
    alertRulesDetails.setActiveInd(true);
    alertRulesDetails.setCategory(alert.getCategoryId());
    alertRulesDetails.setCreatedBy(ticket.getUserFullName());
    alertRulesDetails.setOperator(alert.getOperator());
    alertRulesDetails.setCreatedTime(new Date());
    alertRulesDetails.setThresholdValue(alert.getThresholdValue());
    alertRulesRepository.save(alertRulesDetails);
    return alert;
  }

  /**
   * Update Alert Rule.
   *
   * @param alert Alert
   * @return Alert
   */
  @Override
  public Alert updateAlertRule(
      @NotNull(message = "Alert definition cannot be null") @Valid Alert alert,
      Long alertRuleId,
      Ticket ticket) {
    Optional<AlertRulesDetails> alertRulesDetails = alertRulesRepository.findById(alertRuleId);
    if (alertRulesDetails.isPresent()) {
      alertRulesDetails.get().setRuleName(alert.getRuleName());
      alertRulesDetails.get().setAggregation(alert.getAggregation());
      alertRulesDetails.get().setAlertSeverity(alert.getAlertSeverity());
      alertRulesDetails.get().setDatapodId(alert.getDatapodId());
      alertRulesDetails.get().setMonitoringEntity(alert.getMonitoringEntity());
      alertRulesDetails.get().setActiveInd(true);
      alertRulesDetails.get().setOperator(alert.getOperator());
      alertRulesDetails.get().setModifiedTime(new Date());
      alertRulesDetails.get().setModifiedBy(ticket.getUserFullName());
      alertRulesDetails.get().setThresholdValue(alert.getThresholdValue());
      alertRulesRepository.save(alertRulesDetails.get());
    }
    return alert;
  }

  /**
   * Fetch all available alerts for the customer.
   *
   * @param ticket Ticket Id
   * @return AlertRulesDetails
   */
  @Override
  public List<AlertRulesDetails> retrieveAllAlerts(Ticket ticket) {
    return alertRulesRepository.findAll();
  }

  /**
   * Delete Alert Rule.
   *
   * @param alertRuleId Alert rule Id
   */
  @Override
  public void deleteAlertRule(
      @NotNull(message = "Alert Id cannot be null") @NotNull Long alertRuleId, Ticket ticket) {
    alertRulesRepository.deleteById(alertRuleId);
  }

  /**
   * Get Alert Rule.
   *
   * @param alertRuleId Alert rule Id
   * @return
   */
  @Override
  public Alert getAlertRule(
      @NotNull(message = "alertRuleID cannot be null") @NotNull Long alertRuleId, Ticket ticket) {

    Alert alert = new Alert();
    AlertRulesDetails alertRulesDetails = alertRulesRepository.findById(alertRuleId).get();
    alert.setThresholdValue(alertRulesDetails.getThresholdValue());
    alert.setAlertSeverity(alertRulesDetails.getAlertSeverity());
    alert.setActiveInd(alertRulesDetails.getActiveInd().toString());
    alert.setAggregation(alertRulesDetails.getAggregation());
    alert.setDatapodId(alertRulesDetails.getDatapodId());
    alert.setMonitoringEntity(alertRulesDetails.getMonitoringEntity());
    alert.setOperator(alertRulesDetails.getOperator());
    alert.setRuleDescription(alertRulesDetails.getRuleDescriptions());
    alert.setRuleName(alertRulesDetails.getRuleName());
    Optional<DatapodDetails> datapodDetail = alertDatapodRepository.findById(alert.getDatapodId());
    alert.setDatapodName(datapodDetail.get().getDatapodName());
    alert.setCategoryId(alertRulesDetails.getCategory());
    return alert;
  }

  /**
   * Get Alert Rules By Category.
   *
   * @param category Category Id
   * @return
   */
  @Override
  public List<AlertRulesDetails> getAlertRulesByCategory(
      @NotNull(message = "categoryId cannot be null") @NotNull String category, Ticket ticket) {

    Optional<AlertCustomerDetails> alertCustomerDetails =
        alertCustomerRepository.findByCustomerCode(ticket.getCustCode());
    List<AlertRulesDetails> alertRulesDetails = alertRulesRepository.findByCategory(category);
    return alertRulesDetails;
  }

  /**
   * Create Customer Details.
   *
   * @param createdBy Created By
   * @param productCode Product Code
   * @param customerCode Customer Code
   * @return AlertCustomerSysId if created successfully.
   */
  private Long createCustomerDetails(String createdBy, String productCode, String customerCode) {
    AlertCustomerDetails alertCustomerDetails = new AlertCustomerDetails();
    alertCustomerDetails.setCreatedBy(createdBy);
    alertCustomerDetails.setProductCode(productCode);
    alertCustomerDetails.setActiveInd(true);
    alertCustomerDetails.setCustomerCode(customerCode);
    return alertCustomerRepository.save(alertCustomerDetails).getAlertCustomerSysId();
  }

  /**
   * Create datapod details.
   *
   * @param datapodId datapod ID
   * @param datapodName DataPod Name
   * @param createdBy Created BY
   * @param alertCustomerSysId Alert Customer ID
   * @return Boolean if success.
   */
  private Boolean createDatapodDetails(
      String datapodId, String datapodName, String createdBy, Long alertCustomerSysId) {
    DatapodDetails datapodDetails = new DatapodDetails();
    datapodDetails.setDatapodName(datapodName);
    datapodDetails.setDatapodId(datapodId);
    datapodDetails.setCreatedBy(createdBy);
    datapodDetails.setAlertCustomerSysId(alertCustomerSysId);
    alertDatapodRepository.save(datapodDetails);
    return true;
  }

  /**
   * Retrieve operator details.
   *
   * @param ticket ticket Id
   * @return String if matched
   */
  @Override
  public String retrieveOperatorsDetails(Ticket ticket) {
    List<AlertRulesDetails> rulesDetails = alertRulesRepository.findAll();
    JsonArray elements = new JsonArray();
    JsonObject response = new JsonObject();
    if (rulesDetails != null && !rulesDetails.isEmpty()) {
      for (AlertRulesDetails details : rulesDetails) {
        JsonObject object = new JsonObject();
        String readableOperator = getReadableOperator(details.getOperator());
        if (readableOperator != null) {
          object.addProperty(ID, details.getOperator().value());
          object.addProperty(NAME, readableOperator);
          elements.add(object);
        }
      }
    }
    response.add(OPERATORS, elements);
    return response.toString();
  }

  /**
   * It return readable operator name
   *
   * @param operator
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
      case BTW:
        return "Between";
      case SW:
        return "Start With";
      case EW:
        return "End With";
      case CONTAINS:
        return "Contains";
      case ISIN:
        return "Is IN";
      default:
        return null;
    }
  }
}