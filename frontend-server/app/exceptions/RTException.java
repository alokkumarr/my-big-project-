package exceptions;
import org.slf4j.LoggerFactory;

/**
 * Created by srya0001 on 4/22/2016.
 */
public class RTException extends RuntimeException {


    private static ch.qos.logback.classic.Logger m_log =
            (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(RTException.class.getName());


    private final String prefix = "RTFE-RT-";
    private final String msg;


    public RTException(ErrorCodes ec)
    {
        msg = prefix + ec.name() + " - " + ErrorCodes.getDescription(ec);
        m_log.error(msg);
    }

    public RTException(ErrorCodes ec, Object... args)
    {
        msg = String.format(prefix + ec.name() + " - " + ErrorCodes.getDescription(ec) , args);
        m_log.error(msg);
    }

    public RTException(ErrorCodes ec, Throwable t)
    {
        msg = prefix + ec.name() + " - " + ErrorCodes.getDescription(ec) + ", root exception: " +  t.getMessage();
        m_log.error(msg);
    }

    public String getMessage()
    {
        return msg;
    }


}
