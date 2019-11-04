package com.synchronoss.saw.apipull.service;

    import com.synchronoss.saw.apipull.pojo.SipApiResponse;

public interface BaseHttpClient {
    public SipApiResponse execute() throws Exception;
}
