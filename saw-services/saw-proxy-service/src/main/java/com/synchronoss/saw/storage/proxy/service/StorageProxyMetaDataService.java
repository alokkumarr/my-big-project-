package com.synchronoss.saw.storage.proxy.service;

import java.util.UUID;
import com.synchronoss.saw.storage.proxy.model.StorageProxy;

public interface StorageProxyMetaDataService {

  String delimiter = "::";
  String StoragaProxyDataSet = "storagaProxyDataSet";
  String nodeCategoryConvention = "StorageProxyNode";

  public StorageProxy createEntryInMetaData (StorageProxy storageProxy) throws Exception;
  public StorageProxy readEntryFromMetaData (StorageProxy storageProxy) throws Exception;
  public StorageProxy updateEntryFromMetaData (StorageProxy storageProxy) throws Exception;
  
  default String generateId()  {
    String id = UUID.randomUUID().toString() + delimiter + StoragaProxyDataSet + delimiter
        + System.currentTimeMillis();
    return id;
  }

}
