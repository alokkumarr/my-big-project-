package com.synchronoss.saw.gateway.exceptions;



@SuppressWarnings("serial")
public class SecurityModuleSAWException extends GatewayRuntimeSAWException {

    public SecurityModuleSAWException(String msg) {
        super(msg);
}

/**
 * Create a new BeansException with the specified message
 * and root cause.
 * @param msg the detail message
 * @param cause the root cause
 */
public SecurityModuleSAWException(String msg, Throwable cause) {
        super(msg, cause);
}


@Override
public boolean equals(Object other) {
        if (this == other) {
                return true;
        }
        if (!(other instanceof SecurityModuleSAWException)) {
                return false;
        }
        SecurityModuleSAWException otherBe = (SecurityModuleSAWException) other;
        return (getMessage().equals(otherBe.getMessage()) &&
        		ExceptionSAWUtils.nullSafeEquals(getCause(), otherBe.getCause()));
}

@Override
public int hashCode() {
        return getMessage().hashCode();
}

	
}
