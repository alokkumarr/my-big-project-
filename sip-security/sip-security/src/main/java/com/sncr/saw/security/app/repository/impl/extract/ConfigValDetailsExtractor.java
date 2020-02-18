package com.sncr.saw.security.app.repository.impl.extract;

import com.sncr.saw.security.common.bean.repo.ConfigValDetails;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class ConfigValDetailsExtractor implements ResultSetExtractor<ConfigValDetails> {

  @Override
  public ConfigValDetails extractData(ResultSet resultSet)
      throws SQLException, DataAccessException {
    ConfigValDetails preference = null;
    while (resultSet.next()) {
      preference = new ConfigValDetails();
      preference.setConfigValCode(resultSet.getString("CONFIG_VAL_CODE"));
      preference.setConfigValDesc(resultSet.getString("CONFIG_VAL_DESC"));
      preference.setConfigValSysId(resultSet.getLong("CONFIG_VAL_SYS_ID"));
      preference.setActiveStatusInd(resultSet.getInt("ACTIVE_STATUS_IND"));
      preference.setConfigValObjType(resultSet.getString("CONFIG_VAL_OBJ_TYPE"));
      preference.setConfigValObjGroup(resultSet.getString("CONFIG_VAL_OBJ_GROUP"));
    }
    return preference;
  }
}
