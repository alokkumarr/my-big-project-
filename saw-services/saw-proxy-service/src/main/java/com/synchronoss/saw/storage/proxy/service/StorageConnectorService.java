package com.synchronoss.saw.storage.proxy.service;

import com.synchronoss.saw.storage.proxy.model.StorageProxy;

public interface StorageConnectorService {
  
  public Object searchDocuments(String query,StorageProxy proxyDetails) throws Exception;
  public Object deleteDocumentById(String id, StorageProxy proxyDetails)throws Exception;
  public Object createDocument(String query, StorageProxy proxyDetails)throws Exception;
  public Object countDocument(String query, StorageProxy proxyDetails)throws Exception;
}
