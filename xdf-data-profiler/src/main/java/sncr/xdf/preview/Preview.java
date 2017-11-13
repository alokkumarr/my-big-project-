package sncr.xdf.preview;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.metadata.ParquetMetadata;
import org.apache.parquet.hadoop.util.HiddenFileFilter;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.tools.read.SimpleReadSupport;
import org.apache.parquet.tools.read.SimpleRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.apache.parquet.format.converter.ParquetMetadataConverter.NO_FILTER;

public class Preview {

    public static void main(String[] args) throws Exception {
        String p = args[0];
        //Read 10 records from parquet
        //previewParquet(p, 10L);
        Long t1 = System.currentTimeMillis();
        String schema = getParquetSchema(p);
        Long t2 = System.currentTimeMillis();
        System.out.println("Request time :" + (t2-t1) + " ms.");
        System.out.println(schema);

        t1 = System.currentTimeMillis();
        String data = previewUnpartitionedParquetData(p, 100L);
        t2 = System.currentTimeMillis();
        System.out.println("Request time :" + (t2-t1) + " ms.");
        System.out.println(data);


    }

    // Extract <num> records from file <p>
    public static String previewUnpartitionedParquetData(String p, Long num) throws Exception {

        Configuration conf = new Configuration();
        Path path = new Path(p);
        FileSystem fs = path.getFileSystem(conf);
        List<Path> files = new ArrayList<>();
        FileStatus[] statuses;
        if (fs.isDirectory(path)) {
            statuses = fs.listStatus(path, HiddenFileFilter.INSTANCE);
            if (statuses.length == 0) {
                throw new RuntimeException("Directory " + path.toString() + " is Empty");
            }
            for(FileStatus s : statuses) {
                if(!s.isDirectory())
                    files.add(s.getPath());
            }
        } else {
            files.add(path);
        }

        StringBuilder sb = new StringBuilder();
        ParquetReader<SimpleRecord> reader = ParquetReader
            .builder(new SimpleReadSupport(), files.get(0)).build();
        try {
            StringWriter sw = new StringWriter();
            PrintWriter writer = new PrintWriter(sw);

            // Read parquet file record by record
            for (SimpleRecord value = reader.read(); value != null && num-- > 0; value = reader.read()) {
                value.prettyPrintJson(writer);
                sb.append(sw.toString()).append("\n");
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception ex) {
                }
            }
        }
        return sb.toString();
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
        StringBuilder sb = new StringBuilder();
        schema.writeToStringBuilder(sb, " ");
        return sb.toString();
    }

    public static String previewLocalRawTextData(String fpath, int beg, int num) throws Exception {
        // Read result from exteral process
        // Process process = Runtime.getRuntime().exec("your command");
        //ProcessBuilder pb = new ProcessBuilder("");
        String[] cmd = { "/bin/sh", "-c", String.format("exec 2>&1; tail<%s -n +%d | head -%d",fpath,beg,num)};
        Process process = Runtime.getRuntime().exec(cmd);
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()) );
        StringBuilder sb = new StringBuilder();
        while( num-- > 0) {
            String s = br.readLine();
            if( s == null ) break;
            sb.append( s ).append( '\n' );
        }
        br.close();
        process.waitFor();
        return sb.toString();
    }

}
