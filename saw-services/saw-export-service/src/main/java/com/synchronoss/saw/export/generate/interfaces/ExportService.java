package com.synchronoss.saw.export.generate.interfaces;

import javax.servlet.http.HttpServletRequest;

import com.synchronoss.saw.export.exceptions.JSONValidationSAWException;
import com.synchronoss.saw.export.model.DataResponse;

public interface ExportService {

  public DataResponse dataToBeExported(String executionId, HttpServletRequest request, String analysisId) throws JSONValidationSAWException;
}
