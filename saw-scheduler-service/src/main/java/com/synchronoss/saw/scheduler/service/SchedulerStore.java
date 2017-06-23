package com.synchronoss.saw.scheduler.service;

import java.time.Instant;

import com.mapr.db.MapRDB;
import com.mapr.db.Table;
import org.ojai.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchedulerStore implements AutoCloseable {
    @Value("${saw-maprdb-table-home}")
    private String tableHome;

    private final static String LAST_EXECUTION_ID = "lastExecutionId";
    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    private Table table;

    private String getTablePath() {
        return tableHome + "/saw-scheduler-execution";
    }

    public SchedulerStore() {
        String path = getTablePath()
        boolean exists = MapRDB.tableExists(path);
        table = !exists ? MapRDB.createTable(path) : MapRDB.getTable(path);
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
