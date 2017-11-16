package com.sncr.saw.security.app.repository.impl;

import com.sncr.saw.security.app.repository.CustomerProductModuleFeatureRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

public class CustomerProductModuleFeatureRepositoryDaoImpl implements
    CustomerProductModuleFeatureRepository {

  private static final Logger logger = LoggerFactory
      .getLogger(CustomerProductModuleFeatureRepositoryDaoImpl.class);
  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public CustomerProductModuleFeatureRepositoryDaoImpl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public Map<Integer, String> createCustomerProductModuleFeatureLinkageForOnboarding() {
    Map<Integer, String> sqlstatements = new HashMap<Integer, String>();
    Map<Integer, String> results = new HashMap<Integer, String>();
    KeyHolder keyHolder = new GeneratedKeyHolder();

    sqlstatements.put(1,
        "INSERT INTO `CUSTOMER_PRODUCT_MODULE_FEATURES` (`CUST_PROD_MOD_SYS_ID`,`DEFAULT_URL`,`FEATURE_NAME`,`FEATURE_DESC`,`FEATURE_CODE`,`FEATURE_TYPE`,`DEFAULT`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (1,'/','CANNED ANALYSIS','Canned Analysis','F0000000001','PARENT_F0000000001',1,1,?,'admin','','','','')");
    sqlstatements.put(2,
        "INSERT INTO `CUSTOMER_PRODUCT_MODULE_FEATURES` (`CUST_PROD_MOD_SYS_ID`,`DEFAULT_URL`,`FEATURE_NAME`,`FEATURE_DESC`,`FEATURE_CODE`,`FEATURE_TYPE`,`DEFAULT`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (1,'/','My Analysis','My Analysis','F0000000002','PARENT_F0000000002',0,1,?,'admin','','','','')");
    sqlstatements.put(3,
        "INSERT INTO `CUSTOMER_PRODUCT_MODULE_FEATURES` (`CUST_PROD_MOD_SYS_ID`,`DEFAULT_URL`,`FEATURE_NAME`,`FEATURE_DESC`,`FEATURE_CODE`,`FEATURE_TYPE`,`DEFAULT`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (1,'/','OPTIMIZATION','Optimization','F0000000003','CHILD_F0000000001',0,1,?,'admin','','','','')");
    sqlstatements.put(4,
        "INSERT INTO `CUSTOMER_PRODUCT_MODULE_FEATURES` (`CUST_PROD_MOD_SYS_ID`,`DEFAULT_URL`,`FEATURE_NAME`,`FEATURE_DESC`,`FEATURE_CODE`,`FEATURE_TYPE`,`DEFAULT`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (1,'/','DRAFTS','Drafts','F0000000004','CHILD_F0000000002',0,1,?,'admin','','','','')");

    for (Map.Entry m : sqlstatements.entrySet()) {
      jdbcTemplate.update(new PreparedStatementCreator() {
                            @Override
                            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                              PreparedStatement ps = con
                                  .prepareStatement(m.getValue().toString(),
                                      new String[]{"CUST_PROD_MOD_FEATURE_SYS_ID"});
                              ps.setDate(1, new java.sql.Date(new Date().getTime()));
                              return ps;
                            }
                          },
          keyHolder
      );
      results.put((Integer) m.getKey(), keyHolder.toString());
    }

    return results;
  }
}
