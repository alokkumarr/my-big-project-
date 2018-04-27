package sncr.xdf.ngcomponent;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.spark.sql.SparkSession;
import sncr.bda.CliHandler;
import sncr.bda.ConfigLoader;
import sncr.bda.base.MetadataBase;
import sncr.bda.base.MetadataStore;
import sncr.bda.conf.ComponentConfiguration;
import sncr.bda.core.file.HFileOperations;
import sncr.bda.datasets.conf.DataSetProperties;
import sncr.bda.services.AuditLogService;
import sncr.bda.services.DLDataSetService;
import sncr.bda.services.TransformationService;
import sncr.xdf.adapters.readers.DLBatchReader;
import sncr.xdf.context.ComponentServices;
import sncr.xdf.context.InternalContext;
import sncr.xdf.context.NGContext;
import sncr.xdf.exceptions.XDFException;
import sncr.xdf.services.WithDataSet;
import sncr.xdf.services.WithProjectScope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * The AbstractComponent class is base class for all XDF components.
 * All component should be implemented as follow:
 * - Component specific class inherits from AbstractComponent
 * - Component specific class should implement interfaces with given functionality
 * or using base classes:
 * - Read data from a source (???)
 * - Write data (DLBatchWriter)
 * - Move data from temp location to permanent location: WithMovableResult
 * - Read and write result from/to metadata
 * - Support Spark context
 * and so on.
 * All mentioned above are design/development time solution
 * <p>
 * However, component also can be run:
 * - with/without support of metadata
 * - with/without Writing/moving result to permanent location
 * - with writing full result vs creating sample
 * - with internal Spark context vs External Spark context.
 * These are runtime options.
 */
public abstract class AbstractComponent implements WithContext {

    private static final Logger logger = Logger.getLogger(AbstractComponent.class);

    protected String error;
    protected NGContext ngctx;
    protected InternalContext ctx;
    protected String componentName = "unnamed";
    protected final Services services = new Services();
    protected Map<ComponentServices, Boolean> serviceStatus = new HashMap<>();
    protected DLBatchReader reader;

    /**
     * If ngctx is null - we assume Spark context should be created internally and no dataframes should be
     * exported/imported from/to outside
     *
     * @param ngctx
     */
    public AbstractComponent(NGContext ngctx, ComponentServices[] cs) {
        this.ngctx = ngctx;
        for (int i = 0; i < cs.length; i++) {
            serviceStatus.put(cs[i], false);
        }

    }

    public AbstractComponent() {

        serviceStatus.put(ComponentServices.InputDSMetadata, false);
        serviceStatus.put(ComponentServices.OutputDSMetadata, false);
        serviceStatus.put(ComponentServices.Project, false);
        serviceStatus.put(ComponentServices.TransformationMetadata, false);
        serviceStatus.put(ComponentServices.Sample, true);
        serviceStatus.put(ComponentServices.Spark, false);

    }

    public AbstractComponent(ComponentServices[] cs) {
        for (int i = 0; i < cs.length; i++) {
            serviceStatus.put(cs[i], false);
        }
    }


    public String getError() {
        return error;
    }

    public int run() {
        if (!verifyComponentServices()) {
            error = "Component " + componentName + " is not serviceStatus";
            return -1;
        }
        ngctx.setStartTs();
        int ret = execute();
        if (ret == 0) {
            ret = move();
            if (ret == 0) {
                ret = archive();
                if (ret == 0) {
                    ret = Finalize(ret);
                } else {
                }
            } else {
                logger.error("Could not complete move phase!");
            }
        } else {
            logger.error("Could not complete execution phase!");
        }
        return ret;
    }

    private boolean verifyComponentServices() {
        for (ComponentServices cs : serviceStatus.keySet()) {
            if (!serviceStatus.get(cs)) {
                logger.error("Component service: " + cs.name() + " is not initialized");
                return false;
            }
        }
        return true;
    }

    private int initServices() {

        try {

            if (serviceStatus.containsKey(ComponentServices.InputDSMetadata) ||
                serviceStatus.containsKey(ComponentServices.OutputDSMetadata) ||
                serviceStatus.containsKey(ComponentServices.TransformationMetadata)) {
                services.md = new DLDataSetService(ngctx.xdfDataRootSys);
                services.als = new AuditLogService(services.md.getRoot());
            }

            if (serviceStatus.containsKey(ComponentServices.Project) &&
                    this instanceof WithProjectScope)
                services.prj = (WithProjectScope) this;

            if (serviceStatus.containsKey(ComponentServices.TransformationMetadata)) {
                services.transformationMD = new TransformationService(ngctx.xdfDataRootSys);
            }

            if (this instanceof WithDataSet) {
                services.mddl = (WithDataSet) this;
            }

        } catch (Exception e) {
            error = "Services initialization has failed";
            logger.error(error, e);
            return -1;
        }

        return 0;
    }

