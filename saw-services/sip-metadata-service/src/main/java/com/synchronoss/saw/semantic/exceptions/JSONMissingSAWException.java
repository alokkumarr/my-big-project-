package com.synchronoss.saw.semantic.exceptions;

public class JSONMissingSAWException extends SemanticRuntimeSAWException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public JSONMissingSAWException(String msg) {
		super(msg);
	}

	/**
	 * Create a new BeansException with the specified message and root cause.
	 *
	 * @param msg
	 *            the detail message
	 * @param cause
	 *            the root cause
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
		return (getMessage().equals(otherBe.getMessage())
				&& ExceptionSAWUtils.nullSafeEquals(getCause(), otherBe.getCause()));
	}

	@Override
	public int hashCode() {
		return getMessage().hashCode();
	}

}
