package com.razor.raw.generation.rest.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


/**
 * Configuration properties for RAW Engine
 *
 * @author surendra.rajaneni
 * 
 */
//@ConfigurationProperties(locations={"${properties.location}rawgeneration.properties"}, prefix = "raw.generation")
@Configuration
public class RGProperties {

	@Value("${raw.generation.broker-url}")
	private String brokerUrl;
	
	@Value("${raw.generation.queue}")
	private String queue;
	
	@Value("${xlsx.row.fetch.size}")
	private String xlsRowFetchSize;
	
	@Value("${csv-row-fetch-size}")
	private String csvRowFetchSize;
	
	@Value("${max-attachement-size}")
	private String maxAttachementSize;
	
	@Value("${published-report-path}")
	private String publishedReportPath;
	
	@Value("${no-of-schedule-upcomming-event}")
	private String noOfScheduleUpcommingEvents;
	
	@Value("${spring.oauth2.server.url}")
	private String checkTokenUrl;
	@Value("${spring.oauth2.client.client-id}") 
	private String clientId;
	@Value("${spring.oauth2.client.client-secret}") 
	private String clientSecret;
	
	/**
	 * @return the checkTokenUrl
	 */
	public String getCheckTokenUrl() {
		return checkTokenUrl;
	}
	/**
	 * @param checkTokenUrl the checkTokenUrl to set
	 */
	public void setCheckTokenUrl(String checkTokenUrl) {
		this.checkTokenUrl = checkTokenUrl;
	}
	/**
	 * @return the clientId
	 */
	public String getClientId() {
		return clientId;
	}
	/**
	 * @param clientId the clientId to set
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	/**
	 * @return the clientSecret
	 */
	public String getClientSecret() {
		return clientSecret;
	}
	/**
	 * @param clientSecret the clientSecret to set
	 */
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}
	/**
	 * @return the noOfScheduleUpcommingEvents
	 */
	public String getNoOfScheduleUpcommingEvents() {
		return noOfScheduleUpcommingEvents;
	}
	/**
	 * @param noOfScheduleUpcommingEvents the noOfScheduleUpcommingEvents to set
	 */
	public void setNoOfScheduleUpcommingEvents(String noOfScheduleUpcommingEvents) {
		this.noOfScheduleUpcommingEvents = noOfScheduleUpcommingEvents;
	}
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
