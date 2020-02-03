package com.sncr.saw.security.app.repository;

import com.sncr.saw.security.app.model.OnBoardCustomer;

import java.util.List;

public interface OnBoardCustomerRepository {
    boolean isValidCustCode(String custCode);
    int haveCustomerInfo();
    long createNewCustomer(OnBoardCustomer cust);
    void displayCustomers();
    List<String> getCustomers();
    List<String> getProducts();
}
