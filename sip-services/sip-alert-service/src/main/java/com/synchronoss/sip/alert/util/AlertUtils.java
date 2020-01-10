package com.synchronoss.sip.alert.util;

import static com.synchronoss.sip.utils.SipCommonUtils.setUnAuthResponse;

import com.synchronoss.bda.sip.jwt.token.ProductModuleFeature;
import com.synchronoss.bda.sip.jwt.token.ProductModules;
import com.synchronoss.bda.sip.jwt.token.Products;
import com.synchronoss.saw.model.Model.Operator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
  public boolean validAlertPrivileges(ArrayList<Products> productList) {
    boolean[] haveValid = {false};
    if (productList != null && !productList.isEmpty()) {
      productList.stream().forEach(products -> {
        ProductModules productModule = products.getProductModules().stream()
            .filter(productModules -> "ALERTS".equalsIgnoreCase(productModules.getProductModName()))
            .collect(Collectors.toList()).get(0);

        ArrayList<ProductModuleFeature> prodModFeature = productModule.getProdModFeature();
        if (prodModFeature != null && !prodModFeature.isEmpty()) {
          haveValid[0] = prodModFeature.stream()
              .anyMatch(productModuleFeature ->
                  "Alerts".equalsIgnoreCase(productModuleFeature.getProdModFeatureName()));
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
                                                  AlertResponse alertResponse) {
    try {
      // validate the alerts access privileges
      logger.error(String.format(UNAUTHORIZED, "Access"));
      setUnAuthResponse(response);
      alertResponse.setMessage(String.format(UNAUTHORIZED, "Access"));
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
                                                        AlertStatesResponse alertResponse) {
    try {
      // validate the alerts access privileges
      logger.error(String.format(UNAUTHORIZED, "Access"));
      setUnAuthResponse(response);
      alertResponse.setMessage(String.format(UNAUTHORIZED, "Access"));
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
   * @param alertResponse
   * @return AlertRuleResponse
   */
  public AlertRuleResponse validatePermissionResponse(HttpServletResponse response,
                                                      AlertRuleResponse alertResponse) {
    try {
      // validate the alerts access privileges
      logger.error(String.format(UNAUTHORIZED, "Access"));
      setUnAuthResponse(response);
      alertResponse.setMessage(String.format(UNAUTHORIZED, "Access"));
      return alertResponse;
    } catch (IOException ex) {
      return alertResponse;
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
  public String validatePermissionResponse(HttpServletResponse response) {
    try {
      // validate the alerts access privileges
      logger.error(String.format(UNAUTHORIZED, "Access"));
      setUnAuthResponse(response);
      return String.format(UNAUTHORIZED, "Access");
    } catch (IOException ex) {
      return String.format(UNAUTHORIZED, "Access");
    }
  }
}
