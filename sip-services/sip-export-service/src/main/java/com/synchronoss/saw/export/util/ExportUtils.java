package com.synchronoss.saw.export.util;

import com.synchronoss.saw.model.Field;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * This utility class used for export services
 *
 * @author alok.kumarr
 * @since 3.3.4
 */
public class ExportUtils {

  /**
   * Method to provide column header exact GUI sequence
   *
   * @param fields List of field from sip query
   * @return
   */
  public static Object[] buildColumnHeader(List<Field> fields) {
    List<String> header = new LinkedList<>();
    if (fields != null && !fields.isEmpty()) {
      fields.forEach(field -> {
        String[] split = StringUtils.isEmpty(field.getColumnName()) ? null : field.getColumnName().split("\\.");
        if (split != null && split.length >= 2) {
          header.add(split[0]);
        } else {
          header.add(field.getColumnName());
        }
      });
    }
    return header.toArray();
  }
}
