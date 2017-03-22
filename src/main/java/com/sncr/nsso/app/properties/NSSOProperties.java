package com.sncr.nsso.app.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;


/**
 * Configuration properties for new authentication component
 *
 * @author girija.sankar
 * 
 */

@Configuration
@RefreshScope
public class NSSOProperties {

	@Value("${ticket.validity.mins}")
	private String validityMins;

	@Value("${mail.host}")
	private String mailHost;
	
	
	@Value("${mail.port}")
	private int mailPort;
	
	@Value("${mail.from}")
	private String mailFrom;
	
	@Value("${mail.subject}")
	private String mailSubject;
	
	
	@Value("${mail.username}")
	private String mailUserName;
	
	
	@Value("${mail.password}")
	private byte[] mailPassword;
	
	
	
	/**
	 * @return the validityMins
	 */
	public String getValidityMins() {
		return validityMins;
	}

	/**
	 * @param validityMins the validityMins to set
	 */
	public void setValidityMins(String validityMins) {
		this.validityMins = validityMins;
	}

	/**
	 * @return the mailHost
	 */
	public String getMailHost() {
		return mailHost;
	}

	/**
	 * @param mailHost the mailHost to set
	 */
	public void setMailHost(String mailHost) {
		this.mailHost = mailHost;
	}

	

	/**
	 * @return the mailPort
	 */
	public int getMailPort() {
		return mailPort;
	}

	/**
	 * @param mailPort the mailPort to set
	 */
	public void setMailPort(int mailPort) {
		this.mailPort = mailPort;
	}

	/**
	 * @return the mailFrom
	 */
	public String getMailFrom() {
		return mailFrom;
	}

	/**
	 * @param mailFrom the mailFrom to set
	 */
	public void setMailFrom(String mailFrom) {
		this.mailFrom = mailFrom;
	}

	/**
	 * @return the mailSubject
	 */
	public String getMailSubject() {
		return mailSubject;
	}

	/**
	 * @param mailSubject the mailSubject to set
	 */
	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}

	public String getMailUserName() {
		return mailUserName;
	}

	public void setMailUserName(String mailUserName) {
		this.mailUserName = mailUserName;
	}

	public byte[] getMailPassword() {
		return mailPassword;
	}

	public void setMailPassword(byte[] mailPassword) {
		this.mailPassword = mailPassword;
	}

	
}
