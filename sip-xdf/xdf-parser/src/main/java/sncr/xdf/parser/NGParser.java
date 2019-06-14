package sncr.xdf.parser;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import sncr.xdf.context.RequiredNamedParameters;
import sncr.bda.conf.ParserInputFileFormat;

public class NGParser extends AbstractComponent implements WithDLBatchWriter, WithSpark, WithDataSet, WithProjectScope {

    private static final Logger logger = Logger.getLogger(NGParser.class);

    private ParserInputFileFormat parserInputFileFormat;
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

    private List<String> pkeys;

    public static final String REJECTED_FLAG = "__REJ_FLAG";
    public static final String REJ_REASON = "__REJ_REASON";

    private JavaRDD<Row> rejectedDataCollector;
    private JavaRDD<Row> acceptedDataCollector;

    public NGParser(NGContext ngctx, ComponentServices[] cs) { super(ngctx, cs); }

    {
        componentName = "parser";
    }

    public NGParser(NGContext ngctx,String mode) {  super(ngctx); }

    public NGParser(NGContext ngctx) {
        super(ngctx);
    }

    public NGParser() {  super(); }


//    protected int execute(){
//        int retval = 0;
//
//        parserInputFileFormat = ctx.componentConfiguration.getParser().getParserInputFileFormat();
//        sourcePath = ngctx.componentConfiguration.getParser().getFile();
//        headerSize = ngctx.componentConfiguration.getParser().getHeaderSize();
//        tempDir = generateTempLocation(new DataSetHelper(ngctx, services.md),
//            ngctx.batchID,
//            ngctx.componentName,
//            null, null);
//        archiveDir = generateArchiveLocation(new DataSetHelper(ngctx, services.md));
//
//        lineSeparator = ngctx.componentConfiguration.getParser().getLineSeparator();
//        delimiter = ngctx.componentConfiguration.getParser().getDelimiter().charAt(0);
//        quoteChar = ngctx.componentConfiguration.getParser().getQuoteChar().charAt(0);
//        quoteEscapeChar = ngctx.componentConfiguration.getParser().getQuoteEscape().charAt(0);
//
//        errCounter = ctx.sparkSession.sparkContext().longAccumulator("ParserErrorCounter");
//        recCounter = ctx.sparkSession.sparkContext().longAccumulator("ParserRecCounter");
//
//        schema = createSchema(ngctx.componentConfiguration.getParser().getFields(), false, false);
//
//        tsFormats = createTsFormatList(ngctx.componentConfiguration.getParser().getFields());
//        logger.trace(tsFormats.toString());
//
//        internalSchema = createSchema(ngctx.componentConfiguration.getParser().getFields(), true, true);
//
//        // Output data set
//        if(ngctx.outputDataSets.size() != 1){
//            // error - must be only one for parser
//            logger.error("Found multiple output data set definitions "+ ngctx.outputDataSets.size());
//        }
//
//        logger.info("Outputs = " + ngctx.outputs + "\n");
//        logger.info("Output DS Size = " + ngctx.outputDataSets.size() + "\n");
//        logger.info("Output DS = " + ngctx.outputDataSets + "\n");
//
//        Map.Entry<String, Map<String, Object>> ds =  (Map.Entry<String, Map<String, Object>>)ngctx.outputDataSets.entrySet().toArray()[0];
//        outputDataSetName = ds.getKey();
//        outputDataSetLocation = (String) ds.getValue().get(DataSetProperties.PhysicalLocation.name());
//        outputFormat = (String) ds.getValue().get(DataSetProperties.Format.name());
//        outputNOF =  (Integer) ds.getValue().get(DataSetProperties.NumberOfFiles.name());
//        pkeys = (List<String>) ds.getValue().get(DataSetProperties.PartitionKeys.name());
//
//        logger.debug("Output data set " + outputDataSetName + " located at " + outputDataSetLocation + " with format " + outputFormat);
//
//        Map<String, Object> rejDs = getRejectDatasetDetails() ;
//
//        logger.debug("Rejected dataset details = " + rejDs);
//        if (rejDs != null) {
//            rejectedDatasetName = rejDs.get(DataSetProperties.Name.name()).toString();
//            rejectedDatasetLocation = rejDs.get(DataSetProperties.PhysicalLocation.name()).toString();
//            rejectedDataFormat = rejDs.get(DataSetProperties.Format.name()).toString();
//            rejectedDataSetMode = rejDs.get(DataSetProperties.Mode.name()).toString();
//
//            logger.debug("Rejected dataset " + rejectedDatasetName + " at "
//                + rejectedDatasetLocation + " with format " + rejectedDataFormat);
//
//            if (rejectedDataFormat == null || rejectedDataFormat.length() == 0) {
//                rejectedDataFormat = "parquet";
//            }
//
//            logger.info("Rejected data set " + rejectedDatasetName + " located at " + rejectedDatasetLocation
//                + " with format " + rejectedDataFormat);
//        }
//
//        FileSystem fs = HFileOperations.getFileSystem();
//        try {
//
//            if (ctx.fs.exists(new Path(tempDir)))
//                HFileOperations.deleteEnt(tempDir);
//
//
//            if(headerSize >= 1) {
//                FileStatus[] files = fs.globStatus(new Path(sourcePath));
//                // Check if directory has been given
//                if(files.length == 1 && files[0].isDirectory()){
//                    // If so - we have to process all the files inside - create the mask
//                    sourcePath += Path.SEPARATOR + "*";
//                    // ... and query content
//                    files = fs.globStatus(new Path(sourcePath));
//                }
//                retval = parseFiles(files,  DLDataSetOperations.MODE_APPEND);
//            } else {
//                retval = parse(DLDataSetOperations.MODE_APPEND);
//            }
//
//            //Write Consolidated Accepted data
//            if (this.acceptedDataCollector != null) {
//                scala.collection.Seq<Column> outputColumns =
//                    scala.collection.JavaConversions.asScalaBuffer(createFieldList(ngctx.componentConfiguration.getParser().getFields())).toList();
//                Dataset outputDS = ctx.sparkSession.createDataFrame(acceptedDataCollector.rdd(), internalSchema).select(outputColumns);
//                ngctx.datafileDFmap.put(ngctx.dataSetName,outputDS);
//            }
//
//            //Write rejected data
//            if (this.rejectedDataCollector != null) {
//                boolean status = writeRejectedData();
//
//                if (!status) {
//                    logger.warn("Unable to write rejected data");
//                }
//            }
//
//        }catch (IOException e){
//            logger.error("IO error: " + ExceptionUtils.getFullStackTrace(e));
//            retval =  -1;
//        } catch (Exception e) {
//            logger.error("Error: " + ExceptionUtils.getFullStackTrace(e));
//            retval =  -1;
//        }
//        return retval;
//    }

