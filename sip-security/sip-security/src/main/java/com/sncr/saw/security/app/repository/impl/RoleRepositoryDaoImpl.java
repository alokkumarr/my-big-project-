package com.sncr.saw.security.app.repository.impl;

import com.sncr.saw.security.app.repository.RoleRepository;
import com.sncr.saw.security.common.bean.Role;
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
	public boolean validateRoleByIdAndCustomerCode(Long loginId, String customerCode) {
		try {

		} catch (Exception ex) {

		}
		return false;
	}

	@Override
	public long createNewRoleDao(Long customerId, Role role) {
		if (customerId != null) {
			String sql =
					"INSERT INTO `ROLES` (`CUSTOMER_SYS_ID`, `ROLE_NAME`, `ROLE_CODE`, `ROLE_DESC`," +
							"`ROLE_TYPE`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`)" +
							" VALUES (?, ?, ?, ?, ?, ?, ?, 'admin')";

			String roleName = !role.getRoleName().isEmpty() ? role.getRoleName() : "ADMIN_USER";
			String activeStatus = !role.getActiveStatusInd().isEmpty() ? role.getActiveStatusInd() : "false";

			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(
					con -> {
						PreparedStatement ps = con.prepareStatement(sql, new String[]{"ROLE_SYS_ID"});
						ps.setLong(1, customerId);
						ps.setString(2, roleName);
						ps.setString(3, role.getCustomerCode().concat("_" + roleName));
						ps.setString(4, !role.getRoleDesc().isEmpty() ? role.getRoleDesc() : "Admin User");
						ps.setString(5, role.getRoleType());
						ps.setInt(6, Boolean.valueOf(activeStatus) ? 1 : 0);
						ps.setDate(7, new java.sql.Date(new Date().getTime()));

						logger.trace("Prepare statement to create new roles : %s", ps.toString());
						return ps;
					},
					keyHolder
			);
			return (Long) keyHolder.getKey();
		}
		return -1L;
	}

	@Override
	public long addNewRoleType(Role role) {
		String sql =
				"INSERT INTO `ROLES_TYPE` (`ROLES_TYPE_NAME`, `ROLES_TYPE_DESC`, `ACTIVE_STATUS_IND`," +
						"`CREATED_DATE`, `CREATED_BY`, `INACTIVATED_DATE`, `INACTIVATED_BY`, `MODIFIED_DATE`, `MODIFIED_BY`)" +
						" VALUES (?, ?, ?, ?, 'admin', null, null, null, null)";

		String activeStatus = !role.getActiveStatusInd().isEmpty() ? role.getActiveStatusInd() : "false";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(
				con -> {
					PreparedStatement ps = con.prepareStatement(sql, new String[]{"ROLES_TYPE_SYS_ID"});
					ps.setString(1, role.getRoleType().toUpperCase());
					ps.setString(2, role.getRoleType());
					ps.setInt(3, Boolean.valueOf(activeStatus) ? 1 : 0);
					ps.setDate(4, new java.sql.Date(new Date().getTime()));
					logger.trace("Prepare statement to create new roles types : %s", ps.toString());
					return ps;
				},
				keyHolder
		);
		return (Long) keyHolder.getKey();
	}
}
