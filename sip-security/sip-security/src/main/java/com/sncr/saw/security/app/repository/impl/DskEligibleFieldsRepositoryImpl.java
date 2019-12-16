package com.sncr.saw.security.app.repository.impl;

import com.sncr.saw.security.app.model.DskEligibleFields;
import com.sncr.saw.security.app.repository.DskEligibleFieldsRepository;
import com.sncr.saw.security.common.bean.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DskEligibleFieldsRepositoryImpl implements DskEligibleFieldsRepository {

  private static final Logger logger = LoggerFactory
      .getLogger(DskEligibleFieldsRepositoryImpl.class);

  private JdbcTemplate jdbcTemplate;

  private static final int ACTIVE_STATUS = 1;

  @Autowired
  public DskEligibleFieldsRepositoryImpl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public Valid createDskEligibleFields(DskEligibleFields dskEligibleFields) {
    String sql = "INSERT INTO DSK_ELIGIBLE_FIELDS "
        + "(CUSTOMER_SYS_ID, PRODUCT_ID, SEMANTIC_ID, COLUMN_NAME, "
        + "DISPLAY_NAME , ACTIVE_STATUS_IND , CREATED_DATE , CREATED_BY)"
        + " VALUES (?,?,?,?,?,?,sysdate(),?)";
    Valid valid = new Valid();

    dskEligibleFields.getFields().forEach(
        dskField -> {
          try {
            jdbcTemplate.update(
                sql,
                ps -> {
                  ps.setLong(1, dskEligibleFields.getCustomerSysId());
                  ps.setLong(2, dskEligibleFields.getProductSysId());
                  ps.setString(3, dskEligibleFields.getSemantic_id());
                  ps.setString(4, dskField.getColumnName());
                  ps.setString(5, dskField.getDisplayName());
                  ps.setInt(6, ACTIVE_STATUS);
                  ps.setString(7, dskEligibleFields.getCreatedBy());
                });
            valid.setValid(Boolean.TRUE);
            valid.setValidityMessage("Success");
          } catch (DuplicateKeyException dke) {
            logger
                .error("Exception encountered while creating a new user " + dke.getMessage(), null,
                    dke);
            valid.setValid(Boolean.FALSE);
            valid.setValidityMessage(dke.getMessage());
          } catch (DataIntegrityViolationException de) {
            logger.error("Exception encountered while creating a new user " + de.getMessage(), null,
                de);
            valid.setValid(Boolean.FALSE);
            valid.setValidityMessage(de.getMessage());
          } catch (Exception e) {
            logger.error("Exception encountered while creating a new user " + e.getMessage(), null,
                e);
            valid.setValid(Boolean.FALSE);
            valid.setValidityMessage(e.getMessage());
          }
        }
    );
    return valid;
  }
}
