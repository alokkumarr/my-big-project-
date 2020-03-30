package com.synchronoss.sip.alert.service.migrationservice;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.synchronoss.sip.alert.modal.AlertRuleDetails;
import com.synchronoss.sip.alert.modal.ModuleName;
import com.synchronoss.sip.alert.modal.ModuleSubscriberMappingPayload;
import com.synchronoss.sip.alert.modal.NotificationChannelType;
import com.synchronoss.sip.alert.modal.NotificationSubscriber;
import com.synchronoss.sip.alert.modal.SubscriberDetails;
import com.synchronoss.sip.alert.service.SubscriberService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    List<SubscriberDetails> subscriberDetailsList = new ArrayList<>();
    if (emailIds != null && emailIds.size() > 0) {
      emailIds.forEach(mail -> {
        NotificationSubscriber notificationSubscriber = new NotificationSubscriber();
        notificationSubscriber.setChannelType(NotificationChannelType.EMAIL);
        notificationSubscriber.setChannelValue(mail.getAsString());
        notificationSubscriber.setSubscriberName(mail.getAsString());
        notificationSubscriber = subscriberService.addSubscriber(notificationSubscriber,
            oldAlertsDefinition.get("customerCode").getAsString());

        SubscriberDetails subscriberDetails = new SubscriberDetails();
        subscriberDetails.setSubscriberId(notificationSubscriber.getSubscriberId());
        subscriberDetails.setChannelTypes(Collections.singletonList(NotificationChannelType.EMAIL));
        subscriberDetailsList.add(subscriberDetails);
      });
    }

    ModuleSubscriberMappingPayload moduleSubscriberMappingPayload = new ModuleSubscriberMappingPayload();
    moduleSubscriberMappingPayload.setModuleId(alertRulesSysId);
    moduleSubscriberMappingPayload.setModuleName(ModuleName.ALERT);
    moduleSubscriberMappingPayload.setSubscribers(subscriberDetailsList);

    oldAlertsDefinition.remove("notification");
    AlertRuleDetails alertRuleDetails = gson.fromJson(oldAlertsDefinition, AlertRuleDetails.class);
    List<String> subscribersList = new ArrayList<>();
    subscriberDetailsList.forEach(subscriberDetails -> {
      subscribersList.add(subscriberDetails.getSubscriberId());
    });
    alertRuleDetails.setSubscribers(subscribersList);
    return alertRuleDetails;
  }
}
