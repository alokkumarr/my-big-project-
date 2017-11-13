package sncr.xdf.core.file;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.parquet.hadoop.Footer;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.metadata.BlockMetaData;
import scala.Tuple4;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.List;


/**
 * Created by alexey.sorokin on 10/14/2015.
 */
public class HDirOperations {

    public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public final static String SPARK_PARM_PREFIX = "spark.";
    public final static String SPARK_MASTER = SPARK_PARM_PREFIX + "master";
    public final static String ES_PARAM_PREFIX = "es.";

    public final static String PARQUET_EXT = "parquet";
    public static final String JSON_EXT = "json";
    public static final String TEXT_EXT = "txt";
    public final static String DEFAULT_FS = "maprfs:///";

    public static final PathFilter FILE_FILTER = file -> (true);

    public final static String ES_PARAM_CLUSTER = ES_PARAM_PREFIX + "cluster.name";
    public final static String ES_PARAM_ADMIN_PORT = ES_PARAM_PREFIX + "admin.port";
    public final static String ES_PARAM_NODES = ES_PARAM_PREFIX + "nodes";
    public final static String ES_PARAM_USER = ES_PARAM_PREFIX + "net.http.auth.user";
    public final static String ES_PARAM_PASSWORD = ES_PARAM_PREFIX + "net.http.auth.pass";
    public final static String ES_PARAM_PORT = ES_PARAM_PREFIX + "port";

    public static final String JAR_EXT = ".jar";


    public static final String CLI_NONE = ".jar";

    public final static PathFilter JARFILE_FILTER = file ->
            (file.getName().endsWith("." + JAR_EXT) && (!file.getName().startsWith(".")));


    public static boolean isEmptyParquetFile(FileStatus inputFileStatus, Configuration conf ) throws Exception {
        boolean emptyFile = false;
        List<Footer> footers = ParquetFileReader.readFooters(conf, inputFileStatus, false);
        if(footers.size() < 2) {
            for(Footer f : footers) {
                List<BlockMetaData> lb = f.getParquetMetadata().getBlocks();
		if(lb.size() == 0) {
			emptyFile = true;
		} else if (lb.size() == 1) {
                    BlockMetaData bmd = lb.get(0);
                    if (bmd.getRowCount() == 0) {
                        emptyFile = true;
                    }
                } 				
            }
        }
        return emptyFile;
    }


    public static String readFile(String fileName) throws FileNotFoundException {
        String data = "";
        try {
            InputStream stream;
            Path path = new Path(fileName);
            Configuration conf = new Configuration();
            FileSystem fs = FileSystem.get(path.toUri(), conf);
            CompressionCodecFactory factory = new CompressionCodecFactory(conf);
            CompressionCodec codec = factory.getCodec(path);

            if(codec != null){
                stream = codec.createInputStream(fs.open(path));
            } else {
                stream = fs.open(path);
            }
            data = new String(IOUtils.toByteArray(stream));

            stream.close();
            fs.close();

        } catch (Exception e) {
                throw new FileNotFoundException("File not found on the provided location :" + e);
            }

        return data;
    }

    public enum PARTITION_STRUCTURE {
        FLAT,
        HIVE,
        DRILL,
        EMPTY,
        ERROR
    }

    public final static PathFilter FILEDIR_FILTER = file ->
            ((!file.getName().startsWith("."))
                    && (!file.getName().startsWith("_common_"))
                    && (!file.getName().startsWith("_metadata"))
                    && (!file.getName().startsWith("_SUCCESS")));

