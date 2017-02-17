package com.razor.raw.core.common.exception;

/**
 * @author creddy
 *
 * This class is used for capturing any data related exceptions
 */
public class RazorsightDataException extends RazorsightRuntimeException {
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param rootCause
     */
    public RazorsightDataException(Throwable rootCause) {
        super(rootCause);
    }

    /**
     * @param messageKey
     * @param rootCause
     */
    public RazorsightDataException(String messageKey,Throwable rootCause) {
        super(messageKey,rootCause);
    }

    /**
     * @param message
     */
    public RazorsightDataException(String message) {
        super(message);
    }
}
