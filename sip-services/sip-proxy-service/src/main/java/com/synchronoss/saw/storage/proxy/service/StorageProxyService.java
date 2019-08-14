package com.synchronoss.saw.storage.proxy.service;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.model.DataSecurityKey;
import com.synchronoss.saw.model.SipQuery;
import com.synchronoss.saw.model.globalfilter.GlobalFilters;
import com.synchronoss.saw.model.kpi.KPIBuilder;
import com.synchronoss.saw.storage.proxy.model.ExecuteAnalysisResponse;
import com.synchronoss.saw.storage.proxy.model.ExecutionResponse;
import com.synchronoss.saw.storage.proxy.model.ExecutionResult;
import com.synchronoss.saw.storage.proxy.model.ExecutionType;
import com.synchronoss.saw.storage.proxy.model.StorageProxy;
import java.io.IOException;
import java.util.List;

public interface StorageProxyService {

  StorageProxy execute(StorageProxy proxy) throws Exception;

  List<Object> execute(SipQuery sipQuery, Integer size, DataSecurityKey dataSecurityKey,
      ExecutionType executionType, String analysisType, Boolean designerEdit) throws Exception;

  Boolean saveDslExecutionResult(ExecutionResult executionResult);

  List<?> fetchDslExecutionsList(String dslQueryId);

  ExecutionResponse fetchExecutionsData(
      String executionId, ExecutionType executionType, Integer page, Integer pageSize);


    ExecuteAnalysisResponse executeAnalysis(Analysis analysis, Integer size, Integer page, Integer pageSize, DataSecurityKey dataSecurityKey,
        ExecutionType executionType) throws Exception;
  ExecutionResponse fetchLastExecutionsData(
      String dslQueryId, ExecutionType executionType, Integer page, Integer pageSize);

  ExecutionResponse fetchDataLakeExecutionData(
      String executionId, Integer pageNo, Integer pageSize, ExecutionType executionType);

    ExecutionResponse fetchLastExecutionsDataForDL(
        String analysisId, Integer pageNo, Integer pageSize);
  Boolean saveTtlExecutionResult(ExecutionResult executionResult);

  Object fetchGlobalFilter(GlobalFilters globalFilters,DataSecurityKey dataSecurityKey )
      throws Exception;

    Object processKpi(KPIBuilder kpiBuilder, DataSecurityKey dataSecurityKey)
        throws Exception;
}
