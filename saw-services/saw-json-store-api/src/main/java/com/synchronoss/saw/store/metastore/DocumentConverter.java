package com.synchronoss.saw.store.metastore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ojai.Document;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.mapr.db.MapRDB;

/**
 * Created by srya0001 on 11/1/2017.
 *
 */
public interface DocumentConverter {

    default Document toMapRDBDocument(JsonElement je){
        return DCHelper.processMap(MapRDB.newDocument(), null, je);
    }

    default Document toMapRDBDocument(String path, JsonElement je){
        return DCHelper.processMap(MapRDB.newDocument(), path, je);
    }

    default JsonElement toJsonElement (Document doc){
        String json = doc.asJsonString();
        JsonParser jsonParser = new JsonParser();
        return jsonParser.parse(json);
    }

    default List<String> convertToString(List<Document> listDoc){
        List<String> res = new ArrayList<String>();
        listDoc.forEach( i -> {
            JsonElement je = toJsonElement(i);
            if (je != null && !je.isJsonNull()) res.add(je.toString());
        });
        return res;
    }


    class DCHelper{

        protected static final Logger logger = LoggerFactory.getLogger(DCHelper.class);

        public static Document processMap(Document doc, String k, JsonElement je) {
          logger.trace("After Parsing into JSONElement Object inside processMap :{}", je.toString());
            logger.trace("Process doc [" + k + "]:" + doc.asJsonString());
            logger.trace( "JSON element: " +je.toString() );
            if (!je.isJsonNull()){
                if (je.isJsonObject()) {
                    JsonObject jo = je.getAsJsonObject();
                    logger.trace("Process JSON object");
                    Set<Map.Entry<String, JsonElement>> s = jo.entrySet();
                    Document doc1 = MapRDB.newDocument();
                    s.forEach(entry -> {
                        processMap(doc1, entry.getKey(), entry.getValue());
                        logger.trace("Process pair: key = " + entry.getKey() + " value = " + entry.getValue().toString() );
                    });
                    if ( k == null )
                        doc = doc1;
                    else
                        doc.set(k, doc1);
                } else if (je.isJsonArray()) {
                    logger.trace("Process JSON array");
                    JsonArray ja = je.getAsJsonArray();
                    List<Object> l = new ArrayList<>();
                    ja.forEach(el -> processList(l, el));
                    doc.set(k, l);
                } else if (je.isJsonPrimitive()) {
                    logger.trace("Process JSON primitive with field name " + k);
                    JsonPrimitive jp = je.getAsJsonPrimitive();
                    if (jp.isBoolean())
                        doc.set(k, jp.getAsBoolean());
                    else if (jp.isString())
                        doc.set(k, jp.getAsString());
                    else if (jp.isNumber()) {
                        Number jn = jp.getAsNumber();
                        logger.debug("Process JSON number: " + jn.intValue());

                        if (jn.doubleValue() != jn.intValue())
                            doc.set(k, jn.doubleValue());
                        else
                            doc.set(k, jn.intValue());
                    }
                }
            }
            else{
                logger.trace("Null JSON object encountered");
            }
            return doc;
        }

        private static List<?> processList(List<Object> l, JsonElement je) {
            logger.trace("After Parsing into JSONElement Object inside processList :{}", je.toString());
            logger.trace("Process list" );
            logger.trace( "JSON element: " +je.toString() );
            if (!je.isJsonNull()){
                if (je.isJsonObject()) {
                    logger.trace("Process JSON object processList");
                    JsonObject jo = je.getAsJsonObject();
                    Set<Map.Entry<String, JsonElement>> s = jo.entrySet();
                    Document doc1 = MapRDB.newDocument();
                    s.forEach(entry -> processMap(doc1, entry.getKey(), entry.getValue()));
                    l.add(doc1);
                } else if (je.isJsonArray()) {
                    logger.trace("Process JSON array processList");
                    JsonArray ja = je.getAsJsonArray();
                    List<Object> l1 = new ArrayList<>();
                    ja.forEach(el -> processList(l1, el));
                    l.add(l1);
                } else if (je.isJsonPrimitive()) {
                    logger.trace("Process JSON primitive processList: " + je.toString() );
                    JsonPrimitive jp = je.getAsJsonPrimitive();
                    if (jp.isBoolean())
                        l.add(jp.getAsBoolean());
                    else if (jp.isString())
                        l.add(jp.getAsString());
                    else if (jp.isNumber()) {
                        Number jn = jp.getAsNumber();
                        if (jn.doubleValue() != jn.intValue())
                            l.add(jn.doubleValue());
                        else
                            l.add(jn.intValue());

                    }
                }
            }
            else{
                logger.debug("Null JSON object encountered");
            }
            return l;
        }
    }

}
