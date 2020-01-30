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
import sncr.bda.CliHandler;
import sncr.bda.ConfigLoader;
import sncr.bda.base.MetadataBase;
import sncr.bda.conf.ComponentConfiguration;
import sncr.bda.conf.Field;
import sncr.bda.conf.Output;
import sncr.bda.conf.OutputFieldsList;
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
import java.util.HashMap;
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
				FileStatus[] files = fs.globStatus(new Path(sourcePath));

				if (files.length <= 0) {
					return 0;
				}

			} catch (Exception e) {
				logger.error("Error while deletion of outputDataSetLocation " + outputDataSetLocation);
				logger.error(e.getMessage());
			} 
		}

		if (this.inputDataFrame == null && parserInputFileFormat.equals(ParserInputFileFormat.CSV)) {
			logger.debug("format csv");
			
			logger.debug("#####Component config:: " + ngctx.componentConfiguration);
			logger.debug("#####Component config parser :: " +ngctx.componentConfiguration.getParser());

            headerSize = ngctx.componentConfiguration.getParser().getHeaderSize();
            logger.debug("header size"+ headerSize);

            lineSeparator = ngctx.componentConfiguration.getParser().getLineSeparator();
            logger.debug("lineSeparator"+ lineSeparator);
            delimiter = (ngctx.componentConfiguration.getParser().getDelimiter() != null)? ngctx.componentConfiguration.getParser().getDelimiter().charAt(0): ',';
            logger.debug("delimiter"+ delimiter);
            quoteChar = (ngctx.componentConfiguration.getParser().getQuoteChar() != null)? ngctx.componentConfiguration.getParser().getQuoteChar().charAt(0): '\'';
            logger.debug("quoteChar"+ quoteChar);
            quoteEscapeChar = (ngctx.componentConfiguration.getParser().getQuoteEscape() != null)? ngctx.componentConfiguration.getParser().getQuoteEscape().charAt(0): '\"';
            logger.debug("quoteEscapeChar"+ quoteEscapeChar);

            schema = createSchema(ngctx.componentConfiguration.getParser().getFields(), false, false);
            logger.debug("schema"+ schema);
            tsFormats = createTsFormatList(ngctx.componentConfiguration.getParser().getFields());
            logger.debug("tsFormats"+ tsFormats);
            logger.info(tsFormats);

            internalSchema = createSchema(ngctx.componentConfiguration.getParser().getFields(), true, true);
            logger.debug("internalSchema"+internalSchema);

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
						retval = parseFiles(files, outputDataSetMode);
					} else {
						logger.debug("No Header");
						retval = parse(outputDataSetMode);
					}
				}

                //Write Consolidated Accepted data
                if (this.acceptedDataCollector != null) {

                    scala.collection.Seq<Column> outputColumns = null;
                    if (ngctx.componentConfiguration.getParser().getOutputFieldsList().size() <= 0)
                    {
                        outputColumns =
                            scala.collection.JavaConversions.asScalaBuffer(
                                createFieldList(ngctx.componentConfiguration.getParser().getFields())).toList();

                        Dataset outputDS = ctx.sparkSession.createDataFrame(acceptedDataCollector.rdd(), internalSchema).select(outputColumns);

                        ngctx.datafileDFmap.put(ngctx.dataSetName,outputDS.cache());
                        //TODO: SIP-9791 - The count statements are executed even when it is logger.debug mode.
                        //TODO: This is a crude way of checking. This need to be revisited.
                        if(logger.isDebugEnabled()) {
                            logger.debug("####### end of parser after caching " + outputDS.count());
                        }
                    }
                    else {
                        outputColumns =
                            scala.collection.JavaConversions.asScalaBuffer(
                                createParserOutputFieldList(ngctx.componentConfiguration.getParser().getOutputFieldsList())).toList();

                        Dataset outputDS = ctx.sparkSession.createDataFrame(acceptedDataCollector.rdd(), internalSchema).select(outputColumns);

                        Map<String,String> columnRenameList = createDestinationFieldList(ngctx.componentConfiguration.getParser().getOutputFieldsList());

                        Dataset filterOutputDS = null;
                        Dataset renameOutputDS = outputDS;

                        // using for-each loop for iteration over Map.entrySet()
                        for (Map.Entry<String, String> e : columnRenameList.entrySet()) {
                            String origin = e.getKey();
                            String destination = e.getValue();
                            filterOutputDS = renameOutputDS.withColumnRenamed(origin,destination);
                            renameOutputDS = filterOutputDS;
                        }

                        ngctx.datafileDFmap.put(ngctx.dataSetName,filterOutputDS.cache());
                        logger.debug("####### end of parser after caching "+ filterOutputDS.count());
                    }

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

             //TODO: SIP-9791 - The count statements are executed even when it is logger.debug mode.
             //TODO: This is a crude way of checking. This need to be revisited.
            if(logger.isDebugEnabled()) {
                logger.debug("Count for parser in dataset :: " + ngctx.dataSetName + ngctx.datafileDFmap.get(ngctx.dataSetName).count());
            }

            logger.debug("NGCSVFileParser ==> dataSetName  & size " + ngctx.dataSetName + "," + ngctx.datafileDFmap.size() + "\n");

        } else if (this.inputDataFrame == null && parserInputFileFormat.equals(ParserInputFileFormat.JSON))
        {
            NGJsonFileParser jsonFileParser = new NGJsonFileParser(ctx);

            Dataset<Row> inputDataset = null;
            multiLine = ngctx.componentConfiguration.getParser().getMultiLine();

            logger.debug("NGJsonFileParser ==> multiLine  value is  " + multiLine + "\n");
             inputDataset = jsonFileParser.parseInput(sourcePath,multiLine);

            logger.debug("RECORD COUNT IS " + inputDataset.count());

            
            this.recCounter.setValue(inputDataset.count());

            commitDataSetFromDSMap(ngctx, inputDataset, outputDataSetName, tempDir, Output.Mode.APPEND.name());

            ctx.resultDataDesc.add(new MoveDataDescriptor(tempDir, outputDataSetLocation,
                outputDataSetName, outputDataSetMode, outputFormat, pkeys));
            ngctx.datafileDFmap.put(ngctx.dataSetName,inputDataset.cache());
            logger.debug("Count for parser in dataset :: "+ ngctx.dataSetName +  ngctx.datafileDFmap.get(ngctx.dataSetName).count());
            logger.debug("NGJsonFileParser ==> dataSetName  & size " + ngctx.dataSetName + "," + ngctx.datafileDFmap.size() + "\n");
         } else if (this.inputDataFrame == null && parserInputFileFormat.equals(ParserInputFileFormat.PARQUET))    {
        

                NGParquetFileParser parquetFileParser = new NGParquetFileParser(ctx);
                Dataset<Row> inputDataset = null;
                
                if (inputDataFrame != null) {
    				inputDataset = inputDataFrame;
    			} else {
    				inputDataset = parquetFileParser.parseInput(sourcePath);
    			}
                this.recCounter.setValue(inputDataset.count());

                commitDataSetFromDSMap(ngctx, inputDataset, outputDataSetName, tempDir, "append");

                ctx.resultDataDesc.add(new MoveDataDescriptor(tempDir, outputDataSetLocation,
                    outputDataSetName, outputDataSetMode, outputFormat, pkeys));
                ngctx.datafileDFmap.put(ngctx.dataSetName,inputDataset.cache());
                logger.debug("Count for parser in dataset :: " + ngctx.dataSetName +  ngctx.datafileDFmap.get(ngctx.dataSetName).count());
                logger.debug("NGParquetFileParser ==>  dataSetName  & size " + ngctx.dataSetName + "," + ngctx.datafileDFmap.size()+ "\n");
            } else if(this.inputDataFrame != null) {


                inputDataFrame.show();
                this.recCounter.setValue(inputDataFrame.count());

                commitDataSetFromDSMap(ngctx, inputDataFrame, outputDataSetName, tempDir, Output.Mode.APPEND.name());

                ctx.resultDataDesc.add(new MoveDataDescriptor(tempDir, outputDataSetLocation,
                    outputDataSetName, outputDataSetMode, outputFormat, pkeys));
                ngctx.datafileDFmap.put(ngctx.dataSetName,inputDataFrame.cache());

                logger.debug("NGJsonFileParser ==> dataSetName  & size " + ngctx.dataSetName + "," + ngctx.datafileDFmap.size() + "\n");
            
            	
            	
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

        JavaRDD<Row> parsedRdd = rdd.map(
            new ConvertToRow(schema, tsFormats, lineSeparator, delimiter, quoteChar, quoteEscapeChar,
                '\'', recCounter, errCounter));

        logger.debug("Output rdd length = " + recCounter.value());
        logger.debug("Rejected rdd length = " + errCounter.value());

        JavaRDD<Row> outputRdd = getOutputData(parsedRdd);
        int status = 0 ;
        logger.debug("Rdd partition : "+ outputRdd.getNumPartitions());
        

        scala.collection.Seq<Column> outputColumns = null;
        if (ngctx.componentConfiguration.getParser().getOutputFieldsList().size() <= 0)
        {
            outputColumns =
                scala.collection.JavaConversions.asScalaBuffer(
                    createFieldList(ngctx.componentConfiguration.getParser().getFields())).toList();

            Dataset<Row> outputDataset = ctx.sparkSession.createDataFrame(outputRdd.rdd(), internalSchema).select(outputColumns);

            logger.debug("Dataset partition : "+ outputDataset.rdd().getNumPartitions());

            status = commitDataSetFromDSMap(ngctx, outputDataset, outputDataSetName, tempDir.toString(), "append");

        }
        else {
            outputColumns =
                scala.collection.JavaConversions.asScalaBuffer(
                    createParserOutputFieldList(ngctx.componentConfiguration.getParser().getOutputFieldsList())).toList();

            Dataset<Row> outputDataset = ctx.sparkSession.createDataFrame(outputRdd.rdd(), internalSchema).select(outputColumns);

            Map<String,String> columnRenameList = createDestinationFieldList(ngctx.componentConfiguration.getParser().getOutputFieldsList());

            Dataset filterOutputDS = null;
            Dataset renameOutputDS = outputDataset;

            for (Map.Entry<String, String> e : columnRenameList.entrySet()) {
                String origin = e.getKey();
                String destination = e.getValue();
                filterOutputDS = renameOutputDS.withColumnRenamed(origin,destination);
                renameOutputDS = filterOutputDS;
            }

            logger.debug("Dataset partition : "+ filterOutputDS.rdd().getNumPartitions());

            status = commitDataSetFromDSMap(ngctx, filterOutputDS, outputDataSetName, tempDir.toString(), "append");
        }

        collectAcceptedData(parsedRdd,outputRdd);
        

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
        return 0;
    }


    private int parseSingleFile(Path file, Path destDir){
        logger.trace("Parsing " + file + " to " + destDir +"\n");
        logger.trace("Header size : " + headerSize +"\n");

        JavaRDD<String> rdd = new JavaSparkContext(ctx.sparkSession.sparkContext())
                .textFile(file.toString(), 1);


        JavaRDD<Row> parseRdd = rdd
                // Add line numbers
                .zipWithIndex()
                // Filter out header based on line number
                .filter(new HeaderFilter(headerSize))
                // Get rid of file numbers
                .keys()
                .map(new ConvertToRow(schema, tsFormats, lineSeparator, delimiter, quoteChar,
                    quoteEscapeChar, '\'', recCounter, errCounter));

        
     // Create output dataset
        JavaRDD<Row> rejectedRdd = getRejectedData(parseRdd);

         //TODO: SIP-9791 - The count statements are executed even when it is logger.debug mode.
         //TODO: This is a crude way of checking. This need to be revisited.
        if(logger.isDebugEnabled()) {
            logger.debug("####### Rejected RDD COUNT:: "+ rejectedRdd.count());
        }

        JavaRDD<Row> outputRdd = getOutputData(parseRdd);
        int rc = 0;
        scala.collection.Seq<Column> outputColumns = null;
        if (ngctx.componentConfiguration.getParser().getOutputFieldsList().size() <= 0)
        {
            outputColumns =
                scala.collection.JavaConversions.asScalaBuffer(
                    createFieldList(ngctx.componentConfiguration.getParser().getFields())).toList();

            Dataset<Row> df = ctx.sparkSession.createDataFrame(outputRdd.rdd(), internalSchema).select(outputColumns);

            logger.debug("Output rdd length = " + recCounter.value() +"\n");
            logger.debug("Rejected rdd length = " + errCounter.value() +"\n");
            logger.debug("Dest dir for file " + file + " = " + destDir +"\n");

        rc = commitDataSetFromDSMap(ngctx, df, outputDataSetName, destDir.toString(), Output.Mode.APPEND.toString());
            logger.debug("************************************** Dest dir for file " + file + " = " + destDir +"\n");


            logger.debug("Write dataset status = " + rc);
        }
        else {

            outputColumns =
                scala.collection.JavaConversions.asScalaBuffer(
                    createParserOutputFieldList(ngctx.componentConfiguration.getParser().getOutputFieldsList())).toList();

            Dataset<Row> df = ctx.sparkSession.createDataFrame(outputRdd.rdd(), internalSchema).select(outputColumns);

            Map<String,String> columnRenameList = createDestinationFieldList(ngctx.componentConfiguration.getParser().getOutputFieldsList());

            Dataset filterOutputDS = null;
            Dataset renameOutputDS = df;

            for (Map.Entry<String, String> e : columnRenameList.entrySet()) {
                String origin = e.getKey();
                String destination = e.getValue();
                filterOutputDS = renameOutputDS.withColumnRenamed(origin,destination);
                renameOutputDS = filterOutputDS;
            }

            logger.debug("Output rdd length = " + recCounter.value() +"\n");
            logger.debug("Rejected rdd length = " + errCounter.value() +"\n");
            logger.debug("Dest dir for file " + file + " = " + destDir +"\n");

            logger.debug("************************************** Dest dir for file " + file + " = " + destDir +"\n");

            filterOutputDS.printSchema();
            filterOutputDS.show(5);

            rc = commitDataSetFromDSMap(ngctx, filterOutputDS, outputDataSetName, destDir.toString(), "append");

            logger.debug("Write dataset status = " + rc);
        }


        //Filter out Accepted Data
        collectAcceptedData(parseRdd,outputRdd);

        logger.debug("Write dataset status = " + rc);

        //Filter out Rejected Data
        collectRejectedData(parseRdd, outputRdd);
        return rc;
    }
    
    private int parseDataFrame(Dataset<String> dataFrame, Path destDir){
    	JavaRDD<String> rdd = dataFrame.rdd().toJavaRDD();
        logger.debug("parsing dataframe starts here");
        logger.debug("Headersize is: " + headerSize);
    	JavaRDD<Row> parseRdd = null;
    	if (headerSize >= 1) {
			parseRdd = rdd
					// Add line numbers
					.zipWithIndex()
					// Filter out header based on line number
					.filter(new HeaderFilter(headerSize))
					// Get rid of file numbers
					.keys().map(new ConvertToRow(schema, tsFormats, lineSeparator, delimiter, quoteChar,
							quoteEscapeChar, '\'', recCounter, errCounter));
		} else {
			parseRdd = rdd.map(new ConvertToRow(schema, tsFormats, lineSeparator, delimiter, quoteChar, quoteEscapeChar,
					'\'', recCounter, errCounter));
		}
	    // Create output dataset
        scala.collection.Seq<Column> outputColumns =
            scala.collection.JavaConversions.asScalaBuffer(
                createFieldList(ngctx.componentConfiguration.getParser().getFields())).toList();
        JavaRDD<Row> rejectedRdd = getRejectedData(parseRdd);
        logger.debug("Rejected rdd count in data frame :: "+ rejectedRdd.count());
        JavaRDD<Row> outputRdd = getOutputData(parseRdd);
        Dataset<Row> localDataFrame = ctx.sparkSession.createDataFrame(outputRdd.rdd(), internalSchema).select(outputColumns);
        collectAcceptedData(parseRdd,outputRdd);
        logger.debug("Output rdd length in data frame = " + recCounter.value() +"\n");
        logger.debug("Rejected rdd length in data frame = " + errCounter.value() +"\n");
        logger.debug("Dest dir for file in data frame = " + destDir +"\n");
        int rc = 0;
        rc = commitDataSetFromDSMap(ngctx, localDataFrame, outputDataSetName, destDir.toString(), Output.Mode.APPEND.toString());
        logger.debug("Write dataset status = " + rc);
        //Filter out Rejected Data
        collectRejectedData(parseRdd, outputRdd);
        logger.debug("parsing dataframe ends here");
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
        	logger.error("Exception is : " + e + "\n");
            System.exit(-1);
        }
    }
    
    
    private static List<Column> createParserOutputFieldList(List<OutputFieldsList> outputs){

        List<Column> retval = new ArrayList<>(outputs.size());
        for(OutputFieldsList output : outputs){
            retval.add(new Column(output.getName()));
        }
        return retval;
    }

    private static Map createDestinationFieldList(List<OutputFieldsList> outputs){

        Map<String,String> hmap = new HashMap();
        for(OutputFieldsList output : outputs){
            if (output.getDestinationName() != null ) {
                hmap.put(output.getName(),output.getDestinationName());
            }
        }
        logger.debug("createDestinationFieldList ************************** " + hmap.toString());
        return hmap;
    }

}
