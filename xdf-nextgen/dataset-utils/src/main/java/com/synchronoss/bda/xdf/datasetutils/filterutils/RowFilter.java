package com.synchronoss.bda.xdf.datasetutils.filterutils;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

/**
 * Created by skbm0001 on 14/2/2018.
 */
public class RowFilter implements Filter {
    private String filterString;

    public RowFilter(String filterString) {
        this.filterString = filterString;
    }

    public Dataset<Row> filter(Dataset<Row> originalDataset) {
        return originalDataset.filter(this.filterString);
    }
}
