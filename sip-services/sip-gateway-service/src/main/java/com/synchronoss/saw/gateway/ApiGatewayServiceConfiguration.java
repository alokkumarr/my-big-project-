package com.synchronoss.saw.gateway;

import java.util.Collections;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.bda.sip.SipXssRequestFilter;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableConfigurationProperties({ApiGatewayProperties.class})
public class ApiGatewayServiceConfiguration implements WebMvcConfigurer {

  Logger logger = LoggerFactory.getLogger(this.getClass());
  
  @Bean
  public RestTemplate restTemplate(HttpMessageConverters converters) {

    // we have to define Apache HTTP client to use the PATCH verb
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setSupportedMediaTypes(MediaType.parseMediaTypes(MediaType.APPLICATION_JSON_UTF8_VALUE));
    converter.setObjectMapper(new ObjectMapper());
    HttpClient httpClient = HttpClients.createDefault();
    RestTemplate restTemplate = new RestTemplate(Collections.<HttpMessageConverter<?>>singletonList(converter));
    restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
    restTemplate.setErrorHandler(new RestTemplateErrorHandler());
    logger.info("restTemplate: " + restTemplate.toString());
    return restTemplate;
  }
  
  @Bean
  public FilterRegistrationBean<SipXssRequestFilter> loggingFilter(){
      logger.info("sip xss filter starts here.");
      FilterRegistrationBean<SipXssRequestFilter> registrationBean 
        = new FilterRegistrationBean<>();
      registrationBean.setFilter(new SipXssRequestFilter());
      logger.info("sip xss filter ends here.");
      return registrationBean;    
  }
  
}
