package com.synchronoss.saw.storage.proxy.service;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.synchronoss.saw.model.Store;
import com.synchronoss.saw.storage.proxy.model.StorageProxy;
import com.synchronoss.saw.storage.proxy.model.StoreField;
import com.synchronoss.saw.storage.proxy.model.response.ClusterAliasesResponse;
import com.synchronoss.saw.storage.proxy.model.response.ClusterIndexResponse;

public interface StorageConnectorService {
  
  public Object searchDocuments(String query,StorageProxy proxyDetails) throws Exception;
  JsonNode ExecuteESQuery(String query, Store store) throws Exception;
  public Object deleteDocumentById(String id, StorageProxy proxyDetails)throws Exception;
  public Object createDocument(String query, StorageProxy proxyDetails)throws Exception;
  public Object countDocument(String query, StorageProxy proxyDetails)throws Exception;
  public List<ClusterIndexResponse> catClusterIndices(StorageProxy proxyDetails)throws Exception;
  public List<ClusterAliasesResponse> catClusterAliases(StorageProxy proxyDetails)throws Exception;
  public List<StoreField> getMappingbyIndex(StorageProxy proxyDetails)throws Exception;
  public StorageProxy getMappingbyAlias(StorageProxy proxyDetails)throws Exception;
}
