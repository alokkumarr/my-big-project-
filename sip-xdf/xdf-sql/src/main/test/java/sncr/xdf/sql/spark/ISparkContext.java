package sncr.xdf.sql.spark;

import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;

/**
 * @author alok.kumarr
 * @since 3.6.0
 */
public interface ISparkContext {
    JavaSparkContext getJavaSparkContext();
    SparkSession getSparkSession();
    SparkContext getSparkContext();
}
