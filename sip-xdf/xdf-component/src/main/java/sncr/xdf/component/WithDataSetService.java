package sncr.xdf.component;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import scala.Tuple4;
import sncr.bda.base.MetadataBase;
import sncr.bda.conf.Input;
import sncr.bda.conf.Output;
import sncr.bda.core.file.HFileOperations;
import sncr.bda.datasets.conf.DataSetProperties;
import sncr.bda.services.DLDataSetService;
import sncr.xdf.context.Context;
import sncr.xdf.context.DSMapKey;
import sncr.xdf.file.DLDataSetOperations;
import sncr.xdf.exceptions.XDFException;
import sncr.xdf.context.XDFReturnCode;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by asor0002 on 9/8/2017.
 * Interface provides
 *
 */
public interface WithDataSetService {

    /**
     * The method creates Map of Input/Output parameter name to Map of data object attributes
     * Data object attributes includes:
     * - physical location
     * - data object name
     * - data object format
     * - data object container type
     * - indicates if it is Empty
     * - indicates if it is Exists
     * The method uses Input[n].Name attribute not Input[n].Object attribute.
     * Use discoverDataSetWithInput to get Map with Data Object (Object names) as partitionList.
     */
    default Map<String, Map<String, Object>> discoverDataParametersWithInput(DataSetServiceAux aux) throws Exception {
        Map<String, Map<String, Object>> retval = new HashMap<>(aux.ctx.componentConfiguration.getInputs().size());
        for (Input in: aux.ctx.componentConfiguration.getInputs()){
            if (in.getName() != null)
                retval.put(in.getName(), discoverDataSetWithInput(aux, in));
        }
        return retval;
    }

    default Map<String, Map<String, Object>> discoverDataParametersWithMetaData(DataSetServiceAux aux) throws Exception {
        Map<String, Map<String, Object>> retval = new HashMap<>(aux.ctx.componentConfiguration.getInputs().size());
        String project = aux.ctx.applicationID;
        for (Input in: aux.ctx.componentConfiguration.getInputs()) {

            if (in.getName() != null)
                retval.put(in.getName(), discoverDataSetWithMetaData(aux, project, in.getDataSet()));

        }

        return retval;
    }

    /**
     * The method creates Map of Input/Output parameter name to Map of data object attributes
     * Data object attributes includes:
     * - data object name
     * - data object format
     * - data object container type
     * - indicates if it is Empty
     * - indicates if it is Exists ???
     */
    default Map<String, Map<String, Object>> discoverInputDataSetsWithInput(DataSetServiceAux aux) throws Exception {
        Map<String, Map<String, Object>> retval = new HashMap<>(aux.ctx.componentConfiguration.getInputs().size());

        for (Input in: aux.ctx.componentConfiguration.getInputs()) {
            if (in.getDataSet() == null)
                throw new XDFException(XDFReturnCode.CONFIG_ERROR, "DataSet parameter cannot be null");
            retval.put(in.getDataSet(), discoverDataSetWithInput(aux, in));
        }

        return retval;
    }

    default Map<String, Map<String, Object>> discoverInputDataSetsWithMetadata(DataSetServiceAux aux) throws Exception {

        String project = aux.ctx.applicationID;
        DataSetServiceAux.logger.debug("Set projects " + project);
        Map<String, Map<String, Object>> retval = new HashMap<>(aux.ctx.componentConfiguration.getInputs().size());

        for (Input in: aux.ctx.componentConfiguration.getInputs()) {
            if (in.getDataSet() == null)
                throw new XDFException(XDFReturnCode.CONFIG_ERROR, "DataSet parameter cannot be null");

            retval.put(in.getDataSet(), discoverDataSetWithMetaData(aux, project, in.getDataSet()));
        }
        return retval;
    }

