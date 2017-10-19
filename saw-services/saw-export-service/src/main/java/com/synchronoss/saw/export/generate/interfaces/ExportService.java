package com.synchronoss.saw.export.generate.interfaces;

import java.util.stream.Stream;

import com.synchronoss.saw.export.exceptions.JSONValidationSAWException;
import com.synchronoss.saw.export.model.DataResponse;

public interface ExportService {

  public DataResponse dataToBeExported(String executionId) throws JSONValidationSAWException;
  public Stream<DataResponse> dataToBeExportedStream(String executionId) throws JSONValidationSAWException;
}
