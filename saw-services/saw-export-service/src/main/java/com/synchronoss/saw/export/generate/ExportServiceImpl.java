package com.synchronoss.saw.export.generate;

import java.io.*;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.saw.export.ServiceUtils;
import com.synchronoss.saw.export.distribution.MailSenderUtil;
import com.synchronoss.saw.export.generate.interfaces.IFileExporter;
import com.synchronoss.saw.export.model.AnalysisMetaData;
import com.synchronoss.saw.export.model.ftp.FTPDetails;
import com.synchronoss.saw.export.model.ftp.FtpCustomer;
import com.synchronoss.saw.export.pivot.CreatePivotTable;
import com.synchronoss.saw.export.pivot.ElasticSearchAggeragationParser;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ExportServiceImpl implements ExportService{

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

  @Value("${published.path}")
  private String publishedPath;

  @Value("${spring.mail.body}")
  private String mailBody;

  @Value("${ftp.details.file}")
  private String ftpDetailsFile;

  @Value("${exportChunkSize}")
  private String exportChunkSize;

  @Autowired
  private ApplicationContext appContext;

  @Autowired
  private ServiceUtils serviceUtils;

  @Override
  public DataResponse dataToBeExportedSync(String executionId, HttpServletRequest request,String analysisId) throws JSONValidationSAWException {
    HttpEntity<?> requestEntity = new HttpEntity<Object>(setRequestHeader(request));
    RestTemplate restTemplate = new RestTemplate();
    // During report extraction time, this parameter will not be passed.
    // Hence we should use uiExportSize configuration parameter.
    String sizOfExport;
    sizOfExport = ((sizOfExport = request.getParameter("pageSize"))!=null) ? sizOfExport : uiExportSize;
    String url = apiExportOtherProperties+"/" + executionId +"/executions/"+analysisId+"/data?page=1&pageSize="+ sizOfExport
        +"&analysisType=report";
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
  public ListenableFuture<ResponseEntity<DataResponse>> dataToBeExportedAsync(String executionId,
                                                                               HttpServletRequest request, String analysisId, String analysisType, String executionType) {
    // During report extraction time, this parameter will not be passed.
    // Hence we should use uiExportSize configuration parameter.
    String sizOfExport;
    String url;
      sizOfExport = ((sizOfExport = request.getParameter("pageSize"))!=null) ? sizOfExport : uiExportSize;
    if (executionType!=null && !executionType.isEmpty() && executionType.equalsIgnoreCase("onetime"))
       url = apiExportOtherProperties+"/" + executionId +"/executions/"+analysisId+"/data?page=1&pageSize="+sizOfExport+"&analysisType=" + analysisType+"&executionType=onetime";
    else
        url = apiExportOtherProperties+"/" + executionId +"/executions/"+analysisId+"/data?page=1&pageSize="+sizOfExport+"&analysisType=" + analysisType;
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
  public void reportToBeDispatchedAsync(String executionId, RequestEntity request,
      String analysisId, String analysisType) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    HttpEntity<?> requestEntity = new HttpEntity<Object>(request.getHeaders());
    AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();
    // at times we need synchronous processing even in async as becasue of massive parallelism
    // it may halt entire system or may not complete the request
    RestTemplate restTemplate = new RestTemplate();
    Object dispatchBean = request.getBody();
    String recipients = null;
    String ftp = null;
    String jobGroup = null;

    // presetting the variables, as their presence will determine which URLs to process
    if (dispatchBean != null && dispatchBean instanceof LinkedHashMap) {
      recipients = String.valueOf(((LinkedHashMap) dispatchBean).get("emailList"));
      ftp = String.valueOf(((LinkedHashMap) dispatchBean).get("ftp"));
      jobGroup = String.valueOf(((LinkedHashMap) dispatchBean).get("jobGroup"));
      if(recipients!=null && !recipients.equals("") && recipients.contains("@")) {
          logger.trace("Recipients: " +recipients);
        String url = apiExportOtherProperties+"/" + analysisId +"/executions/"+executionId+"/data?page=1&pageSize="
            +emailExportSize+"&analysisType="+ analysisType;
        ListenableFuture<ResponseEntity<DataResponse>> responseStringFuture = asyncRestTemplate.exchange(url, HttpMethod.GET,
            requestEntity, DataResponse.class);
        String finalRecipients = recipients;
        responseStringFuture.addCallback(new ListenableFutureCallback<ResponseEntity<DataResponse>>() {
          @Override
          public void onSuccess(ResponseEntity<DataResponse> entity) {
            logger.debug("Email async success");
            logger.debug("[Success] Response :" + entity.getStatusCode());
            IFileExporter iFileExporter = new CSVReportDataExporter();
            ExportBean exportBean = new ExportBean();
            String dir = UUID.randomUUID().toString();
            MailSenderUtil MailSender = new MailSenderUtil(appContext.getBean(JavaMailSender.class));
            exportBean.setFileName(publishedPath + File.separator + dir + File.separator + String.valueOf(((LinkedHashMap)
                dispatchBean).get("name")) + "." + ((LinkedHashMap) dispatchBean).get("fileType"));
            exportBean.setReportDesc(String.valueOf(((LinkedHashMap) dispatchBean).get("description")));
            exportBean.setReportName(String.valueOf(((LinkedHashMap) dispatchBean).get("name")));
            exportBean.setPublishDate(String.valueOf(((LinkedHashMap) dispatchBean).get("publishedTime")));
            exportBean.setCreatedBy(String.valueOf(((LinkedHashMap) dispatchBean).get("userFullName")));
            try {
              // create a directory with unique name in published location to avoid file conflict for dispatch.
              File file = new File(exportBean.getFileName());
              file.getParentFile().mkdir();

              FileOutputStream fos = new FileOutputStream(file);
              OutputStreamWriter osw = new OutputStreamWriter(fos);
              streamToCSVReport(entity, Long.parseLong(emailExportSize), exportBean, osw);

              osw.close();
              fos.close();

              MailSender.sendMail(finalRecipients, exportBean.getReportName() + " | " +
                      exportBean.getPublishDate(), serviceUtils.prepareMailBody(exportBean, mailBody),
                  exportBean.getFileName());
              logger.debug("Email sent successfully");

              logger.debug("Deleting exported file.");
              serviceUtils.deleteFile(exportBean.getFileName(), true);

            } catch (IOException e) {
              logger.error(
                  "Exception occurred while dispatching report :" + this.getClass().getName()
                      + "  method dataToBeDispatchedAsync()");
            }
          }
          @Override
          public void onFailure(Throwable t) {
            logger.error("[Failed] Getting string response:" + t);
          }
        });
      }

      // this export is synchronous
      if (ftp!=null && ftp != "") {

        // Do the background work beforehand
        String finalFtp = ftp;
        String finalJobGroup = jobGroup;
        ExportBean exportBean = new ExportBean();
        String dir = UUID.randomUUID().toString();
        exportBean.setFileName(publishedPath + File.separator + dir + File.separator + String.valueOf(((LinkedHashMap)
            dispatchBean).get("name")) + "."
            +(((LinkedHashMap) dispatchBean).get("fileType") !=null ? ((LinkedHashMap) dispatchBean).get("fileType") : ".csv"));
        exportBean.setReportDesc(String.valueOf(((LinkedHashMap) dispatchBean).get("description")));
        exportBean.setReportName(String.valueOf(((LinkedHashMap) dispatchBean).get("name")));
        exportBean.setPublishDate(String.valueOf(((LinkedHashMap) dispatchBean).get("publishedTime")));
        exportBean.setCreatedBy(String.valueOf(((LinkedHashMap) dispatchBean).get("userFullName")));

        long limitPerPage = Long.parseLong(exportChunkSize);
        long page = 0; // just to keep hold of last not processed data in for loop

        double noOfPages = Math.ceil(Double.parseDouble(ftpExportSize)/limitPerPage);
        boolean flag =true;
          long totalRowCount =0;

        for (int i = 1; i < noOfPages; i+=1) {
          // get data in pages and keep storing it to file
          // do not use entire ftpexportsize else there will be no data
          // this happens because of memory issues / JVM configuration.
          // This page number will make sure that we process the last bit of info
          page = i;
          // Paginated URL for limitPerPage records till the end of the file.
          String url = apiExportOtherProperties+"/" + analysisId +"/executions/"+ executionId +"/data?page="+ page
              +"&pageSize="
              + limitPerPage +"&analysisType=" + analysisType;
          // we directly get response and start processing this.
          ResponseEntity<DataResponse> entity = restTemplate.exchange(url, HttpMethod.GET,
              requestEntity, DataResponse.class);
            totalRowCount  = entity.getBody().getTotalRows();
            if (totalRowCount <= Double.parseDouble(ftpExportSize) && flag) {
                noOfPages = Math.ceil(totalRowCount / limitPerPage);
                flag =false;
            }
          streamResponseToFile(exportBean, limitPerPage, entity);

        }
        // final rows to process
          long leftOutRows =0 ;
          if (totalRowCount <= Double.parseDouble(ftpExportSize)) {
              leftOutRows = totalRowCount - page * limitPerPage;
          }else {
              leftOutRows = Long.parseLong(ftpExportSize) - page * limitPerPage;
          }

        // if limits are set in such a way that no of pages becomes zero, then there's just one page to process for entire data.
        // process the remaining page
        page+=1;
        if (leftOutRows > 0) {
          // Paginated URL for limitPerPage records till the end of the file.
          String url = apiExportOtherProperties+"/" + analysisId +"/executions/"+ executionId +"/data?page="+ page
              +"&pageSize="
              + leftOutRows +"&analysisType="+analysisType;
          // we directly get response and start processing this.
          ResponseEntity<DataResponse> entity = restTemplate.exchange(url, HttpMethod.GET,
              requestEntity, DataResponse.class);

          streamResponseToFile(exportBean, leftOutRows, entity);
        }

        // zip the contents of the file
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
        LocalDateTime now = LocalDateTime.now();

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
              logger.error("Error while writing to zip: "+ e.getMessage());
            }

//            byte[] bytes = Files.readAllBytes(Paths.get(exportBean.getFileName()));
//            zos.write(bytes, 0, bytes.length);
            zos.closeEntry();
            zos.close();

            for (String aliastemp : finalFtp.split(",")) {
              ObjectMapper jsonMapper = new ObjectMapper();
              try {
                FtpCustomer obj = jsonMapper.readValue(new File(ftpDetailsFile), FtpCustomer.class);
                for (FTPDetails alias:obj.getFtpList()) {
                  if (alias.getCustomerName().equals(finalJobGroup) && aliastemp.equals(alias.getAlias())) {
                    serviceUtils.uploadToFtp(alias.getHost(),
                        alias.getPort(),
                        alias.getUsername(),
                        alias.getPassword(),
                        zipFileName,
                        alias.getLocation(),
                        cfile.getName().substring(0, cfile.getName().lastIndexOf(".")) + dtf.format(now).toString() + "." + ((LinkedHashMap) dispatchBean).get("fileType") + ".zip",
                        alias.getType());
                    logger.debug("Uploaded to ftp alias: "+alias.getCustomerName()+":"+alias.getHost());
                  }
                }
              } catch (IOException e) {
                logger.error(e.getMessage());
              }
            }

            // deleting the files
            logger.debug("Deleting exported file.");
            serviceUtils.deleteFile(exportBean.getFileName(),true);
            serviceUtils.deleteFile(zipFileName,true);

            // close the streams
            zos.close();
            fos_zip.close();

          } catch (Exception e) {
            logger.error("ftp error: "+e.getMessage());
          }
        }
      }
    }
  }

  private void streamResponseToFile(ExportBean exportBean, long limitPerPage,
      ResponseEntity<DataResponse> entity) {
    try {
      // create a directory with unique name in published location to avoid file conflict for dispatch.
      File file = new File(exportBean.getFileName());
      file.getParentFile().mkdirs();

      // if the file is found, append the content
      // this is basically for entire for loop to execute correctly on the same file
      // as no two executions are going to have same ID.
      FileOutputStream fos = new FileOutputStream(file, true);
      OutputStreamWriter osw = new OutputStreamWriter(fos);

      // stream the page output to file.
      streamToCSVReport(entity, limitPerPage, exportBean, osw);

      osw.close();
      fos.close();


    } catch (IOException e) {
      logger.error("Exception occurred while dispatching report :" + this.getClass().getName()+ "  method dataToBeDispatchedAsync()");
    }
  }


  private void streamToCSVReport(ResponseEntity<DataResponse> entity, long LimittoExport, ExportBean exportBean,
      OutputStreamWriter osw) {
    entity.getBody().getData()
        .stream()
        .limit(LimittoExport)
        .forEach(
        line -> {
          try {
            if( line instanceof LinkedHashMap) {
              String[] header = null;
              if (exportBean.getColumnHeader()==null || exportBean.getColumnHeader().length==0) {
                Object[] obj = ((LinkedHashMap) line).keySet().toArray();
                header = Arrays.copyOf(obj, obj.length, String[].class);
                exportBean.setColumnHeader(header);
                osw.write(Arrays.stream(header).map(i -> "\""+ i + "\"")
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
            logger.error("ERROR_PROCESSING_STREAM: "+e.getMessage());
          }
        }
    );
  }

    /**
     *
     * @param entity
     * @param LimittoExport
     * @param exportBean
     * @throws IOException
     */
    public void streamToXslxReport(ResponseEntity<DataResponse> entity, long LimittoExport, ExportBean exportBean) throws IOException {

        BufferedOutputStream stream = null;
        File xlsxFile = null;
        xlsxFile = new File(exportBean.getFileName());
        xlsxFile.createNewFile();
        stream = new BufferedOutputStream(new FileOutputStream(xlsxFile));
        XlsxExporter xlsxExporter = new XlsxExporter();
        Workbook workBook = new XSSFWorkbook();
        workBook.getSpreadsheetVersion();
        XSSFSheet sheet = (XSSFSheet) workBook.createSheet(exportBean.getReportName());

        entity.getBody().getData()
            .stream()
            .limit(LimittoExport)
            .forEach(
                line -> {
                    try {
                        xlsxExporter.addxlsxRow(exportBean, workBook, sheet, line);

                    } catch (Exception e) {
                        logger.error("ERROR_Adding_Rows: " + e.getMessage());
                    }
                }
            );

        workBook.write(stream);
        stream.flush();
        stream.close();
    }

  @Override
  @Async
  public void pivotToBeDispatchedAsync(String executionId, RequestEntity request, String analysisId) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    HttpEntity<?> requestEntity = new HttpEntity<Object>(request.getHeaders());
    AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();
    Object dispatchBean = request.getBody();
    String recipients = null;
    String ftp = null;
    String jobGroup = null;

    // check beforehand if the request is not null
    if (dispatchBean != null && dispatchBean instanceof LinkedHashMap) {
      recipients = String.valueOf(((LinkedHashMap) dispatchBean).get("emailList"));
      ftp = String.valueOf(((LinkedHashMap) dispatchBean).get("ftp"));
      jobGroup = String.valueOf(((LinkedHashMap) dispatchBean).get("jobGroup"));

      logger.debug("recipients: " + recipients);
      logger.debug("ftp: " + ftp);

      if(recipients!=null && !recipients.equals("")) {
        String url = apiExportOtherProperties+"/" + analysisId +"/executions/"+executionId+"/data?page=1&pageSize="
            +emailExportSize+"&analysisType=pivot";
        ListenableFuture<ResponseEntity<JsonNode>> responseStringFuture = asyncRestTemplate.exchange(url, HttpMethod.GET,
            requestEntity, JsonNode.class);

        logger.debug("dispatchBean for Pivot: "+ dispatchBean.toString());
        String finalRecipients = recipients;
        responseStringFuture.addCallback(new ListenableFutureCallback<ResponseEntity<JsonNode>>() {
          @Override
          public void onSuccess(ResponseEntity<JsonNode> entity) {
            logger.debug("[Success] Response :" + entity.getStatusCode());
            IFileExporter iFileExporter = new XlsxExporter();
            ExportBean exportBean = new ExportBean();
            String dir = UUID.randomUUID().toString();
            MailSenderUtil MailSender = new MailSenderUtil(appContext.getBean(JavaMailSender.class));
            exportBean.setFileName(publishedPath + File.separator + dir + File.separator + String.valueOf(((LinkedHashMap)
                dispatchBean).get("name")) + ".xlsx");
            exportBean.setReportDesc(String.valueOf(((LinkedHashMap) dispatchBean).get("description")));
            exportBean.setReportName(String.valueOf(((LinkedHashMap) dispatchBean).get("name")));
            exportBean.setPublishDate(String.valueOf(((LinkedHashMap) dispatchBean).get("publishedTime")));
            exportBean.setCreatedBy(String.valueOf(((LinkedHashMap) dispatchBean).get("userFullName")));
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
              MailSender.sendMail(finalRecipients,exportBean.getReportName() + " | " + exportBean.getPublishDate(),
                  serviceUtils.prepareMailBody(exportBean,mailBody)
                  ,exportBean.getFileName());
              logger.debug("Email sent successfully ");
            } catch (IOException e) {
              logger.error("Exception occurred while dispatching pivot :" + this.getClass().getName()+ "  method dataToBeDispatchedAsync()");
            }
          }
          @Override
          public void onFailure(Throwable t) {
            logger.error("[Failed] Getting string response:" + t);
          }
        });
      }

      if(ftp!=null && !ftp.equals("")) {
        String url = apiExportOtherProperties+"/" + analysisId +"/executions/"+executionId+"/data?page=1&pageSize="
            +ftpExportSize+"&analysisType=pivot";
        ListenableFuture<ResponseEntity<JsonNode>> responseStringFuture = asyncRestTemplate.exchange(url, HttpMethod.GET,
            requestEntity, JsonNode.class);

        logger.debug("dispatchBean for Pivot: "+ dispatchBean.toString());
        String finalFtp = ftp;
        String finalJobGroup = jobGroup;
        responseStringFuture.addCallback(new ListenableFutureCallback<ResponseEntity<JsonNode>>() {
          @Override
          public void onSuccess(ResponseEntity<JsonNode> entity) {
            logger.debug("[Success] Response :" + entity.getStatusCode());
            IFileExporter iFileExporter = new XlsxExporter();
            ExportBean exportBean = new ExportBean();
            String dir = UUID.randomUUID().toString();

            exportBean.setFileName(publishedPath + File.separator + dir + File.separator + String.valueOf(((LinkedHashMap)
                dispatchBean).get("name")) + ".xlsx");
            exportBean.setReportDesc(String.valueOf(((LinkedHashMap) dispatchBean).get("description")));
            exportBean.setReportName(String.valueOf(((LinkedHashMap) dispatchBean).get("name")));
            exportBean.setPublishDate(String.valueOf(((LinkedHashMap) dispatchBean).get("publishedTime")));
            exportBean.setCreatedBy(String.valueOf(((LinkedHashMap) dispatchBean).get("userFullName")));

            File cfile = new File(exportBean.getFileName());
            String zipFileName = cfile.getAbsolutePath().concat(".zip");

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
                    FtpCustomer obj = jsonMapper
                        .readValue(new File(ftpDetailsFile), FtpCustomer.class);
                    for (FTPDetails alias : obj.getFtpList()) {
                      logger.debug("Processing Host: " + alias.getHost());
                      logger.debug("jobGroup: " + alias.getCustomerName());
                      logger.debug("Alias: " + aliastemp.equals(alias.getAlias()));
                      if (alias.getCustomerName().equals(finalJobGroup) && aliastemp
                          .equals(alias.getAlias())) {
                        logger.debug("Inside If");
                        serviceUtils.uploadToFtp(alias.getHost(),
                            alias.getPort(),
                            alias.getUsername(),
                            alias.getPassword(),
                            zipFileName,
                            alias.getLocation(),
                            cfile.getName().substring(0, cfile.getName().lastIndexOf(".") + 1) + dtf
                                .format(now).toString() + ".xlsx" + ".zip",
                            alias.getType());
                        logger.debug(
                            "Uploaded to ftp alias: " + alias.getCustomerName() + ":" + alias
                                .getHost());
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
              serviceUtils.deleteFile(exportBean.getFileName(),true);
              serviceUtils.deleteFile(zipFileName,true);
            } catch (IOException e) {
              logger.error("Exception occurred while dispatching pivot :" + this.getClass().getName()+ "  method dataToBeDispatchedAsync()");
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

  @Override
  public AnalysisMetaData getAnalysisMetadata(String analysisId) {

    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    String url = apiExportOtherProperties+"/md?analysisId="+analysisId;
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
}
