package com.synchronoss.sip.alert.service;

import static com.synchronoss.sip.alert.util.AlertUtils.saveNotificationStatus;

import com.synchronoss.sip.alert.modal.AlertNotificationLog;
import com.synchronoss.sip.alert.modal.AlertRuleDetails;
import com.synchronoss.sip.alert.modal.ModuleName;
import com.synchronoss.sip.alert.modal.ModuleSubscriberMappingPayload;
import com.synchronoss.sip.utils.RestUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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
          saveNotificationStatus(notificationLog, basePath, notificationLogTable);
        }
      } else {
        String msg =
            "Unable to read alert rule details for alertRule" + alertRule.getAlertRuleName();
        logger.error(msg);
        notificationLog.setNotifiedStatus(false);
        notificationLog.setMessage(msg);
        notificationLog.setCreatedTime(new Date());
        saveNotificationStatus(notificationLog, basePath, notificationLogTable);
      }
    } catch (Exception e) {
      logger.error("Exception occurred while sending notification {}", e);
    }
  }
}
