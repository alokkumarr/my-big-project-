package com.sncr.saw.security.app.repository.result.extract;

import com.sncr.saw.security.common.bean.UserDetails;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class UserDetailsExtractor implements ResultSetExtractor<UserDetails> {

  @Override
  public UserDetails extractData(ResultSet rs) throws SQLException, DataAccessException {
    UserDetails userDetails = null;
    if (rs.next()) {
      userDetails = new UserDetails();
      userDetails.setMasterLoginId(rs.getString("USER_ID"));
      userDetails.setUserId(rs.getLong("USER_SYS_ID"));
      userDetails.setEmail(rs.getString("EMAIL"));
      userDetails.setRoleName(rs.getString("ROLE_NAME"));
      userDetails.setRoleId(rs.getLong("ROLE_SYS_ID"));
      userDetails.setFirstName(rs.getString("FIRST_NAME"));
      userDetails.setLastName(rs.getString("LAST_NAME"));
      userDetails.setMiddleName(rs.getString("MIDDLE_NAME"));
      userDetails.setCustomerCode(rs.getString("CUSTOMER_CODE"));
      userDetails.setCustomerId(rs.getLong("CUSTOMER_SYS_ID"));
      Long secGroupSysId = rs.getLong("SEC_GROUP_SYS_ID");
      secGroupSysId = (secGroupSysId < 1) ? null : secGroupSysId;
      userDetails.setSecGroupSysId(secGroupSysId);
      userDetails.setSecurityGroupName(rs.getString("SEC_GROUP_NAME"));

      if (rs.getInt("ACTIVE_STATUS_IND") == 1) {
        userDetails.setActiveStatusInd(true);
      } else {
        userDetails.setActiveStatusInd(false);
      }
    }
    return userDetails;
  }
}
