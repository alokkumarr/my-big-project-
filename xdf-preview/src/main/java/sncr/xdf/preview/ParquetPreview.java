package sncr.xdf.preview;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.column.ColumnDescriptor;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.metadata.ParquetMetadata;
import org.apache.parquet.hadoop.util.HiddenFileFilter;
import org.apache.parquet.schema.MessageType;

import java.io.IOException;
import java.util.List;

import static org.apache.parquet.format.converter.ParquetMetadataConverter.NO_FILTER;

public class ParquetPreview {

    public static void main(String[] args) throws Exception {
        String path = args[0];

        String schema = ParquetPreview.getParquetSchema(path);
        System.out.println(schema);
        System.exit(0);
    }

    public static String getParquetSchema(String p) throws IOException {
        Configuration conf = new Configuration();
        Path path = new Path(p);
        FileSystem fs = path.getFileSystem(conf);
        Path file;
        if (fs.isDirectory(path)) {
            FileStatus[] statuses = fs.listStatus(path, HiddenFileFilter.INSTANCE);
            if (statuses.length == 0) {
                throw new RuntimeException("Directory " + path.toString() + " is Empty");
            }
            file = statuses[0].getPath();
        } else {
            file = path;
        }

        ParquetMetadata metaData = ParquetFileReader.readFooter(conf, file, NO_FILTER);
        MessageType schema = metaData.getFileMetaData().getSchema();

        JsonObject parsedHeaderLine = new JsonObject();

        List<ColumnDescriptor> columns = schema.getColumns();
        for(ColumnDescriptor column : columns){
            System.out.println(column);
            switch(column.getType()){
                case DOUBLE:
                    parsedHeaderLine.addProperty(column.getPath()[0], CsvInspectorRowProcessor.T_DOUBLE);
                    break;
                case INT64:
                    parsedHeaderLine.addProperty(column.getPath()[0], CsvInspectorRowProcessor.T_LONG);
                    break;

                case BINARY:
                    parsedHeaderLine.addProperty(column.getPath()[0], CsvInspectorRowProcessor.T_STRING);
                    break;
                case INT96:
                    parsedHeaderLine.addProperty(column.getPath()[0], CsvInspectorRowProcessor.T_DATETIME);
                    break;
                default:
                    parsedHeaderLine.addProperty(column.getPath()[0], "UNSUPPORTED TYPE");
                    break;

            }
        }

        Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setVersion(1.0)
            .create();
        return gson.toJson(parsedHeaderLine);
    }

}
