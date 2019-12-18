package com.sncr.saw.security.app.repository.result.extract;

import com.sncr.saw.security.common.bean.UserDetails;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class UserDetailsListExtractor implements ResultSetExtractor<ArrayList<UserDetails>> {

  @Override
  public ArrayList<UserDetails> extractData(ResultSet rs) throws SQLException, DataAccessException {
    UserDetails userDetails = null;
    ArrayList<UserDetails> userDetailsList = new ArrayList<UserDetails>();
    while (rs.next()) {
      userDetails = new UserDetails();
      userDetails.setMasterLoginId(rs.getString("USER_ID"));
      userDetails.setUserId(rs.getLong("USER_SYS_ID"));
      userDetails.setEmail(rs.getString("EMAIL"));
      userDetails.setRoleName(rs.getString("ROLE_NAME"));
      userDetails.setRoleId(rs.getLong("ROLE_SYS_ID"));
      userDetails.setFirstName(rs.getString("FIRST_NAME"));
      userDetails.setLastName(rs.getString("LAST_NAME"));
      userDetails.setMiddleName(rs.getString("MIDDLE_NAME"));
      userDetails.setCustomerId(rs.getLong("CUSTOMER_SYS_ID"));
      userDetails.setCustomerCode(rs.getString("CUSTOMER_CODE"));
      userDetails.setSecurityGroupName(rs.getString("SEC_GROUP_NAME"));
      Long secGroupSysId = rs.getLong("SEC_GROUP_SYS_ID");
      secGroupSysId = (secGroupSysId < 1) ? null : secGroupSysId;
      userDetails.setSecGroupSysId(secGroupSysId);
      if (rs.getInt("ACTIVE_STATUS_IND") == 1) {
        userDetails.setActiveStatusInd(true);
      } else {
        userDetails.setActiveStatusInd(false);
      }

      userDetailsList.add(userDetails);
    }
    return userDetailsList;
  }
}
