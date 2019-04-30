package sncr.xdf.parser;

import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;

/**
 * Created by skbm0001 on 20/2/2018.
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
