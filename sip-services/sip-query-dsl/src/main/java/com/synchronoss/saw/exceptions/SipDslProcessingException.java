package com.synchronoss.saw.exceptions;

public class SipDslProcessingException extends SipDslRuntimeException{

        public SipDslProcessingException(String msg) {
            super(msg);
        }

        /**
         * Create a new BeansException with the specified message and root cause.
         *
         * @param msg the detail message
         * @param cause the root cause
         */
        public SipDslProcessingException(String msg, Throwable cause) {
            super(msg, cause);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof SipDslProcessingException)) {
                return false;
            }
            SipDslProcessingException otherBe = (SipDslProcessingException) other;
            return (getMessage().equals(otherBe.getMessage())
                && SipExceptionUtils.nullSafeEquals(getCause(), otherBe.getCause()));
        }

        @Override
        public int hashCode() {
            return getMessage().hashCode();
        }
    }
