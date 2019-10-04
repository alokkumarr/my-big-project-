package com.synchronoss.saw.alert.service;

import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.saw.alert.modal.AlertCount;
import com.synchronoss.saw.alert.modal.AlertCountResponse;
import com.synchronoss.saw.alert.modal.AlertRuleDetails;
import com.synchronoss.saw.alert.modal.AlertRuleResponse;
import com.synchronoss.saw.alert.modal.AlertStatesFilter;
import com.synchronoss.saw.alert.modal.AlertStatesResponse;
import com.synchronoss.saw.model.Model.Operator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface AlertService {

  AlertRuleDetails createAlertRule(
      @NotNull(message = "Alert definition cannot be null") @Valid AlertRuleDetails alert,
      Ticket ticket);

  AlertRuleResponse retrieveAllAlerts(
      @NotNull(message = "Fetch all alerts rule details") Integer pageNumber,
      Integer pageSize,
      Ticket ticket);

  AlertRuleDetails updateAlertRule(
      @NotNull(message = "Alert definition cannot be null") @Valid AlertRuleDetails alert,
      String alertRuleId,
      Ticket token);

  Boolean deleteAlertRule(
      @NotNull(message = "Alert Id cannot be null") String alertRuleId, Ticket ticket);

  AlertRuleDetails getAlertRule(
      @NotNull(message = "alertRuleId cannot be null") String alertRuleId, Ticket ticket);

  AlertRuleResponse getAlertRulesByCategory(
      @NotNull(message = "categoryID cannot be null") String categoryId,
      Integer pageNumber,
      Integer pageSize,
      Ticket token);

  String retrieveOperatorsDetails(
      @NotNull(message = "Fetch all alerts rule operators details") Ticket ticket);

  String retrieveAggregations(Ticket ticket);

  AlertStatesResponse getAlertStates(
      @NotNull(message = "alertRuleId cannot be null") String alertRuleId,
      Integer pageNumber,
      Integer pageSize,
      Ticket ticket);

  AlertStatesResponse listAlertStates(
      Integer pageNumber, Integer pageSize, Ticket ticket, Optional<AlertStatesFilter> alertStates);

  List<AlertCountResponse> alertCount(
      AlertCount alertCount,
      Integer pageNumber,
      Integer pageSize,
      String alertRuleSysId,
      Ticket ticket);

  Set<String> listAttribueValues(Ticket ticket);

  String retrieveMonitoringType(Ticket ticket);

  void sendMessageToStream();

  String getReadableOperator(Operator operator);
}
