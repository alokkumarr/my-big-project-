package com.synchronoss.saw.batch;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.springframework.web.util.HtmlUtils;

public class DefaultXssJsonSerializer extends JsonSerializer<String> {

  @Override
  public Class<String> handledType() {
    return String.class;
  }

  @Override
  public void serialize(
      String value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
      throws IOException {
    if (value != null) {
      String encodedValue = HtmlUtils.htmlEscape(value);
      jsonGenerator.writeString(encodedValue);
    }
  }
}
