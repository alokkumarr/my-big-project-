package sncr.datalake;

import com.typesafe.config.Config;
import org.apache.hadoop.fs.FileSystem;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SQLContext;
import sncr.saw.common.config.SAWServiceConfig;

import java.io.IOException;
import java.util.Set;


/**
 * Created by srya0001 on 2/10/2017.
 */
public class DLConfiguration {

    private static DLConfiguration instance = new DLConfiguration();
    public DLConfiguration getInstance(){ return instance; }

    protected static JavaSparkContext jctx;
    protected static SparkContext ctx;

    protected static SparkConf sparkConf;
    protected static SQLContext sqlctx;


    public org.apache.hadoop.conf.Configuration getConfig() {
        return config;
    }
    public SQLContext getSqlContext(){return sqlctx;}
    public SparkContext getSC(){return ctx; }
    public FileSystem getFS() {
        return fs;
    }

    protected static org.apache.hadoop.conf.Configuration config;
    protected Set<System> sysParams;
    protected static FileSystem fs;

    private static final Logger logger = Logger.getLogger(DLConfiguration.class.getName());

    static  {

        Config cfg = SAWServiceConfig.spark_conf();

        logger.debug("Configure MAPR: ");
        config = new org.apache.hadoop.conf.Configuration();
        config.set("fs.defaultFS", "maprfs:///");
        try {
            fs = FileSystem.get(config);
        } catch (IOException e) {
            logger.error("Could not get FS: ", e);
        }
        logger.debug("Configure spark context: ");


        sparkConf = new SparkConf().setAppName("SAW-DataAccess");
        sparkConf.set("spark.master", cfg.getString("master"));
        sparkConf.set("spark.executor.memory", cfg.getString("executor.memory"));
        sparkConf.set("spark.cores.max", cfg.getString("cores.max"));
        sparkConf.set("driver.memory", cfg.getString("driver.memory"));
        jctx = new JavaSparkContext(sparkConf);
        ctx = jctx.sc();
        sqlctx = new SQLContext(ctx);
    }





    protected void finalize() {
        try {
            fs.close();
        } catch (IOException e) {
            logger.error("Could not close file system: ", e);
        }
        ctx.stop();
    }

}

