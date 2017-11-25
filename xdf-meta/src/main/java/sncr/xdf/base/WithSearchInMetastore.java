package sncr.xdf.base;

import com.google.gson.JsonElement;
import com.mapr.db.MapRDB;
import com.mapr.db.Table;
import org.ojai.Document;
import org.ojai.DocumentStream;
import org.ojai.store.QueryCondition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by srya0001 on 10/31/2017.
 */
public interface WithSearchInMetastore {

    default Map<String, Document> searchAsMap(Table table, QueryCondition  qc) throws Exception
    {
        Map<String, Document> docs = new HashMap<>();
        try(DocumentStream documentStream = table.find(qc)) {
            for (Document doc : documentStream ) docs.put(doc.getId().toString(), doc);
            return docs;
        }
    }

    default List<Document> searchAsList(Table table, QueryCondition  qc) throws Exception
    {
        List<Document> docs = new ArrayList<>();
        try(DocumentStream documentStream = table.find(qc)) {
            for (Document doc : documentStream ) docs.add(doc);
            return docs;
        }
    }


}
