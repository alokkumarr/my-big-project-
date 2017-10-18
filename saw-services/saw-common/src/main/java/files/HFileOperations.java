package files;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;

import java.io.*;

/**
 * Created by srya0001 on 2/23/2017.
 */
public class HFileOperations {


    public static String readFile(String fileName) throws FileNotFoundException {
        FileSystem fs;
        InputStream stream;
        try {

            Path path = new Path(fileName);
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
            fs.close();
            return data;
        } catch (Exception e) {
            throw new FileNotFoundException("File not found on the provided location :" + e);
        }
    }


    public static InputStream readFileToInputStream(String fileName) throws FileNotFoundException {
        FileSystem fs;
        InputStream stream;
        try {

            Path path = new Path(fileName);
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
            throw new FileNotFoundException("File not found on the provided location :" + e);
        }
        return stream;
    }


    public static OutputStream writeToFile(String fileName) throws FileNotFoundException {
        FileSystem fs;
        try {
            Path path = new Path(fileName);
            Configuration conf = new Configuration();
            fs = FileSystem.get(conf);
            FSDataOutputStream fout_stream = fs.create(path, true);
            return fout_stream.getWrappedStream();
        } catch (Exception e) {
            throw new FileNotFoundException("File not found on the provided location :" + e);
        }
    }

    public static FileStatus[] getFilesStatus(String fileName) throws FileNotFoundException {
        FileSystem fs;
        try {
            Path path = new Path(fileName);
            Configuration conf = new Configuration();
            fs = FileSystem.get(path.toUri(), conf);
            if (fs.isDirectory(path))
                return fs.listStatus(path);
            else {
                FileStatus[] fstat = new FileStatus[1];
                fstat[0] = fs.getFileStatus(path);
                return fstat;
            }
        } catch (IOException e) {
            throw new FileNotFoundException("Cannot get file status on provided locations:" + e);
        }
    }

    public static void deleteFile(String file) throws FileNotFoundException {
        FileSystem fs;
        try {
            Path path = new Path(file);
            Configuration conf = new Configuration();
            fs = FileSystem.get(path.toUri(), conf);
            fs.delete(path, true);
        } catch (IOException e) {
            throw new FileNotFoundException("Cannot get file status on provided locations:" + e);
        }
    }

    public static void createDirectory(String dir) throws IOException {
        Path path = new Path(dir);
        Configuration conf = new Configuration();
        try (FileSystem fs = FileSystem.get(path.toUri(), conf)) {
            fs.mkdirs(path);
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


}
