package com.synchronoss.saw.alert.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.saw.alert.modal.AlertCount;
import com.synchronoss.saw.alert.modal.AlertCount.GroupBy;
import com.synchronoss.saw.alert.modal.AlertCount.Preset;
import com.synchronoss.saw.alert.modal.AlertCountResponse;
import com.synchronoss.saw.alert.modal.AlertRuleDetails;
import com.synchronoss.saw.alert.modal.AlertStatesResponse;
import com.synchronoss.saw.model.Aggregate;
import com.synchronoss.saw.model.Model.Operator;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.threeten.extra.YearQuarter;
import sncr.bda.base.MaprConnection;

@Service
public class AlertServiceImpl implements AlertService {
  private static final String ID = "id";
  private static final String NAME = "name";

  @Value("${metastore.base}")
  @NotNull
  private String basePath;

  @Value("${metastore.alertRulesTable}")
  @NotNull
  private String alertRulesMetadata;

  /**
   * Create Alert rule.
   *
   * @param alert Alert
   * @return Alert
   */
  @Override
  public AlertRuleDetails createAlertRule(
      @NotNull(message = "Alert definition cannot be null") @Valid AlertRuleDetails alert,
      Ticket ticket) {
    MaprConnection connection = new MaprConnection(basePath, alertRulesMetadata);

    String id = UUID.randomUUID().toString();
    alert.setAlertRulesSysId(id);
    connection.insert(id, alert);
    return alert;
  }

  /**
   * Update Alert Rule.
   *
   * @param alertRuleDetails AlertRuleDetails
   * @return Alert
   */
  @Override
  public AlertRuleDetails updateAlertRule(
      @NotNull(message = "Alert definition cannot be null") @Valid
          AlertRuleDetails alertRuleDetails,
      String alertRuleId,
      Ticket ticket) {
    MaprConnection connection = new MaprConnection(basePath, alertRulesMetadata);
    connection.update(alertRuleId, alertRuleDetails);
    return alertRuleDetails;
  }

  /**
   * Fetch all available alerts for the customer.
   *
   * @param ticket Ticket Id
   * @return AlertRulesDetails
   */
  @Override
  public List<AlertRuleDetails> retrieveAllAlerts(Ticket ticket) {

    return null;
  }

  /**
   * Delete Alert Rule.
   *
   * @param alertRuleId Alert rule Id
   */
  @Override
  public Boolean deleteAlertRule(
      @NotNull(message = "Alert Id cannot be null") @NotNull String alertRuleId, Ticket ticket) {
    MaprConnection connection = new MaprConnection(basePath, alertRulesMetadata);
    connection.deleteById(alertRuleId);
    return true;
  }

  /**
   * Get Alert Rule.
   *
   * @param alertRuleId Alert rule Id
   * @return
   */
  @Override
  public AlertRuleDetails getAlertRule(
      @NotNull(message = "alertRuleID cannot be null") @NotNull String alertRuleId, Ticket ticket) {

    return null;
  }

  /**
   * Get Alert Rules By Category.
   *
   * @param category Category Id
   * @return
   */
  @Override
  public List<AlertRuleDetails> getAlertRulesByCategory(
      @NotNull(message = "categoryId cannot be null") @NotNull String category, Ticket ticket) {

    /* Optional<AlertCustomerDetails> alertCustomerDetails =
        alertCustomerRepository.findByCustomerCode(ticket.getCustCode());
    List<AlertRulesDetails> alertRulesDetails = alertRulesRepository.findByCategory(category);*/
    return null;
  }

  /**
   * Retrieve operator details.
   *
   * @param ticket ticket Id
   * @return String if matched
   */
  @Override
  public String retrieveOperatorsDetails(Ticket ticket) {
    JsonArray elements = new JsonArray();
    List<Operator> operatorList = Arrays.asList(Operator.values());
    for (Operator operator : operatorList) {
      JsonObject object = new JsonObject();
      String readableOperator = getReadableOperator(operator);
      if (readableOperator != null) {
        object.addProperty(ID, operator.value());
        object.addProperty(NAME, readableOperator);
        elements.add(object);
      }
    }
    return elements.toString();
  }

  @Override
  public String retrieveAggregations(Ticket ticket) {
    JsonArray elements = new JsonArray();
    List<Aggregate> aggregationList = Arrays.asList(Aggregate.values());
    for (Aggregate aggregation : aggregationList) {
      JsonObject object = new JsonObject();
      String readableOperator = getReadableAggregation(aggregation);
      if (readableOperator != null) {
        object.addProperty(ID, aggregation.value());
        object.addProperty(NAME, readableOperator);
        elements.add(object);
      }
    }
    return elements.toString();
  }

