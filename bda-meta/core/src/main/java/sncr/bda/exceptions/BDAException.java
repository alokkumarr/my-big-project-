package sncr.bda.exceptions;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by srya0001 on 7/27/2016.
 */

public class BDAException extends RuntimeException {

    protected static final Logger m_log = Logger.getLogger(BDAException.class);
    protected final String prefix = "XDF-";
    protected String msg;
    public static final Map<ErrorCodes, String> messages;

    static {
        messages = new HashMap<>();
        messages.put(ErrorCodes.IncorrectCall, "The component was not called correctly: incorrect set of parameters");
        messages.put(ErrorCodes.CouldNotCreateDSMeta, "Could not create data object repo");
    }


    public BDAException(ErrorCodes ec) {
        msg = prefix + ec.name() + " => " + messages.get(ec);
        m_log.error(msg);

    }

    public BDAException(ErrorCodes ec, Object... args) {
        msg = String.format(prefix + ec.name() + " => " + messages.get(ec), args);
        m_log.error( msg );
    }

    public BDAException(ErrorCodes ec, Exception e, Object... args) {
        msg = String.format(prefix + ec.name() + " => Embedded exception details: %s\n" + messages.get(ec), e.getMessage(), args);
        e.printStackTrace();
        m_log.error( msg );
    }

    public String getMessage() { return msg; }


    public enum ErrorCodes {
        IncorrectCall,
        CouldNotCreateDSMeta,
        lastUnused
    }

}