    protected int execute(){

        int retval = 0;

        parserInputFileFormat = ngctx.componentConfiguration.getParser().getParserInputFileFormat();
        sourcePath = ngctx.componentConfiguration.getParser().getFile();
        tempDir = generateTempLocation(new DataSetHelper(ngctx, services.md),
                                      null, null);

        archiveDir = generateArchiveLocation(new DataSetHelper(ngctx, services.md));

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

        logger.debug("Input file format = " + this.parserInputFileFormat);

        if (parserInputFileFormat.equals(ParserInputFileFormat.CSV)) {
            headerSize = ngctx.componentConfiguration.getParser().getHeaderSize();

            lineSeparator = ngctx.componentConfiguration.getParser().getLineSeparator();
            delimiter = (ngctx.componentConfiguration.getParser().getDelimiter() != null)? ngctx.componentConfiguration.getParser().getDelimiter().charAt(0): ',';
            quoteChar = (ngctx.componentConfiguration.getParser().getQuoteChar() != null)? ngctx.componentConfiguration.getParser().getQuoteChar().charAt(0): '\'';
            quoteEscapeChar = (ngctx.componentConfiguration.getParser().getQuoteEscape() != null)? ngctx.componentConfiguration.getParser().getQuoteEscape().charAt(0): '\"';

            schema = createSchema(ngctx.componentConfiguration.getParser().getFields(), false, false);
            tsFormats = createTsFormatList(ngctx.componentConfiguration.getParser().getFields());
            logger.info(tsFormats);

            internalSchema = createSchema(ngctx.componentConfiguration.getParser().getFields(), true, true);

            // Output data set
            if (ngctx.outputDataSets.size() == 0) {
                logger.error("Output dataset not defined");
                return -1;
            }

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

            try {

                if (ctx.fs.exists(new Path(tempDir)))
                    HFileOperations.deleteEnt(tempDir);

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

                //Write Consolidated Accepted data
                if (this.acceptedDataCollector != null) {
                    scala.collection.Seq<Column> outputColumns =
                        scala.collection.JavaConversions.asScalaBuffer(createFieldList(ngctx.componentConfiguration.getParser().getFields())).toList();
                    Dataset outputDS = ctx.sparkSession.createDataFrame(acceptedDataCollector.rdd(), internalSchema).select(outputColumns);
                    ngctx.datafileDFmap.put(ngctx.dataSetName,outputDS);
                }

                //Write rejected data
                if (this.rejectedDataCollector != null) {
                    boolean status = writeRejectedData();

                    if (!status) {
                        logger.warn("Unable to write rejected data");
                    }
                }

            }catch (IOException e){
                logger.error("IO error: " + ExceptionUtils.getFullStackTrace(e));
                retval =  -1;
            } catch (Exception e) {
                logger.error("Error: " + ExceptionUtils.getFullStackTrace(e));
                retval =  -1;
            }

        } else if (parserInputFileFormat.equals(ParserInputFileFormat.JSON)) {
            NGJsonFileParser jsonFileParser = new NGJsonFileParser(ctx);

            Dataset<Row> inputDataset = jsonFileParser.parseInput(sourcePath);

            this.recCounter.setValue(inputDataset.count());

            commitDataSetFromDSMap(ngctx, inputDataset, outputFormat, tempDir, "append");

            ctx.resultDataDesc.add(new MoveDataDescriptor(tempDir, outputDataSetLocation,
                outputDataSetName, outputDataSetMode, outputFormat, pkeys));

        } else if (parserInputFileFormat.equals(ParserInputFileFormat.PARQUET)) {
            NGParquetFileParser parquetFileParser = new NGParquetFileParser(ctx);
            Dataset<Row> inputDataset = parquetFileParser.parseInput(sourcePath);

            this.recCounter.setValue(inputDataset.count());

            commitDataSetFromDSMap(ngctx, inputDataset, outputFormat, tempDir, "append");

            ctx.resultDataDesc.add(new MoveDataDescriptor(tempDir, outputDataSetLocation,
                outputDataSetName, outputDataSetMode, outputFormat, pkeys));
        }

        return retval;
    }

