package sncr.bda.metastore;

import com.mapr.db.MapRDB;
import org.ojai.Document;
import org.ojai.store.QueryCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sncr.bda.base.DocumentConverter;
import sncr.bda.base.MetadataStore;
import sncr.bda.base.WithSearchInMetastore;

import java.util.Map;

public class ProductModuleMetaStore extends MetadataStore implements WithSearchInMetastore, DocumentConverter {

    private static final Logger logger = LoggerFactory.getLogger(ProductModuleMetaStore.class);




    public ProductModuleMetaStore(String tableName, String altXDFRoot) throws Exception {
        super(tableName, altXDFRoot);

    }

    public Map<String, Document> search(QueryCondition qc) throws Exception {
        logger.trace("Search query on search " + qc.toString());
        return searchAsMap(table, qc);
    }

    public Map<String, Document> searchAll() throws Exception {
        QueryCondition cond = MapRDB.newCondition();
        return searchAsMap(table, cond);
    }

    public Map<String, Document> searchAll( Map<String,String> keyValues ) throws Exception {
        logger.trace("Search query on search " + keyValues);
        QueryCondition cond = MapRDB.newCondition();
        cond.and();
        if (keyValues != null || keyValues.size() != 0) {

            for ( String key : keyValues.keySet() ) {
                cond.is(key,QueryCondition.Op.EQUAL,keyValues.get(key));
            }
        }

        cond.close();
        cond.build();
        return searchAsMap(table, cond);

    }
}
