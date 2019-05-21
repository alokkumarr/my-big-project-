package com.synchronoss.saw.workbench.exceptions;

public class ReadEntitySAWException extends WorkbenchRuntimeSAWException {

    /**
   * 
   */
  private static final long serialVersionUID = 1L;

    public ReadEntitySAWException(String msg) {
        super(msg);
}
    

/**
 * Create a new BeansException with the specified message
 * and root cause.
 * @param msg the detail message
 * @param cause the root cause
 */
public ReadEntitySAWException(String msg, Throwable cause) {
        super(msg, cause);
}


@Override
public boolean equals(Object other) {
        if (this == other) {
                return true;
        }
        if (!(other instanceof ReadEntitySAWException)) {
                return false;
        }
        ReadEntitySAWException otherBe = (ReadEntitySAWException) other;
        return (getMessage().equals(otherBe.getMessage()) &&
        		ExceptionSAWUtils.nullSafeEquals(getCause(), otherBe.getCause()));
}

@Override
public int hashCode() {
        return getMessage().hashCode();
}

	
}
