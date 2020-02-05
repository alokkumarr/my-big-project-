package com.synchronoss.saw.workbench.executor.service;

import com.mapr.db.MapRDB;
import com.mapr.db.Table;
import org.ojai.DocumentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Workbench preview builder. Used for creating a MapR-DB document for storing a Workbench preview.
 */
/**
 * Workbench preview builder. Used for creating a MapR-DB document for storing a Workbench preview.
 */
public class PreviewBuilder {
  private Table table;
  private DocumentBuilder document;

  /**
   * Create new preview builder with the given preview ID and preview status.
   */
  public PreviewBuilder(String tablePath, String id, String status) {
    table = MapRDB.getTable(tablePath);
    document = MapRDB.newDocumentBuilder();
    document.addNewMap();
    document.put("_id", id);
    document.put("status", status);
  }

  /**
   * Get the document builder that allows setting properties in the preview document.
   */
  public DocumentBuilder getDocumentBuilder() {
    return document;
  }

  /**
   * Insert the preview document into the MapR-DB table.
   */
  public void insert() {
    document.endMap();
    table.insertOrReplace(document.getDocument());
    table.flush();
  }
}