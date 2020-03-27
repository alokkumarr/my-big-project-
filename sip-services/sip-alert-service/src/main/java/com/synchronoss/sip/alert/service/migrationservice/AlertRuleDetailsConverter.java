package com.synchronoss.sip.alert.service.migrationservice;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.synchronoss.sip.alert.modal.AlertRuleDetails;
import com.synchronoss.sip.alert.modal.NotificationChannelType;
import com.synchronoss.sip.alert.modal.NotificationSubscriber;
import com.synchronoss.sip.alert.service.SubscriberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlertRuleDetailsConverter implements AlertConverter {

  private static final Logger logger = LoggerFactory.getLogger(AlertRuleDetailsConverter.class);
  Gson gson = new GsonBuilder().setPrettyPrinting().create();

  @Autowired
  private SubscriberService subscriberService;

  @Override
  public AlertRuleDetails convert(JsonObject oldAlertsDefinition) {
    JsonObject email = null;
    JsonObject notification = null;
    JsonArray emailIds = null;

    // TODO : Call api and get the subscriber ids for all recipients.

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
        // TODO : Add this email to subsriber table and relate with AlertMapping.
        NotificationSubscriber notificationSubscriber = new NotificationSubscriber();
        notificationSubscriber.setChannelType(NotificationChannelType.EMAIL);
        notificationSubscriber.setChannelValue(mail.getAsString());
        notificationSubscriber.setSubscriberName(mail.getAsString());
        subscriberService.addSubscriber(notificationSubscriber,
            oldAlertsDefinition.get("customerCode").getAsString());
      });
    }
    oldAlertsDefinition.remove("notification");
    AlertRuleDetails alertRuleDetails = gson.fromJson(oldAlertsDefinition, AlertRuleDetails.class);
    return alertRuleDetails;
  }
}
