package com.synchronoss.saw.storage.proxy.service;

import com.mapr.db.MapRDB;
import java.util.List;
import java.util.Map;
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

  public List<Document> searchAll() throws Exception {
    QueryCondition cond = MapRDB.newCondition();
    return searchAsList(table, cond);
  }

  public List<Document> searchAll(Map<String, String> keyValues) throws Exception {
    logger.trace("Search query on search " + keyValues);
    QueryCondition cond = MapRDB.newCondition();
    cond.and();
    if (keyValues != null || keyValues.size() != 0) {

      for (String key : keyValues.keySet()) {
        cond.is(key, QueryCondition.Op.EQUAL, keyValues.get(key));
      }
    }

    cond.close();
    cond.build();
    return searchAsList(table, cond);
  }
}
