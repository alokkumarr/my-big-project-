package com.synchronoss.saw.composite;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@RefreshScope
public class SAWCompositeProperties {

	@Value("${metadata.url}")
	private String metaDataURL;
	
	@Value("${execution.url}")
	private String executionDataURL;

	@Value("${semantic.url}")
	private String semanticDataURL;

	@Value("${metadata.context}")
	private String metaDataContext;
	
	@Value("${execution.context}")
	private String executionDataContext;

	@Value("${semantic.context}")
	private String semanticDataContext;
	
	
	@Value("${client.context.security}")
	private String securityContext;


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
