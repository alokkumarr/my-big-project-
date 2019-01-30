package com.synchronoss.saw.storage.proxy.service;

import com.google.gson.JsonElement;
import com.synchronoss.saw.storage.proxy.model.response.Valid;

public interface ProductSpecificModuleService {
    public Valid addDocument(String tableName,String id, JsonElement doc);
    public Valid updateDocument(String tableName,String id, JsonElement doc);
    public Valid deleteDocument(String tableName,String id);
    public JsonElement getDocument(String tableName,String id);
}
