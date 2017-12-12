package com.synchronoss.saw.export.generate.interfaces;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;

import com.synchronoss.saw.export.exceptions.JSONValidationSAWException;
import com.synchronoss.saw.export.model.DataResponse;

public interface ExportService {

  public DataResponse dataToBeExportedSync(String executionId, HttpServletRequest request, String analysisId) throws JSONValidationSAWException;
  public ListenableFuture<ResponseEntity<DataResponse>> dataToBeExportedAsync(String executionId, HttpServletRequest request, String analysisId)throws JSONValidationSAWException;
  public void dataToBeDispatchedAsync(String executionId, RequestEntity request, String analysisId)throws JSONValidationSAWException;
}
