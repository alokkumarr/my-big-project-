package com.synchronoss.saw.storage.proxy.service;

import com.google.gson.JsonElement;
import com.mapr.db.MapRDB;
import java.util.Map;

import com.mapr.db.Table;
import org.ojai.Document;
import org.ojai.store.QueryCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sncr.bda.base.DocumentConverter;
import sncr.bda.base.MetadataStore;
import sncr.bda.base.WithSearchInMetastore;

public class ExecutionResultStore extends MetadataStore
    implements WithSearchInMetastore, DocumentConverter {

  private static final Logger logger = LoggerFactory.getLogger(ExecutionResultStore.class);

  public ExecutionResultStore(String tableName, String altXDFRoot) throws Exception {
    super(tableName, altXDFRoot);
  }

  public Map<String, Document> search(QueryCondition qc) throws Exception {
    logger.trace("Search query on search " + qc.toString());
    return searchAsMap(table, qc);
  }

  public JsonElement create(Table table, String id, String src) throws Exception {
    Document ds = MapRDB.newDocument(src);
    _saveNew(table, id, ds);
    return toJsonElement(ds);
  }

  protected void _saveNew(Table table, String id, Document doc) {
    doc.setId(id);
    logger.debug("Final document to be saved: " + doc.toString());
    table.insert(doc);
    table.flush();
  }
}
