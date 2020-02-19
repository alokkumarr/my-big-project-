package com.sncr.saw.security.app.repository.result.extract;

import com.synchronoss.bda.sip.dsk.DskDetails;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class UserCustomerDetailsExtractor implements ResultSetExtractor<DskDetails> {

  @Override
  public DskDetails extractData(ResultSet rs) throws SQLException, DataAccessException {
    DskDetails userDetails = null;
    if (rs.next()) {
      userDetails = new DskDetails();
      userDetails.setCustomerCode(rs.getString("CUSTOMER_CODE"));
      userDetails.setCustomerId(rs.getLong("CUSTOMER_SYS_ID"));
      userDetails.setIsJvCustomer(rs.getInt("IS_JV_CUSTOMER"));
      Long secGroupSysId = rs.getLong("SEC_GROUP_SYS_ID");
      secGroupSysId = (secGroupSysId < 1) ? null : secGroupSysId;
      userDetails.setSecurityGroupSysId(secGroupSysId);
    }
    return userDetails;
  }
}
