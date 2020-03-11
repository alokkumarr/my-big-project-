package sncr.xdf.ngcomponent.util;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.rdd.RDD;
import sncr.xdf.ngcomponent.AbstractComponent;
import sncr.xdf.exceptions.XDFException;
import sncr.xdf.context.XDFReturnCode;
import sncr.xdf.context.XDFReturnCodes;

import org.apache.hadoop.fs.Path;
import java.util.Optional;
import sncr.bda.conf.ComponentConfiguration;

public class NGComponentUtil {

    private static final Logger logger = Logger.getLogger(NGComponentUtil.class);

    public static int handleErrors(Optional<AbstractComponent> optComponent, Optional<ComponentConfiguration> optCfg, int rc, Exception e) {
        logger.info("handleErrors() : Return Code: " + rc +", Exception : ", e);
        AbstractComponent component = null;
        ComponentConfiguration config = null;
        if(optComponent.isPresent() &&  optComponent.get() != null){
            component = optComponent.get();
            config = component.getNgctx().componentConfiguration;
        }else if(optCfg.isPresent() &&  optCfg.get() != null){
            config = optCfg.get();
        }
        boolean isErrorHandlingEnabled = isErrorHandlingEnabled(Optional.ofNullable(config));
        logger.info("isErrorHandlingEnabled : "+ isErrorHandlingEnabled);
        if(isErrorHandlingEnabled) {
            try {
                logger.debug("handleErrors() Arg rc :" + rc);
                if (rc != 0 && e == null && !XDFReturnCodes.getMap().containsKey(rc)) {
                    logger.error("Non XDF Return Code :" + rc);
                    e = new XDFException(XDFReturnCode.INTERNAL_ERROR);
                }
                if (e != null) {
                    String description = e.getMessage();
                    if (e instanceof XDFException) {
                        rc = ((XDFException) e).getReturnCode().getCode();
                    } else {
                        rc = XDFReturnCode.INTERNAL_ERROR.getCode();
                    }
                    if (component != null) {
                        component.getErrors().put(rc, description);
                        try {
                            component.finalize(rc);
                        } catch (Exception ex) {
                            if (ex instanceof XDFException) {
                                rc = ((XDFException) ex).getReturnCode().getCode();
                            } else {
                                rc = XDFReturnCode.INTERNAL_ERROR.getCode();
                            }
                        }
                    }
                    logger.debug("Error Return Code :" + rc);
                } else {
                    rc = 0;
                }
            } catch (Exception ex) {
                if (ex instanceof XDFException) {
                    rc = ((XDFException) ex).getReturnCode().getCode();
                } else {
                    rc = XDFReturnCode.INTERNAL_ERROR.getCode();
                }
            }
            logger.info("Component Return Code :" + rc);
        }else{
            rc = (rc == 0 && e == null) ? 0 : -1;
        }
        return rc;
    }

    public static boolean isErrorHandlingEnabled(Optional<ComponentConfiguration> optCfg){
        if(optCfg.isPresent() && optCfg.get() != null && optCfg.get().isErrorHandlingEnabled()){
            return true;
        }
        return false;
    }

    public static String getLineFromRdd(JavaRDD<String> rdd, int headerSize, int lineNumber){
        String line  = null;
        if(headerSize == 1){
            lineNumber = 1;
        }
        final int index = lineNumber-1;
        if(rdd != null && index >= 0){
            try{
                line = rdd.zipWithIndex().filter(row->row._2==index).map(row->row._1).first();
            } catch (Exception e) {
                logger.error("Exception occurred while getting line from rdd :" + e);
            }
        }
        logger.info("Line :: :" + line);
        return line;
    }
}
