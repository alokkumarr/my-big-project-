package sncr.xdf.context;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class ReturnCodes {
    protected static final Logger logger = Logger.getLogger(ReturnCodes.class);
    private static Map<Integer, ReturnCode> map = null;

    static{
        populateReturnCodesMap();
    }

    private static void populateReturnCodesMap(){
        logger.debug("ReturnCode Map initiation started");
        map = new HashMap<>();
        for(ReturnCode rc : ReturnCode.values()){
            logger.debug("RC code :"+rc.getCode()+", RC :"+ rc);
            map.put(rc.getCode(), rc);
        }
        logger.debug("ReturnCode Map size :"+ map.size());
    }

    public static Map<Integer, ReturnCode> getMap(){
        if(map == null){
            logger.debug("ReturnCode Map is null and re-initiating");
            populateReturnCodesMap();
        }
        return map;
    }
}
