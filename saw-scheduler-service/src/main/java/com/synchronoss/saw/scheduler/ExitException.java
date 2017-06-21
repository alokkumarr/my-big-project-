package com.synchronoss.saw.scheduler;

import org.springframework.boot.ExitCodeGenerator;

public class ExitException extends RuntimeException
    implements ExitCodeGenerator {
    private static final long serialVersionUID = 1L;
    private int exitCode;

    public ExitException(String message, int exitCode) {
        super(message);
        this.exitCode = exitCode;
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}
