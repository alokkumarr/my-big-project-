package com.sncr.saw.security.app.service;

import com.sncr.saw.security.app.repository.impl.ConfigValRepositoryDaoImpl;
import com.sncr.saw.security.app.repository.impl.CustomerRepositoryDaoImpl;
import com.sncr.saw.security.app.repository.impl.PreferenceRepositoryImpl;
import com.sncr.saw.security.common.bean.Valid;
import com.sncr.saw.security.common.bean.repo.ConfigValDetails;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    // return configValRepositoryDao.insertConfigVal(cv);
    return configValRepositoryDao.addConfigVal(cv);
  }

  public Valid updateConfigValue(String customerCode, int activeStatusInd) {
    return configValRepositoryDao.updateConfigVal(customerCode, activeStatusInd);
  }
}
