package com.synchronoss.saw.analysis.service.migrationservice;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.synchronoss.saw.model.Store;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AnalysisSipDSLConverterTest {
  JsonObject oldAnalysisObject;

  @Before
  public void init() throws IOException {
    ClassLoader classLoader = new AnalysisSipDSLConverterTest().getClass().getClassLoader();
    File file =
        new File(
            classLoader.getResource("com/synchronoss/saw/analyze/sample-chart.json").getFile());

    String fileContent = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));

    JsonParser parser = new JsonParser();

    oldAnalysisObject =
        parser
            .parse(fileContent)
            .getAsJsonObject()
            .getAsJsonObject("contents")
            .getAsJsonArray("analyze")
            .get(0)
            .getAsJsonObject();
  }

  @Test
  public void testBuildStoreObject() {
    ChartConverter converter = new ChartConverter();

    Store store = converter.buildStoreObject(oldAnalysisObject);

    Assert.assertEquals("ES", store.getStorageType());
  }
}
