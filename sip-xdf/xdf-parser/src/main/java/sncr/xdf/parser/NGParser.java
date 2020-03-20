package sncr.xdf.parser;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.*;
import org.apache.spark.util.LongAccumulator;
import sncr.bda.CliHandler;
import sncr.bda.ConfigLoader;
import sncr.bda.base.MetadataBase;
import sncr.bda.conf.ComponentConfiguration;
import sncr.bda.conf.Field;
import sncr.bda.conf.Output;
import sncr.bda.core.file.HFileOperations;
import sncr.bda.datasets.conf.DataSetProperties;
import sncr.xdf.adapters.writers.MoveDataDescriptor;
import sncr.xdf.context.ComponentServices;
import sncr.xdf.context.NGContext;
import sncr.xdf.parser.parsers.NGJsonFileParser;
import sncr.xdf.parser.parsers.NGParquetFileParser;
import sncr.xdf.exceptions.XDFException;
import sncr.xdf.ngcomponent.*;
import sncr.xdf.parser.spark.ConvertToRow;
import sncr.xdf.preview.CsvInspectorRowProcessor;
import sncr.xdf.services.NGContextServices;
import sncr.xdf.services.WithDataSet;
import sncr.xdf.services.WithProjectScope;
import sncr.xdf.parser.spark.HeaderFilter;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.util.*;

import sncr.xdf.context.RequiredNamedParameters;
import sncr.bda.conf.ParserInputFileFormat;
import sncr.xdf.context.XDFReturnCode;
import sncr.xdf.ngcomponent.util.NGComponentUtil;
import sncr.bda.conf.PivotFields;
import sncr.xdf.parser.spark.Pivot;
import sncr.xdf.parser.spark.Flattener;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;
import org.apache.spark.sql.Encoders;
import sncr.xdf.parser.spark.Flattener;
import static org.apache.spark.sql.functions.from_json;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder;
import scala.Tuple2;

public class NGParser extends AbstractComponent implements WithDLBatchWriter, WithSpark, WithDataSet, WithProjectScope {

    private static final Logger logger = Logger.getLogger(NGParser.class);

    private ParserInputFileFormat parserInputFileFormat;
    private String lineSeparator;
    private char delimiter;
    private char quoteChar;
    private char quoteEscapeChar;
    private int headerSize;
    private int fieldDefRowNumber;
    private String sourcePath;
    private String tempDir;
    private String archiveDir;
    private boolean multiLine = false;

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

    private List<String> pkeys;

    public static final String REJECTED_FLAG = "__REJ_FLAG";
    public static final String REJ_REASON = "__REJ_REASON";

    private JavaRDD<Row> rejectedDataCollector;
    private JavaRDD<Row> acceptedDataCollector;

    private boolean isRealTime;
    private long inputDSCount = 0;
    private PivotFields pivotFields;
    private boolean isFlatteningEnabled;
    private boolean isPivotApplied;
    private boolean allowInconsistentCol;
    private boolean isSchemaContainsJsonType;
    private DataSetHelper datasetHelper = null;
    private Flattener flattner = null;
    private Map<String, Tuple2<Integer, Object>> fieldDefaultValuesMap = null;
    private boolean isSkipFieldsEnabled;

    private static final String DEFAULT_DATE_FORMAT = "dd/MM/yy HH:mm:ss";

    public NGParser(NGContext ngctx, ComponentServices[] cs) { super(ngctx, cs); }

    {
        componentName = "parser";
    }

    public NGParser(NGContext ngctx,String mode) {  super(ngctx); }

    public NGParser(NGContext ngctx) {
        super(ngctx);
    }

    public NGParser(NGContext ngctx,  Dataset dataset) {
        super( ngctx, dataset);
        this.inputDataFrame = dataset;
        logger.debug("Parser constructor with dataset "+ dataset);
        logger.debug(":::: parser constructor services parser :::"+ ngctx.componentConfiguration.getParser());
    }

    public NGParser(NGContext ngctx, Dataset<Row> dataset, boolean isRealTime) {

        super(ngctx);
        this.isRealTime = isRealTime;
        this.inputDataFrame = dataset;
        logger.debug("************ Inside Parser with real time ***********");
    }

    public NGParser() {  super(); }


