package sncr.xdf.rtps.driver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.gson.Gson;
import sncr.xdf.context.ComponentServices;

import sncr.bda.CliHandler;
import sncr.bda.ConfigLoader;
import sncr.bda.base.MetadataBase;
import sncr.bda.conf.ComponentConfiguration;
import sncr.xdf.esloader.NGESLoaderComponent;
import sncr.xdf.exceptions.XDFException;
import sncr.xdf.parser.NGParser;
import sncr.xdf.services.NGContextServices;
import sncr.xdf.sql.ng.NGSQLComponent;
import sncr.xdf.transformer.ng.NGTransformerComponent;
import sncr.xdf.ngcomponent.AbstractComponent;
import sncr.xdf.context.XDFReturnCode;

public class RTPSPipelineProcessor {

	public RTPSPipelineProcessor() {
	}

	public RTPSPipelineProcessor(Dataset<Row> dataset) {
		logger.debug("### creating parser with dataset ###");
	    this.datafileDFmap = new HashMap<>();
		this.datafileDFmap.put("DATA_STREAM", dataset.cache());
	}

	public RTPSPipelineProcessor(List<Row> rowsList) {
		// TODO Auto-generated constructor stub
	}

	private String PIPELINE_CONFIG;
	private JSONObject jsonObj;
	private Map<String, Object> pipelineConfigParams;
	private boolean RUNNING_MODE = true;
	private static final Logger logger = Logger.getLogger(RTPSPipelineProcessor.class);
	Map<String, Dataset> datafileDFmap;
	String dataSetName = "";
	String error;
	String[] args;

	public int processDataWithDataFrame(JSONObject pipeLineConfig, Map<String, Object> pipelineParams, String type) {
		logger.info("###### Starting pipeline with dataset.....:) for event type "+ type);
		Dataset<Row> dataset = null;
		int rc = 0;
		int pipeLinStartIndex = 0;
		JSONArray pipeline = null;
		JSONObject rtaConfig = null;
		Object config = null;
		JSONObject jsonConfig = null;
		try {
            dataset = this.datafileDFmap.get("DATA_STREAM");
            logger.debug("###### Dataset display completed.....:"+ dataset);
			config =  pipeLineConfig.get("pipeline");
			if( config instanceof JSONObject) {
			    jsonConfig = (JSONObject)config;
				rtaConfig = (JSONObject)jsonConfig.get("rta");
			    logger.debug("### Pipeline config in RTPS pipeline ::"+ rtaConfig);
			} else if( config instanceof JSONArray){
				 pipeline = (JSONArray)config;
				 logger.debug("### Pipeline config in RTPS pipeline ::"+ pipeLineConfig);
			}
			logger.debug("###### rta config "+ rtaConfig);
    		if(rtaConfig ==null){
    			logger.info("is Multiple doesnt exists ::");
				logger.debug("### Pipeline configuration retrived successfully starting processing");
				/**
				 * If single pipeline, then need not process
				 * first pipeline entry i.e rtps
				 */
				pipeLinStartIndex = 1;
    		} else {
    			logger.info("is Multiple  exists ::");
    			pipeline = (JSONArray) jsonConfig.get(type);
    			logger.debug("#######pipeline"  + pipeline);
    		}
			if(pipeline == null) {
				logger.error("####No pipeline defined for event type "+ type);
			} else {
				for (int i = pipeLinStartIndex; i < pipeline.size() && rc == 0; i++) {
					logger.info("### Pipeline starting from ::"+ pipeLinStartIndex);
					JSONObject pipeObj = (JSONObject) pipeline.get(i);
					String component = pipeObj.get("component").toString();
					boolean persist = Boolean.parseBoolean(pipeObj.get("persist").toString());
					logger.debug("######## Processing   ---> " + pipeObj.get("component") + " Component" + "\n");
					logger.debug("######## Config   ---> " + pipeObj.get("configuration").toString() );
					logger.debug("######## Params   ---> " + pipelineParams );
					switch (component) {
					case "parser":
						rc = processParser(pipelineParams, pipeObj.get("configuration").toString(), persist);
						break;
					case "transformer":
						rc = processTransformer(pipelineParams, pipeObj.get("configuration").toString(), persist);
						break;
					case "sql":
						rc = processSQL(pipelineParams, pipeObj.get("configuration").toString(), persist);
						break;
					case "esloader":
						rc = processESLoader(pipelineParams, pipeObj.get("configuration").toString(), persist);
						break;
					}
				}
			}
        } catch(Exception e){
            logger.error("RTPSPipelineProcessor:processData() Exception is : ",e);
            if (e instanceof XDFException) {
                rc = ((XDFException)e).getReturnCode().getCode();
            } else {
                rc = XDFReturnCode.INTERNAL_ERROR.getCode();
            }
        }
        return rc;
	}

