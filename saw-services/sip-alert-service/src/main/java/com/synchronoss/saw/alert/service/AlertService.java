package com.synchronoss.saw.alert.service;

import com.synchronoss.saw.alert.modal.Alert;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import jdk.nashorn.internal.ir.ObjectNode;

public interface AlertService {

  Alert createAlertRule(@NotNull(message = "Alert definition cannot be null") @Valid Alert alert);

  Alert updateAlertRule(@NotNull(message = "Alert definition cannot be null") @Valid Alert alert);

  void deleteAlertRule(@NotNull(message = "Alert Id cannot be null") @NotNull String alertRuleId);

  Alert getAnalysis(@NotNull(message = "alertRuleId cannot be null") @NotNull String alertRuleId);

  List<ObjectNode> getAlertRulesByCategory(
      @NotNull(message = "categoryID cannot be null") @NotNull String categoryId);
}
