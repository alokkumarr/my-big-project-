package sncr.xdf.transformer.ng;

import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.types.*;
import sncr.bda.CliHandler;
import sncr.bda.ConfigLoader;
import sncr.bda.base.MetadataBase;
import sncr.bda.conf.*;
import sncr.bda.core.file.HFileOperations;
import sncr.bda.datasets.conf.DataSetProperties;
import sncr.xdf.adapters.writers.MoveDataDescriptor;
import sncr.xdf.context.ComponentServices;
import sncr.xdf.context.NGContext;
import sncr.xdf.exceptions.XDFException;
import sncr.xdf.ngcomponent.*;
import sncr.xdf.services.NGContextServices;
import sncr.xdf.services.WithDataSet;
import sncr.xdf.services.WithProjectScope;
import sncr.xdf.context.RequiredNamedParameters;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import sncr.xdf.context.XDFReturnCode;
import sncr.xdf.ngcomponent.util.NGComponentUtil;

/**
 * Created by srya0001 on 12/19/2017.
 * The transformer component is designed to execute only intra-record transformation.
 * The transformation should be presented in format recognized by Janino embedded compiler
 * And may have global context stored in-side Janono-code.
 * The component literally transforms rew-by-row using Spark functions.
 * The component DOES NOT PERFORM any multi-record conversion, use Spark SQL XDF Component
 * if you need to make such transformation.
 */

public class NGTransformerComponent extends AbstractComponent implements WithDLBatchWriter, WithSpark, WithDataSet, WithProjectScope {

    public static String RECORD_COUNTER = "_record_counter";
    public static String TRANSFORMATION_RESULT = "_tr_result";
    public static String TRANSFORMATION_ERRMSG = "_tr_errmsg";

    private static final Logger logger = Logger.getLogger(NGTransformerComponent.class);
    private String tempLocation;

    {
        componentName = "transformer";
    }
    public NGTransformerComponent(NGContext ngctx) {  super(ngctx); }

    public NGTransformerComponent(NGContext ngctx, String mode) {  super(ngctx); }

    public NGTransformerComponent() {  super(); }

    public NGTransformerComponent(NGContext ngctx, ComponentServices[] cs) { super(ngctx, cs); }


