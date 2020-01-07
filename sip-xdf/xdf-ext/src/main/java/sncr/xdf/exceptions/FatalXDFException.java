package sncr.xdf.exceptions;

import sncr.xdf.context.ReturnCode;

/**
 * Created by srya0001 on 9/22/2016.
 */
public class FatalXDFException extends XDFException{

    public FatalXDFException(ReturnCode rc, int ecode) {
        super(rc);
        System.exit(ecode);
    }

    public FatalXDFException(ReturnCode rc, int ecode, Object... args) {
        super(rc, args);
        System.exit(ecode);
    }

    public FatalXDFException(ReturnCode rc, int ecode, Exception e, Object... args) {
        super(rc, e, args);
        System.exit(ecode);
    }


}