    public static ComponentConfiguration analyzeAndValidate(String config) throws Exception {

        ComponentConfiguration compConf = AbstractComponent.analyzeAndValidate(config);
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

                logger.debug("Total files = " + files.length);

                int archiveCounter = 0;

                for(FileStatus fiile: files) {
                    String currentTimestamp = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss.SSS"));

                    Path archivePath = new Path(archiveDir + "/" + currentTimestamp + "/");
                    ctx.fs.mkdirs(archivePath);

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

        return result;
    }

    private boolean archiveSingleFile(Path sourceFilePath, Path archiveLocation) throws
        IOException {
        return ctx.fs.rename(sourceFilePath, archiveLocation);
    }

    // Parse data without headers
    int parse(String mode){
        int rc = parseSingleFile(new Path(sourcePath), new Path(tempDir));

        if (rc != 0){
            error = "Could not parse file: " + sourcePath;
            logger.error(error);
            return rc;
        }
        ctx.resultDataDesc.add(new MoveDataDescriptor(tempDir  + Path.SEPARATOR + outputDataSetName, outputDataSetLocation, outputDataSetName, mode, outputFormat,pkeys));
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
        return 0;
    }


    private int parseSingleFile(Path file, Path destDir){
        logger.trace("Parsing " + file + " to " + destDir +"\n");
        logger.trace("Header size : " + headerSize +"\n");

        JavaRDD<String> rdd = reader.readToRDD(file.toString(), 1);

        JavaRDD<Row> parseRdd = rdd
            // Add line numbers
            .zipWithIndex()
            // Filter out header based on line number
            .filter(new HeaderFilter(headerSize))
            // Get rid of file numbers
            .keys()
            .map(new ConvertToRow(schema,
                tsFormats,
                lineSeparator,
                delimiter,
                quoteChar,
                quoteEscapeChar,
                '\'',
                recCounter,
                errCounter));

        // Create output dataset
        scala.collection.Seq<Column> outputColumns =
            scala.collection.JavaConversions.asScalaBuffer(createFieldList(ngctx.componentConfiguration.getParser().getFields())).toList();
        JavaRDD<Row> outputRdd = getOutputData(parseRdd);
        Dataset<Row> df = ctx.sparkSession.createDataFrame(outputRdd.rdd(), internalSchema).select(outputColumns);

        collectAcceptedData(parseRdd,outputRdd);

        scala.collection.Seq<Column> scalaList=
            scala.collection.JavaConversions.asScalaBuffer(createFieldList(ngctx.componentConfiguration.getParser().getFields())).toList();
//        Column cond = df.col(REJECTED_FLAG).isNull().or(df.col(REJECTED_FLAG).equalTo(0));
//        Dataset<Row> filteredDataset = df.select(scalaList).where(cond);
//        logger.warn ( "Filtered df:  " + filteredDataset.count());

        logger.debug("Output rdd length = " + recCounter.value() +"\n");
        logger.debug("Rejected rdd length = " + errCounter.value() +"\n");
        logger.debug("Dest dir for file " + file + " = " + destDir +"\n");

        int rc = 0;
        logger.debug("************************************** Dest dir for file " + file + " = " + destDir +"\n");

        rc = commitDataSetFromDSMap(ngctx, df, outputDataSetName, destDir.toString(), "append");

        logger.debug("Write dataset status = " + rc);

        //Filter out Rejected Data
        collectRejectedData(parseRdd, outputRdd);
        return rc;
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
            logger.debug(" ********  Parser Data Records Count *******  = " + acceptedDataCollector.count() + "\n");

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
            default:
                return DataTypes.StringType;

        }
    }


