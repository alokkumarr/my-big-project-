package com.razor.raw.published.rest.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


/**
 * Configuration properties for RAW Engine
 *
 * @author surendra.rajaneni
 * 
 */
@Configuration
public class RPProperties {

	
	
	@Value("${published-report-path}")
	private String publishedReportPath;

	/**
	 * @return the publishedReportPath
	 */
	public String getPublishedReportPath() {
		return publishedReportPath;
	}

	/**
	 * @param publishedReportPath the publishedReportPath to set
	 */
	public void setPublishedReportPath(String publishedReportPath) {
		this.publishedReportPath = publishedReportPath;
	}
	
	
	@Value("${published-report-max-size}")
	private int publishedReportMaxSize;

	/**
	 * @return the publishedReportMaxSize
	 */
	public int getPublishedReportMaxSize() {
		return publishedReportMaxSize;
	}

	/**
	 * @param publishedReportMaxSize the publishedReportMaxSize to set
	 */
	public void setPublishedReportMaxSize(int publishedReportMaxSize) {
		this.publishedReportMaxSize = publishedReportMaxSize;
	}

	
	
	
	
	

}
