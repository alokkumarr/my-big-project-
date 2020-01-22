package com.sncr.saw.security.app.repository;

import com.sncr.saw.security.app.model.Customer;
import com.sncr.saw.security.common.bean.Valid;
import com.sncr.saw.security.common.bean.repo.BrandDetails;

/**
 * Created by pawan.
 */
public interface CustomerRepository {
    boolean isValidCustCode(String custCode);
    long createNewCustomerDao(Customer cust);
    boolean deleteCustomerBrand(Long customerId);
    int haveCustomerInfo();
    BrandDetails fetchCustomerBrand(Long customerId);
    Valid upsertCustomerBrand(Long customerId,
                              String brandColor,
                              byte[] brandLogo);
}