	public static ComponentConfiguration analyzeAndValidate(String cfg) throws Exception {
		ComponentConfiguration config = new Gson().fromJson(cfg, ComponentConfiguration.class);
		logger.debug("ComponentConfiguration: " + config + "\n");
		return config;
	}

	public int processParser(Map<String, Object> parameters, String configPath, boolean persistFlag) {
		logger.debug("###### Starting parser in pipe line with dataset.....updated");
        NGParser component = null;
        int rc= 0;
        Exception exception = null;
		try {
			String configAsStr = ConfigLoader.loadConfiguration(configPath);

			if (configAsStr == null || configAsStr.isEmpty()) {
				throw new XDFException(XDFReturnCode.INCORRECT_OR_ABSENT_PARAMETER, "configuration file name");
			}

			String appId = (String) parameters.get(CliHandler.OPTIONS.APP_ID.name());
			if (appId == null || appId.isEmpty()) {
				throw new XDFException(XDFReturnCode.INCORRECT_OR_ABSENT_PARAMETER, "Project/application name");
			}

			String batchId = (String) parameters.get(CliHandler.OPTIONS.BATCH_ID.name());
			if (batchId == null || batchId.isEmpty()) {
				throw new XDFException(XDFReturnCode.INCORRECT_OR_ABSENT_PARAMETER, "batch id/session id");
			}

			String xdfDataRootSys = System.getProperty(MetadataBase.XDF_DATA_ROOT);
			if (xdfDataRootSys == null || xdfDataRootSys.isEmpty()) {
				throw new XDFException(XDFReturnCode.INCORRECT_OR_ABSENT_PARAMETER, "XDF Data root");
			}

			ComponentServices pcs[] = { ComponentServices.OutputDSMetadata, ComponentServices.Project,
					ComponentServices.TransformationMetadata, ComponentServices.Spark };

			ComponentConfiguration cfg = analyzeAndValidate(configAsStr);
			logger.debug("#### After reading config:: Parser "+ cfg.getParser());
			logger.debug("###### Setting up ngContext services with dataset.....");
			NGContextServices ngParserCtxSvc = new NGContextServices(pcs, xdfDataRootSys, cfg, appId, "parser",
					batchId);
			logger.debug("#### services :: "+ pcs.length);
			ngParserCtxSvc.initContext();
			
			
			logger.debug("#### starting register outputdatset :: "+ pcs.length);
			ngParserCtxSvc.registerOutputDataSet();

			logger.warn("Output datasets:");

			ngParserCtxSvc.getNgctx().registeredOutputDSIds.forEach(id -> logger.warn(id));

			logger.warn(ngParserCtxSvc.getNgctx().toString());
			logger.debug("###### Registered outputs.....");

			ngParserCtxSvc.getNgctx().datafileDFmap = new HashMap<>();
			String parserKey =  cfg.getOutputs().get(0).getDataSet().toString();
            ngParserCtxSvc.getNgctx().dataSetName = parserKey;
			ngParserCtxSvc.getNgctx().datafileDFmap.putAll ( this.datafileDFmap);
			ngParserCtxSvc.getNgctx().runningPipeLine = RUNNING_MODE;
			ngParserCtxSvc.getNgctx().persistMode = persistFlag;

			Dataset dataset = datafileDFmap.get("DATA_STREAM");
			logger.debug("######Retrived dataset and passing to parser #####");
			component = new NGParser(ngParserCtxSvc.getNgctx(),  dataset, true);
			logger.debug("######Starting init component #####");
            if (component.initComponent(null)) {
                rc = component.run();
                if (rc == 0) {
                    datafileDFmap.putAll(ngParserCtxSvc.getNgctx().datafileDFmap);
                    //datafileDFmap.put(parserKey,
                    //	ngParserCtxSvc.getNgctx().datafileDFmap.get(ngParserCtxSvc.getNgctx().dataSetName).cache());
                    //dataSetName = parserKey;
                    logger.debug("End Of Parser Component ==>  dataSetKeys  & size " + datafileDFmap.keySet() + "," + datafileDFmap.size()+ "\n");
                }
            }
        }catch (Exception ex) {
            logger.error("RTPSPipelineProcessor:processParser() Exception is : ",ex);
            exception = ex;
        }
        rc = AbstractComponent.handleErrors(component, rc, exception);
        return rc;
	}


