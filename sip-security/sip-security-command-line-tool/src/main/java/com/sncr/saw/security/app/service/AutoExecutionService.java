package com.sncr.saw.security.app.service;

import com.sncr.saw.security.app.model.Customer;
import com.sncr.saw.security.app.repository.impl.CustomerRepositoryDaoImpl;
import com.sncr.saw.security.app.repository.impl.PreferenceRepositoryImpl;
import com.sncr.saw.security.common.bean.Valid;
import com.sncr.saw.security.common.bean.repo.ConfigValDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author pras0004
 * @since 3.5.0
 */
@Service
public class AutoExecutionService {
  @Autowired private CustomerRepositoryDaoImpl customerDao;

  @Autowired private PreferenceRepositoryImpl configValRepositoryDao;

  public ConfigValDetails getConfigDetails(String customerCode) {
    return configValRepositoryDao.getConfigDetails(customerCode);
  }

  public Valid addConfigVal(ConfigValDetails cv) {
    if (cv == null || StringUtils.isEmpty(cv.getConfigValObjGroup())) {
      throw new IllegalArgumentException(
          "Missing argument!! customerCode field can't be empty or null");
    }

    Customer customer = customerDao.fetchCustomerDetails(cv.getConfigValObjGroup());
    if (customer == null || StringUtils.isEmpty(customer.getCustCode())) {
      throw new IllegalArgumentException("Customer doesn't exist!!");
    } else if (customer.getActiveStatusInd() != null && customer.getActiveStatusInd() != 1) {
      throw new IllegalArgumentException("Not a active customer!! Contact Admin!!");
    }
    cv.setFilterByCustCode(customer.getIsJvCustomer() == 1 ? 0 : 1);
    ConfigValDetails cvd = getConfigDetails(cv.getConfigValObjGroup());
    if (cvd != null && !StringUtils.isEmpty(cvd.getConfigValObjGroup())) {
      return updateConfigValue(cv.getConfigValObjGroup(),cv.getActiveStatusInd());
    }
     return configValRepositoryDao.addConfigVal(cv);
  }

  public Valid updateConfigValue(String customerCode, int activeStatusInd) {
    return configValRepositoryDao.updateConfigVal(customerCode, activeStatusInd);
  }
}
