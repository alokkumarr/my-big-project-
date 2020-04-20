package com.synchronoss.sip.alert.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.synchronoss.sip.alert.metadata.AlertsMetadata;
import com.synchronoss.sip.alert.service.migrationservice.AlertConverter;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.ojai.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class MigrateAlerts {

  private static final Logger logger = LoggerFactory.getLogger(MigrateAlerts.class);

  @Value("${sip.service.metastore.base}")
  @NotNull
  private String basePath;

  @Value("${sip.service.metastore.alertRulesTable}")
  @NotNull
  private String alertRulesMetadata;

  @Value("${alerts.alert-rule-migration-required}")
  @NotNull
  private boolean migrationRequired;

  @Autowired
  private AlertConverter alertConverter;

  private static String NOTIFICATION = "notification";

  Gson gson = new GsonBuilder().setPrettyPrinting().create();

  public MigrateAlerts() {
  }

  /**
   * Convert Alerts.
   */
  public void convertAllAlerts() throws Exception {
    List<String> alertRuleDetailsList = getAllAlerts();
    if (!CollectionUtils.isEmpty(alertRuleDetailsList)) {
      alertRuleDetailsList.forEach(
          alertRuleDetails -> {
            JsonObject alertJsonObject =
                new JsonParser().parse(alertRuleDetails.toString()).getAsJsonObject();
            if (alertJsonObject != null && alertJsonObject.has(NOTIFICATION)) {
              logger.info("Before Converting AlertRule - Json : {}", gson.toJson(alertJsonObject));
              alertJsonObject = alertConverter.convert(alertJsonObject);
              String alertRulesSysId = alertJsonObject.get("alertRulesSysId").getAsString();
              logger.info("Updated AlertRuleDef : {}", gson.toJson(alertJsonObject));
              updateAlertRule(alertJsonObject, alertRulesSysId);
            }
          });
    } else {
      logger.info("No Alerts definitions to migrate !!");
    }
  }

  /**
   * Fetch all alerts.
   *
   * @return List of alerts
   */
  public List<String> getAllAlerts() throws Exception {
    AlertsMetadata alertsMetadata = new AlertsMetadata(alertRulesMetadata, basePath);
    List<String> alertsList = new ArrayList<>();
    List<Document> doc = alertsMetadata.searchAll();
    if (doc == null) {
      return null;
    }
    ObjectMapper mapper = new ObjectMapper();
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    for (Document d : doc) {
      alertsList.add(d.asJsonString());
    }
    logger.info("number of Alerts definitions that needs migration : {}", alertsList.size());
    return alertsList;
  }

  /**
   * Update alert rule definition.
   *
   * @param alertRuleDetails Alert rule details
   * @param alertRuleId Alert rule id
   * @return Updated alert rule
   */
  public JsonObject updateAlertRule(JsonObject alertRuleDetails, String alertRuleId) {
    try {
      AlertsMetadata alertsMetadata = new AlertsMetadata(alertRulesMetadata, basePath);
      logger.trace("alertRuleId : {}, alertRulesMetadata : {}, basePath : {}", alertRuleId,
          alertRulesMetadata, basePath);
      alertsMetadata.update(alertRuleId, alertRuleDetails);
      logger.info("AlertDefinition id {} successfully updated : {}", alertRuleId,
          gson.toJson(alertRuleDetails));
    } catch (Exception e) {
      logger.error("Exception occurred while updating AlertDefinition : {}", e);
    }
    return alertRuleDetails;
  }

  /**
   * Invokes binary to json migration for analysis metadata.
   *
   * @throws Exception In case of errors
   */
  public void start() throws Exception {
    if (migrationRequired) {
      logger.info("Migration initiated");
      convertAllAlerts();
    }
    logger.info("Migration ended..");
  }
}
