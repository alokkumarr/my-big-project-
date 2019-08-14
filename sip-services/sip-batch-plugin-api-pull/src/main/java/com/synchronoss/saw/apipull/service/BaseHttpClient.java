package com.synchronoss.saw.apipull.service;

    import com.fasterxml.jackson.databind.JsonNode;

public interface BaseHttpClient {
    public JsonNode execute() throws Exception;
}