  /**
   * Get alert state by alert ID.
   *
   * @param alertRuleId alertRuleId
   * @param ticket Ticket
   * @return List of AlertStates
   */
  @Override
  public AlertStatesResponse getAlertStates(
      @NotNull(message = "alertRuleId cannot be null") Long alertRuleId,
      Integer pageNumber,
      Integer pageSize,
      Ticket ticket) {
    AlertStatesResponse alertStatesResponse = null;
    /*Long alertRuleSysId =
        alertRulesRepository.findAlertByCustomer(ticket.getCustCode(), alertRuleId);
    if (alertRuleSysId != null) {
      alertStatesResponse = new AlertStatesResponse();
      Pageable pageable = PageRequest.of(pageNumber, pageSize, Direction.DESC, "START_TIME");
      Page<AlertStates> alertStates = alertTriggerLog.findByAlertRulesSysId(alertRuleId, pageable);
      alertStatesResponse.setAlertStatesList(alertStates.getContent());
      alertStatesResponse.setNumberOfRecords(alertStates.getTotalElements());
    }*/
    return alertStatesResponse;
  }

  /**
   * Get list of pageable Alerts by time order.
   *
   * @param ticket Ticket
   * @return List of AlertStates, number of records.
   */
  @Override
  public AlertStatesResponse listAlertStates(Integer pageNumber, Integer pageSize, Ticket ticket) {
    /* Pageable pageable = PageRequest.of(pageNumber, pageSize, Direction.DESC, "START_TIME");
    AlertStatesResponse alertStatesResponse = new AlertStatesResponse();
    Page<AlertStates> alertStates = alertTriggerLog.findByAlertStates(pageable);
    alertStatesResponse.setAlertStatesList(alertStates.getContent());
    alertStatesResponse.setNumberOfRecords(alertStates.getTotalElements());
    alertStatesResponse.setMessage("Success");*/
    return null;
  }

  /**
   * It return readable operator name.
   *
   * @param operator Operator
   * @return String
   */
  private String getReadableOperator(Operator operator) {

    switch (operator) {
      case EQ:
        return "Equal To";
      case GT:
        return "Greater Than";
      case LT:
        return "Less Than";
      case GTE:
        return "Greater Than and Equal To";
      case LTE:
        return "Less Than and Equal To";
      case NEQ:
        return "Not Equal To";
        /**
         * case BTW: return "Between"; case SW: return "Start With"; case EW: return "End With";
         * case CONTAINS: return "Contains"; case ISIN: return "Is IN";
         */
      default:
        return null;
    }
  }

  /**
   * It return readable aggregation name.
   *
   * @param aggregation Aggregation
   * @return String
   */
  private String getReadableAggregation(Aggregate aggregation) {

    switch (aggregation) {
      case AVG:
        return "Average";
      case SUM:
        return "SUM";
      case MIN:
        return "Minimum";
      case MAX:
        return "Maximum";
      case COUNT:
        return "Count Values";
      default:
        return null;
    }
  }

