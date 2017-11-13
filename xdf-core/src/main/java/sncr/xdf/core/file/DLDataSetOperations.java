package sncr.xdf.core.file;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.log4j.Logger;
import scala.Tuple4;

import java.io.File;
import java.io.IOException;

/**
 * Created by srya0001 on 10/12/2017.
 */
public class DLDataSetOperations {

    public static final String MODE_REPLACE = "replace";
    public static final String MODE_APPEND = "append";
    private static final Logger logger = Logger.getLogger(DLDataSetOperations.class);

    public final static PathFilter FILEDIR_FILTER = file ->
            ((!file.getName().startsWith("."))
                    && (!file.getName().startsWith("_common_"))
                    && (!file.getName().startsWith("_metadata"))
                    && (!file.getName().startsWith("_SUCCESS")));
    public static final String FORMAT_PARQUET = "parquet";
    public static final String FORMAT_JSON = "json";


    private static HDirOperations.PARTITION_STRUCTURE checkLevel(FileSystem fileSystem, String glob) {
        HDirOperations.PARTITION_STRUCTURE partitionStructure = HDirOperations.PARTITION_STRUCTURE.HIVE;
        try {
            FileStatus[] it = fileSystem.globStatus(new Path(glob), FILEDIR_FILTER);
            if (it.length == 0) {
                // Empty data object - nothing to partition
                return HDirOperations.PARTITION_STRUCTURE.EMPTY;
            }
            boolean hasDirectories = false;
            boolean hasFiles = false;
            // Check if object directory has only files (non-partitioned, only directories (partitioned)
            // or both - ERRROR

            String firstDirName = "";
            int charPosition1 = -1;
            String firstFieldName = "";

            for (FileStatus fileStatus : it) {
                if (fileStatus.isDirectory()) {
                    hasDirectories = true;

                    if(firstDirName.isEmpty()){
                        // first directory in iterations initialize
                        firstDirName = fileStatus.getPath().getName();
                        charPosition1 = firstDirName.indexOf('=');
                        if(charPosition1 > 0) firstFieldName = firstDirName.substring(0, charPosition1);
                    }

                    if(charPosition1 > 0 && partitionStructure == HDirOperations.PARTITION_STRUCTURE.HIVE) {
                        String dirName = fileStatus.getPath().getName();
                        int charPosition = dirName.indexOf('=');
                        if( charPosition != charPosition1){
                            partitionStructure = HDirOperations.PARTITION_STRUCTURE.DRILL;
                        } else {
                            String fieldName = dirName.substring(0, charPosition1);
                            if(!fieldName.equals(firstFieldName)){
                                partitionStructure = HDirOperations.PARTITION_STRUCTURE.DRILL;
                            }
                        }
                    } else {
                        // Otherwise it is Drill, since we have at least one non-HIVE directory name
                        partitionStructure = HDirOperations.PARTITION_STRUCTURE.DRILL;
                    }

                    //System.out.println("D:" + fileStatus.getPath());
                } else {
                    hasFiles = true;
                    //System.out.println("F:" + fileStatus.getPath());
                }
                if (hasDirectories && hasFiles) {
                    // Invalid data object
                    // It has both partitions (directories) and files on this level
                    break;
                }
            }

            if (hasDirectories && hasFiles) {
                // ERROR in data object directory structure
                System.err.println("Invalid object directory structure - having directories and files on the same level");
                return HDirOperations.PARTITION_STRUCTURE.ERROR;
            }
            if (hasFiles) {
                // This is flat, non-partitioned data object
                return HDirOperations.PARTITION_STRUCTURE.FLAT;
            }
            if (hasDirectories) {
                // This is partitioned data object
                // Check directory names to define if it is HVIE or DRILL
                // HIVE directories should have only 1 '=' character
                // and start with the same prefix representing same field name
                return partitionStructure;

            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return HDirOperations.PARTITION_STRUCTURE.ERROR;
    }


    // Get partitioning info
    //  Glob - to use with Spark
    //  Full glob - to enumerate all files and use with HDFS API
    //  Depth
    //  Partition structure type (DRILL, HIVE, FLAT)
    public static Tuple4<String, String, Integer, HDirOperations.PARTITION_STRUCTURE>
    getPartitioningInfo(String location) throws Exception {
        FileSystem fs;
        try {
            Path path = new Path(location);
            Configuration conf = new Configuration();
            fs = FileSystem.get(path.toUri(), conf);

            int depth = 0;
            String glob = location;
            HDirOperations.PARTITION_STRUCTURE overallStructure = HDirOperations.PARTITION_STRUCTURE.HIVE;
            HDirOperations.PARTITION_STRUCTURE i = HDirOperations.PARTITION_STRUCTURE.HIVE;

            while ((i == HDirOperations.PARTITION_STRUCTURE.DRILL || (i == HDirOperations.PARTITION_STRUCTURE.HIVE))) {
                glob += File.separatorChar + "*";
                i = checkLevel(fs, glob);
                switch (i) {
                    case ERROR:
                        overallStructure = HDirOperations.PARTITION_STRUCTURE.ERROR;
                        break;
                    case DRILL:
                        overallStructure = overallStructure != HDirOperations.PARTITION_STRUCTURE.ERROR
                                ? HDirOperations.PARTITION_STRUCTURE.DRILL : overallStructure;
                        break;
                }
                //System.out.println(glob + " : " + i);
                depth++;
            }

            Tuple4<String, String, Integer, HDirOperations.PARTITION_STRUCTURE> retval = null;
            if (i == HDirOperations.PARTITION_STRUCTURE.FLAT && depth == 1) {
                overallStructure = HDirOperations.PARTITION_STRUCTURE.FLAT;
                retval = new Tuple4<>(location, glob, depth, HDirOperations.PARTITION_STRUCTURE.FLAT);
            }
            if (overallStructure == HDirOperations.PARTITION_STRUCTURE.DRILL) {
                retval = new Tuple4<>(glob, glob, depth, overallStructure);
            }
            if (overallStructure == HDirOperations.PARTITION_STRUCTURE.HIVE) {
                retval = new Tuple4<>(location, glob, depth, overallStructure);
            }
            return retval;
        }
        catch(IOException e)
        {
            //TODO::Remove before release
            e.printStackTrace();
            logger.error("IO Exception at attempt to create dir: ", e);
            throw new Exception("Cannot create directory provided PhysicalLocation:" + e);
        }
    }


}
