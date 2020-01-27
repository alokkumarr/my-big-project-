package com.synchronoss.saw.storage.proxy.service;

import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.model.SipQuery;
import com.synchronoss.saw.model.globalfilter.GlobalFilters;
import com.synchronoss.saw.model.kpi.KPIBuilder;
import com.synchronoss.saw.storage.proxy.model.ExecuteAnalysisResponse;
import com.synchronoss.saw.storage.proxy.model.ExecutionResponse;
import com.synchronoss.saw.storage.proxy.model.ExecutionResult;
import com.synchronoss.saw.storage.proxy.model.ExecutionType;
import com.synchronoss.saw.storage.proxy.model.StorageProxy;
import java.util.List;

public interface StorageProxyService {

  StorageProxy execute(StorageProxy proxy) throws Exception;

  List<Object> execute(SipQuery sipQuery, Integer size,
      ExecutionType executionType, String analysisType, Boolean designerEdit,
      Ticket authTicket) throws Exception;

  Boolean saveDslExecutionResult(ExecutionResult executionResult);

  List<?> fetchDslExecutionsList(String dslQueryId,Ticket authTicket, boolean isScheduled);

  ExecutionResponse fetchExecutionsData(
      String executionId, ExecutionType executionType, Integer page, Integer pageSize,
      Ticket authTicket, boolean isScheduled);


    ExecuteAnalysisResponse executeAnalysis(Analysis analysis, Integer size, Integer page,
        Integer pageSize, ExecutionType executionType, String userId,
        Ticket authTicket, String queryId, boolean isScheduledExecution) throws Exception;

  ExecutionResponse fetchLastExecutionsData(
      String dslQueryId, ExecutionType executionType, Integer page, Integer pageSize,
      Ticket authTicket, boolean isScheduled);

  ExecutionResponse fetchDataLakeExecutionData(
      String executionId, Integer pageNo, Integer pageSize, ExecutionType executionType,
      Ticket authTicket, boolean isScheduled);

    ExecutionResponse fetchLastExecutionsDataForDL(
        String analysisId, Integer pageNo, Integer pageSize, Ticket authTicket,
        boolean isScheduled);
  Boolean saveTtlExecutionResult(ExecutionResult executionResult);

  Object fetchGlobalFilter(GlobalFilters globalFilters, Ticket authTicket) throws Exception;

    Object processKpi(KPIBuilder kpiBuilder, Ticket authTicket)
        throws Exception;

  List<Object> pagingData(Integer page, Integer pageSize, List<Object> dataObj);

  Boolean updateAnalysis(Analysis analysis);
}
