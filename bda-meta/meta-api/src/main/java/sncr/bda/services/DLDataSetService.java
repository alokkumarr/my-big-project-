package sncr.bda.services;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS;
import static sncr.bda.base.MetadataBase.DEFAULT_CATALOG;
import static sncr.bda.base.MetadataBase.PREDEF_DATA_SOURCE;
import static sncr.bda.base.MetadataStore.delimiter;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.ojai.joda.DateTime;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import sncr.bda.base.MetadataBase;
import sncr.bda.conf.Input;
import sncr.bda.context.ContextMetadata;
import sncr.bda.core.file.HFileOperations;
import sncr.bda.datasets.conf.DataSetProperties;
import sncr.bda.datasets.conf.Dataset;
import sncr.bda.exceptions.BDAException;
import sncr.bda.metastore.DataSetStore;


/**
 * The class provides
 * - CRUD Meta Store functions
 * - File system based data set repository
 * - loading metadata of existing datasets
 * Created by srya0001 on 10/27/2017.
 */
public class DLDataSetService {

    private static final Logger logger = Logger.getLogger(DLDataSetService.class);

    protected static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Map<String, Map<String, Object>> repository;
    private DataSetStore dsStore;
    private Boolean persistMode;

    public String getRoot() {
        return dsStore.getRoot();
    }


    public DLDataSetService(String fsr) throws Exception {
        dsStore = new DataSetStore(fsr);
        repository = new HashMap();
    }

    public DLDataSetService(String fsr, Boolean persistMode) throws Exception {
        dsStore = new DataSetStore(fsr);
        repository = new HashMap<>();
        this.persistMode = persistMode;
    }

    public void writeDLFSMeta(ContextMetadata ctx) throws Exception {

        logger.trace("Save changes in data Object repository");

        repository.keySet().forEach(k -> {
            Map<String, Object> entry = repository.get(k);
            String dt = format.format(new Timestamp(new Date().getTime()));
            if (entry.containsKey(DataSetProperties.isNewDataSet.name())) {
                String metadataFileName = buildMetadataFileName(ctx, entry);
                logger.trace("New file in Data Object repository: " + metadataFileName);
                Dataset dataset = new Dataset();
                dataset.setComponent(ctx.componentName);

                String desc = (entry.containsKey(DataSetProperties.MetaDescription.name())) ? (String)
                    entry.get(DataSetProperties.MetaDescription.name()) : "__none__";
                dataset.setDescription(desc);
                dataset.setFormat((String) entry.get(DataSetProperties.Format.name()));
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(SerializationFeature.INDENT_OUTPUT, true)
                    .configure(ALLOW_COMMENTS, true)
                    .configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
                OutputStream os = null;
                try {
                    os = HFileOperations.writeToFile(metadataFileName);
                    if (os != null) {
                        mapper.writeValue(os, dataset);
                        os.flush();
                        os.close();
                    }
                } catch (Exception e) {
                    throw new BDAException(BDAException.ErrorCodes.CouldNotCreateDSMeta, e);
                }

            }
        });
    }

    private String buildMetadataFileName(ContextMetadata ctx, Map<String, Object> ds) {
        StringBuilder sb = new StringBuilder(dsStore.getRoot());
        sb.append(Path.SEPARATOR + ctx.applicationID)
            .append(Path.SEPARATOR + MetadataBase.PREDEF_DL_DIR)
            .append(Path.SEPARATOR + PREDEF_DATA_SOURCE)
            .append(Path.SEPARATOR + ds.get(DataSetProperties.Catalog.name()))
            .append(Path.SEPARATOR + ds.get(DataSetProperties.Name.name()))
            .append(Path.SEPARATOR + MetadataBase.FILE_DESCRIPTOR);

        logger.debug(String.format("Resolve metadata storage of %s dataset to location: %s",
            ds.get(DataSetProperties.Name.name()), sb.toString()));
        return sb.toString();
    }


