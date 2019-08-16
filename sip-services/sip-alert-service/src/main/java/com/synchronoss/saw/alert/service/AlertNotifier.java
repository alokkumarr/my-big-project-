package com.synchronoss.saw.alert.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.synchronoss.saw.alert.entities.AlertRulesDetails;
import com.synchronoss.saw.alert.entities.AlertTriggerDetailsLog;
import com.synchronoss.saw.alert.modal.AlertNotificationLog;
import com.synchronoss.saw.alert.repository.AlertRulesRepository;
import com.synchronoss.sip.utils.RestUtil;
import java.util.Date;
import java.util.Optional;
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

@Component
public class AlertNotifier {

  private static final Logger logger = LoggerFactory.getLogger(AlertNotifier.class);

  @Value("${mail.body}")
  private String mailBody;

  @Value("${alert.dashborad.url}")
  private String alertDashboardPath;

  @Value("${transport.service.host}")
  private String transportUrl;

  @Value("${mail.subject}")
  private String mailSubject;

  @Value("${metastore.base}")
  @NotNull
  private String basePath;

  @Value("${metastore.notificationTable}")
  @NotNull
  private String notificationLogTable;

  @Autowired RestUtil restUtil;

  @Autowired AlertRulesRepository alertRulesRepository;

  private ObjectMapper objectMapper = new ObjectMapper();

  private RestTemplate restTemplate = null;

  void sendNotification(AlertTriggerDetailsLog triggerLog) {
    logger.info("Inside send notification method");

    Long alertRuleId = triggerLog.getAlertRulesSysId();
    // TODO:change the logic to read the data for alertruledetails from the maprdb instead of
    // mariadb
    //  and send notofication based on the configuration in alertruledetails

    Optional<AlertRulesDetails> alertDetails = alertRulesRepository.findById(alertRuleId);
    AlertNotificationLog notificationLog = new AlertNotificationLog();
    String id = UUID.randomUUID().toString();
    notificationLog.setNotificationId(id);
    if (alertDetails != null && alertDetails.isPresent()) {

      Boolean notifiedStatus = sendMail(alertDetails.get());
      notificationLog.setIsNotified(notifiedStatus);
    } else {
      notificationLog.setIsNotified(false);
      logger.error(
          "Unable to send notification as no alert rule details found for id :{} ", alertRuleId);
    }
    // need to change the value from string to boolean
    notificationLog.setAlertTriggerSysId(alertRuleId);
    notificationLog.setCreatedTime(new Date());
    saveNotificationStatus(notificationLog.getNotificationId(), notificationLog);
  }

  /**
   * Create Alert rule.
   *
   * @param alertRulesDetails AlertRulesDetails
   * @return status of mail notification
   */
  public boolean sendMail(AlertRulesDetails alertRulesDetails) {
    ObjectNode mailRequestPayload = objectMapper.createObjectNode();
    // ToDO: read the receipents form teh alertRuleDetails
    mailRequestPayload.put("recipients", "CHRamesh.Kumar@synchronoss.com");
    mailRequestPayload.put("subject", mailSubject);
    String preparedMailBody = prepareMailBody(alertRulesDetails, mailBody, alertDashboardPath);
    mailRequestPayload.put("content", preparedMailBody);
    String mailRequestBody = null;
    try {
      mailRequestBody = objectMapper.writeValueAsString(mailRequestPayload);
    } catch (JsonProcessingException e) {
      logger.error("Error occured while parsing the request body of mail API");
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
   * Create Alert rule.
   *
   * @param alertRulesDetails AlertRulesDetails
   * @param body mailbody
   * @param alertLink link for alert dashboard
   * @return prepared mail body
   */
  public String prepareMailBody(
      AlertRulesDetails alertRulesDetails, String body, String alertLink) {
    logger.debug("prepare mail body starts here :" + body);
    if (body.contains(MailBodyResolver.ALERT_NAME)) {
      body = body.replaceAll("\\" + MailBodyResolver.ALERT_NAME, alertRulesDetails.getAlertName());
    }
    if (body.contains(MailBodyResolver.CATEGORY)) {
      body = body.replaceAll("\\" + MailBodyResolver.CATEGORY, alertRulesDetails.getCategory());
    }
    if (body.contains(MailBodyResolver.LINK_FOR_ALERT)) {
      body = body.replaceAll("\\" + MailBodyResolver.LINK_FOR_ALERT, alertLink);
    }
    if (body.contains(MailBodyResolver.ALERT_SEVERITY)) {
      body =
          body.replaceAll(
              "\\" + MailBodyResolver.ALERT_SEVERITY, alertRulesDetails.getAlertSeverity().value());
    }
    logger.debug("prepare mail body ends here :" + this.getClass().getName() + ": " + body);
    return body;
  }

  interface MailBodyResolver {
    String ALERT_NAME = "$alertName";
    String CATEGORY = "$category";
    String LINK_FOR_ALERT = "$link";
    String ALERT_SEVERITY = "$alertSeverity";
  }

  void saveNotificationStatus(String id, AlertNotificationLog notificationLog) {
    try {
      AlertDataStore store = new AlertDataStore(notificationLogTable, basePath);
      logger.info("AlertStore::" + store);
      JsonElement parsedNotification =
          toJsonElement(objectMapper.writeValueAsString(notificationLog));
      store.create(id, parsedNotification);
    } catch (Exception e) {
      logger.error("Exception occured while writing the notificaton status." + e);
    }
  }

  /**
   * Convert to JsonElement.
   *
   * @param jsonString Json String
   * @return JsonElement
   */
  public static JsonElement toJsonElement(String jsonString) {
    logger.trace("toJsonElement Called: String = ", jsonString);
    com.google.gson.JsonParser jsonParser = new com.google.gson.JsonParser();
    JsonElement jsonElement;
    try {
      jsonElement = jsonParser.parse(jsonString);
      logger.trace("Parsed String = ", jsonElement);
      return jsonElement;
    } catch (JsonParseException jse) {
      logger.error("Can't parse String to Json, JsonParseException occurred!\n");
      logger.error(jse.getStackTrace().toString());
      return null;
    }
  }
}
