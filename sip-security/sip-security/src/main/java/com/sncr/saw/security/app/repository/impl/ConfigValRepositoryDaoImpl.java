package com.sncr.saw.security.app.repository.impl;

import com.sncr.saw.security.app.repository.ConfigValRepository;
import com.sncr.saw.security.common.bean.repo.ConfigValDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author pras0004
 * @since 3.5.0
 */
@Repository
public class ConfigValRepositoryDaoImpl implements ConfigValRepository {
  private static final Logger logger = LoggerFactory.getLogger(CustomerRepositoryDaoImpl.class);

  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public ConfigValRepositoryDaoImpl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public ConfigValDetails fetchConfigValues(String customerCode) {
    return null;
  }

  @Override
  public ConfigValDetails insertConfigVal(ConfigValDetails configValDetails) {
    return null;
  }
}
