package com.synchronoss.querybuilder.kpi;

import com.synchronoss.BuilderUtil;
import com.synchronoss.DynamicConvertor;
import org.junit.Before;
import org.junit.Test;
import org.threeten.extra.YearQuarter;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class TestBuilderUtil {

    LocalDateTime now;
    DateTimeFormatter dateTimeFormatter;
    String DATE_FORMAT_LTE = null;
    String DATE_FORMAT_GTE = null;
    String SPACE = null;
    DynamicConvertor dynamicConvertor;
    DayOfWeek firstDayOfWeek;


    @Before
    public void setUp() throws Exception {
        DATE_FORMAT_LTE = "23:59:59";
        DATE_FORMAT_GTE = "00:00:00";
        SPACE = " ";
        now = LocalDateTime.now();
        dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
    }

    @Test
    public void getLy() {
        LocalDateTime currentDayOflastYearDate = now.minusMonths(12);
        dynamicConvertor = BuilderUtil.dynamicDecipher("LY");
        assertEquals(dynamicConvertor.getLte(), currentDayOflastYearDate.with(TemporalAdjusters.lastDayOfYear()).format(dateTimeFormatter) + SPACE + DATE_FORMAT_LTE);
        assertEquals(dynamicConvertor.getGte(), currentDayOflastYearDate.with(TemporalAdjusters.firstDayOfYear()).format(dateTimeFormatter) + SPACE + DATE_FORMAT_GTE);
    }

    @Test
    public void getYesterday() {
        LocalDateTime yesterday = now.minusDays(1);
        dynamicConvertor = BuilderUtil.dynamicDecipher("Yesterday");
        assertEquals(dynamicConvertor.getLte(), yesterday.format(dateTimeFormatter) + SPACE + DATE_FORMAT_LTE);
        assertEquals(dynamicConvertor.getGte(), yesterday.format(dateTimeFormatter) + SPACE + DATE_FORMAT_GTE);
    }

    @Test
    public void getYTD() {
        LocalDateTime firstDay = now.with(TemporalAdjusters.firstDayOfYear());
        dynamicConvertor = BuilderUtil.dynamicDecipher("YTD");
        assertEquals(dynamicConvertor.getLte(), now.format(dateTimeFormatter) + SPACE + DATE_FORMAT_LTE);
        assertEquals(dynamicConvertor.getGte(), firstDay.format(dateTimeFormatter) + SPACE + DATE_FORMAT_GTE);
    }

    @Test
    public void getMTD() {
        LocalDateTime firstDayOfMonth = now.with(TemporalAdjusters.firstDayOfMonth());
        dynamicConvertor = BuilderUtil.dynamicDecipher("MTD");
        assertEquals(dynamicConvertor.getLte(), now.format(dateTimeFormatter) + SPACE + DATE_FORMAT_LTE);
        assertEquals(dynamicConvertor.getGte(), firstDayOfMonth.format(dateTimeFormatter) + SPACE + DATE_FORMAT_GTE);
    }

    @Test
    public void getLTM() {
        LocalDateTime last3Month = now.minusMonths(3);
        dynamicConvertor = BuilderUtil.dynamicDecipher("LTM");
        assertEquals(dynamicConvertor.getLte(), now.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).format(dateTimeFormatter) + SPACE + DATE_FORMAT_LTE);
        assertEquals(dynamicConvertor.getGte(), last3Month.with(TemporalAdjusters.firstDayOfMonth()).format(dateTimeFormatter) + SPACE + DATE_FORMAT_GTE);
    }

    @Test
    public void getLSM() {
        LocalDateTime last6Months = now.minusMonths(6);
        dynamicConvertor = BuilderUtil.dynamicDecipher("LSM");
        assertEquals(dynamicConvertor.getLte(), now.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).format(dateTimeFormatter) + SPACE + DATE_FORMAT_LTE);
        assertEquals(dynamicConvertor.getGte(), last6Months.with(TemporalAdjusters.firstDayOfMonth()).format(dateTimeFormatter) + SPACE + DATE_FORMAT_GTE);
    }

    @Test
    public void getLM() {
        LocalDateTime lastMonth = now.minusMonths(1);
        dynamicConvertor = BuilderUtil.dynamicDecipher("LM");
        assertEquals(dynamicConvertor.getLte(),lastMonth.with(TemporalAdjusters.lastDayOfMonth()).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_LTE);
        assertEquals(dynamicConvertor.getGte(),lastMonth.with(TemporalAdjusters.firstDayOfMonth()).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
    }

    @Test
    public void getLQ() {
        YearQuarter quarter = YearQuarter.now();
        dynamicConvertor = BuilderUtil.dynamicDecipher("LQ");
        assertEquals(dynamicConvertor.getLte(),quarter.minusQuarters(1).atEndOfQuarter().toString()+ SPACE + DATE_FORMAT_LTE);
        assertEquals(dynamicConvertor.getGte(),quarter.minusQuarters(1).atDay(1).toString()+ SPACE + DATE_FORMAT_GTE);
    }

    @Test
    public void getLW() {
        DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
        LocalDateTime lastWeek = now.minusWeeks(1);
        LocalDateTime startOfWeek =
            lastWeek.with(TemporalAdjusters.previousOrSame(firstDayOfWeek.plus(1)));
        LocalDateTime endOfWeek = lastWeek.with(TemporalAdjusters.nextOrSame(firstDayOfWeek));
        dynamicConvertor = BuilderUtil.dynamicDecipher("LW");
        assertEquals(dynamicConvertor.getLte(),endOfWeek.format(dateTimeFormatter)+ SPACE + DATE_FORMAT_LTE);
        assertEquals(dynamicConvertor.getGte(),startOfWeek.format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
    }

    @Test
    public void getLSW()    {
        DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
        LocalDateTime lastWeek = now.minusWeeks(6);
        dynamicConvertor = BuilderUtil.dynamicDecipher("LSW");
        assertEquals(dynamicConvertor.getLte(),now.with(DayOfWeek.MONDAY).minusDays(1).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_LTE);
        assertEquals(dynamicConvertor.getGte(),lastWeek.with(DayOfWeek.MONDAY).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
    }

    @Test
    public void getTW() {
        DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
        LocalDateTime lastWeek = now;
        LocalDateTime startOfWeek =
            lastWeek.with(TemporalAdjusters.previousOrSame(firstDayOfWeek.plus(1)));
        dynamicConvertor = BuilderUtil.dynamicDecipher("TW");
        assertEquals(dynamicConvertor.getLte(),now.format(dateTimeFormatter)+ SPACE + DATE_FORMAT_LTE);
        assertEquals(dynamicConvertor.getGte(),startOfWeek.format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
    }

    @Test
    public void getLTW()    {
        LocalDateTime last2Week = now.minusWeeks(2);
        dynamicConvertor = BuilderUtil.dynamicDecipher("LTW");
        assertEquals(dynamicConvertor.getLte(),now.with(DayOfWeek.MONDAY).minusDays(1).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_LTE);
        assertEquals(dynamicConvertor.getGte(),last2Week.with(DayOfWeek.MONDAY).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
    }

    @Test
    public void getPriorYesterday() {
        LocalDateTime yesterday = now.minusDays(1);
        LocalDateTime dayBeforeYesterday = yesterday.minusDays(1);
        dynamicConvertor = BuilderUtil.dynamicDecipherForPrior("Yesterday");
        assertEquals(dynamicConvertor.getLte(),dayBeforeYesterday.format(dateTimeFormatter)+ SPACE + DATE_FORMAT_LTE);
        assertEquals(dynamicConvertor.getGte(),dayBeforeYesterday.format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
    }

    @Test
    public void getPriorYTD()   {
        LocalDateTime firstDayOfYear = now.with(TemporalAdjusters.firstDayOfYear());
        int calculatedDayDifference  = now.getDayOfYear()-firstDayOfYear.getDayOfYear();
        LocalDateTime priorDayOfYear = firstDayOfYear.minusDays(calculatedDayDifference);
        dynamicConvertor = BuilderUtil.dynamicDecipherForPrior("YTD");
        assertEquals(dynamicConvertor.getLte(),firstDayOfYear.minusDays(1).with(TemporalAdjusters.lastDayOfYear()).format(dateTimeFormatter) + SPACE + DATE_FORMAT_LTE);
        assertEquals(dynamicConvertor.getGte(),priorDayOfYear.minusDays(1).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
    }

    @Test
    public void getPriorMTD()   {
        LocalDateTime firstDayOfMonth = now.with(TemporalAdjusters.firstDayOfMonth());
        int calculatedDayDifference = now.getDayOfMonth()- firstDayOfMonth.getDayOfMonth();
        LocalDateTime lastMonth = firstDayOfMonth.minusDays(1);
        LocalDateTime priorDayOfMonth = lastMonth.minusDays(calculatedDayDifference);
        dynamicConvertor = BuilderUtil.dynamicDecipherForPrior("MTD");
        assertEquals(dynamicConvertor.getLte(),lastMonth.format(dateTimeFormatter)+ SPACE + DATE_FORMAT_LTE);
        assertEquals(dynamicConvertor.getGte(),priorDayOfMonth.format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
    }

    @Test
    public void getPriorLTM()   {
        LocalDateTime priorLast3Month = now.minusMonths(3);
        LocalDateTime last3Month = priorLast3Month.minusMonths(3);
        dynamicConvertor = BuilderUtil.dynamicDecipherForPrior("LTM");
        assertEquals(dynamicConvertor.getLte(),priorLast3Month.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_LTE);
        assertEquals(dynamicConvertor.getGte(),last3Month.with(TemporalAdjusters.firstDayOfMonth()).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
    }

    @Test
    public void getPriorLSM()   {
        LocalDateTime priorLast6Month = now.minusMonths(6);
        LocalDateTime last6Months = priorLast6Month.minusMonths(6);
        dynamicConvertor = BuilderUtil.dynamicDecipherForPrior("LSM");
        assertEquals(dynamicConvertor.getLte(),priorLast6Month.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_LTE);
        assertEquals(dynamicConvertor.getGte(),last6Months.with(TemporalAdjusters.firstDayOfMonth()).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
    }

    @Test
    public void getPriorLM()    {
        LocalDateTime priorLastMonth = now.minusMonths(1);
        LocalDateTime lastMonth = priorLastMonth.minusMonths(1);
        dynamicConvertor = BuilderUtil.dynamicDecipherForPrior("LM");
        assertEquals(dynamicConvertor.getLte(),lastMonth.with(TemporalAdjusters.lastDayOfMonth()).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_LTE);
        assertEquals(dynamicConvertor.getGte(),lastMonth.with(TemporalAdjusters.firstDayOfMonth()).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
    }

    @Test
    public void getPriorLY()    {
        LocalDateTime priorLastYear = now.minusYears(1);
        LocalDateTime lastYear = priorLastYear.minusYears(1);
        dynamicConvertor = BuilderUtil.dynamicDecipherForPrior("LY");
        assertEquals(dynamicConvertor.getLte(),lastYear.with(TemporalAdjusters.lastDayOfYear()).format(dateTimeFormatter) + SPACE + DATE_FORMAT_LTE);
        assertEquals(dynamicConvertor.getGte(),lastYear.with(TemporalAdjusters.firstDayOfYear()).format(dateTimeFormatter) + SPACE + DATE_FORMAT_GTE);
    }

    @Test
    public void getPriorLQ()    {
        YearQuarter lastQuarter = YearQuarter.now().minusQuarters(1);
        YearQuarter priorQuarter = lastQuarter.now().minusQuarters(1);
        dynamicConvertor = BuilderUtil.dynamicDecipherForPrior("LQ");
        assertEquals(dynamicConvertor.getLte(),priorQuarter.minusQuarters(1).atEndOfQuarter().toString()+ SPACE + DATE_FORMAT_LTE);
        assertEquals(dynamicConvertor.getGte(),priorQuarter.minusQuarters(1).atDay(1).toString()+ SPACE + DATE_FORMAT_GTE);
    }

    @Test
    public void getPriorLW()    {
        LocalDateTime priorLastWeek = now.minusWeeks(2);
        LocalDateTime startOfWeek =
            priorLastWeek.with(TemporalAdjusters.previousOrSame(firstDayOfWeek.plus(1)));
        LocalDateTime endOfWeek = priorLastWeek.with(TemporalAdjusters.nextOrSame(firstDayOfWeek));
        dynamicConvertor = BuilderUtil.dynamicDecipherForPrior("LW");
        assertEquals(dynamicConvertor.getLte(),endOfWeek.format(dateTimeFormatter)+ SPACE + DATE_FORMAT_LTE);
        assertEquals(dynamicConvertor.getGte(),startOfWeek.format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
    }

    @Test
    public void getPriorLSW()   {
        LocalDateTime last6Week = now.minusWeeks(6);
        LocalDateTime priorlast6Week = last6Week.minusWeeks(6);
        dynamicConvertor = BuilderUtil.dynamicDecipherForPrior("LSW");
        assertEquals(dynamicConvertor.getLte(),last6Week.with(DayOfWeek.MONDAY).minusDays(1).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_LTE);
        assertEquals(dynamicConvertor.getGte(),priorlast6Week.with(DayOfWeek.MONDAY).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
    }

    @Test
    public void getPriorTW()    {
        LocalDateTime startOfWeek =
            now.with(TemporalAdjusters.previousOrSame(firstDayOfWeek.plus(1)));
        Long calculatedDayDifference = ChronoUnit.DAYS.between(startOfWeek,now);
        LocalDateTime week = startOfWeek.minusHours(24);
        LocalDateTime lastWeek = week.minusDays(calculatedDayDifference);
        dynamicConvertor = BuilderUtil.dynamicDecipherForPrior("TW");
        assertEquals(dynamicConvertor.getLte(),week.format(dateTimeFormatter)+ SPACE + DATE_FORMAT_LTE);
        assertEquals(dynamicConvertor.getGte(),lastWeek.format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
    }

    @Test
    public void getPriorLTW()   {
        LocalDateTime last2Week = now.minusWeeks(2);
        LocalDateTime priorLast2Week = last2Week.minusWeeks(2);
        dynamicConvertor = BuilderUtil.dynamicDecipherForPrior("LTW");
        assertEquals(dynamicConvertor.getLte(),last2Week.with(DayOfWeek.MONDAY).minusDays(1).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_LTE);
        assertEquals(dynamicConvertor.getGte(),priorLast2Week.with(DayOfWeek.MONDAY).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
    }

}
