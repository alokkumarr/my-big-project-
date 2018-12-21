package sncr.xdf.esloader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import sncr.bda.conf.ComponentConfiguration;
import sncr.bda.conf.ESLoader;
import sncr.bda.core.file.HFileOperations;
import sncr.bda.datasets.conf.DataSetProperties;
import sncr.xdf.component.WithDataSetService;
import sncr.xdf.component.WithSparkContext;
import sncr.xdf.component.Component;
import sncr.xdf.esloader.esloadercommon.ElasticSearchLoader;
import sncr.xdf.exceptions.FatalXDFException;
import sncr.xdf.exceptions.XDFException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by skbm0001 on 29/1/2018.
 */
public class ESLoaderComponent extends Component implements WithSparkContext, WithDataSetService {
    private static final Logger logger = Logger.getLogger(ESLoaderComponent.class);

    //TODO: Remove this
    public static String ESLOADER_DATASET;

    private Map<String, Object> esDataset;
    private String dataSetName;
    private String inputDataFormat;

    private ESLoader esLoaderConfig;

    ESLoaderComponent() {
        super.componentName = "esloader";
    }

    public static void main(String[] args) {
        ESLoaderComponent component = new ESLoaderComponent();

        try {
            if (component.collectCommandLineParameters(args) == 0) {
                int r = component.run();
                System.exit(r);
            }
        } catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    protected int execute() {
        int retVal = 0;
        try {
            esLoaderConfig = this.ctx.componentConfiguration.getEsLoader();
            if (this.inputDataSets != null && !this.inputDataSets.isEmpty()) {
                ESLOADER_DATASET = this.inputDataSets.keySet().iterator().next();
            }

            esDataset = this.inputDataSets.get(ESLOADER_DATASET);
            logger.debug("ES Dataset = " + esDataset);

            dataSetName = (String)esDataset.get(DataSetProperties.Name.name());
            inputDataFormat = (String)esDataset.get(DataSetProperties.Format.name());

            Map<String, Dataset> dataSetMap = createDatasetMap();

            logger.debug("Input dataset map = " + dataSetMap);

            Dataset<Row> inputDataset = dataSetMap.get(this.dataSetName);


            ElasticSearchLoader loader = new ElasticSearchLoader(this.ctx.sparkSession, esLoaderConfig);

            JsonElement inputDsConfig = dsaux.dl.getDSStore().read(ctx.applicationID + "::" + ESLOADER_DATASET);
            logger.debug("Input DS config = " + inputDsConfig);

            registerDataset();
            int rc = loader.loadSingleObject(this.dataSetName, inputDataset, inputDataFormat);

//            if (rc == 0) {
//                rc = registerDataset();
//            }

            return rc;
        } catch (Exception ex) {
            logger.error(ex);
            logger.debug(ExceptionUtils.getStackTrace(ex));
            retVal = -1;
        }

        return retVal;
    }

    private int registerDataset () throws Exception {
        int result = 0;

        String datasetId = ctx.applicationID + "::" + ESLOADER_DATASET;

        String esDatasetId = datasetId + "::esdata";

        // Fetch the existing ES dataset
        JsonElement inputDsConfigElement = dsaux.dl.getDSStore()
            .read(esDatasetId);

        if (inputDsConfigElement == null || inputDsConfigElement.equals("")) {
            logger.info("ES dataset doesn't exist. Creating it with input dataset");
            inputDsConfigElement = dsaux.dl.getDSStore()
                .read(datasetId);
        }

        if (inputDsConfigElement == null) {
            logger.error("Input dataset is not registered");

            return -1;
        }

        logger.debug("Input DS config = " + inputDsConfigElement);

        JsonObject inputDsConfigObject = inputDsConfigElement.getAsJsonObject();
        inputDsConfigObject.remove(DataSetProperties.UserData.toString());
        inputDsConfigObject.remove("userData");
        inputDsConfigObject.remove(DataSetProperties.CreatedTime.toString());
        inputDsConfigObject.remove(DataSetProperties.ModifiedTime.toString());

        String indexType = esLoaderConfig.getDestinationIndexName();

        String index = indexType.substring(0, indexType.indexOf("/"));
        String type = indexType.substring(indexType.indexOf("/") + 1);

        String mappingFileLocation = esLoaderConfig.getIndexMappingfile();

        String mappingInfo = HFileOperations.readFile(mappingFileLocation);

        logger.debug("Mapping info = " + mappingInfo);

        JsonObject mappingObject = new JsonParser().parse(mappingInfo).getAsJsonObject();

        JsonObject esFields = mappingObject.getAsJsonObject("mappings")
            .getAsJsonObject(type).getAsJsonObject("properties");

        JsonObject schema = generateSchema(esFields);
        inputDsConfigObject.add(DataSetProperties.Schema.toString(), schema);


        JsonObject system = inputDsConfigObject.get(DataSetProperties.System.toString())
            .getAsJsonObject();

        inputDsConfigObject.add(DataSetProperties.System.toString(), updateSystemObject(system, index, type));

        // Update ID Field
        inputDsConfigObject.addProperty("_id", esDatasetId);

        logger.debug("Updated dataset = " + inputDsConfigObject);

        logger.info("Registering " + esDatasetId + " to metadata");

        dsaux.dl.getDSStore().create(esDatasetId, inputDsConfigElement);

        return result;
    }

    private JsonObject updateSystemObject(JsonObject system, String index, String type) {
        if (system == null) {
            system = new JsonObject();
        }

        system.addProperty(DataSetProperties.PhysicalLocation.toString(), index);
        system.addProperty(DataSetProperties.Name.toString(), index);
        system.addProperty("type", "ESIndex");

        return system;
    }

    private JsonObject generateSchema (JsonObject esFields) {
        JsonObject schema = new JsonObject();

        JsonArray fields = transformFields(esFields);

        schema.add("fields", fields);

        return schema;
    }

    private JsonArray transformFields (JsonObject esFields) {
        JsonArray schemaFields = new JsonArray();

        esFields.entrySet().forEach(entry -> {
            JsonObject field = new JsonObject();

            field.addProperty("name", entry.getKey());

            String fieldType = entry.getValue().getAsJsonObject().get("type").getAsString();
            field.addProperty("type", fieldType);

            JsonElement fieldsObject = entry.getValue().getAsJsonObject().get("fields");
            if (fieldsObject != null) {
                field.add("fields", fieldsObject.getAsJsonObject());
            }

            JsonElement fieldFormat = entry.getValue().getAsJsonObject().get("format");
            if (fieldFormat != null) {
                field.addProperty("format", fieldFormat.getAsString());
            }

            schemaFields.add(field);
        });

        return schemaFields;
    }

    @Override
    protected int archive() {
        return 0;
    }

    @Override
    protected String mkConfString() {
        return null;
    }

    protected ComponentConfiguration validateConfig(String config) throws Exception {
        return ESLoaderComponent.analyzeAndValidate(config);
    }

    public static ComponentConfiguration analyzeAndValidate(String cfgAsStr) throws Exception {

        ComponentConfiguration compConf = Component.analyzeAndValidate(cfgAsStr);
        ESLoader esLoaderConfig = compConf.getEsLoader();
        if (esLoaderConfig == null)
            throw new XDFException(XDFException.ErrorCodes.NoComponentDescriptor, "es-loader");

        if (esLoaderConfig.getEsNodes() == null || esLoaderConfig.getEsNodes().isEmpty()) {
            throw new XDFException(XDFException.ErrorCodes.ConfigError, "Incorrect configuration: ElasticSearch Nodes configuration missing.");
        }
        if (esLoaderConfig.getEsPort() == 0) {
            throw new XDFException(XDFException.ErrorCodes.ConfigError, "Incorrect configuration: ElasticSearch Port configuration missing.");
        }
        if (esLoaderConfig.getDestinationIndexName() == null || esLoaderConfig.getDestinationIndexName().isEmpty()) {
            throw new XDFException(XDFException.ErrorCodes.ConfigError, "Incorrect configuration: ElasticSearch Destination Index Name missing.");
        }
        if (esLoaderConfig.getEsClusterName() == null || esLoaderConfig.getEsClusterName().isEmpty()) {
            throw new XDFException(XDFException.ErrorCodes.ConfigError, "Incorrect configuration: ElasticSearch clustername configuration missing.");
        }

        return compConf;
    }

    private Map<String, Dataset> createDatasetMap() {
        Map<String, Dataset> dataSetmap = new HashMap();

        for ( Map.Entry<String, Map<String, Object>> entry : this.inputDataSets.entrySet()) {
            Map<String, Object> desc = entry.getValue();
            String loc = (String)desc.get(DataSetProperties.PhysicalLocation.name());
            String format = (String)desc.get(DataSetProperties.Format.name());
            logger.debug("Physical location = + " + loc + ". Format = " + format);
            Dataset ds = null;
            switch (format.toLowerCase()) {
                case "json":
                    ds = ctx.sparkSession.read().json(loc); break;
                case "parquet":
                    ds = ctx.sparkSession.read().parquet(loc); break;
                default:
                    error = "Unsupported data format: " + format;
                    logger.error(error);
                    throw new FatalXDFException(XDFException.ErrorCodes.UnsupportedDataFormat, -1);
            }
            dataSetmap.put(entry.getKey(), ds);
        }

        return dataSetmap;
    }
}
