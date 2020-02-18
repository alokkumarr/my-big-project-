package com.synchronoss.sip.alert.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.synchronoss.bda.sip.jwt.token.ProductModuleFeature;
import com.synchronoss.bda.sip.jwt.token.ProductModules;
import com.synchronoss.bda.sip.jwt.token.Products;
import com.synchronoss.saw.model.Model.Operator;

import com.synchronoss.sip.alert.modal.AlertResult;
import com.synchronoss.sip.alert.modal.AlertSubscriberToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import com.synchronoss.sip.alert.modal.AlertResponse;
import com.synchronoss.sip.alert.modal.AlertRuleResponse;
import com.synchronoss.sip.alert.modal.AlertStatesResponse;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import sncr.bda.base.MaprConnection;


@Component
public class AlertUtils {
  private static final Logger LOGGER = LoggerFactory.getLogger(AlertUtils.class);

  private static final String INVALID_TOKEN = "Invalid Token";
  private static final String ERROR_MESSAGE = "Error occurred while checking permission {}";
  public static final String ALERT_RULE_SYS_ID = "alertRulesSysId";
  public static final String START_TIME = "startTime";
  public static final String ALERT_STATE = "alertState";
  public static final String ALERT_UNSUBSCRIBE = "AlertUnsubscription";
  public static final String ALERT_SUBSCRIBER = "AlertSubscriber";

  private static String UNAUTHORIZED =
      "UNAUTHORIZED ACCESS : User don't have the %s permission for alerts!!";


  /**
   * Return timestamp from the given date.
   *
   * @param date String
   * @return Long
   */
  public static Long getEpochFromDateTime(String date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime ldt = LocalDateTime.parse(date, formatter);
    ZoneId zoneId = ZoneId.systemDefault();
    return ldt.atZone(zoneId).toInstant().toEpochMilli();
  }

  /**
   * Continuous Monitoring For All Rows.
   *
   * @param operator    Operator
   * @param value       Value
   * @param otherValue  other value
   * @param metricValue metric value
   * @return Boolean
   */
  public static Boolean checkThresholdsForRow(
      Operator operator, Double value, Double otherValue, Double metricValue) {
    switch (operator) {
      case BTW:
        return (metricValue >= otherValue && metricValue <= value);
      case LT:
        return (metricValue < value);
      case GT:
        return (metricValue > value);
      case GTE:
        return (metricValue >= value);
      case LTE:
        return (metricValue <= value);
      case EQ:
        return (Double.compare(metricValue, value) == 0);
      case NEQ:
        return (Double.compare(metricValue, value) != 0);
      default:
        return false;
    }
  }

  /**
   * Check the valid alerts privileges.
   *
   * @param productList
   * @return true if valid else false
   */
  public boolean validAlertPrivileges(List<Products> productList
      , String subcategory) {
    boolean[] haveValid = {false};
    if (productList != null && !productList.isEmpty()) {
      productList.stream().forEach(products -> {
        List<ProductModules> productModules = products.getProductModules().stream()
            .filter(pm -> "ALERTS".equalsIgnoreCase(pm.getProductModName()))
            .collect(Collectors.toList());

        if (productModules != null && !productModules.isEmpty()) {
          ProductModules modules = productModules.get(0);
          ArrayList<ProductModuleFeature> prodModFeature = modules.getProdModFeature();
          if (prodModFeature != null && !prodModFeature.isEmpty()) {
            ProductModuleFeature feature = prodModFeature.stream()
                .filter(productModuleFeature ->
                    "Alerts".equalsIgnoreCase(productModuleFeature.getProdModFeatureName()))
                .findAny().get();

            haveValid[0] = feature.getProductModuleSubFeatures() != null
                ? feature.getProductModuleSubFeatures().stream()
                .anyMatch(pmf -> subcategory.equalsIgnoreCase(pmf.getProdModFeatureName()))
                : haveValid[0];
          }
        }
      });
    }
    return haveValid[0];
  }


  /**
   * No access token for the request.
   *
   * @param response
   * @param alertResponse
   * @return AlertResponse
   */
  public AlertResponse emptyTicketResponse(HttpServletResponse response,
                                           AlertResponse alertResponse) {
    LOGGER.error(INVALID_TOKEN);
    response.setStatus(HttpStatus.SC_UNAUTHORIZED);
    alertResponse.setMessage(INVALID_TOKEN);
    return alertResponse;
  }

  /**
   * Check valid permission if exist return the Alert response.
   *
   * @param response
   * @param alertResponse
   * @return AlertResponse
   */
  public AlertResponse validatePermissionResponse(HttpServletResponse response,
                                                  AlertResponse alertResponse,
                                                  String privileges) {
    try {
      // validate the alerts access privileges
      LOGGER.error(String.format(UNAUTHORIZED, privileges));
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      response.sendError(HttpStatus.SC_UNAUTHORIZED,
          String.format(UNAUTHORIZED, privileges));
      alertResponse.setMessage(String.format(UNAUTHORIZED, privileges));
      return alertResponse;
    } catch (IOException ex) {
      LOGGER.error(ERROR_MESSAGE, ex);
      return alertResponse;
    }
  }

  /**
   * No access token for the request.
   *
   * @param response
   * @param alertResponse
   * @return AlertStatesResponse
   */
  public AlertStatesResponse emptyTicketResponse(HttpServletResponse response,
                                                 AlertStatesResponse alertResponse) {
    LOGGER.error(INVALID_TOKEN);
    response.setStatus(HttpStatus.SC_UNAUTHORIZED);
    alertResponse.setMessage(INVALID_TOKEN);
    return alertResponse;
  }

