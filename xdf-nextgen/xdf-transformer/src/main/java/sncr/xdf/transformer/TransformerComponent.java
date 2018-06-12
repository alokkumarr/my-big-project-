package sncr.xdf.transformer;


import com.google.gson.JsonElement;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.types.*;
import org.apache.spark.sql.types.Metadata;
import sncr.bda.conf.*;
import sncr.bda.core.file.HFileOperations;
import sncr.xdf.component.*;
import sncr.bda.datasets.conf.DataSetProperties;
import sncr.xdf.context.RequiredNamedParameters;
import sncr.xdf.exceptions.XDFException;
import sncr.xdf.adapters.writers.MoveDataDescriptor;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.Consumer;

/**
 * Created by srya0001 on 12/19/2017.
 * The transformer component is designed to execute only intra-record transformation.
 * The transformation should be presented in format recognized by Janino embedded compiler
 * And may have global context stored in-side Janono-code.
 * The component literally transforms rew-by-row using Spark functions.
 * The component DOES NOT PERFORM any multi-record conversion, use Spark SQL XDF Component
 * if you need to make such transformation.
 */
public class TransformerComponent extends Component implements WithMovableResult, WithSparkContext, WithDataSetService{

    public static String RECORD_COUNTER = "_record_counter";
    public static String TRANSFORMATION_RESULT = "_tr_result";
    public static String TRANSFORMATION_ERRMSG = "_tr_errmsg";

    private static final Logger logger = Logger.getLogger(TransformerComponent.class);
    private String tempLocation;

    {
        componentName = "transformer";
    }

    public static void main(String[] args){
        TransformerComponent component = new TransformerComponent();
        try {
           if (component.collectCommandLineParameters(args) == 0) {
                int r = component.run();
                System.exit(r);
           }
        } catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
    }


    private String getScriptFullPath() {
        String sqlScript = ctx.componentConfiguration.getTransformer().getScriptLocation() + Path.SEPARATOR +
                           ctx.componentConfiguration.getTransformer().getScript();
        logger.debug(String.format("Get script %s in location: ", sqlScript));
        return sqlScript;
    }

