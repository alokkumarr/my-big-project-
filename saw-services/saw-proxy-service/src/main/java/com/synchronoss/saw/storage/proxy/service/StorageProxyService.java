package com.synchronoss.saw.storage.proxy.service;

import com.synchronoss.saw.model.SIPDSL;
import com.synchronoss.saw.storage.proxy.model.StorageProxy;

import java.util.List;

public interface StorageProxyService {

  public StorageProxy execute(StorageProxy proxy) throws Exception;

  List<Object> execute(SIPDSL sipdsl) throws Exception;

}
