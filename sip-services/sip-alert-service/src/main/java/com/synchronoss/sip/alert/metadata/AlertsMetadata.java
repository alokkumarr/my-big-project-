package com.synchronoss.sip.alert.metadata;

import com.mapr.db.MapRDB;
import java.util.List;
import org.ojai.Document;
import org.ojai.store.QueryCondition;
import sncr.bda.base.DocumentConverter;
import sncr.bda.base.MetadataStore;
import sncr.bda.base.WithSearchInMetastore;

public class AlertsMetadata extends MetadataStore
    implements WithSearchInMetastore, DocumentConverter {

  public AlertsMetadata(String tableName, String baseTableLocation) throws Exception {
    super(tableName, baseTableLocation);
  }

  /**
   * Search All analysis.
   *
   * @return {@link List} of {@link Document}
   * @throws java.io.IOException MaprDBexception
   */
  public List<Document> searchAll() throws Exception {
    QueryCondition cond = MapRDB.newCondition();
    return searchAsList(table, cond);
  }
}
