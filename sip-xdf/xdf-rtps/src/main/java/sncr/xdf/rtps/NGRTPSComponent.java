package sncr.xdf.rtps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import scala.Tuple2;
import sncr.bda.CliHandler;
import sncr.bda.ConfigLoader;
import sncr.bda.base.MetadataBase;
import sncr.bda.conf.ComponentConfiguration;
import sncr.bda.conf.Rtps;
import sncr.bda.core.file.HFileOperations;
import sncr.xdf.context.ComponentServices;
import sncr.xdf.context.NGContext;
import sncr.xdf.exceptions.XDFException;
import sncr.xdf.ngcomponent.AbstractComponent;
import sncr.xdf.ngcomponent.WithDLBatchWriter;
import sncr.xdf.ngcomponent.WithSpark;
import sncr.xdf.services.NGContextServices;
import sncr.xdf.services.WithDataSet;
import sncr.xdf.services.WithProjectScope;
import sncr.xdf.exceptions.XDFException.ErrorCodes;

public class NGRTPSComponent extends AbstractComponent
		implements WithDLBatchWriter, WithSpark, WithDataSet, WithProjectScope {

	  private static final Logger logger = Logger.getLogger(NGRTPSComponent.class);


	 public NGRTPSComponent(NGContext ngctx) {
	        super(ngctx);
	    }

	@Override
	protected int execute() {
		logger.debug("########rtps execute started#######");
		 SparkSession spark = SparkSession
	                .builder()
	                .appName("SparkSample")
	                .master("local[*]")
	                .getOrCreate();

		List<Tuple2<String,String[]>> inputList = new ArrayList<Tuple2<String,String[]>>();
        inputList.add(new Tuple2<String,String[]>("link91",new String[]{"link620","link761"}));
        inputList.add(new Tuple2<String,String[]>("link297",new String[]{"link999","link942"}));
        Dataset<Row> dataset = spark.createDataset(inputList, Encoders.tuple(Encoders.STRING(),
        		spark.implicits().newStringArrayEncoder())).toDF();
        ngctx.datafileDFmap.put(ngctx.dataSetName,dataset.cache());
		logger.debug("########rtps execute completed#######");
		return 0;
	}


	@Override
	protected int archive() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static void main(String[] args) {
		logger.debug("Inside RTPS main");
		NGContextServices ngCtxSvc;
		CliHandler cli = new CliHandler();
		try {
			long start_time = System.currentTimeMillis();

			HFileOperations.init(10);
			logger.debug("Hadoop file system initialized");

			Map<String, Object> parameters = cli.parse(args);

			logger.debug("Command line arguments parsing completed");

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


			logger.debug("Config validation completed");
			ComponentServices pcs[] = { ComponentServices.OutputDSMetadata, ComponentServices.Project,
					ComponentServices.TransformationMetadata, ComponentServices.Spark, };
			ComponentConfiguration cfg = NGRTPSComponent.analyzeAndValidate(configAsStr);

			logger.debug("Analyze and validation completed");


			ngCtxSvc = new NGContextServices(pcs, xdfDataRootSys, cfg, appId, "rtps", batchId);
			logger.debug("NG Context services initialized");

			ngCtxSvc.initContext();

			logger.debug("NG init Context completed");
			logger.debug("Starting register output dataset");
	        ngCtxSvc.registerOutputDataSet();
			logger.debug("register output dataset completed");
	        ngCtxSvc.getNgctx().registeredOutputDSIds.forEach( id ->
            logger.warn(id)
        );
        logger.warn(ngCtxSvc.getNgctx().toString());
        NGRTPSComponent component = new NGRTPSComponent(ngCtxSvc.getNgctx());
        logger.debug("NGRTPSComponent initialized with NgContext");
        if (!component.initComponent(null))
            System.exit(-1);

        logger.debug("Invoking run() method.....");
        int rc = component.run();
        logger.debug("run() execution completed");
        long end_time = System.currentTimeMillis();
        long difference = end_time-start_time;
        logger.info("Parser total time " + difference );
        logger.debug("Exiting from RTPS");
        System.exit(rc);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

	}

	public static ComponentConfiguration analyzeAndValidate(String config) throws Exception {

		ComponentConfiguration compConf = AbstractComponent.analyzeAndValidate(config);
		Rtps parserProps = compConf.getRtps();
		if (parserProps == null) {
			throw new XDFException(XDFException.ErrorCodes.InvalidConfFile);
		}



		return compConf;
	}

}
