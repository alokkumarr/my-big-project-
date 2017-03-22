package com.synchronoss.saw.composite.exceptions;



@SuppressWarnings("serial")
public class TokenValidationSAWException extends CompositeRuntimeSAWException {

    public TokenValidationSAWException(String msg) {
        super(msg);
}

/**
 * Create a new BeansException with the specified message
 * and root cause.
 * @param msg the detail message
 * @param cause the root cause
 */
public TokenValidationSAWException(String msg, Throwable cause) {
        super(msg, cause);
}


@Override
public boolean equals(Object other) {
        if (this == other) {
                return true;
        }
        if (!(other instanceof TokenValidationSAWException)) {
                return false;
        }
        TokenValidationSAWException otherBe = (TokenValidationSAWException) other;
        return (getMessage().equals(otherBe.getMessage()) &&
        		ExceptionSAWUtils.nullSafeEquals(getCause(), otherBe.getCause()));
}

@Override
public int hashCode() {
        return getMessage().hashCode();
}

	
}
