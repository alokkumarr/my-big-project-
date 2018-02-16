package sncr.xdf.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.json4s.jackson.Json;
import sncr.bda.base.MetadataBase;
import sncr.bda.conf.Input;
import sncr.bda.conf.Output;
import sncr.bda.core.file.HFileOperations;
import sncr.bda.datasets.conf.DataSetProperties;
import sncr.bda.services.DLDataSetService;
import sncr.xdf.context.Context;
import sncr.xdf.core.file.DLDataSetOperations;
import sncr.xdf.exceptions.XDFException;

import javax.xml.crypto.Data;
import java.nio.file.Paths;
import java.util.HashMap;
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
     * Use resolveDataObjectWithInput to get Map with Data Object (Object names) as keys.
     */
    default Map<String, Map<String, String>> resolveDataParametersWithInput(DataSetServiceAux aux) throws Exception {
        Map<String, Map<String, String>> retval = new HashMap<>(aux.ctx.componentConfiguration.getInputs().size());
        for (Input in: aux.ctx.componentConfiguration.getInputs()) retval.put(in.getName(), resolveDataObjectWithInput(aux, in));
        return retval;
    }

    default Map<String, Map<String, String>> resolveDataParametersWithMetaData(DataSetServiceAux aux) throws Exception {
        Map<String, Map<String, String>> retval = new HashMap<>(aux.ctx.componentConfiguration.getInputs().size());
        String project = aux.ctx.applicationID;
        for (Input in: aux.ctx.componentConfiguration.getInputs()) {
            retval.put(in.getName(), resolveDataObjectWithMetaData(aux, project, in.getName()));
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
    default Map<String, Map<String, String>> resolveDataObjectsWithInput(DataSetServiceAux aux) throws Exception {
        Map<String, Map<String, String>> retval = new HashMap<>(aux.ctx.componentConfiguration.getInputs().size());
        for (Input in: aux.ctx.componentConfiguration.getInputs())
            retval.put(in.getDataSet(), resolveDataObjectWithInput(aux, in));
        return retval;
    }

    default Map<String, Map<String, String>> resolveDataObjectsWithMetadata(DataSetServiceAux aux) throws Exception {
        Map<String, Map<String, String>> retval = new HashMap<>(aux.ctx.componentConfiguration.getInputs().size());

        String project = aux.ctx.applicationID;

        for (Input in: aux.ctx.componentConfiguration.getInputs())
            retval.put(in.getDataSet(), resolveDataObjectWithMetaData(aux, project,  in.getDataSet()));

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
    default Map<String, String> resolveDataObjectWithInput(DataSetServiceAux aux, Input in) throws Exception {
        String ds = ((in.getDatasource() != null)? in.getDatasource(): MetadataBase.DEFAULT_DATA_SOURCE);
        String prj = ((in.getProject() != null && !in.getProject().isEmpty())?
                Path.SEPARATOR + in.getProject():
                Path.SEPARATOR + aux.ctx.applicationID);

        StringBuilder sb = new StringBuilder(aux.md.getRoot());
        sb.append(prj + Path.SEPARATOR + MetadataBase.PREDEF_DL_DIR)
                .append(Path.SEPARATOR + ds);

        if (in.getCatalog() != null && !in.getCatalog().isEmpty())
                sb.append(Path.SEPARATOR + in.getCatalog());

        sb.append(Path.SEPARATOR + in.getDataSet())
                .append(Path.SEPARATOR + MetadataBase.PREDEF_DATA_DIR);
        DataSetServiceAux.logger.debug(String.format("Resolve object %s in location: %s", in.getDataSet(), sb.toString()));
        if (!HFileOperations.exists(sb.toString())){
            //TODO:: Should we return Map with 'Exists::no' instead of throwing exception
            throw new XDFException(XDFException.ErrorCodes.InputDataObjectNotFound, in.getDataSet());
        }
        else{
            FileStatus[] fst = HFileOperations.getFilesStatus(sb.toString());
            boolean doEmpty = (fst == null && fst.length == 0);
            Map<String, String> res = new HashMap();
            res.put(DataSetProperties.PhysicalLocation.name(), sb.toString());
            res.put(DataSetProperties.Name.name(), in.getDataSet());
            if (in.getCatalog() != null && !in.getCatalog().isEmpty())
                res.put(DataSetProperties.Catalog.name(), in.getCatalog());
            res.put(DataSetProperties.Type.name(), ds);
            //TODO:: Get actual format reading data descriptor
            res.put(DataSetProperties.Format.name(), in.getFormat().name());

            res.put(DataSetProperties.Exists.name(), String.valueOf(true));
            res.put(DataSetProperties.Empty.name(), String.valueOf(doEmpty));
            return res;
        }

    }

    default Map<String, String> resolveDataObjectWithMetaData(DataSetServiceAux aux, String projectName, String dataset) {
        Map<String, String> metaDataMap = new HashMap<>();
        DLDataSetService md = aux.md;

        String datasetId = projectName + "::" + dataset;
        try {
            JsonElement element = md.getDSStore().read(datasetId);

            if (element != null) {
                if (((JsonObject)element).has(DataSetProperties.System.toString())) {
                    JsonObject system = ((JsonObject)element).get(DataSetProperties.System.toString()).getAsJsonObject();

                    String dataLakeRoot = aux.md.getRoot();

                    String projectId = (system.has(DataSetProperties.Project.toString()))?
                            system.get(DataSetProperties.Project.toString()).getAsString() : projectName;

                    String dlDir = MetadataBase.PREDEF_DL_DIR;

                    String dataSource = system.has(DataSetProperties.Type.toString()) ?
                            system.get(DataSetProperties.Type.toString()).getAsString() : MetadataBase.DEFAULT_DATA_SOURCE;

                    String catalog = system.has(DataSetProperties.Catalog.toString()) ?
                            system.get(DataSetProperties.Catalog.toString()).getAsString() : "";

                    String datasetName = system.has(DataSetProperties.Name.toString()) ?
                            system.get(DataSetProperties.Name.toString()).getAsString() : dataset;

                    String dataDir = MetadataBase.PREDEF_DATA_DIR;
                    String format = system.has(DataSetProperties.Format.toString()) ?
                            system.get(DataSetProperties.Format.toString()).getAsString() : null;

                    //TODO: Not working
//                    String location = Paths.get(dataLakeRoot, projectId, dlDir, dataSource, catalog, datasetName, dataDir).toString();

                    String location = dataLakeRoot + Path.SEPARATOR + projectId + Path.SEPARATOR + dlDir
                            + Path.SEPARATOR + dataSource + Path.SEPARATOR + catalog
                            + Path.SEPARATOR + datasetName + Path.SEPARATOR + dataDir;

                    DataSetServiceAux.logger.debug("Dataset location = " + location);

                    if (!HFileOperations.exists(location)){
                        //TODO:: Should we return Map with 'Exists::no' instead of throwing exception
                        throw new XDFException(XDFException.ErrorCodes.InputDataObjectNotFound, dataset);
                    }
                    else{
                        FileStatus[] fst = HFileOperations.getFilesStatus(location);
                        boolean doEmpty = (fst == null && fst.length == 0);
                        Map<String, String> res = new HashMap();
                        res.put(DataSetProperties.PhysicalLocation.name(), location);
                        res.put(DataSetProperties.Name.name(), datasetName);
                        if (catalog != null && !catalog.isEmpty())
                            res.put(DataSetProperties.Catalog.name(), catalog);
                        res.put(DataSetProperties.Type.name(), dataSource);
                        //TODO:: Get actual format reading data descriptor
                        res.put(DataSetProperties.Format.name(), format);

                        res.put(DataSetProperties.Exists.name(), String.valueOf(true));
                        res.put(DataSetProperties.Empty.name(), String.valueOf(doEmpty));


                        DataSetServiceAux.logger.debug("Result Map = " + res);
                        return res;
                    }

                }
                else {
                    throw new XDFException(XDFException.ErrorCodes.InputDataObjectNotFound, dataset);
                }
            } else {
                throw new XDFException(XDFException.ErrorCodes.InputDataObjectNotFound, dataset);
            }
        } catch (Exception e) {
            e.printStackTrace();
            DataSetServiceAux.logger.error(ExceptionUtils.getStackTrace(e));
        }

        return metaDataMap;
    }

    default Map<String, String> createDatasetMap(String physicalLocation, String datasetName, String catalog,
                                                 boolean doEmpty, String dataSource, Input.Format format) {
        Map<String, String> res = new HashMap();
        res.put(DataSetProperties.PhysicalLocation.name(), physicalLocation);
        res.put(DataSetProperties.Name.name(), datasetName);
        if (catalog != null && !catalog.isEmpty())
            res.put(DataSetProperties.Catalog.name(), catalog);
        res.put(DataSetProperties.Type.name(), dataSource);
        res.put(DataSetProperties.Format.name(), format.name());

        res.put(DataSetProperties.Exists.name(), String.valueOf(true));
        res.put(DataSetProperties.Empty.name(), String.valueOf(doEmpty));
        return res;
    }

    default String getInfo(JsonElement metaData) {
        JsonObject system = ((JsonObject)metaData).get(DataSetProperties.System.name()).getAsJsonObject();

        String project = system.get(DataSetProperties.Project.toString()).getAsString();
        String type = system.get(DataSetProperties.Type.toString()).getAsString();
        String dataSetName = system.get(DataSetProperties.Name.toString()).getAsString();
        String catalog = system.get(DataSetProperties.Catalog.toString()).getAsString();


        return "";
    }

    default String generateTempLocation(DataSetServiceAux aux, String tempDS, String tempCatalog) {
        StringBuilder sb = new StringBuilder(aux.md.getRoot());
        sb.append(Path.SEPARATOR + aux.ctx.applicationID)
                .append(Path.SEPARATOR + ((tempDS == null || tempDS.isEmpty())? MetadataBase.PREDEF_SYSTEM_DIR :tempDS))
                .append(Path.SEPARATOR + ((tempCatalog == null || tempCatalog.isEmpty())? MetadataBase.PREDEF_TEMP_DIR :tempCatalog))
                .append(Path.SEPARATOR + aux.ctx.batchID)
                .append(Path.SEPARATOR + aux.ctx.componentName);

        DataSetServiceAux.logger.debug(String.format("Generated temp location: %s", sb.toString()));
        return sb.toString();
    }

    default Map<String,Map<String,String>> buildPathForOutputs(DataSetServiceAux dsaux){
        return dsaux.buildDataSetMap(DSMapKey.parameter);
    }

    default Map<String, Map<String,String>> buildPathForOutputDataSets(DataSetServiceAux aux){
        return aux.buildDataSetMap(DSMapKey.dataset);
    }

    class DataSetServiceAux {
        private static final Logger logger = Logger.getLogger(WithDataSetService.class);
        Context ctx;
        DLDataSetService md;

        public DataSetServiceAux(Context c, DLDataSetService m){
            ctx = c; md = m;
        }

        private Map<String, Map<String,String>> buildDataSetMap( DSMapKey ktype)
        {
            Map<String, Map<String,String>> resMap = new HashMap();
            for( Output output: this.ctx.componentConfiguration.getOutputs()){
                Map<String, String> res_output = new HashMap<String, String>();

                String dataSource = (output.getDataSource() != null) ? output.getDataSource().toString(): MetadataBase.PREDEF_DATA_SOURCE;
                String catalog = (output.getCatalog() != null)? output.getCatalog():  MetadataBase.DEFAULT_CATALOG;
                String format = (output.getFormat() != null) ? output.getFormat().toString() : DLDataSetOperations.FORMAT_PARQUET;
                String mode = (output.getMode() != null) ? output.getMode().toString() : DLDataSetOperations.MODE_APPEND;


                StringBuilder sb = new StringBuilder(this.md.getRoot());
                sb.append(Path.SEPARATOR + this.ctx.applicationID)
                        .append(Path.SEPARATOR + MetadataBase.PREDEF_DL_DIR)
                        .append(Path.SEPARATOR + dataSource)
                        .append(Path.SEPARATOR + catalog)
                        .append(Path.SEPARATOR + output.getDataSet())
                        .append(Path.SEPARATOR + MetadataBase.PREDEF_DATA_DIR);

                DataSetServiceAux.logger.debug(String.format("Resolve object %s in location: %s", output.getDataSet(), sb.toString()));
                res_output.put(DataSetProperties.PhysicalLocation.name(), sb.toString());
                res_output.put(DataSetProperties.Name.name(), output.getDataSet());
                String ds = dataSource;
                String nof = (output.getNumberOfFiles() != null)? String.valueOf(output.getNumberOfFiles()):"1";
                res_output.put(DataSetProperties.Type.name(), ds);
                res_output.put(DataSetProperties.Catalog.name(), catalog);
                res_output.put(DataSetProperties.Format.name(), format);
                res_output.put(DataSetProperties.NumberOfFiles.name(), nof);
                res_output.put(DataSetProperties.Mode.name(), mode);
                boolean exists = false;
                try {
                    exists = HFileOperations.exists(sb.toString());
                } catch (Exception e) {
                    DataSetServiceAux.logger.warn("Could not check output data object: " + output.getDataSet());
                }
                res_output.put(DataSetProperties.Exists.name(), String.valueOf(exists));

                switch (ktype) {
                    case parameter:
                        resMap.put(output.getName(), res_output); break;
                    case dataset:
                        resMap.put(output.getDataSet(), res_output); break;
                }
            }
            return resMap;
        }

    }

    enum DSMapKey{
        parameter,
        dataset
    };

}

