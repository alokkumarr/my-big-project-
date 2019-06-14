package com.synchronoss.saw.export;

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
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

@Configuration
public class WebConfig extends WebMvcConfigurationSupport {

  @Bean
  public MappingJackson2HttpMessageConverter jackson2HttpMessageConverter() {
    MappingJackson2HttpMessageConverter jackson = 
        new MappingJackson2HttpMessageConverter();
    ObjectMapper om = jackson.getObjectMapper();
    JsonSerializer<?> streamSer = new StdSerializer<Stream<?>>(Stream.class, true) {
      private static final long serialVersionUID = 1L;

      @Override public void serialize(
          Stream<?> stream, JsonGenerator jgen, SerializerProvider provider
      ) throws IOException, JsonGenerationException
      {
        provider.findValueSerializer(Iterator.class, null)
          .serialize(stream.iterator(), jgen, provider);
      }
    };
    om.registerModule(new SimpleModule("Streams API").addSerializer(streamSer));
    jackson.setObjectMapper(om);
    return jackson;
  }



  @Override
  protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.add(jackson2HttpMessageConverter());
    StringHttpMessageConverter converter = new StringHttpMessageConverter();
    converter.setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_PLAIN));
    /* Workaround: Configure message converter for "text/plain" as
     * required for Spring Boot Actuator Prometheus or
     * "/actuator/prometheus" will respond with HTTP 406 */
    converters.add(converter);
  }
  
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
      .addResourceHandler("swagger-ui.html")
      .addResourceLocations("classpath:/META-INF/resources/");
    registry
      .addResourceHandler("/webjars/**")
      .addResourceLocations("classpath:/META-INF/resources/webjars/");
  }

}
