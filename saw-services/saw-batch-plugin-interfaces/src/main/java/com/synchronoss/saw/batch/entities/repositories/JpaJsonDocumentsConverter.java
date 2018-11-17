package com.synchronoss.saw.batch.entities.repositories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.saw.batch.model.BisJson;
import java.io.IOException;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Converter
public class JpaJsonDocumentsConverter implements AttributeConverter<BisJson, String> {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  private Logger log = LoggerFactory.getLogger(getClass());

  @Override
  public String convertToDatabaseColumn(BisJson meta) {
    String jsonString = "";
    try {
      log.debug("Start convertToDatabaseColumn");

      jsonString = objectMapper.writeValueAsString(meta);
      log.debug("convertToDatabaseColumn" + jsonString);

    } catch (JsonProcessingException ex) {
      log.error(ex.getMessage());
    }
    return jsonString;
  }

  @Override
  public BisJson convertToEntityAttribute(String dbData) {
    BisJson bisJson = new BisJson();
    try {
      log.debug("Start convertToEntityAttribute");
      bisJson = objectMapper.readValue(dbData, BisJson.class);
      log.debug("JsonDocumentsConverter.convertToDatabaseColumn" + bisJson);

    } catch (IOException ex) {
      log.error(ex.getMessage());
    }
    return bisJson;
  }
}
