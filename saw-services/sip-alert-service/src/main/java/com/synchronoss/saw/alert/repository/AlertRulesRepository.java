package com.synchronoss.saw.alert.repository;

import com.synchronoss.saw.alert.entities.AlertRulesDetails;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlertRulesRepository extends JpaRepository<AlertRulesDetails, Long> {

  @Query(
      value = "SELECT * from ALERT_RULES_DETAILS ARD WHERE ARD.CATEGORY = ?1",
      nativeQuery = true)
  List<AlertRulesDetails> findByCategory(String category);

  @Query(
      value =
          "SELECT ALD.* FROM ALERT_RULES_DETAILS ALD, DATAPOD_DETAILS DD,  ALERT_CUSTOMER_DETAILS ACD\n"
              + "where ALD.DATAPOD_ID = DD.DATAPOD_ID\n"
              + "AND ACD.ALERT_CUSTOMER_SYS_ID = DD.ALERT_CUSTOMER_SYS_ID\n"
              + "and ACD.CUSTOMER_CODE = ?1",
      nativeQuery = true)
  List<AlertRulesDetails> findByCustomer(String customerCode);
}
