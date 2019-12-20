package com.sncr.saw.security.app.repository.result.extract;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class LongExtractor implements ResultSetExtractor<Long> {
  private String fieldName;

  public LongExtractor(String fieldName) {
    this.fieldName = fieldName;
  }

  @Override
  public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
    Long result = null;
    if (rs.next()) {
      result = rs.getLong(fieldName);
    }
    return result;
  }
}