  /**
   * Check valid permission if exist return the Alert state response.
   *
   * @param response
   * @param alertResponse
   * @return AlertStatesResponse
   */
  public AlertStatesResponse validatePermissionResponse(HttpServletResponse response,
                                                        AlertStatesResponse alertResponse,
                                                        String privileges) {
    try {
      // validate the alerts access privileges
      String errorMessage = String.format(UNAUTHORIZED, privileges);
      LOGGER.error(errorMessage);
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, errorMessage);
      alertResponse.setMessage(errorMessage);
      return alertResponse;
    } catch (IOException ex) {
      LOGGER.error(ERROR_MESSAGE, ex);
      return alertResponse;
    }
  }

  /**
   * No access token for the request.
   *
   * @param response
   * @param alertResponse
   * @return AlertRuleResponse
   */
  public AlertRuleResponse emptyTicketResponse(HttpServletResponse response,
                                               AlertRuleResponse alertResponse) {
    LOGGER.error(INVALID_TOKEN);
    response.setStatus(HttpStatus.SC_UNAUTHORIZED);
    alertResponse.setMessage(INVALID_TOKEN);
    return alertResponse;
  }

  /**
   * Check valid permission if exist return the Alert rule response.
   *
   * @param response
   * @param ruleResponse
   * @return ruleResponse
   */
  public AlertRuleResponse validatePermissionResponse(HttpServletResponse response,
                                                      AlertRuleResponse ruleResponse,
                                                      String privileges) {
    try {
      // validate the alerts access privileges
      LOGGER.error(String.format(UNAUTHORIZED, privileges));
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      response.sendError(HttpStatus.SC_UNAUTHORIZED,
          String.format(UNAUTHORIZED, "Access"));
      ruleResponse.setMessage(String.format(UNAUTHORIZED, privileges));
      return ruleResponse;
    } catch (IOException ex) {
      LOGGER.error(ERROR_MESSAGE, ex);
      return ruleResponse;
    }
  }

  /**
   * No access token for the request.
   *
   * @param response
   * @return String
   */
  public String emptyTicketResponse(HttpServletResponse response) {
    LOGGER.error(INVALID_TOKEN);
    response.setStatus(HttpStatus.SC_UNAUTHORIZED);
    return String.format(UNAUTHORIZED, "Access");
  }

  /**
   * Check valid permission if exist return the Alert response.
   *
   * @param response
   * @return String
   */
  public String validatePermissionResponse(HttpServletResponse response,
                                           String privileges) {
    String errorMessage = String.format(UNAUTHORIZED, privileges);
    try {
      // validate the alerts access privileges
      LOGGER.error(errorMessage);
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      response.sendError(HttpStatus.SC_UNAUTHORIZED, errorMessage);
      return errorMessage;
    } catch (IOException ex) {
      LOGGER.error(ERROR_MESSAGE, ex);
      return errorMessage;
    }
  }

  public static String getSubscriberToken(AlertSubscriberToken alertSubscriberToken, String secretKey) {
    return Jwts.builder()
        .setSubject(ALERT_UNSUBSCRIBE)
        .claim(ALERT_SUBSCRIBER, alertSubscriberToken)
        .setIssuedAt(new Date())
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();
  }

  public static AlertSubscriberToken parseSubscriberToken(String token, String secretKey) {
    Claims ssoToken = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    // Check if the Token is valid
    try {
      Set<Entry<String, Object>> entrySet =
          ((Map<String, Object>) ssoToken.get(ALERT_SUBSCRIBER)).entrySet();
      String alertRulesSysId = null,
          alertTriggerSysId = null,
          emailId = null,
          alertRuleName = null,
          alertRuleDescription = null;
      for (Map.Entry<String, Object> pair : entrySet) {
        if (pair.getKey().equals("alertRulesSysId")) {
          alertRulesSysId = pair.getValue().toString();
        }
        if (pair.getKey().equals("alertRulesSysId")) {
          alertTriggerSysId = pair.getValue().toString();
        }
        if (pair.getKey().equals("emailId")) {
          emailId = pair.getValue().toString();
        }
        if (pair.getKey().equals("alertRuleName")) {
          alertRuleName = pair.getValue().toString();
        }
        if (pair.getKey().equals("alertRuleDescription")) {
          alertRuleDescription = pair.getValue().toString();
        }
      }
      AlertSubscriberToken alertSubscriberToken =
          new AlertSubscriberToken(
              alertRulesSysId, alertRuleName, alertRuleDescription, alertTriggerSysId, emailId);
      return alertSubscriberToken;
    } catch (SignatureException signatureException) {
      throw signatureException;
    } catch (Exception exception) {
      throw exception;
    }
  }

   public static List<AlertResult> getLastAlertResultByAlertRuleId(
      String alertRulesSysId, String basePath, String alertResults) {
    ObjectMapper objectMapper = new ObjectMapper();
    ObjectNode node = objectMapper.createObjectNode();
    ObjectNode objectNode = node.putObject(MaprConnection.EQ);
    objectNode.put(ALERT_RULE_SYS_ID, alertRulesSysId);
    MaprConnection connection = new MaprConnection(basePath, alertResults);
    return connection.runMaprDbQueryWithFilter(
        node.toString(), 1, 1, AlertUtils.START_TIME, AlertResult.class);
  }
}
