package com.sncr.saw.security.app.repository.impl;

import com.sncr.saw.security.app.repository.ProductModuleRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class ProductModuleRepositoryDaoImpl implements ProductModuleRepository {

  private static final Logger logger = LoggerFactory.getLogger(ProductModuleRepositoryDaoImpl.class);

  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public ProductModuleRepositoryDaoImpl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public List getProductModules() {
    String sql = "select a.PROD_MOD_SYS_ID AS MODULE_ID, b.PRODUCT_NAME AS PRODUCT_NAME, c.MODULE_NAME AS MODULE_NAME from product_modules a JOIN PRODUCTS b ON a.PRODUCT_SYS_ID = b.PRODUCT_SYS_ID JOIN MODULES c ON a.MODULE_SYS_ID = c.MODULE_SYS_ID";
    List rs = jdbcTemplate.queryForList(sql);
    return rs;
  }

  @Override
  public boolean checkProductModuleExistance(Long prodModId) {
    String sql = "select PROD_MOD_SYS_ID from product_modules where PROD_MOD_SYS_ID = " + prodModId;
    return !jdbcTemplate.queryForList(sql).isEmpty();
  }

  @Override
  public Map<Integer, String> createProductModuleLinkageForOnboarding() {
    Map<Integer, String> sqlstatements = new HashMap<Integer, String>();
    Map<Integer, String> results = new HashMap<Integer, String>();
    KeyHolder keyHolder = new GeneratedKeyHolder();

    sqlstatements.put(1,
        "INSERT INTO `PRODUCT_MODULES` (`PRODUCT_SYS_ID`,`MODULE_SYS_ID`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (1,2,1,?,'admin',NULL,'',NULL,'')");
    sqlstatements.put(2,
        "INSERT INTO `PRODUCT_MODULES` (`PRODUCT_SYS_ID`,`MODULE_SYS_ID`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (2,2,1,?,'admin',NULL,'',NULL,'')");
    sqlstatements.put(3,
        "INSERT INTO `PRODUCT_MODULES` (`PRODUCT_SYS_ID`,`MODULE_SYS_ID`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (3,2,1,?,'admin',NULL,'',NULL,'')");
    sqlstatements.put(4,
        "INSERT INTO `PRODUCT_MODULES` (`PRODUCT_SYS_ID`,`MODULE_SYS_ID`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (4,1,1,?,'admin',NULL,'',NULL,'')");
    sqlstatements.put(5,
        "INSERT INTO `PRODUCT_MODULES` (`PRODUCT_SYS_ID`,`MODULE_SYS_ID`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (5,2,1,?,'admin',NULL'',NULL,'')");
    sqlstatements.put(6,
        "INSERT INTO `PRODUCT_MODULES` (`PRODUCT_SYS_ID`,`MODULE_SYS_ID`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (1,1,1,?,'admin',NULL,'',NULL,'')");
    sqlstatements.put(7,
        "INSERT INTO `PRODUCT_MODULES` (`PRODUCT_SYS_ID`,`MODULE_SYS_ID`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (4,3,1,?,'admin',NULL,'',NULL,'')");
    sqlstatements.put(8,
        "INSERT INTO `PRODUCT_MODULES` (`PRODUCT_SYS_ID`,`MODULE_SYS_ID`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (4,2,1,?,'admin',NULL,'',NULL,'')");

    for (Map.Entry m : sqlstatements.entrySet()) {
      jdbcTemplate.update(con -> {
        PreparedStatement ps = con.prepareStatement(m.getValue().toString(), new String[]{"PROD_MOD_SYS_ID"});
        ps.setDate(1, new java.sql.Date(new Date().getTime()));
        return ps;
      }, keyHolder);
      results.put((Integer) m.getKey(), keyHolder.getKey().toString());
    }
    return results;
  }

  @Override
  public boolean validateModuleProductName(String productName, String moduleName, String loginId) {

    boolean validateModuleProductName = false;
    String sql = "select PR.PRODUCT_NAME, M.MODULE_NAME from PRODUCT_MODULES PM, CUSTOMER_PRODUCT_MODULES CPM, PRODUCTS PR, MODULES M, USERS U, CUSTOMERS C " +
        "WHERE U.CUSTOMER_SYS_ID=C.CUSTOMER_SYS_ID AND CPM.CUSTOMER_SYS_ID=C.CUSTOMER_SYS_ID " +
        "AND PR.PRODUCT_SYS_ID=PM.PRODUCT_SYS_ID AND M.MODULE_SYS_ID=PM.MODULE_SYS_ID AND CPM.PROD_MOD_SYS_ID=PM.PROD_MOD_SYS_ID " +
        "AND U.USER_ID = ?;";
    try {
      Map<String, String> productModeule = jdbcTemplate.query(sql, preparedStatement ->
              preparedStatement.setString(1, loginId)
          , new ProductModuleRepositoryDaoImpl.ProductModuleExtractor());

      // check if product and  name exists
      validateModuleProductName = productModeule != null && !productModeule.isEmpty()
          ? (productModeule.keySet().contains(productName) || productModeule.values().contains(productName)) : false;
    } catch (DataAccessException de) {
      logger.error("Exception encountered while accessing DB : " + de.getMessage());
    } catch (Exception e) {
      logger.error("Exception encountered while checking user Id :" + e.getMessage());
    }
    return validateModuleProductName;
  }

  private class ProductModuleExtractor implements ResultSetExtractor<Map<String, String>> {
    Map<String, String> values = new HashMap<>();
    @Override
    public Map<String, String> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
      while (resultSet.next()) {
        values.put(resultSet.getString("PRODUCT_NAME"), resultSet.getString("MODULE_NAME"));
      }
      return values;
    }
  }
}
