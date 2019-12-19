package com.synchronoss.saw.export.generate.interfaces;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;

import com.synchronoss.saw.export.exceptions.JSONValidationSAWException;
import com.synchronoss.saw.export.model.DataResponse;

import java.util.List;

public interface ExportService {
  ResponseEntity<DataResponse> dataToBeExportedAsync(
      String executionId,
      HttpServletRequest request,
      String analysisId,
      String analysisType,
      String executionType)
      throws JSONValidationSAWException;

  void reportToBeDispatchedAsync(
      String executionId, RequestEntity request, String analysisId, String analysisType)
      throws JSONValidationSAWException;

  void pivotDispatchAsync(String executionId, RequestEntity request, String analysisId)
      throws JSONValidationSAWException;
  List<String> listFtpsForCustomer(RequestEntity request);

  List<String> listS3ForCustomer(RequestEntity requestEntity);
}
