package com.synchronoss.saw.export;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonValidator;
import com.synchronoss.saw.export.generate.ExportBean;
import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ServiceUtils {
  private static final Logger logger = LoggerFactory.getLogger(ServiceUtils.class);
  public final String SCHEMA_FILENAME = "payload-schema.json";

  public ObjectMapper getMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);
    return objectMapper;
  }

  public <T> ResponseEntity<T> createOkResponse(T body) {
    return createResponse(body, HttpStatus.OK);
  }

  /**
   * Clone an existing result as a new one, filtering out http headers that not should be moved on
   * and so on...
   *
   * @param result
   * @param <T>
   * @return
   */
  public <T> ResponseEntity<T> createResponse(ResponseEntity<T> result) {

    ResponseEntity<T> response = createResponse(result.getBody(), result.getStatusCode());
    return response;
  }

  public <T> ResponseEntity<T> createResponse(T body, HttpStatus httpStatus) {
    return new ResponseEntity<>(body, httpStatus);
  }

  public Resource getClassPathResources(String filename) {
    return new ClassPathResource(filename);
  }

  public Boolean jsonSchemaValidate(String jsonDataString, String filename)
      throws IOException, ProcessingException {
    final JsonNode data = JsonLoader.fromString(jsonDataString);
    final JsonNode schema = JsonLoader.fromURL(this.getClassPathResources(filename).getURL());
    final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
    JsonValidator validator = factory.getValidator();
    ProcessingReport report = validator.validate(schema, data);
    return report.isSuccess();
  }

  public String prepareMailBody(ExportBean exportBean, String body) {
    logger.debug("prepare mail body starts here :" + body);
    if (body.contains(MailBodyResolver.ANALYSIS_NAME)) {
      body = body.replaceAll("\\" + MailBodyResolver.ANALYSIS_NAME, exportBean.getReportName());
    }
    if (body.contains(MailBodyResolver.ANALYSIS_DESCRIPTION)) {
      body =
          body.replaceAll("\\" + MailBodyResolver.ANALYSIS_DESCRIPTION, exportBean.getReportDesc());
    }
    if (body.contains(MailBodyResolver.PUBLISH_TIME)) {
      body = body.replaceAll("\\" + MailBodyResolver.PUBLISH_TIME, exportBean.getPublishDate());
    }
    if (body.contains(MailBodyResolver.CREATED_BY)) {
      body = body.replaceAll("\\" + MailBodyResolver.CREATED_BY, exportBean.getCreatedBy());
    }
    logger.debug("prepare mail body ends here :" + this.getClass().getName() + ": " + body);
    return body;
  }

  public boolean deleteFile(String sourceFile, boolean isDeleteSourceFile) throws IOException {
    logger.debug(" Requested file to deleted  :" + this.getClass().getName() + ":" + sourceFile);
    File file = new File(sourceFile);
    if (!file.exists()) return false;

    if (isDeleteSourceFile) {
      file.delete();
      // delete the parent directory since this is temporarily created for particular execution.
      file.getParentFile().delete();
    }
    return true;
  }

  public boolean uploadToFtp(
      String ftpServer,
      int ftpPort,
      String ftpUsername,
      String ftpPassword,
      String localFilePath,
      String destinationDir,
      String destinationFileName,
      String type, String privateKeyPath, String passPhrase) {

    if (ftpServer == null
        || ftpUsername == null
        || localFilePath == null
        || destinationDir == null
        || destinationFileName == null
        || type == null) {
      logger.error(
          "One of the required fields for File transfer is absent. "
              + "Please enter all the fields like FTP server host, port, username, password and type.");
      return false;
    } else {

      try {
        logger.debug("ftpServer: " + ftpServer);
        logger.debug("ftpPort: " + ftpPort);
        logger.debug("localFilePath: " + localFilePath);
        logger.debug("destinationDir: " + destinationDir);
        logger.debug("destinationFileName: " + destinationFileName);
        logger.debug("type: " + type);
        if (type.equalsIgnoreCase("ftp")) {
          logger.debug("uploading to ftp --> ");
          FTPUploader ftp = new FTPUploader(ftpServer, ftpPort, ftpUsername, ftpPassword);
          ftp.uploadFile(localFilePath, destinationFileName, destinationDir);
          ftp.disconnect();
        } else if (type.equalsIgnoreCase("ftps")) {
          logger.debug("uploading to ftps --> ");
          FTPSUploader ftp = new FTPSUploader(ftpServer, ftpPort, ftpUsername, ftpPassword);
          ftp.uploadFile(localFilePath, destinationFileName, destinationDir);
          ftp.disconnect();
        } else if (type.equalsIgnoreCase("sftp")) {
          logger.debug("uploading to sftp --> ");
          SFTPUploader ftp = new SFTPUploader(ftpServer, ftpPort,
              ftpUsername, ftpPassword, privateKeyPath, passPhrase);
          ftp.uploadFile(localFilePath, destinationFileName, destinationDir, privateKeyPath, passPhrase);
          ftp.disconnect();
        }
        logger.info("Successfully uploaded to FTP");
      } catch (Exception e) {
        logger.error("Error occurred: ", e);
        return false;
      }
    }
    return true;
  }

  public class MailBodyResolver {
    public static final String ANALYSIS_NAME = "$analysis_name";
    public static final String ANALYSIS_DESCRIPTION = "$analysis_description";
    public static final String PUBLISH_TIME = "$publish_time";
    public static final String CREATED_BY = "$created_by";
  }
}
