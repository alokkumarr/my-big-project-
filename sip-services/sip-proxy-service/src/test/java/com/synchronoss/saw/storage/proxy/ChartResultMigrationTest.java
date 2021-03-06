package com.synchronoss.saw.storage.proxy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.saw.storage.proxy.service.executionResultMigrationService.ChartResultMigration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestClientTest(ChartResultMigration.class)
public class ChartResultMigrationTest {
  private ChartResultMigration migration;

  @Before
  public void init() {
    migration = new ChartResultMigration();
  }

  @Test
  public void testParseData() throws IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    File dataFile =
        new File(
            classLoader
                .getResource("com/synchronoss/saw/storage/proxy/sample_chart_data.json")
                .getPath());
    File queryFile =
        new File(
            classLoader
                .getResource("com/synchronoss/saw/storage/proxy/sample-chart-query.json")
                .getPath());
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode dataNode = objectMapper.readTree(dataFile);
    JsonNode queryNode = objectMapper.readTree(queryFile);
    List<Object> flatten = migration.parseData(dataNode, queryNode);
    Assert.assertTrue(flatten.size() > 0);
  }
}
