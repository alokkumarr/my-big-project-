package com.synchronoss.saw.storage.proxy.service;

import com.synchronoss.saw.model.DataSecurityKey;
import com.synchronoss.saw.model.SipQuery;
import com.synchronoss.saw.storage.proxy.model.ExecutionResponse;
import com.synchronoss.saw.storage.proxy.model.ExecutionResult;
import com.synchronoss.saw.storage.proxy.model.ExecutionType;
import com.synchronoss.saw.storage.proxy.model.StorageProxy;
import java.util.List;

public interface StorageProxyService {

  StorageProxy execute(StorageProxy proxy) throws Exception;

  List<Object> execute(SipQuery sipQuery, Integer size, DataSecurityKey dataSecurityKey)
      throws Exception;

  Boolean saveDslExecutionResult(ExecutionResult executionResult);

  List<?> fetchDslExecutionsList(String dslQueryId);

  ExecutionResponse fetchExecutionsData(
      String executionId, ExecutionType executionType, Integer page, Integer pageSize);

  ExecutionResponse fetchLastExecutionsData(
      String dslQueryId, ExecutionType executionType, Integer page, Integer pageSize);

  Boolean saveTTLExecutionResult(ExecutionResult executionResult);
}
