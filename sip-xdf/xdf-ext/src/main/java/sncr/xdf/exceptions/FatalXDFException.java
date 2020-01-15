package sncr.xdf.exceptions;

import sncr.xdf.context.XDFReturnCode;

/**
 * Created by srya0001 on 9/22/2016.
 */
public class FatalXDFException extends XDFException{

    public FatalXDFException(XDFReturnCode rc, int ecode) {
        super(rc);
        System.exit(ecode);
    }

    public FatalXDFException(XDFReturnCode rc, int ecode, Object... args) {
        super(rc, args);
        System.exit(ecode);
    }

    public FatalXDFException(XDFReturnCode rc, int ecode, Exception e, Object... args) {
        super(rc, e, args);
        System.exit(ecode);
    }


}
