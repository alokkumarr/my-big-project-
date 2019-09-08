package com.synchronoss.saw.alert.util;

import com.synchronoss.saw.alert.modal.AlertCount.Preset;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.threeten.extra.YearQuarter;

@Component
public class AlertUtils {
  private static final Logger logger = LoggerFactory.getLogger(AlertUtils.class);

  /**
   * Returns start Time and end time for the preset.
   *
   * @param preset Preset
   * @param startTime Preset
   * @param endTime Preset
   * @return Map
   */
  public static Map<String, Long> getEpochTimeForPreset(
      Preset preset, Long startTime, Long endTime) {
    logger.debug("Calculate StartTime and End Time for the preset" + preset);
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String dateFormatLte = "23:59:59";
    String dateFormatGte = "00:00:00";
    String space = " ";
    Long epochGte = null;
    Long epochLte = null;
    String startDate = null;
    String endDate = null;
    LocalDateTime now = LocalDateTime.now();
    if (preset == null) {
      throw new IllegalArgumentException("Preset cannot be null");
    }
    Map<String, Long> epochMap = new HashMap<String, Long>();
    switch (preset.value()) {
      case "Yesterday": {
        LocalDateTime yesterday = now.minusDays(1);
        endDate = yesterday.format(dateTimeFormatter) + space + dateFormatLte;
        startDate = yesterday.format(dateTimeFormatter) + space + dateFormatGte;
        break;
      }
      case "Today": {
        LocalDateTime today = now;
        endDate = today.format(dateTimeFormatter) + space + dateFormatLte;
        startDate = today.format(dateTimeFormatter) + space + dateFormatGte;
        break;
      }
      case "YTD": {
        LocalDateTime firstDay = now.with(TemporalAdjusters.firstDayOfYear());
        endDate = now.format(dateTimeFormatter) + space + dateFormatLte;
        startDate = firstDay.format(dateTimeFormatter) + space + dateFormatGte;
        break;
      }
      case "MTD": {
        LocalDateTime firstDayOfMonth = now.with(TemporalAdjusters.firstDayOfMonth());
        endDate = now.format(dateTimeFormatter) + space + dateFormatLte;
        startDate = firstDayOfMonth.format(dateTimeFormatter) + space + dateFormatGte;
        break;
      }
      case "LW": {
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
      case "TW": {
        DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
        LocalDateTime lastWeek = now;
        LocalDateTime startOfWeek =
            lastWeek.with(TemporalAdjusters.previousOrSame(firstDayOfWeek.plus(1)));
        startDate = startOfWeek.format(dateTimeFormatter) + space + dateFormatGte;
        endDate = now.format(dateTimeFormatter) + space + dateFormatLte;
        break;
      }
      case "LTW": {
        LocalDateTime last2Week = now.minusWeeks(2);
        endDate =
            now.with(DayOfWeek.MONDAY).minusDays(1).format(dateTimeFormatter)
                 + space
                 + dateFormatLte;
        startDate =
            last2Week.with(DayOfWeek.MONDAY).format(dateTimeFormatter) + space + dateFormatGte;
        break;
      }
      case "LSW": {
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

      case "LQ": {
        YearQuarter quarter = YearQuarter.now();
        endDate = quarter.minusQuarters(1).atEndOfQuarter().toString() + space + dateFormatLte;
        startDate = quarter.minusQuarters(1).atDay(1).toString() + space + dateFormatGte;
        break;
      }
      case "LM": {
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
      case "LTM": {
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
      case "LSM": {
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
      case "LY": {
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
      case "BTW": {
        if (startTime == null) {
          throw new IllegalArgumentException("Start time is missing for custom date filter");
        } else if (endTime == null) {
          throw new IllegalArgumentException("End date is missing for custom date filter");
        }
        break;
      }
      default:
        throw new IllegalArgumentException(preset + " not present");
    }
    if (preset != Preset.BTW) {
      epochGte = getEpochFromDateTime(startDate);
      epochLte = getEpochFromDateTime(endDate);
    } else {
      epochGte = startTime;
      epochLte = endTime;
    }
    epochMap.put("startTime", epochGte);
    epochMap.put("endTime", epochLte);
    logger.debug("Start Time  = " + epochGte);
    logger.debug("End Time  = " + epochLte);
    return epochMap;
  }

  /**
   * Return timestamp from the given date.
   *
   * @param date String
   * @return Long
   */
  private static Long getEpochFromDateTime(String date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime ldt = LocalDateTime.parse(date, formatter);
    ZoneId zoneId = ZoneId.systemDefault();
    Long epochValue = ldt.atZone(zoneId).toInstant().toEpochMilli();
    return epochValue;
  }
}
