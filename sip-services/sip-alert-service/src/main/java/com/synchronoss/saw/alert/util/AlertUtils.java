package com.synchronoss.saw.alert.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.synchronoss.saw.alert.modal.AlertRuleDetails;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
  private static Long getEpochFromDateTime(String date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime ldt = LocalDateTime.parse(date, formatter);
    ZoneId zoneId = ZoneId.systemDefault();
    Long epochValue = ldt.atZone(zoneId).toInstant().toEpochMilli();
    return epochValue;
  }

  public static List<AlertRuleDetails> convertJsonListToAlertRuleList(List<JsonNode> alertLists) {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode jsonNode = mapper.createArrayNode().addAll(alertLists);
    ObjectReader reader =
        mapper
            .readerFor(new TypeReference<List<AlertRuleDetails>>() {})
            .without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    List<AlertRuleDetails> alertList = null;
    try {
      reader.without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
      alertList = reader.readValue(jsonNode);
      return alertList;
    } catch (IOException e) {
      logger.error("exeception e" + e);
      throw new RuntimeException("Exeception occured while parsing the results");
    }
  }
}
