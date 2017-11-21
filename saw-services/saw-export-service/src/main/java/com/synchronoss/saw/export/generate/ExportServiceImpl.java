package com.synchronoss.saw.export.generate;

import javax.servlet.http.HttpServletRequest;

import com.synchronoss.saw.export.ServiceUtils;
import com.synchronoss.saw.export.distribution.MailSenderUtil;
import com.synchronoss.saw.export.generate.interfaces.IFileExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import com.synchronoss.saw.export.exceptions.JSONValidationSAWException;
import com.synchronoss.saw.export.generate.interfaces.ExportService;
import com.synchronoss.saw.export.model.DataResponse;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

@Service
public class ExportServiceImpl implements ExportService{

  private static final Logger logger = LoggerFactory.getLogger(ExportServiceImpl.class);
  
  @Value("${analysis.service.host}")
  private String apiExportOtherProperties;

  @Value("${analysis.service.transportHost}")
  private String dispatchHostproperties;
 
  @Value("${analysis.size}")
  private String apiExportSize;

  @Value("${published.path}")
  private String publishedPath;

  @Value("${spring.mail.body}")
  private String mailBody;

  @Autowired
  private ApplicationContext appContext;

  @Autowired
  private ServiceUtils serviceUtils;

  @Override
  public DataResponse dataToBeExportedSync(String executionId, HttpServletRequest request,String analysisId) throws JSONValidationSAWException {
    HttpEntity<?> requestEntity = new HttpEntity<Object>(setRequestHeader(request));
    RestTemplate restTemplate = new RestTemplate();
    String url = apiExportOtherProperties+"/" + executionId +"/executions/"+analysisId+"/data?page=1&pageSize="+apiExportSize+"&analysisType=report";
    logger.debug("Transport Service URL: {}", url);
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
        logger.debug("[Success] Response string:" + entity);

      }
      @Override
      public void onFailure(Throwable t) {
        logger.error("[Failed] Getting string response:" + t);

      }
    });
    return responseStringFuture;
  }

  @Override
  @Async
  public void dataToBeDispatchedAsync(String executionId, RequestEntity request, String analysisId) {
    String url = dispatchHostproperties+"/" + analysisId +"/executions/"+executionId+"/data?page=1&pageSize="
            +apiExportSize+"&analysisType=report";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
   // headers.set("Authorization", "Bearer "+ServiceUtils.getDefaultJwtToken());
    headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    HttpEntity<?> requestEntity = new HttpEntity<Object>(request.getHeaders());
//    requestEntity.getHeaders().set("Authorization", ServiceUtils.getDefaultJwtToken());
    AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();
    ListenableFuture<ResponseEntity<DataResponse>> responseStringFuture = asyncRestTemplate.exchange(url, HttpMethod.GET,
            requestEntity, DataResponse.class);
    Object dispatchBean = request.getBody();
    responseStringFuture.addCallback(new ListenableFutureCallback<ResponseEntity<DataResponse>>() {
      @Override
      public void onSuccess(ResponseEntity<DataResponse> entity) {
        logger.debug("[Success] Response :" + entity.getStatusCode());
        IFileExporter iFileExporter = new CSVReportDataExporter();
        ExportBean exportBean = new ExportBean();
        String recipients =null;
        appContext.getBean(JavaMailSender.class);
          MailSenderUtil MailSender = new MailSenderUtil(appContext.getBean(JavaMailSender.class));
        if (dispatchBean instanceof LinkedHashMap)
        {
            exportBean.setFileName(publishedPath+ File.separator+ String.valueOf(((LinkedHashMap)
                    dispatchBean).get("name"))+"."+((LinkedHashMap) dispatchBean).get("fileType"));
            exportBean.setReportDesc(String.valueOf(((LinkedHashMap) dispatchBean).get("description")));
            exportBean.setReportName(String.valueOf(((LinkedHashMap) dispatchBean).get("name")));
            exportBean.setPublishDate(String.valueOf(((LinkedHashMap) dispatchBean).get("publishedTime")));
            exportBean.setCreatedBy(String.valueOf(((LinkedHashMap) dispatchBean).get("userFullName")));
            recipients= String.valueOf(((LinkedHashMap) dispatchBean).get("emailList"));
        }
        try {
          iFileExporter.generateFile(exportBean ,entity.getBody().getData());
          if (recipients!=null)
            MailSender.sendMail(recipients,exportBean.getReportName() + " | " + exportBean.getPublishDate(),
                    serviceUtils.prepareMailBody(exportBean,mailBody)
             ,exportBean.getFileName());
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      @Override
      public void onFailure(Throwable t) {
        logger.error("[Failed] Getting string response:" + t);
      }
    });
  }
}
