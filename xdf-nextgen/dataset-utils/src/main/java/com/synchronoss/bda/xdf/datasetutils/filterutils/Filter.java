package com.synchronoss.bda.xdf.datasetutils.filterutils;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

/**
 * Created by skbm0001 on 9/2/2018.
 */

@FunctionalInterface
public interface Filter {
    Dataset<Row> filter(Dataset<Row> originalDataset);
}
