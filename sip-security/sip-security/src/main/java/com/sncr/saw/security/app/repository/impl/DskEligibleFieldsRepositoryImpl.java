package com.sncr.saw.security.app.repository.impl;

import com.sncr.saw.security.app.model.DskEligibleFields;
import com.sncr.saw.security.app.repository.DskEligibleFieldsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DskEligibleFieldsRepositoryImpl implements DskEligibleFieldsRepository {

  private static final Logger logger = LoggerFactory.getLogger(DskEligibleFieldsRepositoryImpl.class);
  private JdbcTemplate jdbcTemplate ;

  @Autowired
  public DskEligibleFieldsRepositoryImpl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public DskEligibleFields createDskEligibleFields(DskEligibleFields dskEligibleFields) {
    String sql = "INSERT INTO DSK_ELIGIBLE_FIELDS "
        + "(CUSTOMER_SYS_ID, PRODUCT_ID, SEMANTIC_ID, COLUMN_NAME, "
        + "DISPLAY_NAME , ACTIVE_STATUS_IND , CREATED_DATE , CREATED_BY )"
        + " VALUES (?,?,?,?,?,?,sysdate(),?)";

    try {
      jdbcTemplate.update(
          sql,
          preparedStatement -> {
            preparedStatement.setString(1, "roleName");
          });

    } catch (DuplicateKeyException e) {
      logger.error("Exception encountered while creating a new user " + e.getMessage(), null, e);
    } catch (DataIntegrityViolationException de) {
      logger.error("Exception encountered while creating a new user " + de.getMessage(), null, de);
    } catch (Exception e) {
      logger.error("Exception encountered while creating a new user " + e.getMessage(), null, e);
    }

    return null;
  }
}
