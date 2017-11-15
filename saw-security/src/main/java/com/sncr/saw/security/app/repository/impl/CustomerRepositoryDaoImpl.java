package com.sncr.saw.security.app.repository.impl;

import com.sncr.saw.security.app.repository.CustomerRepository;
import com.sncr.saw.security.app.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
    public int testSql() {
        String sql = "select * from users LIMIT 10";
        System.out.println(jdbcTemplate.queryForList(sql));
        return 0;
    }

    /*
    *
    * Example: create-new-customer "abc" "pawan" "computers" 10 1 "somebody" "admin" "someoneelse" 30 "pawan.io"
    *
    * */
    @Override
    public long createNewCustomerDao(Customer cust) {
        if (cust != null) {
            String sql = "INSERT INTO customers(CUSTOMER_CODE, COMPANY_NAME, COMPANY_BUSINESS, LANDING_PROD_SYS_ID," +
                    " ACTIVE_STATUS_IND, CREATED_DATE, CREATED_BY, INACTIVATED_DATE, INACTIVATED_BY, MODIFIED_DATE," +
                    "MODIFIED_BY, PASSWORD_EXPIRY_DAYS, DOMAIN_NAME) VALUES(?,?,?,?,?,?,?,NULL,?,NULL,?,?,?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(
                    new PreparedStatementCreator() {
                        @Override
                        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                            PreparedStatement ps = con.prepareStatement(sql, new String[]{"CUSTOMER_SYS_ID"});
                            ps.setString(1, cust.getCustCode());
                            ps.setString(2, cust.getCompanyName());
                            ps.setString(3, cust.getCompanyBusiness());
                            ps.setLong(4, cust.getLandingProdSysId());
                            ps.setInt(5, cust.getActiveStatusInd());
                            // created at logic taken from https://justinjohnson.org/java/inserting-datetime-with-spring-jdbctemplate-and-namedparameterjdbctemplate/
                            ps.setDate(6, new java.sql.Date(new Date().getTime()));
                            ps.setString(7, cust.getCreatedBy());
                            ps.setString(8, cust.getInactivatedBy());
                            ps.setString(9, cust.getModifiedBy());
                            ps.setLong(10, cust.getPasswordExpiryDate());
                            ps.setString(11, cust.getDomainName());
                            return ps;
                        }
                    },
                    keyHolder
            );

            return (Long) keyHolder.getKey();
        }
        return -1L;
    }
}
