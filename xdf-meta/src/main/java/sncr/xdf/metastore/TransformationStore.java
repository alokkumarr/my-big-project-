package sncr.xdf.metastore;

import com.google.gson.JsonObject;
import org.ojai.Document;
import org.ojai.store.QueryCondition;
import sncr.xdf.base.MetadataStore;
import sncr.xdf.base.WithSearchInMetastore;

import java.util.Map;

/**
 * Created by srya0001 on 10/31/2017.
 */
public class TransformationStore extends MetadataStore implements WithSearchInMetastore{

    public   static String TABLE_NAME = "transformations";

    public TransformationStore(String altXDFRoot) throws Exception {
        super(TABLE_NAME, altXDFRoot);
    }

    public Map<String, Document> search(QueryCondition qc) throws Exception {
        return searchAsMap(table, qc);
    }

    /**
     *  Transformation specific method:
     *  update Transformation with status:
     *
     * @param id      -- Transformation ID
     * @param status  -- "status":"INIT|SUCCESS|FAILED",                // Must be updated before and after excution of component changing data of the data set initiated by UI or pipeline
     * @param startTS -- "started":"20171117-214242",                   // Must be right before excution of component changing data of the data set set initiated by UI or pipeline
     * @param finishedTS -- "finished":"20171117-214745",               // Must be updated right after excution of component changing data of the data set set initiated by UI or pipeline
     * @param aleId      --  "aleId":"project1::1510955142031",         // last ALE ID (audit log entry ID - for future use, ALE will contain detailed info about component execution)
     * @param batchSessionId  -- "batchId":"20174217-211133"            // Must be updated right after excution of component changing data of the data set set initiated by UI or pipeline
     * @throws Exception
     */
    public void updateStatus(String id, String status, String startTS, String finishedTS, String aleId, String batchSessionId) throws Exception {
        JsonObject src = createStatusSection(status, startTS, finishedTS, aleId, batchSessionId);
        _updatePath(id, null, "asOfNow", src);
    }

}
