package com.synchronoss.saw.export.util;

import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.export.ServiceUtils;
import com.synchronoss.saw.export.generate.ExportBean;
import com.synchronoss.saw.model.Artifact;
import com.synchronoss.saw.model.Field;
import com.synchronoss.saw.model.SipQuery;
import com.synchronoss.sip.utils.SipCommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedCaseInsensitiveMap;
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
  private static final String AUTHORIZATION = "Authorization";
  private static final String FILE_TYPE = "fileType";
  private static final String DESCRIPTION = "description";
  private static final String CUSTOMER_CODE = "customerCode";
  private static final String PUBLISHED_TIME = "publishedTime";
  private static final String DISTINCT_COUNT = "distinctCount";
  private static final String USER_FULL_NAME = "userFullName";
  private static final String DISTINCT_COUNT_AGGREGATION = "distinctcount";
  private static final String DEFAULT_FILE_TYPE = "csv";

  private static final String ANALYSIS_TYPE_DL_REPORT = "report";

  private ExportUtils() {}
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
   * @param analysis Analysis definition
   * @return
   */
  public static Map<String, String> buildColumnHeaderMap(Analysis analysis) {
    logger.trace("Building column header map for analysis " + analysis);
    SipQuery sipQuery = analysis.getSipQuery();
    boolean haveDLQuery = sipQuery.getQuery() != null && !sipQuery.getQuery().isEmpty();
    // collect all the fields to build column sequence
    List<Field> fields = new ArrayList<>();
    if (ANALYSIS_TYPE_DL_REPORT.equals(analysis.getType())) {
      logger.trace("DL Report found");
      if (analysis.getDesignerEdit() != null && !analysis.getDesignerEdit()) {
        for (Artifact artifact : sipQuery.getArtifacts()) {
          String artifactName = artifact.getArtifactsName();
          artifact
              .getFields()
              .forEach(
                  field -> {
                    // This is added to fix SIP-8811 customer Code column issue on DL report
                    if (haveDLQuery
                        && artifactName != null
                        && CUSTOMER_CODE.matches(field.getColumnName())) {
                      field.setColumnName(artifactName.concat("_").concat(field.getColumnName()));
                    }
                    fields.add(field);
                  });
        }
      }
    } else {
      for (Artifact artifact : sipQuery.getArtifacts()) {
        fields.addAll(artifact.getFields());
      }
    }

    logger.trace("Fields length = " + fields.size() + ", fields = " + fields);
    Map<String, String> header = new LinkedHashMap();
    if (!fields.isEmpty()) {
      for (int visibleIndex = 0; visibleIndex < fields.size(); visibleIndex++) {
        for (Field field : fields) {
          String aliasName = field.getAlias() != null && !field.getAlias().isEmpty() ? field.getAlias() : null;

          if (aliasName == null && !StringUtils.isEmpty(field.getDisplayName())) {
            aliasName = field.getDisplayName().trim();
          }
          logger.trace("Alias name=" + aliasName);
          // look for DL report
          if (haveDLQuery) {
            if (field.getVisibleIndex() != null && field.getVisibleIndex().equals(visibleIndex)) {
              String[] split = StringUtils.isEmpty(field.getColumnName()) ? null : field.getColumnName().split("\\.");
              String columnName;
              String aggregationName = field.getAggregate() != null ? field.getAggregate().value() : null;
              if (aggregationName != null && DISTINCT_COUNT_AGGREGATION.equalsIgnoreCase(aggregationName)) {
                aggregationName = aggregationName.replace(aggregationName, DISTINCT_COUNT);
              }
              if (split != null && split.length >= 2) {
                columnName = aggregationName != null ? aggregationName.trim() + "(" + split[0].trim() + ")" : split[0];
                header.put(columnName.trim(), aliasName);
              } else {
                columnName = aggregationName != null ? aggregationName.trim() + "(" + field.getColumnName().trim() + ")" : field.getColumnName();
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

    logger.trace("Header = " + header);
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
    String normalizedPath = SipCommonUtils.normalizePath(exportBean.getFileName());
    try (FileInputStream inputStream = new FileInputStream(normalizedPath)) {
      while ((amountRead = inputStream.read(readBuffer)) > 0) {
        zos.write(readBuffer, 0, amountRead);
        written += amountRead;
      }
      logger.info(String.format("Written %s, bytes to %s ", written, zipFileName));
    } catch (Exception e) {
      logger.error("Error while writing to zip: {}", e.getMessage());
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
      logger.error("Error deleting File : {}", sourceFile);
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
    exportBean.setReportDesc(String.valueOf(((LinkedHashMap) dispatchBean).get(DESCRIPTION)));
    String reportName =
        prepareReportName(String.valueOf(((LinkedHashMap) dispatchBean).get("name")));
    exportBean.setReportName(reportName);
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

      public static String prepareExcelSheetName(String excelSheetName) {
    /*Excel sheet Name doesn't allow some special characters and length should be less than 31,so
    removing the special characters from analysis name and prearing name with only first 31
    characters*/
    excelSheetName = excelSheetName.trim().replaceAll("[*/:\\\\?\\[\\]]", "");
    if (excelSheetName.length() > 31) {
      excelSheetName = excelSheetName.substring(0, 31);
    }
    return excelSheetName;
  }

  public static String prepareFileName(String name, String fileType) {
    name = name.replaceAll("[/\\\\]", "").concat(".");
    if (fileType != null && !StringUtils.isEmpty(fileType)) {
      return name.concat(fileType);
    }
    return name.concat(DEFAULT_FILE_TYPE);
  }

  public static String prepareReportName(String name) {
    return name.replaceAll("[\\\\]", "");
  }

  public static LinkedCaseInsensitiveMap<Object> convert(LinkedHashMap<String, Object> linkedHashMap) {
    LinkedCaseInsensitiveMap<Object> caseInsensitiveMap = new LinkedCaseInsensitiveMap<>();
    caseInsensitiveMap.putAll(linkedHashMap);

    return caseInsensitiveMap;
  }
}
