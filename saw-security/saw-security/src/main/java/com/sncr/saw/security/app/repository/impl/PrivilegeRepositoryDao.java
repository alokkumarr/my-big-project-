package com.sncr.saw.security.app.repository.impl;

import com.sncr.saw.security.app.repository.PrivilegeRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class PrivilegeRepositoryDao implements PrivilegeRepository {

  private static final Logger logger = LoggerFactory.getLogger(PrivilegeRepositoryDao.class);

  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public PrivilegeRepositoryDao(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public long createNewPrivilegeDao(Long custProdSysId, Long custProdModSysId, Long custProdModFeatureSysId, Long roleSysId) {
    try {
      String sql ="INSERT INTO PRIVILEGES(CUST_PROD_SYS_ID, CUST_PROD_MOD_SYS_ID, CUST_PROD_MOD_FEATURE_SYS_ID,"
          + "ROLE_SYS_ID, ANALYSIS_SYS_ID, PRIVILEGE_CODE, PRIVILEGE_DESC, ACTIVE_STATUS_IND, CREATED_DATE,"
          + "CREATED_BY) VALUES(?,?,?,?,?,?,?,?,?,?)";
      KeyHolder keyHolder = new GeneratedKeyHolder();

      jdbcTemplate.update(
          new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
              PreparedStatement ps = con.prepareStatement(sql, new String[]{"PRIVILEGE_SYS_ID"});

              ps.setLong(1,custProdSysId);
              ps.setLong(2, custProdModSysId);
              ps.setLong(3,custProdModFeatureSysId);
              ps.setLong(4, roleSysId);
              ps.setLong(5,0L);
              ps.setString(6, "128");
              ps.setString(7, "All");
              ps.setInt(8, 1);
              ps.setDate(9, new java.sql.Date(new Date().getTime()));
              ps.setString(10,"admin");
              return ps;
            }
          },
          keyHolder
      );

      return (Long) keyHolder.getKey();
    } catch (Exception e) {
      logger.error(e.toString());
      return -1L;
    }
  }

  @Override
  public void displayPrivileges() {
    String sql = "select PRIVILEGE_SYS_ID, CUST_PROD_SYS_ID, CUST_PROD_MOD_SYS_ID, CUST_PROD_MOD_FEATURE_SYS_ID,"
        + "ROLE_SYS_ID, ANALYSIS_SYS_ID, PRIVILEGE_CODE, PRIVILEGE_DESC, ACTIVE_STATUS_IND, CREATED_DATE, "
        + "CREATED_BY, INACTIVATED_DATE, INACTIVATED_BY, MODIFIED_DATE, MODIFIED_BY from PRIVILEGES";
    List rs = jdbcTemplate.queryForList(sql);
    for (Object x:rs) {
      System.out.println(x);
    }

  }
}
