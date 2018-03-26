package sncr.xdf.ngcomponent;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;
import sncr.bda.conf.Parameter;
import sncr.xdf.context.InternalContext;
import sncr.xdf.context.NGContext;
import sncr.xdf.core.spark.SparkOps;

import java.util.List;

public interface WithSpark {

    default void initSpark(InternalContext ctx) {
        WithSparkHelper.logger.debug("Configure spark context: " + ctx.componentName);
        SparkConf sparkConf = new SparkConf().setAppName(ctx.componentName + "::" + ctx.applicationID + "::" + ctx.batchID );

        /// Overwrite configuration with parameters from component
        List<Parameter> componentSysParams = ctx.componentConfiguration.getParameters();

        //TODO: We must have default parameters
        if(componentSysParams != null && componentSysParams.size() > 0) {
            SparkOps.setSparkConfig(sparkConf, componentSysParams);
        }
        //ctx = new JavaSparkContext(sparkConf);
        ctx.sparkSession = SparkSession.builder().config(sparkConf).getOrCreate();
    }

    default void initSpark(NGContext ngctx, InternalContext ctx) {
        WithSparkHelper.logger.debug("Configure spark context: " + ctx.componentName);
        ctx.sparkSession = ngctx.sparkSession;
        ctx.extSparkCtx = true;
    }

    class WithSparkHelper {
        private static final Logger logger = Logger.getLogger(WithSpark.class);
    }

}
