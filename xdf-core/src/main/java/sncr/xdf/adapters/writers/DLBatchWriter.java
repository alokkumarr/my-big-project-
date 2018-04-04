package sncr.xdf.adapters.writers;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import sncr.bda.base.MetadataBase;
import sncr.bda.core.file.HFileOperations;
import sncr.xdf.context.NGContext;

import java.io.IOException;
import java.util.List;

public class DLBatchWriter {
    private static final Logger logger = Logger.getLogger(DLBatchWriter.class);
    protected String format;
    protected Integer numberOfFiles;
    protected List<String> keys;

    protected NGContext ngctx;
    /**
     * Default constructor is to be used everywhere else
     */
    public DLBatchWriter(NGContext ngctx){
        this.ngctx = ngctx;
    }

    /**
     * The constructor is to be used only for old XDF-style component.
     */
    public DLBatchWriter(String format, Integer numberOfFiles, List<String> keys){
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
        // json() was prettyJson()
        return parser.parse(finalResult.schema().json());
    }


    /**
     * The default method to write data into datalake,
     * writes data to specified format in given format and mode.
     * with appropriate partitioning and format
     * @param DS
     * @param tempLocation
     */
    public void write( Dataset<Row> DS, String tempLocation, boolean replace) throws Exception {
        baseWrite(DS, tempLocation, replace, false);
    }

    /**
     * Base function that writes data to a location in append/replace mode and if requested, creates data sample
     * with appropriate partitioning and format.
     * @param DS
     * @param tempLocation
     */
    public void baseWrite( Dataset<Row> DS, String tempLocation, boolean replace, boolean produceSample) throws Exception {

        if (replace && HFileOperations.exists(tempLocation)) {
            logger.debug("Clean up temp location: " + tempLocation);
            HFileOperations.deleteEnt(tempLocation);
        }

        String dataLocation = (produceSample)? tempLocation + Path.SEPARATOR + MetadataBase.PREDEF_DATA_DIR : tempLocation;
        //TODO:: Fix BDA Meta
        String sampleLocation = tempLocation + Path.SEPARATOR + "sample";



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
                DS.coalesce(numberOfFiles).write().partitionBy(scalaList).parquet(dataLocation);
                break;
            case "json" :
                DS.coalesce(numberOfFiles).write().partitionBy(scalaList).json(dataLocation);
                break;
            case "csv" :
                DS.coalesce(numberOfFiles).write().partitionBy(scalaList).csv(dataLocation);
                break;
            default:
                DS.coalesce(numberOfFiles).write().partitionBy(scalaList).parquet(dataLocation);
                break;
        }
        else {
            // Create flat structure/compact files - no key file definitions provided
            switch (format){
                case "parquet":
                    DS.coalesce(numberOfFiles).write().parquet(dataLocation);
                    break;
                case "json" :
                    DS.coalesce(numberOfFiles).write().json(dataLocation);
                    break;
                case "csv" :
                    DS.coalesce(numberOfFiles).write().csv(dataLocation);
                    break;
                default:
                    DS.coalesce(numberOfFiles).write().parquet(dataLocation);
                    break;
            }
        }

        if (produceSample)
        switch (format){
            case "parquet":
                DS.coalesce(1).sample(false, 0.1).write().parquet(sampleLocation);
                break;
            case "json" :
                DS.coalesce(1).sample(false, 0.1).write().json(sampleLocation);
                break;
            case "csv" :
                DS.coalesce(1).sample(false, 0.1).write().csv(sampleLocation);
                break;
            default:
                DS.coalesce(1).sample(false, 0.1).write().parquet(sampleLocation);
                break;
        }
    }

    protected boolean isPathDir( Path p ) throws IOException {
        return  HFileOperations.fs.exists(p) &&
                HFileOperations.fs.isDirectory(p) &&
                HFileOperations.fs.listStatus(p) != null &&
                HFileOperations.fs.listStatus(p).length > 0;
    }

    //TODO:: Fix BDA Meta

    /**
     * The method checks if the dataset sample is presented in temploc (written by baseWrite method.
     * @param source
     * @return
     * @throws IOException
     */
    public boolean doesSampleExist(String source) throws IOException {
        return isPathDir( new Path(source + Path.SEPARATOR + "sample") );
    }

    /**
     * The method determines if dataset is written to temploc/ds/data or to temploc/ds
     * directory. The latter one is for old XDF components and for dataset only (without samples)
     * The first one is to store data with data samples
     * @param source
     * @return
     * @throws IOException
     */
    public String getActualDatasetSourceDir(String source) throws IOException {
        String spdd = source + Path.SEPARATOR + MetadataBase.PREDEF_DATA_DIR;
        if( isPathDir( new Path( spdd ) ) )
            return spdd;
        return source;
    }

    /**
     * The method generates sample directory assuming fact that actual data are written to temploc/ds/data
     * @param moveTask
     * @return
     */
    public String getSampleSourceDir(MoveDataDescriptor moveTask) {
        return moveTask.source + Path.SEPARATOR + "sample";
    }

    /**
     * The method generates sample destination directory.
     * @param moveTask
     * @return
     */
    public String getSampleDestDir(MoveDataDescriptor moveTask) {
        return moveTask.dest.substring(0, moveTask.dest.lastIndexOf( MetadataBase.PREDEF_DATA_DIR)) +  "sample";
    }
}
