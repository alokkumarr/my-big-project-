package com.synchronoss.saw.export.generate;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import com.synchronoss.saw.export.exceptions.JSONValidationSAWException;
import com.synchronoss.saw.export.generate.interfaces.ExportService;
import com.synchronoss.saw.export.model.DataResponse;

@Service
public class ExportServiceImpl implements ExportService{

  private static final Logger logger = LoggerFactory.getLogger(ExportServiceImpl.class);
  
  @Value("${analysis.service.host}")
  private String apiExportOtherProperties;
 
  @Value("${analysis.size}")
  private String apiExportSize;
  
  @Override
  public DataResponse dataToBeExportedSync(String executionId, HttpServletRequest request,String analysisId) throws JSONValidationSAWException {
    HttpEntity<?> requestEntity = new HttpEntity<Object>(setRequestHeader(request));
    RestTemplate restTemplate = new RestTemplate();
    String url = apiExportOtherProperties+"/" + executionId +"/executions/"+analysisId+"/data?page=1&pageSize="+apiExportSize+"&analysisType=report";
    logger.info("Transport Service URL: {}", url);
    ResponseEntity<DataResponse> transportResponse = restTemplate.exchange(url, HttpMethod.GET,
            requestEntity, DataResponse.class);
    return transportResponse.getBody();
  }

  private HttpHeaders setRequestHeader(HttpServletRequest request){
    HttpHeaders  requestHeaders = new HttpHeaders();
    requestHeaders.set("Host", request.getHeader("Host"));
    requestHeaders.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    requestHeaders.set("Content-type", MediaType.APPLICATION_JSON_VALUE);
    requestHeaders.set("Authorization", request.getHeader("Authorization"));
    return requestHeaders;  
  }
  
  @Override
  @Async
  public ListenableFuture<ResponseEntity<DataResponse>> dataToBeExportedAsync(String executionId, HttpServletRequest request, String analysisId) {
    String url = apiExportOtherProperties+"/" + executionId +"/executions/"+analysisId+"/data?page=1&pageSize="+apiExportSize+"&analysisType=report";
    HttpEntity<?> requestEntity = new HttpEntity<Object>(setRequestHeader(request));
    AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();
    ListenableFuture<ResponseEntity<DataResponse>> responseStringFuture = asyncRestTemplate.exchange(url, HttpMethod.GET,
        requestEntity, DataResponse.class);
    responseStringFuture.addCallback(new ListenableFutureCallback<ResponseEntity<DataResponse>>() {
      @Override
      public void onSuccess(ResponseEntity<DataResponse> entity) {
        logger.info("[Success] Response string:" + entity);
      }
      @Override
      public void onFailure(Throwable t) {
        logger.error("[Failed] Getting string response:" + t);

      }
    });
    return responseStringFuture;
  }

}
