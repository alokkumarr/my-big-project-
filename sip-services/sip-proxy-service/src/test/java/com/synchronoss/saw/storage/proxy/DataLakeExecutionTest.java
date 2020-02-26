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
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    List<Object> runTimeFilters = dl.getRunTimeFilters(sipQuery);
    logger.trace("sipQuery : {}", sipQuery);
    String query = dl.applyRunTimeFilterForQuery(sipQuery.getQuery(), runTimeFilters, 0);
    Assert.assertEquals(
        "select integer, string from SALES"
            + " where string =  \"string 1\"  and SALES.integer =  \"100\"",
        query.trim());

    // Assert for one runtime filter object
    sipQuery.setQuery("select integer, string from SALES where date = ?");
    sipQuery.getFilters().remove(1);
    Filter filter = getFilter(Type.DATE, "date",
        "26-02-2020 18:00:00", true);
    sipQuery.getFilters().set(0, filter);
    logger.trace("sipQuery : {}", sipQuery);
    runTimeFilters = dl.getRunTimeFilters(sipQuery);
    query = dl.applyRunTimeFilterForQuery(sipQuery.getQuery(), runTimeFilters, 0);
    Assert.assertEquals("select integer, string from SALES"
        + " where date =  \"26-02-2020 18:00:00\"", query.trim());

    //Assert for one runTime filter and one regular filter
    sipQuery.setQuery("select integer, string from SALES where double = ? AND integer = 100");
    filter = getFilter(Type.DOUBLE, "double", "26.4500", true);
    sipQuery.getFilters().set(0, filter);
    filter = getFilter(Type.INTEGER, "integer", "100", false);
    sipQuery.getFilters().add(filter);
    logger.trace("sipQuery : {}", sipQuery);
    runTimeFilters = dl.getRunTimeFilters(sipQuery);
    query = dl.applyRunTimeFilterForQuery(sipQuery.getQuery(), runTimeFilters, 0);
    Assert.assertEquals(
        "select integer, string from SALES where double =  \"26.4500\"  AND integer = 100",
        query.trim());

  }

  @Test
  public void testGetRunTimeFilters() {
    //Assert for two run time filters
    SipQuery sipQuery = analysis.getSipQuery();
    logger.trace("sipQuery : {}", sipQuery);
    List<Object> runTimeFilters = dl.getRunTimeFilters(sipQuery);
    Assert.assertEquals(2, runTimeFilters.size());
    Assert.assertEquals("string 1", runTimeFilters.get(0));
    Assert.assertEquals("100", runTimeFilters.get(1));

    // Assert for one runtime filter object
    sipQuery.setQuery("select integer, string from SALES where date = ?");
    sipQuery.getFilters().remove(1);
    Filter filter = getFilter(Type.DATE, "date", "26-02-2020 18:00:00", true);
    sipQuery.getFilters().set(0, filter);
    logger.trace("sipQuery : {}", sipQuery);
    runTimeFilters = dl.getRunTimeFilters(sipQuery);
    Assert.assertEquals(1, runTimeFilters.size());
    Assert.assertEquals("26-02-2020 18:00:00", runTimeFilters.get(0));

    //Assert for one runTime filter and one regular filter
    sipQuery.setQuery("select integer, string from SALES where double = ? AND integer = 100");
    filter = getFilter(Type.DOUBLE, "double", "26.4500", true);
    sipQuery.getFilters().set(0, filter);
    filter = getFilter(Type.INTEGER, "integer", "100", false);
    sipQuery.getFilters().add(filter);
    logger.trace("sipQuery : {}", sipQuery);
    runTimeFilters = dl.getRunTimeFilters(sipQuery);
    Assert.assertEquals(1, runTimeFilters.size());
    Assert.assertEquals("26.4500", runTimeFilters.get(0));

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
