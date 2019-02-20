package com.synchronoss.saw.storage.proxy.service;

import com.google.gson.JsonElement;
import com.synchronoss.saw.storage.proxy.model.response.ProductModuleDocs;
import com.synchronoss.saw.storage.proxy.model.response.Valid;

import java.util.Map;

public interface ProductSpecificModuleService {
    public Valid addDocument(String tableName,String id, JsonElement doc);
    public Valid updateDocument(String tableName,String id, JsonElement doc);
    public Valid deleteDocument(String tableName,String id);
    public ProductModuleDocs getDocument(String tableName, String id);
    public ProductModuleDocs getAllDocs(String tableName, Map<String, String> keyValues);
    public ProductModuleDocs getAllDocs(String tableName);
}
