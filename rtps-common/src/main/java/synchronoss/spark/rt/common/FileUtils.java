package synchronoss.spark.rt.common;


import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by asor0002 on 8/1/2016.
 *
 * TODO: This should be the part of XDF common
 */
public class FileUtils {
    // TODO: This should be the part of XDF common
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
        } catch (Exception e) {
            throw new FileNotFoundException("File not found on the provided location :" + e);
        }

        return data;
    }

}
