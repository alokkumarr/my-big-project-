package com.sncr.saw.security.app.repository.impl.extract;

import com.sncr.saw.security.common.bean.repo.BrandDetails;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author alok.kumarr
 * @since 3.5.0
 */
public class BrandDetailsExtractor implements ResultSetExtractor<BrandDetails> {

  @Override
  public BrandDetails extractData(ResultSet resultSet)
      throws SQLException, DataAccessException {
    BrandDetails details = new BrandDetails();
    while (resultSet.next()) {
      details.setBrandColor(resultSet.getString("BRAND_COLOR"));
      details.setBrandName(resultSet.getString("BRAND_LOGO"));
    }
    return details;
  }
}