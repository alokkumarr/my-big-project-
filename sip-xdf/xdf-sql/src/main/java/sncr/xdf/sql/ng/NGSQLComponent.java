package sncr.xdf.sql.ng;

import org.apache.log4j.LogManager;
import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sncr.bda.CliHandler;
import sncr.bda.ConfigLoader;
import sncr.bda.base.MetadataBase;
import sncr.bda.conf.ComponentConfiguration;
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

import java.util.List;
import java.util.Map;

/**
 * Created by asor0002 on 9/11/2017.
 */

public class NGSQLComponent extends AbstractComponent implements WithDLBatchWriter, WithSpark, WithDataSet, WithProjectScope {

    private static final Logger logger = LoggerFactory.getLogger(NGSQLComponent.class);
    // Set name
    {
        componentName = "sql";
    }

    NGJobExecutor executor;

    public NGSQLComponent(NGContext ngctx, ComponentServices[] cs) { super(ngctx, cs); }

    public NGSQLComponent(NGContext ngctx) {  super(ngctx); }

    public NGSQLComponent() {  super(); }

    protected int execute(){
        /* Workaround: If executed through Apache Livy the logging
         * level will be WARN by default and at the moment no way to
         * change that level through configuration files has been
         * found, so set it programmatically to at least INFO to help
         * troubleshooting */
        if (!LogManager.getRootLogger().isInfoEnabled()) {
            LogManager.getRootLogger().setLevel(Level.INFO);
        }
        try {
            executor = new NGJobExecutor(this);
            String tempDir = generateTempLocation(new DataSetHelper(ngctx, services.md),
                null, null);
            logger.info("tempDir : " +tempDir);
            int rc = executor.start(tempDir);
            if(rc != 0) {
            	return -1;
            }
        } catch (Exception e) {
            error = "SQL Executor runtime exception: " + e.getMessage();
            logger.error(e.toString());
            return -1;
        }
        return 0;
    }

    protected int archive(){
        return 0;
    }

    protected ComponentConfiguration validateConfig(String config) throws Exception {
        logger.trace("Validate Config : " + config);
        return analyzeAndValidate(config);
    }



    @Override
    protected int move(){

        //TODO: Remove the below line
        logger.warn("######### Moving data");

        if (executor.getResultDataSets() == null ||
            executor.getResultDataSets().size() == 0 )
        {
            logger.warn("Component does not produce any Data Sets");
            return 0;
        }

        Map<String, SQLDescriptor> resultDataSets = executor.getResultDataSets();

        logger.warn("Move descriptors " + resultDataSets);
        logger.warn("Output datasets" + ngctx.outputDataSets);


        ngctx.outputDataSets.forEach(
            (on, obDesc) ->
            {
                logger.debug("SQL Descriptor for " + on + " = " + on);

                logger.debug("SQL Descriptor for obDesc.keySet  " + obDesc.keySet());

                SQLDescriptor descriptor = resultDataSets.get(on);
                logger.debug("SQL Descriptor for " + on + " = " + descriptor);

                if (descriptor != null) {
                    logger.info("Generating MoveDataDescriptor for " + on + " description " + obDesc);
                    List<String> kl = (List<String>) obDesc.get(DataSetProperties.PartitionKeys.name());
                    String partKeys = on + ": ";

                    for (String s : kl) partKeys += s + " ";

                    MoveDataDescriptor desc = new NGSQLMoveDataDescriptor(
                        resultDataSets.get(on),         // SQLDescriptor
                        (String) obDesc.get(DataSetProperties.PhysicalLocation.name()),kl);

                    ctx.resultDataDesc.add(desc);

                    logger.debug(String.format("DataSet %s will be moved to %s, Partitioning: %s",
                        obDesc.get(DataSetProperties.Name.name()),
                        obDesc.get(DataSetProperties.PhysicalLocation.name()), partKeys));
                }

            }
        );

        logger.warn("Result desc = " + ctx.resultDataDesc);
        return super.move();
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
            NGSQLComponent component = new NGSQLComponent(ngCtxSvc.getNgctx());
            if (!component.initComponent(null))
                System.exit(-1);
            int rc = component.run();

            long end_time = System.currentTimeMillis();
            long difference = end_time-start_time;
            logger.info("SQL total time " + difference );


            System.exit(rc);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
