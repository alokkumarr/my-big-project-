package com.synchronoss.saw.exceptions;

public class MissingFieldException extends RuntimeException {
    public MissingFieldException(String fieldName) {
        super("Missing field: " + fieldName);
    }
}
