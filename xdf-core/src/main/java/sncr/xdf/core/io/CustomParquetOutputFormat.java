package sncr.xdf.core.io;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetOutputFormat;
import org.apache.parquet.hadoop.example.GroupWriteSupport;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.hadoop.util.ContextUtil;
import org.apache.parquet.schema.MessageType;

import java.io.IOException;

/**
 * Created by alexey.sorokin on 4/21/2015.
 */
public class CustomParquetOutputFormat extends ParquetOutputFormat<Group> {
	
	public CustomParquetOutputFormat() {
        super(new GroupWriteSupport());
        
    }

    
	/**
     * set the schema being written to the job conf
     * @param schema the schema of the data
     * @param configuration the job configuration
     */
    public static void setSchema(Job job, MessageType schema) {
    	GroupWriteSupport.setSchema(schema, ContextUtil.getConfiguration(job));
    }

    /**
     * retrieve the schema from the conf
     * @param configuration the job conf
     * @return the schema
     */
    public static MessageType getSchema(Job job) {
        return GroupWriteSupport.getSchema(ContextUtil.getConfiguration(job));
    }

    
    @Override
    public RecordWriter<Void, Group> getRecordWriter(TaskAttemptContext arg0) throws IOException, InterruptedException 
    {

    	final Configuration conf = ContextUtil.getConfiguration(arg0);
		//Path rejectedOutputPath = new Path(conf.get("location"));
		//Path rejectedOutputPathTransformation = new Path(conf.get("tlocation"));
		CompressionCodecName codec = getCompression(arg0);
        String extension = codec.getExtension() + ".parquet";
        Path file = getDefaultWorkFile(arg0, extension);
        return getRecordWriter(conf, file, codec);
    }
}

