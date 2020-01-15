package com.sncr.saw.security.app.repository.impl;

import com.sncr.saw.security.app.repository.CustomerRepository;
import com.sncr.saw.security.app.model.Customer;

import com.sncr.saw.security.app.repository.impl.extract.BrandDetailsExtractor;
import com.sncr.saw.security.common.bean.Valid;
import com.sncr.saw.security.common.bean.repo.BrandDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pawan
 */
@Repository
public class CustomerRepositoryDaoImpl implements CustomerRepository {

  private static final Logger logger = LoggerFactory.getLogger(CustomerRepositoryDaoImpl.class);

  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public CustomerRepositoryDaoImpl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public boolean isValidCustCode(String custCode) {
    try {
      if (custCode == null || custCode.trim().isEmpty()) {
        return false;
      }
      Pattern p = Pattern.compile("[^A-Za-z0-9]");
      Matcher m = p.matcher(custCode);
      boolean b = m.find();
      if (!b) {
        return true;
      }
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    return false;
  }

  @Override
  public long createNewCustomerDao(Customer cust) {
    if (cust != null) {
      String sql =
          "INSERT INTO customers(CUSTOMER_CODE, COMPANY_NAME, COMPANY_BUSINESS, LANDING_PROD_SYS_ID,"
              +
              " ACTIVE_STATUS_IND, CREATED_DATE, CREATED_BY, INACTIVATED_DATE, INACTIVATED_BY, MODIFIED_DATE,"
              +
              "MODIFIED_BY, PASSWORD_EXPIRY_DAYS, DOMAIN_NAME) VALUES(?,?,?,?,?,?,?,NULL,'',NULL,'',?,?)";
      KeyHolder keyHolder = new GeneratedKeyHolder();

      jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql,
                new String[]{"CUSTOMER_SYS_ID"});
            ps.setString(1, cust.getCustCode());
            ps.setString(2, cust.getCompanyName());
            ps.setString(3, cust.getCompanyBusiness());
            ps.setLong(4, cust.getLandingProdSysId());
            ps.setInt(5, cust.getActiveStatusInd());
            ps.setDate(6, new java.sql.Date(new Date().getTime()));
            ps.setString(7, cust.getCreatedBy());
            ps.setLong(8, cust.getPasswordExpiryDate());
            ps.setString(9, cust.getDomainName());
            return ps;
          },
          keyHolder
      );

      return (Long) keyHolder.getKey();
    }
    return -1L;
  }

  @Override
  public Valid upsertCustomerBrand(Long customerId, String brandColor, String fileLocation) {
    String sql = "UPDATE CUSTOMERS SET BRAND_COLOR = ?, BRAND_LOGO = ? " +
        "WHERE CUSTOMER_SYS_ID = ?";
    Valid valid = new Valid();
    try {
      int updated = jdbcTemplate.update(sql, preparedStatement -> {
        preparedStatement.setString(1, brandColor);
        preparedStatement.setString(2, fileLocation);
        preparedStatement.setLong(3, customerId);
      });
      if (updated > 0) {
        valid.setValidityMessage("Brand upserted successfully.");
        valid.setValid(true);
      }
    } catch (DataAccessException ex) {
      logger.error("Exception occured during the branding upsert.");
      valid.setValidityMessage("Exception occured during the branding upsert.");
      valid.setValid(false);
    }
    return valid;
  }

  @Override
  public BrandDetails fetchCustomerBrand(Long customerId) {
    String sql = "SELECT BRAND_COLOR, BRAND_LOGO FROM CUSTOMERS WHERE CUSTOMER_SYS_ID = ?;";
    try {
      return jdbcTemplate.query(sql, ps -> ps.setLong(1, customerId),
          new BrandDetailsExtractor());
    } catch (DataAccessException ex) {
      logger.error("Exception occured during the branding upsert.");
    }
    return null;
  }
}
