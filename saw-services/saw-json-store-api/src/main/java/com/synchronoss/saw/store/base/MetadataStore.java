package com.synchronoss.saw.store.base;

import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ojai.Document;
import org.ojai.store.DocumentMutation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mapr.db.MapRDB;
import com.mapr.db.Table;
import com.synchronoss.saw.store.metastore.DocumentConverter;


/**
 * Created by srya0001 on 10/30/2017.
 * The class is processMap parent for all Metadata related classes.
 * It provides basic functions:
 * - open table
 * - saves documents
 * - reads documents
 * - basic search over MapR DB tables
 */
public abstract class MetadataStore extends MetadataBase  implements DocumentConverter{

    private static final Logger logger = LoggerFactory.getLogger(MetadataStore.class);
    private static final String METASTORE = "metadata";

    public static final String delimiter = "::";

    protected String metaRoot;
    protected final Table table;
    
     //TODO:: Replace altRoot with configuration reading
    protected MetadataStore(String tableName, String altRoot) throws Exception {
        super(altRoot);
        metaRoot = dlRoot + Path.SEPARATOR + METASTORE;
        String fullTableName = metaRoot + Path.SEPARATOR + tableName;
        logger.debug("Open table: " + fullTableName);
       boolean exists = MapRDB.tableExists(fullTableName);
       table = !exists ? MapRDB.createTable(fullTableName) : MapRDB.getTable(fullTableName);
       table.setOption(Table.TableOption.BUFFERWRITE, false);
    }


    protected void _save(String id, Document doc) throws Exception
    {
        doc.setId(id);
        logger.debug("Final document to be saved: " + doc.toString());
        table.insertOrReplace( doc );
        table.flush();
    }

    protected void _saveNew(String id, Document doc) throws Exception
    {
        doc.setId(id);
        logger.debug("Final document to be saved: " + doc.toString());
        table.insert( doc );
        table.flush();
    }

    protected Document _read(String id) throws Exception
    {
        return table.findById( id );
    }

    public void update(String id, JsonElement src) throws Exception {
        Document ds = toMapRDBDocument(src);
        _save(id, ds);
    }

    public void create(String id, JsonElement src) throws Exception {
      logger.trace("Element received to create in the store {}",src.toString());
        Document ds = toMapRDBDocument(src);
        logger.trace("Element received after processing to create in the store {}",ds.asJsonString());
        _saveNew(id, ds);
    }

    public JsonElement create(String id, String src) throws Exception {
        Document ds = MapRDB.newDocument(src);
        _saveNew(id, ds);
        return toJsonElement(ds);
    }

    public JsonElement read(String id) throws Exception {
        Document ds = _read(id);
        if (ds != null)
            return toJsonElement(ds);
        return null;
    }

    public void delete(JsonElement src) throws Exception {
        if (!src.getAsJsonObject().has(DataSetProperties.Id.toString()))
            throw new Exception("Metadata document does contain ID");
        Document ds = toMapRDBDocument(src);
        table.delete(ds);
        table.flush();
    }

    public void delete(String id) throws Exception {
        table.delete(id);
        table.flush();
    }

    public void update(String src) throws Exception {
        Document doc = MapRDB.newDocument(src);
        table.replace(doc);
        table.flush();
    }

    public void update(String id, String src) throws Exception {
        Document doc = MapRDB.newDocument(src);
        doc.setId(id);
        table.replace(doc);
        table.flush();
    }

    public void _updatePath(String id, String path, String root, JsonElement src) throws Exception {
        Document mutatedPart = toMapRDBDocument(root, src);
        DocumentMutation mutation = MapRDB.newMutation();
        logger.trace("Path: " + path + ", Doc.part.: " + mutatedPart.toString());
        mutation.setOrReplace(path, mutatedPart);
        table.update(id, mutation);
        table.flush();
    }

    protected JsonObject createStatusSection(String status, String startTS, String finishedTS, String aleId, String batchSessionId)
    {
        JsonObject src = new JsonObject();
        src.add("status", new JsonPrimitive(status));
        src.add("started", new JsonPrimitive(startTS));
        if ( finishedTS != null)
            src.add("finished", new JsonPrimitive(finishedTS));
        else
            src.add("finished", new JsonPrimitive( "" ) );
        src.add("aleId", new JsonPrimitive(aleId));
        src.add("batchId", new JsonPrimitive(batchSessionId));
        return src;
    }


}
