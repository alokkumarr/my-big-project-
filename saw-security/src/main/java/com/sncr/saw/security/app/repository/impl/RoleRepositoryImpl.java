package com.sncr.saw.security.app.repository.impl;

import com.sncr.saw.security.app.repository.RoleRepository;
import com.sncr.saw.security.app.repository.CustomerRepository;
import com.sncr.saw.security.common.bean.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RoleRepositoryImpl implements RoleRepository {

    private static final Logger logger = LoggerFactory.getLogger(RoleRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RoleRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // we decided that we will initially create only admin user

    @Override
    public long createNewRoleDao(Long custId) {
        String sql = "INSERT INTO `ROLES` (`CUSTOMER_SYS_ID`, `ROLE_NAME`, `ROLE_CODE`, `ROLE_DESC`," +
                "`ROLE_TYPE`, `DATA_SECURITY_KEY`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`)" +
                " VALUES (?, 'ADMIN', ?, 'Admin User', 'ADMIN', 'NA', '1'," +
                "'2017-05-23 08:28:37', 'admin')";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                        PreparedStatement ps = con.prepareStatement(sql, new String[]{"ROLE_SYS_ID"});
                        ps.setLong(1, custId);
                        ps.setString(2, custId.toString()+"_ADMIN_USER");
                        return ps;
                    }
                },
                keyHolder
        );
        return (Long) keyHolder.getKey();
    }
}