	public int processTransformer(Map<String, Object> parameters, String configPath, boolean persistFlag) {
        NGTransformerComponent component = null;
        int rc= 0;
        Exception exception = null;
		try {
			String configAsStr = ConfigLoader.loadConfiguration(configPath);

			if (configAsStr == null || configAsStr.isEmpty()) {
				throw new XDFException(XDFReturnCode.INCORRECT_OR_ABSENT_PARAMETER, "configuration file name");
			}

			String appId = (String) parameters.get(CliHandler.OPTIONS.APP_ID.name());
			if (appId == null || appId.isEmpty()) {
				throw new XDFException(XDFReturnCode.INCORRECT_OR_ABSENT_PARAMETER, "Project/application name");
			}

			String batchId = (String) parameters.get(CliHandler.OPTIONS.BATCH_ID.name());
			if (batchId == null || batchId.isEmpty()) {
				throw new XDFException(XDFReturnCode.INCORRECT_OR_ABSENT_PARAMETER, "batch id/session id");
			}

			String xdfDataRootSys = System.getProperty(MetadataBase.XDF_DATA_ROOT);
			if (xdfDataRootSys == null || xdfDataRootSys.isEmpty()) {
				throw new XDFException(XDFReturnCode.INCORRECT_OR_ABSENT_PARAMETER, "XDF Data root");
			}

			ComponentServices[] scs = { ComponentServices.OutputDSMetadata, ComponentServices.Project,
					ComponentServices.TransformationMetadata, ComponentServices.Spark };

			logger.debug("Starting Transformer component :" + "\n");

			ComponentConfiguration config = NGContextServices.analyzeAndValidateTransformerConf(configAsStr);

			NGContextServices ngTransformerCtxSvc = new NGContextServices(scs, xdfDataRootSys, config, appId,
					"transformer", batchId);
			ngTransformerCtxSvc.initContext(); // debug
			ngTransformerCtxSvc.registerOutputDataSet();

			logger.trace("Output datasets:   ");

			ngTransformerCtxSvc.getNgctx().registeredOutputDSIds.forEach(id -> logger.trace(id));

			String transInKey = config.getInputs().get(0).getDataSet().toString();
			String transOutKey = config.getOutputs().get(0).getDataSet().toString();

			ngTransformerCtxSvc.getNgctx().datafileDFmap.putAll(this.datafileDFmap);
			ngTransformerCtxSvc.getNgctx().runningPipeLine = RUNNING_MODE;
			ngTransformerCtxSvc.getNgctx().persistMode = persistFlag;
            
			//ngTransformerCtxSvc.getNgctx().dataSetName = transInKey;
			//ngTransformerCtxSvc.getNgctx().datafileDFmap.put(transInKey, datafileDFmap.get(dataSetName));
			//logger.debug("dataset count in transformer ::" + datafileDFmap.get(dataSetName).count());

			component = new NGTransformerComponent(ngTransformerCtxSvc.getNgctx());
            if (component.initTransformerComponent(null)) {
                rc = component.run();
                if (rc == 0) {
                    datafileDFmap.putAll(ngTransformerCtxSvc.getNgctx().datafileDFmap);
                    //datafileDFmap = new HashMap<>();
                    //.put(transOutKey,
                    //		ngTransformerCtxSvc.getNgctx().datafileDFmap.get(ngTransformerCtxSvc.getNgctx().dataSetName));
                    //dataSetName = transOutKey;

                    logger.debug("End Of Transformer Component ==>  dataSetName  & size " + datafileDFmap.keySet() + ","
                        + datafileDFmap.size() + "\n");
                }
            }
        }catch (Exception ex) {
            logger.error("RTPSPipelineProcessor:processTransformer() Exception is : ",ex);
            exception = ex;
        }
        rc = AbstractComponent.handleErrors(component, rc, exception);
        return rc;
	}

