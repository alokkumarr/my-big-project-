package com.sncr.saw.security.app.repository.impl.extract;

import com.sncr.saw.security.common.bean.repo.admin.category.SubCategoryDetails;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author alok.kumarr
 * @since 3.5.0
 */
public class SubCategoryDetailExtractor implements ResultSetExtractor<ArrayList<SubCategoryDetails>> {
  @Override
  public ArrayList<SubCategoryDetails> extractData(ResultSet rs) throws SQLException, DataAccessException {


    SubCategoryDetails subCategory = null;
    ArrayList<SubCategoryDetails> subCatList = new ArrayList<SubCategoryDetails>();
    while (rs.next()) {
      subCategory = new SubCategoryDetails();
      subCategory.setSubCategoryId(rs.getLong("CUST_PROD_MOD_FEATURE_SYS_ID"));
      subCategory.setSubCategoryName(rs.getString("FEATURE_NAME"));
      subCategory.setSubCategoryDesc(rs.getString("FEATURE_DESC"));
      subCategory.setActivestatusInd(rs.getLong("ACTIVE_STATUS_IND"));
      subCatList.add(subCategory);
    }
    return subCatList;
  }

}
