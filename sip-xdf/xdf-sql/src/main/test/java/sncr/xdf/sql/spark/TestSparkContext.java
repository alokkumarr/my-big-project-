package sncr.xdf.sql.spark;

import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;

/**
 * @author alok.kumarr
 * @since 3.6.0
 */
public class TestSparkContext implements ISparkContext {
    private SparkSession sparkSession;

    public TestSparkContext() {
        sparkSession = SparkSession.builder().master("local[*]").getOrCreate();
    }

    @Override
    public JavaSparkContext getJavaSparkContext() {
        return new JavaSparkContext(sparkSession.sparkContext());
    }

    @Override
    public SparkSession getSparkSession() {
        return sparkSession;
    }

    @Override
    public SparkContext getSparkContext() {
        return sparkSession.sparkContext();
    }
}
