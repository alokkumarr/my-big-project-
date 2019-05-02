package com.synchronoss.saw.alert.repository;

import com.synchronoss.saw.alert.entities.AlertTriggerDetailsLog;
import com.synchronoss.saw.alert.modal.AlertCountResponse;
import com.synchronoss.saw.alert.modal.AlertStates;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AlertTriggerLog extends JpaRepository<AlertTriggerDetailsLog, Long> {

  @Query(
      value =
          "SELECT "
              + "    ATDL.ALERT_TRIGGER_SYS_ID as alertTriggerSysId, "
              + "    ATDL.ALERT_RULES_SYS_ID as alertRulesSysId, "
              + "    ALD.ALERT_NAME as alertName, "
              + "    ALD.ALERT_DESCRIPTION as alertDescription, "
              + "    ATDL.ALERT_STATE as alertState, "
              + "    ATDL.START_TIME as startTime, "
              + "    ALD.CATEGORY as category, "
              + "    ALD.SEVERITY as severity, "
              + "    ATDL.THRESHOLD_VALUE as metricValue, "
              + "    ATDL.METRIC_VALUE as thresholdValue "
              + "FROM "
              + "    ALERT_TRIGGER_DETAILS_LOG ATDL INNER JOIN "
              + "    ALERT_RULES_DETAILS ALD "
              + "    ON ATDL.ALERT_RULES_SYS_ID = ALD.ALERT_RULES_SYS_ID "
              + "WHERE "
              + "    ATDL.ALERT_RULES_SYS_ID = :alertRuleSysId",
      countQuery =
          "select"
              + "        count(ATDL.ALERT_TRIGGER_SYS_ID) "
              + "    FROM "
              + "        ALERT_TRIGGER_DETAILS_LOG ATDL "
              + "    INNER JOIN "
              + "        ALERT_RULES_DETAILS ALD     "
              + "            ON ATDL.ALERT_RULES_SYS_ID = ALD.ALERT_RULES_SYS_ID "
              + "WHERE "
              + "        ATDL.ALERT_RULES_SYS_ID = :alertRuleSysId",
      nativeQuery = true)
  Page<AlertStates> findByAlertRulesSysId(Long alertRuleSysId, Pageable pageable);

  @Query(
      value =
          "SELECT "
              + "    ATDL.ALERT_TRIGGER_SYS_ID as alertTriggerSysId, "
              + "    ATDL.ALERT_RULES_SYS_ID as alertRulesSysId, "
              + "    ALD.ALERT_NAME as alertName, "
              + "    ALD.ALERT_DESCRIPTION as alertDescription, "
              + "    ATDL.ALERT_STATE as alertState, "
              + "    ATDL.START_TIME as startTime, "
              + "    ALD.CATEGORY as category, "
              + "    ALD.SEVERITY as severity, "
              + "    ATDL.THRESHOLD_VALUE as metricValue, "
              + "    ATDL.METRIC_VALUE as thresholdValue "
              + "FROM "
              + "    ALERT_TRIGGER_DETAILS_LOG ATDL INNER JOIN "
              + "    ALERT_RULES_DETAILS ALD "
              + "    ON ATDL.ALERT_RULES_SYS_ID = ALD.ALERT_RULES_SYS_ID ",
      countQuery =
          "select"
              + "        count(ATDL.ALERT_TRIGGER_SYS_ID) "
              + "    FROM "
              + "        ALERT_TRIGGER_DETAILS_LOG ATDL "
              + "    INNER JOIN "
              + "        ALERT_RULES_DETAILS ALD     "
              + "            ON ATDL.ALERT_RULES_SYS_ID = ALD.ALERT_RULES_SYS_ID ",
      nativeQuery = true)
  Page<AlertStates> findByAlertStates(Pageable pageable);

  @Query(
      value =
          "select  date_format(from_unixtime(START_TIME/1000),'%d-%m-%Y') as date,"
              + " count(*)  as count "
              + "FROM alert_trigger_details_log WHERE START_TIME>=:fromEpoch "
              + "AND START_TIME<=:toEpoch  GROUP BY date ORDER BY date ASC",
      nativeQuery = true)
  List<AlertCountResponse> alertCountByDate(Long fromEpoch, Long toEpoch);

  @Query(
      value =
          "select  date_format(from_unixtime(atd.START_TIME/1000),'%d-%m-%Y') as date,"
              + "count(*) as count FROM alert_trigger_details_log atd  \n"
              + " WHERE   atd.ALERT_RULES_SYS_ID=:alertRuleSysId  AND  atd.START_TIME>=:fromEpoch "
              + "AND atd.START_TIME<=:toEpoch  GROUP BY date ORDER BY date ASC ;",
      nativeQuery = true)
  List<AlertCountResponse> alertCountByDateForAlertId(
      Long fromEpoch, Long toEpoch, Long alertRuleSysId);

  @Query(
      value =
          "select  ard.SEVERITY as alertSeverity, count(*) as count "
              + "FROM alert_trigger_details_log atd INNER JOIN  alert_rules_details ard "
              + "ON atd.ALERT_RULES_SYS_ID=ard.ALERT_RULES_SYS_ID "
              + "WHERE atd.START_TIME>=:fromEpoch AND atd.START_TIME<=:toEpoch"
              + " GROUP BY alertSeverity ORDER BY alertSeverity ASC ;",
      nativeQuery = true)
  List<AlertCountResponse> alertCountBySeverity(Long fromEpoch, Long toEpoch);

  @Query(
      value =
          "select  ard.SEVERITY as alertSeverity, count(*) as count "
              + "FROM alert_trigger_details_log atd INNER JOIN  alert_rules_details ard "
              + "ON atd.ALERT_RULES_SYS_ID=ard.ALERT_RULES_SYS_ID "
              + "WHERE atd.ALERT_RULES_SYS_ID=:alertRuleSysId AND "
              + "atd.START_TIME>=:fromEpoch AND atd.START_TIME<=:toEpoch"
              + " GROUP BY alertSeverity ORDER BY alertSeverity ASC ;",
      nativeQuery = true)
  List<AlertCountResponse> alertCountBySeverityForAlertId(
      Long fromEpoch, Long toEpoch, Long alertRuleSysId);
}
