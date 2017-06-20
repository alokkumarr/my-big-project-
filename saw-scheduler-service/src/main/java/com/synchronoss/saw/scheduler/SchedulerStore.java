package com.synchronoss.saw.scheduler;

import com.mapr.db.MapRDB;
import com.mapr.db.Table;
import org.ojai.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class SchedulerStore implements AutoCloseable {
    public static final String TABLE_PATH = "/tmp/saw-scheduler-service";

    private final static String LAST_EXECUTION_ID = "lastExecutionId";
    private final Logger log = LoggerFactory.getLogger(
        SchedulerStore.class.getName());

    private Table table;

    public SchedulerStore() {
        boolean exists = MapRDB.tableExists(TABLE_PATH);
        table = !exists ?
            MapRDB.createTable(TABLE_PATH) : MapRDB.getTable(TABLE_PATH);
    }

    public String getLastExecutionId(String analysisId) {
        Document record = table.findById(analysisId);
        if (record == null) {
            /* If record is missing, the analysis has simply not been
             * executed before */
            return null;
        }
        String lastExecutionId = record.getString(LAST_EXECUTION_ID);
        if (lastExecutionId == null) {
            /* If record exists, the column should be set */
            log.warn("Last execution ID column missing: {}", analysisId);
        }
        return lastExecutionId;
    }

    public void setLastExecutionId(String analysisId, String lastExecutionId) {
        Document document = MapRDB.newDocument()
            .set("_id", analysisId)
            .set(LAST_EXECUTION_ID, lastExecutionId);
        table.insertOrReplace(document);
        table.flush();
    }

    public void close() {
        table.close();
    }
}