	public int processSQL(Map<String, Object> parameters, String configPath, boolean persistFlag) {
        NGSQLComponent component = null;
        int rc= 0;
        Exception exception = null;
		try {

			String configAsStr = ConfigLoader.loadConfiguration(configPath);

			if (configAsStr == null || configAsStr.isEmpty()) {
				throw new XDFException(XDFReturnCode.INCORRECT_OR_ABSENT_PARAMETER, "configuration file name");
			}

			String appId = (String) parameters.get(CliHandler.OPTIONS.APP_ID.name());
			if (appId == null || appId.isEmpty()) {
				throw new XDFException(XDFReturnCode.INCORRECT_OR_ABSENT_PARAMETER, "Project/application name");
			}

			String batchId = (String) parameters.get(CliHandler.OPTIONS.BATCH_ID.name());
			if (batchId == null || batchId.isEmpty()) {
				throw new XDFException(XDFReturnCode.INCORRECT_OR_ABSENT_PARAMETER, "batch id/session id");
			}

			String xdfDataRootSys = System.getProperty(MetadataBase.XDF_DATA_ROOT);
			if (xdfDataRootSys == null || xdfDataRootSys.isEmpty()) {
				throw new XDFException(XDFReturnCode.INCORRECT_OR_ABSENT_PARAMETER, "XDF Data root");
			}

			ComponentServices[] sqlcs = {
					// ComponentServices.InputDSMetadata,
					ComponentServices.OutputDSMetadata, ComponentServices.Project,
					ComponentServices.TransformationMetadata, ComponentServices.Spark };

			logger.debug("Starting SQL component  dataSetName :" + dataSetName + "\n");

			ComponentConfiguration config = NGContextServices.analyzeAndValidateSqlConf(configAsStr);

			NGContextServices ngSQLCtxSvc = new NGContextServices(sqlcs, xdfDataRootSys, config, appId, "sql", batchId);
			ngSQLCtxSvc.initContext(); // debug
			ngSQLCtxSvc.registerOutputDataSet();

			logger.trace("Output datasets:   ");

			ngSQLCtxSvc.getNgctx().registeredOutputDSIds.forEach(id -> logger.trace(id));

			String sqlInKey = dataSetName;
			int sqlOutputSize = config.getOutputs().size();

			logger.debug("SQL component sqlOutputSize  :" + sqlOutputSize + "\n");

			String sqlOutKey = config.getOutputs().get(sqlOutputSize - 1).getDataSet().toString();

			//ngSQLCtxSvc.getNgctx().datafileDFmap = new HashMap<>();
			ngSQLCtxSvc.getNgctx().dataSetName = sqlInKey; // TRANS_out
			ngSQLCtxSvc.getNgctx().datafileDFmap.putAll(this.datafileDFmap);
			//ngSQLCtxSvc.getNgctx().datafileDFmap.put(sqlInKey, datafileDFmap.get(dataSetName)); // TRANS_OUT
			ngSQLCtxSvc.getNgctx().runningPipeLine = RUNNING_MODE;
			ngSQLCtxSvc.getNgctx().persistMode = persistFlag;
			ngSQLCtxSvc.getNgctx().pipeComponentName = "sql";

			component = new NGSQLComponent(ngSQLCtxSvc.getNgctx());
            if (component.initComponent(null)) {
                rc = component.run();
                if (rc == 0) {
                    //datafileDFmap.put(sqlOutKey, ngSQLCtxSvc.getNgctx().datafileDFmap.get(ngSQLCtxSvc.getNgctx().dataSetName));
                    dataSetName = sqlOutKey;
                    datafileDFmap.putAll(ngSQLCtxSvc.getNgctx().datafileDFmap);
                    logger.debug(
                        "End Of SQL Component ==>  dataSetName  & size " + datafileDFmap.keySet() + "," + datafileDFmap.size() + "\n");
                }
            }
        }catch (Exception ex) {
            logger.error("RTPSPipelineProcessor:processSQL() Exception is : ",ex);
            exception = ex;
        }
        rc = AbstractComponent.handleErrors(component, rc, exception);
        return rc;
	}

