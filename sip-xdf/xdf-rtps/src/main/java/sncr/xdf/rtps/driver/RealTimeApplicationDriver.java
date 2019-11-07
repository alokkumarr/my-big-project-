package sncr.xdf.rtps.driver;

import com.typesafe.config.ConfigFactory;
import info.faljse.SDNotify.SDNotify;
import sncr.bda.conf.Rtps;
import sncr.xdf.context.InternalContext;
import sncr.xdf.context.NGContext;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.function.Function0;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import java.io.File;
import java.util.Optional;
import java.util.Properties;
import sncr.xdf.rtps.common.AppMonitoringInfo;

/**
 * Created by asor0002 on 7/25/2016.
 * Class implementing main initialization sequence for streaming application
 */
public class RealTimeApplicationDriver {
    private static Logger logger = Logger.getLogger(RealTimeApplicationDriver.class);
    protected String appName;

    // Initialize real time application from scratch or restore from checkpoint
    // And starts application
    // Expects parameters being provided in configuration:
    //  - spark.checkpoint.path
    //  - spark.app.name
    //  - spark.app.id
    //  - monitoring.controlfile.path
    //  - monitoring.interval
    //  - monitoring.idle.threshold

    private String mandatorySettings[][] = {
            {"spark.app.name", "Application name is not configured (%s). Please correct configuration file."},
            {"monitoring.controlfile.path", "Path to control file is not configured (%s). Please correct configuration file."},
            {"monitoring.interval", "Monitoring interval is not configured (%s). Please correct configuration file."}
    };

    protected void run(Rtps rtpsProps, Optional<NGContext> ngctx , Optional<InternalContext> ctx) {
        int exit_code;

        // Read configuration file
       
        String configPath = rtpsProps.getConfigFilePath();
        logger.info("Configuration file : " + configPath);
        
        String controlFilePath = null;
        long interval = 0;
        String topic;
        
        JavaStreamingContext jssc = null;
        
        /**
         * No configPath means its new way with json.
         * ConfigPath entry with path in configuration means
         * its older way of loading with typeconfig. 
         */
        if(configPath == null  || "".equals(configPath)) {
        	appName = rtpsProps.getSpark().getAppName();
        	controlFilePath = rtpsProps.getMonitoring().getControlfilePath();
        	interval = Long.valueOf(rtpsProps.getMonitoring().getInterval());
        	topic = rtpsProps.getStreams().getTopic();
        	
        	if(!checkConfiguration(rtpsProps)){
                System.exit(-3);
            }
        	logger.debug("##### About to start Create Context JSON #######");
        	 jssc = createContext(appName, rtpsProps, ngctx, ctx);
        	
        } else {
        	logger.debug("##### Before parsing config file name:: #######"+ configPath);
        	
        	com.typesafe.config.Config appConfig = ConfigFactory.parseFile(new File(configPath));
        	
        	logger.debug("##### After parsing #######");
        	
        	appName = appConfig.getString("spark.app.name");
        	controlFilePath = appConfig.getString("monitoring.controlfile.path");
        	interval = appConfig.getLong("monitoring.interval");
        	topic = appConfig.getString("streams.topic");
        	
        	logger.debug("##### Reading values completed #######");

            // Check configuration parameters
        	if(!checkConfiguration(appConfig)){
                System.exit(-3);
            }
        	
        	// Extract configuration options
            

            // Create Streaming context
        	logger.debug("##### About to start Create Context type safe config #######");
        	jssc = createContext(appName, appConfig,ngctx,  ctx);
         

        }
        

        

        if(jssc != null) {
            // In order to connect to other systems (e.g. monitoring) we have to write
            // process information to dedicated file
            if (AppMonitoringInfo.store(jssc.sparkContext().sc(), appName, controlFilePath, topic) < 0) {
                logger.error("Can't write application monitoring data - exiting application");
                System.exit(-1);
            }
            // Set callbacks for bacth processing
            RealTimeBatchListener batchListener = new RealTimeBatchListener();
            jssc.addStreamingListener(batchListener );
            // Start streaming application
            jssc.start();
            try {
                // Setup monitoring interval
                long timeout = 1000 * 60 * interval;
                // Monitor execution flow
                monitor(jssc, timeout, appName, controlFilePath);
                exit_code = 0;
            } catch (Exception e) {
                logger.error(e.getMessage());
                e.printStackTrace();
                exit_code = -1;
            }
        } else {
            logger.error("Can't create application's context");
            exit_code = -2;
        }
        System.exit(exit_code);
    }

    // This function should define pipeline of Spark stages.
    // Should be redefined in all applications
    protected JavaStreamingContext createContext(String instanceName, com.typesafe.config.Config appConfig, Optional<NGContext> ngctx , Optional<InternalContext> ctx){
        logger.error("createContext() method should be redefined on application level.");
        return null;
    }
    
    
    protected JavaStreamingContext createContext(String instanceName, Rtps rtpsPros, Optional<NGContext> ngctx , Optional<InternalContext> ctx) {
    	 logger.error("createContext() method should be redefined on application level.");
         return null;
    }

    protected void monitor(JavaStreamingContext jssc, long timeout,
                           String instanceName, String controlFilePath) throws Exception {
        logger.info("Staring application loop...");
        logger.info("Control file will be created created: " + controlFilePath);
        notifyStartup();
        while (!jssc.awaitTerminationOrTimeout(timeout)) {
            // Check if shutdown request has been placed
            if(AppMonitoringInfo.checkForShutdown(controlFilePath, instanceName)){
                logger.info(String.format("Found shutdown request for %s at %s - exiting.", instanceName, controlFilePath));
                jssc.stop(true, true);
                System.exit(0);
            }
        }
    }

    private void notifyStartup() {
        logger.info("Notifying service manager about start-up completion");
        SDNotify.sendNotify();
    }

    private boolean checkConfiguration(com.typesafe.config.Config appConfig){
        for(int i = 0; i < mandatorySettings.length; i++){
            if(!appConfig.hasPath(mandatorySettings[i][0])){
                logger.error(String.format(mandatorySettings[i][1], mandatorySettings[i][0]));
                return false;
            }
        }
        return true;
    }
    
    
	private boolean checkConfiguration(Rtps rtpsProps) {

		boolean isMaprfsPath = rtpsProps.getMaprfs().getPath() == null || rtpsProps.getMaprfs().getPath().isEmpty();

		boolean isFieldsModel = rtpsProps.getFields().getModel() == null || rtpsProps.getFields().getModel().isEmpty();

		boolean isStreamsTopic = rtpsProps.getStreams().getTopic() == null
				|| rtpsProps.getStreams().getTopic().isEmpty();

		boolean isSparkBatchInterval = rtpsProps.getSpark().getBatchInterval() == null
				|| rtpsProps.getSpark().getBatchInterval().isEmpty();

		return !(isMaprfsPath || isFieldsModel || isStreamsTopic || isSparkBatchInterval);

	}
}
