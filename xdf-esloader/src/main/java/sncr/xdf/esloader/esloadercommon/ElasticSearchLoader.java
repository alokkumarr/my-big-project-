package sncr.xdf.esloader.esloadercommon;

import com.synchronoss.bda.xdf.datasetutils.filterutils.FilterUtils;
import com.synchronoss.bda.xdf.datasetutils.filterutils.RowFilter;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.storage.StorageLevel;
import org.elasticsearch.spark.sql.api.java.JavaEsSparkSQL;
import sncr.bda.conf.ESLoader;
import sncr.bda.conf.Input;
import sncr.xdf.esloader.XDFTimestampconverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by skbm0001 on 30/1/2018.
 */
public class ElasticSearchLoader {
    private static final Logger logger = Logger.getLogger(ElasticSearchLoader.class);

    private SparkSession sparkSession;
    private ESHttpClient esClient;
    private Map<String, String> esConfig;

    public final static String ES_PARAM_PREFIX = "es.";
    public final static String ES_PARAM_CLUSTER = ES_PARAM_PREFIX + "cluster.name";
    public final static String ES_PARAM_ADMIN_PORT = ES_PARAM_PREFIX + "admin.port";
    public final static String ES_PARAM_NODES = ES_PARAM_PREFIX + "nodes";
    public final static String ES_PARAM_USER = ES_PARAM_PREFIX + "net.http.auth.user";
    public final static String ES_PARAM_PASSWORD = ES_PARAM_PREFIX + "net.http.auth.pass";
    public final static String ES_PARAM_PORT = ES_PARAM_PREFIX + "port";

    private ESLoader esLoader;

    public ElasticSearchLoader(SparkSession session, ESLoader esLoader) throws Exception {

        // General ES Loader configuration and setup
        this.esLoader = esLoader;

        // Extract all the config information
        String esHost = esLoader.getEsNodes();
        int esPort = esLoader.getEsPort();
        String esUser = esLoader.getEsUser();
        String esPass = esLoader.getEsPass();
        String esIndex = esLoader.getDestinationIndexName();
        String esClusterName = esLoader.getEsClusterName();

        ESConfig config = new ESConfig(esHost, esUser, esPass, esPort, esIndex);
        config.setEsClusterName(esClusterName);

        this.esConfig = generateESParamMap(config);


        this.esClient = new ESHttpClient(config);

        this.sparkSession = session;

        this.sparkSession.udf().register("_XdfDateToString", new XDFTimestampconverter(), DataTypes.StringType);
    }

    public static Map<String, String> generateESParamMap(ESConfig config) {
        Map<String, String> configMap = new HashMap<>();

        configMap.put(ES_PARAM_NODES, config.getEsHost());
        configMap.put(ES_PARAM_ADMIN_PORT, String.valueOf(config.getEsPort()));
        configMap.put(ES_PARAM_USER, config.getEsUser());
        configMap.put(ES_PARAM_PASSWORD, config.getEsPassword());
        configMap.put(ES_PARAM_CLUSTER, config.getEsClusterName());

        configMap.put("es.index.auto.create", "false");

        return configMap;
    }

