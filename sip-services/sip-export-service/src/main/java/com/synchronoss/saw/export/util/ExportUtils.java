package com.synchronoss.saw.export.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.saw.export.ServiceUtils;
import com.synchronoss.saw.export.generate.ExportBean;
import com.synchronoss.saw.model.Artifact;
import com.synchronoss.saw.model.Field;
import com.synchronoss.saw.model.SipQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * This utility class used for export services
 *
 * @author alok.kumarr
 * @since 3.3.4
 */
public class ExportUtils {

  private static final Logger logger = LoggerFactory.getLogger(ExportUtils.class);

  private static final String HOST = "Host";
  private static final String NAME = "name";
  private static final String AUTHORIZATION = "Authorization";
  private static final String FILE_TYPE = "fileType";
  private static final String DESCRIPTION = "description";
  private static final String PUBLISHED_TIME = "publishedTime";
  private static final String DISTINCT_COUNT = "distinctCount";
  private static final String USER_FULL_NAME = "userFullName";
  private static final String DISTINCT_COUNT_AGGREGATION = "distinctcount";

  /**
   * Create Request header with common properties
   *
   * @param request
   * @return HttpHeaders
   */
  public static HttpHeaders setRequestHeader(HttpServletRequest request) {
    HttpHeaders requestHeaders = new HttpHeaders();
    requestHeaders.set(HOST, request.getHeader(HOST));
    requestHeaders.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    requestHeaders.set("Content-type", MediaType.APPLICATION_JSON_VALUE);
    requestHeaders.set(AUTHORIZATION, request.getHeader(AUTHORIZATION));
    return requestHeaders;
  }

  /**
   * Method to provide column header exact GUI sequence
   *
   * @param sipQuery query of field from sip query
   * @return
   */
  public static Map<String, String> buildColumnHeaderMap(SipQuery sipQuery) {
    // collect all the fields to build column sequence
    List<Field> fields = new ArrayList<>();
    for (Artifact artifact : sipQuery.getArtifacts()) {
      String artifactName = artifact.getArtifactsName();
      List<Field> artiFields = artifact.getFields();

      for(Field artiField: artiFields) {
        String artiFieldAlias = artiField.getAlias();
        String artiFieldDispName = artiField.getDisplayName();

        if ((artifactName != null && artiFieldAlias.equalsIgnoreCase("customerCode")) ||
            (artiFieldDispName!= null && artiFieldDispName.equalsIgnoreCase("customerCode"))) {
          String tempAliasName = "customerCode";
          artiField.setAlias(artifactName + "_" + tempAliasName);
        }
        fields.add(artiField);
      }
    }


    Map<String, String> header = new LinkedHashMap();
    if (!fields.isEmpty()) {
      for (int visibleIndex = 0; visibleIndex < fields.size(); visibleIndex++) {
        for (Field field : fields) {
          String aliasName = field.getAlias() != null && !field.getAlias().isEmpty() ? field.getAlias() : null;
          if (aliasName == null && !StringUtils.isEmpty(field.getDisplayName())) {
              aliasName = field.getDisplayName().trim();
          }
          // look for DL report
          if (sipQuery.getQuery() != null && !sipQuery.getQuery().isEmpty()) {
            if (field.getVisibleIndex() != null && field.getVisibleIndex().equals(visibleIndex)) {
              String[] split = StringUtils.isEmpty(field.getAlias()) ? null : field.getAlias().split("\\.");
              String columnName;
              String aggregationName = field.getAggregate() != null ? field.getAggregate().value() : null;
              if (aggregationName != null && DISTINCT_COUNT_AGGREGATION.equalsIgnoreCase(aggregationName)) {
                aggregationName = aggregationName.replace(aggregationName, DISTINCT_COUNT);
              }
              if (split != null && split.length >= 2) {
                columnName = aggregationName != null ? aggregationName.trim() + "(" + split[0].trim() + ")" : split[0];
                header.put(columnName.trim(), aliasName);
              } else {
                if (field.getColumnName().equalsIgnoreCase("customerCode")) {
                  columnName = aggregationName != null ? aggregationName.trim()
                      + "(" + field.getAlias().trim() + ")" : field.getAlias();
                } else {
                  columnName = aggregationName != null ? aggregationName.trim()
                      + "(" + field.getColumnName().trim() + ")" : field.getColumnName();
                }
                header.put(columnName.trim(), aliasName);
              }
              break;
            }
          } else {
            if (field.getVisibleIndex() != null && field.getVisibleIndex().equals(visibleIndex)) {
              String[] split = StringUtils.isEmpty(field.getColumnName()) ? null : field.getColumnName().split("\\.");
              if (split != null && split.length >= 2) {
                header.put(split[0], aliasName);
              } else {
                header.put(field.getColumnName(), aliasName);
              }
              break;
            }
          }
        }
      }
    }
    return header;
  }

