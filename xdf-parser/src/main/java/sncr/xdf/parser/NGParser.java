package sncr.xdf.parser;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.*;
import org.apache.spark.util.LongAccumulator;
import sncr.bda.conf.ComponentConfiguration;
import sncr.bda.conf.Field;
import sncr.bda.core.file.HFileOperations;
import sncr.bda.datasets.conf.DataSetProperties;
import sncr.xdf.component.*;
import sncr.xdf.context.NGContext;
import sncr.xdf.core.file.DLDataSetOperations;
import sncr.xdf.exceptions.XDFException;
import sncr.xdf.ngcomponent.AbstractComponent;
import sncr.xdf.parser.spark.ConvertToRow;
import sncr.xdf.parser.spark.HeaderFilter;
import sncr.xdf.preview.CsvInspectorRowProcessor;
import sncr.xdf.adapters.writers.MoveDataDescriptor;
import sncr.xdf.adapters.writers.XDFDataWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NGParser extends AbstractComponent implements WithWrittenResult, WithSpark, WithDataSetService {

    private static final Logger logger = Logger.getLogger(NGParser.class);

    private String lineSeparator;
    private char delimiter;
    private char quoteChar;
    private char quoteEscapeChar;
    private int headerSize;
    private String sourcePath;
    private String tempDir;
    private String outputDataSetName;
    private String outputDataSetLocation;
    private String outputFormat;

    private LongAccumulator errCounter;
    private LongAccumulator recCounter;

    private StructType schema;
    private List<String> tsFormats;
    private StructType internalSchema;
    private Integer outputNOF;

    private List<String> pkeys;

    public NGParser(NGContext ngctx, boolean useMD, boolean useSample) { super(ngctx, useMD, useSample); }

    public NGParser() {  super(); }

    {
        componentName = "parser";
    }

    public static void main(String[] args){
        NGParser component = new NGParser();
        try {
            // Spark based component
            if (component.initWithCMDParameters(args) == 0) {
                int r = component.run();
                System.exit(r);
            }
        } catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    protected int execute(){

        int retval = 0;

        sourcePath = ctx.componentConfiguration.getParser().getFile();
        headerSize = ctx.componentConfiguration.getParser().getHeaderSize();
        tempDir = generateTempLocation(new DataSetServiceAux(ctx, services.md), null, null);
        lineSeparator = ctx.componentConfiguration.getParser().getLineSeparator();
        delimiter = ctx.componentConfiguration.getParser().getDelimiter().charAt(0);
        quoteChar = ctx.componentConfiguration.getParser().getQuoteChar().charAt(0);
        quoteEscapeChar = ctx.componentConfiguration.getParser().getQuoteEscape().charAt(0);

        errCounter = ctx.sparkSession.sparkContext().longAccumulator("ParserErrorCounter");
        recCounter = ctx.sparkSession.sparkContext().longAccumulator("ParserRecCounter");

        schema = createSchema(ctx.componentConfiguration.getParser().getFields(), false);
        tsFormats = createTsFormatList(ctx.componentConfiguration.getParser().getFields());
        logger.info(tsFormats);

        internalSchema = createSchema(ctx.componentConfiguration.getParser().getFields(), true);

        // Output data set
        if(ngctx.outputDataSets.size() != 1){
            // error - must be only one for parser
            logger.error("Found multiple output data set definitions");
            return -1;
        }

        Map.Entry<String, Map<String, Object>> ds =  (Map.Entry<String, Map<String, Object>>)ngctx.outputDataSets.entrySet().toArray()[0];
        outputDataSetName = ds.getKey();
        outputDataSetLocation = (String) ds.getValue().get(DataSetProperties.PhysicalLocation.name());
        outputFormat = (String) ds.getValue().get(DataSetProperties.Format.name());
        outputNOF =  (Integer) ds.getValue().get(DataSetProperties.NumberOfFiles.name());
        pkeys = (List<String>) ds.getValue().get(DataSetProperties.PartitionKeys.name());

        logger.info("Output data set " + outputDataSetName + " located at " + outputDataSetLocation + " with format " + outputFormat);

        //TODO: If data set exists and flag is not append - error
        // This is good for UI what about pipeline? Talk to Suren


        // Check what sourcePath referring
        FileSystem fs = HFileOperations.getFileSystem();

        //new ConvertToRow(schema, tsFormats, lineSeparator, delimiter, quoteChar, quoteEscapeChar, '\'', recCounter, errCounter);
        //System.exit(0);

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
        return NGParser.analyzeAndValidate(config);
    }

    public static ComponentConfiguration analyzeAndValidate(String config) throws Exception {

        ComponentConfiguration compConf = Component.analyzeAndValidate(config);
        sncr.bda.conf.Parser parserProps = compConf.getParser();
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

    protected int archive(){
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
            .map(new ConvertToRow(schema, tsFormats, lineSeparator, delimiter, quoteChar, quoteEscapeChar, '\'', recCounter, errCounter));


        Dataset<Row> df = ctx.sparkSession.createDataFrame(rdd.rdd(), internalSchema);

        scala.collection.Seq<Column> scalaList=
            scala.collection.JavaConversions.asScalaBuffer(createFieldList(ctx.componentConfiguration.getParser().getFields())).toList();

        //TODO: Change here
        Dataset<Row> filteredDataset = df.select(scalaList).where(df.col("__REJ_FLAG").equalTo(0));
        writeDataset(filteredDataset, outputFormat.toString(), tempDir);
        ctx.resultDataDesc.add(new MoveDataDescriptor(tempDir, outputDataSetLocation, outputDataSetName, mode, outputFormat,pkeys));

        return 0;
    }


    // Parse data with headers - we have to do this file by file
    private int parseFiles(FileStatus[] files, String mode){
        // Files
        for (FileStatus file : files) {
            if (file.isFile()) {
                String tempPath = tempDir + Path.SEPARATOR + file.getPath().getName();
                if (parseSingleFile(file.getPath(), new Path(tempPath)) == 0) {
                   ctx.resultDataDesc.add(new MoveDataDescriptor(tempPath, outputDataSetLocation, outputDataSetName, mode, outputFormat,pkeys));
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
            .map(new ConvertToRow(schema, tsFormats, lineSeparator, delimiter, quoteChar, quoteEscapeChar, '\'', recCounter, errCounter));


        Dataset<Row> df = ctx.sparkSession.createDataFrame(rdd.rdd(), internalSchema);
        // TODO: Filter out all rejected records

        scala.collection.Seq<Column> scalaList=
            scala.collection.JavaConversions.asScalaBuffer(createFieldList(ctx.componentConfiguration.getParser().getFields())).toList();
        Dataset<Row> filteredDataset = df.select(scalaList).where(df.col("__REJ_FLAG").equalTo(0));

        return writeDataset(filteredDataset, outputFormat, destDir.toString());
    }

    /**
     *
     * Writes the given dataset with a given format in the specified path.
     *
     * @param dataset Input dataset which needs to be written
     * @param format Format in which data needs to be written
     * As of now, the supported data formats are
     * <ol>
     *     <li>json</li>
     *     <li>parquet</li>
     * </ol>
     * @param path Fully qualified path to which data needs to be written
     * @return
     * @throws XDFException
     */
    private int writeDataset(Dataset<Row> dataset, String format, String path) {
        try {
            XDFDataWriter xdfDW = new XDFDataWriter(format, outputNOF, pkeys);
            xdfDW.writeToTempLoc(dataset,  path);
            Map<String, Object> outputDS = ngctx.outputDataSets.get(outputDataSetName);
            outputDS.put(DataSetProperties.Schema.name(), xdfDW.extractSchema(dataset) );
            return 0;
        } catch (Exception e) {
            error = ExceptionUtils.getFullStackTrace(e);
            logger.error("Error at writing result: " + error);
            return -1;
        }

    }

    private static List<Column> createFieldList(List<Field> fields){

        List<Column> retval = new ArrayList<>(fields.size());
        for(Field field : fields){
            retval.add(new Column(field.getName()));
        }
        return retval;
    }

    private static List<String> createTsFormatList(List<Field> fields){
        List<String> retval = new ArrayList<>();
        for(Field field : fields){
            if (field.getType().equals(CsvInspectorRowProcessor.T_DATETIME) &&
            field.getFormat() != null && !field.getFormat().isEmpty()){
                retval.add(field.getFormat());
                logger.info("Found date field " + field.getName() + " format: " + field.getFormat());
            } else {
                retval.add("");
            }
        }
        return retval;
    }
    private static StructType createSchema(List<Field> fields, boolean addRejectedFlag){

        StructField[] structFields = new StructField[fields.size() + (addRejectedFlag ? 1 : 0)];
        int i = 0;
        for(Field field : fields){

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