    @SuppressWarnings("unchecked")
    @Override
    protected int execute(){

        logger.debug(":::: parser execute   :::"+ ngctx.componentConfiguration.getParser());
        int retval = 0;

        parserInputFileFormat = ngctx.componentConfiguration.getParser().getParserInputFileFormat();
        sourcePath = ngctx.componentConfiguration.getParser().getFile();

        datasetHelper = new DataSetHelper(ngctx, services.md);
        tempDir = generateTempLocation(datasetHelper, null, null);
        archiveDir = generateArchiveLocation(datasetHelper);

        Map<String, Object> outputDataset = getOutputDatasetDetails();
        logger.debug("Output dataset details = " + outputDataset);
        outputDataSetName = outputDataset.get(DataSetProperties.Name.name()).toString();
        outputDataSetLocation = outputDataset.get(DataSetProperties.PhysicalLocation.name()).toString();

        outputDataSetMode = outputDataset.get(DataSetProperties.Mode.name()).toString();
        logger.debug("Output dataset mode = " + outputDataSetMode);

        outputFormat = outputDataset.get(DataSetProperties.Format.name()).toString();
        outputNOF =  (Integer) outputDataset.get(DataSetProperties.NumberOfFiles.name());
        pkeys = (List<String>) outputDataset.get(DataSetProperties.PartitionKeys.name());
        errCounter = ctx.sparkSession.sparkContext().longAccumulator("ParserErrorCounter");
        recCounter = ctx.sparkSession.sparkContext().longAccumulator("ParserRecCounter");
        allowInconsistentCol = ngctx.componentConfiguration.getParser().getAllowInconsistentColumn();

        logger.info("Input file format = " + this.parserInputFileFormat);

        if (this.inputDataFrame == null) {
            try {
                logger.info("pkeys ::"+pkeys);
                if (pkeys != null && pkeys.size() <= 0) {
                    logger.info("checking pkeys" + pkeys);
                    logger.info("outputlocation" + outputDataSetLocation);
                    logger.info("replace".equalsIgnoreCase(outputDataSetMode));
                    logger.info(HFileOperations.exists(outputDataSetLocation));
                    if ("replace".equalsIgnoreCase(outputDataSetMode)
                        && HFileOperations.exists(outputDataSetLocation)) {
                        logger.info(" Deleting outputDataSetLocation  = " + outputDataSetMode + " for "
                            + outputDataSetMode);
                        HFileOperations.deleteEnt(outputDataSetLocation);
                    }
                }

                FileSystem fs = HFileOperations.getFileSystem();
                logger.debug("Input Source Path = " + sourcePath);
                Path inputPath = new Path(sourcePath);
                FileStatus[] files = fs.globStatus(inputPath);
                if(files != null && files.length == 1 && files[0].isDirectory()){
                    logger.debug("Files length = 1 and is a directory");
                    // If so - we have to process all the files inside - create the mask
                    sourcePath += Path.SEPARATOR + "*";
                    // ... and query content
                    files = fs.globStatus(new Path(sourcePath));
                }
                if(files == null || files.length == 0){
                    logger.debug("No files exist. files = " + files);
                    throw new XDFException(XDFReturnCode.FILE_NOT_FOUND, "parser input file - " +  sourcePath);
                }
            } catch (Exception e) {
                if (e instanceof XDFException) {
                    throw ((XDFException)e);
                }else {
                    String error = "Error while deletion of outputDataSetLocation " + outputDataSetLocation;
                    logger.error(error);
                    throw new XDFException(XDFReturnCode.INTERNAL_ERROR, e, error);
                }
            }
        }

        pivotFields = ngctx.componentConfiguration.getParser().getPivotFields();
        isPivotApplied = (pivotFields != null);
        isFlatteningEnabled = ngctx.componentConfiguration.getParser().isFlatteningEnabled();

        if (this.inputDataFrame == null && parserInputFileFormat.equals(ParserInputFileFormat.CSV)) {
            logger.debug("format csv");
            logger.debug("#####Component config:: " + ngctx.componentConfiguration);
            logger.debug("#####Component config parser :: " +ngctx.componentConfiguration.getParser());
            headerSize = ngctx.componentConfiguration.getParser().getHeaderSize();
            logger.debug("header size"+ headerSize);
            fieldDefRowNumber = ngctx.componentConfiguration.getParser().getFieldDefRowNumber();
            logger.debug("fieldDefRowNumber : "+ fieldDefRowNumber);
            lineSeparator = ngctx.componentConfiguration.getParser().getLineSeparator();
            logger.debug("lineSeparator"+ lineSeparator);
            delimiter = (ngctx.componentConfiguration.getParser().getDelimiter() != null)? ngctx.componentConfiguration.getParser().getDelimiter().charAt(0): ',';
            logger.debug("delimiter"+ delimiter);
            quoteChar = (ngctx.componentConfiguration.getParser().getQuoteChar() != null)? ngctx.componentConfiguration.getParser().getQuoteChar().charAt(0): '\'';
            logger.debug("quoteChar"+ quoteChar);
            quoteEscapeChar = (ngctx.componentConfiguration.getParser().getQuoteEscape() != null)? ngctx.componentConfiguration.getParser().getQuoteEscape().charAt(0): '\"';
            logger.debug("quoteEscapeChar"+ quoteEscapeChar);
            tsFormats = createTsFormatList(ngctx.componentConfiguration.getParser().getFields());
            logger.debug("tsFormats"+ tsFormats);
            isSchemaContainsJsonType = isSchemaContainsJsonType(ngctx.componentConfiguration.getParser().getFields());
            logger.info("***** isSchemaContainsJsonType : "+ isSchemaContainsJsonType);
            // Output data set
            if (ngctx.outputDataSets.size() == 0) {
                logger.error("Output dataset not defined");
                return -1;
            }
            logger.debug("Output data set " + outputDataSetName + " located at " + outputDataSetLocation + " with format " + outputFormat);
            Map<String, Object> rejDs = getRejectDatasetDetails();
            logger.debug("Rejected dataset details = " + rejDs);
            if (rejDs != null) {
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

            // Check what sourcePath referring
            FileSystem fs = HFileOperations.getFileSystem();

            try {

                if (ctx.fs.exists(new Path(tempDir)))
                    HFileOperations.deleteEnt(tempDir);


                if (inputDataFrame !=null) {
                    this.recCounter.setValue(inputDataFrame.count());
                    retval = parseDataFrame(inputDataFrame, new Path(tempDir));
                }
                // This block has been added to support DF in Parser
                // SIP-7758
                else {
                    if (headerSize >= 1) {
                        logger.debug("Header present");
                        FileStatus[] files = fs.globStatus(new Path(sourcePath));

                        if (files != null) {
                            logger.debug("Total number of files in the directory = " + files.length);
                        }
                        // Check if directory has been given
                        if (files != null && files.length == 1 && files[0].isDirectory()) {
                            logger.debug("Files length = 1 and is a directory");
                            // If so - we have to process all the files inside - create the mask
                            sourcePath += Path.SEPARATOR + "*";
                            // ... and query content
                            files = fs.globStatus(new Path(sourcePath));
                        }
                        if(isPivotApplied || isFlatteningEnabled || isSchemaContainsJsonType){
                            retval = parseAndUnionFiles(files, outputDataSetMode);
                        }else{
                            retval = parseFiles(files, outputDataSetMode);
                        }
                    } else {
                        logger.debug("No Header");
                        retval = parse(outputDataSetMode);
                    }
                }
                if(!isPivotApplied && !isFlatteningEnabled && !isSchemaContainsJsonType) {
                    //Write Consolidated Accepted data
                    if (this.acceptedDataCollector != null) {
                        scala.collection.Seq<Column> outputColumns =
                            scala.collection.JavaConversions.asScalaBuffer(
                                createFieldList(ngctx.componentConfiguration.getParser().getFields())).toList();

                        Dataset outputDS = ctx.sparkSession.createDataFrame(acceptedDataCollector.rdd(), internalSchema).select(outputColumns);

                        ngctx.datafileDFmap.put(ngctx.dataSetName, outputDS.cache());
                        //TODO: SIP-9791 - The count statements are executed even when it is logger.debug mode.
                        //TODO: This is a crude way of checking. This need to be revisited.
                        if (logger.isDebugEnabled()) {
                            logger.debug("####### end of parser after caching " + outputDS.count());
                        }
                    }
                }

                //Write rejected data
                if (this.rejectedDataCollector != null) {
                    boolean status = writeRejectedData();

                    if (!status) {
                        logger.warn("Unable to write rejected data");
                    }
                }
            }catch (Exception e) {
                logger.error("Exception in parser module: ",e);
                if (e instanceof XDFException) {
                    throw ((XDFException)e);
                }else {
                    throw new XDFException(XDFReturnCode.INTERNAL_ERROR, e);
                }
            }
        }
        else if (this.inputDataFrame == null && parserInputFileFormat.equals(ParserInputFileFormat.JSON))
        {
            NGJsonFileParser jsonFileParser = new NGJsonFileParser(ctx);

            Dataset<Row> inputDataset = null;
            multiLine = ngctx.componentConfiguration.getParser().isMultiLine();

            logger.debug("NGJsonFileParser ==> multiLine  value is  " + multiLine + "\n");
            inputDataset = jsonFileParser.parseInput(sourcePath,multiLine);
            inputDSCount = inputDataset.count();
            this.recCounter.setValue(inputDSCount);
            //This will throw an error if Dataset is Empty
            if(ngctx.componentConfiguration.isErrorHandlingEnabled() && inputDSCount == 0){
                throw new XDFException(XDFReturnCode.INPUT_DATA_EMPTY_ERROR, sourcePath);
            }
            inputDataset = pivotOrFlattenDataset(inputDataset);
            logger.debug("Final DS Schema : "+ inputDataset.schema());
            commitDataSetFromDSMap(ngctx, inputDataset, outputDataSetName, tempDir, Output.Mode.APPEND.name());

            ctx.resultDataDesc.add(new MoveDataDescriptor(tempDir, outputDataSetLocation,
                outputDataSetName, outputDataSetMode, outputFormat, pkeys));
            ngctx.datafileDFmap.put(ngctx.dataSetName,inputDataset.cache());
        }
        else if (this.inputDataFrame == null && parserInputFileFormat.equals(ParserInputFileFormat.PARQUET))
        {
            NGParquetFileParser parquetFileParser = new NGParquetFileParser(ctx);
            Dataset<Row> inputDataset = null;

            if (inputDataFrame != null) {
                inputDataset = inputDataFrame;
            } else {
                inputDataset = parquetFileParser.parseInput(sourcePath);
            }
            inputDSCount = inputDataset.count();
            this.recCounter.setValue(inputDSCount);
            if(ngctx.componentConfiguration.isErrorHandlingEnabled() && inputDSCount == 0){
                throw new XDFException(XDFReturnCode.INPUT_DATA_EMPTY_ERROR, sourcePath);
            }
            inputDataset = pivotOrFlattenDataset(inputDataset);
            logger.debug("Final DS Schema : "+ inputDataset.schema());
            commitDataSetFromDSMap(ngctx, inputDataset, outputDataSetName, tempDir, "append");

            ctx.resultDataDesc.add(new MoveDataDescriptor(tempDir, outputDataSetLocation,
                outputDataSetName, outputDataSetMode, outputFormat, pkeys));
            ngctx.datafileDFmap.put(ngctx.dataSetName,inputDataset.cache());
        }
        else if(this.inputDataFrame != null)
        {
            inputDSCount = inputDataFrame.count();
            this.recCounter.setValue(inputDSCount);
            if(ngctx.componentConfiguration.isErrorHandlingEnabled() && inputDSCount == 0){
                throw new XDFException(XDFReturnCode.INPUT_DATA_EMPTY_ERROR, "");
            }

            inputDataFrame = pivotOrFlattenDataset(inputDataFrame);
            logger.debug("Final DS Schema : "+ inputDataFrame.schema());
            commitDataSetFromDSMap(ngctx, inputDataFrame, outputDataSetName, tempDir, Output.Mode.APPEND.name());

            ctx.resultDataDesc.add(new MoveDataDescriptor(tempDir, outputDataSetLocation,
                outputDataSetName, outputDataSetMode, outputFormat, pkeys));
            ngctx.datafileDFmap.put(ngctx.dataSetName,inputDataFrame.cache());
        }

        //TODO: SIP-9791 - The count statements are executed even when it is logger.debug mode.
        //TODO: This is a crude way of checking. This need to be revisited.
        if(
            //logger.isDebugEnabled() &&
            ngctx.datafileDFmap.get(ngctx.dataSetName) != null) {
            logger.debug("Count for parser in dataset :: " + ngctx.dataSetName + ngctx.datafileDFmap.get(ngctx.dataSetName).count());
        }
        logger.debug("NGParser ==>  dataSetName  & size " + outputDataSetName + "," + ngctx.datafileDFmap.size()+ "\n");
        validateOutputDSCounts(inputDSCount, isPivotApplied);
        return retval;
    }


    public static ComponentConfiguration analyzeAndValidate(String config) throws Exception {

        ComponentConfiguration compConf = AbstractComponent.analyzeAndValidate(config);
        sncr.bda.conf.Parser parserProps = compConf.getParser();
        if (parserProps == null) {
            throw new XDFException( XDFReturnCode.INVALID_CONF_FILE);
        }

        if(parserProps.getFile() == null || parserProps.getFile().length() == 0){
            throw new XDFException(XDFReturnCode.INVALID_CONF_FILE);
        }

        return compConf;
    }

    protected int archive() {
        int result = 0;

        if (!this.isRealTime) {

            logger.info("Archiving source data at " + sourcePath + " to " + archiveDir);

            try {
                FileStatus[] files = ctx.fs.globStatus(new Path(sourcePath));

                if (files != null && files.length != 0) {
                    // Create archive directory

                    logger.debug("Total files = " + files.length);

                    int archiveCounter = 0;
                    String currentTimestamp = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss.SSS"));

                    Path archivePath = new Path(
                        archiveDir + Path.SEPARATOR + currentTimestamp + "_" + UUID.randomUUID() + Path.SEPARATOR);
                    ctx.fs.mkdirs(archivePath);
                    logger.debug("Archive directory " + archivePath);

                    for (FileStatus fiile : files) {

                        if (archiveSingleFile(fiile.getPath(), archivePath)) {
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
        }

        return result;
    }


    private boolean archiveSingleFile(Path sourceFilePath, Path archiveLocation) throws
        IOException {
        return ctx.fs.rename(sourceFilePath, archiveLocation);
    }

    // Parse data without headers
    int parse(String mode){

        logger.info("Parsing " + sourcePath + " to " + tempDir);
        logger.info("Header size : " + headerSize);
        // Keep the minimum partition as same as number of files (outputNOF) requested for output.
        // Spark also optimize and handle the number of partition automatically based on input
        // data and number of executor configured. This will also avoid the repartitioning of
        // Dataset later on to insure output number of files.
        JavaRDD<String> rdd = new JavaSparkContext(ctx.sparkSession.sparkContext())
            .textFile(sourcePath, outputNOF);
        logger.debug("Source Rdd partition : "+ rdd.getNumPartitions());
        inputDSCount = rdd.count();
        if(ngctx.componentConfiguration.isErrorHandlingEnabled() && inputDSCount == 0){
            throw new XDFException(XDFReturnCode.INPUT_DATA_EMPTY_ERROR, sourcePath);
        }
        createSchema(ngctx.componentConfiguration.getParser().getFields(), Optional.empty());
        logger.debug("schema"+ schema);
        logger.debug("internalSchema"+internalSchema);

        JavaRDD<Row> parsedRdd = rdd.map(
            new ConvertToRow(schema, tsFormats, lineSeparator, delimiter, quoteChar, quoteEscapeChar,
                '\'', recCounter, errCounter, allowInconsistentCol
                , fieldDefaultValuesMap, isSkipFieldsEnabled));

        logger.debug("Output rdd length = " + recCounter.value());
        logger.debug("Rejected rdd length = " + errCounter.value());

        JavaRDD<Row> outputRdd = getOutputData(parsedRdd);
        int status = 0 ;
        logger.debug("Rdd partition : "+ outputRdd.getNumPartitions());

        scala.collection.Seq<Column> outputColumns =
            scala.collection.JavaConversions.asScalaBuffer(
                createFieldList(ngctx.componentConfiguration.getParser().getFields())).toList();

        Dataset<Row> outputDataset = ctx.sparkSession.createDataFrame(outputRdd.rdd(), internalSchema).select(outputColumns);

        logger.debug("Dataset partition : "+ outputDataset.rdd().getNumPartitions());
        outputDataset = convertJsonStringColToStruct(outputDataset, ngctx.componentConfiguration.getParser().getFields());
        Dataset<Row> outputDS = pivotOrFlattenDataset(outputDataset);
        logger.debug("Final DS Schema : "+ outputDS.schema());
        status = commitDataSetFromDSMap(ngctx, outputDS, outputDataSetName, tempDir.toString(), "append");

        if(isPivotApplied || isFlatteningEnabled) {
            ngctx.datafileDFmap.put(ngctx.dataSetName, outputDS.cache());
        }else{
            collectAcceptedData(parsedRdd, outputRdd);
        }

        if (status != 0) {
            return -1;
        }

        boolean rejectedStatus = collectRejectedData(parsedRdd, outputRdd);
        if (status != 0 || !rejectedStatus) {
            logger.error("Failed to write rejected data");
        }

        ctx.resultDataDesc.add(new MoveDataDescriptor(tempDir, outputDataSetLocation, outputDataSetName, mode, outputFormat, pkeys));

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
                    ctx.resultDataDesc.add(new MoveDataDescriptor(tempPath, outputDataSetLocation, outputDataSetName, mode, outputFormat, pkeys));
                } else {
                    return retVal;
                }
            }
        }
        if(ngctx.componentConfiguration.isErrorHandlingEnabled() && inputDSCount == 0){
            throw new XDFException(XDFReturnCode.INPUT_DATA_EMPTY_ERROR, sourcePath);
        }
        return 0;
    }


    private int parseSingleFile(Path file, Path destDir){
        logger.trace("Parsing " + file + " to " + destDir +"\n");
        logger.trace("Header size : " + headerSize +"\n");

        JavaRDD<String> rdd = null;

        if(this.ctx.extSparkCtx) {

            logger.debug("##### Using existing JavaSparkContext ...");
            rdd = this.ctx.javaSparkContext
                .textFile(file.toString(), 1);
        } else {
            logger.debug("##### Crating new JavaSparkContext ...");
            JavaSparkContext context  = new JavaSparkContext(ctx.sparkSession.sparkContext());
            rdd = context
                .textFile(file.toString(), 1);
        }

        // Add line numbers
        JavaPairRDD<String, Long> zipIndexRdd = rdd.zipWithIndex();

        String headerLine = NGComponentUtil.getLineFromRdd(zipIndexRdd, headerSize, fieldDefRowNumber);
        createSchema(ngctx.componentConfiguration.getParser().getFields(), Optional.ofNullable(headerLine));
        logger.debug("schema"+ schema);
        logger.debug("internalSchema"+internalSchema);

        // Filter out header based on line number
        JavaRDD<String> rddWithoutHeader =  zipIndexRdd.filter(new HeaderFilter(headerSize))
            // Get rid of file numbers
            .keys();
        long rddCount = rddWithoutHeader.count();
        logger.info("RDD Count is : " + rddCount);
        int rc = 0;
        if(!ngctx.componentConfiguration.isErrorHandlingEnabled() || rddCount > 0) {
            inputDSCount += rddCount;
            JavaRDD<Row> parseRdd = rddWithoutHeader.map(new ConvertToRow(schema, tsFormats, lineSeparator, delimiter, quoteChar,
                quoteEscapeChar, '\'', recCounter, errCounter, allowInconsistentCol
                , fieldDefaultValuesMap, isSkipFieldsEnabled));
            // Create output dataset
            JavaRDD<Row> rejectedRdd = getRejectedData(parseRdd);
            logger.debug("####### Rejected RDD COUNT:: " + rejectedRdd.count());
            JavaRDD<Row> outputRdd = getOutputData(parseRdd);
            scala.collection.Seq<Column> outputColumns =
                scala.collection.JavaConversions.asScalaBuffer(
                    createFieldList(ngctx.componentConfiguration.getParser().getFields())).toList();

            Dataset<Row> df = ctx.sparkSession.createDataFrame(outputRdd.rdd(), internalSchema).select(outputColumns);

            logger.debug("Output rdd length = " + recCounter.value() + "\n");
            logger.debug("Rejected rdd length = " + errCounter.value() + "\n");
            logger.debug("Dest dir for file " + file + " = " + destDir + "\n");

            rc = commitDataSetFromDSMap(ngctx, df, outputDataSetName, destDir.toString(), Output.Mode.APPEND.toString());
            logger.debug("************************************** Dest dir for file " + file + " = " + destDir + "\n");

            logger.debug("Write dataset status = " + rc);

            //Filter out Accepted Data
            collectAcceptedData(parseRdd, outputRdd);

            logger.debug("Write dataset status = " + rc);

            //Filter out Rejected Data
            collectRejectedData(parseRdd, outputRdd);
        }
        return rc;
    }

    private int parseDataFrame(Dataset<String> dataFrame, Path destDir){
        logger.debug("parsing dataframe starts here");
        logger.debug("Headersize is: " + headerSize);
        JavaRDD<String> rdd = dataFrame.rdd().toJavaRDD();
        JavaRDD<String> rddWithoutHeader = null;
        String headerLine = null;
        if (headerSize >= 1) {
            // Add line numbers
            JavaPairRDD<String, Long> zipIndexRdd = rdd.zipWithIndex();
            headerLine = NGComponentUtil.getLineFromRdd(zipIndexRdd, headerSize, fieldDefRowNumber);
            rddWithoutHeader = zipIndexRdd
                // Filter out header based on line number
                .filter(new HeaderFilter(headerSize))
                // Get rid of file numbers
                .keys();
        } else {
            rddWithoutHeader = rdd;
        }
        createSchema(ngctx.componentConfiguration.getParser().getFields(), Optional.ofNullable(headerLine));
        logger.debug("schema"+ schema);
        logger.debug("internalSchema"+internalSchema);

        if(ngctx.componentConfiguration.isErrorHandlingEnabled() && rddWithoutHeader.count() == 0){
            throw new XDFException(XDFReturnCode.INPUT_DATA_EMPTY_ERROR, sourcePath);
        }
        JavaRDD<Row>  parseRdd = rddWithoutHeader.map(new ConvertToRow(schema, tsFormats, lineSeparator, delimiter, quoteChar, quoteEscapeChar,
            '\'', recCounter, errCounter, allowInconsistentCol
            , fieldDefaultValuesMap, isSkipFieldsEnabled));
        // Create output dataset
        scala.collection.Seq<Column> outputColumns =
            scala.collection.JavaConversions.asScalaBuffer(
                createFieldList(ngctx.componentConfiguration.getParser().getFields())).toList();
        JavaRDD<Row> rejectedRdd = getRejectedData(parseRdd);
        logger.debug("Rejected rdd count in data frame :: "+ rejectedRdd.count());
        JavaRDD<Row> outputRdd = getOutputData(parseRdd);
        Dataset<Row> localDataFrame = ctx.sparkSession.createDataFrame(outputRdd.rdd(), internalSchema).select(outputColumns);
        logger.debug("Output rdd length in data frame = " + recCounter.value() +"\n");
        logger.debug("Rejected rdd length in data frame = " + errCounter.value() +"\n");
        logger.debug("Dest dir for file in data frame = " + destDir +"\n");
        localDataFrame = convertJsonStringColToStruct(localDataFrame, ngctx.componentConfiguration.getParser().getFields());
        localDataFrame = pivotOrFlattenDataset(localDataFrame);
        logger.debug("Final DS Schema : "+ localDataFrame.schema());
        int rc = 0;
        rc = commitDataSetFromDSMap(ngctx, localDataFrame, outputDataSetName, destDir.toString(), Output.Mode.APPEND.toString());
        logger.debug("Write dataset status = " + rc);
        if(isPivotApplied || isFlatteningEnabled) {
            ngctx.datafileDFmap.put(ngctx.dataSetName, localDataFrame.cache());
        }else{
            collectAcceptedData(parseRdd,outputRdd);
        }
        //Filter out Rejected Data
        collectRejectedData(parseRdd, outputRdd);
        logger.debug("parsing dataframe ends here");
        return rc;
    }

    public int parseAndUnionFiles(FileStatus[] files, String mode){
        JavaRDD<String> combinedRdd = null;
        // Files
        String headerLine = null;
        for (FileStatus file : files) {
            if (file.isFile()) {
                logger.debug("Reading " + file.getPath() + "\n");
                JavaRDD<String> rdd = new JavaSparkContext(ctx.sparkSession.sparkContext())
                    .textFile(file.getPath().toString(), 1);
                // Add line numbers
                JavaPairRDD<String, Long> zipIndexRdd = rdd.zipWithIndex();
                if(headerLine == null){
                    headerLine = NGComponentUtil.getLineFromRdd(zipIndexRdd, headerSize, fieldDefRowNumber);
                }
                JavaRDD<String> rddWithoutHeader = zipIndexRdd
                    // Filter out header based on line number
                    .filter(new HeaderFilter(headerSize))
                    // Get rid of file numbers
                    .keys();
                if (combinedRdd == null) {
                    combinedRdd = rddWithoutHeader;
                } else {
                    combinedRdd = combinedRdd.union(rddWithoutHeader);
                }
            }
        }
        inputDSCount = combinedRdd.count();
        createSchema(ngctx.componentConfiguration.getParser().getFields(), Optional.ofNullable(headerLine));
        logger.debug("schema"+ schema);
        logger.debug("internalSchema"+internalSchema);

        JavaRDD<Row> parseRdd = combinedRdd.map(new ConvertToRow(schema, tsFormats, lineSeparator, delimiter, quoteChar,
            quoteEscapeChar, '\'', recCounter, errCounter, allowInconsistentCol
            , fieldDefaultValuesMap, isSkipFieldsEnabled));
        // Create output dataset
        JavaRDD<Row> outputRdd = getOutputData(parseRdd);

        logger.debug("Output rdd length = " + recCounter.value() + "\n");
        logger.debug("Rejected rdd length = " + errCounter.value() + "\n");

        Dataset<Row> outputDS = convertRddToDS(outputRdd);
        outputDS = convertJsonStringColToStruct(outputDS, ngctx.componentConfiguration.getParser().getFields());
        Dataset<Row> pivotDS = pivotOrFlattenDataset(outputDS);
        logger.debug("Final DS Schema : "+ pivotDS.schema());
        logger.debug("************************************** Dest dir for rdd = " + tempDir + "\n");

        int retval = commitDataSetFromDSMap(ngctx, pivotDS, outputDataSetName, tempDir, Output.Mode.APPEND.name());
        if (retval == 0) {
            ngctx.datafileDFmap.put(ngctx.dataSetName, pivotDS.cache());
            ctx.resultDataDesc.add(new MoveDataDescriptor(tempDir, outputDataSetLocation,
                outputDataSetName, mode, outputFormat, pkeys));
        }
        //Filter out Rejected Datasss
        collectRejectedData(parseRdd, outputRdd);
        return retval;
    }

    private JavaRDD<String> loadSingleFile(Path file){
        logger.debug("Reading " + file + "\n");
        logger.debug("Header size : " + headerSize +"\n");

        JavaRDD<String> rdd = new JavaSparkContext(ctx.sparkSession.sparkContext())
            .textFile(file.toString(), 1);

        JavaRDD<String> rddWithoutHeader = rdd
            // Add line numbers
            .zipWithIndex()
            // Filter out header based on line number
            .filter(new HeaderFilter(headerSize))
            // Get rid of file numbers
            .keys();
        return rddWithoutHeader;
    }

    private Dataset<Row> convertRddToDS(JavaRDD<Row> outputRdd){
        logger.debug("==> convertRddToDS()");
        scala.collection.Seq<Column>  outputColumns =
            scala.collection.JavaConversions.asScalaBuffer(
                createFieldList(ngctx.componentConfiguration.getParser().getFields())).toList();
        Dataset<Row> outputDS = ctx.sparkSession.createDataFrame(outputRdd.rdd(), internalSchema).select(outputColumns);
        logger.debug("Output rdd length = " + recCounter.value() + "\n");
        logger.debug("Rejected rdd length = " + errCounter.value() + "\n");
        return outputDS;
    }

    private boolean collectAcceptedData(JavaRDD<Row> fullRdd, JavaRDD<Row> outputRdd) {
        boolean status = true;

        try {
            // Get all entries which are rejected
            logger.debug("Collecting Accepted data");

            JavaRDD<Row> acceptedRdd = getOutputData(fullRdd);

            if (this.acceptedDataCollector == null) {
                acceptedDataCollector = acceptedRdd;
            } else {
                acceptedDataCollector = acceptedDataCollector.union(acceptedRdd);
            }


            //TODO: SIP-9791 - The count statements are executed even when it is logger.debug mode.
            //TODO: This is a crude way of checking. This need to be revisited.
            if(logger.isDebugEnabled()) {
                logger.debug(" ********  Parser Data Records Count *******  = " + acceptedDataCollector.count() + "\n");
            }

        } catch (Exception exception) {
            logger.error(exception);
            logger.debug(ExceptionUtils.getStackTrace(exception));
            status = false;
        }
        return status;
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

            JavaRDD<Row> rejectedRdd = getRejectedData(fullRdd);

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

    private boolean writeRdd(JavaRDD rdd, String path) {
        if (rdd != null && path != null) {
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
            JavaRDD<String> rejectedRecords = rejectedDataCollector
                .map(new TransformRDDWithDelimiter(this.delimiter));
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

                String tempRejectedLocation = this.rejectedDatasetLocation + "_" + UUID.randomUUID()
                    + "_" + System.currentTimeMillis();
                logger.debug("Writing rejected data to temp directory " + tempRejectedLocation);

                writeRdd(rejectedRecords, tempRejectedLocation);


                if (HFileOperations.exists(this.rejectedDatasetLocation)) {
                    logger.debug("Deleting existing rejected records");
                    HFileOperations.deleteEnt(this.rejectedDatasetLocation);
                }

                this.ctx.fs.rename(new Path(tempRejectedLocation),
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
                logger.trace("Found date field " + field.getName() + " format: " + field.getFormat());
            } else {
                retval.add("");
            }
        }
        return retval;
    }

    private void createSchema(List<Field> fields, Optional<String> optHeader){
        AtomicReference<StructField[]> structFields = new AtomicReference<>(new StructField[fields.size()]);
        AtomicReference<List<String>> fieldNames = new AtomicReference<>();
        if(optHeader.isPresent()){
            String header = optHeader.get().trim();
            if(!header.isEmpty()){
                fieldNames.set(Arrays.asList(header.toUpperCase().split("\\s*"+delimiter+"\\s*",-1)));
            }
        }
        AtomicBoolean isIndexConfigNotExists = new AtomicBoolean(true);
        AtomicBoolean skipFieldsEnabled = new AtomicBoolean(true);
        AtomicReference<Map<String, Tuple2<Integer, Object>>> arFieldDefaultValuesMap = new AtomicReference<>(new HashMap<>());
        AtomicInteger index = new AtomicInteger(0);
        fields.stream().forEach(field -> {
            DataType dataType = convertXdfToSparkType(field.getType());
            structFields.get()[index.get()] = new StructField(field.getName(), dataType, true, Metadata.empty());
            int fieldIndex = getFieldIndex(field, Optional.ofNullable(fieldNames.get()));
            if(fieldIndex == -1){
                skipFieldsEnabled.set(false);
                fieldIndex=index.get();
            }else{
                isIndexConfigNotExists.set(false);
            }
            Object defaultValObj =  getFieldDefaultValue(dataType, field.getDefaultValue(), Optional.ofNullable(tsFormats.get(index.get())));
            arFieldDefaultValuesMap.get().put(field.getName(), new Tuple2<>(fieldIndex, defaultValObj));
            index.getAndDecrement();
        });
        fieldDefaultValuesMap = arFieldDefaultValuesMap.get();
        isSkipFieldsEnabled = skipFieldsEnabled.get();
        if(isSkipFieldsEnabled || isIndexConfigNotExists.get()){
            schema = new StructType(structFields.get());
            StructField rejFlagField = new StructField(REJECTED_FLAG, DataTypes.IntegerType, true, Metadata.empty());
            StructField rejRsnField = new StructField(REJ_REASON, DataTypes.StringType, true, Metadata.empty());
            StructField[] structFields1 = ArrayUtils.addAll(structFields.get(), rejFlagField, rejRsnField);
            internalSchema = new StructType(structFields1);
        }else{
            throw new XDFException(XDFReturnCode.CONFIG_ERROR,"Fields sourceIndex or sourceFieldName config is incorrect.");
        }
    }

    private int getFieldIndex(Field field, Optional<List<String>> optFieldNames) {
        int index = -1;
        if(field.getSourceIndex() != null){
            if(field.getSourceIndex() >= 0){
                index = field.getSourceIndex();
            }else{
                throw new XDFException(XDFReturnCode.CONFIG_ERROR,"sourceIndex ("+field.getSourceIndex()+") should not be negative value.");
            }
        }else if(field.getSourceFieldName() != null){
            if(field.getSourceFieldName().trim().isEmpty()){
                throw new XDFException(XDFReturnCode.CONFIG_ERROR,"sourceFieldName should not be empty in Fields Config.");
            }else{
                if(optFieldNames.isPresent()) {
                    int fieldIndex = optFieldNames.get().indexOf(field.getSourceFieldName().trim().toUpperCase());
                    if(fieldIndex >= 0){
                        index = fieldIndex;
                    }else{
                        throw new XDFException(XDFReturnCode.CONFIG_ERROR,"Field sourceFieldName ("+field.getSourceFieldName()+") not exist in File Header.");
                    }
                }else{
                    throw new XDFException(XDFReturnCode.CONFIG_ERROR,"File Header not exist. So sourceFieldName should not add to Field Config.");
                }
            }
        }
        return index;
    }

    private Object getFieldDefaultValue(DataType dataType, String defaultValue, Optional<String> optTsFormat) {
        if(defaultValue != null){
            defaultValue = defaultValue.trim();
            try {
                if (dataType.equals(DataTypes.StringType)) {
                    if (NGComponentUtil.validateString(defaultValue,this.quoteChar)) {
                        return defaultValue;
                    } else {
                        throw new Exception("Invalid default value");
                    }
                } else if (dataType.equals(DataTypes.LongType)) {
                    return Long.parseLong(defaultValue);
                } else if (dataType.equals(DataTypes.DoubleType)) {
                    return Double.parseDouble(defaultValue);
                } else if (dataType.equals(DataTypes.IntegerType)) {
                    return Integer.parseInt(defaultValue);
                } else if (dataType.equals(DataTypes.TimestampType)) {
                    SimpleDateFormat df = new SimpleDateFormat();
                    df.setLenient(false);
                    if (optTsFormat.isPresent()) {
                        df.applyPattern(optTsFormat.get());
                    } else {
                        df.applyPattern(DEFAULT_DATE_FORMAT);
                    }
                    return new java.sql.Timestamp(df.parse(defaultValue).getTime());
                }
            } catch (Exception e) {
                throw new XDFException(XDFReturnCode.CONFIG_ERROR,"Invalid default value for the Type ("+dataType+") is : " + defaultValue);
            }
        }
        return null;
    }

    private Map<String, Object> getOutputDatasetDetails() {
        Map<String, Object> outputDataset = null;
        logger.info("Outputs = " + ngctx.outputs);
        logger.info("Output DS = " + ngctx.outputDataSets);
        outputDataset = ngctx.outputs.get(RequiredNamedParameters.Output.toString());
        logger.debug("Output dataset = " + outputDataset);

        return outputDataset;
    }


    private Map<String, Object> getRejectDatasetDetails() {
        Map<String, Object> rejectDataset = null;
        rejectDataset = ngctx.outputs.get(RequiredNamedParameters.Rejected.toString());
        logger.debug("Rejected dataset = " + rejectDataset);

        return rejectDataset;
    }

    private JavaRDD<Row> getOutputData (JavaRDD<Row> parsedData) {
        int rejectedColumn = internalSchema.length() - 2;
        JavaRDD<Row> outputRdd = parsedData.filter(row -> (int)row.get(rejectedColumn) == 0);
        return outputRdd;
    }

    private JavaRDD<Row> getRejectedData (JavaRDD<Row> parsedData) {
        int rejectedColumn = internalSchema.length() - 2;
        JavaRDD<Row> rejectedRdd = parsedData.filter(row -> (int)row.get(rejectedColumn) == 1);

        return rejectedRdd;
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
            case CsvInspectorRowProcessor.T_INTEGER:
                return DataTypes.IntegerType;
            case CsvInspectorRowProcessor.T_JSON:
                return DataTypes.StringType;
            case CsvInspectorRowProcessor.T_JSON_ARRAY:
                return DataTypes.StringType;
            default:
                return DataTypes.StringType;
        }
    }


    public static void main(String[] args) {
        NGContextServices ngCtxSvc;
        CliHandler cli = new CliHandler();
        NGParser component = null;
        int rc= 0;
        Exception exception = null;
        ComponentConfiguration cfg = null;
        try {
            long start_time = System.currentTimeMillis();

            HFileOperations.init(10);

            Map<String, Object> parameters = cli.parse(args);
            String cfgLocation = (String) parameters.get(CliHandler.OPTIONS.CONFIG.name());
            String configAsStr = ConfigLoader.loadConfiguration(cfgLocation);
            if (configAsStr == null || configAsStr.isEmpty()) {
                throw new XDFException(XDFReturnCode.INCORRECT_OR_ABSENT_PARAMETER, "configuration file name");
            }

            String appId = (String) parameters.get(CliHandler.OPTIONS.APP_ID.name());
            if (appId == null || appId.isEmpty()) {
                throw new XDFException(XDFReturnCode.INCORRECT_OR_ABSENT_PARAMETER, "Project/application name");
            }

            String batchId = (String) parameters.get(CliHandler.OPTIONS.BATCH_ID.name());
            if (batchId == null || batchId.isEmpty()) {
                throw new XDFException(XDFReturnCode.INCORRECT_OR_ABSENT_PARAMETER, "batch id/session id");
            }

            String xdfDataRootSys = System.getProperty(MetadataBase.XDF_DATA_ROOT);
            if (xdfDataRootSys == null || xdfDataRootSys.isEmpty()) {
                throw new XDFException(XDFReturnCode.INCORRECT_OR_ABSENT_PARAMETER, "XDF Data root");
            }

            ComponentServices pcs[] = {
                ComponentServices.OutputDSMetadata,
                ComponentServices.Project,
                ComponentServices.TransformationMetadata,
                ComponentServices.Spark,
            };
            cfg = NGParser.analyzeAndValidate(configAsStr);
            ngCtxSvc = new NGContextServices(pcs, xdfDataRootSys, cfg, appId, "parser", batchId);
            ngCtxSvc.initContext();
            ngCtxSvc.registerOutputDataSet();
            logger.warn("Output datasets:");
            ngCtxSvc.getNgctx().registeredOutputDSIds.forEach( id ->
                logger.warn(id)
            );
            logger.warn(ngCtxSvc.getNgctx().toString());
            component = new NGParser(ngCtxSvc.getNgctx());
            if (component.initComponent(null)) {
                rc = component.run();
                long end_time = System.currentTimeMillis();
                long difference = end_time - start_time;
                logger.info("Parser total time " + difference);
            }
        }catch (Exception ex) {
            exception = ex;
        }
        rc = NGComponentUtil.handleErrors(Optional.ofNullable(component), Optional.ofNullable(cfg), rc, exception);
        System.exit(rc);
    }

    private boolean isSchemaContainsJsonType(List<Field> fields){
        for(Field field : fields){
            if(CsvInspectorRowProcessor.T_JSON.equalsIgnoreCase(field.getType().trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param dataset
     * @param fields
     * @return
     *
     * convertJsonStringColToStruct() checks if any filed type is json or json_array
     * and converts then to StructType or ArrayType columns in Dataset.
     *
     */
    public Dataset<Row> convertJsonStringColToStruct(Dataset<Row> dataset, List<Field> fields) {
        if (isSchemaContainsJsonType) {
            logger.debug("Parser isFlatteningEnabled : " + isFlatteningEnabled);
            for (Field field : fields) {
                if (CsvInspectorRowProcessor.T_JSON.equalsIgnoreCase(field.getType().trim())) {
                    dataset = processJsonColumnInCSV(dataset, field);
                } else if (CsvInspectorRowProcessor.T_JSON_ARRAY.equalsIgnoreCase(field.getType().trim())) {
                    dataset = processJsonArrayColumnInCSV(dataset, field);
                }
            }
        }
        return dataset;
    }

    /**
     *
     * @param dataset
     * @param field
     * @return
     *
     * processJsonColumnInCSV() converts String json type column to StructType column.
     * To achieve this, First extracts String json field as separate Dataset
     * Then extract schema from above Json Dataset
     * Then Apply this StructType schema to String Json field in input Dataset.
     * Then it will apply sanitize on field names of StructType
     * Then Cast Json Field to Sanitized StructType
     * If Flattening Enabled then it will apply Flattening logic on StructType Json field.
     */
    private Dataset<Row> processJsonColumnInCSV(Dataset<Row> dataset,Field field) {
        String jsonFieldName = field.getName().trim();
        Dataset<Row> jsonDS = ctx.sparkSession.read().json(dataset.select(jsonFieldName).as(Encoders.STRING()));
        StructType jsonDSschema = jsonDS.schema();
        logger.debug("Json DS Schema : "+ jsonDSschema);
        dataset = dataset.withColumn(jsonFieldName, from_json(dataset.col(jsonFieldName), jsonDSschema));
        StructType newStructType = NGComponentUtil.getSanitizedStructType(jsonDSschema);
        dataset = NGComponentUtil.changeColumnType(dataset, jsonFieldName, newStructType);
        logger.debug("Field isFlatteningEnabled : "+ field.isFlatteningEnabled());
        if(!isFlatteningEnabled && field.isFlatteningEnabled()) {
            if (flattner == null) {
                flattner = new Flattener(ctx, this, datasetHelper);
            }
            dataset = flattner.processStructType(dataset, jsonFieldName, newStructType);
        }
        return dataset;
    }

    /**
     *
     * @param dataset
     * @param field
     * @return
     *
     * processJsonArrayColumnInCSV() converts String json_array type column to ArrayType<StructType> column.
     * To achieve this, First extracts String json_array field as separate Dataset
     * Then extract schema from above Json Dataset
     * Then Creates ArrayType of above StructType schema
     * Then Apply this ArrayType<StructType> to String json_array field in input Dataset.
     * Then it will apply sanitize on field names of ArrayType<StructType>
     * Then Cast Json Field to Sanitized ArrayType<StructType>
     * If Flattening Enabled then it will apply Flattening logic on ArrayType<StructType> json_array field.
     */
    private Dataset<Row> processJsonArrayColumnInCSV(Dataset<Row> dataset, Field field) {
        String jsonFieldName = field.getName().trim();
        Dataset<Row> jsonDS = ctx.sparkSession.read().json(dataset.select(jsonFieldName).as(Encoders.STRING()));
        StructType jsonDSschema = jsonDS.schema();
        logger.debug("Json DS Schema : "+ jsonDSschema);
        ArrayType arrayType = ArrayType.apply(jsonDSschema);
        dataset = dataset.withColumn(jsonFieldName, from_json(dataset.col(jsonFieldName), arrayType));
        ArrayType newArrayType = NGComponentUtil.getSanitizedArrayType(arrayType);
        dataset = NGComponentUtil.changeColumnType(dataset, jsonFieldName, newArrayType);
        logger.debug("Field isFlatteningEnabled : "+ field.isFlatteningEnabled());
        if(!isFlatteningEnabled && field.isFlatteningEnabled()) {
            if (flattner == null) {
                flattner = new Flattener(ctx, this, datasetHelper);
            }
            dataset = flattner.processArrayType(dataset, jsonFieldName, newArrayType);
        }
        return dataset;
    }

    public Dataset<Row> pivotOrFlattenDataset(Dataset<Row> dataset) {
        if(isPivotApplied) {
            dataset = new Pivot().applyPivot(dataset, pivotFields);
        }
        if(isFlatteningEnabled) {
            dataset = flattenDataset(dataset);
        }

        /*if(isPivotApplied || isFlatteningEnabled) {
            dataset = sortColumnNames(dataset);
        }*/
        if(logger.isDebugEnabled()){
            dataset.show();
        }
        return dataset;
    }

    public Dataset<Row> flattenDataset(Dataset<Row> dataset) {
        if(flattner == null) {flattner = new Flattener(ctx, this, datasetHelper);}
        return flattner.flattenDataset(dataset);
    }

    public Dataset<Row> sortColumnNames(Dataset<Row> dataset){
        String[] dsCols = dataset.columns();
        logger.debug("Before Sort - Columns : " + Arrays.toString(dsCols));
        Arrays.sort(dsCols);
        logger.debug("After Sort - Columns : " + Arrays.toString(dsCols));
        dataset = dataset.select(dsCols[0].trim(), IntStream.range(1, dsCols.length)
            .mapToObj(index -> dsCols[index].trim())
            .toArray(String[]::new));
        return dataset;
    }
}