	public int processESLoader(Map<String, Object> parameters, String configPath, boolean persistFlag) {
        NGESLoaderComponent component = null;
        int rc= 0;
        Exception exception = null;
		try {
			String configAsStr = ConfigLoader.loadConfiguration(configPath);

			if (configAsStr == null || configAsStr.isEmpty()) {
				throw new XDFException(XDFReturnCode.INCORRECT_OR_ABSENT_PARAMETER, "configuration file name");
			}

			String appId = (String) parameters.get(CliHandler.OPTIONS.APP_ID.name());
			if (appId == null || appId.isEmpty()) {
				throw new XDFException(XDFReturnCode.INCORRECT_OR_ABSENT_PARAMETER, "Project/application name");
			}

			String batchId = (String) parameters.get(CliHandler.OPTIONS.BATCH_ID.name());
			if (batchId == null || batchId.isEmpty()) {
				throw new XDFException(XDFReturnCode.INCORRECT_OR_ABSENT_PARAMETER, "batch id/session id");
			}

			String xdfDataRootSys = System.getProperty(MetadataBase.XDF_DATA_ROOT);
			if (xdfDataRootSys == null || xdfDataRootSys.isEmpty()) {
				throw new XDFException(XDFReturnCode.INCORRECT_OR_ABSENT_PARAMETER, "XDF Data root");
			}

			ComponentServices[] escs = { ComponentServices.Project, ComponentServices.TransformationMetadata,
					ComponentServices.Spark };

			logger.debug("Starting ESLoader component :" + "\n");

			ComponentConfiguration config = NGContextServices.analyzeAndValidateEsLoaderConf(configAsStr);
			NGContextServices ngESCtxSvc = new NGContextServices(escs, xdfDataRootSys, config, appId, "esloader",
					batchId);
			ngESCtxSvc.initContext(); // debug

			logger.trace("Output datasets:   ");

			ngESCtxSvc.getNgctx().registeredOutputDSIds.forEach(id -> logger.debug(id));
			//ngESCtxSvc.registerOutputDataSet();

			String dataSetInKey = config.getInputs().get(0).getDataSet();

			logger.debug("ES loader Dataset Name is    " + dataSetInKey);
			ngESCtxSvc.getNgctx().datafileDFmap.putAll(datafileDFmap);
			ngESCtxSvc.getNgctx().dataSetName = dataSetInKey;
			//ngESCtxSvc.getNgctx().datafileDFmap.put(dataSetInKey, datafileDFmap.get(dataSetName));
			ngESCtxSvc.getNgctx().runningPipeLine = RUNNING_MODE;

            component = new NGESLoaderComponent(ngESCtxSvc.getNgctx());
            if (component.initComponent(null)) {
                rc = component.run();
                if (rc == 0) {
                    logger.debug("End Of ESLoader Component ==>  dataSetName  & size " + ngESCtxSvc.getNgctx().dataSetName + ","
                        + ngESCtxSvc.getNgctx().datafileDFmap.size() + "\n");
                }
            }
        }catch (Exception ex) {
            logger.error("RTPSPipelineProcessor:processESLoader() Exception is : ",ex);
            exception = ex;
        }
        rc = AbstractComponent.handleErrors(component, rc, exception);
        return rc;
	}
}
