package com.razor.raw.core.dao.repository.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.razor.raw.core.common.exception.RAWDSException;
import com.razor.raw.core.dao.mapper.CustomerFTPMapper;
import com.razor.raw.core.dao.repository.CustomerFTPRepository;
import com.razor.raw.core.pojo.CustomerFTP;


/**
 * 
 * @author surendra.rajaneni
 *
 */
@Repository
public class CustomerFTPRepositoryImpl implements CustomerFTPRepository {
	private static final Logger logger = LoggerFactory
			.getLogger(CustomerFTPRepositoryImpl.class);
	private final JdbcTemplate jdbcTemplate;
	@Autowired
	public CustomerFTPRepositoryImpl(JdbcTemplate jdbc) {
		jdbcTemplate = jdbc;
	}
	
	public enum CustomerFTPCols {
		RAW_CUSTOMER_FTP_ID, TENANT_ID, PRODUCT_ID, IS_FTP_ENABLED, IS_SFTP_ON, FTP_HOST, FTP_PORT, FTP_USER, FTP_PASSWORD, FTP_FOLDER 
	}
	
	String sftpQuery = "select * FROM RAW_CUSTOMER_FTP where TENANT_ID = ? and PRODUCT_ID = ? ";
	@Override
	public List<CustomerFTP> getCustomerSftpData(String customerId,
			String productId) throws RAWDSException {
		logger.debug(this.getClass().getName() + " - getCustomerSftpData - START");
		logger.debug(this.getClass().getName() + "- getCustomerSftpData  -- Query : " + sftpQuery);
		List<CustomerFTP> listCustFtp = null;
		try {
			listCustFtp = jdbcTemplate.query(sftpQuery, new CustomerFTPMapper(), customerId, productId);
		} catch (Exception e) {
			logger.error(this.getClass().getName() + " - getIMCBanForAnalysis - Exception occured at Connection - ", e);
		}
		logger.debug(this.getClass().getName() + "- getCustomerSftpData - END");
		return listCustFtp;
	}
}
