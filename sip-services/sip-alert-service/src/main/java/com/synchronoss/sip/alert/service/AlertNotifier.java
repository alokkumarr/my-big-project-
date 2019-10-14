package com.synchronoss.sip.alert.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.synchronoss.saw.model.Model.Operator;
import com.synchronoss.sip.alert.modal.AlertNotificationLog;
import com.synchronoss.sip.alert.modal.AlertResult;
import com.synchronoss.sip.alert.modal.AlertRuleDetails;
import com.synchronoss.sip.alert.modal.Notification;
import com.synchronoss.sip.utils.RestUtil;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import sncr.bda.base.MaprConnection;

@Component
public class AlertNotifier {
  private static final Logger logger = LoggerFactory.getLogger(AlertNotifier.class);
  @Autowired RestUtil restUtil;

  @Autowired AlertService alertService;

  @Value("${sip.service.alert.mail.body}")
  private String mailBody;

  @Value("${sip.service.alert.dashborad.url}")
  private String alertDashboardPath;

  @Value("${sip.service.storage-proxy.service.host}")
  private String transportUrl;

  @Value("${sip.service.alert.mail.subject}")
  private String mailSubject;

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
   */
  public void sendNotification(AlertRuleDetails alertRule) {
    logger.info("Inside send notification method");
    try {
      AlertNotificationLog notificationLog = new AlertNotificationLog();
      if (alertRule != null) {
        Notification notification = alertRule.getNotification();
        if (notification != null) {
          if (notification.getEmail() != null) {
            sendMailNotification(alertRule);
          }
          // Slack/Webhooks and other notification channel can be added here.

        } else {
          String msg =
              "Notification mechanism is not configured for alertRule :"
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
      logger.error("Exeception occured while sending notification" + e);
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

  /**
   * Sends email notification.
   *
   * @param alertRulesDetails AlertRulesDetails
   */
  public void sendMailNotification(AlertRuleDetails alertRulesDetails) {
    logger.info("sending email notification");
    AlertNotificationLog notificationLog = new AlertNotificationLog();
    notificationLog.setAlertRuleName(alertRulesDetails.getAlertRuleName());
    notificationLog.setThresholdValue(alertRulesDetails.getThresholdValue());
    notificationLog.setAttributeName(alertRulesDetails.getAttributeName());
    notificationLog.setAlertSeverity(alertRulesDetails.getAlertSeverity());
    List<String> recipientsList = alertRulesDetails.getNotification().getEmail().getRecipients();
    try {
      if (recipientsList != null) {
        String recipients = String.join(",", recipientsList);
        Boolean notifiedStatus = sendMail(alertRulesDetails, recipients);
        notificationLog.setNotifiedStatus(notifiedStatus);
        if (notifiedStatus) {
          logger.debug("Successfully sent email notification");
          notificationLog.setMessage("Successfully sent email notification");
        } else {
          logger.debug("error occured while sending email notification");
          notificationLog.setMessage("error occured while sending email notification");
        }
      } else {
        notificationLog.setMessage(
            "Receipients are missing for the alertRuleId:"
                + alertRulesDetails.getAlertRulesSysId());
      }
      notificationLog.setCreatedTime(new Date());
      saveNotificationStatus(notificationLog);
    } catch (RuntimeException exeception) {
      notificationLog.setNotifiedStatus(false);
      notificationLog.setMessage(exeception.toString());
      saveNotificationStatus(notificationLog);
      logger.error("Exception occured while sending Email Notification" + exeception);
    }
  }

  /**
   * sends mail.
   *
   * @param alertRulesDetails AlertRulesDetails
   * @return status of mail notification
   */
  public boolean sendMail(AlertRuleDetails alertRulesDetails, String recipients) {
    ObjectNode mailRequestPayload = objectMapper.createObjectNode();
    mailRequestPayload.put("recipients", recipients);
    mailRequestPayload.put("subject", mailSubject);
    String preparedMailBody = prepareMailBody(alertRulesDetails, mailBody, alertDashboardPath);
    mailRequestPayload.put("content", preparedMailBody);
    String mailRequestBody = null;
    try {
      mailRequestBody = objectMapper.writeValueAsString(mailRequestPayload);
    } catch (JsonProcessingException e) {
      logger.error("Error occured while parsing the request body of mail API" + e);
      return false;
    }
    HttpHeaders requestHeaders = new HttpHeaders();
    requestHeaders.set("Content-type", MediaType.APPLICATION_JSON_UTF8_VALUE);
    HttpEntity<?> requestEntity = new HttpEntity<Object>(mailRequestBody, requestHeaders);
    String url = transportUrl + "/exports/email/send";
    restTemplate = restUtil.restTemplate();
    ResponseEntity<JsonNode> aliasResponse =
        restTemplate.exchange(url, HttpMethod.POST, requestEntity, JsonNode.class);
    JsonNode response = (JsonNode) aliasResponse.getBody();
    Boolean notifiedStatus;
    if (response.has("emailSent")) {
      notifiedStatus = response.get("emailSent").asBoolean();
    } else {
      notifiedStatus = false;
    }
    return notifiedStatus;
  }

  /**
   * prepares mail body.
   *
   * @param alertRulesDetails AlertRulesDetails
   * @param body mailbody
   * @param alertLink link for alert dashboard
   * @return prepared mail body
   */
  public String prepareMailBody(AlertRuleDetails alertRulesDetails, String body, String alertLink) {
    logger.debug("prepare mail body starts here :" + body);
    if (alertRulesDetails.getNotification().getEmail().getTemplate() != null) {
      // override the Body template if its configured for specific alerts.
      body = alertRulesDetails.getNotification().getEmail().getTemplate();
    }
    if (body.contains(MailBodyResolver.ALERT_RULE_NAME)) {
      String alertRuleName = alertRulesDetails.getAlertRuleName();
      if (alertRuleName == null) {
        alertRuleName = "";
      }
      body = body.replaceAll("\\" + MailBodyResolver.ALERT_RULE_NAME, alertRuleName);
    }
    if (body.contains(MailBodyResolver.CATEGORY)) {
      String category = alertRulesDetails.getCategoryId();
      if (category == null) {
        category = "";
      }
      body = body.replaceAll("\\" + MailBodyResolver.CATEGORY, category);
    }
    if (body.contains(MailBodyResolver.LINK_FOR_ALERT)) {
      String link = alertLink;
      if (link == null) {
        link = "";
      }
      body = body.replaceAll("\\" + MailBodyResolver.LINK_FOR_ALERT, link);
    }
    if (body.contains(MailBodyResolver.ALERT_SEVERITY)) {
      String severity = null;
      if (alertRulesDetails.getAlertSeverity() != null) {
        severity = alertRulesDetails.getAlertSeverity().value();
      } else {
        severity = "";
      }
      body = body.replaceAll("\\" + MailBodyResolver.ALERT_SEVERITY, severity);
    }
    if (body.contains(MailBodyResolver.ALERT_RULE_DESCRIPTION)) {
      String alertDescrptn = alertRulesDetails.getAlertRuleDescription();
      if (alertDescrptn == null) {
        alertDescrptn = "";
      }
      body = body.replaceAll("\\" + MailBodyResolver.ALERT_RULE_DESCRIPTION, alertDescrptn);
    }
    if (body.contains(MailBodyResolver.ATTRIBUTE_NAME)) {
      String attrName = alertRulesDetails.getAttributeName();
      if (attrName == null) {
        attrName = "";
      } else {
        if (attrName.endsWith(".keyword")) {
          attrName = attrName.replace(".keyword", "");
        }
      }
      body = body.replaceAll("\\" + MailBodyResolver.ATTRIBUTE_NAME, attrName);
    }
    if (body.contains(MailBodyResolver.ATTRIBUTE_VALUE)) {
      String attributeValue = alertRulesDetails.getAttributeValue();
      if (attributeValue == null) {
        attributeValue = "";
      }
      body = body.replaceAll("\\" + MailBodyResolver.ATTRIBUTE_VALUE, attributeValue);
    }
    if (body.contains(MailBodyResolver.THRESHOLD_VALUE)) {
      String alertCondition = getReadableConditionWithValues(alertRulesDetails);
      if (alertCondition == null) {
        alertCondition = "";
      }
      body = body.replaceAll("\\" + MailBodyResolver.THRESHOLD_VALUE, alertCondition);
    }
    if (body.contains(MailBodyResolver.LOOKBACK_PERIOD)) {
      String lookBackperiod = alertRulesDetails.getLookbackPeriod();
      if (lookBackperiod == null) {
        lookBackperiod = "";
      }
      body = body.replaceAll("\\" + MailBodyResolver.LOOKBACK_PERIOD, lookBackperiod);
    }
    logger.debug("prepare mail body ends here :" + this.getClass().getName() + ": " + body);
    return body;
  }

  private String getReadableConditionWithValues(AlertRuleDetails alertRulesDetails) {
    Operator operator = alertRulesDetails.getOperator();
    Double threshold = alertRulesDetails.getThresholdValue();
    Double otherThreshold = alertRulesDetails.getOtherThresholdValue();
    String readbleOperator = alertService.getReadableOperator(operator);
    if (operator == Operator.BTW) {
      return readbleOperator + " " + otherThreshold + " and " + threshold;
    } else {
      return readbleOperator + " " + threshold;
    }
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

  interface MailBodyResolver {
    String ALERT_RULE_NAME = "$alertRuleName";
    String CATEGORY = "$category";
    String LINK_FOR_ALERT = "$link";
    String ALERT_SEVERITY = "$alertSeverity";
    String ALERT_RULE_DESCRIPTION = "$alertRuleDescription";
    String THRESHOLD_VALUE = "$thresholdValue";
    String ATTRIBUTE_NAME = "$attributeName";
    String ATTRIBUTE_VALUE = "$attributeValue";
    String LOOKBACK_PERIOD = "$lookbackPeriod";
  }
}
