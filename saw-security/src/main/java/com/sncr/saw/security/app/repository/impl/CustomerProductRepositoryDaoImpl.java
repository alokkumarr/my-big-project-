package com.sncr.saw.security.app.repository.impl;

import com.sncr.saw.security.app.repository.CustomerProductRepository;
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
import org.springframework.stereotype.Repository;

@Repository
public class CustomerProductRepositoryDaoImpl implements CustomerProductRepository {


  private static final Logger logger = LoggerFactory
      .getLogger(CustomerProductRepositoryDaoImpl.class);
  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public CustomerProductRepositoryDaoImpl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }


  @Override
  public Map<Integer, String> createCustomerProductLinkageForOnboarding() {
    Map<Integer, String> sqlstatements = new HashMap<Integer, String>();
    Map<Integer, String> results = new HashMap<Integer, String>();
    KeyHolder keyHolder = new GeneratedKeyHolder();

    sqlstatements.put(1,
        "INSERT INTO `CUSTOMER_PRODUCTS` (`CUSTOMER_SYS_ID`,`PRODUCT_SYS_ID`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (1,4,1,?,'admin','','','','')");
    for (Map.Entry m : sqlstatements.entrySet()) {
      jdbcTemplate.update(new PreparedStatementCreator() {
                            @Override
                            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                              PreparedStatement ps = con
                                  .prepareStatement(m.getValue().toString(), new String[]{"CUST_PROD_SYS_ID"});
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
