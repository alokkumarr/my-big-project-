package sncr.xdf.ngcomponent;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
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
import sncr.xdf.component.*;
import sncr.xdf.context.InternalContext;
import sncr.xdf.context.NGContext;
import sncr.xdf.exceptions.XDFException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 *  The AbstractComponent class is base class for all XDF components.
 *  All component should be implemented as follow:
 *   - Component specific class inherits from AbstractComponent
 *   - Component specific class should implement interfaces with given functionality
 *   or using base classes:
 *      - Read data from a source (???)
 *      - Write data (DLBatchWriter)
 *      - Move data from temp location to permanent location: WithMovableResult
 *      - Read and write result from/to metadata
 *      - Support Spark context
 *      and so on.
 *      All mentioned above are design/development time solution
 *
 *      However, component also can be run:
 *      - with/without support of metadata
 *      - with/without Writing/moving result to permanent location
 *      - with writing full result vs creating sample
 *      - with internal Spark context vs External Spark context.
 *     These are runtime options.
 *
 */
public abstract class AbstractComponent {

    private static final Logger logger = Logger.getLogger(AbstractComponent.class);

    protected String error;
    protected NGContext ngctx;
    protected InternalContext ctx;
    protected String componentName = "unnamed";

    protected final boolean useMDForInput;
    protected final boolean useMDForOutput;
    protected final boolean useMDPrj;
    protected final boolean useMDForTransformations;
    protected final boolean useSample;
    protected final Services services = new Services();
    private boolean initialized = false;
    protected DLBatchReader reader;

    /**
     * If ngctx is null - we assume Spark context should be created internally and no dataframes should be
     * exported/imported from/to outside
     * @param ngctx
     */
    public AbstractComponent(NGContext ngctx, boolean useMDForInput, boolean useMDForOutput, boolean useMDPrj, boolean useMDForTransformations, boolean useSample){
        this.ngctx = ngctx;
        this.useMDForInput = useMDForInput;
        this.useMDForOutput = useMDForOutput;
        this.useMDPrj = useMDPrj;
        this.useMDForTransformations = useMDForTransformations;
        this.useSample = useSample;
    }

    public AbstractComponent(){

        this.useMDForInput = true;
        this.useMDForOutput = true;
        this.useMDPrj = true;
        this.useMDForTransformations = true;
        this.useSample = true;

    }

    public AbstractComponent(NGContext ngctx, boolean useMD, boolean useSample){
        this(ngctx, useMD, useMD, useMD, useMD, useSample);
    }



    public String getError(){
        return error;
    }

    //TODO:: Create NGContext at the start
    //TODO:: Store all datasets once ( in NGContext )
    //TODO:: For internal Spark context keep spark* vars null in NGContext
    //TODO:: Add flags into NGContexts nbased on call, or fallow flags if NGContexts accepted from outside


    public int run() {
        if (!initialized) {
            error = "Component " + componentName + " is not initialized";
            return -1;
        }
        int ret = execute();
        if (ret == 0) {
            ret = move();
            if (ret == 0) {
                ret = archive();
                if (ret == 0) {
                } else {
                }
            } else {
                logger.error("Could not complete archive phase!");
            }
        } else {
            logger.error("Could not complete execution phase!");
        }
        ret = Finalize(ret);
        return ret;
    }

    private int initServices(){

        try {

            if (useMDForInput || useMDForOutput || useMDForTransformations) {
                services.md = new DLDataSetService(ctx.xdfDataRootSys);
                services.als = new AuditLogService(services.md.getRoot());
            }

            if (useMDPrj && this instanceof WithProjectScope)
                services.prj = (WithProjectScope) this;

            if (useMDForTransformations) {
                services.transformationMD = new TransformationService(ctx.xdfDataRootSys);
            }

            if (this instanceof WithDataSetService) {
                services.mddl = (WithDataSet) this;
            }

        }
        catch(Exception e){
            error = "Services initialization has failed";
            logger.error(error, e);
            return -1;
        }

        return 0;
    }


