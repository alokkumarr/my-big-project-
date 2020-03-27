package com.synchronoss.sip.alert.service.migrationService;

import com.google.gson.JsonObject;
import com.synchronoss.sip.alert.modal.AlertRuleDetails;

public interface AlertConverter {

  AlertRuleDetails convert(JsonObject oldAnalysisDefinition);

}
