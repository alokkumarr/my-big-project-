package com.sncr.saw.security.app.repository.impl;

import com.sncr.saw.security.app.repository.ModuleRepository;
import com.sncr.saw.security.common.bean.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Repository
public class ModuleRepositoryDaoImpl implements ModuleRepository {

  private static final Logger logger = LoggerFactory.getLogger(ModuleRepositoryDaoImpl.class);
  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public ModuleRepositoryDaoImpl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public Map<Integer, String> createModuleForOnboarding() {
    Map<Integer, String> sqlstatements = new HashMap<Integer, String>();
    Map<Integer, String> results = new HashMap<Integer, String>();

    KeyHolder keyHolder = new GeneratedKeyHolder();

    sqlstatements.put(1,
        "INSERT INTO `MODULES` (`MODULE_NAME`,`MODULE_CODE`,`MODULE_DESC`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES ('ANALYZE','ANLYS00001','Analyze Module',1,?,'admin','','','','')");
    sqlstatements.put(2,
        "INSERT INTO `MODULES` (`MODULE_NAME`,`MODULE_CODE`,`MODULE_DESC`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES ('OBSERVE','OBSR000001','Observe Module',1,?,'admin','','','','')");
    sqlstatements.put(3,
        "INSERT INTO `MODULES` (`MODULE_NAME`,`MODULE_CODE`,`MODULE_DESC`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES ('ALERT','ALRT000001','Alert Module',1,?,'admin','','','','')");
    sqlstatements.put(4,
        "INSERT INTO `MODULES` (`MODULE_NAME`,`MODULE_CODE`,`MODULE_DESC`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES ('OBSERVE','OBSR000001','Observe Module',1,?,'admin','','','','')");

    for (Map.Entry m : sqlstatements.entrySet()) {
      jdbcTemplate.update(new PreparedStatementCreator() {
                            @Override
                            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                              PreparedStatement ps = con
                                  .prepareStatement(m.getValue().toString(), new String[]{"MODULE_SYS_ID"});
                              ps.setDate(1, new java.sql.Date(new Date().getTime()));
                              return ps;
                            }
                          },
          keyHolder
      );
      results.put((Integer) m.getKey(), keyHolder.toString());
    }
    // all the IDs of the generated products
    return results;
  }

  @Override
  public boolean updateModule(Module prod) {
    return false;
  }

  @Override
  public boolean deleteModule(Long moduleId) {
    return false;
  }

  @Override
  public Module getModule(Long moduleId) {
    return null;
  }
}
