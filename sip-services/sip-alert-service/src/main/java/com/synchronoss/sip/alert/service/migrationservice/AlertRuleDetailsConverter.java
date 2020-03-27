package com.synchronoss.sip.alert.service.migrationservice;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.synchronoss.sip.alert.modal.AlertRuleDetails;
import com.synchronoss.sip.alert.modal.Email;
import com.synchronoss.sip.alert.modal.Notification;
import com.synchronoss.sip.alert.modal.NotificationChannelType;
import com.synchronoss.sip.alert.modal.NotificationSubscriber;
import com.synchronoss.sip.alert.service.SubscriberService;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
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
<<<<<<< HEAD:sip-services/sip-alert-service/src/main/java/com/synchronoss/sip/alert/service/migrationService/AlertRuleDetailsConverter.java
    AlertRuleDetails alertRuleDetails;
    JsonObject email = null;
    JsonObject notification = null;
    JsonArray emailIds = null;

=======
    Set<String> subscribersSet =
        new HashSet<>(); // TODO : Call api and get the subscriber ids for all recipients.

    Set<String> emailSet = new HashSet();
>>>>>>> 1264c48935bb807241e0e84620f89640fe5c6f80:sip-services/sip-alert-service/src/main/java/com/synchronoss/sip/alert/service/migrationservice/AlertRuleDetailsConverter.java
    String alertRulesSysId = oldAlertsDefinition.get("alertRulesSysId").getAsString();
    logger.info(String.format("Migrating Alert Id : %s has started", alertRulesSysId));

    JsonObject notification = null;
    if (oldAlertsDefinition.has("notification")) {
      notification = oldAlertsDefinition.getAsJsonObject("notification");
    }

    JsonObject email = null;
    if (notification != null && notification.has("email")) {
      email = notification.getAsJsonObject("email");
    }

    JsonArray emailIds = null;
    if (email != null && email.has("recipients")) {
      emailIds = email.getAsJsonArray("recipients");
    }

    if (emailIds != null && emailIds.size() > 0) {
<<<<<<< HEAD:sip-services/sip-alert-service/src/main/java/com/synchronoss/sip/alert/service/migrationService/AlertRuleDetailsConverter.java
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
=======
      emailIds.forEach(
          mail -> {
            emailSet.add(mail.getAsString());
            // TODO : Add this email to subsriber table and relate with AlertMapping.
            subscribersSet.add("test-id");
          });
    }

    Email email1 = new Email();
    Notification notification1 = new Notification();
    email1.setSubscribers(subscribersSet);
    notification1.setEmail(email1);
    AlertRuleDetails alertRuleDetails;
>>>>>>> 1264c48935bb807241e0e84620f89640fe5c6f80:sip-services/sip-alert-service/src/main/java/com/synchronoss/sip/alert/service/migrationservice/AlertRuleDetailsConverter.java
    alertRuleDetails = gson.fromJson(oldAlertsDefinition, AlertRuleDetails.class);
    return alertRuleDetails;
  }
}
