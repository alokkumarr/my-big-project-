package com.sncr.saw.security.app.model;

import java.io.Serializable;

public class OnBoardCustomer implements Serializable {
    private static final long serialVersionUID = 6710950208794990633L;

    private String customerCode;
    private String productName;
    private String productCode;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private String isJvCustomer;
    private String filterByCustomerCode;

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getIsJvCustomer() {
        return isJvCustomer;
    }

    public void setIsJvCustomer(String isJvCustomer) {
        this.isJvCustomer = isJvCustomer;
    }

    public String getFilterByCustomerCode() {
        return filterByCustomerCode;
    }

    public void setFilterByCustomerCode(String filterByCustomerCode) {
        this.filterByCustomerCode = filterByCustomerCode;
    }
}
