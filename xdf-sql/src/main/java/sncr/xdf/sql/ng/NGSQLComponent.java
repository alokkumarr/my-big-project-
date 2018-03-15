package sncr.xdf.sql.ng;

import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import sncr.bda.conf.ComponentConfiguration;
import sncr.bda.conf.Sql;
import sncr.bda.core.file.HFileOperations;
import sncr.bda.datasets.conf.DataSetProperties;
import sncr.xdf.component.*;
import sncr.xdf.context.NGContext;
import sncr.xdf.exceptions.XDFException;
import sncr.xdf.ngcomponent.AbstractComponent;
import sncr.xdf.sql.SQLDescriptor;
import sncr.xdf.sql.SQLMoveDataDescriptor;
import sncr.xdf.adapters.writers.MoveDataDescriptor;

import java.io.FileNotFoundException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * Created by asor0002 on 9/11/2017.
 */
public class NGSQLComponent extends AbstractComponent implements WithDLBatchWriter, WithSpark, WithDataSet {

    private static final Logger logger = Logger.getLogger(NGSQLComponent.class);
    NGJobExecutor executor;

    public NGSQLComponent(NGContext ngctx, boolean useMD, boolean useSample) { super(ngctx, useMD, useSample); }

    public NGSQLComponent() {  super(); }

    {
        componentName = "sql";
    }

    protected int execute(){
        try {
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
            executor = new NGJobExecutor(this, script);
            String tempDir = ngGenerateTempLocation(new DataSetHelper(ctx, services.md),  null, null);
            executor.start(tempDir);
        } catch (Exception e) {
            error = "SQL Executor runtime exception: " + e.getMessage();
            logger.error(e);
            return -1;
        }
        return 0;
    }

    protected int archive(){
        return 0;
    }

    protected ComponentConfiguration validateConfig(String config) throws Exception {
        return NGSQLComponent.analyzeAndValidate(config);
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
        return "SQL Component parameters: \n" +
                ((ctx.componentConfiguration.getSql().getScriptLocation().equalsIgnoreCase("inline"))?" encoded script ":getScriptFullPath());
    }


    private String getScriptFullPath() {
        String sqlScript = ctx.componentConfiguration.getSql().getScriptLocation() + Path.SEPARATOR + ctx.componentConfiguration.getSql().getScript();
        logger.debug(String.format("Get script %s in location: ", sqlScript));
        return sqlScript;
    }


    @Override
    protected int move(){

        if (executor.getResultDataSets() == null ||
            executor.getResultDataSets().size() == 0 )
        {
            logger.warn("Component does not produce any Data Sets");
            return 0;
        }

        Map<String, SQLDescriptor> resultDataDesc = executor.getResultDataSets();
        ngctx.outputDataSets.forEach(
            (on, obDesc) ->
            {
                List<String> kl = (List<String>) obDesc.get(DataSetProperties.PartitionKeys.name());
                String partKeys = on + ": "; for (String s : kl) partKeys += s + " ";

                MoveDataDescriptor desc = new SQLMoveDataDescriptor(
                        resultDataDesc.get(on),         // SQLDescriptor
                        (String) obDesc.get(DataSetProperties.PhysicalLocation.name()),kl);
                ctx.resultDataDesc.add(desc);

                logger.debug(String.format("DataSet %s will be moved to %s, Partitioning: %s",
                        obDesc.get(DataSetProperties.Name.name()),
                        obDesc.get(DataSetProperties.PhysicalLocation.name()), partKeys));

            }
        );
        return super.move();
    }


    public static void main(String[] args){
        NGSQLComponent component = new NGSQLComponent();
        try {
            if (component.initWithCMDParameters(args) == 0) {
                int r = component.run();
                System.exit(r);
            }
        } catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
