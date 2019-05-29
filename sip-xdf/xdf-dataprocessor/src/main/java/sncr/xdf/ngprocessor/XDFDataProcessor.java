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

public class XDFDataProcessor  extends AbstractComponent {
    {
        componentName = "pipeline";
    }

    private  String PIPELINE_CONFIG;
    private String RUNNING_MODE  = "true";
    private static final Logger logger = Logger.getLogger(XDFDataProcessor.class);
    Map<String, Dataset> datafileDFmap = new HashMap<>();


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
                    processParser(parameters,pipeObj.get("configuration").toString(),pipeObj.get("persist").toString());
                }
                if ( pipeObj.get("component").equals("transformer"))
                {
                    logger.debug("Processing Transformer Component ---> " + pipeObj.get("component"));
                    processTransformer(parameters,pipeObj.get("configuration").toString(),pipeObj.get("persist").toString());
                }
                if ( pipeObj.get("component").equals("sql"))
                {
                    logger.debug("Processing Transformer Component ---> " + pipeObj.get("component"));
                    processSQL(parameters,pipeObj.get("configuration").toString(),pipeObj.get("persist").toString());
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

    public void processTransformer(Map<String, Object> parameters, String configPath,String persistFlag)
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

            ngTransformerCtxSvc.getNgctx().datafileDFmap =  new HashMap<>();
            ngTransformerCtxSvc.getNgctx().dataSetName = config.getInputs().get(0).getDataSet().toString();
            ngTransformerCtxSvc.getNgctx().datafileDFmap = datafileDFmap;

            ngTransformerCtxSvc.getNgctx().runningPipeLine = RUNNING_MODE;
            ngTransformerCtxSvc.getNgctx().persistMode = persistFlag;

            NGTransformerComponent tcomponent = new NGTransformerComponent(ngTransformerCtxSvc.getNgctx());

            if (!tcomponent.initTransformerComponent(null))
                System.exit(-1);

            tcomponent.run();

            datafileDFmap = ngTransformerCtxSvc.getNgctx().datafileDFmap;

            logger.debug("Transformation completed: "  + " Size is : " + datafileDFmap.size() + "\n");

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }


    public void processParser(Map<String, Object> parameters, String configPath,String persistFlag)
    {
        try {
            String runningMode  = "true";

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

            ngParserCtxSvc.getNgctx().datafileDFmap =  new HashMap<>();
            ngParserCtxSvc.getNgctx().dataSetName = cfg.getOutputs().get(0).getDataSet().toString();

            logger.warn("Output datasets:");

            ngParserCtxSvc.getNgctx().registeredOutputDSIds.forEach( id ->
                logger.warn(id)
            );

            logger.warn(ngParserCtxSvc.getNgctx().toString());

            ngParserCtxSvc.getNgctx().runningPipeLine = RUNNING_MODE;
            ngParserCtxSvc.getNgctx().persistMode = persistFlag;

            NGParser component = new NGParser(ngParserCtxSvc.getNgctx());

            if (!component.initComponent(null))
                System.exit(-1);

            component.run();

            datafileDFmap = ngParserCtxSvc.getNgctx().datafileDFmap;

            logger.debug("Parser completed: "  + " Size is : " + datafileDFmap.size() + "\n");

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }


    public void processSQL(Map<String, Object> parameters, String configPath,String persistFlag)
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
                    ComponentServices.OutputDSMetadata,
                    ComponentServices.Project,
                    ComponentServices.TransformationMetadata,
                    ComponentServices.Spark
                };


            logger.debug("Starting SQL component :" + "\n" );

            ComponentConfiguration config = NGContextServices.analyzeAndValidateSqlConf(configAsStr);

            NGContextServices ngSQLCtxSvc = new NGContextServices(sqlcs, xdfDataRootSys, config, appId,
                "sql", batchId);
            ngSQLCtxSvc.initContext(); // debug
            ngSQLCtxSvc.registerOutputDataSet();

            logger.trace("Output datasets:   ");

            ngSQLCtxSvc.getNgctx().registeredOutputDSIds.forEach( id ->
                logger.trace(id)
            );

            logger.debug(" DataSetnAME IS  :" + config.getInputs().get(0).getDataSet().toString() +  "\n" );

            ngSQLCtxSvc.getNgctx().datafileDFmap =  new HashMap<>();
            ngSQLCtxSvc.getNgctx().dataSetName = config.getInputs().get(0).getDataSet().toString();
            ngSQLCtxSvc.getNgctx().datafileDFmap = datafileDFmap;

            ngSQLCtxSvc.getNgctx().runningPipeLine = RUNNING_MODE;
            ngSQLCtxSvc.getNgctx().persistMode = persistFlag;

            NGSQLComponent sqlcomponent = new NGSQLComponent(ngSQLCtxSvc.getNgctx());

            if (!sqlcomponent.initSQLComponent(null))
                System.exit(-1);

            sqlcomponent.run();

            datafileDFmap = ngSQLCtxSvc.getNgctx().datafileDFmap;

            logger.debug("SQL completed: "  + " Size is : " + datafileDFmap.size() + "\n");

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }



}
