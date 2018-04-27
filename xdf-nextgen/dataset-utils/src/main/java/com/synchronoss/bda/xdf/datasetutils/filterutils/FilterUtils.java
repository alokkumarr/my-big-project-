package com.synchronoss.bda.xdf.datasetutils.filterutils;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.List;

/**
 * Created by skbm0001 on 14/2/2018.
 */
public class FilterUtils {
    public static Dataset<Row> applyFilter(Filter filter, Dataset<Row> dataset) {
        return filter.filter(dataset);
    }

    public static Dataset<Row> applyFilters(List<Filter> filters, Dataset<Row> dataset) {
        return null;
    }
}
