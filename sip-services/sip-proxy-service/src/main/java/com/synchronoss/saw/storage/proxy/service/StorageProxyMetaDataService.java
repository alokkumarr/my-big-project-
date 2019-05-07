package com.synchronoss.saw.storage.proxy.service;

import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import com.synchronoss.saw.storage.proxy.model.StorageProxy;
import com.synchronoss.saw.storage.proxy.model.StorageProxyNode;

public interface StorageProxyMetaDataService {

  String delimiter = "::";
  String StoragaProxyDataSet = "storagaProxyDataSet";
  String nodeCategoryConvention = "StorageProxyNode";

  public StorageProxy createEntryInMetaData (StorageProxy storageProxy) throws Exception;
  public StorageProxy readEntryFromMetaData (StorageProxy storageProxy) throws Exception;
  public StorageProxy updateEntryFromMetaData (StorageProxy storageProxy) throws Exception;
  public StorageProxy deleteEntryFromMetaData (StorageProxy storageProxy) throws Exception;
  public StorageProxyNode searchEntryFromMetaData (StorageProxy storageProxy) throws Exception;
  default String generateId()  {
    String id = UUID.randomUUID().toString() + delimiter + StoragaProxyDataSet + delimiter
        + System.currentTimeMillis();
    if (StringUtils.containsWhitespace(id)) {
      id = StringUtils.deleteWhitespace(id);
    }
    return id;
  }

}
