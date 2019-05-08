package com.synchronoss.saw.export.generate.interfaces;

import javax.servlet.http.HttpServletRequest;

import com.synchronoss.saw.export.model.AnalysisMetaData;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;

import com.synchronoss.saw.export.exceptions.JSONValidationSAWException;
import com.synchronoss.saw.export.model.DataResponse;

import java.util.List;

public interface ExportService {

  public DataResponse dataToBeExportedSync(String executionId, HttpServletRequest request, String analysisId) throws JSONValidationSAWException;
  public ListenableFuture<ResponseEntity<DataResponse>> dataToBeExportedAsync(String executionId,
                                                                              HttpServletRequest request, String analysisId, String analysisType, String executionType)throws JSONValidationSAWException;
  public void reportToBeDispatchedAsync(String executionId, RequestEntity request,
      String analysisId, String analysisType)throws JSONValidationSAWException;
  public void pivotToBeDispatchedAsync(String executionId, RequestEntity request, String analysisId)throws JSONValidationSAWException;
  public List<String> listFtpsForCustomer(RequestEntity request);
  public List<String> listS3ForCustomer(RequestEntity requestEntity);

  AnalysisMetaData getAnalysisMetadata(String analysisId);
}
