package sncr.xdf.transformer;

import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.types.*;
import org.apache.spark.sql.types.Metadata;
import sncr.bda.conf.*;
import sncr.bda.core.file.HFileOperations;
import sncr.xdf.component.Component;
import sncr.xdf.component.WithDataSetService;
import sncr.xdf.component.WithMovableResult;
import sncr.xdf.component.WithSparkContext;
import sncr.bda.datasets.conf.DataSetProperties;
import sncr.xdf.exceptions.XDFException;

import java.io.FileNotFoundException;
import java.util.*;

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
           if (component.collectCMDParameters(args) == 0) {
                int r = component.Run();
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
    protected int Execute(){
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
            logger.debug("Script to execute:\n" +  script);


//2. Read input datasets
//TODO:: Some of datasets may be regarded as reference data, add reference data as Json array to Transformer configuration.

            Map<String, Dataset> dsMap = new HashMap();
            for ( Map.Entry<String, Map<String, String>> entry : inputs.entrySet()) {
                Map<String, String> desc = entry.getValue();
                String loc = desc.get(DataSetProperties.PhysicalLocation.name());
                String format = desc.get(DataSetProperties.Format.name());
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
                dsMap.put(entry.getKey(), ds);
            }
            Transformer.ScriptEngine engine = ctx.componentConfiguration.getTransformer().getScriptEngine();
            Set<OutputSchema> ou = ctx.componentConfiguration.getTransformer().getOutputSchema();
            if (ou != null && ou.size() > 0){

                StructType st = createSchema(ou);

                //3. Based of configuration run Jexl or Janino engine.
                if (engine == Transformer.ScriptEngine.JEXL) {
                    JexlExecutorWithSchema jexlExecutorWithSchema  =
                            new JexlExecutorWithSchema(ctx.sparkSession,
                                    script,
                                    st,
                                    tempLocation,
                                    0,
                                    inputs,
                                    outputs);
                    jexlExecutorWithSchema.execute(dsMap);
                } else if (engine == Transformer.ScriptEngine.JANINO) {

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
                            new JexlExecutor(ctx.sparkSession,
                                    script,
                                    tempLocation,
                                    0,
                                    inputs,
                                    outputs);
                    jexlExecutor.execute(dsMap);
                } else if (engine == Transformer.ScriptEngine.JANINO) {

                } else {
                    error = "Unsupported transformation engine: " + engine;
                    logger.error(error);
                    return -1;
                }
            }
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
            logger.debug(String.format("Field %s, index: %d, type: %s",osa[i].getName(), i, getType(osa[i].getType(), osa[i].getFormat()) ));
           sf[i] = new StructField(osa[i].getName(), getType(osa[i].getType(), osa[i].getFormat()), true, Metadata.empty());
           st = st.add(sf[i]);
        }
        st = st.add( new StructField(RECORD_COUNTER, DataTypes.LongType, true, Metadata.empty()));
        st = st.add( new StructField(TRANSFORMATION_RESULT, DataTypes.IntegerType, true, Metadata.empty()));
        st = st.add( new StructField(TRANSFORMATION_ERRMSG, DataTypes.StringType, true, Metadata.empty()));
        logger.debug("Output schema: " + st.prettyJson() );
        return st;
    }


    private DataType getType(String tp, String frmt) throws Exception {
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

    protected int Archive(){
        return 0;
    }

    @Override
    protected int Move(){

        List<Map<String, String>> dss = new ArrayList<>();
        dss.add(outputs.get(RequiredNamedParameters.Output.toString()));
        dss.add(outputs.get(RequiredNamedParameters.Rejected.toString()));
        for ( Map<String, String> ads : dss) {
            String name = ads.get(DataSetProperties.Name.name());
            String src = tempLocation + Path.SEPARATOR + name;
            String dest = ads.get(DataSetProperties.PhysicalLocation.name());
            String mode = ads.get(DataSetProperties.Mode.name());
            String format = ads.get(DataSetProperties.Format.name());
            MoveDataDescriptor desc = new MoveDataDescriptor(src, dest, name, mode, format);
            resultDataDesc.add(desc);
            logger.debug(String.format("DataSet %s will be moved to %s", name, dest));
        }
        return super.Move();
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