    private static PARTITION_STRUCTURE checkLevel(FileSystem fileSystem, String glob) {
        PARTITION_STRUCTURE partitionStructure = PARTITION_STRUCTURE.HIVE;
        try {
            FileStatus[] it = fileSystem.globStatus(new Path(glob), FILEDIR_FILTER);
            if (it.length == 0) {
                // Empty data object - nothing to partition
                return PARTITION_STRUCTURE.EMPTY;
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

                    if(charPosition1 > 0 && partitionStructure == PARTITION_STRUCTURE.HIVE) {
                        String dirName = fileStatus.getPath().getName();
                        int charPosition = dirName.indexOf('=');
                        if( charPosition != charPosition1){
                            partitionStructure = PARTITION_STRUCTURE.DRILL;
                        } else {
                            String fieldName = dirName.substring(0, charPosition1);
                            if(!fieldName.equals(firstFieldName)){
                                partitionStructure = PARTITION_STRUCTURE.DRILL;
                            }
                        }
                    } else {
                        // Otherwise it is Drill, since we have at least one non-HIVE directory name
                        partitionStructure = PARTITION_STRUCTURE.DRILL;
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
                return PARTITION_STRUCTURE.ERROR;
            }
            if (hasFiles) {
                // This is flat, non-partitioned data object
                return PARTITION_STRUCTURE.FLAT;
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
        return PARTITION_STRUCTURE.ERROR;
    }
    // Get partitioning info
    //  Glob - to use with Spark
    //  Full glob - to enumerate all files and use with HDFS API
    //  Depth
    //  Partition structure type (DRILL, HIVE, FLAT)
    public static Tuple4<String, String, Integer, PARTITION_STRUCTURE> getPartitioningInfo(FileSystem fileSystem, String location){
        int depth = 0;
        String glob = location;
        PARTITION_STRUCTURE overallStructure = PARTITION_STRUCTURE.HIVE;
        PARTITION_STRUCTURE i = PARTITION_STRUCTURE.HIVE;

        while((i == PARTITION_STRUCTURE.DRILL || (i == PARTITION_STRUCTURE.HIVE))){
            glob += File.separatorChar + "*";
            i = checkLevel(fileSystem, glob);
            switch(i){
                case ERROR:
                    overallStructure = PARTITION_STRUCTURE.ERROR;
                    break;
                case DRILL:
                    overallStructure = overallStructure != PARTITION_STRUCTURE.ERROR
                            ? PARTITION_STRUCTURE.DRILL : overallStructure;
                    break;
            }
            depth++;
        }

        Tuple4<String, String, Integer, PARTITION_STRUCTURE> retval = null;
        if(i == PARTITION_STRUCTURE.FLAT && depth == 1){
            overallStructure = PARTITION_STRUCTURE.FLAT;
            retval = new Tuple4<>(location, glob, depth, PARTITION_STRUCTURE.FLAT);
        }
        if(overallStructure == PARTITION_STRUCTURE.DRILL ) {
            retval = new Tuple4<>(glob, glob, depth, overallStructure);
        }
        if(overallStructure == PARTITION_STRUCTURE.HIVE ) {
            retval = new Tuple4<>(location, glob, depth, overallStructure);
        }
        return retval;
    }

    public static int cleanupDataDirectory(String location)
            throws Exception {

        org.apache.hadoop.conf.Configuration fsConf =  new org.apache.hadoop.conf.Configuration();
        FileSystem fs = FileSystem.get(fsConf );

        int retval = -1;
        // remove empty files
        Path loc = new Path(location);
        try {
            FileStatus[] files = fs.listStatus(loc, FILE_FILTER);
            for (FileStatus s : files) {
                if(s.isDirectory()){
                    // Blindly remove any directory
                    fs.delete(s.getPath(), true);

                } else {
                    if(s.getPath().getName().endsWith(PARQUET_EXT)){
                        if(HDirOperations.isEmptyParquetFile(s, fsConf)){
                            fs.delete(s.getPath(), false);
                        }
                    } else if(s.getPath().getName().startsWith("_")){
                        fs.delete(s.getPath(), false);
                    } else if(s.getLen() == 0 && !s.isDirectory()) {
                        fs.delete(s.getPath(), false);
                    }
                }
            }
            retval = 0;
        } catch(java.io.IOException e){
            System.err.println("cleanupDataDirectory() Exception : " + e.getMessage());
        }
        return retval;
    }


}
