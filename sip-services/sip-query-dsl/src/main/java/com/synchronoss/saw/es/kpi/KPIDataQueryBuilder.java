package com.synchronoss.saw.es.kpi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.synchronoss.saw.model.DataSecurityKey;
import com.synchronoss.saw.model.DataSecurityKeyDef;
import com.synchronoss.saw.model.kpi.DataField;
import com.synchronoss.saw.model.kpi.Filter;
import com.synchronoss.saw.model.kpi.KPIBuilder;
import com.synchronoss.saw.model.kpi.KPIExecutionObject;
import com.synchronoss.saw.model.kpi.Model;
import com.synchronoss.saw.util.BuilderUtil;
import com.synchronoss.saw.util.DynamicConvertor;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KPIDataQueryBuilder {

  private static final Logger logger = LoggerFactory.getLogger(KPIDataQueryBuilder.class);
  private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd";

  String jsonString;
  DataSecurityKey dataSecurityKeyNode;

  public KPIDataQueryBuilder(DataSecurityKey dataSecurityKeyNode) {
    super();
    this.dataSecurityKeyNode = dataSecurityKeyNode;
  }

  public String getJsonString() {
    return jsonString;
  }
  /**
   * This method is used to generate the query to build elastic search query for<br>
   * KPI builder data set
   *
   * @return query
   * @throws IOException
   * @throws JsonProcessingException
   * @throws ProcessingException
   */
  public KPIExecutionObject buildQuery(KPIBuilder kpiBuilder)
      throws IOException, ProcessingException {

    if (kpiBuilder.getKpi().getFilters() == null) {
      throw new NullPointerException(
          "Please add filter[] block.It can be empty but these blocks are important.");
    }
    KPIExecutionObject kpiExecutionObject = new KPIExecutionObject();
    kpiExecutionObject.setCurrentSearchSourceBuilder(buildKPIQuery(kpiBuilder, true));
    kpiExecutionObject.setPriorSearchSourceBuilder(buildKPIQuery(kpiBuilder, false));
    kpiExecutionObject.setEsRepository(kpiBuilder.getKpi().getEsRepository());
    kpiExecutionObject.setDataFields(kpiBuilder.getKpi().getDataFields());
    return kpiExecutionObject;
  }

  /**
   * @param kpiBuilder
   * @param current
   * @return
   */
  private SearchSourceBuilder buildKPIQuery(KPIBuilder kpiBuilder, boolean current)
      throws IOException {
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.size(0);
    List<Filter> filters = kpiBuilder.getKpi().getFilters();
    List<QueryBuilder> builder = new ArrayList<QueryBuilder>();
    final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
    for (Filter item : filters) {
      DynamicConvertor dynamicConvertor = null;
      if (item.getType().value().equals(Filter.Type.DATE.value())
          || item.getType().value().equals(Filter.Type.TIMESTAMP.value())) {
        if (item.getModel().getPreset() != null
            && !item.getModel().getPreset().value().equals(Model.Preset.NA.toString())) {
          logger.info("Build KPI value with preset value : " + item.getModel().getPreset().value());
          if (current)
            dynamicConvertor =
                BuilderUtil.dynamicDecipher(item.getModel().getPreset().value().toUpperCase());
          else
            dynamicConvertor =
                BuilderUtil.dynamicDecipherForPrior(
                    item.getModel().getPreset().value().toUpperCase());
          RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
          if (item.getType().value().equals(Filter.Type.DATE.value())) {
            rangeQueryBuilder.format(DATE_FORMAT);
          }
          rangeQueryBuilder.lte(dynamicConvertor.getLte());
          rangeQueryBuilder.gte(dynamicConvertor.getGte());
          builder.add(rangeQueryBuilder);
        } else {
          logger.info(
              "Build KPI value with custom range value : " + item.getModel().getPreset().value());
          if (current) {
            RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
            if (item.getType().value().equals(Filter.Type.DATE.value())) {
              rangeQueryBuilder.format(DATE_FORMAT);
            }
            rangeQueryBuilder.lte(item.getModel().getLte());
            rangeQueryBuilder.gte(item.getModel().getGte());
            builder.add(rangeQueryBuilder);
          } else {
            RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
            if (item.getType().value().equals(Filter.Type.DATE.value())) {
              rangeQueryBuilder.format(DATE_FORMAT);
            }
            dynamicConvertor = calculatePriorDateCustomRange(item.getModel());
            rangeQueryBuilder.lte(dynamicConvertor.getLte());
            rangeQueryBuilder.gte(dynamicConvertor.getGte());
            builder.add(rangeQueryBuilder);
          }
        }
      }
      ObjectMapper objectMapper = null;
      if (dataSecurityKeyNode != null && dataSecurityKeyNode.getDataSecuritykey() != null) {
        for (DataSecurityKeyDef dsk : dataSecurityKeyNode.getDataSecuritykey()) {
          TermsQueryBuilder dataSecurityBuilder =
              new TermsQueryBuilder(dsk.getName().concat(BuilderUtil.SUFFIX), dsk.getValues());
          builder.add(dataSecurityBuilder);
        }
      }
      // make the query based on the filter given
      if (item.getType().value().equals(Filter.Type.STRING.value())) {
        builder = KPIQueryBuilderUtil.stringFilterKPI(item, builder);
      }
      if ((item.getType().value().toLowerCase().equals(Filter.Type.DOUBLE.value().toLowerCase())
              || item.getType().value().toLowerCase().equals(Filter.Type.INT.value().toLowerCase()))
          || item.getType().value().toLowerCase().equals(Filter.Type.FLOAT.value().toLowerCase())
          || item.getType().value().toLowerCase().equals(Filter.Type.LONG.value().toLowerCase())) {
        builder = KPIQueryBuilderUtil.numericFilterKPI(item, builder);
      }
    }
    builder.forEach(
        item -> {
          boolQueryBuilder.must(item);
        });
    searchSourceBuilder.query(boolQueryBuilder);
    List<DataField> dataFields = kpiBuilder.getKpi().getDataFields();
    for (DataField dataField : dataFields) {
      searchSourceBuilder =
          KPIQueryBuilderUtil.aggregationBuilderDataFieldKPI(dataField, searchSourceBuilder);
    }
    return searchSourceBuilder;
  }

  /**
   * This method is used to calculate the KPI custom range filter for prior data.
   *
   * @param model
   * @return
   */
  private DynamicConvertor calculatePriorDateCustomRange(Model model) {
    final List<String> dateFormats = Arrays.asList("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd");
    Date gte = null;
    Date lte = null;
    DynamicConvertor dynamicConvertor = new DynamicConvertor();
    for (String format : dateFormats) {
      SimpleDateFormat sdf = new SimpleDateFormat(format);
      try {
        gte = sdf.parse(model.getGte());
        lte = sdf.parse(model.getLte());
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        cal.setTime(lte);
        long t1 = cal.getTimeInMillis();
        cal.setTime(gte);
        long diff = Math.abs(t1 - (cal.getTimeInMillis() - 1000));
        long startTime = cal.getTimeInMillis();
        cal.setTimeInMillis(cal.getTimeInMillis() - 1000);
        dynamicConvertor.setLte(sdf.format(cal.getTime()));
        cal.setTimeInMillis(startTime - diff);
        dynamicConvertor.setGte(sdf.format(cal.getTime()));
        return dynamicConvertor;
      } catch (ParseException e) {
        // intentionally empty
      }
    }
    throw new IllegalArgumentException(
        "Invalid input for date. Given '"
            + gte
            + ", "
            + lte
            + "', expecting format yyyy-MM-dd HH:mm:ss or yyyy-MM-dd.");
  }
}
