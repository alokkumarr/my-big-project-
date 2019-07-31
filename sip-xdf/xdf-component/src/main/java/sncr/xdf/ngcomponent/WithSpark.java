package sncr.xdf.ngcomponent;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;
import sncr.bda.conf.Parameter;
import sncr.xdf.context.InternalContext;
import sncr.xdf.context.NGContext;
import sncr.xdf.core.spark.SparkOps;
import sncr.xdf.udf.*;
import org.apache.spark.sql.types.DataTypes;
import sncr.xdf.context.Context;

import java.util.List;

public interface WithSpark {

    default void initSpark(SparkSession sparkSession, InternalContext ctx, NGContext ngctx) {
        WithSparkHelper.logger.trace("Configure spark context: " + ngctx.componentName);
        SparkConf sparkConf = new SparkConf().setAppName(ngctx.componentName + "::" + ngctx.applicationID + "::" + ngctx.batchID );

        /// Overwrite configuration with parameters from component
        List<Parameter> componentSysParams = ngctx.componentConfiguration.getParameters();

        //TODO: We must have default parameters
        if(componentSysParams != null && componentSysParams.size() > 0) {
            SparkOps.setSparkConfig(sparkConf, componentSysParams);
        }
        //ctx = new JavaSparkContext(sparkConf);
		if (sparkSession == null) {
			ctx.sparkSession = SparkSession.builder().config(sparkConf).getOrCreate();
		} else {
			ctx.sparkSession = sparkSession;
		}
        registerUdfs(ctx);
    }
    
    default void registerUdfs(InternalContext ctx) {
        ctx.sparkSession.udf().register("NowAsString",
            new NowAsString(), DataTypes.StringType);

        ctx.sparkSession.udf().register("ToTimestampFromNum",
            new ToTimestampFromNum(), DataTypes.TimestampType);

        ctx.sparkSession.udf().register("ToTimestampFromString",
            new ToTimestampFromString(), DataTypes.TimestampType);

        ctx.sparkSession.udf().register("ToDateFromNum",
            new ToDateFromNum(), DataTypes.DateType);

        ctx.sparkSession.udf().register("ToDateFromString",
            new ToDateFromString(), DataTypes.DateType);

        ctx.sparkSession.udf().register("ToTimestampFromNumAsString",
            new ToTimestampFromNumAsString(), DataTypes.StringType);

        ctx.sparkSession.udf().register("ToTimestampFromStringAsString",
            new ToTimestampFromStringAsString(), DataTypes.StringType);

        ctx.sparkSession.udf().register("ToDateFromNumAsString",
            new ToDateFromNumAsString(), DataTypes.StringType);

        ctx.sparkSession.udf().register("ToDateFromStringAsString",
            new ToDateFromStringAsString(), DataTypes.StringType);
    }

    class WithSparkHelper {
        private static final Logger logger = Logger.getLogger(WithSpark.class);
    }

}
