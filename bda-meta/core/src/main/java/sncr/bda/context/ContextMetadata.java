package sncr.bda.context;

import org.apache.log4j.Logger;

import java.io.Serializable;
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
public class ContextMetadata implements Serializable {

    static final String DATE_FMT = "yyyyMMdd-HHmmss";
    static final SimpleDateFormat format = new SimpleDateFormat(DATE_FMT);
    protected String finishedTs;
    protected final String applicationID;
    protected final String transformationName = "A_transformation";
    protected String user = "A_user";
    protected String batchID;
    protected String startTs;
    protected String componentName;
    protected String transformationID;
    protected String status;
    protected String ale_id;

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
