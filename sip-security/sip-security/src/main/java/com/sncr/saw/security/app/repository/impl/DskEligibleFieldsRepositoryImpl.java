package com.sncr.saw.security.app.repository.impl;

import com.sncr.saw.security.app.model.DskEligibleFields;
import com.sncr.saw.security.app.model.DskField;
import com.sncr.saw.security.app.model.DskFieldsInfo;
import com.sncr.saw.security.app.repository.DskEligibleFieldsRepository;
import com.sncr.saw.security.common.bean.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    String sql =
        "INSERT INTO DSK_ELIGIBLE_FIELDS "
            + "(CUSTOMER_SYS_ID, PRODUCT_ID, SEMANTIC_ID, COLUMN_NAME, "
            + "DISPLAY_NAME , ACTIVE_STATUS_IND , CREATED_TIME , CREATED_BY)"
            + " VALUES (?,?,?,?,?,?,sysdate(),?)";
    Valid valid = new Valid();

    try {
      dskEligibleFields
          .getFields()
          .forEach(
              dskField -> {
                jdbcTemplate.update(
                    sql,
                    ps -> {
                      String columnName = dskField.getColumnName();

                      if (columnName == null
                          || columnName.length() == 0
                          || columnName.trim().contains(" ")) {
                        throw new DataIntegrityViolationException("Column name cannot be empty");
                      }

                      ps.setLong(1, dskEligibleFields.getCustomerSysId());
                      ps.setLong(2, dskEligibleFields.getProductSysId());
                      ps.setString(3, dskEligibleFields.getSemanticId());
                      ps.setString(4, dskField.getColumnName().trim());
                      ps.setString(5, dskField.getDisplayName());
                      ps.setInt(6, ACTIVE_STATUS);
                      ps.setString(7, dskEligibleFields.getCreatedBy());
                    });
                valid.setValid(Boolean.TRUE);
                valid.setValidityMessage("Success");
              });
    } catch (DuplicateKeyException dke) {
      logger.error(
          "Exception encountered while adding dsk eligible fields " + dke.getMessage(), dke);
      valid.setValid(Boolean.FALSE);
      valid.setValidityMessage(dke.getMessage());
    } catch (DataIntegrityViolationException de) {
      logger.error("Exception encountered while adding dsk eligible fields " + de.getMessage(), de);
      valid.setValid(Boolean.FALSE);
      valid.setValidityMessage(de.getMessage());
    } catch (Exception e) {
      logger.error("Exception encountered while adding dsk eligible fields " + e.getMessage(), e);
      valid.setValid(Boolean.FALSE);
      valid.setValidityMessage(e.getMessage());
    }
    return valid;
  }

  @Override
  public Valid updateDskFields(DskEligibleFields dskEligibleFields) {
    Valid valid;
    Long customerSysId = dskEligibleFields.getCustomerSysId();
    Long productId = dskEligibleFields.getProductSysId();
    String semanticId = dskEligibleFields.getSemanticId();

    valid = deleteDskEligibleFields(customerSysId, productId, semanticId);

    if (valid.getValid()) {
      valid = createDskEligibleFields(dskEligibleFields);
    }
    return valid;
  }

  @Override
  public DskFieldsInfo fetchAllDskEligibleFields(Long customerSysId, Long defaultProdID) {
    String tableName = "DSK_ELIGIBLE_FIELDS";

    String fetchQuery =
        "SELECT CUSTOMER_SYS_ID, PRODUCT_ID, SEMANTIC_ID, COLUMN_NAME,"
            + " DISPLAY_NAME FROM " + tableName
            + " WHERE CUSTOMER_SYS_ID=? AND PRODUCT_ID=? AND ACTIVE_STATUS_IND=1";

    DskFieldsInfo dskFieldsInfo = new DskFieldsInfo();

    Map<Long, Map<Long, Map<String, List<DskField>>>> dskEligibleFields =
        jdbcTemplate.query(
            fetchQuery,
            ps -> {
              ps.setLong(1, customerSysId);
              ps.setLong(2, defaultProdID);
            },
            resultSet -> {
              Map<Long, Map<Long, Map<String, List<DskField>>>> dskEligibleData =
                  dskFieldsInfo.getDskEligibleData();
              while (resultSet.next()) {
                String semanticId = resultSet.getString("SEMANTIC_ID");

                String columnName = resultSet.getString("COLUMN_NAME");
                String displayName = resultSet.getString("DISPLAY_NAME");

                logger.info("" + semanticId + " " + columnName + " " + displayName);

                Map<Long, Map<String, List<DskField>>> customerDskFields =
                    dskEligibleData.getOrDefault(customerSysId, new HashMap<>());

                Map<String, List<DskField>> projectDskData =
                    customerDskFields.getOrDefault(defaultProdID, new HashMap<>());

                List<DskField> semanticDskFields =
                    projectDskData.getOrDefault(semanticId, new ArrayList<>());

                DskField dskField = new DskField();
                dskField.setColumnName(columnName);
                dskField.setDisplayName(displayName);

                semanticDskFields.add(dskField);

                logger.debug("" + semanticDskFields);
                projectDskData.put(semanticId, semanticDskFields);

                customerDskFields.put(defaultProdID, projectDskData);

                dskEligibleData.put(customerSysId, customerDskFields);
              }
              return dskEligibleData;
            });
    return dskFieldsInfo;
  }

  @Override
  public Valid deleteDskEligibleFields(Long custId, Long prodId, String semanticId) {
    String sql = "DELETE FROM DSK_ELIGIBLE_FIELDS "
        + " WHERE SEMANTIC_ID = ? AND PRODUCT_ID= ? AND CUSTOMER_SYS_ID = ?";
    Valid valid = new Valid();
    try {
      jdbcTemplate.update(sql, ps -> {
        ps.setString(1, semanticId);
        ps.setLong(2, prodId);
        ps.setLong(3, custId);
      });
      valid.setValid(Boolean.TRUE);
      valid.setValidityMessage("Success");
    } catch (DataIntegrityViolationException de) {
      logger.error("Exception encountered while deleting dsk " + de.getMessage(), de);
      valid.setValid(Boolean.FALSE);
      valid.setValidityMessage(de.getMessage());
    } catch (Exception e) {
      logger.error("Exception encountered while creating a new user " + e.getMessage(), e);
      valid.setValid(Boolean.FALSE);
      valid.setValidityMessage(e.getMessage());
    }
    return valid;
  }
}
