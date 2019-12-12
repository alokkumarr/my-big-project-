package com.sncr.saw.security.app.repository.impl.extract;

import com.sncr.saw.security.common.bean.repo.ProductModuleDetails;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author alok.kumarr
 * @since 3.5.0
 */
public class ProductModuleExtractor implements ResultSetExtractor<ProductModuleDetails> {
  ProductModuleDetails moduleDetails = new ProductModuleDetails();

  @Override
  public ProductModuleDetails extractData(ResultSet resultSet) throws SQLException, DataAccessException {
    while (resultSet.next()) {
      moduleDetails.setCustomerSysId(resultSet.getLong("CUSTOMER_SYS_ID"));
      moduleDetails.setCustomerCode(resultSet.getString("CUSTOMER_CODE"));
      moduleDetails.setProductId(resultSet.getLong("PRODUCT_SYS_ID"));
      moduleDetails.setModuleId(resultSet.getLong("MODULE_SYS_ID"));
    }
    return moduleDetails;
  }

}
