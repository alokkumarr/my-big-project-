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
import sncr.bda.core.file.HFileOperations;
import sncr.bda.datasets.conf.DataSetProperties;
import sncr.xdf.adapters.writers.MoveDataDescriptor;
import sncr.xdf.context.ComponentServices;
import sncr.xdf.context.NGContext;
import sncr.xdf.file.DLDataSetOperations;
import sncr.xdf.exceptions.XDFException;
import sncr.xdf.ngcomponent.*;
import sncr.xdf.parser.spark.ConvertToRow;
import sncr.xdf.preview.CsvInspectorRowProcessor;
import sncr.xdf.services.NGContextServices;
import sncr.xdf.services.WithDataSet;
import sncr.xdf.services.WithProjectScope;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


//TODO:: Refactor AsynchNGParser and NGParser: eliminate duplicate
public class AsynchNGParser extends AsynchAbstractComponent implements WithDLBatchWriter, WithSpark, WithDataSet, WithProjectScope {

    private static final Logger logger = Logger.getLogger(AsynchNGParser.class);

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

    public AsynchNGParser(NGContext ngctx, ComponentServices[] cs) { super(ngctx, cs); }

    {
        componentName = "parser";
    }

    public AsynchNGParser(NGContext ngctx) {  super(ngctx); }

    public AsynchNGParser() {  super(); }


    protected int execute(){

        int retval = 0;

        sourcePath = ngctx.componentConfiguration.getParser().getFile();
        headerSize = ngctx.componentConfiguration.getParser().getHeaderSize();
        tempDir = generateTempLocation(new DataSetHelper(ngctx, services.md),
                        ngctx.batchID,
                        ngctx.componentName,
                        null, null);
        lineSeparator = ngctx.componentConfiguration.getParser().getLineSeparator();
        delimiter = ngctx.componentConfiguration.getParser().getDelimiter().charAt(0);
        quoteChar = ngctx.componentConfiguration.getParser().getQuoteChar().charAt(0);
        quoteEscapeChar = ngctx.componentConfiguration.getParser().getQuoteEscape().charAt(0);

        errCounter = ctx.sparkSession.sparkContext().longAccumulator("ParserErrorCounter");
        recCounter = ctx.sparkSession.sparkContext().longAccumulator("ParserRecCounter");

        schema = createSchema(ngctx.componentConfiguration.getParser().getFields(), false);
        tsFormats = createTsFormatList(ngctx.componentConfiguration.getParser().getFields());
        logger.info(tsFormats);

        internalSchema = createSchema(ngctx.componentConfiguration.getParser().getFields(), true);

        // Output data set
        if(ngctx.outputDataSets.size() != 1){
            // error - must be only one for parser
            logger.error("Found multiple output data set definitions "+ ngctx.outputDataSets.size());
            //return -1;
        }

        Map.Entry<String, Map<String, Object>> ds =  (Map.Entry<String, Map<String, Object>>)ngctx.outputDataSets.entrySet().toArray()[0];
        outputDataSetName = ds.getKey();
        outputDataSetLocation = (String) ds.getValue().get(DataSetProperties.PhysicalLocation.name());
        outputFormat = (String) ds.getValue().get(DataSetProperties.Format.name());
        outputNOF =  (Integer) ds.getValue().get(DataSetProperties.NumberOfFiles.name());
        pkeys = (List<String>) ds.getValue().get(DataSetProperties.PartitionKeys.name());

        logger.info("Output data set " + outputDataSetName + " located at " + outputDataSetLocation + " with format " + outputFormat);

        FileSystem fs = HFileOperations.getFileSystem();

        try {

            if (ctx.fs.exists(new Path(tempDir)))
                HFileOperations.deleteEnt(tempDir);


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
            logger.error("IO error: " + ExceptionUtils.getFullStackTrace(e));
            retval =  -1;
        } catch (Exception e) {
            logger.error("Error: " + ExceptionUtils.getFullStackTrace(e));
            retval =  -1;
        }
        return retval;
    }

    protected ComponentConfiguration validateConfig(String config) throws Exception {
        return AsynchNGParser.analyzeAndValidate(config);
    }

    public static ComponentConfiguration analyzeAndValidate(String config) throws Exception {

        ComponentConfiguration compConf = AsynchAbstractComponent.analyzeAndValidate(config);
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

    // Parse data with headers - we have to do this file by file
    private int parseFiles(FileStatus[] files, String mode){
        // Files
        String tempPath = tempDir;
        for (FileStatus file : files) {
            if (file.isFile()) {
                 // + Path.SEPARATOR + file.getPath().getName();
                if (parseSingleFile(file.getPath(), new Path(tempPath)) == 0) {
                }
            }
        }
        ctx.resultDataDesc.add(new MoveDataDescriptor(tempPath + Path.SEPARATOR + outputDataSetName, outputDataSetLocation, outputDataSetName, mode, outputFormat,pkeys));
        return 0;
    }

    private int parseSingleFile(Path file, Path destDir){
        logger.info("Parsing " + file + " to " + destDir);
        logger.info("Header size : " + headerSize);

        JavaRDD<Row> rdd = reader.readToRDD(file.toString(), headerSize)
                .map(new ConvertToRow(  schema,
                                tsFormats,
                                lineSeparator,
                                delimiter,
                                quoteChar,
                                quoteEscapeChar,
                                '\'',
                                recCounter,
                                errCounter));


        Dataset<Row> df = ctx.sparkSession.createDataFrame(rdd.rdd(), internalSchema);
        // TODO: Filter out all rejected records
//        logger.debug ( "Created rdd:  " + rdd.rdd().count());
        logger.debug ( "Created df:  " + df.count() + " schema: " + df.schema().prettyJson());

            scala.collection.Seq<Column> scalaList=
            scala.collection.JavaConversions.asScalaBuffer(createFieldList(ngctx.componentConfiguration.getParser().getFields())).toList();
        Column cond = df.col("__REJ_FLAG").isNull().or(df.col("__REJ_FLAG").equalTo(0));
        Dataset<Row> filteredDataset = df.select(scalaList).where(cond);
        logger.debug ( "Filtered df:  " + filteredDataset.count());
        int rc = commitDataSetFromDSMap(ngctx, filteredDataset, outputDataSetName, destDir.toString(), "append");
        return rc;
    }

    // Parse data without headers
    int parse(String mode){
        int rc = parseSingleFile(new Path(sourcePath), new Path(tempDir));

        if (rc != 0){
            error = "Could not parse file: " + sourcePath;
            logger.error(error);
            return rc;
        }
        ctx.resultDataDesc.add(new MoveDataDescriptor(tempDir, outputDataSetLocation, outputDataSetName, mode, outputFormat,pkeys));
        return 0;
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


    public static void main(String[] args) {

      NGContextServices ngCtxSvc;
      CliHandler cli = new CliHandler();
      try {
        HFileOperations.init();

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
        ComponentConfiguration cfg = AsynchNGParser.analyzeAndValidate(configAsStr);
        ngCtxSvc = new NGContextServices(pcs, xdfDataRootSys, cfg, appId, "parser", batchId);
        ngCtxSvc.initContext();
        ngCtxSvc.registerOutputDataSet();
        logger.debug("Output datasets:");
        ngCtxSvc.getNgctx().registeredOutputDSIds.forEach( id ->
            logger.debug(id)
        );
        logger.debug(ngCtxSvc.getNgctx().toString());
        AsynchNGParser component = new AsynchNGParser(ngCtxSvc.getNgctx());
        if (!component.initComponent(null))
            System.exit(-1);
        int rc = component.run();
        System.exit(rc);
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(-1);
      }
    }

}
