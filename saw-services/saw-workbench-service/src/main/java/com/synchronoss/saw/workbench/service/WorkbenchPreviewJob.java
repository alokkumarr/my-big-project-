package com.synchronoss.saw.workbench.service;

import java.util.Iterator;

import com.cloudera.livy.Job;
import com.cloudera.livy.JobContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.ojai.DocumentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkbenchPreviewJob implements Job<Integer> {
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String location;
    private final int limit;

    public WorkbenchPreviewJob(String id, String location, int limit) {
        this.id = id;
        this.location = location;
        this.limit = limit;
    }

    private static final String PREVIEWS_TABLE = "/previews";

    @Override
    public Integer call(JobContext jobContext) throws Exception {
        Logger log = LoggerFactory.getLogger(getClass().getName());
        log.debug("Start preview job");
        SparkSession session = jobContext.sparkSession();
        Dataset<Row> dataset = session.read().load(location);
        StructField[] fields = dataset.schema().fields();
        Iterator<Row> rows = dataset.limit(limit).toLocalIterator();
        PreviewBuilder preview = new PreviewBuilder(id, "success");
        DocumentBuilder document = preview.getDocumentBuilder();
        document.putNewArray("rows");
        rows.forEachRemaining((Row row) -> {
            document.addNewMap();
            for (int i = 0; i < row.size(); i++) {
                String name = fields[i].name();
                DataType dataType = fields[i].dataType();
                if (dataType.equals(DataTypes.StringType)) {
                    document.put(name, row.getString(i));
                } else if (dataType.equals(DataTypes.IntegerType)) {
                    document.put(name, row.getInt(i));
                } else if (dataType.equals(DataTypes.LongType)) {
                    document.put(name, row.getLong(i));
                } else if (dataType.equals(DataTypes.FloatType)) {
                    document.put(name, row.getFloat(i));
                } else if (dataType.equals(DataTypes.DoubleType)) {
                    document.put(name, row.getDouble(i));
                } else {
                    log.warn("Unhandled Spark data type: {}", dataType);
                    document.put(name, row.get(i).toString());
                }
            }
            document.endMap();
        });
        document.endArray();
        preview.insert();
        return 0;
    }
}