    private String getScriptFullPath() {
        String sqlScript = ngctx.componentConfiguration.getTransformer().getScriptLocation() + Path.SEPARATOR +
            ngctx.componentConfiguration.getTransformer().getScript();
        logger.trace(String.format("Get script %s in location: ", sqlScript));
        return sqlScript;
    }
    @Override
    protected int execute(){
        logger.debug("Processing NGTransformerComponent ......" );
        try {
            tempLocation = generateTempLocation(new DataSetHelper(ngctx, services.md),
                null, null);

//1. Read expression/scripts, compile it??
            String script;
            if (ngctx.componentConfiguration.getTransformer().getScriptLocation().equalsIgnoreCase("inline")) {
                logger.trace("Script is inline encoded");
                script = new String (Base64.getDecoder().decode(ngctx.componentConfiguration.getTransformer().getScript()));
            }
            else {
                String pathToSQLScript = getScriptFullPath();
                logger.trace("Path to script: " + pathToSQLScript);
                try {
                    script = HFileOperations.readFile(pathToSQLScript);
                } catch (FileNotFoundException e) {
                    throw new XDFException(XDFReturnCode.CONFIG_ERROR, e, "Path to Jexl/Janino script is not correct: " + pathToSQLScript);
                }
            }
            logger.trace("Script to execute:\n" +  script);


//2. Read input datasets
            Map<String, Dataset> dsMap = new HashMap();
            long inputDSCount = 0;
            if (ngctx.runningPipeLine) {
                String transInKey =  ngctx.componentConfiguration.getInputs().get(0).getDataSet().toString();
                Dataset ds = ngctx.datafileDFmap.get(transInKey);
                if(ds == null) {
                    throw new XDFException(XDFReturnCode.INPUT_DATA_OBJECT_NOT_FOUND, transInKey);
                }
                inputDSCount = ds.count();
                if(ngctx.componentConfiguration.isErrorHandlingEnabled() && inputDSCount == 0) {
                    throw new XDFException(XDFReturnCode.INPUT_DATA_EMPTY_ERROR, transInKey);
                }
            }else{
                for ( Map.Entry<String, Map<String, Object>> entry : ngctx.inputs.entrySet()) {
                    Map<String, Object> desc = entry.getValue();
                    String loc = (String) desc.get(DataSetProperties.PhysicalLocation.name());
                    String format = (String) desc.get(DataSetProperties.Format.name());
                    String transInKey =  (String) desc.get(DataSetProperties.Name.name());
                    if(!HFileOperations.getFileSystem().exists(new Path(loc))){
                        throw new XDFException(XDFReturnCode.INPUT_DATA_OBJECT_NOT_FOUND, transInKey);
                    }
                    if(ngctx.componentConfiguration.isErrorHandlingEnabled() && HFileOperations.getFileSystem().getContentSummary(new Path(loc)).getLength() == 0){
                        inputDSCount = 0;
                        throw new XDFException(XDFReturnCode.INPUT_DATA_EMPTY_ERROR, transInKey);
                    }
                    Dataset ds = reader.readDataset(entry.getKey(), format, loc);
                    logger.trace("Added to DS map: " + entry.getKey());
                    inputDSCount = ds.count();
                    dsMap.put(entry.getKey(), ds);
                }
            }

            Transformer.ScriptEngine engine = ngctx.componentConfiguration.getTransformer().getScriptEngine();
            Set<OutputSchema> ou = ngctx.componentConfiguration.getTransformer().getOutputSchema();
            if (ou != null && ou.size() > 0){

                StructType st = createSchema(ou);

                //3. Based of configuration run Jexl or Janino engine.
                if (engine == Transformer.ScriptEngine.JEXL) {
                    NGJexlExecutorWithSchema jexlExecutorWithSchema  =
                        new NGJexlExecutorWithSchema(this,
                            script,
                            ngctx.componentConfiguration.getTransformer().getThreshold(),
                            tempLocation,
                            st   );
                    if (ngctx.runningPipeLine)
                        jexlExecutorWithSchema.executeSingleProcessor(ngctx);
                    else
                        jexlExecutorWithSchema.execute(dsMap);
                } else if (engine == Transformer.ScriptEngine.JANINO) {

                    String preamble = "";
                    if ( ngctx.componentConfiguration.getTransformer().getScriptPreamble() != null &&
                        ! ngctx.componentConfiguration.getTransformer().getScriptPreamble().isEmpty())
                        preamble = HFileOperations.readFile(ngctx.componentConfiguration.getTransformer().getScriptPreamble());

                    script = preamble + script;
                    logger.trace( "Script to execute: " + script);

                    String[] odi = ngctx.componentConfiguration.getTransformer().getAdditionalImports().toArray(new String[0]);
                    String m = "Additional imports: ";
                    for (int i = 0; i < odi.length ; i++) m += " " + odi[i];
                    logger.trace(m);

                    NGJaninoExecutor janinoExecutor =
                        new NGJaninoExecutor(this,
                            script,
                            ngctx.componentConfiguration.getTransformer().getThreshold(),
                            tempLocation,
                            st,
                            odi);
                    if (ngctx.runningPipeLine)
                        janinoExecutor.executeSingleProcessor(ngctx);
                    else
                        janinoExecutor.execute(dsMap);
                } else {
                    error = "Unsupported transformation engine: " + engine;
                    logger.error(error);
                    return -1;
                }
            }
            else {
                //3. Based of configuration run Jexl or Janino engine.
                if (engine == Transformer.ScriptEngine.JEXL) {
                    NGJexlExecutor jexlExecutor =
                        new NGJexlExecutor(this,
                            script,
                            ngctx.componentConfiguration.getTransformer().getThreshold(),
                            tempLocation);
                    jexlExecutor.execute(dsMap);
                } else if (engine == Transformer.ScriptEngine.JANINO) {
                    error = "Transformation with Janino engine requires Output schema, dynamic schema mode is not supported";
                    logger.error(error);
                    return -1;
                } else {
                    error = "Unsupported transformation engine: " + engine;
                    logger.error(error);
                    return -1;
                }
            }
            validateOutputDSCounts(inputDSCount, false);
        } catch (Exception e) {
            logger.error("Exception in main transformer module: ",e);
            if (e instanceof XDFException) {
                throw ((XDFException)e);
            }else {
                throw new XDFException(XDFReturnCode.INTERNAL_ERROR, e);
            }
        }
        return 0;
    }


