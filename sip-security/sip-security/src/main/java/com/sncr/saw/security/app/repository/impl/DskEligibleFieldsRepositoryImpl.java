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
    String sql = "INSERT INTO DSK_ELIGIBLE_FIELDS "
        + "(CUSTOMER_SYS_ID, PRODUCT_ID, SEMANTIC_ID, COLUMN_NAME, "
        + "DISPLAY_NAME , ACTIVE_STATUS_IND , CREATED_TIME , CREATED_BY)"
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
                  ps.setString(3, dskEligibleFields.getSemanticId());
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

    public Valid updateDskFields(Long customerSysId, Long productId,
        String semanticId, List<DskField> dskFields)
    {
        Valid valid;
        valid = deleteDskEligibleFields(customerSysId, productId, semanticId);

        if (valid.getValid()) {
            String insertDsk = "INSERT INTO DSK_ELIGIBLE_FIELDS "
                + "(CUSTOMER_SYS_ID, PRODUCT_ID, SEMANTIC_ID, COLUMN_NAME, "
                + "DISPLAY_NAME , ACTIVE_STATUS_IND , CREATED_TIME , CREATED_BY,"
                + " MODIFIED_TIME, MODIFIED_BY)"
                + " VALUES (?,?,?,?,?,?,sysdate(),?, sysdate(), ?)";

            dskFields.forEach(
                dskField -> {
                    try {
                        jdbcTemplate.update(
                            insertDsk,
                            ps -> {
                                ps.setLong(1, customerSysId);
                                ps.setLong(2, productId);
                                ps.setString(3, semanticId);
                                ps.setString(4, dskField.getColumnName());
                                ps.setString(5, dskField.getDisplayName());
                                ps.setInt(6, ACTIVE_STATUS);
                                //TODO: Change default user to valid user
                                ps.setString(7, "default user");
                                ps.setString(8, "default user");
                            });
                        valid.setValid(Boolean.TRUE);
                        valid.setValidityMessage("Success");
                    } catch (Exception e) {
                        logger.error("Exception encountered while update DSK " + e.getMessage(), null,
                            e);
                        valid.setValid(Boolean.FALSE);
                        valid.setValidityMessage(e.getMessage());
                    }
                }
            );
            return valid;
        }


        return valid;
    }

  @Override
  public DskFieldsInfo fetchAllDskEligibleFields(Long customerSysId, Long defaultProdID) {
    String tableName = "dsk_eligible_fields";

    String fetchQuery =
        "SELECT CUSTOMER_SYS_ID, PRODUCT_ID, SEMANTIC_ID, COLUMN_NAME,"
            + " DISPLAY_NAME FROM "
            + tableName
            + " WHERE CUSTOMER_SYS_ID=? AND PRODUCT_ID=? AND ACTIVE_STATUS_IND=?";

    DskFieldsInfo dskFieldsInfo = new DskFieldsInfo();

    Map<Long, Map<Long, Map<String, List<DskField>>>> dskEligibleFields =
        jdbcTemplate.query(
            fetchQuery,
            ps -> {
              ps.setLong(1, customerSysId);
              ps.setLong(2, defaultProdID);
              ps.setShort(3, (short)1);
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
