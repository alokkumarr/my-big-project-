package sncr.xdf.component;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import sncr.bda.base.MetadataBase;
import sncr.bda.conf.Input;
import sncr.bda.conf.Output;
import sncr.bda.core.file.HFileOperations;
import sncr.bda.datasets.conf.DataSetProperties;
import sncr.bda.services.DLDataSetService;
import sncr.xdf.context.Context;
import sncr.xdf.core.file.DLDataSetOperations;
import sncr.xdf.exceptions.XDFException;

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
     * Use resolveDataObject to get Map with Data Object (Object names) as keys.
     */
    default Map<String, Map<String, String>> resolveDataParameters(DataSetServiceAux aux) throws Exception {
        Map<String, Map<String, String>> retval = new HashMap<>(aux.ctx.componentConfiguration.getInputs().size());
        for (Input in: aux.ctx.componentConfiguration.getInputs()) retval.put(in.getName(), resolveDataObject(aux, in));
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
    default Map<String, Map<String, String>> resolveDataObjects(DataSetServiceAux aux) throws Exception {
        Map<String, Map<String, String>> retval = new HashMap<>(aux.ctx.componentConfiguration.getInputs().size());
        for (Input in: aux.ctx.componentConfiguration.getInputs())
            retval.put(in.getDataSet(), resolveDataObject(aux, in));
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
    default Map<String, String> resolveDataObject(DataSetServiceAux aux, Input in) throws Exception {
        String ds = ((in.getDatasource() != null)? in.getDatasource(): MetadataBase.DEFAULT_DATA_SOURCE);
        String prj = ((in.getProject() != null && !in.getProject().isEmpty())?
                Path.SEPARATOR + in.getProject():
                Path.SEPARATOR + aux.ctx.applicationID);

        StringBuilder sb = new StringBuilder(aux.md.getRoot());
        sb.append(prj + Path.SEPARATOR + MetadataBase.PREDEF_DL_DIR)
                .append(Path.SEPARATOR + ds);

        if (in.getCatalog() != null && !in.getCatalog().isEmpty())
                sb.append(Path.SEPARATOR + in.getCatalog());
                sb
                .append(Path.SEPARATOR + in.getDataSet())
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