    private StructType createSchema(Set<OutputSchema> outputSchema) throws Exception {

        StructType st = new StructType();
        StructField[] sf = new StructField[outputSchema.size()+3];
        OutputSchema[] osa = outputSchema.toArray(new OutputSchema[0]);
        boolean isFilenameFieldNotExistInSchema = true;
        for (int i = 0; i < osa.length; i++) {
            logger.trace(String.format("Field %s, index: %d, type: %s",osa[i].getName(), i, getType(osa[i].getType()) ));
            sf[i] = new StructField(osa[i].getName(), getType(osa[i].getType()), true, Metadata.empty());
            st = st.add(sf[i]);
            if(isFilenameFieldNotExistInSchema && SIP_FILE_NAME.equalsIgnoreCase(osa[i].getName().trim())){
                isFilenameFieldNotExistInSchema = false;
            }
        }
        if(isFilenameFieldNotExistInSchema){
            st = st.add( new StructField(SIP_FILE_NAME, DataTypes.StringType, true, Metadata.empty()));
        }
        st = st.add( new StructField(RECORD_COUNTER, DataTypes.LongType, true, Metadata.empty()));
        st = st.add( new StructField(TRANSFORMATION_RESULT, DataTypes.IntegerType, true, Metadata.empty()));
        st = st.add( new StructField(TRANSFORMATION_ERRMSG, DataTypes.StringType, true, Metadata.empty()));
        logger.trace("Output schema: " + st.prettyJson() );
        return st;
    }


    private DataType getType(String tp) throws Exception {
        DataType dt = null;
        if (tp == null)
            dt = DataTypes.NullType;
        else if (tp.equalsIgnoreCase("string"))
            dt = DataTypes.StringType;
        else if (tp.equalsIgnoreCase("float") || tp.equalsIgnoreCase("double"))
            dt = DataTypes.DoubleType;
        else if (tp.equalsIgnoreCase("short") || tp.equalsIgnoreCase("int") || tp.equalsIgnoreCase("integer"))
            dt = DataTypes.IntegerType;
        else if (tp.equalsIgnoreCase("long"))
            dt = DataTypes.LongType;
        else if (tp.equalsIgnoreCase("timestamp"))
            dt = DataTypes.TimestampType;
        else if (tp.equalsIgnoreCase("boolean"))
            dt = DataTypes.BooleanType;
        else{
            throw new Exception("Unsupported data type: " + tp );
        }
        return dt;
    }

    protected int archive(){
        return 0;
    }

    @Override
    protected int move(){

        List<Map<String, Object>> dss = new ArrayList<>();
        dss.add(ngctx.outputs.get(RequiredNamedParameters.Output.toString()));
        if(ngctx.outputs != null && ngctx.outputs.get(RequiredNamedParameters.Rejected.toString()) != null) {
        	dss.add(ngctx.outputs.get(RequiredNamedParameters.Rejected.toString()));
        }
        for ( Map<String, Object> ads : dss) {
            String name = (String) ads.get(DataSetProperties.Name.name());
            String src = tempLocation + Path.SEPARATOR + name;
            String dest = (String) ads.get(DataSetProperties.PhysicalLocation.name());
            String mode = (String) ads.get(DataSetProperties.Mode.name());
            String format = (String) ads.get(DataSetProperties.Format.name());
            List<String> kl = (List<String>) ads.get(DataSetProperties.PartitionKeys.name());

            MoveDataDescriptor desc = new MoveDataDescriptor(src, dest, name, mode, format, kl);
            ctx.resultDataDesc .add(desc);
            logger.trace(String.format("DataSet %s will be moved to %s", name, dest));
        }
        return super.move();
    }

    public static void main(String[] args) {

        NGContextServices ngCtxSvc;
        CliHandler cli = new CliHandler();
        NGTransformerComponent component = null;
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

            ComponentServices[] scs =
                {
                    ComponentServices.InputDSMetadata,
                    ComponentServices.OutputDSMetadata,
                    ComponentServices.Project,
                    ComponentServices.TransformationMetadata,
                    ComponentServices.Spark
                };
            cfg = NGContextServices.analyzeAndValidateTransformerConf(configAsStr);
            ngCtxSvc = new NGContextServices(scs, xdfDataRootSys, cfg, appId,
                "transformer", batchId);

            ngCtxSvc.initContext();
            ngCtxSvc.registerOutputDataSet();

            logger.trace("Output datasets:");

            ngCtxSvc.getNgctx().registeredOutputDSIds.forEach( id ->
                logger.trace(id)
            );
            component = new NGTransformerComponent(ngCtxSvc.getNgctx());
            if (component.initComponent(null)) {
                rc = component.run();
                long end_time = System.currentTimeMillis();
                long difference = end_time - start_time;
                logger.info("Transformer total time " + difference);
            }
        }catch (Exception ex) {
            exception = ex;
        }
        rc = NGComponentUtil.handleErrors(Optional.ofNullable(component), Optional.ofNullable(cfg),rc, exception);
        System.exit(rc);
    }
}
