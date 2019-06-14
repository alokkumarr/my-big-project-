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
import sncr.xdf.context.ComponentServices;
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

    private  String PIPELINE_CONFIG;
    private boolean RUNNING_MODE  = true;
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
        CliHandler cli = new CliHandler();

        try {
            HFileOperations.init(10);
            Map<String, Object> parameters = cli.parse(args);

            PIPELINE_CONFIG = (String) parameters.get(CliHandler.OPTIONS.CONFIG.name());
            JSONObject jsonObj =  LoadPipelineConfig(PIPELINE_CONFIG);

            JSONArray pipeline = (JSONArray) jsonObj.get("pipeline");

            for(int i=0;i<pipeline.size();i++)
            {
                JSONObject pipeObj = (JSONObject)pipeline.get(i);
                if ( pipeObj.get("component").equals("parser"))
                {
                    logger.debug("Processing Parser Component ---> " + pipeObj.get("component"));
                    processParser(parameters,pipeObj.get("configuration").toString(),Boolean.parseBoolean(pipeObj.get("persist").toString()));
                }
                if ( pipeObj.get("component").equals("transformer"))
                {
                    logger.debug("Processing Transformer Component ---> " + pipeObj.get("component"));
                    processTransformer(parameters,pipeObj.get("configuration").toString(),Boolean.parseBoolean(pipeObj.get("persist").toString()));
                }
                if ( pipeObj.get("component").equals("sql"))
                {
                    logger.debug("Processing Transformer Component ---> " + pipeObj.get("component"));
                    processSQL(parameters,pipeObj.get("configuration").toString(),Boolean.parseBoolean(pipeObj.get("persist").toString()));
                }
                if ( pipeObj.get("component").equals("esloader"))
                {
                    logger.debug("Processing ESLoader Component ---> " + pipeObj.get("component"));
                    processESLoader(parameters,pipeObj.get("configuration").toString(),Boolean.parseBoolean(pipeObj.get("persist").toString()));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    protected int archive()
    {
        return 0;
    }

    protected int move()
    {
        return 0;
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
            e.printStackTrace();
        }
        return pipelineObj;
    }

    public void processParser(Map<String, Object> parameters, String configPath,boolean persistFlag)
    {
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
            NGContextServices ngParserCtxSvc = new NGContextServices(pcs, xdfDataRootSys, cfg, appId, "parser", batchId);
            ngParserCtxSvc.initContext();
            ngParserCtxSvc.registerOutputDataSet();

            logger.warn("Output datasets:");

            ngParserCtxSvc.getNgctx().registeredOutputDSIds.forEach( id ->
                logger.warn(id)
            );

            logger.warn(ngParserCtxSvc.getNgctx().toString());

            ngParserCtxSvc.getNgctx().datafileDFmap =  new HashMap<>();
            String parserKey =  cfg.getOutputs().get(0).getDataSet().toString();
            ngParserCtxSvc.getNgctx().dataSetName = parserKey;
            ngParserCtxSvc.getNgctx().runningPipeLine = RUNNING_MODE;
            ngParserCtxSvc.getNgctx().persistMode = persistFlag;

            NGParser component = new NGParser(ngParserCtxSvc.getNgctx());

            if (!component.initComponent(null))
                System.exit(-1);

            component.run();

            datafileDFmap =  new HashMap<>();
            datafileDFmap.put(parserKey,ngParserCtxSvc.getNgctx().datafileDFmap.get(ngParserCtxSvc.getNgctx().dataSetName));
            dataSetName = parserKey;

            logger.debug("End Of Parser Component  dataSetName :" + dataSetName +  "\n" );
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void processTransformer(Map<String, Object> parameters, String configPath,boolean persistFlag)
    {
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
                "transformer", batchId);
            ngTransformerCtxSvc.initContext(); // debug
            ngTransformerCtxSvc.registerOutputDataSet();

            logger.trace("Output datasets:   ");

            ngTransformerCtxSvc.getNgctx().registeredOutputDSIds.forEach( id ->
                logger.trace(id)
            );

            String transInKey =  config.getInputs().get(0).getDataSet().toString();
            String transOutKey =  config.getOutputs().get(0).getDataSet().toString();

            ngTransformerCtxSvc.getNgctx().datafileDFmap =  new HashMap<>();
            ngTransformerCtxSvc.getNgctx().dataSetName = transInKey;
            ngTransformerCtxSvc.getNgctx().datafileDFmap.put(transInKey,datafileDFmap.get(dataSetName));
            ngTransformerCtxSvc.getNgctx().runningPipeLine = RUNNING_MODE;
            ngTransformerCtxSvc.getNgctx().persistMode = persistFlag;

            NGTransformerComponent tcomponent = new NGTransformerComponent(ngTransformerCtxSvc.getNgctx());

            if (!tcomponent.initTransformerComponent(null))
                System.exit(-1);

            tcomponent.run();

            datafileDFmap =  new HashMap<>();
            datafileDFmap.put(transOutKey,ngTransformerCtxSvc.getNgctx().datafileDFmap.get(ngTransformerCtxSvc.getNgctx().dataSetName));
            dataSetName = transOutKey;

            logger.debug("End Of Transformer Component  dataSetName :" + dataSetName +  "\n" );

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }


    public void processSQL(Map<String, Object> parameters, String configPath,boolean persistFlag)
    {
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

            logger.debug("Starting SQL component :" + "\n" );

            logger.debug("Starting SQL component  dataSetName :" + dataSetName +  "\n" );

            ComponentConfiguration config = NGContextServices.analyzeAndValidateSqlConf(configAsStr);

            NGContextServices ngSQLCtxSvc = new NGContextServices(sqlcs, xdfDataRootSys, config, appId,
                "sql", batchId);
            ngSQLCtxSvc.initContext(); // debug
            ngSQLCtxSvc.registerOutputDataSet();

            logger.trace("Output datasets:   ");

            ngSQLCtxSvc.getNgctx().registeredOutputDSIds.forEach( id ->
                logger.trace(id)
            );


            String sqlInKey =  dataSetName;
            int sqlOutputSize = config.getOutputs().size();

            logger.debug("SQL component sqlOutputSize  :" + sqlOutputSize + "\n" );

            String sqlOutKey =  config.getOutputs().get(sqlOutputSize-1).getDataSet().toString();

            ngSQLCtxSvc.getNgctx().datafileDFmap =  new HashMap<>();
            ngSQLCtxSvc.getNgctx().dataSetName = sqlInKey; //TRANS_out
            ngSQLCtxSvc.getNgctx().datafileDFmap.put(sqlInKey,datafileDFmap.get(dataSetName)); //TRANS_OUT
            ngSQLCtxSvc.getNgctx().runningPipeLine = RUNNING_MODE;
            ngSQLCtxSvc.getNgctx().persistMode = persistFlag;

            NGSQLComponent sqlcomponent = new NGSQLComponent(ngSQLCtxSvc.getNgctx());

            if (!sqlcomponent.initComponent(null))
                System.exit(-1);

            sqlcomponent.run();

            logger.debug("SQL ngSQLCtxSvc.getNgctx().dataSetName  :" + ngSQLCtxSvc.getNgctx().dataSetName + "\n" );

            datafileDFmap =  new HashMap<>();
            datafileDFmap.put(sqlOutKey,ngSQLCtxSvc.getNgctx().datafileDFmap.get(ngSQLCtxSvc.getNgctx().dataSetName));
            dataSetName = sqlOutKey;

            logger.debug("End Of SQL Component  dataSetName :" + dataSetName +  "\n" );


        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void processESLoader(Map<String, Object> parameters, String configPath,boolean persistFlag)
    {
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

            String dataSetInKey =  config.getInputs().get(0).getDataSet();

            logger.debug("ES loader Dataset Name is    " + dataSetInKey);

            ngESCtxSvc.getNgctx().datafileDFmap =  new HashMap<>();
            ngESCtxSvc.getNgctx().dataSetName = dataSetInKey;
            ngESCtxSvc.getNgctx().datafileDFmap.put(dataSetInKey,datafileDFmap.get(dataSetName));
            ngESCtxSvc.getNgctx().runningPipeLine = RUNNING_MODE;

            NGESLoaderComponent esloader = new NGESLoaderComponent(ngESCtxSvc.getNgctx());

            if (!esloader.initComponent(null))
                System.exit(-1);

            esloader.run();

            logger.debug("End of ESLoader Component done : " + ngESCtxSvc.getNgctx().dataSetName + "\n" );

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }


}
