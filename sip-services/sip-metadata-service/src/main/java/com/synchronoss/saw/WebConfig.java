package com.synchronoss.saw;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class WebConfig extends WebMvcConfigurationSupport {

  /**
   * jackson to Http Message Converter.
   *
   * @return MappingJackson2HttpMessageConverter Mapping Jackson to Http Message Converter
   */
  @Bean
  public MappingJackson2HttpMessageConverter jackson2HttpMessageConverter() {
    MappingJackson2HttpMessageConverter jackson = new MappingJackson2HttpMessageConverter();
    ObjectMapper om = jackson.getObjectMapper();
    JsonSerializer<?> streamSer =
        new StdSerializer<Stream<?>>(Stream.class, true) {
          private static final long serialVersionUID = 1L;

          @Override
          public void serialize(Stream<?> stream, JsonGenerator jgen, SerializerProvider provider)
              throws IOException, JsonGenerationException {
            provider
                .findValueSerializer(Iterator.class, null)
                .serialize(stream.iterator(), jgen, provider);
          }
        };
    om.registerModule(new SimpleModule("Streams API").addSerializer(streamSer));
    jackson.setObjectMapper(om);
    return jackson;
  }

  /**
   * configure Message Converters.
   *
   * @param converters Http Message Converter
   */
  @Override
  protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.add(jackson2HttpMessageConverter());
    StringHttpMessageConverter converter = new StringHttpMessageConverter();
    converter.setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_PLAIN));
    /*
     * Workaround: Configure message converter for "text/plain" as required for
     * Spring Boot Actuator Prometheus or "/actuator/prometheus" will respond with
     * HTTP 406
     */
    converters.add(converter);
  }
}
