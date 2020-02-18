package com.synchronoss.sip.alert.service;

import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.saw.model.Model.Operator;
import com.synchronoss.sip.alert.modal.AlertCount;
import com.synchronoss.sip.alert.modal.AlertCountResponse;
import com.synchronoss.sip.alert.modal.AlertRuleDetails;
import com.synchronoss.sip.alert.modal.AlertRuleResponse;
import com.synchronoss.sip.alert.modal.AlertStatesFilter;
import com.synchronoss.sip.alert.modal.AlertStatesResponse;
import com.synchronoss.sip.alert.modal.AlertSubscriberToken;
import com.synchronoss.sip.alert.modal.Subscriber;
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

  String getReadableOperator(Operator operator);

  AlertSubscriberToken extractSubscriberToken(String token);

  Boolean activateSubscriber(String alertRulesSysId, String alertTriggerSysId, String email);

  Boolean deactivateSubscriber(String alertRulesSysId, String alertTriggerSysId, String email);

  List<Subscriber> fetchInactiveSubscriberByAlertId(String alertRuleSysId);

  Boolean createOrUpdateSubscriber(Subscriber subscriber);
}