    public void logMetadata() {
        logger.debug("Metadata: \n");
        repository.keySet().forEach(n ->
            {
                Map<String, Object> props = repository.get(n);
                logger.debug(String.format("Property: %s", n));
                props.forEach((pn, pv) -> logger.debug(String.format("%s => %s", pn, pv)));
            }
        );
    }


    /**
     * The method reads or creates dataset.
     * The ID is being generated by fixed rule and DataSet ID depends only on
     * - component
     * - user info
     * - user entered data
     * - DL metadata
     * If processMap dataset is not found - the component creates it with all data available.
     *
     * @param ctx
     * @param o
     * @return
     */
    public JsonElement readOrCreateDataSet(ContextMetadata ctx, Map<String, Object> o) {
        JsonElement ds = null;
        try {
            String id = generateDSID(ctx, o);
            if (!o.containsKey(DataSetProperties.Id.toString()))
                o.put(DataSetProperties.Id.toString(), id);
            ds = (persistMode) ? dsStore.read(id) : null;
            JsonElement je = createDSDescriptor(id, ctx, o);
            if (ds == null) {
                if (persistMode) dsStore.create(id, je);
                return je;
            } else {
                logger.debug("Metadata found");
                JsonObject oldSystem = ds.getAsJsonObject()
                    .get(DataSetProperties.System.toString()).getAsJsonObject();
                JsonObject newSystem = je.getAsJsonObject()
                    .get(DataSetProperties.System.toString()).getAsJsonObject();
                oldSystem = checkAndUpdateSystemParams(id, oldSystem, newSystem);
                if (oldSystem != null && persistMode) {
                    ds.getAsJsonObject().add(DataSetProperties.System.toString(), oldSystem);
                    dsStore.update(id, ds);
                }
                return ds;
            }
        } catch (Exception e) {
            logger.error("Could not read or create Data set: ", e);
            return ds;
        }
    }

    private JsonObject checkAndUpdateSystemParams(String id, JsonObject oldSystem, JsonObject newSystem) throws Exception {
        boolean status = false;

        for (Map.Entry<String, JsonElement> entry : newSystem.entrySet()) {
            String key = entry.getKey();

            newSystem.addProperty(DataSetProperties.CreatedTime.toString(), oldSystem.get(DataSetProperties.CreatedTime.toString()).getAsLong());
            newSystem.addProperty(DataSetProperties.ModifiedTime.toString(), oldSystem.get(DataSetProperties.ModifiedTime.toString()).getAsLong());

            logger.debug("Key = " + key);

            JsonPrimitive newValue = entry.getValue().getAsJsonPrimitive();

            JsonPrimitive oldValue = oldSystem.getAsJsonPrimitive(key);

            logger.debug("Old value = " + oldValue + ". New value = " + newValue);

            if(oldValue != null && !oldValue.getAsString().equalsIgnoreCase(newValue.getAsString())) {
                logger.debug("Value updated for " + key);

                oldSystem.add(key, newValue);

                status = true;
            }
        }

        if (status) {
            logger.debug("Property values changed");

            oldSystem.addProperty(DataSetProperties.ModifiedTime.toString(), new DateTime().getMillis());
        } else {
            oldSystem = null;
        }

        return oldSystem;
    }

