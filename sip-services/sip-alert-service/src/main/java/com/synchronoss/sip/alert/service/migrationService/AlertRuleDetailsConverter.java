package com.synchronoss.sip.alert.service.migrationService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.synchronoss.sip.alert.modal.AlertRuleDetails;
import com.synchronoss.sip.alert.modal.Email;
import com.synchronoss.sip.alert.modal.Notification;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AlertRuleDetailsConverter implements AlertConverter {

  private static final Logger logger = LoggerFactory.getLogger(AlertRuleDetailsConverter.class);
  Gson gson = new GsonBuilder().setPrettyPrinting().create();

  @Override
  public AlertRuleDetails convert(JsonObject oldAlertsDefinition) {
    AlertRuleDetails alertRuleDetails;
    JsonObject email = null;
    JsonObject notification = null;
    JsonArray emailIds = null;

    Notification notification1 = new Notification();
    Email email1 = new Email();
    Set<String> subscribersSet = new HashSet<>();
    //TODO : Call api and get the subscriber ids for all recipients.

    Set<String> emailSet = new HashSet();
    String alertRulesSysId = oldAlertsDefinition.get("alertRulesSysId").getAsString();
    logger.info(String.format("Migrating Alert Id : %s has started", alertRulesSysId));
    if (oldAlertsDefinition.has("notification")) {
      notification = oldAlertsDefinition.getAsJsonObject("notification");
    }

    if (notification != null && notification.has("email")) {
      email = notification.getAsJsonObject("email");
    }

    if (email != null && email.has("recipients")) {
      emailIds = email.getAsJsonArray("recipients");
    }

    if (emailIds != null && emailIds.size() > 0) {
      emailIds.forEach(mail -> {
        emailSet.add(mail.getAsString());
        // TODO : Add this email to subsriber table and relate with AlertMapping.
        subscribersSet.add("test-id");
      });
    }

    email1.setSubscribers(subscribersSet);
    notification1.setEmail(email1);
    alertRuleDetails = gson.fromJson(oldAlertsDefinition, AlertRuleDetails.class);
    alertRuleDetails.setNotification(notification1);
    return alertRuleDetails;
  }
}
