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
    protected final Services services = new Services();
    protected Map<ComponentServices, Boolean> serviceStatus = new HashMap<>();
    protected DLBatchReader reader;

    /**
     * If ngctx is null - we assume Spark context should be created internally and no dataframes should be
     * exported/imported from/to outside
     * @param ngctx
     */
        public AbstractComponent(NGContext ngctx, ComponentServices[] cs){
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
        //TODO:: fix it when time comes
        serviceStatus.put(ComponentServices.Sample, true);
        serviceStatus.put(ComponentServices.Spark, false);

    }

    public AbstractComponent(ComponentServices[] cs) {
        for (int i = 0; i < cs.length; i++) {
            serviceStatus.put(cs[i], false);
        }
    }


    public String getError(){
        return error;
    }


    public int run() {
        if (!verifyComponentServices()) {
            error = "Component " + componentName + " is not serviceStatus";
            return -1;
        }

        // In case of using initMandatoryServices:
        // ngctx and reader may not be initialized
        // repeat these steps here, even it looks bulky and not elegant.
        if (ngctx == null) {
            ngctx = new NGContext();
            ngctx.fs = HFileOperations.getFileSystem();
            ngctx.fc = HFileOperations.getFileContext();
            if (ngctx.sparkSession == null) ngctx.sparkSession = ctx.sparkSession;
        }
        initReader();


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

    protected boolean verifyComponentServices(){
        for (ComponentServices cs: serviceStatus.keySet()) {
            if (!serviceStatus.get(cs)) {
                logger.error("Component service: " + cs.name() + " is not initialized" );
                return false;
            }
        }
        return true;
    }

    private int initServices(){

        try {

            if (serviceStatus.containsKey(ComponentServices.InputDSMetadata) ||
                serviceStatus.containsKey(ComponentServices.OutputDSMetadata) ||
                serviceStatus.containsKey(ComponentServices.TransformationMetadata))
            {
                services.md = new DLDataSetService(ctx.xdfDataRootSys);
                services.als = new AuditLogService(services.md.getRoot());
            }

            if (serviceStatus.containsKey(ComponentServices.Project) &&
                    this instanceof WithProjectScope)
                services.prj = (WithProjectScope) this;

            if (serviceStatus.containsKey(ComponentServices.TransformationMetadata)) {
                services.transformationMD = new TransformationService(ctx.xdfDataRootSys);
            }

            if (this instanceof WithDataSet) {
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


    public int initProject() {
        try {

            if (services.prj != null) {
                services.prj.getProjectData(ctx);
                serviceStatus.put(ComponentServices.Project, true);
            }
            else{
                logger.debug("Project service is not serviceStatus.");
            }

        } catch (Exception e) {
            error = "component initialization (input-discovery/output-preparation) exception: " + ExceptionUtils.getFullStackTrace(e);
            logger.error(error);
            return -1;

        }
        return 0;
    }

    public int initInputDataSets() {
        int rc = 0;
        try {

            //If a component implements
            if (services.mddl != null) {

                if (ctx.componentConfiguration.getInputs() != null &&
                        ctx.componentConfiguration.getInputs().size() > 0) {
                    logger.info("Extracting meta data");

                    WithDataSet.DataSetHelper dsaux = new WithDataSet.DataSetHelper(ctx, services.md);

                    if (serviceStatus.containsKey(ComponentServices.InputDSMetadata)) {
                        ngctx.inputDataSets = services.mddl.discoverInputDataSetsWithMetadata(dsaux);
                        ngctx.inputs = services.mddl.discoverDataParametersWithMetaData(dsaux);

                        final boolean[] failed = {false};
                        ctx.mdInputDSMap = services.md.loadExistingDataSets(ctx, ngctx.inputDataSets);
                        ctx.mdInputDSMap.forEach((id, ids) -> {
                            try {
                                if (serviceStatus.containsKey(ComponentServices.TransformationMetadata))
                                    services.md.getDSStore().addTransformationConsumer(id, ctx.transformationID);
                            } catch (Exception e) {
                                logger.error("INput DS analysis error: " + ExceptionUtils.getFullStackTrace(e));
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

    public int initOutputDataSets() {

        final int[] rc2 = {0};
        if (services.mddl != null) {

            if (ctx.componentConfiguration.getOutputs() != null &&
                    ctx.componentConfiguration.getOutputs().size() > 0) {

                WithDataSet.DataSetHelper dsaux = new WithDataSet.DataSetHelper(ctx, services.md);
                ngctx.outputDataSets = services.mddl.ngBuildPathForOutputDataSets(dsaux);
                ngctx.outputs = services.mddl.ngBuildPathForOutputs(dsaux);

                if (serviceStatus.containsKey(ComponentServices.OutputDSMetadata)) ctx.mdOutputDSMap = new HashMap<>();


                final int[] rc = {0};
                ctx.componentConfiguration.getOutputs().forEach(o ->
                {
                    logger.debug("Add output object to data object repository: " + o.getDataSet());

                    if (!services.mddl.discoverAndvalidateOutputDataSet(ngctx.outputDataSets.get(o.getDataSet()))) {
                        String error = "Could not validate output dataset: " + o.getDataSet();
                        logger.error(error);
                        rc[0] = -1;
                        return;
                    }

                    if (serviceStatus.containsKey(ComponentServices.OutputDSMetadata)) {
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
        }
        else{
                error = "Incorrect initialization sequence or dataset service is not available";
                logger.error(error);
                return -1;
        }
    }

    public int initTransformation(){

        if (ctx == null ||
            services.transformationMD == null ||
            !serviceStatus.containsKey(ComponentServices.TransformationMetadata)){
            logger.error("Incorrect initialization sequence or service is not available");
            return -1;
        }
        try {
            ctx.transformationID = services.transformationMD.readOrCreateTransformation(ctx);
            serviceStatus.put(ComponentServices.TransformationMetadata, true);
        } catch (Exception e) {
            error = "Exception at transformation init: " + ExceptionUtils.getFullStackTrace(e);
            return -1;
        }
        return 0;

    }

    public int initSpark(){
        if (this instanceof WithSpark && serviceStatus.containsKey(ComponentServices.Spark)) {
            if (ctx.extSparkCtx) {
                ((WithSpark) this).initSpark(ctx);
            }else
                ((WithSpark) this).initSpark(ngctx, ctx);

        }
        serviceStatus.put(ComponentServices.Spark, true);
        return 0;
    }

    protected void initWriter(){
        if (this instanceof WithDLBatchWriter ) {
            ctx.resultDataDesc = new ArrayList<>();
        }
    }

    protected void initReader(){
        if (reader == null)
            reader = new DLBatchReader(ngctx);
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

            return (initAllServices(configAsStr, appId, batchId, xdfDataRootSys)?0:-1);
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

    //TODO:: initMandatoryServices with metadata and without -- create separate calls
    public final boolean initMandatoryServices(String config, String appId, String batchId, String xdfDataRootSys) throws Exception {

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
            ctx.extSparkCtx = ngctx == null;
        } catch (Exception e) {
            logger.error("Could not create internal context: ", e);
            return false;
        }

        //Initialization part-3: Initialize services.
        int rc = initServices();
        if (rc != 0){
            error = "Could not initialize component services.";
            logger.error(error);
            return false;
        }

        //Initialization part-4: Initialize project variables.
        //Initialization part-5: Initialize spark if necessary

        //Initialization part-7: Initialize input datasets.
        //Initialization part-8: Initialize output datasets.
        //Initialization part-9: Initialize writer.


        initWriter();
        return true;
    }



    //TODO:: initMandatoryServices with metadata and without -- create separate calls
    public final boolean initAllServices(String config, String appId, String batchId, String xdfDataRootSys) throws Exception {

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
            ctx.extSparkCtx = ngctx == null;
        } catch (Exception e) {
            logger.error("Could not create internal context: ", e);
            return false;
        }

        //Initialization part-3: Initialize services.
        int rc = initServices();
        if (rc != 0){
            error = "Could not initialize component services.";
            logger.error(error);
            return false;
        }

        //Initialization part-4: Initialize transformation.
        rc = initTransformation();
        if( rc != 0){
            error = "Could not initialize transformation.";
            logger.error(error);
            return false;
        }

        //Initialization part-5: Initialize project variables.
        logger.debug("Read project metadata");
        rc = initProject();
        if (rc != 0){
            error = "Could not initialize project variables.";
            logger.error(error);
            return false;
        }


        //Initialization part-6: Initialize spark if necessary
        rc = initSpark();
        if (rc != 0){
            error = "Could not initialize Spark.";
            logger.error(error);
            return false;
        }

        //Initialization part-6: Initialize external context if not provided.
        if (ngctx == null) {
            ngctx = new NGContext();
            ngctx.fs = HFileOperations.getFileSystem();
            ngctx.fc = HFileOperations.getFileContext();
            if (ngctx.sparkSession == null) ngctx.sparkSession = ctx.sparkSession;
        }
        initReader();


        //Initialization part-8: Initialize input datasets.
        rc = initInputDataSets();
        if (rc != 0){
            error = "Could not initialize input datasets.";
            logger.error(error);
            return false;
        }

        //Initialization part-9: Initialize output datasets.
        rc = initOutputDataSets();
        if (rc != 0){
            error = "Could not initialize output datasets.";
            logger.error(error);
            return false;
        }

        //Initialization part-9: Initialize writer.
        initWriter();


        return true;
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

            if (serviceStatus.containsKey(ComponentServices.OutputDSMetadata)) {
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
                        logger.error("Native exception: " + ExceptionUtils.getFullStackTrace(e));
                        rc[0] = -1;
                        return;
                    }
                });
                services.transformationMD.updateStatus(ctx.transformationID, status, ctx.startTs, ctx.finishedTs, ale_id, ctx.batchID);
            }
        } catch (Exception e) {
            error = "Exception at job finalization: " +  ExceptionUtils.getFullStackTrace(e);
            logger.error(error);
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
            if (self.initAllServices(config, app, batch, dataLakeRoot)) {
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


    public enum ComponentServices {

        InputDSMetadata,
        OutputDSMetadata,
        TransformationMetadata,
        Spark,
        Project,
        Sample

    }
}