  /**
   * It returns alert count for each day based on the preset value.
   *
   * @param alertCount AlertCount
   * @return AlertCountResponse
   */
  @Override
  public List<AlertCountResponse> alertCount(AlertCount alertCount, Long alertRuleSysId) {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String dateFormatLte = "23:59:59";
    String dateFormatGte = "00:00:00";
    String space = " ";
    Long epochGte = null;
    Long epochLte = null;
    String startDate = null;
    String endDate = null;
    LocalDateTime now = LocalDateTime.now();
    if (alertCount.getPreset() == null) {
      throw new IllegalArgumentException("Preset cannot be null");
    }
    if (alertCount.getGroupBy() == null) {
      throw new IllegalArgumentException("GroupBy cannot be null");
    }
    switch (alertCount.getPreset().value()) {
      case "Yesterday":
        {
          LocalDateTime yesterday = now.minusDays(1);
          endDate = yesterday.format(dateTimeFormatter) + space + dateFormatLte;
          startDate = yesterday.format(dateTimeFormatter) + space + dateFormatGte;
          break;
        }
      case "Today":
        {
          LocalDateTime today = now;
          endDate = today.format(dateTimeFormatter) + space + dateFormatLte;
          startDate = today.format(dateTimeFormatter) + space + dateFormatGte;
          break;
        }
      case "YTD":
        {
          LocalDateTime firstDay = now.with(TemporalAdjusters.firstDayOfYear());
          endDate = now.format(dateTimeFormatter) + space + dateFormatLte;
          startDate = firstDay.format(dateTimeFormatter) + space + dateFormatGte;
          break;
        }
      case "MTD":
        {
          LocalDateTime firstDayOfMonth = now.with(TemporalAdjusters.firstDayOfMonth());
          endDate = now.format(dateTimeFormatter) + space + dateFormatLte;
          startDate = firstDayOfMonth.format(dateTimeFormatter) + space + dateFormatGte;
          break;
        }
      case "LW":
        {
          DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
          LocalDateTime priorLastWeek = now.minusWeeks(1);
          LocalDateTime startOfWeek =
              priorLastWeek.with(TemporalAdjusters.previousOrSame(firstDayOfWeek.plus(1)));
          LocalDateTime endOfWeek =
              priorLastWeek.with(TemporalAdjusters.nextOrSame(firstDayOfWeek));
          startDate = startOfWeek.format(dateTimeFormatter) + space + dateFormatGte;
          endDate = (endOfWeek.format(dateTimeFormatter) + space + dateFormatLte);
          break;
        }
      case "TW":
        {
          DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
          LocalDateTime lastWeek = now;
          LocalDateTime startOfWeek =
              lastWeek.with(TemporalAdjusters.previousOrSame(firstDayOfWeek.plus(1)));
          startDate = startOfWeek.format(dateTimeFormatter) + space + dateFormatGte;
          endDate = now.format(dateTimeFormatter) + space + dateFormatLte;
          break;
        }
      case "LTW":
        {
          LocalDateTime last2Week = now.minusWeeks(2);
          endDate =
              now.with(DayOfWeek.MONDAY).minusDays(1).format(dateTimeFormatter)
                  + space
                  + dateFormatLte;
          startDate =
              last2Week.with(DayOfWeek.MONDAY).format(dateTimeFormatter) + space + dateFormatGte;
          break;
        }
      case "LSW":
        {
          DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
          LocalDateTime lastWeek = now.minusWeeks(6);
          endDate =
              now.with(DayOfWeek.MONDAY).minusDays(1).format(dateTimeFormatter)
                  + space
                  + dateFormatLte;
          startDate =
              lastWeek.with(DayOfWeek.MONDAY).format(dateTimeFormatter) + space + dateFormatGte;
          break;
        }

      case "LQ":
        {
          YearQuarter quarter = YearQuarter.now();
          endDate = quarter.minusQuarters(1).atEndOfQuarter().toString() + space + dateFormatLte;
          startDate = quarter.minusQuarters(1).atDay(1).toString() + space + dateFormatGte;
          break;
        }
      case "LM":
        {
          LocalDateTime lastMonth = now.minusMonths(1);
          startDate =
              lastMonth.with(TemporalAdjusters.firstDayOfMonth()).format(dateTimeFormatter)
                  + space
                  + dateFormatGte;
          endDate =
              lastMonth.with(TemporalAdjusters.lastDayOfMonth()).format(dateTimeFormatter)
                  + space
                  + dateFormatLte;
          break;
        }
      case "LTM":
        {
          LocalDateTime last3Month = now.minusMonths(3);
          endDate =
              now.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).format(dateTimeFormatter)
                  + space
                  + dateFormatLte;
          startDate =
              last3Month.with(TemporalAdjusters.firstDayOfMonth()).format(dateTimeFormatter)
                  + space
                  + dateFormatGte;
          break;
        }
      case "LSM":
        {
          LocalDateTime last6Months = now.minusMonths(6);
          endDate =
              now.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).format(dateTimeFormatter)
                  + space
                  + dateFormatLte;
          startDate =
              last6Months.with(TemporalAdjusters.firstDayOfMonth()).format(dateTimeFormatter)
                  + space
                  + dateFormatGte;
          break;
        }
      case "LY":
        {
          LocalDateTime currentDayOflastYearDate = now.minusMonths(12);
          endDate =
              currentDayOflastYearDate
                      .with(TemporalAdjusters.lastDayOfYear())
                      .format(dateTimeFormatter)
                  + space
                  + dateFormatLte;
          startDate =
              currentDayOflastYearDate
                      .with(TemporalAdjusters.firstDayOfYear())
                      .format(dateTimeFormatter)
                  + space
                  + dateFormatGte;
          break;
        }
      case "BTW":
        {
          if (alertCount.getStartTime() == null) {
            throw new IllegalArgumentException("Start time is missing for custom date filter");
          } else if (alertCount.getEndTime() == null) {
            throw new IllegalArgumentException("End date is missing for custom date filter");
          }
          break;
        }
      default:
        throw new IllegalArgumentException(alertCount.getPreset() + " not present");
    }
    if (!(alertCount.getPreset() == Preset.BTW)) {
      epochGte = getEpochFromDateTime(startDate);
      epochLte = getEpochFromDateTime(endDate);
    } else {
      epochGte = alertCount.getStartTime();
      epochLte = alertCount.getEndTime();
    }

    if (alertCount.getGroupBy() == GroupBy.SEVERITY) {
      if (alertRuleSysId != null) {
        return null;
        // alertTriggerLog.alertCountBySeverityForAlertId(epochGte, epochLte, alertRuleSysId);
      }
      return null;
      // alertTriggerLog.alertCountBySeverity(epochGte, epochLte);
    } else {
      if (alertRuleSysId != null) {
        return null;
        // alertTriggerLog.alertCountByDateForAlertId(epochGte, epochLte, alertRuleSysId);
      }
      return null;
      // alertTriggerLog.alertCountByDate(epochGte, epochLte);
    }
  }

  /**
   * Return timestamp from the given date.
   *
   * @param date String
   * @return Long
   */
  private Long getEpochFromDateTime(String date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime ldt = LocalDateTime.parse(date, formatter);
    ZoneId zoneId = ZoneId.systemDefault();
    Long epochValue = ldt.atZone(zoneId).toInstant().toEpochMilli();
    return epochValue;
  }
}
