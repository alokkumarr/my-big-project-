package com.synchronoss.saw.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

  @Override
  public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.add(jsonConverter());
  }

  /**
   * Customer json converter to convert json with the default json serializer - which will
   * sanitize the json strings.
   * @return
   */
  @Bean
  public HttpMessageConverter<?> jsonConverter() {
    SimpleModule module = new SimpleModule();

    DefaultXssJsonSerializer defaultXssJsonSerializer = new DefaultXssJsonSerializer();
    module.addSerializer(defaultXssJsonSerializer);

    ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
    objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    objectMapper.registerModule(module);
    return new MappingJackson2HttpMessageConverter(objectMapper);
  }
}
