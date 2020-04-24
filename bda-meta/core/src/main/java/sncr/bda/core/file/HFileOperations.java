package sncr.bda.core.file;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.util.Shell;
import org.apache.log4j.Logger;

import java.io.*;
import sncr.bda.utils.BdaCoreUtils;

/**
 * Created by srya0001 on 2/23/2017.
 */
public class HFileOperations {

    public static FileSystem fs;
    public static FileContext fc;

    private static final Logger logger = Logger.getLogger(HFileOperations.class);
    private static boolean initialized = false;

    // Initialize File System
    public static void init(int retries) throws Exception {
        if (initialized) return;
        Configuration fsConfig = new Configuration();
        try {
            fs = FileSystem.get(fsConfig);
            fc = FileContext.getFileContext(fs.getConf());
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new Exception("Cannot initialize FileSystem", e);
        }
        // Retry the file system object creation if previous attempt fails
        if ((fs==null || fc == null) && retries >0 ) {
            logger.trace("Number of retry left:  "+ retries);
            init(retries-1);
        }
        initialized = true;
    }


    public static String readFile(String fileName) throws FileNotFoundException {
        FileSystem fs;
        InputStream stream;
        try {
            String normalizedFilePath = BdaCoreUtils.normalizePath(fileName);
            Path path = new Path(normalizedFilePath);
            Configuration conf = new Configuration();
            fs = FileSystem.get(path.toUri(), conf);
            CompressionCodecFactory factory = new CompressionCodecFactory(conf);
            CompressionCodec codec = factory.getCodec(path);
            if(codec != null){
                stream = codec.createInputStream(fs.open(path));
            } else {
                stream = fs.open(path);
            }

            String data = new String(IOUtils.toByteArray(stream));
            stream.close();
            return data;
        } catch (Exception e) {
            logger.error("XDF-Hadoop exception: ", e);
            FileNotFoundException fe = new FileNotFoundException("File not found on the provided location");
            fe.initCause(e);
            throw fe;
        }
    }


    public static InputStream readFileToInputStream(String fileName) throws FileNotFoundException {
        FileSystem fs;
        InputStream stream;
        try {
            String normalizedFilePath = BdaCoreUtils.normalizePath(fileName);
            Path path = new Path(normalizedFilePath);
            Configuration conf = new Configuration();
            fs = FileSystem.get(path.toUri(), conf);
            CompressionCodecFactory factory = new CompressionCodecFactory(conf);
            CompressionCodec codec = factory.getCodec(path);
            if(codec != null){
                stream = codec.createInputStream(fs.open(path));
            } else {
                stream = fs.open(path);
            }

        } catch (Exception e) {
            logger.error("XDF-Hadoop exception: ", e);
            FileNotFoundException fe = new FileNotFoundException("File not found on the provided location");
            fe.initCause(e);
            throw fe;
        }
        return stream;
    }


    public static OutputStream writeToFile(String fileName) throws FileNotFoundException {
        FileSystem fs;
        FSDataOutputStream fout_stream = null;
        try {
            Path path = new Path(fileName);
            Configuration conf = new Configuration();
            fs = FileSystem.get(path.toUri(), conf);

            fout_stream = fs.create(path);

        } catch(Shell.ExitCodeException e){
            e.printStackTrace();
        } catch (Exception e) {
            logger.error("XDF-Hadoop exception: ", e);
            FileNotFoundException fe = new FileNotFoundException("File not found on the provided location");
            fe.initCause(e);
            throw fe;
        }
        return fout_stream.getWrappedStream();
    }

    public static FileStatus[] getFilesStatus(String fileName) throws Exception {
        FileSystem fs;
        try {
            Path path = new Path(fileName);
            Configuration conf = new Configuration();

            fs = FileSystem.get(path.toUri(), conf);
            if(fs.exists(path)) {
                if (fs.isDirectory(path))
                    return fs.listStatus(path);
                else {
                    FileStatus[] fstat = new FileStatus[1];
                    fstat[0] = fs.getFileStatus(path);
                    return fstat;
                }
            } else {
                return null;
            }
        } catch (IOException e) {
            logger.error("XDF-Hadoop exception: ", e);
            throw new Exception("Cannot get file status on provided PhysicalLocation", e);
        }
    }

      public static FileStatus[] getlistOfFileStatus(String fileName) throws Exception {
        FileSystem fs;
        FileStatus[] files = null;
        try {
          Path path = new Path(fileName);
          Configuration conf = new Configuration();
          fs = FileSystem.get(path.toUri(), conf);
          files = fs.globStatus(path);
        } catch (IOException e) {
          logger.error("Exception occurred while getting the file status from hdfs using file pattern: " + fileName, e);
          throw new Exception("Exception occurred while getting the file status from hdfs using file pattern :" + e);
        }
        return files;
      }

    public static void deleteEnt(String file) throws Exception {
        FileSystem fs;
        try {
            String normalizedFilePath = BdaCoreUtils.normalizePath(file);
            Path path = new Path(normalizedFilePath);
            Configuration conf = new Configuration();
            fs = FileSystem.get(path.toUri(), conf);
            fs.delete(path, true);
        } catch (IOException e) {
            logger.error("XDF-Hadoop exception: ", e);
            throw new Exception("Cannot get file status on provided PhysicalLocation", e);
        }
    }

    public static String[] listJarFiles(String directoryLocation, final String fileExtension) {
        String[] jarFiles = null;
        File jars = new File(directoryLocation);
        if(jars.isDirectory()){
            if (jars.isDirectory()) {
                if (jars.listFiles().length > 0) {
                    jarFiles = jars.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                            boolean jarIndicator = false;
                            if (name.endsWith(fileExtension)) {
                                jarIndicator = true;
                            }
                            return jarIndicator;
                        }
                    });
                }
            }
        } else {
            throw new IllegalArgumentException(directoryLocation +" location which has been provided is not directory.");
        }
        return jarFiles;
    }


    public static void createDir(String s) throws Exception {
        FileSystem fs;
        try {
            String normalizedFilePath = BdaCoreUtils.normalizePath(s);
            Path path = new Path(normalizedFilePath);
            Configuration conf = new Configuration();
            fs = FileSystem.get(path.toUri(), conf);
            //fs.create(path);
            fs.mkdirs(path);
        } catch (IOException e) {
            logger.error("IO Exception at attempt to create dir: ", e);
            throw new Exception("Cannot create directory provided PhysicalLocation", e);
        }
    }

    public static boolean exists(String s) throws Exception {
        FileSystem fs;
        try {
            Path path = new Path(s);
            Configuration conf = new Configuration();
            fs = FileSystem.get(path.toUri(), conf);
            return fs.exists(path);
        } catch (IOException e) {
            logger.error("Cannot check path:" + e);
        }
        return false;

    }

    public static FileSystem getFileSystem() {
        try {
            init(10);
        } catch(Exception e){
            logger.error(e.getMessage());
            return null;
        }
        return fs;
    }

    public static FileContext getFileContext() { return fc;}
}
