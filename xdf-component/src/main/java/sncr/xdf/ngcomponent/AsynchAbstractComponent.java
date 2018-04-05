package sncr.xdf.ngcomponent;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
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
import sncr.xdf.services.WithDataSet;
import sncr.xdf.services.WithProjectScope;

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
public abstract class AsynchAbstractComponent implements WithContext{

    private static final Logger logger = Logger.getLogger(AsynchAbstractComponent.class);

    protected String error;
    protected NGContext ngctx;
    protected InternalContext ctx;
    protected String componentName = "unnamed";
    protected final Services services = new Services();

    protected DLBatchReader reader;

    /**
     * The constructor is to be used when component is running with different services than NGContext has.
     * ngctx should not be null
     * @param ngctx
     */
    public AsynchAbstractComponent(NGContext ngctx, ComponentServices[] cs){
        if (ngctx == null)
            throw new IllegalArgumentException("NGContext must not be null");

        this.ngctx = ngctx;
        for (int i = 0; i < cs.length; i++) {
            this.ngctx.serviceStatus.put(cs[i], false);
        }
    }

    /**
     * The constructor is to be used in component asynchronous execution
     * NGContext must be initialized, output datasets must be pre-registered.
     * @param ngctx
     */
    public AsynchAbstractComponent(NGContext ngctx) {
        if (ngctx == null)
            throw new IllegalArgumentException("NGContext must not be null");

        this.ngctx = ngctx;
        logger.debug(this.ngctx.toString());
        if (this.ngctx.serviceStatus.isEmpty())
            throw new IllegalArgumentException("NGContext is not initialized correctly");
    }

    public AsynchAbstractComponent() {}

    public String getError(){
        return error;
    }

