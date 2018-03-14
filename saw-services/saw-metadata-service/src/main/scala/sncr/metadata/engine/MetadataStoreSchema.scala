package sncr.metadata.engine

import files.HFileOperations
import org.apache.hadoop.hbase.HColumnDescriptor
import org.apache.hadoop.hbase.HTableDescriptor
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.Admin
import org.slf4j.LoggerFactory
import sncr.saw.common.config.SAWServiceConfig

/**
 * Utilities for initializing metadata store schema upon application
 * startup.
 */
object MetadataStoreSchema {
  val log = LoggerFactory.getLogger(MetadataStoreSchema.getClass.getName)

  /**
    * Directory in which metadata store tables are created when the
    * metadata store schema is created
    */
  val TableHome = SAWServiceConfig.metadataConfig.getString("path")

  /**
   * Initializes metadata store schema: creates MapR-DB binary tables
   * and column families.  Will detect if an operation has already
   * been applied previously, for example a table has already been
   * created, and handle that gracefully.  Intended to be invoked on
   * every startup of the application.
   */
  def init(admin: Admin) {
    HFileOperations.createDirectory(TableHome)
    /* Create UI artifact table */
    initTable(admin, "ui_metadata", "_system", "_source", "_search")
    /* Create Semantic Layer storage*/
    initTable(admin, "semantic_metadata", "_system", "_source", "_search",
      "_relations")
    /* Create Datalake Metadata storage*/
    initTable(admin, "datalake_metadata", "_system", "_source", "_search",
      "_relations", "_dl_locations")
    /* Create Analysis Metadata Storage*/
    initTable(admin, "analysis_metadata", "_system", "_source", "_search",
      "_relations")
    /* Create Analysis Result table*/
    initTable(admin, "analysis_results", "_system", "_source", "_search",
      "_objects")
  }

  /**
   * Creates MapR-DB binary table using the given name and adds the
   * given column families to it
   */
  private def initTable(admin: Admin, name: String, familyNames: String*) {
    val tablePath = TableHome + "/" + name
    if (!admin.tableExists(TableName.valueOf(tablePath))) {
      log.info("Table not found, so creating: {}", tablePath)
      val descriptor = new HTableDescriptor()
      descriptor.setName(tablePath.getBytes())
      for (familyName <- familyNames) {
        log.info("Adding column family: {}", familyName)
        val columnDescriptor = new HColumnDescriptor(familyName)
        descriptor.addFamily(columnDescriptor);
      }
      log.info("Creating table: {}", tablePath)
      admin.createTable(descriptor);
    }
  }
}
