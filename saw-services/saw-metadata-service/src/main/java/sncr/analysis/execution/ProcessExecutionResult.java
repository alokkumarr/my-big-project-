package sncr.analysis.execution;

public enum ProcessExecutionResult{
    Success,
    InterruptedException,
    ExecutionException,
    Cancelled,
    InProgress,
    Timeout

}
