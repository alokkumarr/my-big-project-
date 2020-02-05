package com.sncr.saw.security.app.repository.impl;

import com.sncr.saw.security.app.model.OnBoardCustomer;
import com.sncr.saw.security.app.repository.OnBoardCustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
public class OnBoardCustomerRepositoryDaoImpl implements OnBoardCustomerRepository{

    private static final Logger logger = LoggerFactory.getLogger(OnBoardCustomerRepositoryDaoImpl.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OnBoardCustomerRepositoryDaoImpl(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    /**
     * Check for validity for customerCode.
     * @param custCode
     * @return
     */
    @Override
    public boolean isValidCustCode(String custCode) {
        try {
            if (custCode == null || custCode.isEmpty()) {
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

    /**
     * This method to check have customer info
     * @return
     */
    @Override
    public int haveCustomerInfo() {
        String sql = "select 1";
        SqlRowSet srs = jdbcTemplate.queryForRowSet(sql);
        int rowCount = 0;
        while(srs.next()) {
            System.out.println(srs.getRow());
            rowCount++;
        }
        return rowCount;
    }

    /**
     *
     * @param cust
     * @return
     */
    @Override
    public long createNewCustomer(OnBoardCustomer cust) {
        String sql = "call onboard_customer(?,?,?,?,?,?,?,?,?) ";

        int res = jdbcTemplate.update(sql, ps-> {
            ps.setString(1,cust.getCustomerCode());
            ps.setString(2,cust.getProductName());
            ps.setString(3,cust.getProductCode());
            ps.setString(4,cust.getEmail());
            ps.setString(5,cust.getFirstName());
            ps.setString(6,cust.getMiddleName());
            ps.setString(7,cust.getLastName());
            ps.setString(8,cust.getIsJvCustomer());
            ps.setString(9,cust.getFilterByCustomerCode());
        });
        logger.info("res",res);
        return res;
    }

    /**
     *
     */
    @Override
    public void displayCustomers() {
        String sql = "select CUSTOMER_SYS_ID, CUSTOMER_CODE, COMPANY_NAME, COMPANY_BUSINESS from CUSTOMERS";
        List rs = jdbcTemplate.queryForList(sql);
        for (Object x:rs) {
            System.out.println(x);
        }
    }

    @Override
    public List<String> getCustomers() {
        String fetchSql = "select CUSTOMER_CODE from CUSTOMERS";
        List<String> customerCode = null;
        try {
            customerCode = jdbcTemplate.query(fetchSql,
                preparedStatement -> {},
                resultSet -> {
                    List<String> nameList = new ArrayList<>();
                    while (resultSet.next()) {
                        nameList.add(resultSet.getString("CUSTOMER_CODE"));
                    }
                    return nameList;
                });
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }

        return customerCode;
    }

    @Override
    public List<String> getProducts() {
        String fetchSql = "select PRODUCT_CODE from PRODUCTS";
        List<String> productCode = null;
        try {
            productCode = jdbcTemplate.query(fetchSql,
                preparedStatement -> {},
                resultSet -> {
                    List<String> pcList = new ArrayList<>();
                    while (resultSet.next()) {
                        pcList.add(resultSet.getString("PRODUCT_CODE"));
                    }
                    return pcList;
                });
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }

        return productCode;
    }
}
