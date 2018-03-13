package com.synchronoss.saw.export.generate;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.synchronoss.saw.export.ServiceUtils;
import com.synchronoss.saw.export.distribution.MailSenderUtil;
import com.synchronoss.saw.export.generate.interfaces.IFileExporter;
import com.synchronoss.saw.export.model.AnalysisMetaData;
import com.synchronoss.saw.export.pivot.CreatePivotTable;
import com.synchronoss.saw.export.pivot.ElasticSearchAggeragationParser;
import org.apache.poi.ss.usermodel.Workbook;
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
import java.util.List;
import java.util.UUID;

@Service
public class ExportServiceImpl implements ExportService{

  private static final Logger logger = LoggerFactory.getLogger(ExportServiceImpl.class);
  
  @Value("${analysis.service.host}")
  private String apiExportOtherProperties;

  @Value("${analysis.size}")
  private String apiExportSize;

  @Value("${published.path}")
  private String publishedPath;

  @Value("${spring.mail.body}")
  private String mailBody;

  // FTP config
  @Value("${ftp.alias}")
  private String ftpAlias;

  @Value("${ftp.server}")
  private String ftpServer;

  @Value("${ftp.port}")
  private int ftpPort;

  @Value("${ftp.username}")
  private String ftpUsername;

  @Value("${ftp.password}")
  private String ftpPassword;

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
  public void reportToBeDispatchedAsync(String executionId, RequestEntity request, String analysisId) {
    String url = apiExportOtherProperties+"/" + analysisId +"/executions/"+executionId+"/data?page=1&pageSize="
            +apiExportSize+"&analysisType=report";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    HttpEntity<?> requestEntity = new HttpEntity<Object>(request.getHeaders());
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
        String ftp = null;
        String dir = UUID.randomUUID().toString();
        MailSenderUtil MailSender = new MailSenderUtil(appContext.getBean(JavaMailSender.class));
        if (dispatchBean != null && dispatchBean instanceof LinkedHashMap) {
          exportBean.setFileName(publishedPath + File.separator + dir + File.separator + String.valueOf(((LinkedHashMap)
                  dispatchBean).get("name")) + "." + ((LinkedHashMap) dispatchBean).get("fileType"));
          exportBean.setReportDesc(String.valueOf(((LinkedHashMap) dispatchBean).get("description")));
          exportBean.setReportName(String.valueOf(((LinkedHashMap) dispatchBean).get("name")));
          exportBean.setPublishDate(String.valueOf(((LinkedHashMap) dispatchBean).get("publishedTime")));
          exportBean.setCreatedBy(String.valueOf(((LinkedHashMap) dispatchBean).get("userFullName")));
          recipients = String.valueOf(((LinkedHashMap) dispatchBean).get("emailList"));
          ftp = String.valueOf(((LinkedHashMap) dispatchBean).get("ftp"));
        }
        try {
          // create a directory with unique name in published location to avoid file conflict for dispatch.
          File file = new File(exportBean.getFileName());
          file.getParentFile().mkdir();
          iFileExporter.generateFile(exportBean ,entity.getBody().getData());

          if (recipients != null)
            MailSender.sendMail(recipients, exportBean.getReportName() + " | " +
                    exportBean.getPublishDate(), serviceUtils.prepareMailBody(exportBean, mailBody),
                    exportBean.getFileName());
          logger.debug("Email sent successfully");

          // ToDo: as of now doing only in root directory, we will remove this after front end integration is done.
          if (ftp != null)
            serviceUtils.uploadToFtp(ftpServer, ftpPort, ftpUsername, ftpPassword,
                    exportBean.getFileName(), "/",
                  "report_" + exportBean.getReportName());
          logger.debug("File successfully dispatched to ftp server");

          logger.debug("Deleting exported file.");

          serviceUtils.deleteFile(exportBean.getFileName(),true);

        } catch (IOException e) {
         logger.error("Exception occured while dispatching report :" + this.getClass().getName()+ "  method dataToBeDispatchedAsync()");
        }
      }
      @Override
      public void onFailure(Throwable t) {
        logger.error("[Failed] Getting string response:" + t);
      }
    });
  }

  @Override
  @Async
  public void pivotToBeDispatchedAsync(String executionId, RequestEntity request, String analysisId) {
    String url = apiExportOtherProperties+"/" + analysisId +"/executions/"+executionId+"/data?page=1&pageSize="
            +apiExportSize+"&analysisType=pivot";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    HttpEntity<?> requestEntity = new HttpEntity<Object>(request.getHeaders());
    AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();
    ListenableFuture<ResponseEntity<JsonNode>> responseStringFuture = asyncRestTemplate.exchange(url, HttpMethod.GET,
            requestEntity, JsonNode.class);
    Object dispatchBean = request.getBody();
    responseStringFuture.addCallback(new ListenableFutureCallback<ResponseEntity<JsonNode>>() {
      @Override
      public void onSuccess(ResponseEntity<JsonNode> entity) {
        logger.debug("[Success] Response :" + entity.getStatusCode());
        IFileExporter iFileExporter = new XlsxExporter();
        ExportBean exportBean = new ExportBean();
        String recipients =null;
        String ftp = null;
        String dir = UUID.randomUUID().toString();
        MailSenderUtil MailSender = new MailSenderUtil(appContext.getBean(JavaMailSender.class));
        if (dispatchBean != null && dispatchBean instanceof LinkedHashMap) {
          exportBean.setFileName(publishedPath + File.separator + dir + File.separator + String.valueOf(((LinkedHashMap)
                  dispatchBean).get("name")) + ".xlsx");
          exportBean.setReportDesc(String.valueOf(((LinkedHashMap) dispatchBean).get("description")));
          exportBean.setReportName(String.valueOf(((LinkedHashMap) dispatchBean).get("name")));
          exportBean.setPublishDate(String.valueOf(((LinkedHashMap) dispatchBean).get("publishedTime")));
          exportBean.setCreatedBy(String.valueOf(((LinkedHashMap) dispatchBean).get("userFullName")));
          recipients = String.valueOf(((LinkedHashMap) dispatchBean).get("emailList"));
          ftp = String.valueOf(((LinkedHashMap) dispatchBean).get("ftp"));
        }
        try {
          // create a directory with unique name in published location to avoid file conflict for dispatch.
          File file = new File(exportBean.getFileName());
          file.getParentFile().mkdir();
          AnalysisMetaData analysisMetaData = getAnalysisMetadata(analysisId);
          ElasticSearchAggeragationParser elasticSearchAggeragationParser
                  = new ElasticSearchAggeragationParser(analysisMetaData.getAnalyses().get(0));
          List<Object> dataObj = elasticSearchAggeragationParser.parseData(entity.getBody());
          elasticSearchAggeragationParser.setColumnDataType(exportBean,analysisMetaData.getAnalyses().get(0));
          Workbook workbook =  iFileExporter.getWorkBook(exportBean, dataObj);
          CreatePivotTable createPivotTable = new CreatePivotTable(analysisMetaData.getAnalyses().get(0));
          createPivotTable.createPivot(workbook,file);
          if (recipients!=null)
            MailSender.sendMail(recipients,exportBean.getReportName() + " | " + exportBean.getPublishDate(),
                    serviceUtils.prepareMailBody(exportBean,mailBody)
                    ,exportBean.getFileName());
          logger.debug("Email sent successfully ");

          // ToDo: as of now doing only in root directory, we will remove this after front end integration is done.
          if (ftp != null)
            serviceUtils.uploadToFtp(ftpServer, ftpPort, ftpUsername, ftpPassword,
                    exportBean.getFileName(), "/",
                    "pivot_" + exportBean.getReportName());

          serviceUtils.uploadToFtp(ftpServer, ftpPort, ftpUsername, ftpPassword, exportBean.getFileName(), "/", exportBean.getReportName());

          logger.debug("Removing the file from published location");
          serviceUtils.deleteFile(exportBean.getFileName(),true);
        } catch (IOException e) {
          logger.error("Exception occured while dispatching pivot :" + this.getClass().getName()+ "  method dataToBeDispatchedAsync()");
        }
      }
      @Override
      public void onFailure(Throwable t) {
        logger.error("[Failed] Getting string response:" + t);
      }
    });
  }

  @Override
  public AnalysisMetaData getAnalysisMetadata(String analysisId) {

    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    String url = apiExportOtherProperties+"/md?analysisId="+analysisId;
    return restTemplate.getForObject(url, AnalysisMetaData.class);
  }
}
