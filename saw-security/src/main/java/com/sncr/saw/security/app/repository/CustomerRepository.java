package com.sncr.saw.security.app.repository;

import com.sncr.saw.security.common.bean.Customer;

/**
 * Created by pawan.
         */
public interface CustomerRepository {
    boolean isValidCustCode(String custCode);
    int testSql();
    long createNewCustomerDao(Customer cust);
}
