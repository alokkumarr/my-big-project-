package com.synchronoss.saw.storage.proxy.exceptions;

public class JSONProcessingSAWException extends ExportRuntimeSAWException {

    /**
   * 
   */
  private static final long serialVersionUID = 1L;

    public JSONProcessingSAWException(String msg) {
        super(msg);
}
    

/**
 * Create a new BeansException with the specified message
 * and root cause.
 * @param msg the detail message
 * @param cause the root cause
 */
public JSONProcessingSAWException(String msg, Throwable cause) {
        super(msg, cause);
}


@Override
public boolean equals(Object other) {
        if (this == other) {
                return true;
        }
        if (!(other instanceof JSONProcessingSAWException)) {
                return false;
        }
        JSONProcessingSAWException otherBe = (JSONProcessingSAWException) other;
        return (getMessage().equals(otherBe.getMessage()) &&
        		ExceptionSAWUtils.nullSafeEquals(getCause(), otherBe.getCause()));
}

@Override
public int hashCode() {
        return getMessage().hashCode();
}

	
}
