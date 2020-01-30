package sncr.bda.base;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mapr.db.MapRDB;
import com.mapr.db.Table;
import com.mapr.db.exceptions.DBException;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.ojai.Document;
import org.ojai.store.DocumentMutation;
import sncr.bda.datasets.conf.DataSetProperties;
import java.util.Optional;


/**
 * Created by srya0001 on 10/30/2017. The class is processMap parent for all Metadata related
 * classes. It provides basic functions: - open table - saves documents - reads documents - basic
 * search over MapR DB tables
 */
public abstract class MetadataStore extends MetadataBase implements DocumentConverter {

  private static final Logger logger = Logger.getLogger(MetadataStore.class);
  // TODO: The below path i.e. /services has been added to support backward compatibility
  // at the service & xdf-ngComponent layer. In addition to that, if we configure the /service at
  // the xd-ngcomponent config file
  // xdf-component will create the parquet file under /service hierarchy like
  // /service/dl/fs/data/dataset_name/data
  // TODO: In future release we have to remove this dependency from here.
  protected static final String METASTORE = "services/metadata";

  protected Table table;
  protected String metaRoot;
  protected final int retries = 3;
  public static final String delimiter = "::";

  // TODO:: Replace altRoot with configuration reading
  protected MetadataStore(String tableName, String altRoot) throws Exception {
    super(altRoot);
    metaRoot = dlRoot + Path.SEPARATOR + METASTORE;
    String fullTableName = metaRoot + Path.SEPARATOR + tableName;
    logger.trace("Open table: " + fullTableName);
    table = initTable(fullTableName, retries);
    table.setOption(Table.TableOption.BUFFERWRITE, false);
  }

  // This has been added to handle the use case
  // while working on SIP-6061
  public Table initTable(String tablePath, int retries) {
    Table tableDesc = null;
    if (MapRDB.tableExists(tablePath)) {
      tableDesc = MapRDB.getTable(tablePath);
    } else {
      try {
        tableDesc = MapRDB.createTable(tablePath);
      } catch (DBException ex) {
        logger.trace("Table possibly already created by other instance : {}", ex);
        logger.trace(String.format("Table path : %s ", tablePath));
        if (MapRDB.tableExists(tablePath)) {
          tableDesc = MapRDB.getTable(tablePath);
        } else {
          logger.trace("Number of retries :" + retries);
          if (retries > 0) {
            initTable(tablePath, retries - 1);
          } else {
            logger.trace("Number of retries has been exhausted:" + retries);
            throw new DBException(
                "Exception occured while creating table with the path :" + tablePath);
          }
        }
      }
    }
    return tableDesc;
  }

  protected void _save(String id, Document doc) throws Exception {
    doc.setId(id);
    logger.debug("Final document to be saved: " + doc.toString());
    table.insertOrReplace(doc);
    table.flush();
  }

  protected void _saveNew(String id, Document doc) throws Exception {
    doc.setId(id);
    logger.debug("Final document to be saved: " + doc.toString());
    table.insert(doc);
    table.flush();
  }

  protected Document _read(String id) throws Exception {
    logger.debug("Find entity by ID: " + id);
    return table.findById(id);
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
    if (ds != null) {
      logger.trace("Entity: " + ds.asJsonString());
      return toJsonElement(ds);
    }
    return null;
  }

  public Document readDocumet(String id) throws Exception {
    Document ds = _read(id);
    if (ds != null) {
      logger.trace("Entity: " + ds.asJsonString());
      return ds;
    }
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

  public void _updatePath(String id, String root, String path, JsonElement src) throws Exception {
    DocumentMutation mutation = MapRDB.newMutation();
    if (src.isJsonObject()) {
      Document mutatedPart = toMapRDBDocument(root, src);
      logger.debug("Path: " + path + ", Doc.part.: " + mutatedPart.toString());
      mutation.setOrReplace(path, mutatedPart);
    } else {
      Document mutatedPart = toMapRDBDocument(path, src);
      logger.debug("Path: " + path + ", Doc.part.: " + mutatedPart.toString());
      mutation.setOrReplace(path, mutatedPart.getValue(path));
    }
    table.update(id, mutation);
    table.flush();
  }

  public JsonObject createStatusSection(String status, String startTS, String finishedTS, String aleId, String batchSessionId) {
     return createStatusSection(status, startTS, finishedTS, aleId, batchSessionId,Optional.empty(),Optional.empty());
  }

  public JsonObject createStatusSection(
     String status, String startTS, String finishedTS, String aleId, String batchSessionId, Optional<Integer> returnCode, Optional<String> errorDesc) {
      JsonObject src = new JsonObject();
      src.add("status", new JsonPrimitive(status));
      src.add("started", new JsonPrimitive((startTS == null) ? "" : startTS));
      src.add("finished", new JsonPrimitive((finishedTS == null) ? "" : finishedTS));
      src.add("aleId", new JsonPrimitive(aleId));
      src.add("batchId", new JsonPrimitive(batchSessionId));
      if(returnCode.isPresent()){
          src.add("xdfReturnCode", new JsonPrimitive(returnCode.get()));
      }
      if(errorDesc.isPresent()){
          src.add("errorDescription", new JsonPrimitive(errorDesc.get()));
      }
      logger.debug("Status Session Json : " + src.toString());
      return src;
  }

  @Override
  protected void finalize() {
    table.flush();
    table.close();
  }

  public Table getTable() {
    return table;
  }
}
