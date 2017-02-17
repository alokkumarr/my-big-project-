package com.razor.raw.core.dao.repository;

import java.util.List;

import com.razor.raw.core.common.exception.RAWDSException;
import com.razor.raw.core.pojo.Datasource;

/**
 * 
 * @author surendra.rajaneni
 *
 */
public interface DataSourceRepository {
	/**
	 * get All data sources from RIR 
	 * @return
	 */
	List<Datasource> getAllDataSources() throws RAWDSException;
	
	/**
	 * get Data source by tenantId and productId
	 * @param tenantId
	 * @param productId
	 * @return
	 */
	Datasource getDataSourec(String tenentId,String productId) throws RAWDSException;
}
