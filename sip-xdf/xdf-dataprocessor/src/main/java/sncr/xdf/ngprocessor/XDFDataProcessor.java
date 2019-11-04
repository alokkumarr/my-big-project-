package sncr.xdf.ngprocessor;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sncr.bda.CliHandler;
import sncr.bda.ConfigLoader;
import sncr.bda.base.MetadataBase;
import sncr.bda.conf.ComponentConfiguration;
import sncr.bda.core.file.HFileOperations;
import sncr.bda.conf.ComponentServices;
import sncr.xdf.exceptions.XDFException;
import sncr.xdf.ngcomponent.AbstractComponent;
import sncr.xdf.services.NGContextServices;
import java.util.HashMap;
import java.util.Map;

import sncr.xdf.transformer.ng.NGTransformerComponent;
import sncr.xdf.parser.NGParser;
import sncr.xdf.sql.ng.NGSQLComponent;
import sncr.xdf.esloader.NGESLoaderComponent;

public class XDFDataProcessor  extends AbstractComponent {
    {
        componentName = "pipeline";
    }

    public XDFDataProcessor()
    {

    }

    public XDFDataProcessor(Dataset dataset)
    {
        Map<String, Dataset> datafileDFmap = new HashMap<>();
        datafileDFmap.put("DATA_STREAM",dataset);
    }

    private  String pipelineConfig = "";
    private boolean runningMode  = true;
    private static final Logger logger = Logger.getLogger(XDFDataProcessor.class);
    Map<String, Dataset> datafileDFmap = new HashMap<>();
    String dataSetName = "";

    public static void main(String[] args)  {

        long start_time = System.currentTimeMillis();

        XDFDataProcessor processor = new XDFDataProcessor();
        processor.processData(args);

        long end_time = System.currentTimeMillis();
        long difference = end_time-start_time;
        logger.debug("Pipeline total time for processing all the components : " + difference );
    }

    protected int execute()
    {
        return 0;
    }

    protected void  processData(String[] args)
    {
        int ret = 0 ;

        CliHandler cli = new CliHandler();

        try {

            HFileOperations.init(10);
            Map<String, Object> parameters = cli.parse(args);

            pipelineConfig = (String) parameters.get(CliHandler.OPTIONS.CONFIG.name());
            JSONObject jsonObj =  LoadPipelineConfig(pipelineConfig);

            JSONArray pipeline = (JSONArray) jsonObj.get("pipeline");

            for(int i=0;i<pipeline.size();i++)
            {
                JSONObject pipeObj = (JSONObject)pipeline.get(i);

                String component = pipeObj.get("component").toString();
                boolean persist = Boolean.parseBoolean(pipeObj.get("persist").toString());
                logger.debug("Processing   ---> " + pipeObj.get("component") + " Component" + "\n" );
                switch(component)
                {
                    case "parser" :
                    ret = processParser(parameters,pipeObj.get("configuration").toString(),persist);
                    break;

                    case "transformer" :
                    ret = processTransformer(parameters,pipeObj.get("configuration").toString(),persist);
                    break;

                    case "sql" :
                    ret = processSQL(parameters,pipeObj.get("configuration").toString(),persist);
                    break;

                    case "esloader" :
                    ret = processESLoader(parameters,pipeObj.get("configuration").toString(),persist);
                    break;
                }
            }

        } catch (Exception e) {
            logger.debug("XDFDataProcessor:processData() Exception is : " + e + "\n");
            System.exit(ret);
        }
    }


    public static ComponentConfiguration analyzeAndValidate(String cfg) throws Exception
    {
        ComponentConfiguration config = new Gson().fromJson(cfg, ComponentConfiguration.class);
        logger.debug("ComponentConfiguration: " + config + "\n");
        return config;
    }

    public JSONObject LoadPipelineConfig(String cfg)
    {
        JSONParser parser = new JSONParser();
        JSONObject pipelineObj = null;
        String reader = ConfigLoader.loadConfiguration(cfg);
        try {
            pipelineObj = (JSONObject) parser.parse(reader);
        } catch (ParseException e) {
            logger.debug("XDFDataProcessor:LoadPipelineConfig() ParseException is : " + e + "\n");
            System.exit(-1);
        }
        return pipelineObj;
    }

