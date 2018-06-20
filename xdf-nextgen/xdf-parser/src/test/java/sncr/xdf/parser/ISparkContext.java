package sncr.xdf.parser;

import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;

/**
 * Created by skbm0001 on 20/2/2018.
 */
public interface ISparkContext {
    JavaSparkContext getJavaSparkContext();
    SparkSession getSparkSession();
    SparkContext getSparkContext();
}
