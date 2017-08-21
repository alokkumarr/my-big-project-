package com.synchronoss.saw.composite;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SAWCompositeProperties {

  @Value("${service-url}")
  private String serviceURL;

  @Value("${context.metadata}")
  private String contextMetadata;

  @Value("${context.security}")
  private String contextSecurity;

  @Value("${context.analysis}")
  private String contextAnalysis;

  public String getServiceURL() {
    return serviceURL;
  }

  public void setServiceURL(String serviceURL) {
    this.serviceURL = serviceURL;
  }

  public String getContextMetadata() {
    return contextMetadata;
  }

  public void setContextMetadata(String contextMetadata) {
    this.contextMetadata = contextMetadata;
  }

  public String getContextSecurity() {
    return contextSecurity;
  }

  public void setContextSecurity(String contextSecurity) {
    this.contextSecurity = contextSecurity;
  }

  public String getContextAnalysis() {
    return contextAnalysis;
  }

  public void setContextAnalysis(String contextAnalysis) {
    this.contextAnalysis = contextAnalysis;
  }

  



}
