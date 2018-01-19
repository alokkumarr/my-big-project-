package com.synchronoss.saw.storage.proxy.service;

import com.synchronoss.saw.storage.proxy.model.StorageProxyNode;
import com.synchronoss.saw.storage.proxy.model.StorageProxyResponse;

public interface StorageProxyService {

  public StorageProxyResponse execute(StorageProxyNode proxy) throws Exception;
}
