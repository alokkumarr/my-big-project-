package model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.DynamicConvertor;
import com.synchronoss.bda.sip.jwt.token.ProductModuleFeature;
import com.synchronoss.bda.sip.jwt.token.ProductModules;
import com.synchronoss.bda.sip.jwt.token.Products;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.extra.YearQuarter;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
public class TransportUtils {
  private static final Logger logger = LoggerFactory.getLogger(TransportUtils.class);

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
            builder.append("upper("+dataSecurityKeyNode.getDataSecuritykey().get(i).getName()+")");
            builder.append(" IN (");
            int valueSize = dataSecurityKeyNode.getDataSecuritykey().get(i).getValues().size();
            for (int j = 0; j<valueSize;j++){
                if (j!=valueSize-1)
                {
                    /* since this is having potential bug in initial implementation, So appending single "'" 
                    to avoid sql query error */
                    builder.append("'"+dataSecurityKeyNode.getDataSecuritykey().get(i).getValues().get(j).toUpperCase()+"'");
                    builder.append(",");
                }
                else {
                    builder.append("'"+dataSecurityKeyNode.getDataSecuritykey().get(i).getValues().get(j).toUpperCase()+"'");
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
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1);

        switch (dynamic) {
            case "Yesterday" :
                LocalDateTime yesterday = now.minusDays(1);
                dynamicConvertor.setLte(now.format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
                dynamicConvertor.setGte(yesterday.format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
                break;
            case "Today": {
                LocalDateTime today = now;
                dynamicConvertor.setLte(tomorrow.format(dateTimeFormatter) + SPACE + DATE_FORMAT_GTE);
                dynamicConvertor.setGte(today.format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
                break;
            }
            case "YTD": {
                LocalDateTime firstDay = now.with(TemporalAdjusters.firstDayOfYear());
                dynamicConvertor.setLte(tomorrow.format(dateTimeFormatter) + SPACE + DATE_FORMAT_GTE);
                dynamicConvertor.setGte(firstDay.format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
                break;
            }
            case "MTD": {
                LocalDateTime firstDayOfMonth = now.with(TemporalAdjusters.firstDayOfMonth());
                dynamicConvertor.setLte(tomorrow.format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
                dynamicConvertor.setGte(firstDayOfMonth.format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
                break;
            }
            case "LTM": {
                LocalDateTime last3Month = now.minusMonths(3);
                dynamicConvertor.setLte(now.with(TemporalAdjusters.firstDayOfMonth()).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
                dynamicConvertor.setGte(last3Month.with(TemporalAdjusters.firstDayOfMonth()).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
                break;
            }
            case "LSM": {
                LocalDateTime last6Months = now.minusMonths(6);
                dynamicConvertor.setLte(now.with(TemporalAdjusters.firstDayOfMonth()).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
                dynamicConvertor.setGte(last6Months.with(TemporalAdjusters.firstDayOfMonth()).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
                break;
            }
            case "LY": {
                LocalDateTime currentDayOflastYearDate = now.minusMonths(12);
                dynamicConvertor.setLte(now.with(TemporalAdjusters.firstDayOfYear()).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
                dynamicConvertor.setGte(currentDayOflastYearDate.with(TemporalAdjusters.firstDayOfYear()).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
                break;
            }
            case "LM": {
                LocalDateTime lastMonth = now.minusMonths(1);
                dynamicConvertor.setLte(now.with(TemporalAdjusters.firstDayOfMonth()).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
                dynamicConvertor.setGte(lastMonth.with(TemporalAdjusters.firstDayOfMonth()).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
                break;
            }
            case "LQ": {
                YearQuarter quarter = YearQuarter.now();
                dynamicConvertor.setLte(quarter.minusQuarters(1).atEndOfQuarter().plusDays(1).toString()+ SPACE + DATE_FORMAT_GTE);
                dynamicConvertor.setGte(quarter.minusQuarters(1).atDay(1).toString()+ SPACE + DATE_FORMAT_GTE);
                break;
            }
            case "LW": {
                DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
                LocalDateTime lastWeek = now.minusWeeks(1);
                LocalDateTime startOfWeek =
                        lastWeek.with(TemporalAdjusters.previousOrSame(firstDayOfWeek.plus(1)));
                LocalDateTime endOfWeek = lastWeek.with(TemporalAdjusters.nextOrSame(firstDayOfWeek));
                dynamicConvertor.setLte(endOfWeek.plusDays(1).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
                dynamicConvertor.setGte(startOfWeek.format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
                break;
            }
            case "LSW": {
                DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
                LocalDateTime lastWeek = now.minusWeeks(6);
                LocalDateTime startOfWeek =
                        lastWeek.with(TemporalAdjusters.previousOrSame(firstDayOfWeek.plus(1)));
                LocalDateTime endOfWeek = lastWeek.with(TemporalAdjusters.nextOrSame(firstDayOfWeek));
                dynamicConvertor.setLte(endOfWeek.plusDays(1).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
                dynamicConvertor.setGte(startOfWeek.format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
                break;
            }
            case "TW": {
                DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
                LocalDateTime lastWeek = now;
                LocalDateTime startOfWeek =
                        lastWeek.with(TemporalAdjusters.previousOrSame(firstDayOfWeek.plus(1)));
                dynamicConvertor.setLte(tomorrow.format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
                dynamicConvertor.setGte(startOfWeek.format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
                break;
            }
            case "LTW": {
                LocalDateTime last2Week = now.minusWeeks(2);
                dynamicConvertor.setLte(now.with(DayOfWeek.MONDAY).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
                dynamicConvertor.setGte(last2Week.with(DayOfWeek.MONDAY).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
                break;
            }
            default : throw new IllegalArgumentException(dynamic + " not present");

        }

        return dynamicConvertor;
    }

  /**
   * extract the my analysis code from the token and if category Id belongs to my Analysis return
   * true
   *
   * @param products
   * @param categoryId
   * @return
   */
  public static boolean checkIfPrivateAnalysis(List<Object> products, String categoryId) {
    logger.debug("checking category is private for cateory id:{}", categoryId);
    for (Object prod : products) {
      Products product = (Products) prod;
      List<ProductModules> productModules = product.getProductModules();
      for (ProductModules prodMod : productModules) {
        List<ProductModuleFeature> productModuleFeature = prodMod.getProdModFeature();
        for (ProductModuleFeature prodModFeat : productModuleFeature) {
          if (prodModFeat.getProdModFeatureName().equalsIgnoreCase("My Analysis")) {
            List<ProductModuleFeature> productModuleSubfeatures =
                prodModFeat.getProductModuleSubFeatures();
            for (ProductModuleFeature prodModSubFeat : productModuleSubfeatures) {
              String cat = String.valueOf(prodModSubFeat.getProdModFeatureID());
              if (categoryId.equalsIgnoreCase(cat)) {
                logger.debug("Category id {} is a private category", categoryId);
                return true;
              }
            }
          }
        }
      }
    }
    logger.debug("Category id {}  is not a private category", categoryId);
    return false;
  }
}