    public int  processParser(Map<String, Object> parameters, String configPath, boolean persistFlag)
    {
        int ret = 0;
        try {
            String configAsStr = ConfigLoader.loadConfiguration(configPath);

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

            ComponentServices pcs[] = {
                ComponentServices.OutputDSMetadata,
                ComponentServices.Project,
                ComponentServices.TransformationMetadata,
                ComponentServices.Spark
            };

            ComponentConfiguration cfg = analyzeAndValidate(configAsStr);
            NGContextServices ngParserCtxSvc = new NGContextServices(pcs, xdfDataRootSys, cfg, appId, "parser", batchId, persistFlag);
            ngParserCtxSvc.initContext();
            ngParserCtxSvc.registerOutputDataSet();

            logger.warn("Output datasets:");

            ngParserCtxSvc.getNgctx().registeredOutputDSIds.forEach( id ->
                logger.warn(id)
            );

            logger.warn(ngParserCtxSvc.getNgctx().toString());

            logger.debug("Parser Input dataset size is : " + datafileDFmap.size() );

            ngParserCtxSvc.getNgctx().datafileDFmap.putAll ( this.datafileDFmap);
            String parserKey =  cfg.getOutputs().get(0).getDataSet().toString();
            ngParserCtxSvc.getNgctx().dataSetName = parserKey;
            ngParserCtxSvc.getNgctx().runningPipeLine = runningMode;

            NGParser component = new NGParser(ngParserCtxSvc.getNgctx());

            if (!component.initComponent(null))
                System.exit(-1);

            ret = component.run();
            
            
            if (ret != 0){
                error = "Could not complete Parser component " + "entry";
                throw new Exception(error);
            }

            datafileDFmap.putAll(ngParserCtxSvc.getNgctx().datafileDFmap);

            logger.debug("End Of Parser Component ==>  dataSetKeys  & size " + datafileDFmap.keySet() + "," + datafileDFmap.size()+ "\n");
        } catch (Exception e) {
            logger.debug("XDFDataProcessor:processParser() Exception is : " + e + "\n");
            System.exit(-1);
        }
        return ret;
    }

