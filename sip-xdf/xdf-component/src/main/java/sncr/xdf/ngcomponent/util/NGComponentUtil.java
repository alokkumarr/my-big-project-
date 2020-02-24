package sncr.xdf.ngcomponent.util;

import org.apache.log4j.Logger;
import sncr.xdf.ngcomponent.AbstractComponent;
import sncr.xdf.exceptions.XDFException;
import sncr.xdf.context.XDFReturnCode;
import sncr.xdf.context.XDFReturnCodes;
import java.util.Optional;

public class NGComponentUtil {

    private static final Logger logger = Logger.getLogger(NGComponentUtil.class);

    public static int handleErrors(Optional<AbstractComponent> optComponent, int rc, Exception e) {
        logger.info("handleErrors() : Return Code: " + rc +", Exception : ", e);
        boolean isErrorHandlingEnabled = isErrorHandlingEnabled(optComponent);
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
                    if (optComponent.isPresent()) {
                        AbstractComponent component = optComponent.get();
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

    public static boolean isErrorHandlingEnabled(Optional<AbstractComponent> optComponent){
        if(optComponent.isPresent() && optComponent.get().getNgctx() != null
            && optComponent.get().getNgctx().componentConfiguration != null
            && optComponent.get().getNgctx().componentConfiguration.isErrorHandlingEnabled()){
            return true;
        }
        return false;
    }
}
