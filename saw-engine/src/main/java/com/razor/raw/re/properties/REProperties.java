package com.razor.raw.re.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * Configuration properties for RAW Engine
 *
 * @author surendra.rajaneni
 * 
 */
//@ConfigurationProperties(locations={"classpath:rawengine.properties"}, prefix = "raw.engine")
//@ConfigurationProperties(locations={"${properties.location}rawengine.properties"}, prefix = "raw.engine")
@Configuration
public class REProperties {
	@Value("${raw.engine.broker-url}")
	private String brokerUrl;
	
	@Value("${raw.engine.queue}")
	private String queue;
	
	@Value("${raw.engine.xls-row-fetch-size}")
	private String xlsRowFetchSize;
	
	@Value("${raw.engine.csv-row-fetch-size}")
	private String csvRowFetchSize;
	
	@Value("${raw.engine.max-attachement-size}")
	private String maxAttachementSize;
	
	@Value("${raw.engine.published-report-path}")
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
	public String getMaxAttachementSize() {
		return maxAttachementSize;
	}
	public void setMaxAttachementSize(String maxAttachementSize) {
		this.maxAttachementSize = maxAttachementSize;
	}
	public String getXlsRowFetchSize() {
		return xlsRowFetchSize;
	}
	public void setXlsRowFetchSize(String xlsRowFetchSize) {
		this.xlsRowFetchSize = xlsRowFetchSize;
	}
	public String getCsvRowFetchSize() {
		return csvRowFetchSize;
	}
	public void setCsvRowFetchSize(String csvRowFetchSize) {
		this.csvRowFetchSize = csvRowFetchSize;
	}
	public String getBrokerUrl() {
		return brokerUrl;
	}
	public void setBrokerUrl(String brokerUrl) {
		this.brokerUrl = brokerUrl;
	}
	public String getQueue() {
		return queue;
	}
	public void setQueue(String queue) {
		this.queue = queue;
	}
	
	
}
