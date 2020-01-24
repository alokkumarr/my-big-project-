package sncr.xdf.context;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class XDFReturnCodes {
    protected static final Logger logger = Logger.getLogger(XDFReturnCodes.class);
    private static Map<Integer, XDFReturnCode> map = null;

    static{
        populateReturnCodesMap();
    }

    private static void populateReturnCodesMap(){
        logger.debug("ReturnCode Map initiation started");
        map = new HashMap<>();
        for(XDFReturnCode rc : XDFReturnCode.values()){
            logger.debug("RC code :"+rc.getCode()+", RC :"+ rc);
            map.put(rc.getCode(), rc);
        }
        logger.debug("ReturnCode Map size :"+ map.size());
    }

    public static Map<Integer, XDFReturnCode> getMap(){
        if(map == null){
            logger.debug("ReturnCode Map is null and re-initiating");
            populateReturnCodesMap();
        }
        return map;
    }
}