    public static void main(String[] args) {

        NGContextServices ngCtxSvc;
        CliHandler cli = new CliHandler();
        try {
            long start_time = System.currentTimeMillis();

            HFileOperations.init(10);

            Map<String, Object> parameters = cli.parse(args);
            String cfgLocation = (String) parameters.get(CliHandler.OPTIONS.CONFIG.name());
            String configAsStr = ConfigLoader.loadConfiguration(cfgLocation);
            if (configAsStr == null || configAsStr.isEmpty()) {
                throw new XDFException(XDFException.ErrorCodes.IncorrectOrAbsentParameter, "configuration file name");
            }

            String appId = (String) parameters.get(CliHandler.OPTIONS.APP_ID.name());
            if (appId == null || appId.isEmpty()) {
                throw new XDFException(XDFException.ErrorCodes.IncorrectOrAbsentParameter, "Project/application name");
            }

            String batchId = (String) parameters.get(CliHandler.OPTIONS.BATCH_ID.name());
            if (batchId == null || batchId.isEmpty()) {
                throw new XDFException(XDFException.ErrorCodes.IncorrectOrAbsentParameter, "batch id/session id");
            }

            String xdfDataRootSys = System.getProperty(MetadataBase.XDF_DATA_ROOT);
            if (xdfDataRootSys == null || xdfDataRootSys.isEmpty()) {
                throw new XDFException(XDFException.ErrorCodes.IncorrectOrAbsentParameter, "XDF Data root");
            }

            ComponentServices pcs[] = {
                ComponentServices.OutputDSMetadata,
                ComponentServices.Project,
                ComponentServices.TransformationMetadata,
                ComponentServices.Spark,
            };
            ComponentConfiguration cfg = NGParser.analyzeAndValidate(configAsStr);
            ngCtxSvc = new NGContextServices(pcs, xdfDataRootSys, cfg, appId, "parser", batchId);
            ngCtxSvc.initContext();
            ngCtxSvc.registerOutputDataSet();
            logger.warn("Output datasets:");
            ngCtxSvc.getNgctx().registeredOutputDSIds.forEach( id ->
                logger.warn(id)
            );
            logger.warn(ngCtxSvc.getNgctx().toString());
            NGParser component = new NGParser(ngCtxSvc.getNgctx());
            if (!component.initComponent(null))
                System.exit(-1);
            int rc = component.run();

            long end_time = System.currentTimeMillis();
            long difference = end_time-start_time;
            logger.info("Parser total time " + difference );

            System.exit(rc);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