    /**
     * Main component function, after component initialized this method can be called.
     * @return
     */
    public int run() {

        if (!verifyComponentServices()) {
            error = "Component " + componentName + " is not serviceStatus";
            return -1;
        }
        ngctx.setStartTs();
        if (updateStatus() != 0){
            error = "Could not update datasets / could not create Audit log " +
                "entry";
            logger.error(error);
            return -1;
        }

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


    /**
     * In asynchronous execution this method updates output dataset with next status: IN-PROGRESS
     * @return
     */
    private int updateStatus() {
        try
        {
            if (ngctx.serviceStatus.containsKey(ComponentServices.OutputDSMetadata) ||
                ngctx.serviceStatus.containsKey(ComponentServices.InputDSMetadata)  ||
                ngctx.serviceStatus.containsKey(ComponentServices.TransformationMetadata)) {
                JsonObject ale = services.als.generateDSAuditLogEntry(ngctx, "STARTED", ngctx.inputDataSets, ngctx.outputDataSets);
                String aleId = services.als.createAuditLog(ngctx, ale);

                if (ngctx.serviceStatus.containsKey(ComponentServices.OutputDSMetadata) &&
                    services.md != null &&
                    ctx.mdOutputDSMap != null){
                    ctx.mdOutputDSMap.forEach((id, ds) -> {
                        try {
                            services.md.getDSStore().updateStatus(id, "IN-PROGRESS", ngctx.startTs, null, aleId, ngctx.batchID);
                        } catch (Exception e) {
                            logger.error("Could not update dataset: " + ExceptionUtils.getFullStackTrace(e));
                            return;
                        }
                    });
                }
            }
        } catch (Exception e) {
            logger.error("Could not create Audit log entry" +
                ExceptionUtils.getFullStackTrace(e));
            return -1;
        }
        return 0;
    }

    /**
     * Virifies readiness of component to be executed
     * @return
     */
    protected boolean verifyComponentServices(){
        for (ComponentServices cs: ngctx.serviceStatus.keySet()) {
            if (!ngctx.serviceStatus.get(cs)) {
                logger.error("Component service: " + cs.name() + " is not initialized" );
                return false;
            }
        }
        return true;
    }

    private int initServices(){

        try {

            if (ngctx.serviceStatus.containsKey(ComponentServices.InputDSMetadata) ||
                ngctx.serviceStatus.containsKey(ComponentServices.OutputDSMetadata) ||
                ngctx.serviceStatus.containsKey(ComponentServices.TransformationMetadata))
            {
                services.md = new DLDataSetService(ngctx.xdfDataRootSys);
                services.als = new AuditLogService(services.md.getRoot());
            }

            if (ngctx.serviceStatus.containsKey(ComponentServices.Project) &&
                    this instanceof WithProjectScope)
                services.prj = (WithProjectScope) this;

            if (ngctx.serviceStatus.containsKey(ComponentServices.TransformationMetadata)) {
                services.transformationMD = new TransformationService(ngctx.xdfDataRootSys);
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

    private int initInputDataSets() {
        int rc = 0;
        try {

            //If a component implements
            if (services.mddl != null) {

                if (ngctx.componentConfiguration.getInputs() != null &&
                        ngctx.componentConfiguration.getInputs().size() > 0) {
                    logger.info("Extracting meta data");

                    WithDataSet.DataSetHelper dsaux = new WithDataSet.DataSetHelper(ngctx, services.md);

                    if (ngctx.serviceStatus.containsKey(ComponentServices.InputDSMetadata)) {
                        ngctx.inputDataSets = services.mddl.discoverInputDataSetsWithMetadata(dsaux);
                        ngctx.inputs = services.mddl.discoverDataParametersWithMetaData(dsaux);

                        final boolean[] failed = {false};
                        ctx.mdInputDSMap = services.md.loadExistingDataSets(ngctx, ngctx.inputDataSets);
                        ctx.mdInputDSMap.forEach((id, ids) -> {
                            try {
                                if (ngctx.serviceStatus.containsKey(ComponentServices.TransformationMetadata))
                                    services.md.getDSStore().addTransformationConsumer(id, ngctx.transformationID);
                            } catch (Exception e) {
                                logger.error("INput DS analysis error: " + ExceptionUtils.getFullStackTrace(e));
                                failed[0] = true;
                                return;
                            }
                        });
                        if (failed[0]){
                            throw new  Exception(String.format("Could not add transformation consumer: %s", ngctx.transformationID ));
                        }

                    }
                    else{
                        ngctx.inputDataSets = services.mddl.discoverInputDataSetsWithInput(dsaux);
                        ngctx.inputs = services.mddl.discoverDataParametersWithInput(dsaux);
                    }

                    logger.debug("Input datasets = " + ngctx.inputDataSets);
                    logger.debug("Inputs = " + ngctx.inputs);
                    ngctx.serviceStatus.put(ComponentServices.InputDSMetadata, true);
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

            if (ngctx.componentConfiguration.getOutputs() != null && ngctx.componentConfiguration.getOutputs().size() > 0) {
                if ( ngctx.outputDataSets.isEmpty()){
                    logger.error("Incorrect component initialization, NGContext contains empty descriptors");
                    return -1;
                }

                if (ngctx.serviceStatus.containsKey(ComponentServices.OutputDSMetadata)) ctx.mdOutputDSMap = new HashMap<>();

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

                    if (ngctx.serviceStatus.containsKey(ComponentServices.OutputDSMetadata)) {
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

                        ctx.mdOutputDSMap.put(id, ds);
                    }
                });
                ngctx.serviceStatus.put(ComponentServices.OutputDSMetadata, true);
            }
            return rc2[0];
        }
        else{
                error = "Incorrect initialization sequence or dataset service is not available";
                logger.error(error);
                return -1;
        }
    }

    private int initSpark(JavaSparkContext jsc){
        if (this instanceof WithSpark && ngctx.serviceStatus.containsKey(ComponentServices.Spark)) {
            if (ctx.extSparkCtx) {
                 SparkSession ss = new SparkSession(jsc.sc());
                ((WithSpark) this).initSpark(ss, ctx, ngctx);
            }
            else{
                ((WithSpark) this).initSpark(null, ctx, ngctx);
            }
        }
        ngctx.serviceStatus.put(ComponentServices.Spark, true);
        return 0;
    }

    protected void initWriter(){
        if (this instanceof WithDLBatchWriter ) {
            ctx.resultDataDesc = new ArrayList<>();
        }
    }

    protected void initReader(){
        if (reader == null)
            reader = new DLBatchReader(ctx);
    }


    /**
     * Performs component initialization in case if component was created without NGContext (ngctx == null)
     * creates NGContext and call subsequent function to complete initialization
     * @param jsc     - externally initiated JavaSparkContext
     * @param cfg     - parset and mapped component configuration
     * @param appId   - project
     * @param batchId - Batch ID
     * @param xdfDataRootSys - Datalake root.
     * @return
     * @throws Exception
     */
    public final boolean initComponent(JavaSparkContext jsc, ComponentConfiguration cfg, String appId, String batchId, String xdfDataRootSys) throws Exception {
        if (ngctx == null) {
            ngctx = new NGContext(xdfDataRootSys, cfg, appId, componentName, batchId);
            ngctx.serviceStatus.put(ComponentServices.InputDSMetadata, false);
            ngctx.serviceStatus.put(ComponentServices.OutputDSMetadata, false);
            ngctx.serviceStatus.put(ComponentServices.Project, false);
            ngctx.serviceStatus.put(ComponentServices.TransformationMetadata, false);
            ngctx.serviceStatus.put(ComponentServices.Sample, true);
            ngctx.serviceStatus.put(ComponentServices.Spark, false);
        }
        logger.trace("Configuration dump: \n" + cfg.toString());
        return initComponent(jsc);
    }


    /**
     * Initializes component: if jsc is null - creates SparkContext based on Project/Configuration settings
     * If jsc is not null - uses the context.
     * @param jsc
     * @return
     * @throws Exception
     */
    public final boolean initComponent(JavaSparkContext jsc) throws Exception {

        if (ngctx == null){
            throw new Exception("Incorrect call, the method can be called only NGContext is initialized");
        }

        //Initialization part-1: Create context

        try {
            ctx = new InternalContext();
            ctx.extSparkCtx = (jsc != null);
            ctx.fs = HFileOperations.getFileSystem();
            ctx.fc = HFileOperations.getFileContext();
        } catch (Exception e) {
            logger.error("Could not create internal context: ", e);
            return false;
        }

        //Initialization part-3: Initialize services.
        int rc = initServices();
        if (rc != 0) {
            error = "Could not initialize component services.";
            logger.error(error);
            return false;
        }


        //Initialization part-4: Initialize trasnsformation metadata.
        if (services.transformationMD == null &&
            ngctx.serviceStatus.containsKey(ComponentServices.TransformationMetadata)){

            try {
                ngctx.transformationID =
                        services.transformationMD.readOrCreateTransformation(ngctx, ngctx.componentConfiguration);
                ngctx.serviceStatus.put(ComponentServices.TransformationMetadata, true);
            } catch (Exception e) {
                String error = "Exception at transformation init: " + ExceptionUtils.getFullStackTrace(e);
                logger.error(error);
                return false;
            }
        }

        //Initialization part-5: Initialize spark if necessary
        rc = initSpark(jsc);
        if (rc != 0) {
            error = "Could not initialize Spark.";
            logger.error(error);
            return false;
        }

        //Initialization part-6: Initialize input datasets.
        rc = initInputDataSets();
        if (rc != 0) {
            error = "Could not initialize input datasets.";
            logger.error(error);
            return false;
        }

        //Initialization part-7: Initialize output datasets.
        rc = initOutputDataSets();
        if (rc != 0) {
            error = "Could not initialize output datasets.";
            logger.error(error);
            return false;
        }

        //Initialization part-8: Initialize writer.
        initWriter();
        initReader();

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
    }

    protected abstract int archive();

    public static ComponentConfiguration analyzeAndValidate(String cfg) throws Exception
    {
        ComponentConfiguration config = new Gson().fromJson(cfg, ComponentConfiguration.class);
        return config;
    }

    /**
     * Final stage of processing
     * @param ret
     * @return
     */
    private int Finalize(int ret)  {
        String status =
                ( ret == 0)? "SUCCESS":
                ((ret == 1)? "PARTIAL":
                             "FAILED");
        int rc[] = {0}; rc[0] = 0;
        try {

            ngctx.setFinishTS();

            if (ngctx.serviceStatus.containsKey(ComponentServices.OutputDSMetadata)) {
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

                        if (schema != null) {
                            logger.trace("Extracted schema: " + schema.toString());
                            services.md.updateDS(id, ngctx, ds, schema);
                        }
                        else{
                            logger.warn("The component was not able to get schema from NG context, assume something went wrong");
                        }
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
            error = "Exception at job finalization: " +  ExceptionUtils.getFullStackTrace(e);
            logger.error(error);
            return -1;
        }
        return rc[0];
    }

    public InternalContext getICtx() {
        return ctx;
    }

    public DLBatchReader getReader() {
        return reader;
    }

    @Override
    public NGContext getNgctx() {
        return ngctx;
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
