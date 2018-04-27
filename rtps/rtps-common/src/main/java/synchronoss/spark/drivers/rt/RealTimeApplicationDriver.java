package synchronoss.spark.drivers.rt;

import com.typesafe.config.ConfigFactory;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.function.Function0;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import synchronoss.spark.rt.common.AppMonitoringInfo;
import synchronoss.spark.rt.common.RealTimeBatchListener;
import java.io.File;
import java.util.Properties;

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

    protected void run(String configurationFilePath) {
        int exit_code;

        // Read configuration file
        logger.info("Configuration file : " + configurationFilePath);
        final com.typesafe.config.Config appConfig = ConfigFactory.parseFile(new File(configurationFilePath));

        // Check configuration parameters
        if(!checkConfiguration(appConfig)){
            System.exit(-3);
        }

        // Extract configuration options
        appName = appConfig.getString("spark.app.name");
        String controlFilePath = appConfig.getString("monitoring.controlfile.path");
        long interval = appConfig.getLong("monitoring.interval");

        // Create Streaming context
        JavaStreamingContext jssc = createContext(appName, appConfig);

        if(jssc != null) {
            // In order to connect to other systems (e.g. monitoring) we have to write
            // process information to dedicated file
            if (AppMonitoringInfo.store(jssc.sparkContext().sc(), appName, controlFilePath, appConfig) < 0) {
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
    protected JavaStreamingContext createContext(String instanceName, com.typesafe.config.Config appConfig){
        logger.error("createContext() method should be redefined on application level.");
        return null;
    }

    protected void monitor(JavaStreamingContext jssc, long timeout,
                           String instanceName, String controlFilePath) throws Exception {
        logger.info("Staring application loop...");
        logger.info("Control file will be created created: " + controlFilePath);
        while (!jssc.awaitTerminationOrTimeout(timeout)) {
            // Check if shutdown request has been placed
            if(AppMonitoringInfo.checkForShutdown(controlFilePath, instanceName)){
                logger.info(String.format("Found shutdown request for %s at %s - exiting.", instanceName, controlFilePath));
                jssc.stop(true, true);
                System.exit(0);
            }
        }
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
}
