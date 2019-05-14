package com.synchronoss.saw.export.generate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.saw.export.AmazonS3Handler;
import com.synchronoss.saw.export.S3Config;
import com.synchronoss.saw.export.ServiceUtils;
import com.synchronoss.saw.export.distribution.MailSenderUtil;
import com.synchronoss.saw.export.exceptions.JSONValidationSAWException;
import com.synchronoss.saw.export.generate.interfaces.ExportService;
import com.synchronoss.saw.export.generate.interfaces.IFileExporter;
import com.synchronoss.saw.export.model.AnalysisMetaData;
import com.synchronoss.saw.export.model.DataResponse;
import com.synchronoss.saw.export.model.S3.S3Customer;
import com.synchronoss.saw.export.model.S3.S3Details;
import com.synchronoss.saw.export.model.ftp.FTPDetails;
import com.synchronoss.saw.export.model.ftp.FtpCustomer;
import com.synchronoss.saw.export.pivot.CreatePivotTable;
import com.synchronoss.saw.export.pivot.ElasticSearchAggeragationParser;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletRequest;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

@Service
public class ExportServiceImpl implements ExportService {

  private static final Logger logger = LoggerFactory.getLogger(ExportServiceImpl.class);

  @Value("${analysis.service.host}")
  private String apiExportOtherProperties;

  @Value("${analysis.uiExportSize}")
  private String uiExportSize;

  // email export size
  @Value("${analysis.emailExportSize}")
  private String emailExportSize;

  // ftp export size
  @Value("${analysis.ftpExportSize}")
  private String ftpExportSize;

  // s3 export size
  @Value("${analysis.s3ExportSize}")
  private String s3ExportSize;

  @Value("${published.path}")
  private String publishedPath;

  @Value("${spring.mail.body}")
  private String mailBody;

  @Value("${ftp.details.file}")
  private String ftpDetailsFile;

  @Value("${s3.details.file}")
  private String s3DetailsFile;

  @Value("${exportChunkSize}")
  private String exportChunkSize;

  @Autowired private ApplicationContext appContext;

  @Autowired private ServiceUtils serviceUtils;

  @Override
  public DataResponse dataToBeExportedSync(
      String executionId, HttpServletRequest request, String analysisId)
      throws JSONValidationSAWException {
    HttpEntity<?> requestEntity = new HttpEntity<Object>(setRequestHeader(request));
    RestTemplate restTemplate = new RestTemplate();
    // During report extraction time, this parameter will not be passed.
    // Hence we should use uiExportSize configuration parameter.
    String sizOfExport;
    sizOfExport =
        ((sizOfExport = request.getParameter("pageSize")) != null) ? sizOfExport : uiExportSize;
    String url =
        apiExportOtherProperties
            + "/"
            + executionId
            + "/executions/"
            + analysisId
            + "/data?page=1&pageSize="
            + sizOfExport
            + "&analysisType=report";
    logger.debug("Transport Service URL: {}", url);
    ResponseEntity<DataResponse> transportResponse =
        restTemplate.exchange(url, HttpMethod.GET, requestEntity, DataResponse.class);
    return transportResponse.getBody();
  }

  private HttpHeaders setRequestHeader(HttpServletRequest request) {
    HttpHeaders requestHeaders = new HttpHeaders();
    requestHeaders.set("Host", request.getHeader("Host"));
    requestHeaders.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    requestHeaders.set("Content-type", MediaType.APPLICATION_JSON_VALUE);
    requestHeaders.set("Authorization", request.getHeader("Authorization"));
    return requestHeaders;
  }