    /**
     * The method is to create Json structure from
     * dataset descriptor, with metadata that must be provided through Context object
     *
     * @param id
     * @param ctx
     * @param o   - output dataset descriptor
     * @return result JSON structure
     */
    private JsonElement createDSDescriptor(String id, ContextMetadata ctx, Map<String, Object> o) {
        JsonObject dl = new JsonObject();
        DateTime currentTIme = new DateTime();
        long epochTime = currentTIme.getMillis() / 1000;

        //TODO:: Dormant, for future development
        String ds_type = o.containsKey(DataSetProperties.Type.name()) ? (String) o.get(DataSetProperties.Type.name()) : Input.Dstype.BASE.toString();
        String catalog = o.containsKey(DataSetProperties.Catalog.name()) ? (String) o.get(DataSetProperties.Catalog.name()) : DEFAULT_CATALOG;

        // Extract user information from output dataset
        Object userDataObject = o.get(DataSetProperties.UserData.name());
        logger.info("Creating DS Descriptor");
        logger.debug("UserData Object = " + userDataObject);
        if (userDataObject != null) {
            JsonObject userData = (JsonObject) userDataObject;

            if (userData.has(DataSetProperties.createdBy.name())) {
                String createdBy = userData.get(DataSetProperties.createdBy.name()).getAsString();
                logger.debug("Created by " + DataSetProperties.createdBy.name() + " " + createdBy);
                dl.add(DataSetProperties.createdBy.toString(), new JsonPrimitive(createdBy));
            }

            if (userData.has(DataSetProperties.modifiedBy.name())) {
                String modifiedBy = userData.get(DataSetProperties.modifiedBy.name()).getAsString();
                logger.debug("Modified by " + DataSetProperties.modifiedBy.name() + " " + modifiedBy);
                dl.add(DataSetProperties.modifiedBy.toString(), new JsonPrimitive(modifiedBy));
            }
        }

//        dl.add(DataSetProperties.User.toString(), new JsonPrimitive(ctx.user));
        dl.add(DataSetProperties.Project.toString(), new JsonPrimitive(ctx.applicationID));
        dl.add(DataSetProperties.Type.toString(), new JsonPrimitive(ds_type));
        dl.add(DataSetProperties.Format.toString(), new JsonPrimitive((String) o.get(DataSetProperties.Format.name())));
        dl.add(DataSetProperties.PhysicalLocation.toString(), new JsonPrimitive((String) o.get(DataSetProperties.PhysicalLocation.name())));
        dl.add(DataSetProperties.Name.toString(), new JsonPrimitive((String) o.get(DataSetProperties.Name.name())));
        dl.add(DataSetProperties.Catalog.toString(), new JsonPrimitive(catalog));
        dl.add(DataSetProperties.NumberOfFiles.toString(), new JsonPrimitive((Integer) o.get(DataSetProperties.NumberOfFiles.name())));
        dl.addProperty(DataSetProperties.CreatedTime.toString(), epochTime);
        dl.addProperty(DataSetProperties.ModifiedTime.toString(), epochTime);
        dl.addProperty(DataSetProperties.Description.toString(), (String) o.get(DataSetProperties.Description.name()));


        //TODO:: Add transformation data from ctx
        JsonArray tr = new JsonArray();
        JsonObject doc = new JsonObject();
        doc.add(DataSetProperties.Id.toString(), new JsonPrimitive(id));
        doc.add(DataSetProperties.System.toString(), dl);
        doc.add(DataSetProperties.Transformations.toString(), tr);
        return doc;
    }


    public JsonElement updateDS(String id, ContextMetadata ctx, JsonElement ds, JsonElement schema, long recordCount) throws Exception {

        if (schema == null || ds == null)
            throw new IllegalArgumentException("Schema/DS descriptor must not be null");

        JsonObject system = ds.getAsJsonObject().get(DataSetProperties.System.toString()).getAsJsonObject();

        DateTime currentTime = new DateTime();
        long modifiedTime = currentTime.getMillis() / 1000;
        logger.debug("Dataset modified at = " + modifiedTime);

        system.addProperty(DataSetProperties.ModifiedTime.toString(), modifiedTime);
        ds.getAsJsonObject().add(DataSetProperties.System.toString(), system);

        JsonObject status = dsStore.createStatusSection(ctx.status, ctx.startTs, ctx.finishedTs, ctx.ale_id, ctx.batchID);
        JsonObject trans = new JsonObject();
        trans.addProperty("asOutput", ctx.transformationID);
        if (schema != null) {
            ds.getAsJsonObject().add(DataSetProperties.Schema.toString(), schema);
        }
        ds.getAsJsonObject().addProperty(DataSetProperties.RecordCount.toString(), recordCount);
        ds.getAsJsonObject().add("transformations", trans);
        ds.getAsJsonObject().add("asOfNow", status);

        dsStore.update(id, ds);
        return ds;
    }

