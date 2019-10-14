package com.synchronoss.sip.alert.modal;

public interface AlertStates {

  Long getAlertTriggerSysId();

  Long getAlertRulesSysId();

  String getAlertName();

  String getAlertDescription();

  AlertState getAlertState();

  Long getStartTime();

  String getCategory();

  AlertSeverity getSeverity();

  Double getMetricValue();

  Double getThresholdValue();
}
