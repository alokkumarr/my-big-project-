package com.synchronoss.saw.observe.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.PRECONDITION_FAILED, reason="json body is missing in request body")
public class JSONMissingSAWException extends ExportRuntimeSAWException {

    /**
   * 
   */
  private static final long serialVersionUID = 1L;

    public JSONMissingSAWException(String msg) {
        super(msg);
}
    

/**
 * Create a new BeansException with the specified message
 * and root cause.
 * @param msg the detail message
 * @param cause the root cause
 */
public JSONMissingSAWException(String msg, Throwable cause) {
        super(msg, cause);
}


@Override
public boolean equals(Object other) {
        if (this == other) {
                return true;
        }
        if (!(other instanceof JSONMissingSAWException)) {
                return false;
        }
        JSONMissingSAWException otherBe = (JSONMissingSAWException) other;
        return (getMessage().equals(otherBe.getMessage()) &&
        		ExceptionSAWUtils.nullSafeEquals(getCause(), otherBe.getCause()));
}

@Override
public int hashCode() {
        return getMessage().hashCode();
}

	
}
