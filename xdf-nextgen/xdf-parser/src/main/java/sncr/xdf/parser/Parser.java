package sncr.xdf.parser;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.util.LongAccumulator;
import sncr.bda.conf.ComponentConfiguration;
import sncr.bda.conf.Field;
import sncr.bda.conf.Output;
import sncr.bda.core.file.HFileOperations;
import sncr.bda.datasets.conf.DataSetProperties;
import sncr.xdf.adapters.writers.DLBatchWriter;
import sncr.xdf.adapters.writers.MoveDataDescriptor;
import sncr.xdf.component.Component;
import sncr.xdf.component.WithDataSetService;
import sncr.xdf.component.WithMovableResult;
import sncr.xdf.component.WithSparkContext;
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
    private String archiveDir;

    private String outputDataSetName;
    private String outputDataSetLocation;
    private String outputFormat;
    private String outputDataSetMode;

    private String rejectedDatasetName;
    private String rejectedDatasetLocation;
    private String rejectedDataFormat;
    private String rejectedDataSetMode;

    private LongAccumulator errCounter;
    private LongAccumulator recCounter;

    private StructType schema;
    private List<String> tsFormats;
    private StructType internalSchema;
    private Integer outputNOF;

    public static final String REJECTED_FLAG = "__REJ_FLAG";
    public static final String REJ_REASON = "__REJ_REASON";

    private JavaRDD<Row> rejectedDataCollector;

    private List<String> outputDsPartitionKeys;

    {
        componentName = "parser";
    }

    public static void main(String[] args){
        Parser component = new Parser();
        try {
            // Spark based component
            if (component.collectCommandLineParameters(args) == 0) {
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
        tempDir = generateTempLocation(new DataSetServiceAux(ctx, md), null, null);
        archiveDir = generateArchiveLocation(new DataSetServiceAux(ctx, md));

        lineSeparator = ctx.componentConfiguration.getParser().getLineSeparator();
        delimiter = (ctx.componentConfiguration.getParser().getDelimiter() != null)? ctx.componentConfiguration.getParser().getDelimiter().charAt(0): ',';
        quoteChar = (ctx.componentConfiguration.getParser().getQuoteChar() != null)? ctx.componentConfiguration.getParser().getQuoteChar().charAt(0): '\'';
        quoteEscapeChar = (ctx.componentConfiguration.getParser().getQuoteEscape() != null)? ctx.componentConfiguration.getParser().getQuoteEscape().charAt(0): '\"';

        errCounter = ctx.sparkSession.sparkContext().longAccumulator("ParserErrorCounter");
        recCounter = ctx.sparkSession.sparkContext().longAccumulator("ParserRecCounter");

        schema = createSchema(ctx.componentConfiguration.getParser().getFields(), false, false);
        tsFormats = createTsFormatList(ctx.componentConfiguration.getParser().getFields());
        logger.info(tsFormats);

        internalSchema = createSchema(ctx.componentConfiguration.getParser().getFields(), true, true);

        // Output data set
        if (outputDataSets.size() == 0) {
            logger.error("Output dataset not defined");
            return -1;
        }

        Map<String, Object> outputDataset = getOutputDatasetDetails();
        logger.debug("Output dataset details = " + outputDataset);
        outputDataSetName = outputDataset.get(DataSetProperties.Name.name()).toString();
        outputDataSetLocation = outputDataset.get(DataSetProperties.PhysicalLocation.name()).toString();

        outputDataSetMode = outputDataset.get(DataSetProperties.Mode.name()).toString();
        logger.debug("Output dataset mode = " + outputDataSetMode);

        outputFormat = outputDataset.get(DataSetProperties.Format.name()).toString();
        outputNOF =  (Integer) outputDataset.get(DataSetProperties.NumberOfFiles.name());
        outputDsPartitionKeys = (List<String>) outputDataset.get(DataSetProperties.PartitionKeys.name());

        logger.info("Output data set " + outputDataSetName + " located at " + outputDataSetLocation + " with format " + outputFormat);

        Map<String, Object> rejDs = getRejectDatasetDetails();

        logger.debug("Rejected dataset details = " + rejDs);
        if (rejDs != null) {
//            rejectedDatasetName = DATASET.rejected.toString();
            rejectedDatasetName = rejDs.get(DataSetProperties.Name.name()).toString();
            rejectedDatasetLocation = rejDs.get(DataSetProperties.PhysicalLocation.name()).toString();
            rejectedDataFormat = rejDs.get(DataSetProperties.Format.name()).toString();
            rejectedDataSetMode = rejDs.get(DataSetProperties.Mode.name()).toString();

            logger.debug("Rejected dataset " + rejectedDatasetName + " at "
                + rejectedDatasetLocation + " with format " + rejectedDataFormat);

            if (rejectedDataFormat == null || rejectedDataFormat.length() == 0) {
                rejectedDataFormat = "parquet";
            }

            logger.info("Rejected data set " + rejectedDatasetName + " located at " + rejectedDatasetLocation
                + " with format " + rejectedDataFormat);
        }

        //TODO: If data set exists and flag is not append - error
        // This is good for UI what about pipeline? Talk to Suren


        // Check what sourcePath referring
        FileSystem fs = HFileOperations.getFileSystem();

        //new ConvertToRow(schema, tsFormats, lineSeparator, delimiter, quoteChar, quoteEscapeChar, '\'', recCounter, errCounter);
        //System.exit(0);

        try {
            if(headerSize >= 1) {
                logger.debug("Header present");
                FileStatus[] files = fs.globStatus(new Path(sourcePath));

                logger.debug("Total number of files in the directory = " + files.length);
                // Check if directory has been given
                if(files.length == 1 && files[0].isDirectory()){
                    logger.debug("Files length = 1 and is a directory");
                    // If so - we have to process all the files inside - create the mask
                    sourcePath += Path.SEPARATOR + "*";
                    // ... and query content
                    files = fs.globStatus(new Path(sourcePath));
                }
                retval = parseFiles(files,  outputDataSetMode);
            } else {
                logger.debug("No Header");
                retval = parse(outputDataSetMode);
            }
            //Write rejected data
            if (this.rejectedDataCollector != null) {
                boolean status = writeRejectedData();

                if (!status) {
                    logger.warn("Unable to write rejected data");
                }
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
        int result = 0;
        logger.info("Archiving source data at " + sourcePath + " to " + archiveDir);

        try {
            FileStatus[] files = ctx.fs.globStatus(new Path(sourcePath));

            if (files != null && files.length != 0) {
                //Create archive directory
                Path archivePath = new Path(archiveDir);
                ctx.fs.mkdirs(archivePath);

                logger.debug("Total files = " + files.length);

                int archiveCounter = 0;

                for(FileStatus status: files) {
                    logger.debug("Archiving " +  status.getPath() + " to " + archivePath);

                    if (archiveSingleFile(status.getPath(), archivePath)) {
                        archiveCounter++;
                    }
                }

                logger.info("Total files archived = " + archiveCounter);
            }
        } catch (IOException e) {
            logger.error("Archival failed");

            logger.error(ExceptionUtils.getStackTrace(e));

            result = 1;
        }

        return result;
    }

    private boolean archiveSingleFile(Path sourceFilePath, Path archiveLocation) throws
        IOException {
        return ctx.fs.rename(sourceFilePath, archiveLocation);
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

        JavaRDD<String> rdd = new JavaSparkContext(ctx.sparkSession.sparkContext())
            .textFile(sourcePath, 1);

        JavaRDD<Row> parsedRdd = rdd.map(
            new ConvertToRow(schema, tsFormats, lineSeparator, delimiter, quoteChar, quoteEscapeChar,
                '\'', recCounter, errCounter));
        // Create output dataset
        scala.collection.Seq<Column> outputColumns =
            scala.collection.JavaConversions.asScalaBuffer(createFieldList(ctx.componentConfiguration.getParser().getFields())).toList();
        JavaRDD<Row> outputRdd = getOutputData(parsedRdd);
        Dataset<Row> outputDataset = ctx.sparkSession.createDataFrame(outputRdd.rdd(), internalSchema).select(outputColumns);

        boolean status = writeDataset(outputDataset, outputFormat, tempDir);

        if (!status) {
            return -1;
        }

        status = collectRejectedData(parsedRdd, outputRdd);
        if (!status) {
            logger.error("Failed to write rejected data");
        }

        resultDataDesc.add(new MoveDataDescriptor(tempDir, outputDataSetLocation, outputDataSetName, mode, outputFormat, outputDsPartitionKeys));

        return 0;
    }


    // Parse data with headers - we have to do this file by file
    private int parseFiles(FileStatus[] files, String mode){
        // Files

        for (FileStatus file : files) {
            if (file.isFile()) {
                String tempPath = tempDir + Path.SEPARATOR + file.getPath().getName();



                int retVal = parseSingleFile(file.getPath(), new Path(tempPath));
                if (retVal == 0) {
                    resultDataDesc.add(new MoveDataDescriptor(tempPath, outputDataSetLocation, outputDataSetName, mode, outputFormat, outputDsPartitionKeys));
                } else {
                    return retVal;
                }
            }
        }
        return 0;
    }

    private int parseSingleFile(Path file, Path destDir){
        logger.info("Parsing " + file + " to " + destDir);
        logger.info("Header size : " + headerSize);

        JavaRDD<String> rdd = new JavaSparkContext(ctx.sparkSession.sparkContext())
            .textFile(file.toString(), 1);

        JavaRDD<Row> parseRdd = rdd
            // Add line numbers
            .zipWithIndex()
            // Filter out header based on line number
            .filter(new HeaderFilter(headerSize))
            // Get rid of file numbers
            .keys()
            .map(new ConvertToRow(schema, tsFormats, lineSeparator, delimiter, quoteChar, quoteEscapeChar, '\'', recCounter, errCounter));

        // Create output dataset
        scala.collection.Seq<Column> outputColumns =
            scala.collection.JavaConversions.asScalaBuffer(createFieldList(ctx.componentConfiguration.getParser().getFields())).toList();
        JavaRDD<Row> outputRdd = getOutputData(parseRdd);
        Dataset<Row> df = ctx.sparkSession.createDataFrame(outputRdd.rdd(), internalSchema).select(outputColumns);

        logger.debug("Dest dir for file " + file + " = " + destDir);

        boolean status = writeDataset(df, outputFormat, destDir.toString());

        logger.debug("Write dataset status = " + status);

        if (!status) {
            return -1;
        }

        collectRejectedData(parseRdd, outputRdd);

        return 0;
    }

    /**
     * Extract all the rejected records from the full rdd and write it in the specified location.
     *
     * @param fullRdd Complete RDD which contains both output and rejected records
     * @param outputRdd Contains output records
     * @return
     *      true - if write is successful
     *      false - if location is not specified or write operation fails
     *
     */

    private boolean collectRejectedData(JavaRDD<Row> fullRdd, JavaRDD<Row> outputRdd) {
        boolean status = true;

        try {
            // Get all entries which are rejected
            logger.debug("Collecting rejected data");
            JavaRDD<Row> rejectedRdd = fullRdd.subtract(outputRdd)
                .map(record ->
                    RowFactory.create(record.get(0), record.get(record.length() - 1)));

            if (this.rejectedDataCollector == null) {
                rejectedDataCollector = rejectedRdd;
            } else {
                rejectedDataCollector = rejectedDataCollector.union(rejectedRdd);
            }
        } catch (Exception exception) {
            logger.error(exception);
            logger.debug(ExceptionUtils.getStackTrace(exception));
            status = false;
        }

        return status;
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
    private boolean writeDataset(Dataset<Row> dataset, String format, String path) {
        try {
            DLBatchWriter xdfDW = new DLBatchWriter(format, outputNOF, outputDsPartitionKeys);
            xdfDW.writeToTempLoc(dataset,  path);
            Map<String, Object> outputDS = outputDataSets.get(outputDataSetName);
            outputDS.put(DataSetProperties.Schema.name(), xdfDW.extractSchema(dataset) );
            outputDS.put(DataSetProperties.RecordCount.name(), xdfDW.extractrecordCount(dataset));
            return true;
        } catch (Exception e) {
            error = ExceptionUtils.getFullStackTrace(e);
            logger.error("Error at writing result: " + error);
            return false;
        }
    }

    private boolean writeRdd(JavaRDD rdd, String path) {
        if (rdd != null && path != null && !rdd.isEmpty()) {
            logger.debug("Writing data to location " + path);
            rdd.coalesce(1).saveAsTextFile(path);
        } else {
            logger.info("Nothing to write");
        }

        return true;
    }

    private boolean writeRejectedData () {
        boolean status = true;

        logger.debug("Writing rejected data in the end");

        try {
//            JavaRDD<String> rejectedRecords = rejectedDataCollector.map(row -> row.mkString(" | "));

            JavaRDD<String> rejectedRecords = rejectedDataCollector.map(new TransformRDDWithDelimiter(this.delimiter));
            if (rejectedDatasetLocation != null) {
                if (rejectedDataSetMode.equalsIgnoreCase(Output.Mode.APPEND.toString())) {
                    /**
                     * Check if rejected data already exists.
                     * If yes, read the existing data from the rejected directory and created a rdd
                     * Merge the existing dataset with current dataset
                     *
                     * If not, write the current rejected dataset
                     *
                     */
                    if (HFileOperations.exists(rejectedDatasetLocation)) {
                        JavaRDD<String> existingData = this.ctx.sparkSession
                            .sparkContext()
                            .textFile(rejectedDatasetLocation, 1).toJavaRDD();

                        if (existingData != null && !existingData.isEmpty()) {
                            rejectedRecords = existingData.union(rejectedRecords);
                        }
                    }

                } else  {
                    /**
                     * Mode is replace by default
                     * Check if rejected data already exists.
                     * If yes, delete the existing data from the rejected location
                     * Write the current data into the rejected location
                     *
                     * If no, write the current data into the rejected location
                     */
//                    if (HFileOperations.exists(rejectedDatasetLocation)) {
//                        logger.debug("Deleting existing rejected records");
//                        HFileOperations.deleteEnt(rejectedDatasetLocation);
//                    }

                }

                logger.debug("Writing rejected data to temp directory " + rejectedDatasetLocation
                    + "temp");
                writeRdd(rejectedRecords, this.rejectedDatasetLocation + "temp");


                if (HFileOperations.exists(this.rejectedDatasetLocation)) {
                    logger.debug("Deleting existing rejected records");
                    HFileOperations.deleteEnt(this.rejectedDatasetLocation);
                }

                this.ctx.fs.rename(new Path(this.rejectedDatasetLocation + "temp"),
                    new Path(this.rejectedDatasetLocation));
            }
        } catch (Exception exception) {
            logger.error("Error occurred while writing rejected records");
            logger.error(exception);

            status = false;
        }



        return status;
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
    private static StructType createSchema(List<Field> fields, boolean addRejectedFlag,boolean addReasonFlag){

        StructField[] structFields = new StructField[fields.size() + (addRejectedFlag ? 1 : 0)
            + (addReasonFlag ? 1 : 0)];
        int i = 0;
        for(Field field : fields){

            StructField structField = new StructField(field.getName(), convertXdfToSparkType(field.getType()), true, Metadata.empty());
            structFields[i] = structField;
            i++;
        }

        if(addRejectedFlag){
            structFields[i] = new StructField(REJECTED_FLAG, DataTypes.IntegerType, true, Metadata.empty());
        }
        if (addReasonFlag) {
            structFields[i+1] = new StructField(REJ_REASON, DataTypes.StringType, true, Metadata.empty());
        }

        return  new StructType(structFields);
    }

    private Map<String, Object> getOutputDatasetDetails() {
        Map<String, Object> outputDataset = null;

        logger.info("Outputs = " + outputs);
        logger.info("Output DS = " + outputDataSets);

        outputDataset = outputs.get(DATASET.output.toString());
        logger.debug("Output dataset = " + outputDataset);

        return outputDataset;
    }

    private Map<String, Object> getRejectDatasetDetails() {
        Map<String, Object> rejectDataset = null;

        rejectDataset = outputs.get(DATASET.rejected.toString());
        logger.debug("Rejected dataset = " + rejectDataset);

        return rejectDataset;
    }

    private JavaRDD<Row> getOutputData (JavaRDD<Row> parsedData) {
        int rejectedColumn = internalSchema.length() - 2;
        JavaRDD<Row> outputRdd = parsedData.filter(row -> (int)row.get(rejectedColumn) == 0);

        return outputRdd;
    }

    private static DataType convertXdfToSparkType(String xdfType){
        switch(xdfType){
            case CsvInspectorRowProcessor.T_STRING:
                return DataTypes.StringType;
            case CsvInspectorRowProcessor.T_LONG:
                return DataTypes.LongType;
            case CsvInspectorRowProcessor.T_DOUBLE:
                return DataTypes.DoubleType;
            case CsvInspectorRowProcessor.T_INTEGER:
                return DataTypes.IntegerType;
            case CsvInspectorRowProcessor.T_DATETIME:
                return DataTypes.TimestampType;  // TODO: Implement proper format for timestamp
            default:
                return DataTypes.StringType;

        }
    }
}

class TransformRDDWithDelimiter implements Function<Row, String> {
    final char delimiter;

    TransformRDDWithDelimiter(char delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    public String call(Row row) {
        return row.mkString(String.valueOf(delimiter));
    }
}