    @Override
    protected int execute(){
        try {
             tempLocation = generateTempLocation(new WithDataSetService.DataSetServiceAux(ctx, md),  null, null);

//1. Read expression/scripts, compile it??
            String script;
            if (ctx.componentConfiguration.getTransformer().getScriptLocation().equalsIgnoreCase("inline")) {
                logger.debug("Script is inline encoded");
                script = new String (Base64.getDecoder().decode(ctx.componentConfiguration.getTransformer().getScript()));
            }
            else {
                String pathToSQLScript = getScriptFullPath();
                logger.debug("Path to script: " + pathToSQLScript);
                try {
                    script = HFileOperations.readFile(pathToSQLScript);
                } catch (FileNotFoundException e) {
                    throw new XDFException(XDFException.ErrorCodes.ConfigError, e, "Path to Jexl/Janino script is not correct: " + pathToSQLScript);
                }
            }
            logger.trace("Script to execute:\n" +  script);


//2. Read input datasets

            Map<String, Dataset> dsMap = new HashMap();
            for ( Map.Entry<String, Map<String, Object>> entry : inputs.entrySet()) {
                Map<String, Object> desc = entry.getValue();
                String loc = (String) desc.get(DataSetProperties.PhysicalLocation.name());
                String format = (String) desc.get(DataSetProperties.Format.name());
                Dataset ds = null;
                switch (format.toLowerCase()) {
                    case "json":
                        ds = ctx.sparkSession.read().json(loc); break;
                    case "parquet":
                        ds = ctx.sparkSession.read().parquet(loc); break;
                    default:
                        error = "Unsupported data format: " + format;
                        logger.error(error);
                        return -1;
                }
                logger.debug("Added to DS map: " + entry.getKey());
                dsMap.put(entry.getKey(), ds);
            }
            Transformer.ScriptEngine engine = ctx.componentConfiguration.getTransformer().getScriptEngine();
            Set<OutputSchema> ou = ctx.componentConfiguration.getTransformer().getOutputSchema();
            if (ou != null && ou.size() > 0){

                StructType st = createSchema(ou);

                //3. Based of configuration run Jexl or Janino engine.
                if (engine == Transformer.ScriptEngine.JEXL) {
                    JexlExecutorWithSchema jexlExecutorWithSchema  =
                            new JexlExecutorWithSchema(ctx.sparkSession, script, st,
                                    tempLocation,ctx.componentConfiguration.getTransformer().getThreshold(), inputs, outputs);
                    jexlExecutorWithSchema.execute(dsMap);
                } else if (engine == Transformer.ScriptEngine.JANINO) {

                    String preamble = "";
                    if ( ctx.componentConfiguration.getTransformer().getScriptPreamble() != null &&
                         ! ctx.componentConfiguration.getTransformer().getScriptPreamble().isEmpty())
                        preamble = HFileOperations.readFile(ctx.componentConfiguration.getTransformer().getScriptPreamble());


                    script = preamble + script;
                    logger.trace( "Script to execute: " + script);

                    String[] odi = ctx.componentConfiguration.getTransformer().getAdditionalImports().toArray(new String[0]);
                    String m = "Additional imports: ";
                    for (int i = 0; i < odi.length ; i++) m += " " + odi[i];
                    logger.debug(m);

                     JaninoExecutor janinoExecutor =
                         new JaninoExecutor(ctx.sparkSession, script, st,
                                 tempLocation,ctx.componentConfiguration.getTransformer().getThreshold(), inputs, outputs, odi);
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
                    JexlExecutor jexlExecutor =
                            new JexlExecutor(ctx.sparkSession, script,
                                    tempLocation, ctx.componentConfiguration.getTransformer().getThreshold(), inputs, outputs);
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

            Consumer<Map<String, Object>> f = ds ->
            {
                if (ds != null) {
                    String dsname = (String) ds.get(DataSetProperties.Name.name());
                    JsonElement je = (JsonElement) ds.get(DataSetProperties.Schema.name());
                    Map<String, Object> outputDS2 = outputDataSets.get(dsname);
                    outputDS2.put(DataSetProperties.Schema.name(), je);
                    logger.trace("Update output DS [" + dsname + "] descriptor with schema: " + outputDS2.get(DataSetProperties.Schema.name()));
                }
            };
            f.accept(outputs.get(RequiredNamedParameters.Output.toString()));
            f.accept(outputs.get(RequiredNamedParameters.Rejected.toString()));

        }
        catch(Exception e){
            logger.error("Exception in main transformer module: ", e);
            error = e.getMessage();
            return -1;
        }
        return 0;
    }

    private StructType createSchema(Set<OutputSchema> outputSchema) throws Exception {

        StructType st = new StructType();
        StructField[] sf = new StructField[outputSchema.size()+3];
        OutputSchema[] osa = outputSchema.toArray(new OutputSchema[0]);
        for (int i = 0; i < osa.length; i++) {
            logger.debug(String.format("Field %s, index: %d, type: %s",osa[i].getName(), i, getType(osa[i].getType()) ));
           sf[i] = new StructField(osa[i].getName(), getType(osa[i].getType()), true, Metadata.empty());
           st = st.add(sf[i]);
        }
        st = st.add( new StructField(RECORD_COUNTER, DataTypes.LongType, true, Metadata.empty()));
        st = st.add( new StructField(TRANSFORMATION_RESULT, DataTypes.IntegerType, true, Metadata.empty()));
        st = st.add( new StructField(TRANSFORMATION_ERRMSG, DataTypes.StringType, true, Metadata.empty()));
        logger.debug("Output schema: " + st.prettyJson() );
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
        dss.add(outputs.get(RequiredNamedParameters.Output.toString()));
        dss.add(outputs.get(RequiredNamedParameters.Rejected.toString()));
        for ( Map<String, Object> ads : dss) {
            String name = (String) ads.get(DataSetProperties.Name.name());
            String src = tempLocation + Path.SEPARATOR + name;
            String dest = (String) ads.get(DataSetProperties.PhysicalLocation.name());
            String mode = (String) ads.get(DataSetProperties.Mode.name());
            String format = (String) ads.get(DataSetProperties.Format.name());
            List<String> kl = (List<String>) ads.get(DataSetProperties.PartitionKeys.name());

            MoveDataDescriptor desc = new MoveDataDescriptor(src, dest, name, mode, format, kl);
            resultDataDesc.add(desc);
            logger.debug(String.format("DataSet %s will be moved to %s", name, dest));
        }
        return super.move();
    }

    protected ComponentConfiguration validateConfig(String config) throws Exception {
        return TransformerComponent.analyzeAndValidate(config);
    }

    public static ComponentConfiguration analyzeAndValidate(String cfgAsStr) throws Exception {

        ComponentConfiguration compConf = Component.analyzeAndValidate(cfgAsStr);
        Transformer transformerCfg = compConf.getTransformer();
        if (transformerCfg == null)
            throw new XDFException(XDFException.ErrorCodes.NoComponentDescriptor, "transformer");

        if (transformerCfg.getScript() == null || transformerCfg.getScript().isEmpty()) {
            throw new XDFException(XDFException.ErrorCodes.ConfigError, "Incorrect configuration: Transformer descriptor does not have script name.");
        }
        if (transformerCfg.getScriptLocation() == null || transformerCfg.getScriptLocation().isEmpty()) {
            throw new XDFException(XDFException.ErrorCodes.ConfigError, "Incorrect configuration: Transformer descriptor does not have script location.");
        }

        boolean valid = false;
        for( Input inpK: compConf.getInputs()){
            if (inpK.getName() != null && inpK.getName().equalsIgnoreCase(RequiredNamedParameters.Input.toString())){
                valid = true; break;
            }
        }

        if (!valid) throw new XDFException(XDFException.ErrorCodes.ConfigError, "Incorrect configuration: dataset parameter with name 'input' does not exist .");

        valid = false;
        boolean rvalid = false;
        for( Output outK: compConf.getOutputs()) {
            if (outK.getName() != null && outK.getName().equalsIgnoreCase(RequiredNamedParameters.Output.toString())) {
                valid = true;
            } else if (outK.getName() != null && outK.getName().equalsIgnoreCase(RequiredNamedParameters.Rejected.toString())) {
                rvalid = true;
            }
        }
        if (!valid || !rvalid) throw new XDFException(XDFException.ErrorCodes.ConfigError, "Incorrect configuration: dataset parameter with name 'output/rejecteds' does not exist .");

        return compConf;
    }

    @Override
    protected String mkConfString() {
        return null;
    }





}


