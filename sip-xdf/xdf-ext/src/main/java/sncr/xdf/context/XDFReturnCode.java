package sncr.xdf.context;

public enum XDFReturnCode {
    SUCCESS(0,"Success. %s"),
    PARTIAL_SUCCESS(1,"Partial Success. %s"),
    FAILURE(2,"Failure. %s"),
    INTERNAL_ERROR(3,"Internal Server Error. %s"),
    UNKNOWN_ERROR(4, ""),
    INCORRECT_OR_ABSENT_PARAMETER(5,"Parameter: [ %s ] is invalid or absent"),
    EMBEDDED_EXCEPTION(6, " ==> exception root source: %s"),
    DATA_OBJECT_NOT_FOUND(7, "Data object: %s was not found in app. configuration file, configuration is not correct"),
    INPUT_DATA_OBJECT_NOT_FOUND(8, "Data object in input configuration file: %s was not found, further processing is not possible"),
    PARSER_ERROR(9, ""),
    INVALID_DATA_SOURCES(10, "SQL contains non-unique target tables == a table name encounters twice in CREATE TABLE clause, canceling process: %s"),
    PARTITION_CALC_ERROR(11, "Could not calculate partition, Object name: %s, Reason - see Spark exception"),
    CONFIG_ERROR(12, "Configuration is not correct, Info: [ %s ]"),
    INCORRECT_SQL(13, "Incorrect SQL: %s"),
    OUTDIR_CLEAN_ERROR(14, "Could not remove old datafiles: "),
    NO_COMPONENT_DESCRIPTOR(15, "Configuration does not have component descriptor, configuration is not correct: %s"),
    FATAL_RES_FILE_LOC_UNDEF(16, "Result file location is not defined. Please modify configuration file by adding " +
        "application/inputOutput/resultLocation parameter into configuration"),
    FATAL_TEMP_LOC_UNDEF(17, "Temporary file location is not defined. Please modify configuration file by adding "
        +  "application/inputOutput/temporaryLocation parameter into configuration"),
    FATAL_ARCHIVAL_LOC_UNDEF(18, "Archive file location is not defined. Please modify configuration file by adding "
        +  "application/inputOutput/dataArchiveLocation parameter into configuration"),
    FATAL_REJECTED_LOC_UNDEF(19, "Rejected data location is not defined. Please modify configuration file by adding "
        +  "application/inputOutput/rejectedDataOutputLocation parameter into configuration"),
    FATAL_INPUT_LOC_UNDEF(20, "Input file location is not defined. Please modify configuration file by adding "
        +  "application/inputOutput/dataInputLocation parameter into configuration"),
    FILE_NOT_FOUND(21, "Cannot find file: %s "),
    INVALID_CONF_FILE(22, "Invalid configuration file"),
    INCORRECT_CALL(23, "The component was not called correctly: incorrect set of parameters"),
    INCORRECT_SCD_TYPE(24, "The SCD component does not handle such type of SCD modifications: %s "),
    INCORRECT_LOCATIONS(25, "Location either does not exist or not reachable, details: %s"),
    INCORRECT_DATA_STRUCTURE(26, "Read data frame structure is not correct: %s"),
    COULD_NOT_CREATE_TEMP(27, "Could not create temporary location: "),
    COULD_NOT_CLEAN_UP_DIR(28, "Could not clean up directory: %s"),
    SCD_DATA_NOT_FOUND (29, "SCD: Neither SCD data not input data found, please check configuration"),
    HDFS_OPERATION_FAILED(30, "Could not complete HDFS Operation, see exception"),
    UNSUPPORTED_PARTITIONING(31, "Unsupported partitioning [ %s ] found: %s, terminate job"),
    INVALID_DATA(32, "Invalid data: %s"),
    SQL_SCRIPT_PRE_PROC_FAILED(33, "Pre-processing of SQL script has failed, reason: %s"),
    SQL_SCRIPT_NOT_PARSABLE(34,"The SQL script is not parsable: "),
    SCD2_UNSUPPORTED_COMPARABLE_TYPE(35, "Unsupported data type is used in SCD combiner to compare two SCD records. Processing canceled."),
    UNSUPPORTED_DATA_FORMAT(36, "Could not read file in given data format"),
    UNSUPPORTED_SQL_STATEMENT_TYPE(37, "Unsupported SQL Statement"),
    INVALID_CONTROL_FILE_NAME(38, "Control file name must be simple file name. Received %s"),
    DATA_OBJECT_ALREADY_REGISTERED (39, "The data object with such name: %s is already registered."),
    CORRUPTED_DATA_OBJECT_REPOSITORY (40, "File based data object repository contains corrupted files: %s"),
    COULD_NOT_CREATE_DS_META (41, "Could not create data object repo"),
    NON_EXISTING_DATA_ROOT (42,""),
    LAST_UNUSED(43,""),
    MOVE_ERROR(44,"Could not complete move phase! %s"),
    ARCHIVAL_ERROR(45,"Could not complete archive phase! %s"),
    VERIFY_COMPONENT_SERVICES_ERROR(46,"Component %s is not serviceStatus"),
    UPDATE_STATUS_ERROR(47,"Could not update datasets / could not create Audit log entry. %s"),
    INPUT_DATA_EMPTY_ERROR(48,"Input File or Dataset is Empty. Input FilePath/Dataset: %s"),
    OUTPUT_DATA_EMPTY_ERROR(49,"Output Dataset is empty. All records are rejected. Output Dataset: %s");

    private final int code;
    private final String description;
    protected final String prefix = "XDF-";

    XDFReturnCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean equals(XDFReturnCode rc){
        return this.code == rc.code && this.description.equals(rc.description);
    }

    @Override
    public String toString() {
        return prefix + this.name() + " => " + this.code + ":" + this.description;
    }
}
