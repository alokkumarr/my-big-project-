package com.synchronoss.saw.storage.proxy.exceptions;

public class UpdateEntitySAWException extends ExportRuntimeSAWException {

    /**
   * 
   */
  private static final long serialVersionUID = 1L;

    public UpdateEntitySAWException(String msg) {
        super(msg);
}
    

/**
 * Create a new BeansException with the specified message
 * and root cause.
 * @param msg the detail message
 * @param cause the root cause
 */
public UpdateEntitySAWException(String msg, Throwable cause) {
        super(msg, cause);
}


@Override
public boolean equals(Object other) {
        if (this == other) {
                return true;
        }
        if (!(other instanceof UpdateEntitySAWException)) {
                return false;
        }
        UpdateEntitySAWException otherBe = (UpdateEntitySAWException) other;
        return (getMessage().equals(otherBe.getMessage()) &&
        		ExceptionSAWUtils.nullSafeEquals(getCause(), otherBe.getCause()));
}

@Override
public int hashCode() {
        return getMessage().hashCode();
}

	
}
