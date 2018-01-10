package com.synchronoss.saw.storage.proxy.service;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.synchronoss.saw.storage.proxy.model.StorageProxyNode;
import com.synchronoss.saw.storage.proxy.model.StorageProxyResponse;

public interface StorageProxyService {

  public StorageProxyResponse execute(StorageProxyNode proxy) throws JsonProcessingException, IOException, ProcessingException;
}
