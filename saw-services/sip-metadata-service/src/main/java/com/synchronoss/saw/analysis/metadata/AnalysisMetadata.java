package com.synchronoss.saw.analysis.metadata;

import com.mapr.db.MapRDB;
import java.util.List;
import java.util.Map;

import org.ojai.Document;
import org.ojai.store.QueryCondition;
import sncr.bda.base.DocumentConverter;
import sncr.bda.base.MetadataStore;
import sncr.bda.base.WithSearchInMetastore;

public class AnalysisMetadata extends MetadataStore
    implements WithSearchInMetastore, DocumentConverter {

  public AnalysisMetadata(String tableName, String baseTableLocation) throws Exception {
    super(tableName, baseTableLocation);
  }

  /** Search All analysis based on category.
   * @param keyValues Map
   * @return {@link List} of {@link Document}
   * @throws java.io.IOException MariaDbFetchExeception
   */
  public List<Document> searchAll(Map<String, String> keyValues) throws Exception {
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
