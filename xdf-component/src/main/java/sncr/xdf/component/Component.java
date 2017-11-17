package sncr.xdf.component;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import sncr.xdf.exceptions.XDFException;
import sncr.xdf.CliHandler;
import sncr.xdf.ConfigLoader;
import sncr.xdf.conf.ComponentConfiguration;
import sncr.xdf.context.Context;
import sncr.xdf.core.file.HFileOperations;
import sncr.xdf.datasets.conf.DataSetProperties;
import sncr.xdf.services.AuditLogService;
import sncr.xdf.services.DLDSMeta;
import sncr.xdf.services.MetadataBase;
import sncr.xdf.services.TransformationMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * Created by asor0002 on 9/8/2017.
 */
public abstract class Component {

    private static final Logger logger = Logger.getLogger(Component.class);

    protected String error;
    protected Context ctx;
    protected DLDSMeta md;
    protected AuditLogService als;
    protected TransformationMeta transformationMD;

    protected String componentName = "unnamed";
    protected ArrayList<WithMovableResult.MoveDataDescriptor> resultDataDesc;
    protected Map<String, Map<String, String>> inputDataSets = null;
    protected Map<String, Map<String, String>> outputDataSets = null;
    protected WithDataSetService.DataSetServiceAux dsaux;
    private Map<String, JsonElement> mdOutputDSMap;
    private Map<String, JsonElement> mdInputDSMap;


    public String getError(){
        return error;
    }

    public int Run(){

        int ret = loadMetadata();
        if ( ret == 0) {
            ret = Execute();
            if (ret == 0) {
                ret = Move();
                if (ret == 0) {
                    ret = Archive();
                    if (ret == 0) {
                    } else {
                    }
                } else {
                    logger.error("Could not complete archive phase!");
                }
            } else {
                logger.error("Could not complete execution phase!");
            }
        }
        else{
            logger.error("Could not generate/retrieve metadata phase!");
        }
        ret = Finalize(ret);
        return ret;
    }

    protected int loadMetadata() {
        try {
            if (this instanceof WithDataSetService) {
                WithDataSetService mddl = (WithDataSetService) this;
                if (ctx.componentConfiguration.getInputs() != null &&
                    ctx.componentConfiguration.getInputs().size() > 0) {
                    inputDataSets = mddl.resolveDataObjects(dsaux);
                    mdInputDSMap = md.loadExistingDataSets(ctx, inputDataSets);
                }

                if (ctx.componentConfiguration.getOutputs() != null &&
                    ctx.componentConfiguration.getOutputs().size() > 0) {
                    outputDataSets = mddl.buildPathForOutputDataObjects(dsaux);
                }

                mdOutputDSMap = new HashMap<>();
                final int[] rc = {0};
                ctx.componentConfiguration.getOutputs().forEach( o ->
                {
                    logger.debug("Add output object to data object repository: " + o.getDataSet() );
                    JsonElement ds = md.readOrCreateDataSet(ctx, outputDataSets.get(o.getDataSet()), o.getMetadata());
                    if (ds == null)
                    {
                        error = "Could not create metadata for output dataset: " +  o.getDataSet();
                        logger.error(error);
                        rc[0] = -1;
                        return;
                    }
                    logger.debug("Create/read DS and add it to Output object DS list" );
                    //TODO:: Now we have both: MapRDb metadata and DL based metadata
                    JsonObject dsObj = ds.getAsJsonObject();
                    logger.debug(String.format("Add to output DataSet map document with ID: %s\n %s",
                            dsObj.getAsJsonPrimitive(DataSetProperties.Id.toString()).getAsString(),
                            ds.toString())
                    );
                    mdOutputDSMap.put(dsObj.getAsJsonPrimitive(DataSetProperties.Id.toString()).getAsString(),ds);

                    md.addDataSetToDLMeta(outputDataSets.get(o.getDataSet()), o);
                });
                if (rc[0] != 0)return rc[0];
            }
        }
        catch (Exception e) {
            error = "component initialization (input-resolving/output-preparation) exception: " + e.getMessage();
            logger.error(error);
            return -1;
        }

        transformationMD.readOrCreateTransformation(ctx);
        return 0;
    }