    public JsonElement updateDS(String id, ContextMetadata ctx, JsonElement ds, JsonElement schema, long recordCount, long size) throws Exception {
      
      /**
       * Commented below code as this block is stopping 
       * some entries in updating status to maprDB
       */
      /*if (schema == null || ds == null)
          throw new IllegalArgumentException("Schema/DS descriptor must not be null");*/

      JsonObject system = ds.getAsJsonObject().get(DataSetProperties.System.toString()).getAsJsonObject();

      DateTime currentTime = new DateTime();
      long modifiedTime = currentTime.getMillis() / 1000;
      logger.debug("Dataset modified at = " + modifiedTime);

      system.addProperty(DataSetProperties.ModifiedTime.toString(), modifiedTime);
      ds.getAsJsonObject().add(DataSetProperties.System.toString(), system);

      JsonObject status = dsStore.createStatusSection(ctx.status, ctx.startTs, ctx.finishedTs, ctx.ale_id, ctx.batchID);
      JsonObject trans = new JsonObject();
      trans.addProperty("asOutput", ctx.transformationID);
      if (schema != null) {
          ds.getAsJsonObject().add(DataSetProperties.Schema.toString(), schema);
      }
      ds.getAsJsonObject().addProperty(DataSetProperties.RecordCount.toString(), recordCount);
      ds.getAsJsonObject().addProperty(DataSetProperties.size.toString(), format(size, 2));
      ds.getAsJsonObject().add("transformations", trans);
      ds.getAsJsonObject().add("asOfNow", status);

      logger.debug("########## DLDataSetService : JsonElement updateDS = " + persistMode);

      if (persistMode)
            dsStore.update(id, ds);
      return ds;
  }
    
    
    private String format(double bytes, int digits) {
      String[] dictionary = { "bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB" };
      int index = 0;
      for (index = 0; index < dictionary.length; index++) {
          if (bytes < 1024) {
              break;
          }
          bytes = bytes / 1024;
      }
      return String.format("%." + digits + "f", bytes) + " " + dictionary[index];
  }

    //TODO: talk about ID generation
    /**
     * Producer of Dataset ID:
     * The following data is used to generate ID:
     * - component
     * - user info
     * - user entered data
     * - DL metadata
     * Example???: project::datasource::transformation::transformation-name::dataset-name
     * @param ctx
     * @param o - dataset descriptor
     * @return
     */
    private String generateDSID(ContextMetadata ctx, Map<String, Object> o) {
        StringBuilder sb = new StringBuilder(ctx.applicationID);
        sb.append(delimiter).append(o.get(DataSetProperties.Name.name()));
        logger.debug(String.format("Generated ID for dataset %s: %s ",o.get(DataSetProperties.Name.name()), sb.toString()));
        String id = sb.toString();
        if (StringUtils.isWhitespace((id))) {
          id = StringUtils.deleteWhitespace(id);
        }
        return id;
    }

    public Map<String, JsonElement> loadExistingDataSets(ContextMetadata ctx, Map<String, Map<String, Object>> inputDataSets) throws Exception {

        Map<String, JsonElement> inputDSMetaData = new HashMap<>();
        for(String dsName : inputDataSets.keySet()) {
            Map<String, Object> dsDesc = inputDataSets.get(dsName);
            String id = generateDSID(ctx, dsDesc);
            JsonElement dsJson = dsStore.read(id);
            if (dsJson == null){
                throw new Exception(String.format("Input object [%s] does not exist, cancel processing", id));
            }
            inputDSMetaData.put(id, dsJson);
        }
        return inputDSMetaData;
    }

    public DataSetStore getDSStore () { return dsStore; }

}
