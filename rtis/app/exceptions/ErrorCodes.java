package exceptions;

/**
 * Created by srya0001 on 4/22/2016.
 */
public enum ErrorCodes
{
    NoRegEventSender,
    NoCrashReportSender,
    InvalidAppConf,
    NoEventHandler,
    IllegalAccess,
    ClassNotFound,
    NoGenericHandler,
    NoCharterHandler,
    ConfigurationIsNotCorrect,
    UnsupportedMsgType,
    MandatoryParameterIsNull,
    NoCheckPoint,
    StreamStale;

    public final static String getDescription(ErrorCodes ec)
    {
        switch (ec)
        {
            case NoRegEventSender: return "Could not initialize topic and queue for regular events";
            case NoCrashReportSender: return "Could not initialize topic and queue for crash reports";
            case InvalidAppConf: return "Application configuration section: %s is not correct ";
            case NoEventHandler: return "Could create Event Handler: %s";
            case IllegalAccess: return "Illegal access to Event Handler class %s";
            case ClassNotFound: return "Event handler class not found, check configuration";
            case NoGenericHandler: return "No generic handler were found";
            case NoCharterHandler: return "No Charter SmartCare handler were found";
            case UnsupportedMsgType: return "Event handler does not support this data type: %s";
            case ConfigurationIsNotCorrect: return "Configuration is not correct, entity not found/corrupted: %s";
            case MandatoryParameterIsNull: return "Mandatory parameters are null (absent): %s";
            case NoCheckPoint: return "Checkpoint file [%s] not found/ not reachable. Use default values";
            case StreamStale: return "Stream is stale. Symptoms: %s";

        }
        return "No description";
    }

}
