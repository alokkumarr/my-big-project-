package com.sncr.saw.security.app.repository.impl;

import com.sncr.saw.security.app.repository.ProductRepository;
import com.sncr.saw.security.common.bean.Product;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Repository
public class ProductRepositoryDaoImpl implements ProductRepository {

  private static final Logger logger = LoggerFactory.getLogger(ProductRepositoryDaoImpl.class);

  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public ProductRepositoryDaoImpl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void displayProducts() {
    String sql = "select PRODUCT_SYS_ID AS PRODUCT_ID, PRODUCT_NAME AS PRODUCT_NAME from PRODUCTS";
    Map<Integer, String> results = new HashMap<Integer, String>();
    List rs = jdbcTemplate.queryForList(sql);
    for (Object x:rs) {
      System.out.println(x);
    }
  }


  @Override
  public boolean checkProductExistance(Long prodId) {
    String sql = "select PRODUCT_SYS_ID from products where PRODUCT_SYS_ID = " + prodId;
    return !jdbcTemplate.queryForList(sql).isEmpty();
  }

  @Override
  public Map<Integer, String> createProductForOnboarding() {

    Map<Integer, String> sqlstatements = new HashMap<Integer, String>();
    Map<Integer, String> results = new HashMap<Integer, String>();

    KeyHolder keyHolder = new GeneratedKeyHolder();

    // MAP because #4 is the only product for which we need to give access
    sqlstatements.put(1,
        "INSERT INTO `PRODUCTS` (`PRODUCT_NAME`,`PRODUCT_CODE`,`PRODUCT_DESC`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES ('MCT Insights','MCTI000001','MCT Insights',1,?,'admin','','','','')");
    sqlstatements.put(2,
        "INSERT INTO `PRODUCTS` (`PRODUCT_NAME`,`PRODUCT_CODE`,`PRODUCT_DESC`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES ('SnT Insighjts','SNT0000001','SnT Insighjts',1,?,'admin','','','','')");
    sqlstatements.put(3,
        "INSERT INTO `PRODUCTS` (`PRODUCT_NAME`,`PRODUCT_CODE`,`PRODUCT_DESC`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES ('Smart Care Insights','SCI0000001','Smart Care Insights',1,?,'admin','','','','')");
    sqlstatements.put(4,
        "INSERT INTO `PRODUCTS` (`PRODUCT_NAME`,`PRODUCT_CODE`,`PRODUCT_DESC`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES ('SAW Demo','SAWD000001','SAW Demo',1,?,'admin','','','','')");
    sqlstatements.put(5,
        "INSERT INTO `PRODUCTS` (`PRODUCT_NAME`,`PRODUCT_CODE`,`PRODUCT_DESC`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES ('Channel Insights','CI00000001','Channel Insights',1,?,'admin','','','','')");

    for (Map.Entry m : sqlstatements.entrySet()) {
      jdbcTemplate.update(new PreparedStatementCreator() {
                            @Override
                            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                              PreparedStatement ps = con
                                  .prepareStatement(m.getValue().toString(), new String[]{"PRODUCT_SYS_ID"});
                              ps.setDate(1, new java.sql.Date(new Date().getTime()));
                              return ps;
                            }
                          },
          keyHolder
      );
      results.put((Integer) m.getKey(), keyHolder.getKey().toString());
    }
    // #4 is the required one for pointing to demo app.
    return results;
  }

  @Override
  public boolean updateProduct(Product prod) {
    return false;
  }

  @Override
  public boolean deleteProduct(Long prodId) {
    return false;
  }

  @Override
  public Product getProduct(Long prodId) {
    return null;
  }
}
