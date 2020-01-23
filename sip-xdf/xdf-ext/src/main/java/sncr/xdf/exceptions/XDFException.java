package sncr.xdf.exceptions;

import org.apache.log4j.Logger;
import sncr.xdf.context.XDFReturnCode;

public class XDFException extends RuntimeException {
    protected static final Logger logger = Logger.getLogger(XDFException.class);
    protected String msg;
    protected XDFReturnCode rc;

    public XDFException(XDFReturnCode rc) {
        this.rc=rc;
        this.msg = String.format(rc.toString(),"");
        logger.error(this.msg);
    }

    public XDFException(XDFReturnCode rc, Object... args) {
        this.rc=rc;
        msg = String.format(rc.toString(), args);
        logger.error(this.msg);
    }

    public XDFException(XDFReturnCode rc, Exception e) {
        this.rc=rc;
        this.msg = String.format(rc.toString(),"")+ ", Embedded exception: " + e.getMessage();
        e.printStackTrace();
        logger.error(this.msg);
    }

    public XDFException(XDFReturnCode rc, Exception e, Object... args) {
        this.rc=rc;
        this.msg = String.format(rc.toString(), args) + ", Embedded exception: " + e.getMessage();
        e.printStackTrace();
        logger.error(this.msg);
    }

    public String getMessage() { return this.msg; }
    public XDFReturnCode getReturnCode() { return this.rc; }
}
