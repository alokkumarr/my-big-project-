package com.synchronoss.saw.composite;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@RefreshScope
public class SAWCompositeProperties {

	@Value("${metadata.url}")
	private String metaDataURL;

	public String getMetaDataURL() {
		return metaDataURL;
	}

	public void setMetaDataURL(String metaDataURL) {
		this.metaDataURL = metaDataURL;
	}
}
