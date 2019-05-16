package com.synchronoss.saw.storage.proxy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.saw.storage.proxy.service.executionResultMigrationService.PivotResultMigration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Alok.KumarR
 * @since 3.3.0
 */
@RestClientTest(PivotResultMigration.class)
public class PivotResultMigrationTest {
  private PivotResultMigration migration;

  @Before
  public void init() {
    migration = new PivotResultMigration();
  }

  @Test
  public void testParseData() throws IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    File file =
        new File(
            classLoader
                .getResource("com/synchronoss/saw/storage/proxy/sample_pivot_result.json")
                .getPath());
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(file);
    List<Object> flatten = migration.parseData(jsonNode);
    Assert.assertTrue(flatten.size() > 0);
  }
}
