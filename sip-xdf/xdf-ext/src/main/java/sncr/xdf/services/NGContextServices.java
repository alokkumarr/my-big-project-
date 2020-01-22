package sncr.xdf.services;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import sncr.bda.conf.*;
import sncr.bda.datasets.conf.DataSetProperties;
import sncr.bda.services.AuditLogService;
import sncr.bda.services.DLDataSetService;
import sncr.bda.services.TransformationService;
import sncr.xdf.context.ComponentServices;
import sncr.xdf.context.NGContext;
import sncr.xdf.context.RequiredNamedParameters;
import sncr.xdf.context.XDFReturnCode;
import sncr.xdf.exceptions.XDFException;
import sncr.bda.conf.Input;


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
public class NGContextServices implements WithDataSet, WithProjectScope{

    private static final Logger logger = Logger.getLogger(NGContextServices.class);

    protected NGContext ngctx;
    protected final Services services = new Services();

    public NGContextServices( ComponentServices[] cs, String xdfRoot,  ComponentConfiguration componentConfiguration, String applicationID, String componentName, String batchID){
        ngctx = new NGContext(xdfRoot, componentConfiguration, applicationID, componentName, batchID);
        for (int i = 0; i < cs.length; i++) {
        	logger.debug("Inside NG context services putting in service status"+ cs[i]);
            this.ngctx.serviceStatus.put(cs[i], false);
            logger.debug("##### service status length"+  this.ngctx.serviceStatus.size());
        }
    }

    public NGContextServices(String xdfRoot,  ComponentConfiguration componentConfiguration, String applicationID, String componentName, String batchID) {
        ngctx = new NGContext(xdfRoot, componentConfiguration, applicationID, componentName, batchID);
        this.ngctx.serviceStatus.put(ComponentServices.InputDSMetadata, false);
        this.ngctx.serviceStatus.put(ComponentServices.OutputDSMetadata, false);
        this.ngctx.serviceStatus.put(ComponentServices.Project, false);
        this.ngctx.serviceStatus.put(ComponentServices.TransformationMetadata, false);
        this.ngctx.serviceStatus.put(ComponentServices.Sample, true);
        this.ngctx.serviceStatus.put(ComponentServices.Spark, false);

    }

    public NGContext getNgctx(){
        return ngctx;
    }


    public int initContext(){
    	
    	logger.debug("#### Inside init context ::"+ ngctx.serviceStatus );

        try {


            if (ngctx.serviceStatus.containsKey(ComponentServices.InputDSMetadata) ||
                ngctx.serviceStatus.containsKey(ComponentServices.OutputDSMetadata) ||
                ngctx.serviceStatus.containsKey(ComponentServices.TransformationMetadata))
            {
                services.md = new DLDataSetService(ngctx.xdfDataRootSys);
                services.als = new AuditLogService(services.md.getRoot());
            }

            if (ngctx.serviceStatus.containsKey(ComponentServices.Project)){
            	logger.debug("#### Inside if project::" );

                services.prj =  this;
                if (initProject() != 0){
                    logger.error("Could not init project data");
                    return  -1;
                }
            }

            if (ngctx.serviceStatus.containsKey(ComponentServices.TransformationMetadata)) {
                services.transformationMD = new TransformationService(ngctx.xdfDataRootSys);
                if (initTransformation() != 0){
                    logger.error("Could not init transformation data");
                    return  -1;
                }
            }
            logger.debug("#### Initializing mddl" + this);
            logger.debug("#### Value mddl"+ services.mddl);
            if (ngctx.serviceStatus.containsKey(ComponentServices.OutputDSMetadata)) {
            	logger.debug("Inside if ::");
            	services.mddl =  this;
            	logger.debug("#### Value mddl"+ services.mddl);
            }
            
        }
        catch(Exception e){
            String error = "Services initialization has failed: " + ExceptionUtils.getFullStackTrace(e);
            logger.error(error);
            return -1;
        }

        return 0;
    }


    private int initProject() {
        try {
            services.prj.getProjectData(ngctx);
            ngctx.serviceStatus.put(ComponentServices.Project, true);
        } catch (Exception e) {
            String error = "component initialization (input-discovery/output-preparation) exception: " + ExceptionUtils.getFullStackTrace(e);
            logger.error(error);
            return -1;

        }
        return 0;
    }

