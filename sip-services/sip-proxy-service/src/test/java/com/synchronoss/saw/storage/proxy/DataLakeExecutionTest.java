package com.synchronoss.saw.storage.proxy;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.model.Filter;
import com.synchronoss.saw.model.Filter.Type;
import com.synchronoss.saw.model.Model;
import com.synchronoss.saw.model.SipQuery;
import com.synchronoss.saw.storage.proxy.service.DataLakeExecutionServiceImpl;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public class DataLakeExecutionTest {

  private static final Logger logger = LoggerFactory.getLogger(DataLakeExecutionTest.class);
  Analysis analysis;

  DataLakeExecutionServiceImpl dl = new DataLakeExecutionServiceImpl();

  @Before
  public void setUpData() throws IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(
        classLoader
            .getResource("com/synchronoss/saw/storage/proxy/sample-dl-query.json")
            .getPath());
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    analysis = objectMapper.readValue(file, Analysis.class);
  }

  @Test
  public void testApplyRunTimeFilterForQuery() {
    SipQuery sipQuery = analysis.getSipQuery();
    List<Object> runTimeFilter1 = new ArrayList<>();
    if (!CollectionUtils.isEmpty(sipQuery.getFilters())) {
      sipQuery.getFilters().forEach(filter -> {
        String filterValue = dl.getRunTimeFilters(filter);
        if (!StringUtils.isEmpty(filterValue)) {
          runTimeFilter1.add(filterValue);
        }
      });
    }
    logger.trace("sipQuery : {}", sipQuery);
    String query = dl.applyRunTimeFilterForQuery(sipQuery.getQuery(), runTimeFilter1, 0);
    System.out.println(query);
    Assert.assertEquals(
        "select integer, string from SALES"
            + " where string IN ('string 1', 'string 2') and SALES.integer = '100'",
        query.trim());

    // Assert for one runtime filter object
    sipQuery.setQuery("select integer, string from SALES where date = ?");
    sipQuery.getFilters().remove(1);
    Filter filter = getFilter(Type.DATE, "date",
        "26-02-2020 18:00:00", true);
    sipQuery.getFilters().set(0, filter);
    logger.trace("sipQuery : {}", sipQuery);
    List<Object> runTimeFilter2 = new ArrayList<>();
    if (!CollectionUtils.isEmpty(sipQuery.getFilters())) {
      sipQuery.getFilters().forEach(filt -> {
        String filterValue = dl.getRunTimeFilters(filt);
        if (!StringUtils.isEmpty(filterValue)) {
          runTimeFilter2.add(filterValue);
        }
      });
    }
    query = dl.applyRunTimeFilterForQuery(sipQuery.getQuery(), runTimeFilter2, 0);
    Assert.assertEquals("select integer, string from SALES"
        + " where date = '26-02-2020 18:00:00'", query.trim());

    //Assert for one runTime filter and one regular filter
    sipQuery.setQuery("select integer, string from SALES where double = ? AND integer = 100");
    filter = getFilter(Type.DOUBLE, "double", "26.4500", true);
    sipQuery.getFilters().set(0, filter);
    filter = getFilter(Type.INTEGER, "integer", "100", false);
    sipQuery.getFilters().add(filter);
    logger.trace("sipQuery : {}", sipQuery);
    List<Object> runTimeFilter3 = new ArrayList<>();
    if (!CollectionUtils.isEmpty(sipQuery.getFilters())) {
      sipQuery.getFilters().forEach(filt3 -> {
        String filterValue = dl.getRunTimeFilters(filt3);
        if (!StringUtils.isEmpty(filterValue)) {
          runTimeFilter3.add(filterValue);
        }
      });
    }
    query = dl.applyRunTimeFilterForQuery(sipQuery.getQuery(), runTimeFilter3, 0);
    Assert.assertEquals(
        "select integer, string from SALES where double = '26.4500' AND integer = 100",
        query.trim());

  }

  @Test
  public void testGetRunTimeFilters() {
    //Assert for two run time filters
    SipQuery sipQuery = analysis.getSipQuery();
    logger.trace("sipQuery : {}", sipQuery);
    List<Object> runTimeFilter1 = new ArrayList<>();
    if (!CollectionUtils.isEmpty(sipQuery.getFilters())) {
      sipQuery.getFilters().forEach(filt3 -> {
        String filterValue = dl.getRunTimeFilters(filt3);
        if (!StringUtils.isEmpty(filterValue)) {
          runTimeFilter1.add(filterValue);
        }
      });
    }
    Assert.assertEquals(2, runTimeFilter1.size());
    Assert.assertEquals("'string 1', 'string 2'", runTimeFilter1.get(0));
    Assert.assertEquals("'100'", runTimeFilter1.get(1));

    // Assert for one runtime filter object
    sipQuery.setQuery("select integer, string from SALES where date = ?");
    sipQuery.getFilters().remove(1);
    Filter filter = getFilter(Type.DATE, "date", "26-02-2020 18:00:00", true);
    sipQuery.getFilters().set(0, filter);
    logger.trace("sipQuery : {}", sipQuery);
    List<Object> runTimeFilter2 = new ArrayList<>();
    if (!CollectionUtils.isEmpty(sipQuery.getFilters())) {
      sipQuery.getFilters().forEach(filt -> {
        String filterValue = dl.getRunTimeFilters(filt);
        if (!StringUtils.isEmpty(filterValue)) {
          runTimeFilter2.add(filterValue);
        }
      });
    }
    Assert.assertEquals(1, runTimeFilter2.size());
    Assert.assertEquals("'26-02-2020 18:00:00'", runTimeFilter2.get(0));

    //Assert for one runTime filter and one regular filter
    sipQuery.setQuery("select integer, string from SALES where double = ? AND integer = 100");
    filter = getFilter(Type.DOUBLE, "double", "26.4500", true);
    sipQuery.getFilters().set(0, filter);
    filter = getFilter(Type.INTEGER, "integer", "100", false);
    sipQuery.getFilters().add(filter);
    logger.trace("sipQuery : {}", sipQuery);
    List<Object> runTimeFilter3 = new ArrayList<>();
    if (!CollectionUtils.isEmpty(sipQuery.getFilters())) {
      sipQuery.getFilters().forEach(filt3 -> {
        String filterValue = dl.getRunTimeFilters(filt3);
        if (!StringUtils.isEmpty(filterValue)) {
          runTimeFilter3.add(filterValue);
        }
      });
    }
    Assert.assertEquals(1, runTimeFilter3.size());
    Assert.assertEquals("'26.4500'", runTimeFilter3.get(0));

  }

  public Filter getFilter(Type type, String colName, String value, Boolean isRunTimeFilter) {
    Filter filter = new Filter();
    filter.setType(type);
    filter.setColumnName(colName);
    filter.setIsRuntimeFilter(isRunTimeFilter ? Boolean.TRUE : Boolean.FALSE);
    Model model = new Model();
    model.setModelValues(Collections.singletonList(value));
    filter.setModel(model);
    return filter;
  }
}
