package com.sncr.saw.security.app.repository.impl.extract;

import com.sncr.saw.security.app.model.Customer;
import com.sncr.saw.security.common.bean.repo.ConfigValDetails;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class CustomerDetailsExtractor implements ResultSetExtractor<Customer> {

  @Override
  public Customer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
    Customer customerDetails = null;
    while (resultSet.next()) {
      customerDetails = new Customer();
      customerDetails.setCustId(resultSet.getLong("CUSTOMER_SYS_ID"));
      customerDetails.setCustCode(resultSet.getString("CUSTOMER_CODE"));
      customerDetails.setCompanyName(resultSet.getString("COMPANY_NAME"));
      customerDetails.setActiveStatusInd(resultSet.getInt("ACTIVE_STATUS_IND"));
      customerDetails.setCompanyBusiness(resultSet.getString("COMPANY_BUSINESS"));
      customerDetails.setLandingProdSysId(resultSet.getLong("LANDING_PROD_SYS_ID"));
      customerDetails.setDomainName(resultSet.getString("DOMAIN_NAME"));
      customerDetails.setIsJvCustomer(resultSet.getInt("IS_JV_CUSTOMER"));
      customerDetails.setCompanyBusiness(resultSet.getString("COMPANY_BUSINESS"));
    }
    return customerDetails;
  }
}
