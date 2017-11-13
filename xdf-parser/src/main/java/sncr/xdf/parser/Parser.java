package sncr.xdf.parser;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import sncr.xdf.exceptions.XDFException;
import sncr.xdf.component.Component;
import sncr.xdf.component.WithDataSetService;
import sncr.xdf.component.WithMovableResult;
import sncr.xdf.component.WithSparkContext;
import sncr.xdf.conf.ComponentConfiguration;
import sncr.xdf.conf.Field;
import sncr.xdf.core.file.HFileOperations;
import sncr.xdf.parser.spark.ConvertToRow;
import sncr.xdf.parser.spark.HeaderFilter;
import sncr.xdf.preview.CsvInspectorRowProcessor;

import java.io.IOException;
import java.util.List;

public class Parser extends Component implements WithMovableResult, WithSparkContext, WithDataSetService {

    private static final Logger logger = Logger.getLogger(Parser.class);

    {
        componentName = "parser";
    }

    public static void main(String[] args){
        Parser component = new Parser();
        try {
            // Spark based component
            if (component.collectParameters(args) == 0) {
                int r = component.Run();
                System.exit(r);
            }
        } catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    protected int Execute(){
        String sourcePath = ctx.componentConfiguration.getParser().getFile();
        String tempDir = generateTempLocation(new DataSetServiceAux(ctx, md), null, null);
        // Check what sourcePath referring
        FileSystem fs = HFileOperations.getFileSystem();
        try {
            FileStatus[] files = fs.globStatus(new Path(sourcePath));

            // Check if directory has been given
            if(files.length == 1 && files[0].isDirectory()){
                // If so - we have to process all the files inside - create the mask
                sourcePath += Path.SEPARATOR + "*";
                // ... and query content
                files = fs.globStatus(new Path(sourcePath));
            }

            for(FileStatus file : files){
                if(file.isFile()){
                    parseSingleFile(file.getPath(), new Path(tempDir + Path.SEPARATOR + file.getPath().getName()));
                }
            }
        }catch (IOException e){
            return -1;
        }
        return 0;
    }

    protected ComponentConfiguration validateConfig(String config) throws Exception {
        return Parser.analyzeAndValidate(config);
    }

    public static ComponentConfiguration analyzeAndValidate(String config) throws Exception {

        ComponentConfiguration compConf = Component.analyzeAndValidate(config);
        sncr.xdf.conf.Parser parserProps = compConf.getParser();
        if (parserProps == null) {
            throw new XDFException( XDFException.ErrorCodes.InvalidConfFile);
        }

        if(parserProps.getFile() == null || parserProps.getFile().length() == 0){
            throw new XDFException(XDFException.ErrorCodes.InvalidConfFile);
        }

        if(parserProps.getFields() == null || parserProps.getFields().size() == 0){
            throw new XDFException(XDFException.ErrorCodes.InvalidConfFile);
        }
        return compConf;
    }

    protected int Archive(){
        return 0;
    }

    @Override
    protected String mkConfString() {
        String s = "Parser Component parameters: ";
        return s;
    }

    int parseSingleFile(Path file, Path destDir){
        logger.info("Parsing " + file + " to " + destDir);
        logger.info("Header size : " + ctx.componentConfiguration.getParser().getHeaderSize());

        StructType schema = createSchema(ctx.componentConfiguration.getParser().getFields());

        JavaRDD<Row> rdd = new JavaSparkContext(ctx.sparkSession.sparkContext())
            .textFile(file.toString(), 1)
            .zipWithIndex()
            .filter(new HeaderFilter(ctx.componentConfiguration.getParser().getHeaderSize()))
            .keys()
            .map(new ConvertToRow(schema));


        Dataset<Row> df = ctx.sparkSession.createDataFrame(rdd.rdd(), schema);
        df.write().parquet(destDir.toString());
        return 0;
    }

    private StructType createSchema(List<Field> fields){

        StructField[] structFields = new StructField[fields.size()];
        int i = 0;
        for(Field field : fields){
            // Must use Metadata.empty(), not null
            StructField structField = new StructField(field.getName(), convertXdfToSparkType(field.getType()), true, Metadata.empty());
            structFields[i] = structField;
            i++;
        }
        return  new StructType(structFields);
    }

    private DataType convertXdfToSparkType(String xdfType){
        switch(xdfType){
            case CsvInspectorRowProcessor.T_STRING:
                return DataTypes.StringType;
            case CsvInspectorRowProcessor.T_LONG:
                return DataTypes.LongType;
            case CsvInspectorRowProcessor.T_DOUBLE:
                return DataTypes.DoubleType;
            case CsvInspectorRowProcessor.T_DATETIME:
                return DataTypes.TimestampType;  // TODO: Implement proper format for timestamp
            default:
                return DataTypes.StringType;

        }
    }
}
