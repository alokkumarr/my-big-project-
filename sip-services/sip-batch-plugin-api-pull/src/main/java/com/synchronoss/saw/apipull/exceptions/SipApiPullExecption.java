package com.synchronoss.saw.apipull.exceptions;

public class SipApiPullExecption extends RuntimeException{

    public SipApiPullExecption() {
    }

    public SipApiPullExecption(String message) {
        super(message);
    }

    public SipApiPullExecption(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SipApiPullExecption)) {
            return false;
        }
        SipApiPullExecption otherBe = (SipApiPullExecption) other;
        return (getMessage().equals(otherBe.getMessage())
            && SipExceptionUtils.nullSafeEquals(getCause(), otherBe.getCause()));
    }

    @Override
    public int hashCode() {
        return getMessage().hashCode();
    }
}