    public int registerOutputDataSet() {

        final int[] rc2 = {0};
        logger.debug("registering outputdataset");
        logger.debug("services ::"+ services);
        logger.debug("mddl ::"+ services.mddl);
        if (services.mddl != null) {

            if (ngctx.componentConfiguration.getOutputs() != null && ngctx.componentConfiguration.getOutputs().size() > 0) {

                WithDataSet.DataSetHelper dsaux = new WithDataSet.DataSetHelper(ngctx, services.md);
                ngctx.outputDataSets = services.mddl.ngBuildPathForOutputDataSets(dsaux);
                ngctx.outputs = services.mddl.ngBuildPathForOutputs(dsaux);

                final int[] rc = {0};

                try
                {
                  JsonObject ale = services.als.generateDSAuditLogEntry(ngctx, "INIT", ngctx.inputDataSets, ngctx.outputDataSets);
                  String aleId = services.als.createAuditLog(ngctx, ale);

                  ngctx.componentConfiguration.getOutputs().forEach(o ->
                  {
                        logger.debug("Add output object to data object repository: " + o.getDataSet());

                        Input.Dstype dsType = getOutputDatasetType();
                        logger.debug("Output DS Type : " + dsType);
                        o.setDstype(dsType);

                        if (ngctx.serviceStatus.containsKey(ComponentServices.OutputDSMetadata)) {
                            JsonElement ds = services.md.readOrCreateDataSet(ngctx, ngctx.outputDataSets.get(o.getDataSet()));
                            if (ds == null) {
                                String error = "Could not create metadata for output dataset [" + o.getDataSet() + "]: " ;
                                logger.error(error);
                                rc[0] = -1;
                                return;
                            }

                            JsonObject dsObj = ds.getAsJsonObject();
                            String id = dsObj.getAsJsonPrimitive(DataSetProperties.Id.toString()).getAsString();
                            //TODO:: Add to list of IDs
                            ngctx.registeredOutputDSIds.add(id);

                            String step = "Could not create activity log entry for DataSet [" + o.getDataSet() + "]: " ;
                            try {
                                step = "Could not update metadata of DataSet [" + o.getDataSet() + "]: " ;
                                services.md.getDSStore().updateStatus(id,"INIT", ngctx.startTs, null, aleId, ngctx.batchID);
                            } catch (Exception e) {
                                String error = step + ExceptionUtils.getFullStackTrace(e);
                                logger.error(error);
                                rc[0] = -1;
                                return;
                            }
                        }
                    });
                    ngctx.serviceStatus.put(ComponentServices.OutputDSMetadata, true);
                } catch (Exception e) {
                  logger.error("Could not create Audit log entry" +
                      ExceptionUtils.getFullStackTrace(e));
                  return -1;
                }

            }
            return rc2[0];
        }
        else{
            String  error = "Incorrect initialization sequence or dataset service is not available";
            logger.error(error);
            return -1;
        }
    }

    public Input.Dstype getOutputDatasetType() {
        String componentName = this.ngctx.componentName;
        logger.debug("getOutputDatasetType() : componentName : " + componentName);
        if(componentName != null && !componentName.trim().isEmpty()){
            switch(componentName.trim().toUpperCase()){
                case "PARSER" :
                    return Input.Dstype.PARSED;
                case "TRANSFORMER" :
                    return Input.Dstype.TRANSFORMED;
                case "SQL" :
                    return Input.Dstype.SQL;
                default:
                    return Input.Dstype.BASE;
            }
        }
        return Input.Dstype.BASE;
    }

    private int initTransformation(){
    	logger.debug("######### Inside init transformation");
        if (services.transformationMD == null ||
            !ngctx.serviceStatus.containsKey(ComponentServices.TransformationMetadata)){
            logger.error("Incorrect initialization sequence or service is not available");
            return -1;
        }
        logger.debug("######### setting transformation metadata");
        try {
            ngctx.transformationID =
                    services.transformationMD.readOrCreateTransformation(ngctx, ngctx.componentConfiguration);
            ngctx.serviceStatus.put(ComponentServices.TransformationMetadata, true);
            logger.debug("######### is transform metadata set??"+  ngctx.serviceStatus.
            		get(ComponentServices.TransformationMetadata));
            
        } catch (Exception e) {
            String error = "Exception at transformation init: " + ExceptionUtils.getFullStackTrace(e);
            logger.error(error);
            return -1;
        }
        return 0;
    }


    protected ComponentConfiguration validateConfig(String config){
        return NGContextServices.analyzeAndValidate(config);
    }

    public static ComponentConfiguration analyzeAndValidate(String cfg)
    {
        ComponentConfiguration config = new Gson().fromJson(cfg, ComponentConfiguration.class);
        return config;
    }

    @Override
    public String toString(){
        String strCtx = "Execution context: " + ngctx.toString();
        return strCtx;
    }

