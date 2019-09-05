package com.synchronoss.saw.export.util;

import com.synchronoss.saw.export.ServiceUtils;
import com.synchronoss.saw.export.generate.ExportBean;
import com.synchronoss.saw.model.Field;
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

  /**
   * Create Request header with common properties
   *
   * @param request
   * @return HttpHeaders
   */
  public static HttpHeaders setRequestHeader(HttpServletRequest request) {
    HttpHeaders requestHeaders = new HttpHeaders();
    requestHeaders.set("Host", request.getHeader("Host"));
    requestHeaders.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    requestHeaders.set("Content-type", MediaType.APPLICATION_JSON_VALUE);
    requestHeaders.set("Authorization", request.getHeader("Authorization"));
    return requestHeaders;
  }

  /**
   * Method to provide column header exact GUI sequence
   *
   * @param fields List of field from sip query
   * @return
   */
  public static Map<String, String> buildColumnHeaderMap(List<Field> fields) {
    Map<String, String> header = new LinkedHashMap();
    if (fields != null && !fields.isEmpty()) {
      for (int visibleIndex = 0; visibleIndex < fields.size(); visibleIndex++) {
        for (Field field : fields) {
          if (field.getVisibleIndex().equals(visibleIndex)) {
            String[] split = StringUtils.isEmpty(field.getColumnName()) ? null : field.getColumnName().split("\\.");
            if (split != null && split.length >= 2) {
              header.put(split[0], field.getAlias());
            } else {
              header.put(field.getColumnName(), field.getAlias());
            }
            break;
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
   * @param publishedPath
   */
  public static void buildExportBean(ExportBean exportBean, Object dispatchBean, String publishedPath) {
    String dir = UUID.randomUUID().toString();
    exportBean.setFileType(
        String.valueOf(((LinkedHashMap) dispatchBean).get("fileType")));
    exportBean.setFileName(
        publishedPath
            + File.separator
            + dir
            + File.separator
            + ((LinkedHashMap) dispatchBean).get("name")
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
  }


  public static File buildDispatchFile(String fileName) {
    File file = new File(fileName);
    file.getParentFile().mkdir();
    return file;
  }

  public static String generateRandomStringDir() {
    String dir = UUID.randomUUID().toString();
    return dir;
  }
}
