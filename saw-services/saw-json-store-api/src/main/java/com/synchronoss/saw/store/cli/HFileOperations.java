package com.synchronoss.saw.store.cli;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.util.Shell;
import org.apache.log4j.Logger;

import java.io.*;

/**
 * Created by srya0001 on 2/23/2017.
 */
public class HFileOperations {

    public static FileSystem fs;
    public static FileContext fc;

    private static final Logger logger = Logger.getLogger(HFileOperations.class);
    private static boolean initialized = false;

    // Initialize File System
    public static void init() throws Exception {
        if (initialized) return;
        Configuration fsConfig = new Configuration();
        try {
            fs = FileSystem.get(fsConfig);
            fc = FileContext.getFileContext(fs.getConf());
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new Exception("Cannot initialize FileSystem");
        }
        initialized = true;
    }


    public static String readFile(String fileName) throws FileNotFoundException {
        FileSystem fs;
        InputStream stream;
        try {

            Path path = new Path(fileName);
            Configuration conf = new Configuration();
            fs = getFileSystem(path,conf);
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
            throw new FileNotFoundException("File not found on the provided location :" + e);
        }
    }


    public static InputStream readFileToInputStream(String fileName) throws FileNotFoundException {
        FileSystem fs;
        InputStream stream;
        try {

            Path path = new Path(fileName);
            Configuration conf = new Configuration();
            fs = getFileSystem(path,conf);
            CompressionCodecFactory factory = new CompressionCodecFactory(conf);
            CompressionCodec codec = factory.getCodec(path);
            if(codec != null){
                stream = codec.createInputStream(fs.open(path));
            } else {
                stream = fs.open(path);
            }

        } catch (Exception e) {
            logger.error("XDF-Hadoop exception: ", e);
            throw new FileNotFoundException("File not found on the provided location :" + e);
        }
        return stream;
    }


    public static OutputStream writeToFile(String fileName) throws FileNotFoundException {
        FileSystem fs;
        FSDataOutputStream fout_stream = null;
        try {
            Path path = new Path(fileName);
            Configuration conf = new Configuration();
            fs = getFileSystem(path,conf);

            fout_stream = fs.create(path);

        } catch(Shell.ExitCodeException e){
            e.printStackTrace();
        } catch (Exception e) {
            logger.error("XDF-Hadoop exception: ", e);
            throw new FileNotFoundException("File not found on the provided location :" + e);
        }
        return fout_stream.getWrappedStream();
    }

    public static FileStatus[] getFilesStatus(String fileName) throws Exception {
        FileSystem fs;
        try {
            Path path = new Path(fileName);
            Configuration conf = new Configuration();
            fs = getFileSystem(path,conf);
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
            throw new Exception("Cannot get file status on provided PhysicalLocation:" + e);
        }
    }

    public static void deleteEnt(String file) throws Exception {
        FileSystem fs;
        try {
            Path path = new Path(file);
            Configuration conf = new Configuration();
            fs = getFileSystem(path,conf);
            fs.delete(path, true);
        } catch (IOException e) {
            logger.error("XDF-Hadoop exception: ", e);
            throw new Exception("Cannot get file status on provided PhysicalLocation:" + e);
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
            Path path = new Path(s);
            Configuration conf = new Configuration();
            fs = getFileSystem(path,conf);
            //fs.create(path);
            fs.mkdirs(path);
        } catch (IOException e) {
            logger.error("IO Exception at attempt to create dir: ", e);
            throw new Exception("Cannot create directory provided PhysicalLocation:" + e);
        }
    }

    public static boolean exists(String s) throws Exception {
        FileSystem fs;
        try {
            Path path = new Path(s);
            Configuration conf = new Configuration();
            fs = getFileSystem(path,conf);
            return fs.exists(path);
        } catch (IOException e) {
            logger.error("Cannot check path:" + e);
        }
        return false;

    }


    public static FileSystem getFileSystem() {
        try {
            init();
        } catch(Exception e){
            logger.error(e.getMessage());
            return null;
        }
        return fs;
    }

    public static FileContext getFileContext() { return fc;}

    /**
     * This method is used to get the file System taking the precedence
     * of hadoop cache , if FileSystem is already closed then get the
     * new instance and that will be automatically inserted into hadoop
     * FileSystem cache for reuse.
     * @param path
     * @param conf
     * @return
     * @throws IOException
     */
    private static FileSystem getFileSystem(Path path, Configuration conf) throws IOException {
        FileSystem fs =null;
        try {
            fs = FileSystem.get(path.toUri(), conf);
            return  fs;
        } catch (IOException ex)
        {
            // File System is already closed then try with new instance
            // new instance will be inserted into HDFS fileSystem Cache for reuse
            // So, closing file system is not required.
            logger.warn("Failed to get FileSystem.", ex);
        }
        if(fs==null) {
            // In case of file system already closed , Attempt to get new instance.
            fs = FileSystem.newInstance(path.toUri(), conf);
        }
        return fs;
    }
}
