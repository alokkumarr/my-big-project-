package sncr.bda.context;

import org.apache.log4j.Logger;
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

    static final String DATE_FMT = "yyyyMMdd-HHmmss";
    static final SimpleDateFormat format = new SimpleDateFormat(DATE_FMT);
    public String finishedTs;
    public final String applicationID;
    public final String user = "A_user";
    public final String transformationName = "A_transformation";
    public String batchID;
    public String startTs;
    public String componentName;
    public String transformationID;
    public String status;
    public String ale_id;

    public void setStartTs()
    {
        this.startTs = new SimpleDateFormat("yyyyMMdd-HHmmss")
                .format(new Timestamp(new java.util.Date().getTime()));
    }

    public ContextMetadata(String componentName,
                            String batchId,
                            String appId){

        this.componentName = componentName;
        this.batchID = batchId;
        this.applicationID = appId;
    }
    public void setFinishTS() {
        finishedTs = format.format(new Timestamp(new java.util.Date().getTime()));
    }
}
