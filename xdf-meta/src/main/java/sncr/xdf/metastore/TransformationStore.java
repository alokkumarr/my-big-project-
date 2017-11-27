package sncr.xdf.metastore;

import org.ojai.Document;
import org.ojai.store.QueryCondition;
import sncr.xdf.base.MetadataStore;
import sncr.xdf.base.WithSearchInMetastore;

import java.util.Map;

/**
 * Created by srya0001 on 10/31/2017.
 */
public class TransformationStore extends MetadataStore implements WithSearchInMetastore{

    private static String TABLE_NAME = "transformations";

    public TransformationStore(String altXDFRoot) throws Exception {
        super(TABLE_NAME, altXDFRoot);
    }

    public Map<String, Document> search(QueryCondition qc) throws Exception {
        return searchAsMap(table, qc);
    }

}
