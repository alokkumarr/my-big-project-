package sncr.xdf.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.*;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import sncr.xdf.exceptions.XDFException;
import sncr.xdf.conf.Metadata;
import sncr.xdf.conf.Output;
import sncr.xdf.context.Context;
import sncr.xdf.core.file.HFileOperations;
import sncr.xdf.datasets.conf.DataSetProperties;
import sncr.xdf.datasets.conf.Dataset;
import sncr.xdf.metastore.DSStore;

import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS;
import static sncr.xdf.base.MetadataStore.delimiter;

/**
 * The class provides functions
 * Created by srya0001 on 10/27/2017.
 */
public class DLDSMeta{

    private static final Logger logger = Logger.getLogger(DLDSMeta.class);

    protected static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Map<String, Map<String, Object>> repository;
    private DSStore dsStore;

    public String getRoot(){
        return dsStore.getRoot();
    }


    public DLDSMeta(String fsr) throws Exception {
        dsStore = new DSStore(fsr);
        repository = new HashMap();
    }

    public void addDataSetToDLMeta(Map<String, String> outputDataSet, Output outputDataSetMeta) {
        if (repository.containsKey(outputDataSet.get(DataSetProperties.Name.name()))) {
            logger.warn("Data object " + outputDataSet.get(DataSetProperties.Name.name()) + " already registered - overwriting");
        }
        HashMap<String, Object> entry = new HashMap<String, Object>();
        outputDataSet.forEach(entry::put);
        entry.put(DataSetProperties.isNewDataSet.name(), "true");

        //TODO:: The header info is required only to build linage
        //header.forEach(entry::put);

        Metadata meta = outputDataSetMeta.getMetadata();
        if (meta != null) {
            entry.put(DataSetProperties.MetaCreatedBy.name(), meta.getCreatedBy());
            entry.put(DataSetProperties.MetaDescription.name(), meta.getDescription());
            if (meta.getTags() != null && !meta.getTags().isEmpty()) {
                entry.put(DataSetProperties.MetaTags.name(), meta.getTags());
            }
        }
        else{
            logger.info(String.format("Metadata for dataset: %s are Empty!", outputDataSet.get(DataSetProperties.Name.name())));
        }
        logger.debug("Newly added object");
        entry.forEach( (k, v) -> logger.debug( "Key: " + k + " Value: " + v ));
        repository.put(outputDataSet.get(DataSetProperties.Name.name()), entry);
    }

    public void removeDataObject(String name) {
        if ( repository.containsKey(name) &&
                repository.get(name).containsKey(DataSetProperties.isNewDataSet.name())
                )
            repository.remove(name);
        else
            logger.error("Removing existing objects is prohibited");
    }

