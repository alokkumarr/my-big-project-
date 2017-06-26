package synchronoss.spark.rt.common;

import org.apache.log4j.Logger;
import org.apache.spark.streaming.scheduler.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alexey.sorokin on 6/3/2016.
 *
 */
public class RealTimeBatchListener implements StreamingListener {
    private static final Logger logger = Logger.getLogger(RealTimeBatchListener.class);
    private int numberOfEmptyBatches;
    private int idleThreshold;
    private boolean idle = false;

    public RealTimeBatchListener(int idleThreshold) {
        this.idleThreshold = idleThreshold;
        numberOfEmptyBatches = 0;
    }

    public boolean isIdleApplication(){
        return idle;
    }

    @Override
    public void onBatchCompleted(StreamingListenerBatchCompleted batchCompleted) {
        BatchInfo batch = batchCompleted.batchInfo();
        if(batch.numRecords() > 0) {
            idle = false;
            numberOfEmptyBatches = 0;
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");
            logger.info("Batch Time: " + formatter.format(new Date(batch.batchTime().milliseconds()))
                    + ", Number of events processed: " + batch.numRecords());

        } else {
            // Count number of empty batches to set idle status
            numberOfEmptyBatches++;
            if(numberOfEmptyBatches >= idleThreshold) {
                idle = true;
            }
            if(numberOfEmptyBatches == idleThreshold ){
                logger.info("Application is idle.");
            }
        }
    }

    @Override
    public void onBatchStarted(StreamingListenerBatchStarted batchStarted) {
    }

    @Override
    public void onBatchSubmitted(StreamingListenerBatchSubmitted batchSubmitted) {
    }

    @Override
    public void onReceiverError(StreamingListenerReceiverError receiverError) {
        logger.error(receiverError.receiverInfo().lastErrorMessage());
        logger.error(receiverError.receiverInfo().lastError());
    }

    @Override
    public void onReceiverStarted(StreamingListenerReceiverStarted receiverStarted) {
    }

    @Override
    public void onReceiverStopped(StreamingListenerReceiverStopped receiverStopped) {
    }

    @Override
    public void onOutputOperationCompleted(StreamingListenerOutputOperationCompleted operationCompleted){
    }

    @Override
    public void onOutputOperationStarted(StreamingListenerOutputOperationStarted operationStarted){
    }
}
