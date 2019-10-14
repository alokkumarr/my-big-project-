package com.synchronoss.sip.alert.util;

import com.synchronoss.saw.model.Model.Operator;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
   * @param operator Operator
   * @param value Value
   * @param otherValue other value
   * @param metricValue metric value
   * @return Boolean
   */
  public static Boolean checkThresholdsForRow(
      Operator operator, Double value, Double otherValue, Double metricValue) {
    switch (operator) {
      case BTW:
        if (metricValue >= otherValue && metricValue <= value) {
          return true;
        } else {
          return false;
        }
      case LT:
        if (metricValue < value) {
          return true;
        } else {
          return false;
        }
      case GT:
        if (metricValue > value) {
          return true;
        } else {
          return false;
        }
      case GTE:
        if (metricValue >= value) {
          return true;
        } else {
          return false;
        }
      case LTE:
        if (metricValue <= value) {
          return true;
        } else {
          return false;
        }
      case EQ:
        if (Double.compare(metricValue, value) == 0) {
          return true;
        } else {
          return false;
        }
      case NEQ:
        if (Double.compare(metricValue, value) != 0) {
          return true;
        } else {
          return false;
        }
      default:
        return false;
    }
  }
}
