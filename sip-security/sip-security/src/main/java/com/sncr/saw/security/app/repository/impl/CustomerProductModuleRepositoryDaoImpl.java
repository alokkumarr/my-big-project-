package com.sncr.saw.security.app.repository.impl;

import com.sncr.saw.security.app.repository.CustomerProductModuleRepository;
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
public class CustomerProductModuleRepositoryDaoImpl implements CustomerProductModuleRepository {


  private static final Logger logger = LoggerFactory
      .getLogger(CustomerProductModuleRepositoryDaoImpl.class);
  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public CustomerProductModuleRepositoryDaoImpl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void displayCustProdModules(Long custId) {
    String sql = "SELECT a.CUST_PROD_MOD_SYS_ID, a.CUST_PROD_SYS_ID, "
        + "a.PROD_MOD_SYS_ID, a.CUSTOMER_SYS_ID, d.PRODUCT_NAME, c.MODULE_NAME "
        + "from customer_product_modules a JOIN product_modules b ON a.PROD_MOD_SYS_ID = b.PROD_MOD_SYS_ID "
        + "JOIN modules c ON b.MODULE_SYS_ID = c.MODULE_SYS_ID "
        + "JOIN products d ON b.PRODUCT_SYS_ID = d.PRODUCT_SYS_ID "
        + "WHERE a.CUSTOMER_SYS_ID = " + custId;
    List rs = jdbcTemplate.queryForList(sql);
    for (Object x : rs) {
      System.out.println(x);
    }
  }

  @Override
  public boolean checkCustProdModExistance(Long prodId, Long custId) {
    String sql =
        "select CUST_PROD_MOD_SYS_ID from customer_product_modules where CUST_PROD_MOD_SYS_ID = "
            + prodId + " AND CUSTOMER_SYS_ID = " + custId;
    return !jdbcTemplate.queryForList(sql).isEmpty();
  }

  @Override
  public Map<Integer, String> createCustomerProductModuleLinkageForOnboarding(
      Long custProdId, Long prodModId, Long custId) {
    Map<Integer, String> sqlstatements = new HashMap<Integer, String>();
    Map<Integer, String> results = new HashMap<Integer, String>();
    KeyHolder keyHolder = new GeneratedKeyHolder();

    sqlstatements.put(1,
        "INSERT INTO `CUSTOMER_PRODUCT_MODULES` (`CUST_PROD_SYS_ID`,`PROD_MOD_SYS_ID`,"
            + "`CUSTOMER_SYS_ID`,`ACTIVE_STATUS_IND`,`MODULE_URL`,`DEFAULT`,`CREATED_DATE`,"
            + "`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) "
            + "VALUES (?,?,?,1,'/',1,?,'admin',NULL,'',NULL,'')");
    for (Map.Entry m : sqlstatements.entrySet()) {
      jdbcTemplate.update(new PreparedStatementCreator() {
                            @Override
                            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                              PreparedStatement ps = con
                                  .prepareStatement(m.getValue().toString(), new String[]{"CUST_PROD_MOD_SYS_ID"});
                              ps.setLong(1, custProdId);
                              ps.setLong(2, prodModId);
                              ps.setLong(3, custId);
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
