package sncr.bda.exceptions;

/**
 * Created by srya0001 on 9/22/2016.
 */
public class FatalXDFException extends XDFException{

    public FatalXDFException(ErrorCodes ec, int ecode) {
        super(ec);
        System.exit(ecode);
    }

    public FatalXDFException(ErrorCodes ec, int ecode, Object... args) {
        super(ec, args);
        System.exit(ecode);
    }

    public FatalXDFException(ErrorCodes ec, int ecode, Exception e, Object... args) {
        super(ec, e, args);
        System.exit(ecode);
    }


}
