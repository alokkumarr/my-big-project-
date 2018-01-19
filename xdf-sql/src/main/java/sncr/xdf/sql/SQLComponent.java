package sncr.xdf.sql;

import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import sncr.bda.core.file.HFileOperations;
import sncr.xdf.exceptions.XDFException;
import sncr.xdf.component.Component;
import sncr.xdf.component.WithDataSetService;
import sncr.xdf.component.WithMovableResult;
import sncr.xdf.component.WithSparkContext;
import sncr.bda.conf.ComponentConfiguration;
import sncr.bda.conf.Sql;
import sncr.bda.datasets.conf.DataSetProperties;

import java.io.FileNotFoundException;
import java.util.Base64;
import java.util.Map;

/**
 * Created by asor0002 on 9/11/2017.
 */
public class SQLComponent extends Component implements WithMovableResult , WithSparkContext, WithDataSetService {

    private static final Logger logger = Logger.getLogger(SQLComponent.class);
    private Map<String, SQLDescriptor> resultDataSets;
    JobExecutor executor;

    {
        componentName = "sql";
    }

    public static void main(String[] args){
        SQLComponent component = new SQLComponent();
        try {
            // Spark based component
            if (component.collectCMDParameters(args) == 0) {
                int r = component.Run();
                System.exit(r);
            }
        } catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    protected int Execute(){
        try {
            executor = new JobExecutor(ctx, inputDataSets, outputDataSets);
            String script;
            if (ctx.componentConfiguration.getSql().getScriptLocation().equalsIgnoreCase("inline")) {
                logger.debug("Script is inline encoded");
                script = new String (Base64.getDecoder().decode(ctx.componentConfiguration.getSql().getScript()));
            }
            else {
                String pathToSQLScript = getScriptFullPath();
                logger.debug("Path to script: " + pathToSQLScript);
                try {
                    script = HFileOperations.readFile(pathToSQLScript);
                } catch (FileNotFoundException e) {
                    throw new XDFException(XDFException.ErrorCodes.ConfigError, e, "Part to SQL script is not correct: " + pathToSQLScript);
                }
            }
            logger.trace("Script to execute:\n" +  script);
            executor.analyze(script);
            String tempDir = generateTempLocation(new WithDataSetService.DataSetServiceAux(ctx, md),  null, null);
            executor.start(tempDir);
        } catch (Exception e) {
            error = "SQL Executor runtime exception: " + e.getMessage();
            logger.error(e);
            return -1;
        }
        return 0;
    }

    protected int Archive(){
        return 0;
    }

    protected ComponentConfiguration validateConfig(String config) throws Exception {
        return SQLComponent.analyzeAndValidate(config);
    }

    public static ComponentConfiguration analyzeAndValidate(String cfgAsStr) throws Exception {

        ComponentConfiguration compConf = Component.analyzeAndValidate(cfgAsStr);

        Sql sparkSQLProps = compConf.getSql();
        if (sparkSQLProps == null) {
            throw new XDFException(XDFException.ErrorCodes.NoComponentDescriptor, "sql");
        }
        if (sparkSQLProps.getScript() == null || sparkSQLProps.getScript().isEmpty()) {
            throw new XDFException(XDFException.ErrorCodes.ConfigError, "Incorrect configuration: Spark SQL does not have SQL script name.");
        }
        if (sparkSQLProps.getScriptLocation() == null || sparkSQLProps.getScriptLocation().isEmpty()) {
            throw new XDFException(XDFException.ErrorCodes.ConfigError, "Incorrect configuration: Spark SQL descriptor does not have SQL script location.");
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
        logger.debug(String.format("Get script %s in location: ", sqlScript));
        return sqlScript;
    }


    @Override
    protected int Move(){

        if (executor.getResultDataSets() == null ||
            executor.getResultDataSets().size() == 0 )
        {
            logger.warn("Component does not produce any Data Sets");
            return 0;
        }

        resultDataSets = executor.getResultDataSets();
        outputDataSets.forEach(
            (on, obDesc) ->
            {
                MoveDataDescriptor desc = new SQLMoveDataDescriptor(
                    resultDataSets.get(on),                                     // SQLDescriptor
                    obDesc.get(DataSetProperties.PhysicalLocation.name()));
                resultDataDesc.add(desc);
                logger.debug(String.format("DataSet %s will be moved to %s",
                        obDesc.get(DataSetProperties.Name.name()),
                        obDesc.get(DataSetProperties.PhysicalLocation.name()) ));
            }
        );
        return super.Move();
    }

}
