package sncr.xdf.sql;


import com.google.gson.JsonElement;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * Created by srya0001 on 8/22/2016.
 */
public class SQLDescriptor {

    public SQLScriptDescriptor.StatementType statementType;

    public String targetTableName;
    public String targetObjectName;
    public int index;

    public String SQL;

    public long startTime;
    public Integer executionTime;
    public Integer writeTime;
    public String result;
    public Integer loadTime;

    public boolean isTemporaryTable;
    public String transactionalLocation;
    public String targetTransactionalLocation;

    public String location;
    public TableDescriptor tableDescriptor;
    public String targetTableMode;
    public String targetTableFormat = "parquet";
    public JsonElement schema;

    public String toString()
    {
        String s=  String.format("{ Statement index: %d, " +
            "Target table: %s, " +
            "target object name: %s, " +
            "is temporary table: %s, " +
            "target table format: %s " +
            "target table mode: %s " +
            "temp location: %s, " +
            "temp target dir: %s, " +
            "SQL: %s",
                index,
                targetTableName,
                targetObjectName,
                (isTemporaryTable)?"yes":"no",
                targetTableFormat,
                targetTableMode,
                transactionalLocation,
                targetTransactionalLocation,
                SQL );

        if (startTime > 0) {

            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHmmss");
            String time = format.format(new Timestamp(startTime*1000));

            String s2 = String.format(" Processing result => Processed table: %s, PhysicalLocation: %s, Load (in sec): %d, Execution (in sec): %d, Write (in sec): %d, Start time: %d (%s)",
                    targetTableName, location, loadTime, executionTime, writeTime, startTime, time);
            return s + s2;
        }
        else
            return s;

    }
}
