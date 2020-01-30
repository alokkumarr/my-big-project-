package sncr.xdf.exceptions;

import org.apache.log4j.Logger;
import sncr.xdf.context.XDFReturnCode;

public class XDFException extends RuntimeException {
    protected static final Logger logger = Logger.getLogger(XDFException.class);
    protected String msg;
    protected XDFReturnCode rc;

    public XDFException(XDFReturnCode rc) {
        this.rc=rc;
        this.msg = String.format(rc.getDescription(),"");
        logger.error(this.msg);
    }

    public XDFException(XDFReturnCode rc, Object... args) {
        this.rc=rc;
        msg = String.format(rc.getDescription(), args);
        logger.error(this.msg);
    }

    public XDFException(XDFReturnCode rc, Exception e) {
        this.rc=rc;
        this.msg = String.format(rc.getDescription(),"")+ ", Embedded exception: " + e.getMessage();
        e.printStackTrace();
        logger.error(this.msg);
    }

    public XDFException(XDFReturnCode rc, Exception e, Object... args) {
        this.rc=rc;
        this.msg = String.format(rc.getDescription(), args) + ", Embedded exception: " + e.getMessage();
        e.printStackTrace();
        logger.error(this.msg);
    }

    public String getMessage() { return this.msg; }
    public XDFReturnCode getReturnCode() { return this.rc; }
}
