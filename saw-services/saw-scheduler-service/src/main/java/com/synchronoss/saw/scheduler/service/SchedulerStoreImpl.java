package com.synchronoss.saw.scheduler.service;

import java.time.Instant;
import javax.annotation.PostConstruct;

import com.mapr.db.MapRDB;
import com.mapr.db.Table;
import org.ojai.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SchedulerStoreImpl implements SchedulerStore {
    @Value("${saw-maprdb-table-home}")
    private String tableHome;

    private final static String LAST_EXECUTED_PERIOD_ID =
        "lastExecutedPeriodId";
    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    private Table table;

    private String getTablePath() {
        return tableHome + "/saw-scheduler-last-executed";
    }

    public SchedulerStoreImpl() {}

    @PostConstruct
    public void init() {
        String path = getTablePath();
        boolean exists = MapRDB.tableExists(path);
        table = !exists ? MapRDB.createTable(path) : MapRDB.getTable(path);
    }

    public String getLastExecutedPeriodId(String analysisId) {
        Document record = table.findById(analysisId);
        if (record == null) {
            /* If record is missing, the analysis has simply not been
             * executed before */
            return null;
        }
        String lastExecutedPeriodId =
            record.getString(LAST_EXECUTED_PERIOD_ID);
        if (lastExecutedPeriodId == null) {
            /* If record exists, the column should be set */
            log.warn("Last execution ID column missing: {}", analysisId);
        }
        return lastExecutedPeriodId;
    }

    public void setLastExecutedPeriodId(
        String analysisId, String lastExecutedPeriodId) {
        Document document = MapRDB.newDocument()
            .set("_id", analysisId)
            .set(LAST_EXECUTED_PERIOD_ID, lastExecutedPeriodId);
        table.insertOrReplace(document);
        table.flush();
    }

    public void close() {
        table.close();
    }
}