    protected int initPrj() {
        try {

            if (services.prj != null) {
                services.prj.getProjectData(ctx);
            }

        } catch (Exception e) {
            error = "component initialization (input-discovery/output-preparation) exception: " + ExceptionUtils.getFullStackTrace(e);
            logger.error(error);
            return -1;

        }
        return 0;
    }


    protected int initInputDataSets() {
        int rc = 0;
        try {

            //If a component implements
            if (services.mddl != null) {

                if (ctx.componentConfiguration.getInputs() != null &&
                        ctx.componentConfiguration.getInputs().size() > 0) {
                    logger.info("Extracting meta data");

                    WithDataSet.DataSetHelper dsaux = new WithDataSet.DataSetHelper(ctx, services.md);

                    if (useMDForInput) {
                        ngctx.inputDataSets = services.mddl.discoverInputDataSetsWithMetadata(dsaux);
                        ngctx.inputs = services.mddl.discoverDataParametersWithMetaData(dsaux);

                        final boolean[] failed = {false};
                        ctx.mdInputDSMap = services.md.loadExistingDataSets(ctx, ngctx.inputDataSets);
                        ctx.mdInputDSMap.forEach((id, ids) -> {
                            try {
                                if (useMDForTransformations)
                                    services.md.getDSStore().addTransformationConsumer(id, ctx.transformationID);
                            } catch (Exception e) {
                                failed[0] = true;
                                return;
                            }
                        });
                        if (failed[0]){
                            throw new  Exception(String.format("Could not add transformation consumer: %s", ctx.transformationID ));
                        }

                    }
                    else{
                        ngctx.inputDataSets = services.mddl.discoverInputDataSetsWithInput(dsaux);
                        ngctx.inputs = services.mddl.discoverDataParametersWithInput(dsaux);
                    }

                    logger.debug("Input datasets = " + ngctx.inputDataSets);
                    logger.debug("Inputs = " + ngctx.inputs);
                }
            }

        } catch (Exception e) {
            error = "component initialization (input-discovery/output-preparation) exception: " + ExceptionUtils.getFullStackTrace(e);
            logger.error(error);
            return -1;
        }
        return rc;
    }

