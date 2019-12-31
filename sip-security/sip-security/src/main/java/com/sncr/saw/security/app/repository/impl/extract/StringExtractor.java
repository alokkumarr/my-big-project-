package com.sncr.saw.security.app.repository.impl.extract;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author alok.kumarr
 * @since 3.5.0
 */
public class StringExtractor implements ResultSetExtractor<String> {
  private String fieldName;

  public StringExtractor(String fieldName) {
    this.fieldName = fieldName;
  }

  @Override
  public String extractData(ResultSet rs) throws SQLException, DataAccessException {
    String result = null;
    if (rs.next()) {
      result = rs.getString(fieldName) != null ? rs.getString(fieldName).trim() : rs.getString(fieldName);
    }
    return result;
  }
}
