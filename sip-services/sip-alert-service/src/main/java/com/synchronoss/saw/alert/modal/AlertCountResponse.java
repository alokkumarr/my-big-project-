package com.synchronoss.saw.alert.modal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public interface AlertCountResponse {

  String getDate();

  String getCount();

  String getAlertSeverity();
}
