package com.synchronoss.sip.alert.util;

import com.synchronoss.bda.sip.jwt.token.ProductModuleFeature;
import com.synchronoss.bda.sip.jwt.token.ProductModules;
import com.synchronoss.bda.sip.jwt.token.Products;
import com.synchronoss.saw.model.Model.Operator;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AlertUtils {
  private static final Logger logger = LoggerFactory.getLogger(AlertUtils.class);

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
}
