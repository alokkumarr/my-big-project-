package synchronoss.spark.rt.common;

import com.typesafe.config.ConfigValue;
import org.apache.spark.SparkConf;

import java.util.Map;
import java.util.Set;

/**
 * Created by asor0002 on 7/21/2016.
 */
public class ConfigurationHelper {
    public static int initConfig(SparkConf sparkConf, com.typesafe.config.Config appConfig, String prefix, boolean removePrefixFromName){
        int counter = 0;
        Set<Map.Entry<String, ConfigValue>> entrySet = appConfig.entrySet();
        for(Map.Entry<String, ConfigValue> entry : entrySet){
            String name = entry.getKey();
            if(name.startsWith(prefix) ){
                String value = getValue(entry.getValue());
                if(removePrefixFromName){
                    // Remove prefix from name
                    name = name.substring(prefix.length());
                }
                sparkConf.set(name , value);
                counter++;
            }
        }
        return counter;
    }

    public static int initConfig(Map<String, String> map, com.typesafe.config.Config appConfig, String prefix, boolean removePrefixFromName){
        int counter = 0;
        Set<Map.Entry<String, ConfigValue>> entrySet = appConfig.entrySet();
        for(Map.Entry<String, ConfigValue> entry : entrySet){
            String name = entry.getKey();
            if(name.startsWith(prefix)){
                String value = getValue(entry.getValue());
                if(removePrefixFromName){
                    // Remove prefix from name
                    name = name.substring(prefix.length());
                }
                // Sometimes configuration may contain overlapping settings e.g
                //   es.nodes = ...
                //   es.nodes.wan.only = ...
                // or
                //   es {
                //     nodes = ...
                //     nodes.wan.only = ...
                //   }
                // In this case second one will overwrite first one and content of es.nodes will be lost
                // Our solution is to replace '.' with '!' in configuration file
                // and decode '!' to '.' in our implementation
                // So we have to specify following parameters
                // es.nodes = ...
                // es.nodes!wan!only = ...
                map.put(name.replace('!', '.'), value);
            }
        }
        return counter;
    }

    private static String getValue(ConfigValue cfv){
        String value = null;
        switch(cfv.valueType()){
            case STRING: {
                value = (String)cfv.unwrapped();
                break;
            }
            case NUMBER: {
                value = ((Integer) cfv.unwrapped()).toString();
                break;
            }
            case BOOLEAN: {
                value = ((Boolean) cfv.unwrapped()).toString();
                break;
            }
            case NULL:
            default:
                break;
        }
        return value;
    }
}
