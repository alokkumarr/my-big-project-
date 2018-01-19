package com.synchronoss.saw.storage.proxy.service;

import com.synchronoss.saw.storage.proxy.model.StorageProxy;

public interface StorageConnectorService {
  
  public Object searchDocuments(String query,StorageProxy proxyDetails) throws Exception;
  public Object validateQuery(String query, StorageProxy proxyDetails)throws Exception;
  public Object updateDocument(String id, StorageProxy proxyDetails)throws Exception;
  public Object deleteDocument(String id, StorageProxy proxyDetails)throws Exception;
  public Object indexDocument(String query, StorageProxy proxyDetails)throws Exception;
  public Object countDocument(String query, StorageProxy proxyDetails)throws Exception;
}