    public static ComponentConfiguration analyzeAndValidateTransformerConf(String configAsStr) {


            ComponentConfiguration compConf = new Gson().fromJson(configAsStr, ComponentConfiguration.class);
            Transformer transformerCfg = compConf.getTransformer();
            if (transformerCfg == null)
                throw new XDFException(XDFReturnCode.NO_COMPONENT_DESCRIPTOR, "transformer");

            if (transformerCfg.getScript() == null || transformerCfg.getScript().isEmpty()) {
                throw new XDFException(XDFReturnCode.CONFIG_ERROR, "Incorrect configuration: Transformer descriptor does not have script name.");
            }
            if (transformerCfg.getScriptLocation() == null || transformerCfg.getScriptLocation().isEmpty()) {
                throw new XDFException(XDFReturnCode.CONFIG_ERROR, "Incorrect configuration: Transformer descriptor does not have script location.");
            }

            boolean valid = false;
            for( Input inpK: compConf.getInputs()){
                if (inpK.getName() != null && inpK.getName().equalsIgnoreCase(RequiredNamedParameters.Input.toString())){
                    valid = true; break;
                }
            }

            if (!valid) throw new XDFException(XDFReturnCode.CONFIG_ERROR, "Incorrect configuration: dataset parameter with name 'input' does not exist .");

            valid = false;
            boolean rvalid = false;
            for( Output outK: compConf.getOutputs()) {
                if (outK.getName() != null && outK.getName().equalsIgnoreCase(RequiredNamedParameters.Output.toString())) {
                    valid = true;
                }
            }
            if (!valid ) throw new XDFException(XDFReturnCode.CONFIG_ERROR, "Incorrect configuration: dataset parameter with name 'output' does not exist .");
            return compConf;

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

    public static ComponentConfiguration analyzeAndValidateParserConf(String config){

        ComponentConfiguration compConf = new Gson().fromJson(config, ComponentConfiguration.class);

        Parser parserProps = compConf.getParser();
        if (parserProps == null) {
            throw new XDFException( XDFReturnCode.INVALID_CONF_FILE);
        }

        if(parserProps.getFile() == null || parserProps.getFile().length() == 0){
            throw new XDFException(XDFReturnCode.INVALID_CONF_FILE);
        }

        if(parserProps.getFields() == null || parserProps.getFields().size() == 0){
            throw new XDFException(XDFReturnCode.INVALID_CONF_FILE);
        }
        return compConf;
    }

    public static ComponentConfiguration analyzeAndValidateSqlConf(String cfgAsStr){

        ComponentConfiguration compConf = new Gson().fromJson(cfgAsStr, ComponentConfiguration.class);

        Sql SQLProps = compConf.getSql();
        if (SQLProps == null) {
            throw new XDFException(XDFReturnCode.NO_COMPONENT_DESCRIPTOR, "sql");
        }
        if (SQLProps.getScript() == null || SQLProps.getScript().isEmpty()) {
            throw new XDFException(XDFReturnCode.CONFIG_ERROR, "Incorrect configuration: Spark SQL does not have SQL script name.");
        }
        if (SQLProps.getScriptLocation() == null || SQLProps.getScriptLocation().isEmpty()) {
            throw new XDFException(XDFReturnCode.CONFIG_ERROR, "Incorrect configuration: Spark SQL descriptor does not have SQL script location.");
        }
        return compConf;
    }

    public static ComponentConfiguration analyzeAndValidateEsLoaderConf(String cfgAsStr){

        ComponentConfiguration compConf = new Gson().fromJson(cfgAsStr, ComponentConfiguration.class);

        ESLoader esLoaderConfig = compConf.getEsLoader();
        if (esLoaderConfig == null)
            throw new XDFException(XDFReturnCode.NO_COMPONENT_DESCRIPTOR, "es-loader");

        if (esLoaderConfig.getEsNodes() == null || esLoaderConfig.getEsNodes().isEmpty()) {
            throw new XDFException(XDFReturnCode.CONFIG_ERROR, "Incorrect configuration: ElasticSearch Nodes configuration missing.");
        }
        if (esLoaderConfig.getEsPort() == 0) {
            throw new XDFException(XDFReturnCode.CONFIG_ERROR, "Incorrect configuration: ElasticSearch Port configuration missing.");
        }
        if (esLoaderConfig.getDestinationIndexName() == null || esLoaderConfig.getDestinationIndexName().isEmpty()) {
            throw new XDFException(XDFReturnCode.CONFIG_ERROR, "Incorrect configuration: ElasticSearch Destination Index Name missing.");
        }
        if (esLoaderConfig.getEsClusterName() == null || esLoaderConfig.getEsClusterName().isEmpty()) {
            throw new XDFException(XDFReturnCode.CONFIG_ERROR, "Incorrect configuration: ElasticSearch clustername configuration missing.");
        }
        return compConf;
    }


}
