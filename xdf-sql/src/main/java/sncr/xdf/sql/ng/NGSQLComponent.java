package sncr.xdf.sql.ng;

import org.apache.log4j.Logger;
import sncr.bda.conf.ComponentConfiguration;
import sncr.bda.conf.Sql;
import sncr.bda.datasets.conf.DataSetProperties;
import sncr.xdf.exceptions.XDFException;
import sncr.xdf.ngcomponent.*;
import sncr.xdf.services.WithDataSet;
import sncr.xdf.services.WithProjectScope;
import sncr.xdf.sql.SQLDescriptor;
import sncr.xdf.sql.SQLMoveDataDescriptor;
import sncr.xdf.adapters.writers.MoveDataDescriptor;

import java.util.List;
import java.util.Map;

/**
 * Created by asor0002 on 9/11/2017.
 */
public class NGSQLComponent extends AbstractComponent implements WithDLBatchWriter, WithSpark, WithDataSet, WithProjectScope {

    private static final Logger logger = Logger.getLogger(NGSQLComponent.class);
    // Set name
    {
        componentName = "sql";
    }

    NGJobExecutor executor;

    public NGSQLComponent() {  super(); }

    protected int execute(){
        try {
            executor = new NGJobExecutor(this);
            String tempDir = generateTempLocation(new DataSetHelper(ngctx, services.md),
                    ngctx.batchID,
                    ngctx.componentName,
                    null, null);

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

        ComponentConfiguration compConf = AbstractComponent.analyzeAndValidate(cfgAsStr);

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
                        resultDataDesc.get(on),        // SQLDescriptor
                        (String) obDesc.get(DataSetProperties.PhysicalLocation.name()),
                        kl);
                ctx.resultDataDesc.add(desc);

                logger.debug(String.format("DataSet %s will be moved to %s, Partitioning: %s\n",
                        obDesc.get(DataSetProperties.Name.name()),
                        obDesc.get(DataSetProperties.PhysicalLocation.name()), partKeys));

            }
        );
        return super.move();
    }


    public static void main(String[] args){
        NGSQLComponent component = new NGSQLComponent();
        try {
            int rc = component.initWithCMDParameters(args);
            if (rc == 0) {
                rc = component.run();
            } else {
                logger.error(String.format("RC:%d from initWithCMDParameters()", rc));
            }
            System.exit(rc);

        } catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
