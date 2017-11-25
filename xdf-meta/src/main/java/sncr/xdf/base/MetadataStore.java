package sncr.xdf.base;

import com.google.gson.JsonElement;
import com.mapr.db.MapRDB;
import com.mapr.db.Table;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.ojai.Document;
import org.ojai.store.DocumentMutation;
import sncr.xdf.datasets.conf.DataSetProperties;
import sncr.xdf.metastore.DocumentConverter;
import sncr.xdf.services.MetadataBase;


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

    private static final Logger logger = Logger.getLogger(MetadataStore.class);
    private static final String METASTORE = ".metadata";

    public static final String delimiter = "::";

    protected String metaRoot;
    protected final Table table;

    //TODO:: Replace altRoot with configuration reading
    protected MetadataStore(String tableName, String altRoot) throws Exception {
        super(altRoot);
        metaRoot = dlRoot + Path.SEPARATOR + METASTORE;
        String fullTableName = metaRoot + Path.SEPARATOR + tableName;
        logger.debug("Open table: " + fullTableName);
        table = MapRDB.getTable(fullTableName);
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
        Document ds = toMapRDBDocument(src);
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

    public void _updatePath(String id, String path, JsonElement src) throws Exception {
        Document mutatedPart = toMapRDBDocument(src);
        DocumentMutation mutation = MapRDB.newMutation();
        mutation.set(path, mutatedPart);
        table.update(id, mutation);
        table.flush();
    }



}