  /**
   * Build zip file and provide to appropriate dispatch
   *
   * @param exportBean
   * @param file
   * @return
   * @throws IOException
   */
  public static String buildZipFile(ExportBean exportBean, File file) throws IOException {
    String zipFileName = file.getAbsolutePath().concat(".zip");
    FileOutputStream fos_zip = new FileOutputStream(zipFileName);
    ZipOutputStream zos = new ZipOutputStream(fos_zip);
    zos.putNextEntry(new ZipEntry(file.getName()));
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
    } finally {
      zos.closeEntry();
      zos.close();
      fos_zip.close();
    }
    return zipFileName;
  }

  /**
   * Cleanup dispatch file from local directory after dispatch
   *
   * @param sourceFile
   * @param serviceUtils
   * @return boolean
   */
  public static boolean deleteDispatchedFile(String sourceFile, ServiceUtils serviceUtils) {
    try {
      serviceUtils.deleteFile(sourceFile, true);
      return true;
    } catch (IOException e) {
      logger.error("Error deleting File : " + sourceFile);
      logger.error(e.getMessage());
      return false;
    }
  }

  /**
   * Build export bean from dispatch bean for further processing
   *
   * @param exportBean
   * @param dispatchBean
   */
  public static void buildExportBean(ExportBean exportBean, Object dispatchBean) {
    exportBean.setFileType(
        String.valueOf(((LinkedHashMap) dispatchBean).get(FILE_TYPE)));
    exportBean.setReportDesc(
        String.valueOf(((LinkedHashMap) dispatchBean).get(DESCRIPTION)));
    exportBean.setReportName(
        String.valueOf(((LinkedHashMap) dispatchBean).get(NAME)));
    exportBean.setPublishDate(
        String.valueOf(((LinkedHashMap) dispatchBean).get(PUBLISHED_TIME)));
    exportBean.setCreatedBy(
        String.valueOf(((LinkedHashMap) dispatchBean).get(USER_FULL_NAME)));
  }


  public static File buildDispatchFile(String fileName) {
    File file = new File(fileName);
    file.getParentFile().mkdir();
    return file;
  }

  public static String generateRandomStringDir() {
    return UUID.randomUUID().toString();
  }

  public static void main(String[] args) throws IOException {
      String sipQueryStr = "{\n"
          + "\t\t\t\"artifacts\": [{\n"
          + "\t\t\t\t\"artifactsName\": \"sales\",\n"
          + "\t\t\t\t\"fields\": [{\n"
          + "\t\t\t\t\t\"alias\": \"\",\n"
          + "\t\t\t\t\t\"columnName\": \"customerCode\",\n"
          + "\t\t\t\t\t\"displayName\": \"CustomerCode\",\n"
          + "\t\t\t\t\t\"type\": \"string\",\n"
          + "\t\t\t\t\t\"visibleIndex\": 0,\n"
          + "\t\t\t\t\t\"name\": \"customerCode\",\n"
          + "\t\t\t\t\t\"table\": \"sales\"\n"
          + "\t\t\t\t}, {\n"
          + "\t\t\t\t\t\"alias\": \"\",\n"
          + "\t\t\t\t\t\"columnName\": \"date\",\n"
          + "\t\t\t\t\t\"displayName\": \"Date\",\n"
          + "\t\t\t\t\t\"type\": \"date\",\n"
          + "\t\t\t\t\t\"visibleIndex\": 2,\n"
          + "\t\t\t\t\t\"dateFormat\": \"yyyy-MM-dd\",\n"
          + "\t\t\t\t\t\"name\": \"date\",\n"
          + "\t\t\t\t\t\"table\": \"sales\"\n"
          + "\t\t\t\t}]\n"
          + "\t\t\t}, {\n"
          + "\t\t\t\t\"artifactsName\": \"product\",\n"
          + "\t\t\t\t\"fields\": [{\n"
          + "\t\t\t\t\t\"alias\": \"\",\n"
          + "\t\t\t\t\t\"columnName\": \"customerCode\",\n"
          + "\t\t\t\t\t\"displayName\": \"CustomerCode\",\n"
          + "\t\t\t\t\t\"type\": \"string\",\n"
          + "\t\t\t\t\t\"visibleIndex\": 1,\n"
          + "\t\t\t\t\t\"name\": \"customerCode\",\n"
          + "\t\t\t\t\t\"table\": \"product\"\n"
          + "\t\t\t\t}]\n"
          + "\t\t\t}],\n"
          + "\t\t\t\"booleanCriteria\": \"AND\",\n"
          + "\t\t\t\"filters\": [],\n"
          + "\t\t\t\"sorts\": [],\n"
          + "\t\t\t\"joins\": [{\n"
          + "\t\t\t\t\"join\": \"inner\",\n"
          + "\t\t\t\t\"criteria\": [{\n"
          + "\t\t\t\t\t\"joinCondition\": {\n"
          + "\t\t\t\t\t\t\"operator\": \"EQ\",\n"
          + "\t\t\t\t\t\t\"left\": {\n"
          + "\t\t\t\t\t\t\t\"artifactsName\": \"SALES\",\n"
          + "\t\t\t\t\t\t\t\"columnName\": \"customerCode\"\n"
          + "\t\t\t\t\t\t},\n"
          + "\t\t\t\t\t\t\"right\": {\n"
          + "\t\t\t\t\t\t\t\"artifactsName\": \"PRODUCT\",\n"
          + "\t\t\t\t\t\t\t\"columnName\": \"customerCode\"\n"
          + "\t\t\t\t\t\t}\n"
          + "\t\t\t\t\t}\n"
          + "\t\t\t\t}]\n"
          + "\t\t\t}],\n"
          + "\t\t\t\"store\": {},\n"
          + "\t\t\t\"query\": \"SELECT sales.customerCode as sales_customerCode , sales.date, product.customerCode as product_customerCode  FROM SALES INNER JOIN PRODUCT ON SALES.customerCode = PRODUCT.customerCode\",\n"
          + "\t\t\t\"semanticId\": \"workbench::sample-spark\"\n"
          + "\t\t}";


      ObjectMapper mapper = new ObjectMapper();

      SipQuery sipQuery = mapper.readValue(sipQueryStr, SipQuery.class);

      Map map = buildColumnHeaderMap(sipQuery);

    System.out.println(map);
  }
}