    /** This method:
     * Initializes component from command line parameters,
     * it calls underlying collectCMDParameters methods converting parameters to Map.
     * @param args - command line parameters.
     * @return  - 0 - Success, -1 - fail
     */

    public final int collectCMDParameters(String[] args){
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

            return Init(configAsStr, appId, batchId, xdfDataRootSys);
        } catch(ParseException e){
            error = "Could not parse CMD line: " +  e.getMessage();
            logger.error(e);
            return -1;
        } catch(XDFException e){
            return -1;
        } catch (Exception e) {
            error = "Exception at component initialization " +  e.getMessage();
            logger.error( e);
            return -1;
        }
    }



    // From API
    // This method should not interact with any storage (hdfs, maprfs, es etc)
    // Ideally should never be overwritten
    // and always executed from custom initialization functions

    public final int Init(String config, String appId, String batchId, String xdfDataRootSys) throws Exception {

        logger.trace( "Configuration dump: \n" + config);
        ComponentConfiguration cfg = null;
        try {
            cfg = validateConfig(config);
        } catch (Exception e) {
            error = "Configuration is not valid, reason : " +  e.getMessage();
            logger.error(e);
            return -1;
        }
        if (cfg == null)
        {
            error = "Internal error: validation procedure returns null";
            logger.error(error);
            return -1;
        }

        try {
            ctx = new Context(componentName, batchId, appId, cfg);
        } catch (Exception e) {
            logger.error("Could not create context: ", e);
            return -1;
        }
        ctx.fs = HFileOperations.getFileSystem();
        ctx.fc = HFileOperations.getFileContext();

        if (this instanceof WithSparkContext) {
            ((WithSparkContext) this).initSpark(ctx);
        }
        if (this instanceof WithMovableResult) {
            resultDataDesc = new ArrayList<>();
        }
        md = new DLDSMeta(xdfDataRootSys);
        transformationMD = new TransformationMeta();
        dsaux = new WithDataSetService.DataSetServiceAux(ctx, md);
        als = new AuditLogService(md.getRoot());

//TODO:: To be used to build DS linage
//        ctx.md.setHeader(componentName, null);

        return 0;
    }

    //dev
    protected abstract int Execute();

    protected int Move(){
        int ret = 0;
        if(this instanceof WithMovableResult){
            ret = ((WithMovableResult)this).doMove(ctx, resultDataDesc);
        }
        return ret;
    };
    protected abstract int Archive();

    protected ComponentConfiguration validateConfig(String config) throws Exception {
        return Component.analyzeAndValidate(config);
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
        int rc[] = {0};
        try {
            md.writeDLMetadata(ctx);
            ctx.setFinishTS();

            JsonObject ale = als.generateDSAuditLogEntry(ctx, status, inputDataSets, outputDataSets);
            String ale_id = als.createAuditLog(ctx, mdOutputDSMap, ale);
            mdOutputDSMap.forEach((id, ds) -> {
                try {
                    als.updateDSWithAuditLog(id, ds, ale_id, ale);
                } catch (Exception e) {
                    error = "Could not write AuditLog entry to document, id = " + id;
                    logger.error(error);
                    logger.error("Native exception: ", e);
                    rc[0]=-1;
                    return;
                }
            });
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
        String strCtx = "Execution context: " + ctx.toString();
        String specificConfParams = mkConfString();
        return strCtx + "\n" + specificConfParams;
    }

    public static int startComponent(Component self, String config, String app, String batch)
    {
        logger.debug(String.format("Component [%s] has been started...", self.componentName));

        String xdfDataRootSys = System.getProperty(MetadataBase.XDF_DATA_ROOT);
        if(xdfDataRootSys == null || xdfDataRootSys.isEmpty()) {
            throw new XDFException(XDFException.ErrorCodes.IncorrectOrAbsentParameter, "XDF Data root");
        }

        try {
            if (self.Init(config, app, batch, xdfDataRootSys) == 0) {
                return self.Run();
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
}