    /**
     * The method creates Map of Input/Output parameter name to Map of data object attributes
     * Data object attributes includes:
     * - data object name
     * - data object format
     * - data object container type
     * - indicates if it is Empty
     * - indicates if it is Exists ???
     */
    default Map<String, Object> discoverDataSetWithInput(DataSetServiceAux aux, Input in) throws Exception {
        String prj = ((in.getProject() != null && !in.getProject().isEmpty()) ?
                Path.SEPARATOR + in.getProject() :
                Path.SEPARATOR + aux.ctx.applicationID);

        StringBuilder sb = new StringBuilder(aux.dl.getRoot());
        sb.append(prj + Path.SEPARATOR + MetadataBase.PREDEF_DL_DIR)
                .append(Path.SEPARATOR + MetadataBase.PREDEF_DATA_SOURCE)
                .append(Path.SEPARATOR + in.getCatalog());

        sb.append(Path.SEPARATOR + in.getDataSet()).append(Path.SEPARATOR + MetadataBase.PREDEF_DATA_DIR);

        if (in.getCatalog() != null && !in.getCatalog().isEmpty())
            sb.append(Path.SEPARATOR + in.getCatalog());
        sb
                .append(Path.SEPARATOR + in.getDataSet())
                .append(Path.SEPARATOR + MetadataBase.PREDEF_DATA_DIR);

        DataSetServiceAux.logger.debug(String.format("Resolve object %s in location: %s", in.getDataSet(), sb.toString()));

        if (!HFileOperations.exists(sb.toString())) {
            //TODO:: Should we return Map with 'Exists::no' instead of throwing exception
            throw new XDFException(XDFReturnCode.INPUT_DATA_OBJECT_NOT_FOUND, in.getDataSet());
        } else {
            DataSetServiceAux.logger.debug(String.format("Resolve object %s in location: %s", in.getDataSet(), sb.toString()));


            Map<String, Object> res = aux.discoverAndValidateInputDS(in.getDataSet(), sb.toString(), null);

            res.put(DataSetProperties.PhysicalLocation.name(), sb.toString());
            res.put(DataSetProperties.Name.name(), in.getDataSet());
            if (in.getCatalog() != null && !in.getCatalog().isEmpty())
                res.put(DataSetProperties.Catalog.name(), in.getCatalog());
            res.put(DataSetProperties.Type.name(), in.getDstype().toString());
            //TODO:: Get actual format reading data descriptor
            res.put(DataSetProperties.Format.name(), in.getFormat().name());
            return res;


        }
    }

    default Map<String, Object> discoverDataSetWithMetaData(DataSetServiceAux aux, String projectName, String dataset) throws Exception {
//        DLDataSetService md = aux.ctx.md;
        String datasetId = projectName + "::" + dataset;
        JsonElement element = aux.dl.getDSStore().read(datasetId);

        if (element != null) {
            if (((JsonObject)element).has(DataSetProperties.System.toString())) {
                JsonObject system = ((JsonObject)element).get(DataSetProperties.System.toString()).getAsJsonObject();

                String dataLakeRoot = aux.dl.getRoot();

                String projectId = (system.has(DataSetProperties.Project.toString()))?
                        system.get(DataSetProperties.Project.toString()).getAsString() : projectName;

                String dlDir = MetadataBase.PREDEF_DL_DIR;

                String dataSource = system.has(DataSetProperties.Type.toString()) ?
                        system.get(DataSetProperties.Type.toString()).getAsString(): Input.Dstype.BASE.toString();

                String catalog = system.has(DataSetProperties.Catalog.toString()) ?
                        system.get(DataSetProperties.Catalog.toString()).getAsString() : MetadataBase.DEFAULT_CATALOG;

                String datasetName = system.has(DataSetProperties.Name.toString()) ?
                        system.get(DataSetProperties.Name.toString()).getAsString() : dataset;

                String dataDir = MetadataBase.PREDEF_DATA_DIR;
                String format = system.has(DataSetProperties.Format.toString()) ?
                        system.get(DataSetProperties.Format.toString()).getAsString() : null;


                String location = dataLakeRoot + Path.SEPARATOR + projectId + Path.SEPARATOR + dlDir
                        + Path.SEPARATOR + MetadataBase.PREDEF_DATA_SOURCE + Path.SEPARATOR + catalog
                        + Path.SEPARATOR + datasetName + Path.SEPARATOR + dataDir;
                
                DataSetServiceAux.logger.debug("Dataset location = " + location);

                //TODO:: Fix BDA Meta
                String sampling = system.has("sample") ?
                        system.get("sample").getAsString() : DLDataSetOperations.SIMPLE_SAMPLING;


                Map<String, Object> res = aux.discoverAndValidateInputDS(dataset, location, system);

                res.put(DataSetProperties.PhysicalLocation.name(), location);
                res.put(DataSetProperties.Name.name(), datasetName);

                if (catalog != null && !catalog.isEmpty())
                    res.put(DataSetProperties.Catalog.name(), catalog);

                res.put(DataSetProperties.Type.name(), dataSource);
                res.put(DataSetProperties.Format.name(), format);

                //TODO:: Fix BDA Meta
                res.put("sample", sampling);
                DataSetServiceAux.logger.debug("Result Map = " + res);

                return res;
            }
            else {
                throw new XDFException(XDFReturnCode.INPUT_DATA_OBJECT_NOT_FOUND, dataset);
            }
        } else {
            throw new XDFException(XDFReturnCode.INPUT_DATA_OBJECT_NOT_FOUND, dataset);
        }
    }



