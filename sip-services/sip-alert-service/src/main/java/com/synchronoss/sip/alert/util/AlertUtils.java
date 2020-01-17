package com.synchronoss.sip.alert.util;

import com.synchronoss.bda.sip.jwt.token.ProductModuleFeature;
import com.synchronoss.bda.sip.jwt.token.ProductModules;
import com.synchronoss.bda.sip.jwt.token.Products;
import com.synchronoss.saw.model.Model.Operator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import com.synchronoss.sip.alert.modal.AlertResponse;
import com.synchronoss.sip.alert.modal.AlertRuleResponse;
import com.synchronoss.sip.alert.modal.AlertStatesResponse;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class AlertUtils {
  private static final Logger logger = LoggerFactory.getLogger(AlertUtils.class);

  private static final String INVALID_TOKEN = "Invalid Token";
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
    Long epochValue = ldt.atZone(zoneId).toInstant().toEpochMilli();
    return epochValue;
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
        return (metricValue >= otherValue && metricValue <= value) ? true : false;
      case LT:
        return (metricValue < value) ? true : false;
      case GT:
        return (metricValue > value) ? true : false;
      case GTE:
        return (metricValue >= value) ? true : false;
      case LTE:
        return (metricValue <= value) ? true : false;
      case EQ:
        return (Double.compare(metricValue, value) == 0) ? true : false;
      case NEQ:
        return (Double.compare(metricValue, value) != 0) ? true : false;
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
  public boolean validAlertPrivileges(ArrayList<Products> productList
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
                : false;
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
    logger.error(INVALID_TOKEN);
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
      logger.error(String.format(UNAUTHORIZED, privileges));
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      response.sendError(HttpStatus.SC_UNAUTHORIZED,
          String.format(UNAUTHORIZED, privileges));
      alertResponse.setMessage(String.format(UNAUTHORIZED, privileges));
      return alertResponse;
    } catch (IOException ex) {
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
    logger.error(INVALID_TOKEN);
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
      logger.error(errorMessage);
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, errorMessage);
      alertResponse.setMessage(errorMessage);
      return alertResponse;
    } catch (IOException ex) {
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
    logger.error(INVALID_TOKEN);
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
      logger.error(String.format(UNAUTHORIZED, privileges));
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      response.sendError(HttpStatus.SC_UNAUTHORIZED,
          String.format(UNAUTHORIZED, "Access"));
      ruleResponse.setMessage(String.format(UNAUTHORIZED, privileges));
      return ruleResponse;
    } catch (IOException ex) {
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
    logger.error(INVALID_TOKEN);
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
      logger.error(errorMessage);
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      response.sendError(HttpStatus.SC_UNAUTHORIZED, errorMessage);
      return errorMessage;
    } catch (IOException ex) {
      return errorMessage;
    }
  }
}
