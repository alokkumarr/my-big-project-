package com.sncr.saw.security.app.repository;

import com.sncr.saw.security.app.model.Customer;

/**
 * Created by pawan.
 */
public interface CustomerRepository {
    boolean isValidCustCode(String custCode);
    int testSql();
    long createNewCustomerDao(Customer cust);
    void displayCustomers();
}
