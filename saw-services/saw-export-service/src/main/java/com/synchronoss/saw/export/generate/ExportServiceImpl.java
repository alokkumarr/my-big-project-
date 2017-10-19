package com.synchronoss.saw.export.generate;

import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;

import com.synchronoss.saw.export.exceptions.JSONValidationSAWException;
import com.synchronoss.saw.export.generate.interfaces.ExportService;
import com.synchronoss.saw.export.model.DataResponse;

public class ExportServiceImpl implements ExportService{

  @Value("${analysis.service.host}")
  private String apiExportOtherProperties;

  @Override
  public DataResponse dataToBeExported(String executionId) throws JSONValidationSAWException {
    
    return null;
  }

  @Override
  public Stream<DataResponse> dataToBeExportedStream(String executionId)
      throws JSONValidationSAWException {
    // TODO Auto-generated method stub
    return null;
  }

}
