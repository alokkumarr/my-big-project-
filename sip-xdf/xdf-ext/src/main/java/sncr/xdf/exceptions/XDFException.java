package sncr.xdf.exceptions;

import org.apache.log4j.Logger;
import sncr.xdf.context.ReturnCode;

public class XDFException extends RuntimeException {
    protected static final Logger logger = Logger.getLogger(XDFException.class);
    protected String msg;
    protected ReturnCode rc;

    public XDFException(ReturnCode rc) {
        this.rc=rc;
        this.msg = String.format(rc.toString(),"");
        logger.error(this.msg);
    }

    public XDFException(ReturnCode rc, Object... args) {
        this.rc=rc;
        msg = String.format(rc.toString(), args);
        logger.error(this.msg);
    }

    public XDFException(ReturnCode rc, Exception e) {
        this.rc=rc;
        this.msg = String.format(rc.toString(),"")+ ", Embedded exception: " + e.getMessage();
        e.printStackTrace();
        logger.error(this.msg);
    }

    public XDFException(ReturnCode rc, Exception e, Object... args) {
        this.rc=rc;
        this.msg = String.format(rc.toString(), args) + ", Embedded exception: " + e.getMessage();
        e.printStackTrace();
        logger.error(this.msg);
    }

    public String getMessage() { return this.msg; }
    public ReturnCode getReturnCode() { return this.rc; }
}
