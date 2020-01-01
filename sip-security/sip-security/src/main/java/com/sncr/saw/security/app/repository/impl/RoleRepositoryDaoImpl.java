package com.sncr.saw.security.app.repository.impl;

import com.sncr.saw.security.app.repository.RoleRepository;
import com.sncr.saw.security.app.repository.impl.extract.RoleDetailsExtractor;
import com.sncr.saw.security.common.bean.Role;
import com.sncr.saw.security.common.bean.repo.admin.role.RoleDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.Date;

@Repository
public class RoleRepositoryDaoImpl implements RoleRepository {

  private static final Logger logger = LoggerFactory.getLogger(RoleRepositoryDaoImpl.class);

  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public RoleRepositoryDaoImpl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  // we decided that we will initially create only admin user

  @Override
  public long createNewAdminRoleDao(Long customerId) {
    if (customerId != null) {
      String sql =
          "INSERT INTO `ROLES` (`CUSTOMER_SYS_ID`, `ROLE_NAME`, `ROLE_CODE`, `ROLE_DESC`," +
              "`ROLE_TYPE`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`)" +
              " VALUES (?, 'ADMIN', ?, 'Admin User', 'ADMIN', '1'," +
              "?, 'admin')";

      KeyHolder keyHolder = new GeneratedKeyHolder();
      jdbcTemplate.update(
          con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"ROLE_SYS_ID"});
            ps.setLong(1, customerId);
            ps.setString(2, customerId.toString() + "_ADMIN_USER");
            ps.setDate(3, new java.sql.Date(new Date().getTime()));
            return ps;
          },
          keyHolder
      );
      return (Long) keyHolder.getKey();
    }
    return -1L;
  }


  @Override
  public RoleDetails fetchRoleByIdAndCustomerCode(Long customerSysId, Role role) {
    String sql = "select R.ROLE_SYS_ID, R.ROLE_NAME, R.ACTIVE_STATUS_IND from ROLES R where R.CUSTOMER_SYS_ID =? AND ROLE_CODE = ? AND ROLE_TYPE = ?";
    RoleDetails roleDetails = new RoleDetails();
    try {
      final StringBuffer roleCode = new StringBuffer();
      roleCode.append(role.getCustomerCode()).append("_");
      if (role.getRoleName() != null && !role.getRoleName().isEmpty()) {
        roleCode.append(role.getRoleName()).append("_").append(role.getRoleType());
      } else {
        roleCode.append(role.getRoleType());
      }
      roleDetails = jdbcTemplate.query(sql, preparedStatement -> {
        preparedStatement.setLong(1, customerSysId);
        preparedStatement.setString(2, roleCode.toString());
        preparedStatement.setString(3, role.getRoleType().toUpperCase());
      }, new RoleDetailsExtractor());

    } catch (Exception e) {
      logger.error("Exception encountered while ", e);
    }
    return roleDetails;
  }
}
