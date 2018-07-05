package sncr.xdf.component;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import sncr.xdf.context.Context;
import sncr.xdf.core.spark.SparkOps;
import sncr.xdf.udf.NowAsString;
import sncr.xdf.udf.ToTimestampFromNum;
import sncr.xdf.udf.ToTimestampFromString;
import sncr.xdf.udf.ToTimestampFromStringAsString;

import java.util.List;

/**
 * Created by srya0001 on 9/15/2017.
 */
public interface WithSparkContext {

    default void initSpark(Context ctx) {
        WithSparkContextAux.logger.debug("Configure spark context: " + ctx.componentName);
        ctx.sparkConf = new SparkConf().setAppName(ctx.componentName + "::" + ctx.applicationID + "::" + ctx.batchID );

        /// Overwrite configuration with parameters from component
        List<sncr.bda.conf.Parameter> componentSysParams = ctx.componentConfiguration.getParameters();

        //TODO: We must have default parameters
        if(componentSysParams != null && componentSysParams.size() > 0) {
            SparkOps.setSparkConfig(ctx.sparkConf, componentSysParams);
        }
        //ctx = new JavaSparkContext(sparkConf);
        ctx.sparkSession = SparkSession.builder().config(ctx.sparkConf).getOrCreate();

        registerUdfs(ctx);
    }

    default void registerUdfs(Context ctx) {
        ctx.sparkSession.udf().register("NowAsString",
            new NowAsString(), DataTypes.StringType);

        ctx.sparkSession.udf().register("ToTimestampFromNum",
            new ToTimestampFromNum(), DataTypes.TimestampType);

        ctx.sparkSession.udf().register("ToTimestampFromString",
            new ToTimestampFromString(), DataTypes.TimestampType);

        ctx.sparkSession.udf().register("ToTimestampFromStringAsString",
            new ToTimestampFromStringAsString(), DataTypes.StringType);


    }

    class WithSparkContextAux {
        private static final Logger logger = Logger.getLogger(WithSparkContext.class);
    }


}
