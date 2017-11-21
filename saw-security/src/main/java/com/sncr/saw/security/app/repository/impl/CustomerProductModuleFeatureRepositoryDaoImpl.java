package com.sncr.saw.security.app.repository.impl;

import com.sncr.saw.security.app.repository.CustomerProductModuleFeatureRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
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
  public Map<Integer, String> createCustomerProductModuleFeatureLinkageForOnboarding(Long custProdModId) {
    Map<Integer, String> sqlstatements = new HashMap<Integer, String>();
    Map<Integer, String> results = new HashMap<Integer, String>();
    KeyHolder keyHolder = new GeneratedKeyHolder();

    sqlstatements.put(1,
        "INSERT INTO `CUSTOMER_PRODUCT_MODULE_FEATURES` (`CUST_PROD_MOD_SYS_ID`,`DEFAULT_URL`,"
            + "`FEATURE_NAME`,`FEATURE_DESC`,`FEATURE_CODE`,`FEATURE_TYPE`,`DEFAULT`,`ACTIVE_STATUS_IND`,"
            + "`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,"
            + "`MODIFIED_BY`) VALUES (?,'/','CANNED ANALYSIS','Canned Analysis',?,"
            + "?,1,1,?,'admin',NULL,'',NULL,'')");
    sqlstatements.put(2,
        "INSERT INTO `CUSTOMER_PRODUCT_MODULE_FEATURES` (`CUST_PROD_MOD_SYS_ID`,`DEFAULT_URL`,"
            + "`FEATURE_NAME`,`FEATURE_DESC`,`FEATURE_CODE`,`FEATURE_TYPE`,`DEFAULT`,`ACTIVE_STATUS_IND`,"
            + "`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,"
              + "`MODIFIED_BY`) VALUES (?,'/','My Analysis','My Analysis',?,"
            + "?,0,1,?,'admin',NULL,'',NULL,'')");
    for (Map.Entry m : sqlstatements.entrySet()) {

      // first retrieve the max Id of this table so as to set unique code.
      String sql = "select max(CUST_PROD_MOD_FEATURE_SYS_ID) a from CUSTOMER_PRODUCT_MODULE_FEATURES";
      List<Map<String, Object>> max_val_list = jdbcTemplate.queryForList(sql);
      Long max_val = Long.parseLong(max_val_list.get(0).get("a").toString());

      jdbcTemplate.update(new PreparedStatementCreator() {
                            @Override
                            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                              PreparedStatement ps = con
                                  .prepareStatement(m.getValue().toString(),
                                      new String[]{"CUST_PROD_MOD_FEATURE_SYS_ID"});

                              ps.setLong(1, custProdModId);
                              ps.setString(2, String.format("F%010d", max_val+1));
                              ps.setString(3, String.format("PARENT_F%010d", max_val+1));
                              ps.setDate(4, new java.sql.Date(new Date().getTime()));
                              return ps;
                            }
                          },
          keyHolder
      );
      results.put((Integer) m.getKey(), keyHolder.getKey().toString());
    }

    return results;
  }
}
