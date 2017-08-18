package com.synchronoss.saw.composite;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SAWCompositeProperties {

  @Value("${metadata.url}")
  private String metaDataURL;

  @Value("${analysis.url}")
  private String executionDataURL;

  @Value("${semantic.url}")
  private String semanticDataURL;

  @Value("${metadata.context}")
  private String metaDataContext;

  @Value("${analysis.context}")
  private String executionDataContext;

  @Value("${semantic.context}")
  private String semanticDataContext;


  @Value("${security.context}")
  private String securityContext;

  @Value("${security.url}")
  private String securityURL;

  public String getSecurityURL() {
    return securityURL;
  }

  public void setSecurityURL(String securityURL) {
    this.securityURL = securityURL;
  }

  public String getMetaDataURL() {
    return metaDataURL;
  }

  public void setMetaDataURL(String metaDataURL) {
    this.metaDataURL = metaDataURL;
  }

  public String getExecutionDataURL() {
    return executionDataURL;
  }

  public void setExecutionDataURL(String executionDataURL) {
    this.executionDataURL = executionDataURL;
  }

  public String getSemanticDataURL() {
    return semanticDataURL;
  }

  public void setSemanticDataURL(String semanticDataURL) {
    this.semanticDataURL = semanticDataURL;
  }

  public String getMetaDataContext() {
    return metaDataContext;
  }

  public void setMetaDataContext(String metaDataContext) {
    this.metaDataContext = metaDataContext;
  }

  public String getExecutionDataContext() {
    return executionDataContext;
  }

  public void setExecutionDataContext(String executionDataContext) {
    this.executionDataContext = executionDataContext;
  }

  public String getSemanticDataContext() {
    return semanticDataContext;
  }

  public void setSemanticDataContext(String semanticDataContext) {
    this.semanticDataContext = semanticDataContext;
  }

  public String getSecurityContext() {
    return securityContext;
  }

  public void setSecurityContext(String securityContext) {
    this.securityContext = securityContext;
  }


}
