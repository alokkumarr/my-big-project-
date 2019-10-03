package com.synchronoss.bda.sip;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.synchronoss.bda.sip.exception.SipNotProcessedSipEntityException;

import java.io.IOException;
import org.owasp.esapi.ESAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class is used to roll over the json attribute value <br/>
 * and check for the intrusion.
 * 
 * @author spau0004
 */
public class SipJsonValidateSerializer extends JsonSerializer<Object> {
  Logger logger = LoggerFactory.getLogger(this.getClass());
  private final JsonSerializer<Object> defaultSerializer;


  SipJsonValidateSerializer(final JsonSerializer<Object> defaultSerializer) {
    this.defaultSerializer = defaultSerializer;
  }

  @Override
  public void serialize(Object value, JsonGenerator gen, SerializerProvider provider)
      throws IOException {
    Boolean isValid =
        ESAPI.validator().isValidInput("Validating json attributes value for intrusion",
            value.toString(), "SafeString", value.toString().length(), false);
    logger.trace(value.toString());
    if (!isValid) {
      throw new SipNotProcessedSipEntityException(value.toString() + " is not valid.");
    }
    defaultSerializer.serialize(value, gen, provider);
  }

}
