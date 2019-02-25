package com.sncr.saw.security.app.repository;

import com.sncr.saw.security.app.model.OnBoardCustomer;

import java.util.List;

public interface OnBoardCustomerRepository {
    public boolean isValidCustCode(String custCode);
    public int testSql();
    public long createNewCustomer(OnBoardCustomer cust);
    public void displayCustomers();
    public List<String> getCustomers();
    public List<String> getProducts();
}
