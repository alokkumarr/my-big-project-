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
}
