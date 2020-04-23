package com.synchronoss.sip.alert.service.migrationservice;

import com.google.gson.JsonObject;

public interface AlertConverter {

  JsonObject convert(JsonObject oldAnalysisDefinition);

}
