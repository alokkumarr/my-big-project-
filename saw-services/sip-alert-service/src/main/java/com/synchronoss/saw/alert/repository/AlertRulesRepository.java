package com.synchronoss.saw.alert.repository;

import com.synchronoss.saw.alert.entities.AlertRulesDetails;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AlertRulesRepository extends JpaRepository<AlertRulesDetails, Long> {

  @Query(
      value = "SELECT * from ALERT_RULES_DETAILS ARD WHERE ARD.CATEGORY = ?1",
      nativeQuery = true)
  List<AlertRulesDetails> findByCategory(String category);

  @Query(
      value =
          "SELECT ALD.* FROM ALERT_RULES_DETAILS ALD, DATAPOD_DETAILS DD, ALERT_CUSTOMER_DETAILS "
              + "ACD "
              + "where ALD.DATAPOD_ID = DD.DATAPOD_ID\n"
              + "AND ACD.ALERT_CUSTOMER_SYS_ID = DD.ALERT_CUSTOMER_SYS_ID\n"
              + "and ACD.CUSTOMER_CODE = ?1",
      nativeQuery = true)
  List<AlertRulesDetails> findByCustomer(String customerCode);

  @Query(
      value =
          "SELECT "
              + "    ALD.ALERT_RULES_SYS_ID AS alertRulesSysId "
              + "FROM "
              + "    ALERT_RULES_DETAILS ALD, "
              + "    DATAPOD_DETAILS DD, "
              + "    ALERT_CUSTOMER_DETAILS ACD "
              + "WHERE "
              + "    ALD.DATAPOD_ID = DD.DATAPOD_ID "
              + "        AND DD.ALERT_CUSTOMER_SYS_ID = ACD.ALERT_CUSTOMER_SYS_ID "
              + "        AND ACD.CUSTOMER_CODE= :customerCode "
              + "        AND ALD.ALERT_RULES_SYS_ID = :alertRulesSysId ",
      nativeQuery = true)
  Long findAlertByCustomer(String customerCode, Long alertRulesSysId);
}
