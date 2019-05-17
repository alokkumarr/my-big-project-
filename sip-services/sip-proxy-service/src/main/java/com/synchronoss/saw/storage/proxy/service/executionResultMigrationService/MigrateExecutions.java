package com.synchronoss.saw.storage.proxy.service.executionResultMigrationService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.synchronoss.saw.model.SipQuery;
import com.synchronoss.saw.storage.proxy.service.productSpecificModuleService.ProductModuleMetaStore;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MigrateExecutions {
  private static final Logger logger = LoggerFactory.getLogger(MigrateExecutions.class);

  public String migrationDirectory = "/opt/migration/";
  public String migrationStatusFile = "migrationStatus.json";

  private ProductModuleMetaStore analysisMetadataStore = null;
  private JsonElement oldAnalysisDef;
  private String tableName;
  private String basePath;

  public MigrateExecutions() {}

  /**
   * Parameterized constructor.
   *
   * @param tableName MAPR DB table
   * @param basePath MAPR DB location
   * @param listAnalysisUri List analysis API endpoint
   * @param oldAnalysisDef Output location for migration status
   */
  public MigrateExecutions(
      String tableName, String basePath, String listAnalysisUri, JsonElement oldAnalysisDef) {

    this.basePath = basePath;
    this.tableName = tableName;
    this.oldAnalysisDef = oldAnalysisDef;
    //    this.statusFilePath = statusFilePath;
  }

  /**
   * Converts analysis definition in binary table to new SIP DSL format.
   *
   * @param tableName - Analysis metastore table name
   * @param basePath - Table path
   * @param oldAnalysisDef - old queryBuilder object
   */
  public void convertBinaryToJson(String tableName, String basePath, JsonElement oldAnalysisDef)
      throws Exception {

    JsonObject analysisBinaryObject = oldAnalysisDef.getAsJsonObject();
    if (!analysisBinaryObject.has("queryBuilder")) {
      throw new UnsupportedOperationException("No queryBuilder{} object present!! ");
    } else {
      logger.trace("Migration process will begin here");
      SipQuery sipQuery =
          convertOldAnalysisObjtoSipDsl(analysisBinaryObject.get("queryBuilder").getAsJsonObject());
    }

    // Currently disabled. Will be enabled in future
    // saveMigrationStatus(migrationStatus, migrationDirectory, migrationStatusFile);
  }

  /**
   * Converts old analysis object to new SIP DSL definition.
   *
   * @param queryBuilderObject Single analysis object in old definition
   * @return
   */
  private SipQuery convertOldAnalysisObjtoSipDsl(JsonObject queryBuilderObject) {

    FieldsSipDslConverter converter = new QueryDefinitionConverter();

    SipQuery sipQuery = converter.convert(queryBuilderObject);

    return sipQuery;
  }

  /**
   * Method can be used by outside class to get the migrated SipQuery Object.
   *
   * @param jsonObject QueryBuilder Object
   * @return SipQuery Object
   */
  public SipQuery migrate(JsonObject jsonObject) {
    SipQuery sipQuery;
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    JsonObject queryBuilderObject = jsonObject.getAsJsonObject(FieldNames.QUERY_BUILDER);
    logger.debug("QueryBuilder obj to be Migrated = " + gson.toJson(queryBuilderObject));
    MigrateExecutions ma = new MigrateExecutions();

    sipQuery = ma.convertOldAnalysisObjtoSipDsl(queryBuilderObject);
    logger.debug("Migrated SipQuery = " + gson.toJson(sipQuery, SipQuery.class));

    return sipQuery;
  }

  /**
   * Main function.
   *
   * @param args - command line args
   * @throws IOException - In case of file errors
   */
  public static void main(String[] args) throws IOException {
    String analysisFile = args[0];
    System.out.println("Convert analysis from file = " + analysisFile);
    SipQuery sipQuery1 = new SipQuery();
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    File jsonFile = new File(analysisFile);
    JsonObject jsonObject = gson.fromJson(new FileReader(jsonFile), JsonObject.class);

    JsonObject queryBuilderObject = jsonObject.getAsJsonObject(FieldNames.QUERY_BUILDER);
    logger.info("QueryBuilder obj to be Migrated = " + gson.toJson(queryBuilderObject));

    MigrateExecutions ma = new MigrateExecutions();

    SipQuery sipQuery = ma.convertOldAnalysisObjtoSipDsl(queryBuilderObject);

    logger.info("Migrated SipQuery = " + gson.toJson(sipQuery, SipQuery.class));
  }
}
