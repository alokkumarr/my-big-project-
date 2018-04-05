package sncr.xdf.sql.ng;

import org.apache.log4j.Logger;
import sncr.bda.CliHandler;
import sncr.bda.ConfigLoader;
import sncr.bda.base.MetadataBase;
import sncr.bda.conf.ComponentConfiguration;
import sncr.bda.conf.Sql;
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
import sncr.xdf.sql.SQLDescriptor;
import sncr.xdf.sql.SQLMoveDataDescriptor;

import java.util.List;
import java.util.Map;

/**
 * Created by asor0002 on 9/11/2017.
 */

//TODO:: Refactor AsynchNGSQLComponent and NGSQLComponent: eliminate duplicate
public class AsynchNGSQLComponent extends AsynchAbstractComponent implements WithDLBatchWriter, WithSpark, WithDataSet, WithProjectScope {

    private static final Logger logger = Logger.getLogger(AsynchNGSQLComponent.class);
    // Set name
    {
        componentName = "sql";
    }

    NGJobExecutor executor;

    public AsynchNGSQLComponent(NGContext ngctx, ComponentServices[] cs) { super(ngctx, cs); }

    public AsynchNGSQLComponent(NGContext ngctx) {  super(ngctx); }

    public AsynchNGSQLComponent() {  super(); }

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
        return analyzeAndValidate(config);
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

            ComponentServices[] scs =
            {
                ComponentServices.InputDSMetadata,
                ComponentServices.OutputDSMetadata,
                ComponentServices.Project,
                ComponentServices.TransformationMetadata,
                ComponentServices.Spark
            };
            ComponentConfiguration cfg = NGContextServices.analyzeAndValidateSqlConf(configAsStr);
            ngCtxSvc = new NGContextServices(scs, xdfDataRootSys, cfg, appId,
                "sql", batchId);

            ngCtxSvc.initContext();
            ngCtxSvc.registerOutputDataSet();

            logger.debug("Output datasets:");

            ngCtxSvc.getNgctx().registeredOutputDSIds.forEach( id ->
                logger.debug(id)
            );
            AsynchNGSQLComponent component = new AsynchNGSQLComponent(ngCtxSvc.getNgctx());
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
