package sncr.xdf.exceptions;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by srya0001 on 7/27/2016.
 */

public class XDFException extends RuntimeException {

    protected static final Logger m_log = Logger.getLogger(XDFException.class);
    protected final String prefix = "XDF-";
    protected String msg;
    public static final Map<ErrorCodes, String> messages;

    static {
        messages = new HashMap<>();
        messages.put(ErrorCodes.IncorrectOrAbsentParameter, "Parameter: [ %s ] is invalid or absent");

        messages.put(ErrorCodes.DataObjectNotFound, "Data object: %s was not found in app. configuration file, configuration is not correct");
        messages.put(ErrorCodes.InputDataObjectNotFound, "Data object in input configuration file: %s was not found in app. configuration file, further processing is not possible");
        messages.put(ErrorCodes.NoComponentDescriptor, "Configuration does not have component descriptor, configuration is not correct: %s");
        messages.put(ErrorCodes.InvalidDataSources, "SQL contains non-unique target tables == a table name encounters twice in CREATE TABLE clause, canceling process: %s");
        messages.put(ErrorCodes.EmbeddedException, " ==> exception root source: %s");
        messages.put(ErrorCodes.PartitionCalcError, "Could not calculate partition, Object name: %s, Reason - see Spark exception");
        messages.put(ErrorCodes.ConfigError, "Configuration is not correct, Info: [ %s ]");
        messages.put(ErrorCodes.IncorrectSQL, "Incorrect SQL: %s");
        messages.put(ErrorCodes.OutDirCleanError, "Could not remove old datafiles: ");
        messages.put(ErrorCodes.FatalResFileLocUndef, "Result file location is not defined. Please modify configuration file by adding " +
                                                        "application/inputOutput/resultLocation parameter into configuration");
        messages.put(ErrorCodes.FatalTempLocUndef, "Temporary file location is not defined. Please modify configuration file by adding "
                                                     +  "application/inputOutput/temporaryLocation parameter into configuration");
        messages.put(ErrorCodes.FileNotFound, "Cannot find file: ");
        messages.put(ErrorCodes.InvalidConfFile, "Invalid configuration file");
        messages.put(ErrorCodes.IncorrectCall, "The component was not called correctly: incorrect set of parameters");

        messages.put(ErrorCodes.IncorrectSCDType, "The SCD component does not handle such type of SCD modifications: %s ");
        messages.put(ErrorCodes.IncorrectLocations, "Location either does not exist or not reachable, details: %s");
        messages.put(ErrorCodes.IncorrectDataStructure, "Read data frame structure is not correct: %s");
        messages.put(ErrorCodes.CouldNotCreateTemp, "Could not create temporary location: ");
        messages.put(ErrorCodes.CouldNotCleanupDir, "Could not clean up directory: %s");
        messages.put(ErrorCodes.InvalidData, "Invalid data: %s");
        messages.put(ErrorCodes.FatalArchivalLocUndef, "Archive file location is not defined. Please modify configuration file by adding "
                +  "application/inputOutput/dataArchiveLocation parameter into configuration");
        messages.put(ErrorCodes.FatalInputLocUndef, "Input file location is not defined. Please modify configuration file by adding "
                +  "application/inputOutput/dataInputLocation parameter into configuration");
        messages.put(ErrorCodes.FatalRejectedLocUndef, "Rejected data location is not defined. Please modify configuration file by adding "
                +  "application/inputOutput/rejectedDataOutputLocation parameter into configuration");
        messages.put(ErrorCodes.SCD_DataNotFound, "SCD: Neither SCD data not input data found, please check configuration");
        messages.put(ErrorCodes.HDFSOperationFailed, "Could not complete HDFS Operation, see exception");
        messages.put(ErrorCodes.UnsupportedPartitioning, "Unsupported partitioning [ %s ] found: %s, terminate job");
        messages.put(ErrorCodes.SQLScriptNotParsable, "The SQL script is not parsable: ");
        messages.put(ErrorCodes.SQLScriptPreProcFailed, "Pre-processing of SQL script has failed, reason: %s");
        messages.put(ErrorCodes.SCD2UnsupportedComparableType, "Unsupported data type is used in SCD combiner to compare two SCD records. Processing canceled.");
        messages.put(ErrorCodes.UnsupportedDataFormat, "Could not read file in given data format");
        messages.put(ErrorCodes.UnsupportedSQLStatementType, "Unsupported SQL Statement");
        messages.put(ErrorCodes.InvalidControlFileName, "Control file name must be simple file name. Received %s");
        messages.put(ErrorCodes.DataObjectAlreadyRegistered, "The data object with such name: %s is already registered.");
        messages.put(ErrorCodes.CorruptedDataObjectRepository, "File based data object repository contains corrupted files: %s");
        messages.put(ErrorCodes.CouldNotCreateDSMeta, "Could not create data object repo");

    }


    public XDFException(ErrorCodes ec) {
        msg = prefix + ec.name() + " => " + messages.get(ec);
        m_log.error(msg);

    }

    public XDFException(ErrorCodes ec, Object... args) {
        msg = String.format(prefix + ec.name() + " => " + messages.get(ec), args);
        m_log.error( msg );
    }

    public XDFException(ErrorCodes ec, Exception e, Object... args) {
        msg = String.format(prefix + ec.name() + " => Embedded exception details: %s\n" + messages.get(ec), e.getMessage(), args);
        e.printStackTrace();
        m_log.error( msg );
    }

    public String getMessage() { return msg; }


    public enum ErrorCodes {
        UnknownError,
        IncorrectOrAbsentParameter,
        EmbeddedException,
        DataObjectNotFound,
        InputDataObjectNotFound,
        ParserError,
        InvalidDataSources,
        PartitionCalcError,
        ConfigError,
        IncorrectSQL,
        OutDirCleanError,
        NoComponentDescriptor,
        FatalResFileLocUndef,
        FatalTempLocUndef,
        FatalArchivalLocUndef,
        FatalRejectedLocUndef,
        FatalInputLocUndef,
        FileNotFound,
        InvalidConfFile,
        IncorrectCall,
        IncorrectSCDType,
        IncorrectLocations,
        IncorrectDataStructure,
        CouldNotCreateTemp,
        CouldNotCleanupDir,
        SCD_DataNotFound,
        HDFSOperationFailed,
        UnsupportedPartitioning,
        InvalidData,
        SQLScriptPreProcFailed,
        SQLScriptNotParsable,
        SCD2UnsupportedComparableType,
        UnsupportedDataFormat,
        UnsupportedSQLStatementType,
        InvalidControlFileName,
        DataObjectAlreadyRegistered,
        CorruptedDataObjectRepository,
        CouldNotCreateDSMeta,
        NonExistingDataRoot,
        lastUnused
    }

}
