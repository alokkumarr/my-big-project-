package com.sncr.saw.security.app.repository.impl.extract;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author alok.kumarr
 * @since 3.5.0
 */
public class TicketValidExtractor implements ResultSetExtractor<Boolean> {
  @Override
  public Boolean extractData(ResultSet rs) throws SQLException, DataAccessException {
    Boolean isValid = false;
    if (rs.next()) {
      int validInd = rs.getInt("VALID_INDICATOR");
      if (validInd > 0) {
        return true;
      } else {
        return false;
      }
    }
    return isValid;
  }

}
