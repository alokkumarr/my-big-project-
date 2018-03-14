package sncr.xdf.adapters.writers;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import sncr.bda.core.file.HFileOperations;

import java.util.List;

public class XDFDataWriter {
    private static final Logger logger = Logger.getLogger(XDFDataWriter.class);
    private final String format;
    private final Integer numberOfFiles;
    private final List<String> keys;

    public XDFDataWriter(String format, Integer numberOfFiles, List<String> keys){
        this.format = format;
        this.numberOfFiles = numberOfFiles;
        this.keys = keys;

        if (keys != null) {
            String m = "Format: " + format + ": ";
            for (String s : keys) m += s + " ";
            logger.debug("Writer: " + m);
        }
    }


    public void writeToTempLoc( Dataset<Row> DS, String tempLocation) throws Exception {
        write(DS, tempLocation, true);
    }

    public JsonElement extractSchema(Dataset<Row> finalResult) {
        JsonParser parser = new JsonParser();
        return parser.parse(finalResult.schema().prettyJson());
    }



    /**
     * The only available call is to write transformed data to temporary location
     * with appropriate partitioning and format
     * @param DS
     * @param tempLocation
     */
    public void write( Dataset<Row> DS, String tempLocation, boolean replace) throws Exception {


        if (replace && HFileOperations.exists(tempLocation))
            HFileOperations.deleteEnt(tempLocation);

        // In HIVE mode we are partitioning by field VALUE only
//        List<String> fields = (List<String>) outds.get(DataSetProperties.Keys.name());
//        Integer numberOfFiles = (Integer) outds.get(DataSetProperties.NumberOfFiles.name());
//        String  format = (String) outds.get(DataSetProperties.Format.name());

        // This can be an empty collection in case FLAT partition
        // is requested or key definitions omited in configuration file
        scala.collection.immutable.Seq<String> scalaList = null;
        if (keys != null)
            scalaList = scala.collection.JavaConversions.asScalaBuffer(keys).toList();

        // Collect number of records
        // (This may require review - may be we need caching)
        long recordCount = DS.count();
        logger.debug("Processing " + recordCount + " records.");

        logger.debug("Requested number of files per partition is " + numberOfFiles + ".");
        if(scalaList != null && scalaList.size() > 0)
            // Setup proper number of output files and write partitions
        switch (format){
            case "parquet":
                DS.coalesce(numberOfFiles).write().partitionBy(scalaList).parquet(tempLocation);
                break;
            case "json" :
                DS.coalesce(numberOfFiles).write().partitionBy(scalaList).json(tempLocation);
                break;
            case "csv" :
                DS.coalesce(numberOfFiles).write().partitionBy(scalaList).csv(tempLocation);
                break;
            default:
                DS.coalesce(numberOfFiles).write().partitionBy(scalaList).parquet(tempLocation);
                break;
        }
        else {
            // Create flat structure/compact files - no key file definitions provided
            switch (format){
                case "parquet":
                    DS.coalesce(numberOfFiles).write().parquet(tempLocation);
                    break;
                case "json" :
                    DS.coalesce(numberOfFiles).write().json(tempLocation);
                    break;
                case "csv" :
                    DS.coalesce(numberOfFiles).write().csv(tempLocation);
                    break;
                default:
                    DS.coalesce(numberOfFiles).write().parquet(tempLocation);
                    break;
            }
        }


    }

    //TODO:: Add sample creation method
    public void createSample(Dataset<Row> DS, String location, boolean replace){




    }

}
