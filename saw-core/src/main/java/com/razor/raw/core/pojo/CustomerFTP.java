package com.razor.raw.core.pojo;

import java.io.Serializable;

public class CustomerFTP implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2111960395499164666L;
	private long customerFtpId;
	private String tenantId;
	private String productId;
	private String host;
	private String uName;
	private String password;
	private boolean sftpOn;
	private boolean ftpEnable;
	private String folder;
	private int port = 22;
	
	
	

	public CustomerFTP() {
	}

	public CustomerFTP(long customerFtpId, String tenantId, String productId, String host, int port, String uName, String password, String folder, boolean sftpOn,
			boolean ftpEnable) {
		this.customerFtpId = customerFtpId;
		this.tenantId = tenantId;
		this.productId =  productId;
		this.host = host;
		this.uName = uName;
		this.password = password;
		this.port = port;
		this.folder = folder;
		this.sftpOn = sftpOn;
		this.ftpEnable = ftpEnable;
	}
	
	public long getCustomerFtpId() {
		return customerFtpId;
	}

	public void setCustomerFtpId(long customerFtpId) {
		this.customerFtpId = customerFtpId;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getuName() {
		return uName;
	}

	public void setuName(String uName) {
		this.uName = uName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isSftpOn() {
		return sftpOn;
	}

	public void setSftpOn(boolean sftpOn) {
		this.sftpOn = sftpOn;
	}

	public boolean isFtpEnable() {
		return ftpEnable;
	}

	public void setFtpEnable(boolean ftpEnable) {
		this.ftpEnable = ftpEnable;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