    protected int initOutputDataSets() {

        final int[] rc2 = {0};
        if (services.mddl != null) {

            if (ctx.componentConfiguration.getOutputs() != null &&
                    ctx.componentConfiguration.getOutputs().size() > 0) {

                WithDataSet.DataSetHelper dsaux = new WithDataSet.DataSetHelper(ctx, services.md);
                ngctx.outputDataSets = services.mddl.ngBuildPathForOutputDataSets(dsaux);
                ngctx.outputs = services.mddl.ngBuildPathForOutputs(dsaux);

                if (useMDForOutput) ctx.mdOutputDSMap = new HashMap<>();


                final int[] rc = {0};
                ctx.componentConfiguration.getOutputs().forEach(o ->
                {
                    logger.debug("Add output object to data object repository: " + o.getDataSet());

                    if (services.mddl.discoverAndvalidateOutputDataSet(ngctx.outputDataSets.get(o.getDataSet()))) {
                        String error = "Could not validate output dataset: " + o.getDataSet();
                        logger.error(error);
                        rc[0] = -1;
                        return;
                    }

                    if (useMDForOutput) {
                        JsonElement ds = services.md.readOrCreateDataSet(ctx, ngctx.outputDataSets.get(o.getDataSet()));
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
                            JsonObject ale = services.als.generateDSAuditLogEntry(ctx, "INIT", ngctx.inputDataSets, ngctx.outputDataSets);
                            String aleId = services.als.createAuditLog(ctx, ale);
                            step = "Could not update metadata of DataSet: " + o.getDataSet();
                            services.md.getDSStore().updateStatus(id, "INIT", ctx.startTs, null, aleId, ctx.batchID);
                        } catch (Exception e) {
                            error = step;
                            logger.error(error, e);
                            rc[0] = -1;
                            return;
                        }
                    }


                });
            }
            return rc2[0];
        }
        return 0;
    }

    protected int initSpark()
    {

        if (this instanceof WithSpark) {
            if (ngctx == null)
                ((WithSpark) this).initSpark(ctx);
            else
                ((WithSpark) this).initSpark(ngctx, ctx);
        }
        return 0;
    }

    protected void initWriter(){
        if (this instanceof WithDLBatchWriter) {
            ctx.resultDataDesc = new ArrayList<>();
        }
    }

    /** This method:
     * Initializes component from command line parameters,
     * it calls underlying initWithCMDParameters methods converting parameters to Map.
     * @param args - command line parameters.
     * @return  - 0 - Success, -1 - fail
     */
    public final int initWithCMDParameters(String[] args){
        CliHandler cli = new CliHandler();
        try {
            HFileOperations.init();

            Map<String,Object> parameters = cli.parse(args);
            String cfgLocation = (String) parameters.get(CliHandler.OPTIONS.CONFIG.name());
            String configAsStr = ConfigLoader.loadConfiguration(cfgLocation);
            if ( configAsStr == null || configAsStr.isEmpty()){
                throw new XDFException(XDFException.ErrorCodes.IncorrectOrAbsentParameter, "configuration file name");
            }

            String appId = (String) parameters.get(CliHandler.OPTIONS.APP_ID.name());
            if ( appId == null || appId.isEmpty()){
                throw new XDFException(XDFException.ErrorCodes.IncorrectOrAbsentParameter, "Project/application name");
            }

            String batchId = (String) parameters.get(CliHandler.OPTIONS.BATCH_ID.name());
            if ( batchId == null || batchId.isEmpty()){
                throw new XDFException(XDFException.ErrorCodes.IncorrectOrAbsentParameter, "batch id/session id");
            }

            String xdfDataRootSys = System.getProperty(MetadataBase.XDF_DATA_ROOT);
            if(xdfDataRootSys == null || xdfDataRootSys.isEmpty()) {
                throw new XDFException(XDFException.ErrorCodes.IncorrectOrAbsentParameter, "XDF Data root");
            }

            return (Init(configAsStr, appId, batchId, xdfDataRootSys)?0:-1);
        } catch(ParseException e){
            error = "Could not parse CMD line: " +  e.getMessage();
            logger.error(ExceptionUtils.getStackTrace(e));
            return -1;
        } catch(XDFException e){
            logger.error(ExceptionUtils.getStackTrace(e));
            return -1;
        } catch (Exception e) {
            error = "Exception at component initialization " +  e.getMessage();
            logger.error(ExceptionUtils.getStackTrace(e));
            return -1;
        }
    }

    public NGContext getNgctx() {
        return ngctx;
    }

    // From API
    // This method should not interact with any storage (hdfs, maprfs, es etc)
    // Ideally should never be overwritten
    // and always executed from custom initialization functions

    //TODO:: Init with metadata and without -- create separate calls
    public final boolean Init(String config, String appId, String batchId, String xdfDataRootSys) throws Exception {

        //Initialization part-1: Read and validate configuration
        logger.trace( "Configuration dump: \n" + config);

        ComponentConfiguration cfg = null;
        try {
            cfg = validateConfig(config);
        } catch (Exception e) {
            error = "Configuration is not valid, reason : " +  e.getMessage();
            logger.error(e);
            return false;
        }
        if (cfg == null)
        {
            error = "Internal error: validation procedure returns null";
            logger.error(error);
            return false;
        }

        //Initialization part-2: Create context
        try {
            ctx = new InternalContext(componentName, batchId, appId, cfg, xdfDataRootSys);
        } catch (Exception e) {
            logger.error("Could not create internal context: ", e);
            return false;
        }


        //Initialization part-3: Init spark if necessary
        int rc = initSpark();
        if (rc != 0){
            error = "Could not initialize Spark.";
            logger.error(error);
            return false;
        }

        //Initialization part-4: Init external context if not provided.
        if(ngctx == null) {
            ngctx = new NGContext();
            ngctx.fs = HFileOperations.getFileSystem();
            ngctx.fc = HFileOperations.getFileContext();
            if (ngctx.sparkSession == null) ngctx.sparkSession = ctx.sparkSession;

        }
        reader = new DLBatchReader(ngctx);

        //Initialization part-5: Initialize services.
        rc = initServices();
        if (rc != 0){
            error = "Could not initialize component services.";
            logger.error(error);
            return false;
        }

        //Initialization part-6: Initialize project variables.
        rc = initPrj();
        if (rc != 0){
            error = "Could not initialize project variables.";
            logger.error(error);
            return false;
        }

        //Initialization part-7: Initialize input datasets.
        rc = initInputDataSets();
        if (rc != 0){
            error = "Could not initialize input datasets.";
            logger.error(error);
            return false;
        }

        //Initialization part-8: Initialize output datasets.
        rc = initOutputDataSets();
        if (rc != 0){
            error = "Could not initialize output datasets.";
            logger.error(error);
            return false;
        }

        //Initialization part-9: Initialize writer.
        initWriter();

        initialized = true;
        return initialized;
    }

    //dev
    protected abstract int execute();

    protected int move(){
        int ret = 0;
        if(this instanceof WithDLBatchWriter){
            ret = ((WithDLBatchWriter)this).moveData(ctx, ngctx);
        }
        return ret;
    };
    protected abstract int archive();

    protected ComponentConfiguration validateConfig(String config) throws Exception {
        return AbstractComponent.analyzeAndValidate(config);
    }

    public static ComponentConfiguration analyzeAndValidate(String cfg) throws Exception
    {
        ComponentConfiguration config = new Gson().fromJson(cfg, ComponentConfiguration.class);
        return config;
    }

    ///
    private int Finalize(int ret)  {
        String status =
                ( ret == 0)? "SUCCESS":
                ((ret == 1)? "PARTIAL":
                             "FAILED");
        int rc[] = {0}; rc[0] = 0;
        try {

            ctx.setFinishTS();

            if (useMDForOutput) {
                services.md.writeDLFSMeta(ctx);
                JsonObject ale = services.als.generateDSAuditLogEntry(ctx, status, ngctx.inputDataSets, ngctx.outputDataSets);
                String ale_id = services.als.createAuditLog(ctx, ale);
                ctx.mdOutputDSMap.forEach((id, ds) -> {
                    try {
                        //TODO:: Move it after merge to appropriate place
                        //ctx.transformationID = transformationID;
                        ctx.ale_id = ale_id;
                        ctx.status = status;

                        //TODO:: Keep it optional, schema might not be available
                        String dsname = id.substring(id.indexOf(MetadataStore.delimiter) + MetadataStore.delimiter.length());
                        Map<String, Object> outDS = ngctx.outputDataSets.get(dsname);
                        JsonElement schema = (JsonElement) outDS.get(DataSetProperties.Schema.name());

                        logger.trace("Extracted schema: " + schema.toString());
                        services.md.updateDS(id, ctx, ds, schema);

                    } catch (Exception e) {
                        error = "Could not update DS/ write AuditLog entry to DS, id = " + id;
                        logger.error(error);
                        logger.error("Native exception: ", e);
                        rc[0] = -1;
                        return;
                    }
                });
                services.transformationMD.updateStatus(ctx.transformationID, status, ctx.startTs, ctx.finishedTs, ale_id, ctx.batchID);
            }
        } catch (Exception e) {
            error = "Exception at job finalization: " +  e.getMessage();
            logger.error(e);
            return -1;
        }
        return rc[0];
    }

    protected abstract String mkConfString();

    @Override
    public String toString(){
        String strCtx = "Execution context: " + ngctx.toString();
        String specificConfParams = mkConfString();
        return strCtx + "\n" + specificConfParams;
    }

    public static int startComponent(AbstractComponent self, String dataLakeRoot, String config, String app, String batch)
    {
        logger.debug(String.format("Component [%s] has been started...", self.componentName));

        try {
            if (self.Init(config, app, batch, dataLakeRoot)) {
                return self.run();
            }
            else{
                logger.error("Could not initialize component");
                return -1;
            }
        } catch (Exception e) {
            logger.error("Exception at start component: ", e);
            return -1;
        }
    }

    public InternalContext getICtx() {
        return ctx;
    }

    public DLBatchReader getReader() {
        return reader;
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
