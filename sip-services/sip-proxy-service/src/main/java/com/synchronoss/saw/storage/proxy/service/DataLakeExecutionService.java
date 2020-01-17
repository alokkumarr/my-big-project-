package com.synchronoss.saw.storage.proxy.service;

import com.synchronoss.bda.sip.dsk.SipDskAttribute;
import com.synchronoss.saw.model.SipQuery;
import com.synchronoss.saw.storage.proxy.model.ExecuteAnalysisResponse;
import com.synchronoss.saw.storage.proxy.model.ExecutionType;

public interface DataLakeExecutionService {

  ExecuteAnalysisResponse executeDataLakeReport(
      SipQuery sipQuery,
      Integer size,
      SipDskAttribute dskAttribute,
      ExecutionType executionType,
      Boolean designerEdit,
      String executionId,
      Integer page,
      Integer pageSize,
      SipQuery sipQueryFromSemantic)
      throws Exception;

  ExecuteAnalysisResponse getDataLakeExecutionData(
      String executionId,
      Integer pageNo,
      Integer pageSize,
      ExecutionType executionType,
      String query);

  void cleanDataLakeData();
}
