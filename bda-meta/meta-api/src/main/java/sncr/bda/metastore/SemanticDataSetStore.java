package sncr.bda.metastore;

import java.util.Map;
import org.apache.hadoop.fs.Path;
import org.ojai.Document;
import org.ojai.store.QueryCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mapr.db.Table;
import sncr.bda.base.MetadataStore;
import sncr.bda.base.WithSearchInMetastore;

public class SemanticDataSetStore extends MetadataStore implements WithSearchInMetastore {

  private static final Logger logger = LoggerFactory.getLogger(SemanticDataSetStore.class);
  private static String TABLE_NAME = "semanticDataStore";

  public SemanticDataSetStore(String altXDFRoot) throws Exception {
    super(TABLE_NAME, altXDFRoot);
  }

  public Map<String, Document> search(QueryCondition qc) throws Exception {
    logger.trace("Search query on search " + qc.toString());
    return searchAsMap(table, qc);
  }
  
  public void createStore(String dlRoot) {
    metaRoot = dlRoot + Path.SEPARATOR + METASTORE;
    String fullTableName = metaRoot + Path.SEPARATOR + TABLE_NAME;
    logger.trace("Open table: " + fullTableName);
    table = initTable(fullTableName, retries);
    table.setOption(Table.TableOption.BUFFERWRITE, false);
  }
}
