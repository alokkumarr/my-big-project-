package sncr.xdf.parser;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.util.LongAccumulator;
import sncr.xdf.component.Component;
import sncr.xdf.component.WithDataSetService;
import sncr.xdf.component.WithMovableResult;
import sncr.xdf.component.WithSparkContext;
import sncr.xdf.conf.ComponentConfiguration;
import sncr.xdf.conf.Field;
import sncr.xdf.core.file.DLDataSetOperations;
import sncr.xdf.core.file.HFileOperations;
import sncr.xdf.datasets.conf.DataSetProperties;
import sncr.xdf.exceptions.XDFException;
import sncr.xdf.parser.spark.ConvertToRow;
import sncr.xdf.parser.spark.HeaderFilter;
import sncr.xdf.preview.CsvInspectorRowProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Parser extends Component implements WithMovableResult, WithSparkContext, WithDataSetService {

    private static final Logger logger = Logger.getLogger(Parser.class);

    private String lineSeparator;
    private char delimiter;
    private char quoteChar;
    private char quoteEscapeChar;
    private int headerSize;
    private String sourcePath;
    private String tempDir;
    private String outputDataSetName;
    private String outputDataSetLocation;

    private LongAccumulator errCounter;
    private LongAccumulator recCounter;

    private StructType schema;
    private StructType internalSchema;


    {
        componentName = "parser";
    }

    public static void main(String[] args){
        Parser component = new Parser();
        try {
            // Spark based component
            if (component.collectCMDParameters(args) == 0) {
                int r = component.Run();
                System.exit(r);
            }
        } catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    protected int Execute(){

        int retval = 0;

        sourcePath = ctx.componentConfiguration.getParser().getFile();
        headerSize = ctx.componentConfiguration.getParser().getHeaderSize();
        tempDir = generateTempLocation(new DataSetServiceAux(ctx, md), null, null);
        lineSeparator = ctx.componentConfiguration.getParser().getLineSeparator();
        delimiter = ctx.componentConfiguration.getParser().getDelimiter().charAt(0);
        quoteChar = ctx.componentConfiguration.getParser().getQuoteChar().charAt(0);
        quoteEscapeChar = ctx.componentConfiguration.getParser().getQuoteEscape().charAt(0);

        errCounter = ctx.sparkSession.sparkContext().longAccumulator("ParserErrorCounter");
        recCounter = ctx.sparkSession.sparkContext().longAccumulator("ParserRecCounter");

        schema = createSchema(ctx.componentConfiguration.getParser().getFields(), false);
        internalSchema = createSchema(ctx.componentConfiguration.getParser().getFields(), true);

        // Output data set
        if(outputDataSets.size() != 1){
            // error - must be only one for parser
            logger.error("Found multiple output data set definitions");
            return -1;
        }

        Map.Entry<String, Map<String, String>> ds =  (Map.Entry<String, Map<String, String>>)outputDataSets.entrySet().toArray()[0];
        outputDataSetName = ds.getKey();
        outputDataSetLocation = ds.getValue().get(DataSetProperties.PhysicalLocation.name());
        logger.info("Output dataset " + outputDataSetName + " located at " + outputDataSetLocation);

        // Check what sourcePath referring
        FileSystem fs = HFileOperations.getFileSystem();

        try {
            if(headerSize >= 1) {
                FileStatus[] files = fs.globStatus(new Path(sourcePath));
                // Check if directory has been given
                if(files.length == 1 && files[0].isDirectory()){
                    // If so - we have to process all the files inside - create the mask
                    sourcePath += Path.SEPARATOR + "*";
                    // ... and query content
                    files = fs.globStatus(new Path(sourcePath));
                }
                retval = parseFiles(files,  DLDataSetOperations.MODE_APPEND);
            } else {
                retval = parse(DLDataSetOperations.MODE_APPEND);
            }
        }catch (IOException e){
            retval =  -1;
        }
        return retval;
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

    // Parse data without headers
    int parse(String mode){

        logger.info("Parsing " + sourcePath + " to " + tempDir);
        logger.info("Header size : " + headerSize);

        JavaRDD<Row> rdd = new JavaSparkContext(ctx.sparkSession.sparkContext())
            .textFile(sourcePath, 1)
            .map(new ConvertToRow(schema, lineSeparator, delimiter, quoteChar, quoteEscapeChar, '\'', recCounter, errCounter));


        Dataset<Row> df = ctx.sparkSession.createDataFrame(rdd.rdd(), internalSchema);

        scala.collection.Seq<Column> scalaList=
            scala.collection.JavaConversions.asScalaBuffer(createFieldList(ctx.componentConfiguration.getParser().getFields())).toList();
        df.select(scalaList).where(df.col("__REJ_FLAG").equalTo(0)).write().parquet(tempDir);
        resultDataDesc.add(new MoveDataDescriptor(tempDir, outputDataSetLocation, outputDataSetName, mode, DLDataSetOperations.FORMAT_PARQUET));

        return 0;
    }


    // Parse data with headers - we have to do this file by file
    private int parseFiles(FileStatus[] files, String mode){
        // Files
        for (FileStatus file : files) {
            if (file.isFile()) {
                String tempPath = tempDir + Path.SEPARATOR + file.getPath().getName();
                if (parseSingleFile(file.getPath(), new Path(tempPath)) == 0) {
                    resultDataDesc.add(new MoveDataDescriptor(tempPath, outputDataSetLocation, outputDataSetName, mode, DLDataSetOperations.FORMAT_PARQUET));
                }
            }
        }
        return 0;
    }

    private int parseSingleFile(Path file, Path destDir){
        logger.info("Parsing " + file + " to " + destDir);
        logger.info("Header size : " + headerSize);

        JavaRDD<Row> rdd = new JavaSparkContext(ctx.sparkSession.sparkContext())
            .textFile(file.toString(), 1)
            // Add line numbers
            .zipWithIndex()
            // Filter out header based on line number
            .filter(new HeaderFilter(headerSize))
            // Get rid of file numbers
            .keys()
            .map(new ConvertToRow(schema, lineSeparator, delimiter, quoteChar, quoteEscapeChar, '\'', recCounter, errCounter));


        Dataset<Row> df = ctx.sparkSession.createDataFrame(rdd.rdd(), internalSchema);
        // TODO: Filter out all rejected records

        scala.collection.Seq<Column> scalaList=
            scala.collection.JavaConversions.asScalaBuffer(createFieldList(ctx.componentConfiguration.getParser().getFields())).toList();
        df.select(scalaList).where(df.col("__REJ_FLAG").equalTo(0)).write().parquet(destDir.toString());
        return 0;
    }

    private static List<Column> createFieldList(List<Field> fields){

        List<Column> retval = new ArrayList<>(fields.size());
        for(Field field : fields){
            retval.add(new Column(field.getName()));
        }
        return retval;
    }

    private static StructType createSchema(List<Field> fields, boolean addRejectedFlag){

        StructField[] structFields = new StructField[fields.size() + (addRejectedFlag ? 1 : 0)];
        int i = 0;
        for(Field field : fields){
            // Must use Metadata.empty(), not null
            StructField structField = new StructField(field.getName(), convertXdfToSparkType(field.getType()), true, Metadata.empty());
            structFields[i] = structField;
            i++;
        }

        if(addRejectedFlag){
            structFields[i] = new StructField("__REJ_FLAG", DataTypes.IntegerType, true, Metadata.empty());
        }
        return  new StructType(structFields);
    }

    private static DataType convertXdfToSparkType(String xdfType){
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
