package sncr.xdf.core.spark;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;

import java.util.List;

/**
 * Spark supporting static functions
 * The class has been created to move static code from Context class
 */
public class SparkOps {

    private static final Logger logger = Logger.getLogger(SparkOps.class);

    public static void setSparkConfig(SparkConf sparkConf,
                                      List<sncr.xdf.conf.Parameter> sysParams) {
        for (sncr.xdf.conf.Parameter param : sysParams) {
            logger.debug("Process parameter: " + param.getName() + " value: " + param.getValue());
            if ((param.getValue() == null || param.getValue().isEmpty())) {
                logger.error("Cannot set parameter: " + param.getName() + " value is Empty or null, skip it");
            }
            else
            {
                if(param.getName().toLowerCase().startsWith("spark")){
                    logger.info("Set parameter: " + param.getName() + " value " + param.getValue());
                    sparkConf.set(param.getName(), param.getValue());
                }
            }
        }
    }

    //public static void setupSparkConfig(SparkConf conf, LocationDescriptor locationDescriptor){
     /*   conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
        if(jsonConf != null) {
            Set<System> system = jsonConf.getSystem();
            if(system != null) {
                for (razorsight.schema.appconfig.System sp : system) {
                    String keyName  = sp.getKey();
                    if(keyName.toLowerCase().startsWith(SPARK_PARM_PREFIX) || keyName.toLowerCase().startsWith(ES_PARAM_PREFIX)){
                        conf.set(keyName, sp.getValue());
                    }
                }
            }
        }*/
   // }


}
