package sncr.bda;

import com.google.gson.Gson;

import org.apache.log4j.Logger;
import sncr.bda.conf.ComponentConfiguration;
import sncr.bda.core.file.HFileOperations;

import java.io.IOException;

/**
 * Created by asor0002 on 9/8/2017.
 */
public class ConfigLoader {

    private static final Logger logger = Logger.getLogger(ConfigLoader.class);

    public static String loadConfiguration(String srcComponentConfigPath){
        logger.debug("Configuration file: " + srcComponentConfigPath);
        try {
            return HFileOperations.readFile(srcComponentConfigPath);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public static ComponentConfiguration parseConfiguration(String jsonComponentConfig){
        return new Gson().fromJson(jsonComponentConfig, ComponentConfiguration.class);
    }


}