    private int initProject() {
        try {

            if (services.prj != null) {
                services.prj.getProjectData(ngctx);
                serviceStatus.put(ComponentServices.Project, true);
            } else {
                logger.debug("Project service is not serviceStatus.");
            }

        } catch (Exception e) {
            error = "component initialization (input-discovery/output-preparation) exception: " + ExceptionUtils.getFullStackTrace(e);
            logger.error(error);
            return -1;

        }
        return 0;
    }

    private int initInputDataSets() {
        int rc = 0;
        try {

            //If a component implements
            if (services.mddl != null) {

                if (ngctx.componentConfiguration.getInputs() != null &&
                        ngctx.componentConfiguration.getInputs().size() > 0) {
                    logger.info("Extracting meta data");

                    WithDataSet.DataSetHelper dsaux = new WithDataSet.DataSetHelper(ngctx, services.md);

                    if (serviceStatus.containsKey(ComponentServices.InputDSMetadata)) {
                        ngctx.inputDataSets = services.mddl.discoverInputDataSetsWithMetadata(dsaux);
                        ngctx.inputs = services.mddl.discoverDataParametersWithMetaData(dsaux);

                        final boolean[] failed = {false};
                        ctx.mdInputDSMap = services.md.loadExistingDataSets(ngctx, ngctx.inputDataSets);
                        ctx.mdInputDSMap.forEach((id, ids) -> {
                            try {
                                if (serviceStatus.containsKey(ComponentServices.TransformationMetadata))
                                    services.md.getDSStore().addTransformationConsumer(id, ngctx.transformationID);
                            } catch (Exception e) {
                                logger.error("INput DS analysis error: " + ExceptionUtils.getFullStackTrace(e));
                                failed[0] = true;
                                return;
                            }
                        });
                        if (failed[0]) {
                            throw new Exception(String.format("Could not add transformation consumer: %s", ngctx.transformationID));
                        }

                    } else {
                        ngctx.inputDataSets = services.mddl.discoverInputDataSetsWithInput(dsaux);
                        ngctx.inputs = services.mddl.discoverDataParametersWithInput(dsaux);
                    }

                    logger.debug("Input datasets = " + ngctx.inputDataSets);
                    logger.debug("Inputs = " + ngctx.inputs);
                    serviceStatus.put(ComponentServices.InputDSMetadata, true);
                }
            }

        } catch (Exception e) {
            error = "component initialization (input-discovery/output-preparation) exception: " + ExceptionUtils.getFullStackTrace(e);
            logger.error(error);
            return -1;
        }
        return rc;
    }

    private int initOutputDataSets() {

        final int[] rc2 = {0};
        if (services.mddl != null) {

            if (ngctx.componentConfiguration.getOutputs() != null &&
                    ngctx.componentConfiguration.getOutputs().size() > 0) {

                WithDataSet.DataSetHelper dsaux = new WithDataSet.DataSetHelper(ngctx, services.md);
                ngctx.outputDataSets = services.mddl.ngBuildPathForOutputDataSets(dsaux);
                ngctx.outputs = services.mddl.ngBuildPathForOutputs(dsaux);

                if (serviceStatus.containsKey(ComponentServices.OutputDSMetadata)) ctx.mdOutputDSMap = new HashMap<>();


                final int[] rc = {0};
                ngctx.componentConfiguration.getOutputs().forEach(o ->
                {
                    logger.debug("Add output object to data object repository: " + o.getDataSet());

                    if (!services.mddl.discoverAndvalidateOutputDataSet(ngctx.outputDataSets.get(o.getDataSet()))) {
                        String error = "Could not validate output dataset: " + o.getDataSet();
                        logger.error(error);
                        rc[0] = -1;
                        return;
                    }

                    if (serviceStatus.containsKey(ComponentServices.OutputDSMetadata)) {
                        JsonElement ds = services.md.readOrCreateDataSet(ngctx, ngctx.outputDataSets.get(o.getDataSet()));
                        if (ds == null) {
                            error = "Could not create metadata for output dataset: " + o.getDataSet();
                            logger.error(error);
                            rc[0] = -1;
                            return;
                        }
                        logger.debug("Create/read DS and add it to Output object DS list");
                        JsonObject dsObj = ds.getAsJsonObject();
                        String id = dsObj.getAsJsonPrimitive(DataSetProperties.Id.toString()).getAsString();
                        logger.debug(String.format("Add to output DataSet map document with ID: %s\n %s", id, ds.toString()));

                        ctx.mdOutputDSMap.put(dsObj.getAsJsonPrimitive(DataSetProperties.Id.toString()).getAsString(), ds);

                        String step = "Could not create activity log entry for DataSet: " + o.getDataSet();
                        try {
                            JsonObject ale = services.als.generateDSAuditLogEntry(ngctx, "INIT", ngctx.inputDataSets, ngctx.outputDataSets);
                            String aleId = services.als.createAuditLog(ngctx, ale);
                            step = "Could not update metadata of DataSet: " + o.getDataSet();
                            services.md.getDSStore().updateStatus(id, "INIT", ngctx.startTs, null, aleId, ngctx.batchID);
                        } catch (Exception e) {
                            error = step + ExceptionUtils.getFullStackTrace(e);
                            logger.error(error);
                            rc[0] = -1;
                            return;
                        }
                    }


                });
                serviceStatus.put(ComponentServices.OutputDSMetadata, true);
            }
            return rc2[0];
        } else {
            error = "Incorrect initialization sequence or dataset service is not available";
            logger.error(error);
            return -1;
        }
    }

