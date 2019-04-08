package com.synchronoss.saw.alert.service;

import com.synchronoss.saw.alert.entities.AlertRulesDetails;
import com.synchronoss.saw.alert.modal.Alert;
import com.synchronoss.saw.alert.repository.AlertCustomerRepository;
import com.synchronoss.saw.alert.repository.AlertDatapodRepository;
import com.synchronoss.saw.alert.repository.AlertRulesRepository;
import com.synchronoss.saw.alert.repository.AlertTriggerLog;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import jdk.nashorn.internal.ir.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlertServiceImpl implements AlertService {

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
      @NotNull(message = "Alert definition cannot be null") @Valid Alert alert) {
    AlertRulesDetails alertRulesDetails = new AlertRulesDetails();
    alertRulesDetails.setRuleName(alert.getRuleName());
    alertRulesDetails.setAggregation(alert.getAggregation());
    alertRulesDetails.setAlertSeverity(alert.getAlertSeverity());
    alertRulesDetails.setDatapodId(alert.getDatapodId());
    alertRulesDetails.setMonitoringEntity(alert.getMonitoringEntity());
    alertRulesDetails.setActiveInd(true);


    return null;
  }

  /**
   * Update Alert Rule.
   *
   * @param alert Alert
   * @return Alert
   */
  @Override
  public Alert updateAlertRule(
      @NotNull(message = "Alert definition cannot be null") @Valid Alert alert) {
    return null;
  }

  /**
   * Delete Alert Rule.
   *
   * @param alertRuleId Alert rule Id
   */
  @Override
  public void deleteAlertRule(
      @NotNull(message = "Alert Id cannot be null") @NotNull String alertRuleId) {}

  /**
   * Get Alert Rule.
   *
   * @param alertRuleId Alert rule Id
   * @return
   */
  @Override
  public Alert getAnalysis(
      @NotNull(message = "alertRuleID cannot be null") @NotNull String alertRuleId) {
    return null;
  }

  /**
   * Get Alert Rules By Category.
   *
   * @param categoryId Category Id
   * @return
   */
  @Override
  public List<ObjectNode> getAlertRulesByCategory(
      @NotNull(message = "categoryId cannot be null") @NotNull String categoryId) {
    return null;
  }
}