    public int processTransformer(Map<String, Object> parameters, String configPath, boolean persistFlag)
    {
        int ret = 0;
        try {

            String configAsStr = ConfigLoader.loadConfiguration(configPath);

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
                    ComponentServices.OutputDSMetadata,
                    ComponentServices.Project,
                    ComponentServices.TransformationMetadata,
                    ComponentServices.Spark
                };

            logger.debug("Starting Transformer component :" + "\n" );

            ComponentConfiguration config = NGContextServices.analyzeAndValidateTransformerConf(configAsStr);

            NGContextServices ngTransformerCtxSvc = new NGContextServices(scs, xdfDataRootSys, config, appId,
                "transformer", batchId, persistFlag);

            ngTransformerCtxSvc.initContext(); // debug

            ngTransformerCtxSvc.registerOutputDataSet();

            logger.trace("Output datasets:   ");

            ngTransformerCtxSvc.getNgctx().registeredOutputDSIds.forEach( id ->
                logger.trace(id)
            );

            String transInKey =  config.getInputs().get(0).getDataSet().toString();

            ngTransformerCtxSvc.getNgctx().datafileDFmap.putAll(this.datafileDFmap);
            ngTransformerCtxSvc.getNgctx().runningPipeLine = runningMode;

            NGTransformerComponent tcomponent = new NGTransformerComponent(ngTransformerCtxSvc.getNgctx());

            if (!tcomponent.initTransformerComponent(null))
                System.exit(-1);

            ret = tcomponent.run();

            if (ret != 0){
                error = "Could not complete Transformer component " + "entry";
                throw new Exception(error);
            }

            datafileDFmap.putAll(ngTransformerCtxSvc.getNgctx().datafileDFmap);

            logger.debug("End Of Transformer Component ==>  dataSetKeys  & size " + datafileDFmap.keySet() + "," + datafileDFmap.size()+ "\n");
        } catch (Exception e) {
            logger.debug("XDFDataProcessor:processTransformer() Exception is : " + e + "\n");
            System.exit(-1);
        }
        return ret;
    }


    public int processSQL(Map<String, Object> parameters, String configPath, boolean persistFlag)
    {
    	
    	int ret = 0;
        try {

            String configAsStr = ConfigLoader.loadConfiguration(configPath);

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

            ComponentServices[] sqlcs =
                {
                    //ComponentServices.InputDSMetadata,
                    ComponentServices.OutputDSMetadata,
                    ComponentServices.Project,
                    ComponentServices.TransformationMetadata,
                    ComponentServices.Spark
                };

            logger.debug("Starting SQL component  dataSetName :" + dataSetName +  "\n" );

            ComponentConfiguration config = NGContextServices.analyzeAndValidateSqlConf(configAsStr);

            NGContextServices ngSQLCtxSvc = new NGContextServices(sqlcs, xdfDataRootSys, config, appId,
                "sql", batchId, persistFlag);
            ngSQLCtxSvc.initContext(); // debug
            ngSQLCtxSvc.registerOutputDataSet();

            logger.trace("Output datasets:   ");

            ngSQLCtxSvc.getNgctx().registeredOutputDSIds.forEach( id ->
                logger.trace(id)
            );

            String sqlInKey =  dataSetName;
            int sqlOutputSize = config.getOutputs().size();

            logger.debug("SQL component sqlOutputSize  :" + sqlOutputSize + "\n" );

            //String sqlOutKey =  config.getOutputs().get(sqlOutputSize-1).getDataSet().toString();

            ngSQLCtxSvc.getNgctx().datafileDFmap.putAll(this.datafileDFmap);
            ngSQLCtxSvc.getNgctx().dataSetName = sqlInKey; //TRANS_out
            ngSQLCtxSvc.getNgctx().runningPipeLine = runningMode;
            ngSQLCtxSvc.getNgctx().pipeComponentName = "sql";

            NGSQLComponent sqlcomponent = new NGSQLComponent(ngSQLCtxSvc.getNgctx());

            if (!sqlcomponent.initComponent(null))
                System.exit(-1);
            

            ret = sqlcomponent.run();

            if (ret != 0){
                error = "Could not complete SQL component " + "entry";
                logger.error(error);
                throw new Exception(error);
            }

            datafileDFmap.putAll(ngSQLCtxSvc.getNgctx().datafileDFmap);

            logger.debug("End Of SQL Component ==>  dataSetKeys  & size " + datafileDFmap.keySet() + "," + datafileDFmap.size()+ "\n");

        } catch (Exception e) {
            logger.debug("XDFDataProcessor:processSQL() Exception is : " + e + "\n");
            System.exit(-1);
        }
        return ret;
    }

    public int processESLoader(Map<String, Object> parameters, String configPath, boolean persistFlag)
    {
        int ret = 0;

        try {

            String configAsStr = ConfigLoader.loadConfiguration(configPath);

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

            ComponentServices[] escs =
                {
                    ComponentServices.Project,
                    ComponentServices.TransformationMetadata,
                    ComponentServices.Spark
                };

            logger.debug("Starting ESLoader component :" + "\n" );

            ComponentConfiguration config = NGContextServices.analyzeAndValidateEsLoaderConf(configAsStr);
            NGContextServices ngESCtxSvc = new NGContextServices(escs, xdfDataRootSys, config, appId,
                "esloader", batchId);
            ngESCtxSvc.initContext(); // debug

            logger.trace("Output datasets:   ");

            ngESCtxSvc.getNgctx().registeredOutputDSIds.forEach( id ->
                logger.trace(id)
            );
            ngESCtxSvc.registerOutputDataSet();
            

            ngESCtxSvc.getNgctx().datafileDFmap.putAll(datafileDFmap);
            ngESCtxSvc.getNgctx().runningPipeLine = runningMode;
            
            NGESLoaderComponent esloader = new NGESLoaderComponent(ngESCtxSvc.getNgctx());

            if (!esloader.initComponent(null))
                System.exit(-1);
            ret = esloader.run();

            if (ret != 0){
                error = "Could not complete ESLoader component " + "entry";
                throw new Exception(error);
            }

            logger.debug("End Of ESLoader Component ==>  dataSetName  & size " +  ngESCtxSvc.getNgctx().dataSetName + "," + ngESCtxSvc.getNgctx().datafileDFmap.size()+ "\n");

        } catch (Exception e) {
            logger.debug("XDFDataProcessor:processESLoader() Exception is : " + e + "\n");
        }
        return ret;
    }


    protected int archive()
    {
        return 0;
    }

    protected int move()
    {
        return 0;
    }


}
