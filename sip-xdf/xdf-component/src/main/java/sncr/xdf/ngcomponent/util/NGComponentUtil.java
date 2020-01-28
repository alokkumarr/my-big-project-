package sncr.xdf.ngcomponent.util;

import org.apache.log4j.Logger;
import sncr.xdf.ngcomponent.AbstractComponent;
import sncr.xdf.exceptions.XDFException;
import sncr.xdf.context.XDFReturnCode;
import sncr.xdf.context.XDFReturnCodes;

public class NGComponentUtil {

    private static final Logger logger = Logger.getLogger(AbstractComponent.class);

    public static int handleErrors(AbstractComponent component, int rc, Exception e) {
        try {
            logger.debug("handleErrors() Arg rc :" + rc);
            if(rc != 0 && e == null && !XDFReturnCodes.getMap().containsKey(rc)){
                logger.error("Non XDF Return Code :" + rc);
                e = new XDFException(XDFReturnCode.INTERNAL_ERROR);
            }
            if(e != null) {
                logger.error("Exception Occurred : ", e);
                String description = e.getMessage();
                if (e instanceof XDFException) {
                    rc = ((XDFException)e).getReturnCode().getCode();
                } else {
                    rc = XDFReturnCode.INTERNAL_ERROR.getCode();
                }
                if(component != null){
                    component.getErrors().put(rc, description);
                    try {
                        component.finalize(rc);
                    } catch (Exception ex) {
                        if (ex instanceof XDFException) {
                            rc = ((XDFException)ex).getReturnCode().getCode();
                        } else {
                            rc = XDFReturnCode.INTERNAL_ERROR.getCode();
                        }
                    }
                }
                logger.debug("Error Return Code :" + rc);
            }else{
                rc=0;
            }
        }catch (Exception ex) {
            if (ex instanceof XDFException) {
                rc = ((XDFException)ex).getReturnCode().getCode();
            }else {
                rc = XDFReturnCode.INTERNAL_ERROR.getCode();
            }
        }
        logger.info("Component Return Code :" + rc);
        return rc;
    }
}