    default boolean discoverAndvalidateOutputDataSet(Map<String, Object> outDS){
        // We will need list of partitions created in order to rename and move files to final destination
        // Get information about newly created partitions - we need proper glob

        String location = (String) outDS.get(DataSetProperties.PhysicalLocation.name());
        String dataset = (String) outDS.get(DataSetProperties.Name.name());

        String mode = (String) outDS.get(DataSetProperties.Mode.name());
        boolean exists = (boolean) outDS.get(DataSetProperties.Exists.name());

        Tuple4<String, List<String>, Integer, DLDataSetOperations.PARTITION_STRUCTURE> trgDSPartitioning =
                DLDataSetOperations.getPartitioningInfo(location);

        DataSetServiceAux.logger.debug("Dataset partitioning = " + trgDSPartitioning);

        //Check partitioning structure and match it with metadata/input
        if (trgDSPartitioning._4() != DLDataSetOperations.PARTITION_STRUCTURE.HIVE &&
                trgDSPartitioning._4() != DLDataSetOperations.PARTITION_STRUCTURE.FLAT) {
            throw new XDFException(XDFReturnCode.UNSUPPORTED_PARTITIONING, trgDSPartitioning._4().toString(), dataset);
        }

        List<String> partitionKeys =
            (List<String>) outDS.get(DataSetProperties.PartitionKeys.name());

        if (exists && mode.toLowerCase().equals(DLDataSetOperations.MODE_APPEND)) {
            if (partitionKeys != null) {
                if (trgDSPartitioning._4() == DLDataSetOperations.PARTITION_STRUCTURE.HIVE
                    && trgDSPartitioning._2() != null) {
                    for (int i = 0; i < partitionKeys.size(); i++)
                        if (!partitionKeys.get(i).equalsIgnoreCase(trgDSPartitioning._2().get(i))) {
                            throw new XDFException(XDFReturnCode.CONFIG_ERROR,
                                "Order and/or set of partitioning keys in Metadata" +
                                    " and in dataset does not match");
                        }
                }
            } else  //No key were provided in Output Dataset configuration: add them from existing dataset
            {
                DataSetServiceAux.logger.warn("Output dataset parameter does not provides partitioning keys, but existent dataset is partitioned, set partition configuration from existing dataset");
                if (trgDSPartitioning._4() == DLDataSetOperations.PARTITION_STRUCTURE.HIVE && trgDSPartitioning._2() != null) {
                    outDS.put(DataSetProperties.PartitionKeys.name(), trgDSPartitioning._2());
                }
            }
        }
        return true;
    }

    default Map<String, Object> createDatasetMap(String physicalLocation, String datasetName, String catalog,
                                                 boolean doEmpty, String dstype, Input.Format format) {
        Map<String, Object> res = new HashMap();

//TODO:: Should not be in Metadata
//        res.put(DataSetProperties.PhysicalLocation.name(), physicalLocation);

        res.put(DataSetProperties.Name.name(), datasetName);
        if (catalog != null && !catalog.isEmpty())
            res.put(DataSetProperties.Catalog.name(), catalog);
        else
            res.put(DataSetProperties.Catalog.name(), MetadataBase.DEFAULT_CATALOG);

        res.put(DataSetProperties.Type.name(), dstype);
        res.put(DataSetProperties.Format.name(), format.name());

        res.put(DataSetProperties.Exists.name(), true);
        res.put(DataSetProperties.Empty.name(), doEmpty);
        return res;
    }


    default String  generateTempLocation(DataSetServiceAux aux, String tempDS, String tempCatalog) {
        StringBuilder tempLocationBuilder = new StringBuilder(aux.dl.getRoot());
        tempLocationBuilder.append(Path.SEPARATOR + aux.ctx.applicationID)
                .append(Path.SEPARATOR + ((tempDS == null || tempDS.isEmpty())? MetadataBase.PREDEF_SYSTEM_DIR :tempDS))
                .append(Path.SEPARATOR + ((tempCatalog == null || tempCatalog.isEmpty())? MetadataBase.PREDEF_TEMP_DIR :tempCatalog))
                .append(Path.SEPARATOR + aux.ctx.batchID)
                .append(Path.SEPARATOR + aux.ctx.componentName)

                 /* Creating a dynamic directory, so that components running is parallel will not
                  * run into conflicts
                  */
                .append(Path.SEPARATOR + Instant.now().toEpochMilli());

        DataSetServiceAux.logger.debug(String.format("Generated temp location: %s",
            tempLocationBuilder.toString()));
        return tempLocationBuilder.toString();
    }

