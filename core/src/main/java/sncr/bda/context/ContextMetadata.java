package sncr.bda.context;

import org.apache.log4j.Logger;
import sncr.bda.conf.ComponentConfiguration;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Created by srya0001 on 9/6/2017.
 *
 * The class provides execution context for processMap component.
 * The contexts includes:
 * - Spark context and configuration
 * - FileSystem context and instance
 * - Component configuration
 * - Aux. values
 * The finalize method is used to close all connections.
 * The context should be used as execution scope of all components
 *
 */
public class ContextMetadata {

    private static final Logger logger = Logger.getLogger(ContextMetadata.class);

    static final String DATE_FMT = "yyyyMMdd-HHmmss";
    static final SimpleDateFormat format = new SimpleDateFormat(DATE_FMT);
    public String finishedTs;
    static final String defaultFsScheme = "hdfs";
    public final String applicationID;
    public final String user = "A_user";
    public final String transformationName = "A_transformation";


    public ComponentConfiguration componentConfiguration;
    public String batchID;
    public String startTs;
    public String componentName;

    {
        this.startTs = new SimpleDateFormat("yyyyMMdd-HHmmss")
                .format(new Timestamp(new java.util.Date().getTime()));
    }

    public ContextMetadata(String componentName,
                            String batchId,
                            String appId,
                            ComponentConfiguration compConf) throws Exception {

        this.componentName = componentName;
        this.batchID = batchId;
        this.applicationID = appId;
        this.componentConfiguration = compConf;
//TODO:: Add project
//        this.componentConfiguration.set
    }
    public void setFinishTS() {
        finishedTs = format.format(new Timestamp(new java.util.Date().getTime()));
    }
}
