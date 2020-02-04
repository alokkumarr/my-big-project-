package com.sncr.saw.security.app.service;

import com.sncr.saw.security.app.repository.impl.ConfigValRepositoryDaoImpl;
import com.sncr.saw.security.app.repository.impl.CustomerRepositoryDaoImpl;
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
  @Autowired
  private CustomerRepositoryDaoImpl customerDao;

  @Autowired
  private ConfigValRepositoryDaoImpl configValRepositoryDao;

  public ConfigValDetails getConfigDetails(String customerCode) {
    ConfigValDetails configValDetail = configValRepositoryDao.fetchConfigValues(customerCode);
    return configValDetail == null || configValDetail.getConfigValObjGroup() == null ? null
        : configValDetail;
  }

  public ConfigValDetails addConfigVal(ConfigValDetails cv) {
    if (cv == null || StringUtils.isEmpty(cv.getConfigValObjGroup())) {
      throw new IllegalArgumentException(
          "Missing argument!! customerCode field can't be empty or null");
    }
    //TODO : CustomerCode validation against customers table.
    ConfigValDetails configValDetail = configValRepositoryDao
        .fetchConfigValues(cv.getConfigValObjGroup());
     return configValRepositoryDao.insertConfigVal(cv);
  }

}