    public void writeDLMetadata(Context ctx) throws Exception {

        logger.trace("Save changes in data Object repository");

        repository.keySet().forEach(  k -> {
            Map<String, Object> entry = repository.get(k);
            String dt = format.format(new Timestamp(new Date().getTime()));
            if (entry.containsKey(DataSetProperties.isNewDataSet.name())){

                String metadataFileName = buildMetadataFileName(ctx, entry);

                logger.trace("New file in Data Object repository: " + metadataFileName );

                Dataset dataset = new Dataset();

                //TODO:: Set linage information here
                //dataset.setBatchId(header.get(DataSetProperties.BatchID.name()));

                dataset.setComponent(ctx.componentName);

                String crtby = (entry.containsKey(DataSetProperties.MetaCreatedBy.name()))?(String)
                        entry.get(DataSetProperties.MetaCreatedBy.name()):"nobody";

                dataset.setCreatedBy(crtby);
                dataset.setCreatedTs(dt);

                String desc = (entry.containsKey(DataSetProperties.MetaDescription.name()))?(String)
                        entry.get(DataSetProperties.MetaDescription.name()):"__none__";
                dataset.setDescription(desc);

                dataset.setFormat((String) entry.get(DataSetProperties.Format.name()));

                if (entry.containsKey(DataSetProperties.MetaTags.name()))
                    dataset.setTags((List<String>) entry.get(DataSetProperties.MetaTags.name()));

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
                    throw new XDFException(XDFException.ErrorCodes.CouldNotCreateDSMeta, e);
                }

            }
        });
    }

    private String buildMetadataFileName(Context ctx, Map<String, Object> ds) {
        StringBuilder sb = new StringBuilder(dsStore.getRoot());
        sb.append(Path.SEPARATOR + ctx.applicationID)
        .append(Path.SEPARATOR + MetadataBase.PREDEF_DL_DIR)
        .append(Path.SEPARATOR + ds.get(DataSetProperties.Type.name()))
        .append(Path.SEPARATOR + ds.get(DataSetProperties.Catalog.name()))
        .append(Path.SEPARATOR + ds.get(DataSetProperties.Name.name()))
        .append(Path.SEPARATOR + MetadataBase.FILE_DESCRIPTOR);

        logger.debug(String.format("Resolve metadata storage of %s dataset to location: %s",
                ds.get(DataSetProperties.Name.name()), sb.toString()));
        return sb.toString();
    }


    public void logMetadata(){
        logger.debug("Metadata: \n");
        repository.keySet().forEach(n ->
                {
                    Map<String, Object> props = repository.get(n);
                    logger.debug( String.format("Property: %s", n));
                    props.forEach( (pn, pv ) -> logger.debug(String.format("%s => %s", pn, pv)));
                }
        );
    }



    //TODO:: We need a way to update existing DS
    // with new Metadata
    // with new other Props?
    /**
     * The method reads or creates dataset.
     * The ID is being generated by fixed rule and DataSet ID depends only on
     * - component
     * - user info
     * - user entered data
     * - DL metadata
     * If processMap dataset is not found - the component creates it with all data available.
     * @param ctx
     * @param o
     * @param metadata
     * @return
     */
    public JsonElement readOrCreateDataSet(Context ctx, Map<String, String> o, Metadata metadata) {
        try {
            String id = generateDSID(ctx, o);
            if (!o.containsKey(DataSetProperties.Id.toString()))
                o.put(DataSetProperties.Id.toString(), id);
                JsonElement ds = dsStore.read(id);
            if ( ds == null ){
                JsonElement je = createDSDescriptor(id, ctx, o, metadata);
                dsStore.create(id, je);
                return je;
            }
        else
            return ds;
        } catch (Exception e) {
            logger.error("Could not read or create Data set: ", e);
            return null;
        }
    }

    /**
     * The method is to create Json structure from
     * dataset descriptor, with metadata that must be provided through Context object
     *
     * @param id
     * @param ctx
     * @param o - output dataset descriptor
     * @param metadata
     * @return result JSON structure
     */
    private JsonElement createDSDescriptor(String id, Context ctx, Map<String, String> o, Metadata metadata){
        JsonObject dl = new JsonObject();
        dl.add(DataSetProperties.Type.toString(), new JsonPrimitive(o.get(DataSetProperties.Type.name())));
        dl.add(DataSetProperties.Format.toString(), new JsonPrimitive(o.get(DataSetProperties.Format.name())));
        dl.add(DataSetProperties.PhysicalLocation.toString(), new JsonPrimitive(o.get(DataSetProperties.PhysicalLocation.name())));
        dl.add(DataSetProperties.Catalog.toString(), new JsonPrimitive(o.get(DataSetProperties.Catalog.name())));
        dl.add(DataSetProperties.NumberOfFiles.toString(), new JsonPrimitive(o.get(DataSetProperties.NumberOfFiles.name())));
        JsonObject userdata = new JsonObject();
        if ( metadata != null) {
            userdata.add(DataSetProperties.Creator.toString(), new JsonPrimitive(metadata.getCreatedBy()));
            if (metadata.getTags() != null){
                JsonArray ja = new JsonArray();
                for(String tag : metadata.getTags()) ja.add( new JsonPrimitive(tag) );
                userdata.add( DataSetProperties.MetaTags.toString(), ja);
            }
            userdata.add(DataSetProperties.Description.toString(), new JsonPrimitive(metadata.getDescription()));
        }
        //TODO:: Add transformation data from ctx
        JsonArray tr = new JsonArray();
        JsonObject doc = new JsonObject();
        doc.add(DataSetProperties.Id.toString(), new JsonPrimitive(id));
        doc.add(DataSetProperties.DataLake.toString(), dl);
        doc.add(DataSetProperties.UserData.toString(), userdata);
        doc.add(DataSetProperties.Transformations.toString(), tr);
        return doc;
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
    private String generateDSID(Context ctx, Map<String, String> o) {
        StringBuilder sb = new StringBuilder(ctx.applicationID);
        sb
        .append(delimiter).append(ctx.user)
        .append(delimiter).append(ctx.componentName)
        .append(delimiter).append(ctx.transformationName)
        .append(delimiter).append(o.get(DataSetProperties.Type.name()))
        .append(delimiter).append(o.get(DataSetProperties.Name.name()));
        logger.debug(String.format("Generated ID for dataset %s: %s ",o.get(DataSetProperties.Name.name()), sb.toString()));
        return sb.toString();
    }

    public Map<String, JsonElement> loadExistingDataSets(Context ctx, Map<String, Map<String, String>> inputDataSets) throws Exception {

        Map<String, JsonElement> inputDSMetaData = new HashMap<>();
        for(String dsName : inputDataSets.keySet()) {
            Map<String, String> dsDesc = inputDataSets.get(dsName);
            String id = generateDSID(ctx, dsDesc);
            JsonElement dsJson = dsStore.read(id);
            if (dsJson == null){
                throw new Exception(String.format("Input object [%s] does not exist, cancel processing", id));
            }
            inputDSMetaData.put(id, dsJson);
        }
        return inputDSMetaData;
    }
}
