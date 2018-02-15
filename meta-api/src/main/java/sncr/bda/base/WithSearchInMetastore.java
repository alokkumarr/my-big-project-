package sncr.bda.base;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mapr.db.MapRDB;
import com.mapr.db.Table;
import org.apache.log4j.Logger;
import org.ojai.Document;
import org.ojai.DocumentStream;
import org.ojai.store.QueryCondition;
import sncr.bda.cli.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by srya0001 on 10/31/2017.
 */
public interface WithSearchInMetastore {

    default Map<String, Document> searchAsMap(Table table, QueryCondition qc) throws Exception {
        Map<String, Document> docs = new HashMap<>();
        try (DocumentStream documentStream = table.find(qc)) {
            for (Document doc : documentStream) docs.put(doc.getId().toString(), doc);
            return docs;
        }
    }

    default List<Document> searchAsList(Table table, QueryCondition qc) throws Exception {
        List<Document> docs = new ArrayList<>();
        try (DocumentStream documentStream = table.find(qc)) {
            for (Document doc : documentStream) docs.add(doc);
            return docs;
        }
    }

    default List<Document> searchAsList(String tableName, QueryCondition qc) throws Exception {
        Table table = MapRDB.getTable(tableName);
        return searchAsList(table, qc);
    }


    default Map<String, Document> searchAsMap(Table table, String qcStr) throws Exception {
        FilterHelper fh = new FilterHelper();
        QueryCondition qc = fh.convertStringToQC(qcStr);
        return searchAsMap(table, qc);
    }

    default List<Document> searchAsList(Table table, String qcStr) throws Exception {
        FilterHelper fh = new FilterHelper();
        QueryCondition qc = fh.convertStringToQC(qcStr);
        return searchAsList(table, qc);
    }

    default List<Document> searchAsList(String tableName, String qc) throws Exception {
        Table table = MapRDB.getTable(tableName);
        return searchAsList(table, qc);
    }


    class FilterHelper {

        private static final Logger logger = Logger.getLogger(FilterHelper.class);

        private QueryCondition convertStringToQC(String filterAsString) {

            JsonParser jsonParser = new JsonParser();
            JsonElement je0 = jsonParser.parse(filterAsString);
            JsonArray filter;

            JsonObject jo0 = null;
            if (je0.isJsonObject()) {
                jo0 = je0.getAsJsonObject();
            }
            if(jo0 == null)
            {
                logger.error("Incorrect JSON representation of filter");
                return null;
            }

            JsonElement query0 = jo0.get("query");
            JsonObject query;
            if ( query0.isJsonObject() ) {
                query = query0.getAsJsonObject();
                JsonElement filter0 = query.get("filter");
                if (filter0 == null || !filter0.isJsonArray()) {
                    logger.error("Filter should not be empty or null; filter entries should be valid JSON array");
                    return null;
                }
                filter = filter0.getAsJsonArray();
            }
            else{
                logger.error("Query should not be empty or null; Query should be valid JSON object");
                return null;
            }

            QueryCondition maprDBCondition = MapRDB.newCondition();

            boolean toBeClosed = false;
            if (filter.size() > 1) {
                if (query.has("conjunction"))
                    if (query.get("conjunction").getAsString().equalsIgnoreCase("or"))
                        maprDBCondition.or();
                    else
                        maprDBCondition.and();
                else
                    maprDBCondition.and();
                toBeClosed = true;
            }

            filter.forEach(c -> {
                if (c.isJsonObject()) {
                    JsonObject cjo = c.getAsJsonObject();
                    String fp = cjo.getAsJsonPrimitive("field-path").getAsString();
                    String cond = cjo.getAsJsonPrimitive("condition").getAsString();
                    String val = cjo.getAsJsonPrimitive("value").getAsString();
                    if (fp != null && fp.isEmpty() && cond != null && cond.isEmpty() && val != null && val.isEmpty()) {
                        if (cond.equalsIgnoreCase("like"))
                            maprDBCondition.like(fp, val);
                        else
                            maprDBCondition.is(fp, getOperation(cond), val);
                    } else {
                        logger.error("Skip incorrect filter element: " + c.getAsJsonObject().toString());
                    }
                } else {
                    logger.warn("Incorrect query");
                }
            });
            if (toBeClosed) maprDBCondition.close();
            return maprDBCondition.build();

        }


        private QueryCondition.Op getOperation(String cond) {
            switch (cond) {
                case "=":
                case "==":
                case "eq":
                case "EQ":
                    return QueryCondition.Op.EQUAL;
                case "!=":
                case "<>":
                case "ne":
                case "NE":
                    return QueryCondition.Op.NOT_EQUAL;
                case ">":
                case "gt":
                case "GT":
                    return QueryCondition.Op.GREATER;
                case "<":
                case "lt":
                case "LT":
                    return QueryCondition.Op.GREATER_OR_EQUAL;
                case ">=":
                case "ge":
                case "GE":
                    return QueryCondition.Op.GREATER_OR_EQUAL;
                case "<=":
                case "le":
                case "LE":
                    return QueryCondition.Op.LESS_OR_EQUAL;
            }
            return null;
        }

    }
}
