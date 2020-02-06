package sncr.xdf.sql;

import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sncr.bda.base.MetadataBase;
import sncr.bda.core.file.HFileOperations;
import sncr.xdf.alert.AlertQueueManager;
import sncr.xdf.component.*;
import sncr.xdf.exceptions.XDFException;
import sncr.bda.conf.ComponentConfiguration;
import sncr.bda.conf.Sql;
import sncr.bda.datasets.conf.DataSetProperties;
import sncr.xdf.adapters.writers.MoveDataDescriptor;

import java.io.FileNotFoundException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import sncr.xdf.context.XDFReturnCode;

/**
 * Created by asor0002 on 9/11/2017.
 */
public class SQLComponent extends Component implements WithMovableResult, WithSparkContext, WithDataSetService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLComponent.class);
    private Map<String, SQLDescriptor> resultDataSets;
    JobExecutor executor;

    {
        componentName = "sql";
    }

    public static void main(String[] args){
        SQLComponent component = new SQLComponent();
        try {
            // Spark based component
            if (component.collectCommandLineParameters(args) == 0) {
                int r = component.run();
                System.exit(r);
            }
        } catch (Exception e){
            System.exit(-1);
        }
    }

    protected int execute(){
        try {
            executor = new JobExecutor(ctx, inputDataSets, outputDataSets);
            String script;
            if (ctx.componentConfiguration.getSql().getScriptLocation().equalsIgnoreCase("inline")) {
                LOGGER.debug("Script is inline encoded");
                script = new String (Base64.getDecoder().decode(ctx.componentConfiguration.getSql().getScript()));
            }
            else {
                String pathToSQLScript = getScriptFullPath();
                LOGGER.trace("Path to script: {}" + pathToSQLScript);
                try {
                    script = HFileOperations.readFile(pathToSQLScript);
                } catch (FileNotFoundException e) {
                    throw new XDFException(XDFReturnCode.CONFIG_ERROR, e, "Part to SQL script is not correct: " + pathToSQLScript);
                }
            }
            LOGGER.trace("Script to execute:\n" +  script);
            executor.analyze(script);
            String tempDir = generateTempLocation(new WithDataSetService.DataSetServiceAux(ctx, md),  null, null);
            int status = executor.start(tempDir);
            // check if Alert is enabled for the component and send the message to queue.
            if (ctx.componentConfiguration.getSql().getAlerts()!=null &&
                ctx.componentConfiguration.getSql().getAlerts().getDatapod()!=null)
            {
                String metadataBasePath = System.getProperty(MetadataBase.XDF_DATA_ROOT);
                AlertQueueManager alertQueueManager = new AlertQueueManager(metadataBasePath);
                Long createdTime = System.currentTimeMillis();
                alertQueueManager.sendMessageToStream(ctx.componentConfiguration.getSql()
                    .getAlerts().getDatapod(),createdTime
                );
                LOGGER.info("Alert configure for the dataset sent notification to stream");
            }
            return status;
        } catch (Exception e) {
            error = "SQL Executor runtime exception: {}" + e.getMessage();
            LOGGER.error(error);
            return -1;
        }
    }

    protected int archive(){
        return 0;
    }

    protected ComponentConfiguration validateConfig(String config) throws Exception {
        return SQLComponent.analyzeAndValidate(config);
    }

    public static ComponentConfiguration analyzeAndValidate(String cfgAsStr) throws Exception {

        ComponentConfiguration compConf = Component.analyzeAndValidate(cfgAsStr);

        Sql sparkSQLProps = compConf.getSql();
        if (sparkSQLProps == null) {
            throw new XDFException(XDFReturnCode.NO_COMPONENT_DESCRIPTOR, "sql");
        }
        if (sparkSQLProps.getScript() == null || sparkSQLProps.getScript().isEmpty()) {
            throw new XDFException(XDFReturnCode.CONFIG_ERROR, "Incorrect configuration: Spark SQL does not have SQL script name.");
        }
        if (sparkSQLProps.getScriptLocation() == null || sparkSQLProps.getScriptLocation().isEmpty()) {
            throw new XDFException(XDFReturnCode.CONFIG_ERROR, "Incorrect configuration: Spark SQL descriptor does not have SQL script location.");
        }
        return compConf;
    }

    @Override
    protected String mkConfString() {
        String s = "SQL Component parameters: \n" +
                ((ctx.componentConfiguration.getSql().getScriptLocation().equalsIgnoreCase("inline"))?" encoded script ":getScriptFullPath());
        return s;
    }


    private String getScriptFullPath() {
        String sqlScript = ctx.componentConfiguration.getSql().getScriptLocation() + Path.SEPARATOR + ctx.componentConfiguration.getSql().getScript();
        LOGGER.debug(String.format("Get script %s in location: ", sqlScript));
        return sqlScript;
    }


    @Override
    protected int move(){

        if (executor.getResultDataSets() == null ||
            executor.getResultDataSets().size() == 0 )
        {
            LOGGER.warn("Component does not produce any Data Sets");
            return 0;
        }

        resultDataSets = executor.getResultDataSets();

        LOGGER.debug("Result data sets : {} ", resultDataSets);
        LOGGER.debug("Output data sets : {}", outputDataSets);
        outputDataSets.forEach(
            (on, obDesc) ->
            {
                SQLDescriptor descriptor = resultDataSets.get(on);
                LOGGER.debug("SQL Descriptor for " + on + " = " + descriptor);

                if (descriptor != null) {
                    LOGGER.info("Generating MoveDataDescriptor for {} description {}",on, obDesc);
                    List<String> kl = (List<String>) obDesc.get(DataSetProperties.PartitionKeys.name());
                    String partKeys = on + ": ";

                    for (String s : kl) partKeys += s + " ";

                    MoveDataDescriptor desc = new SQLMoveDataDescriptor(
                            resultDataSets.get(on),         // SQLDescriptor
                            (String) obDesc.get(DataSetProperties.PhysicalLocation.name()),kl);
                    resultDataDesc.add(desc);

                    LOGGER.debug(String.format("DataSet %s will be moved to %s, Partitioning: %s",
                            obDesc.get(DataSetProperties.Name.name()),
                            obDesc.get(DataSetProperties.PhysicalLocation.name()), partKeys));
                }

            }
        );
        return super.move();
    }

}
