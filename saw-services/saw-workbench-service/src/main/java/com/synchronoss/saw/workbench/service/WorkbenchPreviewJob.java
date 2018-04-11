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
        try {
            log.debug("Start preview job");
            PreviewBuilder preview = new PreviewBuilder(id, "success");
            DocumentBuilder document = preview.getDocumentBuilder();
            document.putNewArray("rows");
            SparkSession session = jobContext.sparkSession();
            Dataset<Row> dataset = getDataset(session, location);
            if (dataset != null) {
                StructField[] fields = dataset.schema().fields();
                Iterator<Row> rows = dataset.limit(limit).toLocalIterator();
                rows.forEachRemaining((Row row) -> {
                    document.addNewMap();
                    for (int i = 0; i < row.size(); i++) {
                        if (row.isNullAt(i)) {
                            continue;
                        }
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
                            log.warn(
                                "Unhandled Spark data type: {}", dataType);
                            document.put(name, row.get(i).toString());
                        }
                    }
                    document.endMap();
                });
            }
            document.endArray();
            preview.insert();
            return 0;
        } catch (Exception e) {
            log.error("Error while creating preview", e);
            /* Workaround: The job executes in Livy, whose logging
             * settings do not show the SAW logging at the moment.  So
             * as a workaround also report on console output, which
             * shows up the Livy logs, until the Livy logging
             * configuration has been fixed.  */
            System.out.println("Error while creating preview: " + e);
            e.printStackTrace();
            return 1;
        }
    }

    private Dataset<Row> getDataset(SparkSession session, String location) {
        Logger log = LoggerFactory.getLogger(getClass().getName());
        try {
            return session.read().load(location);
        } catch (Exception e) {
            /* Handle exception thrown by Spark for example when
             * dataset is empty */
            log.debug("Error while loading dataset, returning no rows");
            return null;
        }
    }
}
