package com.razor.raw.core.dao.repository;

import java.util.List;

import com.razor.raw.core.common.exception.RAWDSException;
import com.razor.raw.core.pojo.CustomerFTP;
/**
 * 
 * @author surendra.rajaneni
 *
 */
public interface CustomerFTPRepository {
	/**
	 * 
	 * @param reportDefinationImpl
	 * @param recordLimit
	 * @param connection
	 * @return
	 */
	public List<CustomerFTP> getCustomerSftpData(String customerId, String productId) throws RAWDSException;
}
