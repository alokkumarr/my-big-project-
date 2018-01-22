package sncr.xdf.dataprofiler;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XDFParquetSupport {

    private static Schema SCHEMA;



    //https://stackoverflow.com/questions/39728854/create-parquet-files-in-java

    public static void main(String[] args) throws IOException {

        String schema = "{\"fields\": [{\"name\": \"F00\", \"type\": \"string\"},{\"name\": \"F01\",\"type\": \"long\"},{\"name\": \"F02\",\"type\": \"double\"}]}";
        String pqSchema = ParquetWriterRecordProcessor.CreateAvroParquetSchema(schema);

        System.out.println(pqSchema);

        SCHEMA = new Schema.Parser().parse(pqSchema);

        List<GenericData.Record> sampleData = new ArrayList<>();

        GenericData.Record record = new GenericData.Record(SCHEMA);
        record.put("F00", "Some String");
        record.put("F01", 1);
        record.put("F02", 2.98);
        sampleData.add(record);

        GenericData.Record record2 = new GenericData.Record(SCHEMA);
        record.put("F00", "Some String 2");
        record.put("F01", 1000);
        record.put("F02", 200.0098);
        sampleData.add(record);

        XDFParquetSupport writerReader = new XDFParquetSupport();
        writerReader.writeToParquet(sampleData, new Path(args[0]));
        writerReader.readFromParquet(new Path(args[0]));
    }

    @SuppressWarnings("unchecked")
    public void readFromParquet(Path filePathToRead) throws IOException {
        try (ParquetReader<GenericData.Record> reader = AvroParquetReader
            .<GenericData.Record>builder(filePathToRead)
            .withConf(new Configuration())
            .build()) {

            GenericData.Record record;
            while ((record = reader.read()) != null) {
                System.out.println(record);
            }
        }
    }

    public void writeToParquet(List<GenericData.Record> recordsToWrite, Path fileToWrite) throws IOException {
        try (ParquetWriter<GenericData.Record> writer = AvroParquetWriter
            .<GenericData.Record>builder(fileToWrite)
            .withSchema(SCHEMA)
            .withConf(new Configuration())
            .withCompressionCodec(CompressionCodecName.SNAPPY)
            .build()) {

            for (GenericData.Record record : recordsToWrite) {
                writer.write(record);
            }
        }
    }

}