  @Override
  @Async
  public ListenableFuture<ResponseEntity<DataResponse>> dataToBeExportedAsync(
      String executionId,
      HttpServletRequest request,
      String analysisId,
      String analysisType,
      String executionType) {
    // During report extraction time, this parameter will not be passed.
    // Hence we should use uiExportSize configuration parameter.
    String sizOfExport;
    String url;
    sizOfExport =
        ((sizOfExport = request.getParameter("pageSize")) != null) ? sizOfExport : uiExportSize;
    if (executionType != null
        && !executionType.isEmpty()
        && executionType.equalsIgnoreCase("onetime")
        && executionId == null)
      url =
          apiExportOtherProperties
              + "/"
              + analysisId
              + "/executions/data?page=1&pageSize="
              + sizOfExport
              + "&analysisType="
              + analysisType
              + "&executionType=onetime";
    else if (executionType != null
        && !executionType.isEmpty()
        && executionType.equalsIgnoreCase("onetime"))
      url =
          apiExportOtherProperties
              + "/"
              + executionId
              + "/executions/"
              + analysisId
              + "/data?page=1&pageSize="
              + sizOfExport
              + "&analysisType="
              + analysisType
              + "&executionType=onetime";
    else if (executionId == null)
      url =
          apiExportOtherProperties
              + "/"
              + analysisId
              + "/executions/data?page=1&pageSize="
              + sizOfExport
              + "&analysisType="
              + analysisType;
    else
      url =
          apiExportOtherProperties
              + "/"
              + executionId
              + "/executions/"
              + analysisId
              + "/data?page=1&pageSize="
              + sizOfExport
              + "&analysisType="
              + analysisType;
    HttpEntity<?> requestEntity = new HttpEntity<Object>(setRequestHeader(request));
    AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();
    ListenableFuture<ResponseEntity<DataResponse>> responseStringFuture =
        asyncRestTemplate.exchange(url, HttpMethod.GET, requestEntity, DataResponse.class);
    responseStringFuture.addCallback(
        new ListenableFutureCallback<ResponseEntity<DataResponse>>() {
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
  public void reportToBeDispatchedAsync(
      String executionId, RequestEntity request, String analysisId, String analysisType) {

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    HttpEntity<?> requestEntity = new HttpEntity<Object>(request.getHeaders());
    AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();
    // at times we need synchronous processing even in async as becasue of massive parallelism
    // it may halt entire system or may not complete the request
    RestTemplate restTemplate = new RestTemplate();
    Object dispatchBean = request.getBody();

    ExportBean exportBean = setExportBeanProps(dispatchBean);
    String recipients = null;
    String ftp = null;
    String s3 = null;
    String jobGroup = null;
    boolean zip = false;

    // Read Additional props
    if (dispatchBean != null && dispatchBean instanceof LinkedHashMap) {
      if (((LinkedHashMap) dispatchBean).get("emailList") != null)
        recipients = String.valueOf(((LinkedHashMap) dispatchBean).get("emailList"));
      if (((LinkedHashMap) dispatchBean).get("ftp") != null)
        ftp = String.valueOf(((LinkedHashMap) dispatchBean).get("ftp"));

      if (((LinkedHashMap) dispatchBean).get("s3") != null)
        s3 = String.valueOf(((LinkedHashMap) dispatchBean).get("s3"));

      if (((LinkedHashMap) dispatchBean).get("zip") != null)
        zip = (boolean) ((LinkedHashMap) dispatchBean).get("zip");

      jobGroup = String.valueOf(((LinkedHashMap) dispatchBean).get("jobGroup"));
    }

    if ((recipients != null && !recipients.equals("") && recipients.contains("@"))
        || (ftp != null && ftp != "")
        || (s3 != null && s3 != "")) {
      logger.trace("Recipients: " + recipients);
      dispatchReport(
          analysisId,
          executionId,
          analysisType,
          emailExportSize,
          exportBean,
          recipients,
          requestEntity,
          s3,
          ftp,
          zip,
          jobGroup,
          restTemplate);
    }
  }

  public ExportBean setExportBeanProps(Object dispatchBean) {

    ExportBean exportBean = new ExportBean();
    // presetting the variables, as their presence will determine which URLs to process
    if (dispatchBean != null && dispatchBean instanceof LinkedHashMap) {
      if (((LinkedHashMap) dispatchBean).get("fileType") != null) {
        exportBean.setFileType(String.valueOf(((LinkedHashMap) dispatchBean).get("fileType")));
      }
      exportBean.setReportDesc(String.valueOf(((LinkedHashMap) dispatchBean).get("description")));
      exportBean.setReportName(String.valueOf(((LinkedHashMap) dispatchBean).get("name")));
      exportBean.setPublishDate(
          String.valueOf(((LinkedHashMap) dispatchBean).get("publishedTime")));
      exportBean.setCreatedBy(String.valueOf(((LinkedHashMap) dispatchBean).get("userFullName")));
      // consider default format as csv if file type is not provided.
      if (exportBean.getFileType() == null || exportBean.getFileType().isEmpty()) {
        exportBean.setFileName(String.valueOf(((LinkedHashMap) dispatchBean).get("name")) + ".csv");
        exportBean.setFileType("csv");
      } else {
        exportBean.setFileName(
            String.valueOf(((LinkedHashMap) dispatchBean).get("name"))
                + "."
                + exportBean.getFileType());
      }
    }
    return exportBean;
  }

  public void streamResponseToFile(
      ExportBean exportBean, long limitPerPage, ResponseEntity<DataResponse> entity) {
    try {

      File file = new File(exportBean.getFileName());
      file.getParentFile().mkdirs();

      // if the file is found, append the content
      // this is basically for entire for loop to execute correctly on the same file
      // as no two executions are going to have same ID.
      FileOutputStream fos = new FileOutputStream(file, true);
      OutputStreamWriter osw = new OutputStreamWriter(fos);
      String fileType = exportBean.getFileType();

      // stream the page output to file.
      if (fileType.equalsIgnoreCase("csv") || fileType == null || fileType.isEmpty()) {
        streamToCSVReport(entity, limitPerPage, exportBean, osw);
        osw.close();
        fos.close();
      } else {
        streamToXlsxReport(entity.getBody(), limitPerPage, exportBean);
      }

    } catch (IOException e) {
      logger.error(
          "Exception occurred while dispatching report :"
              + this.getClass().getName()
              + "  method dataToBeDispatchedAsync()");
    }
  }

  public void streamToCSVReport(
      ResponseEntity<DataResponse> entity,
      long LimittoExport,
      ExportBean exportBean,
      OutputStreamWriter osw) {
    entity.getBody().getData().stream()
        .limit(LimittoExport)
        .forEach(
            line -> {
              try {
                if (line instanceof LinkedHashMap) {
                  String[] header = null;
                  if (exportBean.getColumnHeader() == null
                      || exportBean.getColumnHeader().length == 0) {
                    Object[] obj = ((LinkedHashMap) line).keySet().toArray();
                    header = Arrays.copyOf(obj, obj.length, String[].class);
                    exportBean.setColumnHeader(header);
                    osw.write(
                        Arrays.stream(header)
                            .map(i -> "\"" + i + "\"")
                            .collect(Collectors.joining(",")));
                    osw.write("\n");
                    osw.write(
                        Arrays.stream(exportBean.getColumnHeader())
                            .map(val -> "\"" + ((LinkedHashMap) line).get(val) + "\"")
                            .collect(Collectors.joining(",")));
                    osw.write(System.getProperty("line.separator"));
                    logger.debug("Header for csv file: " + header);
                  } else {
                    // ideally we shouldn't be using collectors but it's a single row so it
                    // won't hamper memory consumption
                    osw.write(
                        Arrays.stream(exportBean.getColumnHeader())
                            .map(val -> "\"" + ((LinkedHashMap) line).get(val) + "\"")
                            .collect(Collectors.joining(",")));
                    osw.write(System.getProperty("line.separator"));
                    logger.debug("Line Item for report: " + line.toString());
                  }
                }
              } catch (Exception e) {
                logger.error("ERROR_PROCESSING_STREAM: " + e.getMessage());
              }
            });
  }

  /**
   * @param response
   * @param LimittoExport
   * @param exportBean
   * @throws IOException
   */
  public Boolean streamToXlsxReport(
      DataResponse response, long LimittoExport, ExportBean exportBean) throws IOException {

    BufferedOutputStream stream = null;
    File xlsxFile = null;
    xlsxFile = new File(exportBean.getFileName());
    xlsxFile.getParentFile().mkdir();
    xlsxFile.createNewFile();
    stream = new BufferedOutputStream(new FileOutputStream(xlsxFile));
    XlsxExporter xlsxExporter = new XlsxExporter();
    Workbook workBook = new XSSFWorkbook();
    workBook.getSpreadsheetVersion();
    XSSFSheet sheet = (XSSFSheet) workBook.createSheet(exportBean.getReportName());
    try {
      response.getData().stream()
          .limit(LimittoExport)
          .forEach(
              line -> {
                xlsxExporter.addxlsxRow(exportBean, workBook, sheet, line);
              });
      xlsxExporter.autoSizeColumns(workBook);
      workBook.write(stream);
    } finally {
      stream.flush();
      stream.close();
    }
    return true;
  }

  @Override
  @Async
  public void pivotToBeDispatchedAsync(
      String executionId, RequestEntity request, String analysisId) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    HttpEntity<?> requestEntity = new HttpEntity<Object>(request.getHeaders());
    AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();
    Object dispatchBean = request.getBody();
    String recipients = null;
    String ftp = null;
    String s3 = null;
    String jobGroup = null;
    ExportBean exportBean = new ExportBean();

    // check beforehand if the request is not null
    if (dispatchBean != null && dispatchBean instanceof LinkedHashMap) {
      Object recipientsObj = ((LinkedHashMap) dispatchBean).get("emailList");
      Object ftpObj = ((LinkedHashMap) dispatchBean).get("ftp");
      Object s3Obj = ((LinkedHashMap) dispatchBean).get("s3");

      if (recipientsObj != null) {
        recipients = String.valueOf(recipientsObj);
      }

      if (ftpObj != null) {
        ftp = String.valueOf(ftpObj);
      }

      if (s3Obj != null) {
        s3 = String.valueOf(s3Obj);
      }
      jobGroup = String.valueOf(((LinkedHashMap) dispatchBean).get("jobGroup"));

      logger.debug("recipients: " + recipients);
      logger.debug("ftp: " + ftp);
      logger.debug("s3: " + s3);

      if ((recipients != null && !recipients.equals(""))
          || ((s3 != null && s3 != ""))
          || ((ftp != null && ftp != ""))) {
        String url =
            apiExportOtherProperties
                + "/"
                + analysisId
                + "/executions/"
                + executionId
                + "/data?page=1&pageSize="
                + emailExportSize
                + "&analysisType=pivot";
        ListenableFuture<ResponseEntity<JsonNode>> responseStringFuture =
            asyncRestTemplate.exchange(url, HttpMethod.GET, requestEntity, JsonNode.class);

        logger.debug("dispatchBean for Pivot: " + dispatchBean.toString());
        String s3bucket = s3;
        String finalRecipients = recipients;
        String finalFtp = ftp;
        String finalJobGroup = jobGroup;
        responseStringFuture.addCallback(
            new ListenableFutureCallback<ResponseEntity<JsonNode>>() {
              @Override
              public void onSuccess(ResponseEntity<JsonNode> entity) {
                if (finalRecipients != null && !finalRecipients.equals("")) {
                  logger.debug(
                      "In Email dispatcher: [Success] Response :" + entity.getStatusCode());
                  IFileExporter iFileExporter = new XlsxExporter();
                  String dir = UUID.randomUUID().toString();
                  MailSenderUtil MailSender =
                      new MailSenderUtil(appContext.getBean(JavaMailSender.class));
                  exportBean.setFileType(
                      String.valueOf(((LinkedHashMap) dispatchBean).get("fileType")));
                  exportBean.setFileName(
                      publishedPath
                          + File.separator
                          + dir
                          + File.separator
                          + String.valueOf(((LinkedHashMap) dispatchBean).get("name"))
                          + "."
                          + exportBean.getFileType());
                  exportBean.setReportDesc(
                      String.valueOf(((LinkedHashMap) dispatchBean).get("description")));
                  exportBean.setReportName(
                      String.valueOf(((LinkedHashMap) dispatchBean).get("name")));
                  exportBean.setPublishDate(
                      String.valueOf(((LinkedHashMap) dispatchBean).get("publishedTime")));
                  exportBean.setCreatedBy(
                      String.valueOf(((LinkedHashMap) dispatchBean).get("userFullName")));
                  try {
                    // create a directory with unique name in published location to avoid file
                    // conflict for dispatch.
                    File file = new File(exportBean.getFileName());
                    file.getParentFile().mkdir();
                    AnalysisMetaData analysisMetaData = getAnalysisMetadata(analysisId);
                    ElasticSearchAggeragationParser elasticSearchAggeragationParser =
                        new ElasticSearchAggeragationParser(analysisMetaData.getAnalyses().get(0));
                    List<Object> dataObj =
                        elasticSearchAggeragationParser.parseData(entity.getBody());
                    elasticSearchAggeragationParser.setColumnDataType(
                        exportBean, analysisMetaData.getAnalyses().get(0));
                    Workbook workbook = iFileExporter.getWorkBook(exportBean, dataObj);
                    CreatePivotTable createPivotTable =
                        new CreatePivotTable(analysisMetaData.getAnalyses().get(0));
                    createPivotTable.createPivot(workbook, file);
                    MailSender.sendMail(
                        finalRecipients,
                        exportBean.getReportName() + " | " + exportBean.getPublishDate(),
                        serviceUtils.prepareMailBody(exportBean, mailBody),
                        exportBean.getFileName());
                    logger.debug("Email sent successfully ");
                    logger.debug("Removing the file from published location");
                    serviceUtils.deleteFile(exportBean.getFileName(), true);
                  } catch (IOException e) {
                    logger.error(
                        "Exception occurred while dispatching pivot :"
                            + this.getClass().getName()
                            + "  method dataToBeDispatchedAsync()");
                  }
                }

                logger.debug("S3 details = " + s3bucket);
                if (s3bucket != null && s3bucket != "") {
                  logger.debug("S3 details set. Dispatching to S3");
                  s3DispatcherPivot(
                      analysisId,
                      executionId,
                      s3bucket,
                      asyncRestTemplate,
                      dispatchBean,
                      requestEntity,
                      finalJobGroup);
                }

                logger.debug("ftp details = " + finalFtp);
                if (finalFtp != null && finalFtp != "") {
                  logger.debug("FTP details set. Dispatching to FTP");
                  ftpDispatcherPivot(
                      analysisId,
                      executionId,
                      finalFtp,
                      asyncRestTemplate,
                      dispatchBean,
                      requestEntity,
                      finalJobGroup);
                }
              }

              @Override
              public void onFailure(Throwable t) {
                logger.error("[Failed] Getting string response:" + t);
              }
            });
      }
    }
  }

  public void ftpDispatcherPivot(
      String analysisId,
      String executionId,
      String ftp,
      AsyncRestTemplate asyncRestTemplate,
      Object dispatchBean,
      HttpEntity<?> requestEntity,
      String jobGroup) {
    if (ftp != null && !ftp.equals("")) {
      String url =
          apiExportOtherProperties
              + "/"
              + analysisId
              + "/executions/"
              + executionId
              + "/data?page=1&pageSize="
              + ftpExportSize
              + "&analysisType=pivot";
      ListenableFuture<ResponseEntity<JsonNode>> responseStringFuture =
          asyncRestTemplate.exchange(url, HttpMethod.GET, requestEntity, JsonNode.class);

      logger.debug("dispatchBean for Pivot: " + dispatchBean.toString());
      String finalFtp = ftp;
      String finalJobGroup = jobGroup;
      responseStringFuture.addCallback(
          new ListenableFutureCallback<ResponseEntity<JsonNode>>() {
            @Override
            public void onSuccess(ResponseEntity<JsonNode> entity) {
              logger.debug("In FTP dispatcher: [Success] Response :" + entity.getStatusCode());
              IFileExporter iFileExporter = new XlsxExporter();
              ExportBean exportBean = new ExportBean();
              String dir = UUID.randomUUID().toString();

              exportBean.setFileType(
                  String.valueOf(((LinkedHashMap) dispatchBean).get("fileType")));
              exportBean.setFileName(
                  publishedPath
                      + File.separator
                      + dir
                      + File.separator
                      + String.valueOf(((LinkedHashMap) dispatchBean).get("name"))
                      + "."
                      + exportBean.getFileType());
              exportBean.setReportDesc(
                  String.valueOf(((LinkedHashMap) dispatchBean).get("description")));
              exportBean.setReportName(String.valueOf(((LinkedHashMap) dispatchBean).get("name")));
              exportBean.setPublishDate(
                  String.valueOf(((LinkedHashMap) dispatchBean).get("publishedTime")));
              exportBean.setCreatedBy(
                  String.valueOf(((LinkedHashMap) dispatchBean).get("userFullName")));

              File cfile = new File(exportBean.getFileName());
              String zipFileName = cfile.getAbsolutePath().concat(".zip");

              try {
                // create a directory with unique name in published location to avoid file conflict
                // for dispatch.
                File file = new File(exportBean.getFileName());
                file.getParentFile().mkdir();
                AnalysisMetaData analysisMetaData = getAnalysisMetadata(analysisId);
                ElasticSearchAggeragationParser elasticSearchAggeragationParser =
                    new ElasticSearchAggeragationParser(analysisMetaData.getAnalyses().get(0));
                List<Object> dataObj = elasticSearchAggeragationParser.parseData(entity.getBody());
                elasticSearchAggeragationParser.setColumnDataType(
                    exportBean, analysisMetaData.getAnalyses().get(0));
                Workbook workbook = iFileExporter.getWorkBook(exportBean, dataObj);
                CreatePivotTable createPivotTable =
                    new CreatePivotTable(analysisMetaData.getAnalyses().get(0));
                createPivotTable.createPivot(workbook, file);

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
                LocalDateTime now = LocalDateTime.now();

                try {
                  FileOutputStream fos = new FileOutputStream(zipFileName);
                  ZipOutputStream zos = new ZipOutputStream(fos);

                  zos.putNextEntry(new ZipEntry(cfile.getName()));

                  byte[] bytes = Files.readAllBytes(Paths.get(exportBean.getFileName()));
                  zos.write(bytes, 0, bytes.length);
                  zos.closeEntry();
                  zos.close();

                  logger.debug("ftp servers: " + finalFtp);

                  for (String aliastemp : finalFtp.split(",")) {
                    ObjectMapper jsonMapper = new ObjectMapper();
                    try {
                      FtpCustomer obj =
                          jsonMapper.readValue(new File(ftpDetailsFile), FtpCustomer.class);
                      for (FTPDetails alias : obj.getFtpList()) {
                        logger.debug("Processing Host: " + alias.getHost());
                        logger.debug("jobGroup: " + alias.getCustomerName());
                        logger.debug("Alias: " + aliastemp.equals(alias.getAlias()));
                        if (alias.getCustomerName().equals(finalJobGroup)
                            && aliastemp.equals(alias.getAlias())) {
                          logger.debug("Inside If");
                          serviceUtils.uploadToFtp(
                              alias.getHost(),
                              alias.getPort(),
                              alias.getUsername(),
                              alias.getPassword(),
                              zipFileName,
                              alias.getLocation(),
                              cfile.getName().substring(0, cfile.getName().lastIndexOf(".") + 1)
                                  + dtf.format(now).toString()
                                  + "."
                                  + exportBean.getFileType()
                                  + ".zip",
                              alias.getType());
                          logger.debug(
                              "Uploaded to ftp alias: "
                                  + alias.getCustomerName()
                                  + ":"
                                  + alias.getHost());
                        }
                      }
                    } catch (IOException e) {
                      logger.error(e.getMessage());
                    } catch (Exception e) {
                      logger.error(e.getMessage());
                    }
                  }
                } catch (FileNotFoundException e) {
                  logger.error("Zip file error FileNotFound: " + e.getMessage());
                } catch (IOException e) {
                  logger.error("Zip file error IOException: " + e.getMessage());
                }

                logger.debug("Removing the file from published location");
                serviceUtils.deleteFile(exportBean.getFileName(), true);
                serviceUtils.deleteFile(zipFileName, true);
              } catch (IOException e) {
                logger.error(
                    "Exception occurred while dispatching pivot :"
                        + this.getClass().getName()
                        + "  method dataToBeDispatchedAsync()");
              }
            }

            @Override
            public void onFailure(Throwable t) {
              logger.error("[Failed] Getting string response:" + t);
            }
          });
    }
  }

  public void s3DispatcherPivot(
      String analysisId,
      String executionId,
      String s3,
      AsyncRestTemplate asyncRestTemplate,
      Object dispatchBean,
      HttpEntity<?> requestEntity,
      String jobGroup) {
    String url =
        apiExportOtherProperties
            + "/"
            + analysisId
            + "/executions/"
            + executionId
            + "/data?page=1&pageSize="
            + s3ExportSize
            + "&analysisType=pivot";
    ListenableFuture<ResponseEntity<JsonNode>> responseStringFuture =
        asyncRestTemplate.exchange(url, HttpMethod.GET, requestEntity, JsonNode.class);

    logger.debug("dispatchBean for Pivot: " + dispatchBean.toString());
    String finalS3 = s3;
    String finalJobGroup = jobGroup;
    responseStringFuture.addCallback(
        new ListenableFutureCallback<ResponseEntity<JsonNode>>() {
          @Override
          public void onSuccess(ResponseEntity<JsonNode> entity) {
            logger.debug("In S3 dispatcher: [Success] Response :" + entity.getStatusCode());
            IFileExporter iFileExporter = new XlsxExporter();
            ExportBean exportBean = new ExportBean();
            String dir = UUID.randomUUID().toString();

            exportBean.setFileType(String.valueOf(((LinkedHashMap) dispatchBean).get("fileType")));
            exportBean.setFileName(
                publishedPath
                    + File.separator
                    + dir
                    + File.separator
                    + String.valueOf(((LinkedHashMap) dispatchBean).get("name"))
                    + "."
                    + exportBean.getFileType());
            exportBean.setReportDesc(
                String.valueOf(((LinkedHashMap) dispatchBean).get("description")));
            exportBean.setReportName(String.valueOf(((LinkedHashMap) dispatchBean).get("name")));
            exportBean.setPublishDate(
                String.valueOf(((LinkedHashMap) dispatchBean).get("publishedTime")));
            exportBean.setCreatedBy(
                String.valueOf(((LinkedHashMap) dispatchBean).get("userFullName")));

            File cfile = new File(exportBean.getFileName());

            try {
              File file = new File(exportBean.getFileName());
              file.getParentFile().mkdir();
              AnalysisMetaData analysisMetaData = getAnalysisMetadata(analysisId);
              ElasticSearchAggeragationParser elasticSearchAggeragationParser =
                  new ElasticSearchAggeragationParser(analysisMetaData.getAnalyses().get(0));
              List<Object> dataObj = elasticSearchAggeragationParser.parseData(entity.getBody());
              elasticSearchAggeragationParser.setColumnDataType(
                  exportBean, analysisMetaData.getAnalyses().get(0));
              Workbook workbook = iFileExporter.getWorkBook(exportBean, dataObj);
              CreatePivotTable createPivotTable =
                  new CreatePivotTable(analysisMetaData.getAnalyses().get(0));
              createPivotTable.createPivot(workbook, file);
            } catch (FileNotFoundException e) {
              logger.error("file error FileNotFound: " + e.getMessage());
            } catch (IOException e) {
              e.printStackTrace();
            }
            logger.debug("s3 List: " + finalS3);

            for (String aliastemp : finalS3.split(",")) {
              ObjectMapper jsonMapper = new ObjectMapper();
              try {
                S3Customer obj = jsonMapper.readValue(new File(s3DetailsFile), S3Customer.class);
                for (S3Details alias : obj.getS3List()) {
                  if (alias.getCustomerCode().equals(finalJobGroup)
                      && aliastemp.equals(alias.getAlias())) {
                    logger.debug("Final Obj to be dispatched for S3 : ");
                    logger.debug("BucketName : " + alias.getBucketName());
                    logger.debug("AccessKey : " + alias.getAccessKey());
                    logger.debug("SecretKey : " + alias.getSecretKey());
                    logger.debug("Region : " + alias.getRegion());
                    logger.debug("getOutputLocation : " + alias.getOutputLocation());
                    logger.debug("FileName : " + exportBean.getFileName());

                    S3Config s3Config =
                        new S3Config(
                            alias.getBucketName(),
                            alias.getAccessKey(),
                            alias.getSecretKey(),
                            alias.getRegion(),
                            alias.getOutputLocation());

                    AmazonS3Handler s3Handler = new AmazonS3Handler(s3Config);
                    s3Handler.uploadObject(cfile.getAbsoluteFile());

                    logger.debug("Removing the file from published location");
                    serviceUtils.deleteFile(exportBean.getFileName(), true);
                  }
                }
              } catch (IOException e) {
                logger.error(e.getMessage());
              } catch (Exception e) {
                logger.error(e.getMessage());
              }
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
    String url = apiExportOtherProperties + "/md?analysisId=" + analysisId;
    return restTemplate.getForObject(url, AnalysisMetaData.class);
  }

  @Override
  public List<String> listFtpsForCustomer(RequestEntity request) {
    Object dispatchBean = request.getBody();
    // this job group is customer unique identifier
    String jobGroup = null;
    List<String> aliases = new ArrayList<String>();

    if (dispatchBean != null && dispatchBean instanceof LinkedHashMap) {
      jobGroup = String.valueOf(((LinkedHashMap) dispatchBean).get("jobGroup"));
      ObjectMapper jsonMapper = new ObjectMapper();
      try {
        File f = new File(ftpDetailsFile);
        if (f.exists() && !f.isDirectory()) {
          FtpCustomer obj = jsonMapper.readValue(f, FtpCustomer.class);
          for (FTPDetails alias : obj.getFtpList()) {
            if (alias.getCustomerName().equals(jobGroup)) {
              aliases.add(alias.getAlias());
            }
          }
        }
      } catch (IOException e) {
        logger.error(e.getMessage());
      }
    }
    return aliases;
  }

  @Override
  public List<String> listS3ForCustomer(RequestEntity requestEntity) {
    Object dispatchBean = requestEntity.getBody();
    String jobGroup = null;
    List<String> aliases = new ArrayList<String>();

    if (dispatchBean != null && dispatchBean instanceof LinkedHashMap) {
      jobGroup = String.valueOf(((LinkedHashMap) dispatchBean).get("jobGroup"));
      ObjectMapper jsonMapper = new ObjectMapper();
      try {
        File f = new File(s3DetailsFile);
        if (f.exists() && !f.isDirectory()) {
          S3Customer obj = jsonMapper.readValue(f, S3Customer.class);
          for (S3Details alias : obj.getS3List()) {
            if (alias.getCustomerCode().equals(jobGroup)) {
              aliases.add(alias.getAlias());
            }
          }
        }
      } catch (IOException e) {
        logger.error(e.getMessage());
      }
    }
    return aliases;
  }

  public boolean dispatchMail(
      ExportBean bean, String recipients, ResponseEntity<DataResponse> entity, boolean zip) {

    ExportBean exportBean = new ExportBean();
    exportBean = bean;

    String fileType = exportBean.getFileType();
    MailSenderUtil MailSender = new MailSenderUtil(appContext.getBean(JavaMailSender.class));

    logger.debug("Email async success");
    logger.debug("[Success] Response :" + entity.getStatusCode());

    try {
      // create a directory with unique name in published location to avoid file
      // conflict for dispatch.
      String mailDispatchFileName =
          publishedPath
              + File.separator
              + "mail"
              + generateRandomStringDir()
              + File.separator
              + exportBean.getFileName();

      exportBean.setFileName(mailDispatchFileName);

      if (fileType.equalsIgnoreCase("csv") || fileType == null || fileType.isEmpty()) {
        File file = createFileforDispatch(mailDispatchFileName);

        FileOutputStream fos = new FileOutputStream(file);
        OutputStreamWriter osw = new OutputStreamWriter(fos);

        streamToCSVReport(entity, Long.parseLong(emailExportSize), exportBean, osw);
        osw.close();
        fos.close();
      } else {
        streamToXlsxReport(entity.getBody(), Long.parseLong(emailExportSize), exportBean);
      }

      File cfile = new File(exportBean.getFileName());
      String zipFileName = cfile.getAbsolutePath().concat(".zip");

      if (zip) {
        FileOutputStream fos_zip = new FileOutputStream(zipFileName);
        ZipOutputStream zos = new ZipOutputStream(fos_zip);
        zos.putNextEntry(new ZipEntry(cfile.getName()));

        byte[] readBuffer = new byte[2048];
        int amountRead;
        int written = 0;

        try (FileInputStream inputStream = new FileInputStream(exportBean.getFileName())) {

          while ((amountRead = inputStream.read(readBuffer)) > 0) {
            zos.write(readBuffer, 0, amountRead);
            written += amountRead;
          }

          logger.info("Written " + written + " bytes to " + zipFileName);

        } catch (Exception e) {
          logger.error("Error while writing to zip: " + e.getMessage());
        }

        zos.closeEntry();
        zos.close();

        MailSender.sendMail(
            recipients,
            exportBean.getReportName() + " | " + exportBean.getPublishDate(),
            serviceUtils.prepareMailBody(exportBean, mailBody),
            exportBean.getFileName());
        logger.debug("Email sent successfully");

        logger.debug("Deleting exported file.");
        try {
          logger.debug(
              "ExportBean.getFileName() to delete -  mail : "
                  + zipFileName
                  + ", "
                  + exportBean.getFileName());
          serviceUtils.deleteFile(exportBean.getFileName(), true);
          serviceUtils.deleteFile(zipFileName, true);
        } catch (IOException e) {
          e.printStackTrace();
        }

      } else {
        MailSender.sendMail(
            recipients,
            exportBean.getReportName() + " | " + exportBean.getPublishDate(),
            serviceUtils.prepareMailBody(exportBean, mailBody),
            exportBean.getFileName());
        logger.debug("Email sent successfully");

        logger.debug("Deleting exported file.");
        try {
          logger.debug("ExportBean.getFileName() to delete -  mail : " + exportBean.getFileName());
          serviceUtils.deleteFile(exportBean.getFileName(), true);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

    } catch (IOException e) {
      logger.error(
          "Exception occurred while dispatching report :"
              + this.getClass().getName()
              + "  method dataToBeDispatchedAsync()");
    }
    return true;
  }

  public void dispatchFileToS3(
      String analysisId,
      String executionId,
      String analysisType,
      ExportBean bean,
      String finalS3,
      boolean zip,
      String finalJobGroup,
      HttpEntity<?> requestEntity,
      RestTemplate restTemplate,
      String userFileName) {
    logger.trace("Inside S3 dispatcher");

    ExportBean exportBean = new ExportBean();
    exportBean = bean;

    // create a directory with unique name in published location to avoid file
    // conflict for dispatch.
    String mailDispatchFileName =
        publishedPath
            + File.separator
            + "s3"
            + generateRandomStringDir()
            + File.separator
            + userFileName;

    exportBean.setFileName(mailDispatchFileName);

    prepareFileWithSize(
        analysisId,
        executionId,
        analysisType,
        s3ExportSize,
        requestEntity,
        restTemplate,
        exportBean);

    File cfile = new File(exportBean.getFileName());
    String zipFileName = cfile.getAbsolutePath().concat(".zip");

    if (zip) {
      logger.debug("S3 - zip = true!!");
      try {
        FileOutputStream fos_zip = new FileOutputStream(zipFileName);
        ZipOutputStream zos = new ZipOutputStream(fos_zip);
        zos.putNextEntry(new ZipEntry(cfile.getName()));

        byte[] readBuffer = new byte[2048];
        int amountRead;
        int written = 0;

        try (FileInputStream inputStream = new FileInputStream(exportBean.getFileName())) {

          while ((amountRead = inputStream.read(readBuffer)) > 0) {
            zos.write(readBuffer, 0, amountRead);
            written += amountRead;
          }

          logger.info("Written " + written + " bytes to " + zipFileName);

        } catch (Exception e) {
          logger.error("Error while writing to zip: " + e.getMessage());
        }

        zos.closeEntry();
        zos.close();

        s3DispatchExecutor(finalS3, finalJobGroup, new File(zipFileName), exportBean);

        logger.debug("ExportBean.getFileName() - to delete in S3 : " + exportBean.getFileName());
        deleteDispatchedFile(exportBean.getFileName());
        deleteDispatchedFile(zipFileName);
        logger.debug("ExportBean.getFileName() - to delete in S3 : " + zipFileName);

      } catch (Exception e) {
        logger.error("Error writing to zip!!");
      }
    } else {
      s3DispatchExecutor(finalS3, finalJobGroup, cfile, exportBean);
      logger.debug("ExportBean.getFileName() - to delete in S3 : " + exportBean.getFileName());
      deleteDispatchedFile(exportBean.getFileName());
    }
  }

  public void s3DispatchExecutor(
      String finalS3, String finalJobGroup, File file, ExportBean exportBean) {
    for (String aliastemp : finalS3.split(",")) {
      logger.info("AliasTemp : " + aliastemp);
      ObjectMapper jsonMapper = new ObjectMapper();
      try {
        S3Customer obj = jsonMapper.readValue(new File(s3DetailsFile), S3Customer.class);
        for (S3Details alias : obj.getS3List()) {
          if (alias.getCustomerCode().equals(finalJobGroup) && aliastemp.equals(alias.getAlias())) {
            logger.debug("Final Obj to be dispatched for S3 : ");
            logger.debug("BucketName : " + alias.getBucketName());
            logger.debug("AccessKey : " + alias.getAccessKey());
            logger.debug("SecretKey : " + alias.getSecretKey());
            logger.debug("Region : " + alias.getRegion());
            logger.debug("getOutputLocation : " + alias.getOutputLocation());
            logger.debug("FileName : " + exportBean.getFileName());

            S3Config s3Config =
                new S3Config(
                    alias.getBucketName(),
                    alias.getAccessKey(),
                    alias.getSecretKey(),
                    alias.getRegion(),
                    alias.getOutputLocation());

            AmazonS3Handler s3Handler = new AmazonS3Handler(s3Config);
            s3Handler.uploadObject(file.getAbsoluteFile());
          }
        }
      } catch (Exception e) {
        logger.error(e.getMessage());
      }
    }
  }

  public void dispatchReport(
      String analysisId,
      String executionId,
      String analysisType,
      String exportSize,
      ExportBean exportBean,
      String recipients,
      HttpEntity<?> requestEntity,
      String s3,
      String ftp,
      boolean zip,
      String jobGroup,
      RestTemplate restTemplate) {
    String userFileName = exportBean.getFileName();
    AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();
    String url =
        apiExportOtherProperties
            + "/"
            + analysisId
            + "/executions/"
            + executionId
            + "/data?page=1&pageSize="
            + exportSize
            + "&analysisType="
            + analysisType;
    ListenableFuture<ResponseEntity<DataResponse>> responseStringFuture =
        asyncRestTemplate.exchange(url, HttpMethod.GET, requestEntity, DataResponse.class);
    responseStringFuture.addCallback(
        new ListenableFutureCallback<ResponseEntity<DataResponse>>() {
          @Override
          public void onSuccess(ResponseEntity<DataResponse> entity) {

            if (recipients != null && !recipients.equals("")) {
              dispatchMail(exportBean, recipients, entity, zip);
            }

            if (s3 != null && s3 != "") {
              logger.debug("S3 details set. Dispatching to S3");
              dispatchFileToS3(
                  analysisId,
                  executionId,
                  analysisType,
                  exportBean,
                  s3,
                  zip,
                  jobGroup,
                  requestEntity,
                  restTemplate,
                  userFileName);
            }

            if (ftp != null && ftp != "") {
              logger.debug("ftp dispatch started : ");
              dispatchToFtp(
                  analysisId,
                  executionId,
                  analysisType,
                  exportBean,
                  ftp,
                  zip,
                  jobGroup,
                  requestEntity,
                  restTemplate,
                  userFileName);
            }
          }

          @Override
          public void onFailure(Throwable t) {
            logger.error("[Failed] Getting string response:" + t);
          }
        });
  }

  public File createFileforDispatch(String fileName) {
    File file = new File(fileName);
    file.getParentFile().mkdir();

    return file;
  }

  public String generateRandomStringDir() {
    String dir = UUID.randomUUID().toString();
    return dir;
  }

  public void prepareFileWithSize(
      String analysisId,
      String executionId,
      String analysisType,
      String exportSize,
      HttpEntity<?> requestEntity,
      RestTemplate restTemplate,
      ExportBean exportBean) {
    long limitPerPage = Long.parseLong(exportChunkSize);
    long page = 0; // just to keep hold of last not processed data in for loop

    double noOfPages = Math.ceil(Double.parseDouble(exportSize) / limitPerPage);
    boolean flag = true;
    long totalRowCount = 0;

    for (int i = 1; i < noOfPages; i += 1) {
      // get data in pages and keep storing it to file
      // do not use entire exportsize else there will be no data
      // this happens because of memory issues / JVM configuration.
      // This page number will make sure that we process the last bit of info
      page = i;
      // Paginated URL for limitPerPage records till the end of the file.
      String url =
          apiExportOtherProperties
              + "/"
              + analysisId
              + "/executions/"
              + executionId
              + "/data?page="
              + page
              + "&pageSize="
              + limitPerPage
              + "&analysisType="
              + analysisType;
      // we directly get response and start processing this.
      ResponseEntity<DataResponse> entity =
          restTemplate.exchange(url, HttpMethod.GET, requestEntity, DataResponse.class);
      totalRowCount = entity.getBody().getTotalRows();
      if (totalRowCount <= Double.parseDouble(exportSize) && flag) {
        noOfPages = Math.ceil(totalRowCount / limitPerPage);
        flag = false;
      }

      streamResponseToFile(exportBean, limitPerPage, entity);
    }
    // final rows to process
    long leftOutRows = 0;
    if (totalRowCount <= Double.parseDouble(exportSize)) {
      leftOutRows = totalRowCount - page * limitPerPage;
    } else {
      leftOutRows = Long.parseLong(exportSize) - page * limitPerPage;
    }

    // if limits are set in such a way that no of pages becomes zero, then there's just one page
    // to process for entire data.
    // process the remaining page
    page += 1;
    if (leftOutRows > 0) {
      // Paginated URL for limitPerPage records till the end of the file.
      String url =
          apiExportOtherProperties
              + "/"
              + analysisId
              + "/executions/"
              + executionId
              + "/data?page="
              + page
              + "&pageSize="
              + leftOutRows
              + "&analysisType="
              + analysisType;
      // we directly get response and start processing this.
      ResponseEntity<DataResponse> entity =
          restTemplate.exchange(url, HttpMethod.GET, requestEntity, DataResponse.class);

      streamResponseToFile(exportBean, leftOutRows, entity);
    }
  }

  public void dispatchToFtp(
      String analysisId,
      String executionId,
      String analysisType,
      ExportBean bean,
      String finalFtp,
      boolean zip,
      String finalJobGroup,
      HttpEntity<?> requestEntity,
      RestTemplate restTemplate,
      String userFileName) {

    ExportBean exportBean = new ExportBean();
    exportBean = bean;

    // create a directory with unique name in published location to avoid file
    // conflict for dispatch.
    String mailDispatchFileName =
        publishedPath
            + File.separator
            + "ftp"
            + generateRandomStringDir()
            + File.separator
            + userFileName;

    exportBean.setFileName(mailDispatchFileName);

    prepareFileWithSize(
        analysisId,
        executionId,
        analysisType,
        ftpExportSize,
        requestEntity,
        restTemplate,
        exportBean);

    // File dispatched here:
    createZipForFtp(finalFtp, exportBean, finalJobGroup);
  }

  public void createZipForFtp(String finalFtp, ExportBean exportBean, String finalJobGroup) {
    // zip the contents of the file

    File cfile = new File(exportBean.getFileName());
    String zipFileName = cfile.getAbsolutePath().concat(".zip");

    if (finalFtp != null && finalFtp != "") {

      try {

        FileOutputStream fos_zip = new FileOutputStream(zipFileName);
        ZipOutputStream zos = new ZipOutputStream(fos_zip);

        zos.putNextEntry(new ZipEntry(cfile.getName()));

        byte[] readBuffer = new byte[2048];
        int amountRead;
        int written = 0;

        try (FileInputStream inputStream = new FileInputStream(exportBean.getFileName())) {

          while ((amountRead = inputStream.read(readBuffer)) > 0) {
            zos.write(readBuffer, 0, amountRead);
            written += amountRead;
          }

          logger.info("Written " + written + " bytes to " + zipFileName);

        } catch (Exception e) {
          logger.error("Error while writing to zip: " + e.getMessage());
        }

        //            byte[] bytes = Files.readAllBytes(Paths.get(exportBean.getFileName()));
        //            zos.write(bytes, 0, bytes.length);
        zos.closeEntry();
        zos.close();

        // Dispatch the zipped file to ftp
        FtpDispatcher(finalJobGroup, finalFtp, zipFileName, cfile, exportBean.getFileType());

        // close the streams
        zos.close();
        fos_zip.close();

        // deleting the files
        logger.debug("ExportBean.getFileName() - to delete file FTP : " + exportBean.getFileName());
        logger.debug("Deleting exported file.");
        deleteDispatchedFile(exportBean.getFileName());
        deleteDispatchedFile(zipFileName);

      } catch (Exception e) {
        logger.error("ftp error: " + e.getMessage());
      }
    }
  }

  public void FtpDispatcher(
      String finalJobGroup, String finalFtp, String zipFileName, File cfile, String fileType) {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
    LocalDateTime now = LocalDateTime.now();

    for (String aliastemp : finalFtp.split(",")) {
      ObjectMapper jsonMapper = new ObjectMapper();
      try {
        FtpCustomer obj = jsonMapper.readValue(new File(ftpDetailsFile), FtpCustomer.class);
        for (FTPDetails alias : obj.getFtpList()) {
          if (alias.getCustomerName().equals(finalJobGroup) && aliastemp.equals(alias.getAlias())) {
            serviceUtils.uploadToFtp(
                alias.getHost(),
                alias.getPort(),
                alias.getUsername(),
                alias.getPassword(),
                zipFileName,
                alias.getLocation(),
                cfile.getName().substring(0, cfile.getName().lastIndexOf("."))
                    + dtf.format(now).toString()
                    + "."
                    + fileType
                    + ".zip",
                alias.getType());
            logger.debug(
                "Uploaded to ftp alias: " + alias.getCustomerName() + ":" + alias.getHost());
          }
        }
      } catch (IOException e) {
        logger.error(e.getMessage());
      }
    }
  }

  public boolean deleteDispatchedFile(String sourceFile) {
    try {
      serviceUtils.deleteFile(sourceFile, true);
      return true;
    } catch (IOException e) {
      logger.error("Error deleting File : " + sourceFile);
      logger.error(e.getMessage());
      return false;
    }
  }
}
