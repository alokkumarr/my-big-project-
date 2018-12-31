package sncr.xdf.esloader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import scala.Tuple2;
import sncr.bda.conf.Alias;
import sncr.bda.conf.ComponentConfiguration;
import sncr.bda.conf.ESLoader;
import sncr.bda.core.file.HFileOperations;
import sncr.bda.datasets.conf.DataSetProperties;
import sncr.xdf.component.WithDataSetService;
import sncr.xdf.component.WithSparkContext;
import sncr.xdf.component.Component;
import sncr.xdf.esloader.esloadercommon.ESConfig;
import sncr.xdf.esloader.esloadercommon.ESHttpClient;
import sncr.xdf.esloader.esloadercommon.ElasticSearchLoader;
import sncr.xdf.exceptions.FatalXDFException;
import sncr.xdf.exceptions.XDFException;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

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

            JsonElement inputDsConfig =
                dsaux.dl.getDSStore().read(ctx.applicationID + "::" + ESLOADER_DATASET);
            logger.debug("Input DS config = " + inputDsConfig);


            Tuple2<Integer, Map<String, String>> ret =
                loader.loadSingleObject(this.dataSetName, inputDataset, inputDataFormat);

            retVal = ret._1;

            if (retVal == 0) {
                Map<String, String> indexMap = ret._2;
                logger.debug("Index Map list = " + indexMap);

                String indexType = indexMap.get(this.dataSetName);
                logger.debug("Final indexType = " + indexType);

                ESHttpClient esHttpClient = loader.getHttpClient();

                retVal = registerDataset(indexType, esHttpClient);
            }

            return retVal;
        } catch (Exception ex) {
            logger.error(ex);
            logger.debug(ExceptionUtils.getStackTrace(ex));
            retVal = -1;
        }

        return retVal;
    }

    /**
     *
     * @param indexType Combintion of index and type value in the form of index/type
     * @param esHttpClient HTTP client to communicate with ElasticSearch
     * @return  0 - Success
     *         -1 - Failure
     * @throws Exception In case of failures during metadata retrival
     */
    private int registerDataset (String indexType, ESHttpClient esHttpClient) throws Exception {
        String datasetId = ctx.applicationID + "::" + ESLOADER_DATASET;

        // Append '_esdata' to the dataset id to identify this as ES dataset
        String esDatasetId = datasetId + "_esdata";

        // Fetch the existing ES dataset
        JsonElement esDatasetElement = dsaux.dl.getDSStore()
            .read(esDatasetId);

        JsonObject esDatasetObject = null;

        if (esDatasetElement == null) {
            // Create ES dataset using input dataset

            logger.info("ES dataset doesn't exist. Creating it with input dataset");
            esDatasetElement = dsaux.dl.getDSStore()
                .read(datasetId);

            esDatasetObject = esDatasetElement.getAsJsonObject();

            long currentTime = Instant.now().toEpochMilli();

            esDatasetObject.addProperty(DataSetProperties.CreatedTime.toString(), currentTime);
            esDatasetObject.addProperty(DataSetProperties.ModifiedTime.toString(), currentTime);
        } else {
            esDatasetObject = esDatasetElement.getAsJsonObject();

            // Update modified time
            long currentTime = Instant.now().toEpochMilli();
            esDatasetObject.addProperty(DataSetProperties.ModifiedTime.toString(), currentTime);
        }

        if (esDatasetObject == null) {
            logger.error("Unable to initialize ES dataset");

            return -1;
        }

        logger.debug("Input DS config = " + esDatasetElement);

        // Removed unwanted fields
        esDatasetObject.remove(DataSetProperties.UserData.toString());
        esDatasetObject.remove("userData");
        esDatasetObject.remove("asInput");
        esDatasetObject.remove(DataSetProperties.Project.toString());
        esDatasetObject.remove(DataSetProperties.Transformations.toString());

        // Add User details
        JsonObject userData = generateUserData();
        esDatasetObject.add(DataSetProperties.UserData.toString(), userData);


        List<Alias> aliases = esLoaderConfig.getAliases();

        String index = indexType.substring(0, indexType.indexOf("/"));
        String type = indexType.substring(indexType.indexOf("/") + 1);

        esDatasetObject.addProperty("storageType", "ES");

        String mappingFileLocation = esLoaderConfig.getIndexMappingfile();

        String mappingInfo = HFileOperations.readFile(mappingFileLocation);

        logger.debug("Mapping info = " + mappingInfo);

        JsonObject mappingObject = new JsonParser().parse(mappingInfo).getAsJsonObject();

        JsonObject esFields = mappingObject.getAsJsonObject("mappings")
            .getAsJsonObject(type).getAsJsonObject("properties");

        JsonObject schema = generateSchema(esFields);
        esDatasetObject.add(DataSetProperties.Schema.toString(), schema);


        JsonObject system = esDatasetObject.get(DataSetProperties.System.toString())
            .getAsJsonObject();

        system = updateSystemObject(system, index, type, aliases);

        esDatasetObject.add(DataSetProperties.System.toString(), system);

        String updatedIndex = system.get(DataSetProperties.PhysicalLocation.toString()).getAsString();

        //Get index/alias info and extract record count
        long recordCount = extractRecordCount(esHttpClient, updatedIndex);

        esDatasetObject.addProperty(DataSetProperties.RecordCount.toString(), recordCount);


        // Update ID Field
        esDatasetObject.addProperty("_id", esDatasetId);

        logger.debug("Updated dataset = " + esDatasetObject);

        logger.info("Registering " + esDatasetId + " to metadata");

        dsaux.dl.getDSStore().create(esDatasetId, esDatasetElement);

        return 0;
    }

    /**
     *
     * @return Retuns an object containing user information
     */
    private JsonObject generateUserData() {
        JsonObject userObject = new JsonObject();

        userObject.addProperty(DataSetProperties.createdBy.toString(),
            "pipelineadmin@synchronoss.com");

        return userObject;
    }

    /**
     *
     * @param esHttpClient HTTP client used to communicate with ElasticSearch
     * @param esIndex - ES index for which record count has to be retrieved
     * @return Record count
     */
    private long extractRecordCount (ESHttpClient esHttpClient,
                                     String esIndex) {
        long recordCount = 0;

        recordCount = esHttpClient.getRecordCount(esIndex);

        return recordCount;
    }

    /**
     *
     * @param system Original <code>system</code> object
     * @param index ES Index
     * @param type ES type
     * @param aliases List of alias objects
     *
     * @return Updated <code>system</code> object
     */
    private JsonObject updateSystemObject(JsonObject system, String index, String type,
                                          List<Alias> aliases) {
        if (system == null) {
            system = new JsonObject();
        }

        // Get alias information
        String alias = extractAlias(aliases);

        if (alias != null) {
            system.addProperty(DataSetProperties.PhysicalLocation.toString(), alias);
            system.addProperty(DataSetProperties.Name.toString(), alias);
//            system.addProperty("alias", alias);
        } else {
            system.addProperty(DataSetProperties.PhysicalLocation.toString(), index);
            system.addProperty(DataSetProperties.Name.toString(), index);
        }

        // Add index type information
        system.remove("type");
        system.addProperty("esIndexType", type);

        return system;
    }

    /**
     *
     * @param aliases List of alias objects
     * @return First alias in the list which is in append mode.
     *         <code>null</code> in case nothing matched
     */
    private String extractAlias(List<Alias> aliases) {
        String alias = null;

        if (aliases != null && aliases.size() != 0) {
            List <Alias> appendAlias = aliases.stream().filter(aliasObject -> {
                return aliasObject.getMode() == Alias.Mode.APPEND;
            }).collect(toList());

            if (appendAlias.size() != 0) {
                alias = appendAlias.get(0).getAliasName();
            }
        }

        return alias;
    }

    /**
     * Used to generate the schema structure for ES dataset
     *
     * @param esFields <code>properties</code> object from ES mapping file
     *
     * @return <code>schema</code> object
     */
    private JsonObject generateSchema (JsonObject esFields) {
        JsonObject schema = new JsonObject();

        JsonArray fields = transformFields(esFields);

        schema.add("fields", fields);

        return schema;
    }

    /**
     * Transforms the <code>properties</code> object
     * from the mapping file into the required ES dataset structure.<br />
     *
     * E.g.: If the <code>properties</code> object has fields as shown below:
     *       <pre>
     *         {
     *             "properties":{
     *                 "NAME":{
     *                     "type":"text",
     *                     "fields":{
     *                         "keyword":{
     *                             "type":"keyword"
     *                         }
     *                     }
     *                 },
     *                 "NTDID":{
     *                     "type":"integer"
     *                 }
     *             }
     *         }
     *       </pre>,
     *
     *       it will be transformed as below:
     *       <pre>
     *         "fields":[
     *          {
     *             "name":"NAME",
     *             "type":"text",
     *             "isKeyword":true
     *          },
     *          {
     *             "name":"NTDID",
     *             "type":"integer"
     *          }
     *       ]
     *       </pre>
     *
     * @param esFields <code>properties</code> object from ES mapping file
     *
     * @return Transformed fields
     */
    private JsonArray transformFields (JsonObject esFields) {
        JsonArray schemaFields = new JsonArray();

        esFields.entrySet().forEach(entry -> {
            JsonObject field = new JsonObject();

            field.addProperty("name", entry.getKey());

            String fieldType = entry.getValue().getAsJsonObject().get("type").getAsString();
            field.addProperty("type", fieldType);

            JsonElement fieldsElement = entry.getValue().getAsJsonObject().get("fields");
            if (fieldsElement != null) {
                JsonObject fieldsObject = fieldsElement.getAsJsonObject();

                if (fieldsObject.has("keyword")) {
                    field.addProperty("isKeyword", true);
                }
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
