package sncr.xdf.rtps.common;


import com.typesafe.config.ConfigValue;

import sncr.bda.conf.Rtps;
import org.apache.log4j.Logger;

import org.apache.spark.SparkConf;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 */
public class ConfigurationHelper {
	
	 private static final Logger logger = Logger.getLogger(ConfigurationHelper.class);
	
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

    public static int initConfig2(Map<String, Object> map, com.typesafe.config.Config appConfig, String prefix, boolean removePrefixFromName){
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
    
 public static void initConfigForSpark(SparkConf sparkConf,  Object obj) {
    	
    	for (Field field: obj.getClass().getFields()) {
    		try {
    			sparkConf.set(field.getName(), field.get(field).toString());
			} catch (IllegalArgumentException e) {
				logger.error("Exception during initialization of config:: " + e.getMessage());
			} catch (IllegalAccessException e) {
				logger.error("Exception during initialization of config:: " + e.getMessage());
			}
        }
    	
    	
    }
    
    
    
    public static void initConfig(Map<String, Object> kafkaParams,  Object obj) {
    	
    	for (Field field: obj.getClass().getFields()) {
    		try {
    			
				kafkaParams.put( field.getName().replace('!', '.'), field.get(field).toString());
			} catch (IllegalArgumentException e) {
				logger.error("Exception during initialization of config:: " + e.getMessage());
			} catch (IllegalAccessException e) {
				logger.error("Exception during initialization of config:: " + e.getMessage());
			}
        }
    	
    	
    }
    
 public static void initConfigForEs(Map<String, String> kafkaParams,  Object obj, Optional prefix) {
	 logger.info("starting reflection:: " );
    	for (Field field: obj.getClass().getFields()) {
    		try {
    			String prefixVal = "";
    			if(prefix.isPresent()) {
    				prefixVal = (String) prefix.get();
    			}
				kafkaParams.put(prefixVal + field.getName().replace('!', '.'), field.get(field).toString());
			} catch (IllegalArgumentException e) {
				logger.error("Exception during initialization of config:: " + e.getMessage());
			} catch (IllegalAccessException e) {
				logger.error("Exception during initialization of config:: " + e.getMessage());
			}
        }
   	 logger.info("reflection completed:: " );
    	
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
