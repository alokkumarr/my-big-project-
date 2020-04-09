package com.synchronoss.sip.alert.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.sip.alert.modal.AlertNotificationLog;
import com.synchronoss.sip.alert.modal.AlertResult;
import com.synchronoss.sip.alert.modal.AlertRuleDetails;
import com.synchronoss.sip.alert.modal.ModuleName;
import com.synchronoss.sip.alert.modal.ModuleSubscriberMappingPayload;
import com.synchronoss.sip.alert.modal.Notification;
import com.synchronoss.sip.utils.RestUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import sncr.bda.base.MaprConnection;

@Component
public class AlertNotifier {

  private static final Logger logger = LoggerFactory.getLogger(AlertNotifier.class);
  @Autowired
  RestUtil restUtil;

  @Autowired
  AlertService alertService;

  @Autowired
  Notifier notifier;

  @Autowired
  EmailNotifier emailNotifier;

  @Autowired
  SubscriberService subscriberService;

  @Value("${sip.service.metastore.base}")
  @NotNull
  private String basePath;

  @Value("${sip.service.metastore.notificationTable}")
  @NotNull
  private String notificationLogTable;

  @Value("${sip.service.metastore.alertRulesTable}")
  @NotNull
  private String alertRulesMetadata;

  private ObjectMapper objectMapper = new ObjectMapper();

  private RestTemplate restTemplate = null;

  /**
   * Send Alert notification.
   *
   * @param alertRule Alert Rule.
   * @param alertTriggerSysId alertTriggerSysId
   */
  public void sendNotification(AlertRuleDetails alertRule, String alertTriggerSysId) {
    logger.info("Inside send notification method");
    try {
      AlertNotificationLog notificationLog = new AlertNotificationLog();
      if (alertRule != null) {
        ModuleSubscriberMappingPayload payload =
            subscriberService
                .fetchSubscribersForModule(alertRule.getAlertRulesSysId(), ModuleName.ALERT);
        List<String> subscriberList = new ArrayList<>();
        payload.getSubscribers().forEach(subscriberDetails -> {
          subscriberList.add(subscriberDetails.getSubscriberId());
        });
        alertRule.setSubscribers(subscriberList);

        if (!CollectionUtils.isEmpty(subscriberList)) {
          emailNotifier.setAlertRule(alertRule);
          emailNotifier.setAlertTriggerSysId(alertTriggerSysId);
          notifier = emailNotifier;
          notifier.notify("email");
        } else {
          String msg =
              "No subscribers configured for alertRule :"
                  + alertRule.getAlertRuleName();
          logger.error(msg);
          notificationLog.setNotifiedStatus(false);
          notificationLog.setMessage(msg);
          notificationLog.setCreatedTime(new Date());
          saveNotificationStatus(notificationLog);
        }
      } else {
        String msg =
            "Unable to read alert rule details for alertRule" + alertRule.getAlertRuleName();
        logger.error(msg);
        notificationLog.setNotifiedStatus(false);
        notificationLog.setMessage(msg);
        notificationLog.setCreatedTime(new Date());
        saveNotificationStatus(notificationLog);
      }
    } catch (Exception e) {
      logger.error("Exeception occured while sending notification {}", e.getStackTrace());
    }
  }

  private AlertResult getAlertResult(String alertTriggerSysId) {
    MaprConnection connection = new MaprConnection(basePath, alertRulesMetadata);
    JsonNode jsonAlertRule = connection.findById(alertTriggerSysId);
    AlertResult alertResult = null;
    try {
      objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
      alertResult = objectMapper.treeToValue(jsonAlertRule, AlertResult.class);
    } catch (JsonProcessingException e) {
      logger.error("Error occured while parsing the alert rule details :" + e);
    }
    return alertResult;
  }

  void saveNotificationStatus(AlertNotificationLog notificationLog) {
    logger.info("Saving the notification status");
    try {
      MaprConnection connection = new MaprConnection(basePath, notificationLogTable);
      String id = UUID.randomUUID().toString();
      notificationLog.setNotificationSysId(id);
      connection.insert(id, notificationLog);
    } catch (Exception e) {
      logger.error("Exception occured while writing the notificaton status." + e);
    }
  }
}
