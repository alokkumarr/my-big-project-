package model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.Locale;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;

import com.synchronoss.DynamicConvertor;
import org.threeten.extra.YearQuarter;
public class TransportUtils {

    public static String buildDSK (String dataSecurityKey)throws JsonProcessingException, IOException
    {
        StringBuilder dskMkString = new StringBuilder();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
        JsonNode objectNode = objectMapper.readTree(dataSecurityKey);
        DataSecurityKey dataSecurityKeyNode =
                objectMapper.treeToValue(objectNode,
                        DataSecurityKey.class);
        StringBuilder builder  = null;
        int dataSecuritySize = dataSecurityKeyNode.getDataSecuritykey().size();
        for (int i =0; i<dataSecurityKeyNode.getDataSecuritykey().size();i++)
        {
            builder=   new StringBuilder();
            builder.append(dataSecurityKeyNode.getDataSecuritykey().get(i).getName());
            builder.append(" IN (");
            int valueSize = dataSecurityKeyNode.getDataSecuritykey().get(i).getValues().size();
            for (int j = 0; j<valueSize;j++){
                if (j!=valueSize-1)
                {
                    /* since this is having potential bug in initial implementation, So appending single "'" 
                    to avoid sql query error */
                    builder.append("'"+dataSecurityKeyNode.getDataSecuritykey().get(i).getValues().get(j)+"'");
                    builder.append(",");
                }
                else {
                    builder.append("'"+dataSecurityKeyNode.getDataSecuritykey().get(i).getValues().get(j)+"'");
                }
            }
            builder.append(")");
            if (i!=dataSecuritySize-1){
                builder.append(" AND ");
            }
            dskMkString.append(builder);
        }
        return dskMkString.toString();
    }

    public static DynamicConvertor dynamicDecipher(String dynamic)

    {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String DATE_FORMAT_LTE = "23:59:59";
        String DATE_FORMAT_GTE = "00:00:00";
        String SPACE = " ";
        DynamicConvertor dynamicConvertor = new DynamicConvertor();

        switch (dynamic) {
            case "YTD": {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime firstDay = now.with(TemporalAdjusters.firstDayOfYear());
                dynamicConvertor.setLte(now.plusDays(1).format(dateTimeFormatter) + SPACE + DATE_FORMAT_LTE);
                dynamicConvertor.setGte(firstDay.minusDays(1).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
                break;
            }
            case "MTD": {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime firstDayOfMonth = now.with(TemporalAdjusters.firstDayOfMonth());
                dynamicConvertor.setLte(now.plusDays(1).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_LTE);
                dynamicConvertor.setGte(firstDayOfMonth.minusDays(1).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
                break;
            }
            case "LTM": {

                LocalDateTime now = LocalDateTime.now();
                LocalDateTime last3Month = now.minusMonths(3);
                dynamicConvertor.setLte(now.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).plusDays(1).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_LTE);
                dynamicConvertor.setGte(last3Month.with(TemporalAdjusters.firstDayOfMonth()).minusDays(1).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
                break;
            }
            case "LSM": {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime last6Months = now.minusMonths(6);
                dynamicConvertor.setLte(now.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).plusDays(1).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_LTE);
                dynamicConvertor.setGte(last6Months.with(TemporalAdjusters.firstDayOfMonth()).minusDays(1).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
                break;
            }
            case "LM": {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime lastMonth = now.minusMonths(1);
                dynamicConvertor.setLte(lastMonth.with(TemporalAdjusters.lastDayOfMonth()).plusDays(1).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_LTE);
                dynamicConvertor.setGte(lastMonth.with(TemporalAdjusters.firstDayOfMonth()).minusDays(1).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
                break;
            }
            case "LQ": {
                YearQuarter quarter = YearQuarter.now();
                dynamicConvertor.setLte(quarter.minusQuarters(1).atEndOfQuarter().toString()+ SPACE + DATE_FORMAT_LTE);
                dynamicConvertor.setGte(quarter.minusQuarters(1).atDay(1).toString()+ SPACE + DATE_FORMAT_GTE);
                break;
            }
            case "LW": {
                DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime lastWeek = now.minusWeeks(1);
                LocalDateTime startOfWeek =
                        lastWeek.with(TemporalAdjusters.previousOrSame(firstDayOfWeek.plus(1)));
                LocalDateTime endOfWeek = lastWeek.with(TemporalAdjusters.nextOrSame(firstDayOfWeek));
                dynamicConvertor.setLte(endOfWeek.plusDays(1).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_LTE);
                dynamicConvertor.setGte(startOfWeek.minusDays(1).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
                break;
            }
            case "LSW": {
                DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime lastWeek = now.minusWeeks(6);
                LocalDateTime startOfWeek =
                        lastWeek.with(TemporalAdjusters.previousOrSame(firstDayOfWeek.plus(1)));
                LocalDateTime endOfWeek = lastWeek.with(TemporalAdjusters.nextOrSame(firstDayOfWeek));
                dynamicConvertor.setLte(endOfWeek.plusDays(1).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_LTE);
                dynamicConvertor.setGte(startOfWeek.minusDays(1).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
                break;
            }
            case "TW": {
                DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime lastWeek = now;
                LocalDateTime startOfWeek =
                        lastWeek.with(TemporalAdjusters.previousOrSame(firstDayOfWeek.plus(1)));
                dynamicConvertor.setLte(now.plusDays(1).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_LTE);
                dynamicConvertor.setGte(startOfWeek.minusDays(1).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
                break;
            }
            case "LTW": {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime last2Week = now.minusWeeks(2);
                DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
                LocalDateTime startOfWeek =
                        last2Week.with(TemporalAdjusters.previousOrSame(firstDayOfWeek.plus(2)));
                dynamicConvertor.setLte(startOfWeek.plusWeeks(2).with(TemporalAdjusters.previous(DayOfWeek.SUNDAY)).plusDays(1).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_LTE);
                dynamicConvertor.setGte(startOfWeek.minusDays(1).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
                break;
            }
            default : throw new IllegalArgumentException(dynamic + " not present");

        }

        return dynamicConvertor;
    }

}
