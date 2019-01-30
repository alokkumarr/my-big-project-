package sncr.bda.metastore;

import org.ojai.Document;
import org.ojai.store.QueryCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sncr.bda.base.MetadataStore;
import sncr.bda.base.WithSearchInMetastore;

import java.util.Map;

public class ProductModuleMetaStore extends MetadataStore implements WithSearchInMetastore {

    private static final Logger logger = LoggerFactory.getLogger(ProductModuleMetaStore.class);




    public ProductModuleMetaStore(String tableName, String altXDFRoot) throws Exception {
        super(tableName, altXDFRoot);

    }

    public Map<String, Document> search(QueryCondition qc) throws Exception {
        logger.trace("Search query on search " + qc.toString());
        return searchAsMap(table, qc);
    }
}
