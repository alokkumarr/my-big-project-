package com.synchronoss.saw.alert.service;

import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.saw.alert.modal.AlertCount;
import com.synchronoss.saw.alert.modal.AlertCountResponse;
import com.synchronoss.saw.alert.modal.AlertRuleDetails;
import com.synchronoss.saw.alert.modal.AlertStatesResponse;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface AlertService {

  AlertRuleDetails createAlertRule(
      @NotNull(message = "Alert definition cannot be null") @Valid AlertRuleDetails alert,
      Ticket ticket);

  List<AlertRuleDetails> retrieveAllAlerts(
      @NotNull(message = "Fetch all alerts rule details") Integer pageNumber,
      Integer pageSize, Ticket ticket);

  AlertRuleDetails updateAlertRule(
      @NotNull(message = "Alert definition cannot be null") @Valid AlertRuleDetails alert,
      String alertRuleId,
      Ticket token);

  Boolean deleteAlertRule(
      @NotNull(message = "Alert Id cannot be null") String alertRuleId, Ticket ticket);

  AlertRuleDetails getAlertRule(
      @NotNull(message = "alertRuleId cannot be null") String alertRuleId, Ticket ticket);

  List<AlertRuleDetails> getAlertRulesByCategory(
      @NotNull(message = "categoryID cannot be null") String categoryId, Integer pageNumber,
      Integer pageSize,Ticket token);

  String retrieveOperatorsDetails(
      @NotNull(message = "Fetch all alerts rule operators details") Ticket ticket);

  String retrieveAggregations(Ticket ticket);

  AlertStatesResponse getAlertStates(
      @NotNull(message = "alertRuleId cannot be null") Long alertRuleId,
      Integer pageNumber,
      Integer pageSize,
      Ticket ticket);

  AlertStatesResponse listAlertStates(Integer pageNumber, Integer pageSize, Ticket ticket);

  List<AlertCountResponse> alertCount(AlertCount alertCount, String alertRuleSysId);
}
