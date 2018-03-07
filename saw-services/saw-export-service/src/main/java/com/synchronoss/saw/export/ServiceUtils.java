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
import com.synchronoss.saw.export.model.Ticket;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

@Component
public class ServiceUtils {
  public final String SCHEMA_FILENAME = "payload-schema.json";
  private static final Logger logger = LoggerFactory.getLogger(ServiceUtils.class);


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

  /**
   * To Do
   * @return
   */
  public String getDefaultJwtToken()
  {
    /* create default jwt token to request saw-transport-service restAPI for scheduled analysis to dispatch the data */

    Ticket ticket = new Ticket();
    ticket.setUserId("1");
    ticket.setRoleType("admin");
    ticket.setUserFullName("system");
    ticket.setDataSecurityKey(new ArrayList<>());

    return Jwts.builder().setSubject("sawadmin@synchronoss.com").claim("ticket", ticket).setIssuedAt(new Date())
            .signWith(SignatureAlgorithm.HS256, "sncrsaw2").compact();
  }

  public String prepareMailBody(ExportBean exportBean, String body)
  {
    logger.debug("prepare mail body starts here :"+body );
    if(body.contains(MailBodyResolver.ANALYSIS_NAME))
    {
     body = body.replaceAll("\\"+MailBodyResolver.ANALYSIS_NAME,exportBean.getReportName());
    }
    if(body.contains(MailBodyResolver.ANALYSIS_DESCRIPTION))
    {
      body= body.replaceAll("\\"+MailBodyResolver.ANALYSIS_DESCRIPTION,exportBean.getReportDesc());
    }
    if(body.contains(MailBodyResolver.PUBLISH_TIME))
    {
      body= body.replaceAll("\\"+MailBodyResolver.PUBLISH_TIME,exportBean.getPublishDate());
    }
    if(body.contains(MailBodyResolver.CREATED_BY))
    {
      body= body.replaceAll("\\"+MailBodyResolver.CREATED_BY,exportBean.getCreatedBy());
    }
    logger.debug("prepare mail body ends here :" + this.getClass().getName() +": " +body);
  return body;
  }

  public class MailBodyResolver {
    public static final String ANALYSIS_NAME="$analysis_name";
    public static final String ANALYSIS_DESCRIPTION="$analysis_description";
    public static final String PUBLISH_TIME="$publish_time";
    public static final String CREATED_BY="$created_by";
  }

  public boolean deleteFile(String sourceFile, boolean isDeleteSourceFile) throws IOException {
    logger.debug(" Requested file to deleted  :" + this.getClass().getName() +":" + sourceFile);
    File file = new File(sourceFile);
    if (!file.exists())
      return false;

    if (isDeleteSourceFile) {
      file.delete();
      // delete the parent directory since this is temporarily created for particular execution.
      file.getParentFile().delete();
    }
    return true;
  }

  public boolean uploadToFtp(String ftpServer,
                             int ftpPort,
                             String ftpUsername,
                             String ftpPassword,
                             String localFilePath,
                             String destinationDir,
                             String destinationFileName) {

    if (ftpServer==null || ftpUsername==null || ftpPassword==null || localFilePath==null || destinationDir==null || destinationFileName==null) {
      return false;
    } else {
      try {
        FTPUploader ftp = new FTPUploader(ftpServer, ftpPort, ftpUsername, ftpPassword);
        ftp.uploadFile(localFilePath, destinationFileName, destinationDir);
        ftp.disconnect();
      } catch (Exception e) {
        logger.error(e.getMessage());
        return false;
      }
    }
    return true;
  }
}
