package com.razor.raw.core.dao.repository.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.razor.raw.core.common.exception.RAWDSException;
import com.razor.raw.core.dao.mapper.DataSourceMapper;
import com.razor.raw.core.dao.repository.DataSourceRepository;
import com.razor.raw.core.pojo.Datasource;

/**
 * 
 * @author surendra.rajaneni
 *
 */
@Repository
public class DataSourceRepositoryImpl implements DataSourceRepository {
	private static final Logger logger = LoggerFactory
			.getLogger(DataSourceRepositoryImpl.class);
	private final JdbcTemplate jdbcTemplate;
	@Autowired
	public DataSourceRepositoryImpl(JdbcTemplate jdbc) {
		jdbcTemplate = jdbc;
	}
	
	private enum dataSourceCols {
		RIR_DATA_SOURCE_ID, TENANT_ID, PRODUCT_ID, DBTYPE, SERVER_NAME, DB_NAME, SCHEMA_NAME, USER_NAME, PASSWORD, PORT
	};
	
	String allDsQuery = "select RAW_DATA_SOURCE_ID,TENANT_ID,PRODUCT_ID,DBTYPE,SERVER_NAME,DB_NAME,SCHEMA_NAME,USER_NAME,PASSWORD, PORT , CONNECTION_TYPE from RAW_DATA_SOURCE";
	String customerDataSoure = "select RAW_DATA_SOURCE_ID, TENANT_ID, PRODUCT_ID, DBTYPE, SERVER_NAME, DB_NAME, SCHEMA_NAME, USER_NAME, PASSWORD, PORT ,CONNECTION_TYPE from RAW_DATA_SOURCE where TENANT_ID = ? and PRODUCT_ID = ?";
	@Override
	public List<Datasource> getAllDataSources() throws RAWDSException {
		logger.debug(this.getClass().getName() + " - getAllDataSources - START");
		logger.debug(this.getClass().getName() + "- getAllDataSources  -- Query : " + allDsQuery);
		List<Datasource> listDatasources = null;
		try {
			listDatasources = jdbcTemplate.query(allDsQuery, new DataSourceMapper());
		} catch (Exception e) {
			logger.error("Exception occured at + "+ this.getClass().getName() + "in getAllDataSources ", e);
		}
		logger.debug(this.getClass().getName() + " - getAllDataSources - END");
		return listDatasources;
	}
	@Override
	public Datasource getDataSourec(String tenentId, String productId) throws RAWDSException {
		logger.debug(this.getClass().getName() + "- getDataSourec  -- Query : " + customerDataSoure);
		Datasource datasource = null;
		try {
			List<Datasource> dataSources = jdbcTemplate.query(customerDataSoure, new DataSourceMapper(), tenentId, productId);
			if(dataSources != null && dataSources.size() > 0){
				datasource = dataSources.get(0);
			}
		} catch (Exception e) {
			logger.error("Exception occured at + "+ this.getClass().getName() + "in getDataSourec ", e);
		}
		logger.debug(this.getClass().getName() + " - getDataSourec - END");
		return datasource;
	}
}
