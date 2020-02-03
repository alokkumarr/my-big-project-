package com.sncr.saw.security.app.repository;

import com.sncr.saw.security.common.bean.repo.ConfigValDetails;

/**
 * @author pras0004
 * @since 3.5.0
 */
public interface ConfigValRepository {
  ConfigValDetails fetchConfigValues(String customerCode);
  ConfigValDetails insertConfigVal(ConfigValDetails configValDetails);
}
