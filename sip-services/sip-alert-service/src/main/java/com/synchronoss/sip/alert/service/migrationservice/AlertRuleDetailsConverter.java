package com.synchronoss.sip.alert.service.migrationservice;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.synchronoss.sip.alert.modal.ModuleName;
import com.synchronoss.sip.alert.modal.ModuleSubscriberMappingPayload;
import com.synchronoss.sip.alert.modal.NotificationChannelType;
import com.synchronoss.sip.alert.modal.NotificationSubscriber;
import com.synchronoss.sip.alert.modal.SubscriberDetails;
import com.synchronoss.sip.alert.service.SubscriberService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlertRuleDetailsConverter implements AlertConverter {

  private static final Logger logger = LoggerFactory.getLogger(AlertRuleDetailsConverter.class);

  @Autowired
  private SubscriberService subscriberService;

  private static String CUSTOMER_CODE = "customerCode";
  private static String NOTIFICATION = "notification";

  @Override
  public JsonObject convert(JsonObject oldAlertsDefinition) {
    JsonObject email = null;
    JsonObject notification = null;
    JsonArray emailIds = null;
    String customerCode = null;

    String alertRulesSysId = oldAlertsDefinition.get("alertRulesSysId").getAsString();
    logger.info(String.format("Migrating Alert Id : %s has started", alertRulesSysId));

    if (oldAlertsDefinition != null && oldAlertsDefinition.has(NOTIFICATION)) {
      notification = oldAlertsDefinition.getAsJsonObject(NOTIFICATION);
    }

    if (notification != null && notification.has("email")) {
      email = notification.getAsJsonObject("email");
    }

    if (email != null && email.has("recipients")) {
      emailIds = email.getAsJsonArray("recipients");
    }

    if (oldAlertsDefinition != null && oldAlertsDefinition.has(CUSTOMER_CODE)) {
      customerCode = oldAlertsDefinition.get(CUSTOMER_CODE).getAsString();
    }

    Map<String, String> emails = getAllSubscribers(oldAlertsDefinition);
    logger.trace("email reciepients : {}", emailIds);

    List<SubscriberDetails> subscriberDetailsList = new ArrayList<>();
    if (emailIds != null && emailIds.size() > 0) {
      String finalCustomerCode = customerCode;
      String[] subsId = new String[1];
      emailIds.forEach(
          mail -> {
            if (mail != null) {
              if (!emails.containsKey(mail.getAsString())) {
                NotificationSubscriber notificationSubscriber = new NotificationSubscriber();
                notificationSubscriber.setChannelType(NotificationChannelType.EMAIL);
                notificationSubscriber.setChannelValue(mail.getAsString());
                notificationSubscriber.setSubscriberName(mail.getAsString());
                notificationSubscriber =
                    subscriberService.addSubscriber(notificationSubscriber, finalCustomerCode);
                subsId[0] = notificationSubscriber.getSubscriberId();
              } else {
                subsId[0] = emails.get(mail.getAsString());
                logger.debug("subsId[0] : {}", subsId[0]);
              }
              SubscriberDetails subscriberDetails = new SubscriberDetails();
              subscriberDetails.setSubscriberId(subsId[0]);
              subscriberDetails.setChannelTypes(
                  Collections.singletonList(NotificationChannelType.EMAIL));
              subscriberDetailsList.add(subscriberDetails);
            }
          });

      ModuleSubscriberMappingPayload moduleSubscriberMappingPayload =
          new ModuleSubscriberMappingPayload();
      moduleSubscriberMappingPayload.setModuleId(alertRulesSysId);
      moduleSubscriberMappingPayload.setModuleName(ModuleName.ALERT);
      logger.debug("setting subscriberDetailsList for alert : {} , {}", alertRulesSysId,
          subscriberDetailsList);
      moduleSubscriberMappingPayload.setSubscribers(subscriberDetailsList);
      subscriberService.addSubscribersToModule(moduleSubscriberMappingPayload);
    }

    oldAlertsDefinition.remove(NOTIFICATION);
    return oldAlertsDefinition;
  }

  /**
   * Get all subscribers.
   *
   * @param oldAlertsDefinition Alert definition from maprdb
   * @return Map
   */
  public Map<String, String> getAllSubscribers(JsonObject oldAlertsDefinition) {
    String customerCode = null;
    if (oldAlertsDefinition != null && oldAlertsDefinition.has(CUSTOMER_CODE)) {
      customerCode = oldAlertsDefinition.get(CUSTOMER_CODE).getAsString();
    }
    List<NotificationSubscriber> subscribers =
        subscriberService.getSubscribersByCustomerCode(customerCode);

    Map<String, String> emailSubscriber = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    subscribers.forEach(
        subscriber -> {
          emailSubscriber.put(subscriber.getChannelValue(), subscriber.getSubscriberId());
        });
    logger.trace("getAllSubscribers for alert id : {} is {}",
        oldAlertsDefinition.get("alertRulesSysId").getAsString(), emailSubscriber);
    return emailSubscriber;
  }
}
