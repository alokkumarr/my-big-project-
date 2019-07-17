package com.synchronoss.saw.storage.proxy.service;

import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.model.DataSecurityKey;
import com.synchronoss.saw.model.SipQuery;
import com.synchronoss.saw.storage.proxy.model.ExecuteAnalysisResponse;
import com.synchronoss.saw.storage.proxy.model.ExecutionResponse;
import com.synchronoss.saw.storage.proxy.model.ExecutionResult;
import com.synchronoss.saw.storage.proxy.model.ExecutionType;
import com.synchronoss.saw.storage.proxy.model.StorageProxy;
import java.util.List;

public interface StorageProxyService {

  public StorageProxy execute(StorageProxy proxy) throws Exception;

  List<Object> execute(SipQuery sipQuery, Integer size, DataSecurityKey dataSecurityKey,
      ExecutionType executionType, String analysisType, Boolean designerEdit) throws Exception;

    Boolean saveDslExecutionResult(ExecutionResult executionResult);

    List<?> fetchDslExecutionsList(String dslQueryId);

    ExecutionResponse fetchExecutionsData(String executionId);

    ExecutionResponse fetchLastExecutionsData(String dslQueryId);
    ExecuteAnalysisResponse executeAnalysis(Analysis analysis, Integer size, DataSecurityKey dataSecurityKey,
        ExecutionType executionType) throws Exception;
}
