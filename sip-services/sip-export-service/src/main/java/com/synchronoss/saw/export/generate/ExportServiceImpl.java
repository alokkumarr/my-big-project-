package com.synchronoss.saw.export.generate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.saw.analysis.response.AnalysisResponse;
import com.synchronoss.saw.export.AmazonS3Handler;
import com.synchronoss.saw.export.util.ExportUtils;
import com.synchronoss.saw.export.S3Config;
import com.synchronoss.saw.export.ServiceUtils;
import com.synchronoss.saw.export.distribution.MailSenderUtil;
import com.synchronoss.saw.export.generate.interfaces.ExportService;
import com.synchronoss.saw.export.generate.interfaces.IFileExporter;
import com.synchronoss.saw.export.model.DataResponse;
import com.synchronoss.saw.export.model.S3.S3Customer;
import com.synchronoss.saw.export.model.S3.S3Details;
import com.synchronoss.saw.export.model.ftp.FTPDetails;
import com.synchronoss.saw.export.model.ftp.FtpCustomer;
import com.synchronoss.saw.export.pivot.CreatePivotTable;
import com.synchronoss.saw.export.pivot.ElasticSearchAggregationParser;

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
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletRequest;

import com.synchronoss.saw.model.Field;
import com.synchronoss.saw.model.SipQuery;
import com.synchronoss.sip.utils.RestUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
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

  @Value("${proxy.service.host}")
  private String storageProxyUrl;

  @Value("${metadata.service.host}")
  private String metaDataServiceExport;

  @Autowired
  private ApplicationContext appContext;

  @Autowired
  private ServiceUtils serviceUtils;

  @Autowired
  private RestUtil restUtil;

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
    if ((executionType != null && executionType.equalsIgnoreCase("onetime"))) {
      url =
          storageProxyUrl
              + "/internal/proxy/storage/"
              + analysisId
              + "/executions/data?page=1&pageSize="
              + sizOfExport
              + "&analysisType="
              + analysisType
              + "&executionType="
              + executionType;

    } else if (executionId == null) {
      url =
          storageProxyUrl
              + "/internal/proxy/storage/"
              + analysisId
              + "/lastExecutions/data?page=1&pageSize="
              + sizOfExport
              + "&analysisType="
              + analysisType;
    } else {
      url =
          storageProxyUrl
              + "/internal/proxy/storage/"
              + executionId
              + "/executions/data?page=1&pageSize="
              + sizOfExport
              + "&analysisType="
              + analysisType;
    }
    HttpEntity<?> requestEntity = new HttpEntity<Object>(ExportUtils.setRequestHeader(request));
    AsyncRestTemplate asyncRestTemplate = restUtil.asyncRestTemplate();
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

    // at times we need synchronous processing even in async as becasue of massive parallelism
    // it may halt entire system or may not complete the request
    RestTemplate restTemplate = restUtil.restTemplate();
    Object dispatchBean = request.getBody();

    logger.debug("Dispatch Bean = " + dispatchBean);
    ExportBean exportBean = setExportBeanProps(dispatchBean);
    logger.debug("Export Bean = " + exportBean);

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

      if (((LinkedHashMap) dispatchBean).get("s3") != null) {
        s3 = String.valueOf(((LinkedHashMap) dispatchBean).get("s3"));
        logger.trace("S3 list in reportToBeDispatchedAsync= " + s3);
      }
      if (((LinkedHashMap) dispatchBean).get("zip") != null)
        zip = (Boolean) ((LinkedHashMap) dispatchBean).get("zip");

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
        exportBean.setFileName(((LinkedHashMap) dispatchBean).get("name") + ".csv");
        exportBean.setFileType("csv");
      } else {
        exportBean.setFileName(
            ((LinkedHashMap) dispatchBean).get("name")
                + "."
                + exportBean.getFileType());
      }
    }
    return exportBean;
  }

  public void streamResponseToFile(
      String analysisId, ExportBean exportBean, long limitPerPage, ResponseEntity<DataResponse> entity) {
    try {

      logger.trace("Inside streamResponseToFile");
      File file = new File(exportBean.getFileName());
      file.getParentFile().mkdirs();

      // if the file is found, append the content
      // this is basically for entire for loop to execute correctly on the same file
      // as no two executions are going to have same ID.
      FileOutputStream fos = new FileOutputStream(file, true);
      OutputStreamWriter osw = new OutputStreamWriter(fos);
      String fileType = exportBean.getFileType();

      final SipQuery sipQuery = getSipQuery(analysisId);
      List<Field> fields = sipQuery.getArtifacts().get(0).getFields();
      Map<String, String> columnHeader = ExportUtils.buildColumnHeaderMap(fields);

      logger.debug("Writing to file");
      logger.debug("Data = " + entity.getBody().getData());
      // stream the page output to file.
      if (fileType.equalsIgnoreCase("csv") || fileType == null || fileType.isEmpty()) {
        streamToCSVReport(columnHeader, entity, limitPerPage, exportBean, osw);
        osw.close();
        fos.close();
      } else {
        streamToXlsxReport(fields, entity.getBody(), limitPerPage, exportBean);
      }

    } catch (IOException e) {
      logger.error(
          "Exception occurred while dispatching report :"
              + this.getClass().getName()
              + "  method dataToBeDispatchedAsync()");
    }
  }

  public void streamToCSVReport(
      Map<String, String> columnHeader,
      ResponseEntity<DataResponse> entity,
      long LimittoExport,
      ExportBean exportBean,
      OutputStreamWriter osw) {
    List<Object> data = entity.getBody().getData();

    if (data == null || data.size() == 0) {
      logger.info("No data to export");
      return;
    }

    // clear export bean column header before building csv header
    exportBean.setColumnHeader(null);

    data.stream()
        .limit(LimittoExport)
        .forEach(
            line -> {
              try {
                if (line instanceof LinkedHashMap) {
                  String[] header = null;
                  if (exportBean.getColumnHeader() == null
                      || exportBean.getColumnHeader().length == 0) {
                    Object[] obj;
                    if (columnHeader != null && !columnHeader.isEmpty()) {
                      obj = columnHeader.keySet().toArray();
                    } else {
                      obj = ((LinkedHashMap) line).keySet().toArray();
                    }
                    if (exportBean.getColumnDataType() != null
                        && exportBean.getColumnDataType().length > 0) {
                      header = exportBean.getColumnHeader();
                    } else {
                      header = Arrays.copyOf(obj, obj.length, String[].class);
                    }
                    exportBean.setColumnHeader(header);
                    osw.write(
                        Arrays.stream(header)
                            .map(i -> {
                              String colHeader = columnHeader != null && !columnHeader.isEmpty()
                                  && columnHeader.get(i) != null ? columnHeader.get(i) : i;
                              return "\"" + colHeader + "\"";
                            })
                            .collect(Collectors.joining(",")));
                    osw.write("\n");
                    osw.write(
                        Arrays.stream(exportBean.getColumnHeader())
                            .map(val -> {
                                  if (((LinkedHashMap) line).get(val) == null) {
                                    return "null";
                                  }
                                  return "\"" + ((LinkedHashMap) line).get(val) + "\"";
                                }
                            )
                            .collect(Collectors.joining(",")));
                    osw.write(System.getProperty("line.separator"));
                    logger.debug("Header for csv file: " + header);
                  } else {
                    // ideally we shouldn't be using collectors but it's a single row so it
                    // won't hamper memory consumption
                    osw.write(
                        Arrays.stream(exportBean.getColumnHeader())
                            .map(val -> {
                                  String value;
                                  if (((LinkedHashMap) line).get(val) == null) {
                                    return "null";
                                  }
                                  value = "\"" + ((LinkedHashMap) line).get(val) + "\"";
                                  return value;
                                }
                            )
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
   * @param limitToExport
   * @param exportBean
   * @throws IOException
   */
  public Boolean streamToXlsxReport(
      List<Field> fields, DataResponse response, long limitToExport, ExportBean exportBean) throws IOException {

    List<Object> data = response.getData();
    if (data == null || data.size() == 0) {
      logger.info("No data to export");
      return false;
    }

    File xlsxFile = new File(exportBean.getFileName());
    xlsxFile.getParentFile().mkdir();
    xlsxFile.createNewFile();
    BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(xlsxFile));
    XlsxExporter xlsxExporter = new XlsxExporter();
    Workbook workBook = new SXSSFWorkbook();
    workBook.getSpreadsheetVersion();
    SXSSFSheet sheet = (SXSSFSheet) workBook.createSheet(exportBean.getReportName());
    try {
      xlsxExporter.buildXlsxSheet(fields, exportBean, workBook, sheet, data, limitToExport);
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
    AsyncRestTemplate asyncRestTemplate = restUtil.asyncRestTemplate();
    Object dispatchBean = request.getBody();
    String recipients = null;
    String ftp = null;
    String s3 = null;
    String jobGroup;
    boolean isZipRequired = false;
    ExportBean exportBean = new ExportBean();
    final SipQuery sipQuery = getSipQuery(analysisId);

    // check beforehand if the request is not null
    if (dispatchBean != null && dispatchBean instanceof LinkedHashMap) {
      Object recipientsObj = ((LinkedHashMap) dispatchBean).get("emailList");
      Object ftpObj = ((LinkedHashMap) dispatchBean).get("ftp");
      Object s3Obj = ((LinkedHashMap) dispatchBean).get("s3");
      if (((LinkedHashMap) dispatchBean).get("zip") != null) {
        isZipRequired = (Boolean) ((LinkedHashMap) dispatchBean).get("zip");
      }

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
            storageProxyUrl
                + "/internal/proxy/storage/"
                + executionId
                + "/executions/data";

        ListenableFuture<ResponseEntity<JsonNode>> responseStringFuture =
            asyncRestTemplate.getForEntity(url, JsonNode.class);

        logger.debug("dispatchBean for Pivot: " + dispatchBean.toString());
        String s3bucket = s3;
        String finalRecipients = recipients;
        String finalFtp = ftp;
        String finalJobGroup = jobGroup;
        boolean isZip = isZipRequired;
        responseStringFuture.addCallback(
            new ListenableFutureCallback<ResponseEntity<JsonNode>>() {
              @Override
              public void onSuccess(ResponseEntity<JsonNode> entity) {
                JsonNode jsonDataNode = entity.getBody().get("data");
                IFileExporter iFileExporter = new XlsxExporter();

                // build export bean to process file
                ExportUtils.buildExportBean(exportBean, dispatchBean);
                String dir = UUID.randomUUID().toString();
                exportBean.setFileName(
                    publishedPath
                        + File.separator
                        + dir
                        + File.separator
                        + ((LinkedHashMap) dispatchBean).get("name")
                        + "."
                        + exportBean.getFileType());

                try {
                  // create a directory with unique name in published location to avoid file
                  // conflict for dispatch.
                  File file = new File(exportBean.getFileName());
                  file.getParentFile().mkdir();

                  List<Field> fieldList = getPivotFields(sipQuery);
                  ElasticSearchAggregationParser responseParser =
                      new ElasticSearchAggregationParser(fieldList);
                  responseParser.setColumnDataType(exportBean);

                  List<Object> dataObj = responseParser.parsePivotData(jsonDataNode);
                  logger.trace("Parse data for workbook writing : " + dataObj);
                  logger.trace("Data size = " + dataObj.size());

                  Workbook workbook = iFileExporter.getWorkBook(exportBean, dataObj);
                  logger.debug("workbook created with DSL : " + workbook);
                  CreatePivotTable createPivotTable = new CreatePivotTable();
                  createPivotTable.createPivot(workbook, file, fieldList);
                  if (finalRecipients != null && !finalRecipients.equals("")) {
                    logger.debug(
                        "In Email dispatcher: [Success] Response :" + entity.getStatusCode());
                    dispatchMailForPivot(exportBean, finalRecipients, entity, isZip);
                  }
                } catch (IOException e) {
                  logger.error(
                      "Exception occurred while dispatching pivot :"
                          + this.getClass().getName()
                          + "  method dataToBeDispatchedAsync()");
                }

                logger.debug("S3 details = " + s3bucket);
                if (s3bucket != null && s3bucket != "") {
                  logger.debug("S3 details set. Dispatching to S3");
                  s3DispatcherPivot(s3bucket, finalJobGroup, exportBean, isZip);
                }
                logger.debug("Deleting exported file11.");
                ExportUtils.deleteDispatchedFile(exportBean.getFileName(), serviceUtils);
                if (isZip) {
                  File file = new File(exportBean.getFileName());
                  String zipFileName = file.getAbsolutePath().concat(".zip");
                  ExportUtils.deleteDispatchedFile(zipFileName, serviceUtils);
                }
                logger.debug("ftp details = " + finalFtp);
                if (finalFtp != null && finalFtp != "") {
                  logger.debug("FTP details set. Dispatching to FTP");
                  ftpDispatcherPivot(
                      executionId,
                      finalFtp,
                      asyncRestTemplate,
                      dispatchBean,
                      finalJobGroup,
                      sipQuery,
                      isZip);
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
      String executionId,
      String ftp,
      AsyncRestTemplate asyncRestTemplate,
      Object dispatchBean,
      String jobGroup,
      SipQuery sipQuery,
      boolean isZip) {
    if (ftp != null && !ftp.equals("")) {
      String url =
          storageProxyUrl
              + "/internal/proxy/storage/"
              + executionId
              + "/executions/data?page=1&pageSize="
              + ftpExportSize;
      ListenableFuture<ResponseEntity<JsonNode>> responseStringFuture =
          asyncRestTemplate.getForEntity(url, JsonNode.class);

      logger.debug("dispatchBean for Pivot: " + dispatchBean.toString());
      String finalFtp = ftp;
      String finalJobGroup = jobGroup;
      responseStringFuture.addCallback(
          new ListenableFutureCallback<ResponseEntity<JsonNode>>() {
            @Override
            public void onSuccess(ResponseEntity<JsonNode> entity) {
              JsonNode jsonNode = entity.getBody().get("data");
              logger.debug("In FTP dispatcher: [Success] Response :" + entity.getStatusCode());

              IFileExporter iFileExporter = new XlsxExporter();
              ExportBean exportBean = new ExportBean();

              // build export bean to process file
              ExportUtils.buildExportBean(exportBean, dispatchBean);
              String dir = UUID.randomUUID().toString();
              exportBean.setFileName(
                  publishedPath
                      + File.separator
                      + dir
                      + File.separator
                      + ((LinkedHashMap) dispatchBean).get("name")
                      + "."
                      + exportBean.getFileType());

              File cfile = new File(exportBean.getFileName());
              String fileName = cfile.getAbsolutePath();
              try {
                // create a directory with unique name in published location to avoid file conflict
                // for dispatch.
                File file = new File(exportBean.getFileName());
                file.getParentFile().mkdir();

                List<Field> fieldList = getPivotFields(sipQuery);
                ElasticSearchAggregationParser responseParser =
                    new ElasticSearchAggregationParser(fieldList);
                responseParser.setColumnDataType(exportBean);

                List<Object> dataObj = responseParser.parsePivotData(jsonNode);
                logger.trace("Parse data for workbook writing : " + dataObj);
                logger.trace("Data size = " + dataObj.size());

                Workbook workbook = iFileExporter.getWorkBook(exportBean, dataObj);
                logger.debug("workbook successfully with DSL" + workbook);
                CreatePivotTable createPivotTable = new CreatePivotTable();
                createPivotTable.createPivot(workbook, file, fieldList);

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
                LocalDateTime now = LocalDateTime.now();

                try {

                  if (isZip) {
                    fileName = fileName.concat(".zip");
                    FileOutputStream fos = new FileOutputStream(fileName);
                    ZipOutputStream zos = new ZipOutputStream(fos);
                    zos.putNextEntry(new ZipEntry(cfile.getName()));
                    byte[] bytes = Files.readAllBytes(Paths.get(exportBean.getFileName()));
                    zos.write(bytes, 0, bytes.length);
                    zos.closeEntry();
                    zos.close();
                  }

                  logger.debug("ftp servers: " + finalFtp);

                  for (String tempAlias : finalFtp.split(",")) {
                    ObjectMapper jsonMapper = new ObjectMapper();
                    try {
                      FtpCustomer obj = jsonMapper.readValue(new File(ftpDetailsFile), FtpCustomer.class);
                      for (FTPDetails alias : obj.getFtpList()) {
                        logger.debug("Processing Host: " + alias.getHost());
                        logger.debug("jobGroup: " + alias.getCustomerName());
                        logger.debug("Alias: " + tempAlias.equals(alias.getAlias()));

                        if (alias.getCustomerName().equals(finalJobGroup) && tempAlias.equals(alias.getAlias())) {
                          logger.debug("Inside If");

                          String destinationFileName = file.getName().substring(0, cfile.getName().lastIndexOf(".") + 1)
                              + dtf.format(now).concat(".").concat(exportBean.getFileType());
                          if (isZip) {
                            destinationFileName = destinationFileName.concat(".zip");
                          }

                          serviceUtils.uploadToFtp(
                              alias.getHost(),
                              alias.getPort(),
                              alias.getUsername(),
                              alias.getPassword(),
                              fileName,
                              alias.getLocation(),
                              destinationFileName,
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

                ExportUtils.deleteDispatchedFile(exportBean.getFileName(), serviceUtils);
                ExportUtils.deleteDispatchedFile(fileName, serviceUtils);
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
      String s3,
      String jobGroup,
      ExportBean exportBean,
      boolean isZip) {
    logger.info("Inside S3 dispatch Pivot");
    String finalS3 = s3;
    String finalJobGroup = jobGroup;
    File file = new File(exportBean.getFileName());
    if (isZip) {
      logger.debug("S3 - zip = true!!");
      try {
        String zipFileName = ExportUtils.buildZipFile(exportBean, file);
        s3DispatchExecutor(finalS3, finalJobGroup, new File(zipFileName), exportBean);
        logger.debug("ExportBean.getFileName() - to delete in S3 : " + exportBean.getFileName());
        logger.debug("ExportBean.getFileName() - to delete in S3 : " + zipFileName);

      } catch (Exception e) {
        logger.error("Error writing to zip!!");
      }
    } else {
      s3DispatchExecutor(finalS3, finalJobGroup, file, exportBean);
      logger.debug("ExportBean.getFileName() - to delete in S3 : " + exportBean.getFileName());
    }
  }

  @Override
  public List<String> listFtpsForCustomer(RequestEntity request) {
    Object dispatchBean = request.getBody();
    // this job group is customer unique identifier
    String jobGroup;
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
      String analysisId, ExportBean exportBean, String recipients, ResponseEntity<DataResponse> entity, boolean zip, String fileName) {

    String fileType = exportBean.getFileType();
    MailSenderUtil MailSender = new MailSenderUtil(appContext.getBean(JavaMailSender.class));

    logger.debug("Email async success");
    logger.debug("[Success] Response :" + entity.getStatusCode());

    final SipQuery sipQuery = getSipQuery(analysisId);
    List<Field> fields = sipQuery.getArtifacts().get(0).getFields();
    Map<String, String> columnHeader = ExportUtils.buildColumnHeaderMap(fields);

    try {
      // create a directory with unique name in published location to avoid file
      // conflict for dispatch.

      final String mailDispatchFileName = filePath("mail", fileName);
      exportBean.setFileName(mailDispatchFileName);

      if (fileType.equalsIgnoreCase("csv") || fileType == null || fileType.isEmpty()) {
        File file = ExportUtils.buildDispatchFile(mailDispatchFileName);

        FileOutputStream fos = new FileOutputStream(file);
        OutputStreamWriter osw = new OutputStreamWriter(fos);

        streamToCSVReport(columnHeader, entity, Long.parseLong(emailExportSize), exportBean, osw);
        osw.close();
        fos.close();
      } else {
        streamToXlsxReport(fields, entity.getBody(), Long.parseLong(emailExportSize), exportBean);
      }

      File file = new File(exportBean.getFileName());
      if (zip) {
        String zipFileName = ExportUtils.buildZipFile(exportBean, file);
        MailSender.sendMail(
            recipients,
            exportBean.getReportName() + " | " + exportBean.getPublishDate(),
            serviceUtils.prepareMailBody(exportBean, mailBody),
            zipFileName);
        logger.info("Email sent successfully");

        logger.debug("Deleting exported file.");
        try {
          logger.debug(
              "ExportBean.getFileName() to delete -  mail : "
                  + zipFileName
                  + ", "
                  + exportBean.getFileName());

          ExportUtils.deleteDispatchedFile(zipFileName, serviceUtils);
          ExportUtils.deleteDispatchedFile(exportBean.getFileName(), serviceUtils);
        } catch (Exception e) {
          logger.error(e.getMessage());
        }
      } else {
        MailSender.sendMail(
            recipients,
            exportBean.getReportName() + " | " + exportBean.getPublishDate(),
            serviceUtils.prepareMailBody(exportBean, mailBody),
            exportBean.getFileName());
        logger.info("Email sent successfully");

        logger.debug("Deleting exported file.");
        try {
          logger.debug("ExportBean.getFileName() to delete -  mail : " + exportBean.getFileName());
          ExportUtils.deleteDispatchedFile(exportBean.getFileName(), serviceUtils);
        } catch (Exception e) {
          logger.error(e.getMessage());
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
    ExportBean exportBean = bean;

    // create a directory with unique name in published location to avoid file
    // conflict for dispatch.
    final String s3FileName = filePath("s3", userFileName);
    exportBean.setFileName(s3FileName);
    logger.debug("Export Bean = " + exportBean);

    prepareFileWithSize(
        analysisId,
        executionId,
        analysisType,
        s3ExportSize,
        requestEntity,
        restTemplate,
        exportBean);

    File cfile = new File(exportBean.getFileName());
    logger.debug("Final S3 = " + finalS3);

    if (zip) {

      logger.debug("S3 - zip = true!!");
      try {
        String zipFileName = ExportUtils.buildZipFile(exportBean, cfile);
        s3DispatchExecutor(finalS3, finalJobGroup, new File(zipFileName), exportBean);

        logger.debug("ExportBean.getFileName() - to delete in S3 : " + exportBean.getFileName());
        ExportUtils.deleteDispatchedFile(exportBean.getFileName(), serviceUtils);
        ExportUtils.deleteDispatchedFile(zipFileName, serviceUtils);
        logger.debug("ExportBean.getFileName() - to delete in S3 : " + zipFileName);

      } catch (Exception e) {
        logger.error("Error writing to zip!!");
      }
    } else {
      s3DispatchExecutor(finalS3, finalJobGroup, cfile, exportBean);
      logger.debug("ExportBean.getFileName() - to delete in S3 : " + exportBean.getFileName());
      ExportUtils.deleteDispatchedFile(exportBean.getFileName(), serviceUtils);
    }
  }

  public void s3DispatchExecutor(
      String finalS3, String finalJobGroup, File file, ExportBean exportBean) {
    for (String aliasTemp : finalS3.split(",")) {
      logger.info("AliasTemp : " + aliasTemp);
      ObjectMapper jsonMapper = new ObjectMapper();
      try {
        S3Customer obj = jsonMapper.readValue(new File(s3DetailsFile), S3Customer.class);
        for (S3Details alias : obj.getS3List()) {
          if (alias.getCustomerCode().equals(finalJobGroup) && aliasTemp.equals(alias.getAlias())) {
            logger.debug("Final Obj to be dispatched for S3 : ");
            logger.debug("BucketName : " + alias.getBucketName());
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
    AsyncRestTemplate asyncRestTemplate = restUtil.asyncRestTemplate();

    String url =
        storageProxyUrl
            + "/internal/proxy/storage/"
            + executionId
            + "/executions/data?page=1&pageSize="
            + exportSize
            + "&executionType=scheduled"
            + "&analysisType="
            + analysisType;
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

    if (recipients != null && !recipients.equals("")) {
      ListenableFuture<ResponseEntity<DataResponse>> responseStringFuture =
          asyncRestTemplate.getForEntity(url, DataResponse.class);
      responseStringFuture.addCallback(
          new ListenableFutureCallback<ResponseEntity<DataResponse>>() {
            @Override
            public void onSuccess(ResponseEntity<DataResponse> entity) {
              dispatchMail(analysisId, exportBean, recipients, entity, zip, userFileName);
            }

            @Override
            public void onFailure(Throwable t) {
              logger.error("[Failed] Getting string response:" + t);
            }
          });
    }
  }

  public void prepareFileWithSize(
      String analysisId,
      String executionId,
      String analysisType,
      String exportSize,
      HttpEntity<?> requestEntity,
      RestTemplate restTemplate,
      ExportBean exportBean) {
    logger.debug("Preparing file with size = " + exportSize);
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
          storageProxyUrl
              + "/internal/proxy/storage/"
              + executionId
              + "/executions/data?page="
              + page
              + "&pageSize="
              + limitPerPage
              + "&analysisType="
              + analysisType
              + "&analysisId="
              + analysisId;

      logger.debug("URL = " + url);

      // we directly get response and start processing this.
      ResponseEntity<DataResponse> entity =
          restTemplate.exchange(url, HttpMethod.GET, requestEntity, DataResponse.class);
      logger.debug("Response = " + entity.getBody());

      totalRowCount = entity.getBody().getTotalRows();
      logger.debug("Total row count = " + totalRowCount);

      if (totalRowCount <= Double.parseDouble(exportSize) && flag) {
        noOfPages = Math.ceil(totalRowCount / limitPerPage);
        flag = false;
      }

      streamResponseToFile(analysisId, exportBean, limitPerPage, entity);
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
          storageProxyUrl
              + "/internal/proxy/storage/"
              + executionId
              + "/executions/data?page="
              + page
              + "&pageSize="
              + leftOutRows
              + "&analysisType="
              + analysisType
              + "&analysisId="
              + analysisId;

      // we directly get response and start processing this.
      ResponseEntity<DataResponse> entity =
          restTemplate.exchange(url, HttpMethod.GET, requestEntity, DataResponse.class);
      logger.debug("Execution response = " + entity);

      streamResponseToFile(analysisId, exportBean, leftOutRows, entity);
      logger.debug("File created");
    }
  }

  /**
   * Dispatch files to FTP
   *
   * @param analysisId
   * @param executionId
   * @param analysisType
   * @param exportBean
   * @param finalFtp
   * @param zip
   * @param finalJobGroup
   * @param requestEntity
   * @param restTemplate
   * @param userFileName
   */
  public void dispatchToFtp(
      String analysisId,
      String executionId,
      String analysisType,
      ExportBean exportBean,
      String finalFtp,
      boolean zip,
      String finalJobGroup,
      HttpEntity<?> requestEntity,
      RestTemplate restTemplate,
      String userFileName) {

    // create a directory with unique name in published location to avoid file
    // conflict for dispatch.
    final String mailDispatchFileName = filePath("ftp", userFileName);
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
    createZipForFtp(finalFtp, exportBean, finalJobGroup, zip);
  }

  /**
   * Build zip file if zip was selected from GUI.
   *
   * @param finalFtp
   * @param exportBean
   * @param finalJobGroup
   * @param zip
   */
  public void createZipForFtp(String finalFtp, ExportBean exportBean, String finalJobGroup, boolean zip) {
    // zip the contents of the file

    if (finalFtp != null && finalFtp != "") {
      try {
        File file = new File(exportBean.getFileName());
        String fileName = file.getAbsolutePath();
        if (zip) {
          fileName = ExportUtils.buildZipFile(exportBean, file);
        }

        // Dispatch the zipped file to ftp
        FtpDispatcher(finalJobGroup, finalFtp, fileName, zip, file, exportBean.getFileType());

        // deleting the files
        logger.debug("ExportBean.getFileName() - to delete file FTP : " + exportBean.getFileName());
        logger.debug("Deleting exported file.");
        ExportUtils.deleteDispatchedFile(exportBean.getFileName(), serviceUtils);
        ExportUtils.deleteDispatchedFile(fileName, serviceUtils);
      } catch (Exception e) {
        logger.error("ftp error: " + e.getMessage());
      }
    }
  }

  public void FtpDispatcher(
      String finalJobGroup, String finalFtp, String fileName, boolean zip, File cfile, String fileType) {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
    LocalDateTime now = LocalDateTime.now();

    for (String aliasTemp : finalFtp.split(",")) {
      ObjectMapper jsonMapper = new ObjectMapper();
      try {
        String destinationFileName;
        String tempFileName = cfile.getName().substring(0, cfile.getName().lastIndexOf("."));
        if (zip) {
          destinationFileName = tempFileName + dtf.format(now) + "." + fileType + ".zip";
        } else {
          destinationFileName = tempFileName + dtf.format(now) + "." + fileType;
        }

        FtpCustomer obj = jsonMapper.readValue(new File(ftpDetailsFile), FtpCustomer.class);
        for (FTPDetails alias : obj.getFtpList()) {
          if (alias.getCustomerName().equals(finalJobGroup) && aliasTemp.equals(alias.getAlias())) {
            serviceUtils.uploadToFtp(
                alias.getHost(),
                alias.getPort(),
                alias.getUsername(),
                alias.getPassword(),
                fileName,
                alias.getLocation(),
                destinationFileName,
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

  /**
   * This will fetch the SIP query from metadata and provide.
   *
   * @param analysisId
   * @return SipQuery
   */
  public SipQuery getSipQuery(String analysisId) {
    RestTemplate restTemplate = restUtil.restTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    String url = metaDataServiceExport + "/dslanalysis/" + analysisId;
    logger.debug("SIP query url for analysis fetch : " + url);
    AnalysisResponse analysisResponse = restTemplate.getForObject(url, AnalysisResponse.class);
    SipQuery sipQuery = analysisResponse.getAnalysis().getSipQuery();

    logger.debug("Fetched SIP query for analysis : " + sipQuery.toString());
    return sipQuery;
  }

  public void dispatchMailForPivot(
      ExportBean bean, String recipients, ResponseEntity<JsonNode> entity, boolean isZip) {
    ExportBean exportBean = bean;
    MailSenderUtil MailSender = new MailSenderUtil(appContext.getBean(JavaMailSender.class));
    try {
      File file = new File(exportBean.getFileName());
      if (isZip) {
        logger.debug("Pivot - zip = true!!");

        String zipFileName = ExportUtils.buildZipFile(exportBean, file);
        MailSender.sendMail(
            recipients,
            exportBean.getReportName() + " | " + exportBean.getPublishDate(),
            serviceUtils.prepareMailBody(exportBean, mailBody),
            zipFileName);
        logger.debug("Email sent successfully");
      } else {
        MailSender.sendMail(
            recipients,
            exportBean.getReportName() + " | " + exportBean.getPublishDate(),
            serviceUtils.prepareMailBody(exportBean, mailBody),
            exportBean.getFileName());
        logger.debug("Email sent successfully");
      }

    } catch (Exception e) {
      logger.error("Error sending mail" + e.getMessage() + ":::" + e.getStackTrace());
    }
  }

  /**
   * This method to organize the pivot table structure
   *
   * @param sipQuery
   * @return
   */
  private List<Field> getPivotFields(SipQuery sipQuery) {
    List<Field> queryFields = sipQuery.getArtifacts().get(0).getFields();
    List<Field> fieldList = new ArrayList<>();
    // set first row fields
    for (Field field : queryFields) {
      if (field != null && "row".equalsIgnoreCase(field.getArea())) {
        fieldList.add(field);
      }
    }
    // set column fields
    for (Field field : queryFields) {
      if (field != null && "column".equalsIgnoreCase(field.getArea())) {
        fieldList.add(field);
      }
    }
    // set data fields
    for (Field field : queryFields) {
      if (field != null && "data".equalsIgnoreCase(field.getArea())) {
        fieldList.add(field);
      }
    }
    return fieldList;
  }

  private String filePath(String type, String fileName) {
    return publishedPath + File.separator + type
        + ExportUtils.generateRandomStringDir() + File.separator + fileName;
  }
}