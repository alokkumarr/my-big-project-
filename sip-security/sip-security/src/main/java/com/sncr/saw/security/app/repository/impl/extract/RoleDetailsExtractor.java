package com.sncr.saw.security.app.repository.impl.extract;

import com.sncr.saw.security.common.bean.repo.admin.role.RoleDetails;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author alok.kumarr
 * @since 3.5.0
 */
public class RoleDetailsExtractor implements ResultSetExtractor<RoleDetails> {

  RoleDetails details = new RoleDetails();

  @Override
  public RoleDetails extractData(ResultSet resultSet) throws SQLException, DataAccessException {
    while (resultSet.next()) {
      details.setRoleSysId(resultSet.getLong("ROLE_SYS_ID"));
      details.setRoleName(resultSet.getString("ROLE_NAME"));
      int indicator = resultSet.getInt("ACTIVE_STATUS_IND");
      details.setActiveStatusInd(indicator == 1 ? "Active" : "Inactive");
    }
    return details;
  }
}