    private int initTransformation() {

        if (ctx == null ||
                services.transformationMD == null ||
                !serviceStatus.containsKey(ComponentServices.TransformationMetadata)) {
            logger.error("Incorrect initialization sequence or service is not available");
            return -1;
        }
        try {
            ngctx.transformationID =
                     services.transformationMD.readOrCreateTransformation(ngctx, ngctx.componentConfiguration);
            serviceStatus.put(ComponentServices.TransformationMetadata, true);
        } catch (Exception e) {
            error = "Exception at transformation init: " + ExceptionUtils.getFullStackTrace(e);
            return -1;
        }
        return 0;

    }

    private int initSpark(SparkSession ss) {
        if (this instanceof WithSpark && serviceStatus.containsKey(ComponentServices.Spark)) {
            ((WithSpark) this).initSpark(ss, ctx, ngctx);
        }
        serviceStatus.put(ComponentServices.Spark, true);
        return 0;
    }

    protected void initWriter() {
        if (this instanceof WithDLBatchWriter) {
            ctx.resultDataDesc = new ArrayList<>();
        }
    }

    protected void initReader() {
        if (reader == null)
            reader = new DLBatchReader(ctx);
    }

    /**
     * This method:
     * Initializes component from command line parameters,
     * it calls underlying initWithCMDParameters methods converting parameters to Map.
     *
     * @param args - command line parameters.
     * @return - 0 - Success, -1 - fail
     */
    public final int initWithCMDParameters(String[] args) {
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

            return (initComponent(null, configAsStr, appId, batchId, xdfDataRootSys) ? 0 : -1);
        } catch (ParseException e) {
            error = "Could not parse CMD line: " + e.getMessage();
            logger.error(ExceptionUtils.getStackTrace(e));
            return -1;
        } catch (XDFException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            return -1;
        } catch (Exception e) {
            error = "Exception at component initialization " + e.getMessage();
            logger.error(ExceptionUtils.getStackTrace(e));
            return -1;
        }
    }

    public NGContext getNgctx() {
        return ngctx;
    }


    public final boolean initComponent(SparkSession ss, String config, String appId, String batchId, String xdfDataRootSys) throws Exception {

        //Initialization part-1: Read and validate configuration
        logger.trace("Configuration dump: \n" + config);

        ComponentConfiguration cfg = null;
        try {
            cfg = validateConfig(config);
        } catch (Exception e) {
            error = "Configuration is not valid, reason : " + e.getMessage();
            logger.error(e);
            return false;
        }
        if (cfg == null) {
            error = "Internal error: validation procedure returns null";
            logger.error(error);
            return false;
        }

        //Initialization part-2: Create context
        try {
            ctx = new InternalContext();
            ctx.extSparkCtx = ( ss != null);
            ctx.fs = HFileOperations.getFileSystem();
            ctx.fc = HFileOperations.getFileContext();

        } catch (Exception e) {
            logger.error("Could not create internal context: ", e);
            return false;
        }

        if (ngctx == null) {
            ngctx = new NGContext( xdfDataRootSys, cfg, appId, componentName, batchId);
            serviceStatus.keySet().forEach(k -> ngctx.serviceStatus.put(k, serviceStatus.get(k)));
        }

        //Initialization part-3: Initialize services.
        int rc = initServices();
        if (rc != 0) {
            error = "Could not initialize component services.";
            logger.error(error);
            return false;
        }

        //Initialization part-4: Initialize transformation.
        rc = initTransformation();
        if (rc != 0) {
            error = "Could not initialize transformation.";
            logger.error(error);
            return false;
        }

        //Initialization part-5: Initialize project variables.
        logger.debug("Read project metadata");
        rc = initProject();
        if (rc != 0) {
            error = "Could not initialize project variables.";
            logger.error(error);
            return false;
        }


        //Initialization part-6: Initialize spark if necessary
        rc = initSpark(ss);
        if (rc != 0) {
            error = "Could not initialize Spark.";
            logger.error(error);
            return false;
        }


        //Initialization part-7: Initialize input datasets.
        rc = initInputDataSets();
        if (rc != 0) {
            error = "Could not initialize input datasets.";
            logger.error(error);
            return false;
        }

        //Initialization part-8: Initialize output datasets.
        rc = initOutputDataSets();
        if (rc != 0) {
            error = "Could not initialize output datasets.";
            logger.error(error);
            return false;
        }

        //Initialization part-9: Initialize writer.
        initWriter();

        //Initialization part-10: Initialize reader.
        initReader();
        return true;
    }

    //dev
    protected abstract int execute();

    protected int move() {
        int ret = 0;
        if (this instanceof WithDLBatchWriter) {
            ret = ((WithDLBatchWriter) this).moveData(ctx, ngctx);
        }
        return ret;
    }

    protected abstract int archive();

    protected ComponentConfiguration validateConfig(String config) throws Exception {
        return AbstractComponent.analyzeAndValidate(config);
    }

    public static ComponentConfiguration analyzeAndValidate(String cfg) throws Exception {



        ComponentConfiguration config = new Gson().fromJson(cfg, ComponentConfiguration.class);
        return config;
    }

    ///
    private int Finalize(int ret) {
        String status = (ret == 0) ?  "SUCCESS" :
                        ((ret == 1) ? "PARTIAL" :
                                      "FAILED");
        int rc[] = {0};
        rc[0] = 0;
        try {

            ngctx.setFinishTS();

            if (serviceStatus.containsKey(ComponentServices.OutputDSMetadata)) {
                services.md.writeDLFSMeta(ngctx);
                JsonObject ale = services.als.generateDSAuditLogEntry(ngctx, status, ngctx.inputDataSets, ngctx.outputDataSets);
                String ale_id = services.als.createAuditLog(ngctx, ale);
                ctx.mdOutputDSMap.forEach((id, ds) -> {
                    try {
                        //TODO:: Move it after merge to appropriate place
                        //ctx.transformationID = transformationID;
                        ngctx.ale_id = ale_id;
                        ngctx.status = status;

                        //TODO:: Keep it optional, schema might not be available
                        String dsname = id.substring(id.indexOf(MetadataStore.delimiter) + MetadataStore.delimiter.length());
                        Map<String, Object> outDS = ngctx.outputDataSets.get(dsname);
                        JsonElement schema = (JsonElement) outDS.get(DataSetProperties.Schema.name());

                        logger.trace("Extracted schema: " + schema.toString());
                        services.md.updateDS(id, ngctx, ds, schema);

                    } catch (Exception e) {
                        error = "Could not update DS/ write AuditLog entry to DS, id = " + id;
                        logger.error(error);
                        logger.error("Native exception: " + ExceptionUtils.getFullStackTrace(e));
                        rc[0] = -1;
                        return;
                    }
                });
                services.transformationMD.updateStatus(ngctx.transformationID, status, ngctx.startTs, ngctx.finishedTs, ale_id, ngctx.batchID);
            }
        } catch (Exception e) {
            error = "Exception at job finalization: " + ExceptionUtils.getFullStackTrace(e);
            logger.error(error);
            return -1;
        }
        return rc[0];
    }




    public static int startComponent(AbstractComponent self, String dataLakeRoot, String config, String app, String batch) {
        logger.debug(String.format("Component [%s] has been started...", self.componentName));

        try {
            if (self.initComponent(null, config, app, batch, dataLakeRoot)) {
                return self.run();
            } else {
                logger.error("Could not initialize component");
                return -1;
            }
        } catch (Exception e) {
            logger.error("Exception at start component: ", e);
            return -1;
        }
    }

    public DLBatchReader getReader() {
        return reader;
    }

    public InternalContext getICtx() {
        return ctx;
    }


    /**
     * The class is container for internal services
     */
    public class Services {
        public WithProjectScope prj;
        public WithDataSet mddl;

        public DLDataSetService md;
        public AuditLogService als;

        public TransformationService transformationMD;
    }


}
