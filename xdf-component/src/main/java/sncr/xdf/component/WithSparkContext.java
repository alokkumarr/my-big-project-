package sncr.xdf.component;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;
import sncr.xdf.context.Context;
import sncr.xdf.core.spark.SparkOps;

import java.util.List;

/**
 * Created by srya0001 on 9/15/2017.
 */
public interface WithSparkContext {

    default void initSpark(Context ctx) {
        WithSparkContextAux.logger.debug("Configure spark context: " + ctx.componentName);
        ctx.sparkConf = new SparkConf().setAppName(ctx.componentName + "::" + ctx.applicationID + "::" + ctx.batchID );

        /// Overwrite configuration with parameters from component
        List<sncr.xdf.conf.Parameter> componentSysParams = ctx.componentConfiguration.getParameters();

        //TODO: We must have default parameters
        if(componentSysParams != null && componentSysParams.size() > 0) {
            SparkOps.setSparkConfig(ctx.sparkConf, componentSysParams);
        }
        //ctx = new JavaSparkContext(sparkConf);
        ctx.sparkSession = SparkSession.builder().config(ctx.sparkConf).getOrCreate();
    }

    class WithSparkContextAux {
        private static final Logger logger = Logger.getLogger(WithSparkContext.class);
    }


}