    default String generateArchiveLocation(DataSetServiceAux aux) {
        StringBuilder archiveLocationBuilder = new StringBuilder(aux.dl.getRoot());

        archiveLocationBuilder.append(Path.SEPARATOR + aux.ctx.applicationID)
            .append(Path.SEPARATOR + MetadataBase.PREDEF_SYSTEM_DIR)
            .append(Path.SEPARATOR + MetadataBase.PREDEF_ARCHIVE_DIR)
            .append(Path.SEPARATOR + aux.ctx.batchID)
            .append(Path.SEPARATOR + aux.ctx.componentName);

        return archiveLocationBuilder.toString();
    }

    default String  ngGenerateTempLocation(DataSetServiceAux aux, String tempDS, String tempCatalog) {
        StringBuilder sb = new StringBuilder(aux.dl.getRoot());
        sb.append(Path.SEPARATOR + aux.ctx.applicationID)
                .append(Path.SEPARATOR + ((tempDS == null || tempDS.isEmpty())? MetadataBase.PREDEF_SYSTEM_DIR :tempDS))
                .append(Path.SEPARATOR + ((tempCatalog == null || tempCatalog.isEmpty())? MetadataBase.PREDEF_TEMP_DIR :tempCatalog))
                .append(Path.SEPARATOR + aux.ctx.batchID)
                .append(Path.SEPARATOR + aux.ctx.componentName);

        DataSetServiceAux.logger.debug(String.format("Generated temp location: %s", sb.toString()));
        return sb.toString();
    }

    default Map<String,Map<String, Object>> buildPathForOutputs(DataSetServiceAux dsaux){
        return dsaux.buildDataSetMap(DSMapKey.parameter);
    }

    default Map<String, Map<String, Object>> buildPathForOutputDataSets(DataSetServiceAux aux){
        return aux.buildDataSetMap(DSMapKey.dataset);
    }

    class DataSetServiceAux {
        private static final Logger logger = Logger.getLogger(WithDataSetService.class);
        Context ctx;
        public DLDataSetService dl;


        public DataSetServiceAux(Context c, DLDataSetService md){
            ctx = c;
            dl = md;
        }

        public Map<String, Map<String, Object>> buildDataSetMap( DSMapKey ktype)
        {
            Map<String, Map<String, Object>> resMap = new HashMap();
            for( Output output: this.ctx.componentConfiguration.getOutputs()){
                Map<String, Object> resOutput = new HashMap<>();
                String catalog = (output.getCatalog() != null)? output.getCatalog():  MetadataBase.DEFAULT_CATALOG;
                String format = (output.getFormat() != null) ? output.getFormat().toString() : DLDataSetOperations.FORMAT_PARQUET;
                String mode = (output.getMode() != null) ? output.getMode().toString() : DLDataSetOperations.MODE_APPEND;

                String description = (output.getDescription() != null) ? output.getDescription() : "";

                String sampling = DLDataSetOperations.SIMPLE_SAMPLING;

                StringBuilder physicalLocationBuffer = new StringBuilder(dl.getRoot());
                physicalLocationBuffer.append(Path.SEPARATOR + this.ctx.applicationID)
                        .append(Path.SEPARATOR + MetadataBase.PREDEF_DL_DIR)
                        .append(Path.SEPARATOR + MetadataBase.PREDEF_DATA_SOURCE)
                        .append(Path.SEPARATOR + catalog)
                        .append(Path.SEPARATOR + output.getDataSet())
                        .append(Path.SEPARATOR + MetadataBase.PREDEF_DATA_DIR);

                logger.debug(String.format("Resolve object %s in location: %s", output.getDataSet(), physicalLocationBuffer.toString()));
                resOutput.put(DataSetProperties.PhysicalLocation.name(), physicalLocationBuffer
                    .toString());
                resOutput.put(DataSetProperties.Name.name(), output.getDataSet());

                Integer nof = (output.getNumberOfFiles() != null)? output.getNumberOfFiles() :1;
                resOutput.put(DataSetProperties.Type.name(), output.getDstype().toString() );
                resOutput.put(DataSetProperties.Catalog.name(), catalog);
                resOutput.put(DataSetProperties.Format.name(), format);
                resOutput.put(DataSetProperties.NumberOfFiles.name(), nof);
                resOutput.put(DataSetProperties.Mode.name(), mode);
                resOutput.put(DataSetProperties.Description.name(), description);

                //Extract User Information
                Object userDataObject = output.getUserdata();
                logger.debug("UserDataobject = " + userDataObject);

                JsonObject userData = null;
                if (userDataObject != null) {
                    userData =  new Gson().toJsonTree((LinkedTreeMap)userDataObject).getAsJsonObject();
                    if (userData != null) {
                        resOutput.put(DataSetProperties.UserData.name(), userData);
                    }
                }

                //TODO::Fix BDA Meta
                //TODO: Need to change this to DatasetProperty
                resOutput.put("sample", sampling);


                //TODO:: Do we really need it??
                List<String> kl = new ArrayList<>();
                kl.addAll(output.getPartitionKeys());

                String m = "Configured keys: [" + kl.size()+ "]";
                for (String s : kl) m += s + " ";
                logger.trace(m);


                resOutput.put(DataSetProperties.PartitionKeys.name(), kl);


                boolean exists = false;
                try {
                    exists = HFileOperations.exists(physicalLocationBuffer.toString());
                } catch (Exception e) {
                    logger.warn("Could not check output data object: " + output.getDataSet());
                }
                resOutput.put(DataSetProperties.Exists.name(), exists);

                switch (ktype) {
                    case parameter:
                        if (output.getName() != null)
                            resMap.put(output.getName(), resOutput); break;
                    case dataset:
                        if (output.getDataSet() != null)
                            resMap.put(output.getDataSet(), resOutput); break;
                }
            }

            logger.debug("Output dataset map" + resMap);
            return resMap;
        }


