package com.synchronoss.saw.alert.service;

import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.saw.alert.entities.AlertRulesDetails;
import com.synchronoss.saw.alert.modal.Alert;
import com.synchronoss.saw.alert.modal.AlertCount;
import com.synchronoss.saw.alert.modal.AlertCountResponse;
import com.synchronoss.saw.alert.modal.AlertStatesResponse;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface AlertService {

  Alert createAlertRule(
      @NotNull(message = "Alert definition cannot be null") @Valid Alert alert, Ticket token);

  List<AlertRulesDetails> retrieveAllAlerts(
      @NotNull(message = "Fetch all alerts rule details") Ticket token);

  Alert updateAlertRule(
      @NotNull(message = "Alert definition cannot be null") @Valid Alert alert,
      Long alertRuleId,
      Ticket token);

  Boolean deleteAlertRule(
      @NotNull(message = "Alert Id cannot be null") Long alertRuleId, Ticket token);

  Alert getAlertRule(
      @NotNull(message = "alertRuleId cannot be null") Long alertRuleId, Ticket token);

  List<AlertRulesDetails> getAlertRulesByCategory(
      @NotNull(message = "categoryID cannot be null") String categoryId, Ticket token);

  String retrieveOperatorsDetails(
      @NotNull(message = "Fetch all alerts rule operators details") Ticket token);

  String retrieveAggregations(Ticket ticket);

  AlertStatesResponse getAlertStates(
      @NotNull(message = "alertRuleId cannot be null") Long alertRuleId,
      Integer pageNumber,
      Integer pageSize,
      Ticket ticket);

  AlertStatesResponse listAlertStates(Integer pageNumber, Integer pageSize, Ticket ticket);

  List<AlertCountResponse> alertCount(AlertCount alertCount,Long alertRuleSysId);
}