    public Long loadSingleObject(String objectName,
                                 Dataset<Row> originalFrame, String inputDataFormat) throws Exception {
        // Parse index/type name
        ElasticSearchStructureManager essm = new ElasticSearchStructureManager(this.esLoader);
        //long totalRecordCount = 0;

        if(!essm.elasticSearchLoaderConfigured()){
            return -1L;
        }

        // In case of partitioned data, each partition will be loaded separately
        // TBD: load separate partitions into separate ES indexes representing partitions
        //TODO: Verify this
        Map<String, String> locationList = getLocationList(objectName, essm);
//        Map<String, String> locationList = new HashMap<String, String>(){{
//            put(objectName,"myindex/type");
//        }};

        // Check if all indexes exists and try to create them (based on configuration)
        // And add them to exception list for future use
        List<String> newIndices = new ArrayList<>();
        try {
            essm.CreateIfNotExists(esClient, locationList.values().toArray(new String[locationList.values().size()]));
            newIndices.addAll(locationList.values());
        } catch(Exception e){
            // Rollback and delete indices created prior to exception
            essm.DeleteIndex(esClient, newIndices.toArray(new String[newIndices.size()]));
            throw e;
        }

        // All indexes should exist by now - load data
        logger.debug("Extracting index and type");
        for(Map.Entry<String, String> location : locationList.entrySet()) {

            logger.info(objectName + " : Load " + location.getKey() + " into " + location.getValue());
            String strLocation = location.getKey();
            String destinationIdx = location.getValue();

            // Retrieve structure definition
            Map<String, String> fieldDefinitions = new HashMap<String, String>();
            esClient.esIndexStructure(
                    ElasticSearchStructureManager.getIndex(destinationIdx),
                    ElasticSearchStructureManager.getType(destinationIdx),
                    fieldDefinitions);

            logger.debug("Field Definitions = " + fieldDefinitions);

            StructType schema = originalFrame.schema();

            // Create List of Columns for ES Index
            // We have to find intersection of fields in the index and fields in actual records
            // Allowing to have data object "wider" (in columns) or "narrower" than ES index
            List<Column> lst = new ArrayList<>();
            final String defaultEsDtFormat = "yyyy-MM-dd'T'HH:mm:ss";
            fieldDefinitions.forEach((k,v) -> {
                if(schema.getFieldIndex(k).nonEmpty()) {
                    DataType t = schema.apply(k).dataType();
                    // Try to analyze structure of the fields
                    // Unfortunately I can't find any way to inspect structure other than compare it with string representation
                    if(t.simpleString().equals("struct<_xdfDate:string,_xdfTime:string>")) {
                        // XDF timestamp
                        String esDtFormat = v.toLowerCase().startsWith("date^")? v.split("\\^")[1] : defaultEsDtFormat;
                        logger.debug("Converting " + k + ":_xdfDate/Time format to " + esDtFormat);
                        lst.add(org.apache.spark.sql.functions.expr(
                                "_XdfDateToString(" + k + "._xdfDate," + k + "._xdfTime, \"" + esDtFormat + "\")").as(k));

                    } else if(t.simpleString().equals("struct<_xdfDate:string>")) {
                        // XDF date
                        String esDtFormat = v.toLowerCase().startsWith("date^")? v.split("\\^")[1] : defaultEsDtFormat;
                        logger.debug("Converting " + k + ":_xdfDate format to " + esDtFormat);
                        lst.add(org.apache.spark.sql.functions.expr(
                                "_XdfDateToString(" + k + "._xdfDate," +  "\"000000\", \"" + esDtFormat + "\")").as(k));

                    } else if(t.simpleString().equals("_xdfTime:string>")) {
                        // XDF Time - should not happen
                        String esDtFormat = v.toLowerCase().startsWith("date^")? v.split("\\^")[1] : defaultEsDtFormat;
                        logger.debug("Converting " + k + ":_xdfTime format to " + esDtFormat);
                        lst.add(org.apache.spark.sql.functions.expr(
                                "_XdfDateToString(\"10000101\"," + k + "._xdfTime, \"" + esDtFormat + "\")").as(k));
                    } else {
                        // Simple field or struct
                        lst.add(new Column(k));
                    }
                }
            });

            logger.debug("Columns list = " + lst);

            String filterString = esLoader.getFilterString();
            Dataset<Row> finalFrame;
            if(filterString == null || filterString.isEmpty())
                finalFrame = originalFrame.select(scala.collection.JavaConversions.asScalaBuffer(lst).readOnly())
                        .persist(StorageLevel.MEMORY_AND_DISK_SER());
            else {
                logger.info(objectName + " : Applying filter string : " + filterString);
                finalFrame = filterData(originalFrame, filterString).select(scala.collection.JavaConversions.asScalaBuffer(lst).readOnly())
                        .persist(StorageLevel.MEMORY_AND_DISK_SER());
            }

            logger.debug("Data = " + finalFrame + ", Destination Index = " + destinationIdx + ", Config = " + esConfig);

            JavaEsSparkSQL.saveToEs(finalFrame, destinationIdx, esConfig);

            logger.debug("Loading " + location.getValue() + " complete");
//            DriverUtils.addDataObject(outputJson, objectName, 0L, 0L, destinationIdx);
        } //<-- for(Map.Entry<String, String> location : locationList.entrySet()) {

        // We are no longer counting records
        // For performance reason
        //totalRecordCount = 0;
        // Load complete
        // Manipulate with aliases if configured

        logger.info("Processing aliases");
        logger.debug("Indices = " + newIndices);

        essm.ProcessAliases(esClient, newIndices.toArray(new String[newIndices.size()]));
        return 0L;
    }

    public Dataset<Row> filterData(Dataset<Row> dataSet, String condition) {
        if (condition == null || condition.length() == 0) {
            return  dataSet;
        }

        RowFilter filter = new RowFilter(condition);

        return FilterUtils.applyFilter(filter, dataSet);
    }

    private Dataset<Row> loadSourceData(String source, Input.Format format) throws Exception {

        logger.info("Loading <" + format + "> data from " + source);
        switch(format){
            case PARQUET:
                return sparkSession.read().parquet(source);
            case JSON:
                return sparkSession.read().json(source);

            default:
                throw new Exception("Source type <" + format + "> currently not supported.");
        }
    }

    private static Map<String, String> getLocationList(String objectName,
                                                       ElasticSearchStructureManager essm)  throws Exception {
        Map<String, String> locations = new HashMap<String, String>();
//        for(razorsight.schema.output.DataObject  dataObject : inputJson.getDataObjects()){
//            if(dataObject.getName().equals(objectName)){

                String partition = null;
                locations.put(objectName, essm.getParsedIndexNameAndType(partition));

                //if(dataObject.getDetails() != null
                //        && dataObject.getDetails().getPartitions() != null
                //        && dataObject.getDetails().getPartitions().size() > 0) {
                //    // process object partitions by creating one location per partition
                //    // TBD
                //    // Also we may want to implement data Lake partition mapping to ES partition
                //} else {
                //
                // }
//                break;
//            }
//        }
        return locations;
    }
}