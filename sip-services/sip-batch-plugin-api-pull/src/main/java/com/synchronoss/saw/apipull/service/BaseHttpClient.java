package com.synchronoss.saw.apipull.service;

    import com.synchronoss.saw.apipull.pojo.ApiResponse;

public interface BaseHttpClient {
    public ApiResponse execute() throws Exception;
}