        private Map<String, Object> discoverAndValidateInputDS(String dataset, String location, JsonObject system) throws Exception {
            if (!HFileOperations.exists(location)) {
                throw new XDFException(XDFReturnCode.INPUT_DATA_OBJECT_NOT_FOUND, dataset);
            } else {
                Map<String, Object> res = new HashMap();

                FileStatus[] fst = HFileOperations.getFilesStatus(location);
                boolean dsEmpty = (fst == null && fst.length == 0);
                if (!dsEmpty) {

                    // Check type of source partition - we need proper path which can read all parquet data files
                    Tuple4<String, List<String>, Integer, DLDataSetOperations.PARTITION_STRUCTURE> srcPartitioning =
                            DLDataSetOperations.getPartitioningInfo(location);

                    logger.debug(String.format("Check partition layout of input dataset %s --> type: %s, final location: %s", dataset, srcPartitioning._4(), srcPartitioning._1() ));
                    //TODO::Potentially we can add DRILL support to read from DRILL partitions.
                    //Check partitioning structure and match it with metadata/input
                    if (srcPartitioning._4() != DLDataSetOperations.PARTITION_STRUCTURE.HIVE &&
                            srcPartitioning._4() != DLDataSetOperations.PARTITION_STRUCTURE.FLAT) {
                        throw new XDFException(XDFReturnCode.UNSUPPORTED_PARTITIONING, srcPartitioning._4().toString(), dataset);
                    }

                    if (system != null && system.get(DataSetProperties.PartitionKeys.toString()) != null) {
                        JsonArray mdKeyListJa = system.get(DataSetProperties.PartitionKeys.toString()).getAsJsonArray();

                        if (srcPartitioning._4() == DLDataSetOperations.PARTITION_STRUCTURE.HIVE && srcPartitioning._2() != null) {
                            for (int i = 0; i < mdKeyListJa.size(); i++)
                                if (!mdKeyListJa.get(i).getAsString().equalsIgnoreCase(srcPartitioning._2().get(i))) {
                                    throw new XDFException(XDFReturnCode.CONFIG_ERROR, "Order and/or set of partitioning keys in Metadata and in dataset does not match");
                                }
                            res.put(DataSetProperties.PartitionKeys.name(), srcPartitioning._2());
                        }
                    }
                    else  //Call is done with input configuration parameters - no match is required.
                    {
                        if (srcPartitioning._4() == DLDataSetOperations.PARTITION_STRUCTURE.HIVE && srcPartitioning._2() != null) {
                            res.put(DataSetProperties.PartitionKeys.name(), srcPartitioning._2());
                        }
                    }


                } else {
                    DataSetServiceAux.logger.warn("Empty input dataset: " + dataset);
                }
                res.put(DataSetProperties.Empty.name(), dsEmpty);
                res.put(DataSetProperties.Exists.name(), true);

                return res;
            }

        }

    }



}

