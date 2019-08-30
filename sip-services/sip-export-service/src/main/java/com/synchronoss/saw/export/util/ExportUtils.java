package com.synchronoss.saw.export.util;

import com.synchronoss.saw.model.Field;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
  public static Map<String, String> buildColumnHeaderMap(List<Field> fields) {
    Map<String, String> header = new LinkedHashMap();
    if (fields != null && !fields.isEmpty()) {
      fields.forEach(field -> {
        String[] split = StringUtils.isEmpty(field.getColumnName()) ? null : field.getColumnName().split("\\.");
        if (split != null && split.length >= 2) {
          header.put(split[0], field.getAlias());
        } else {
          header.put(field.getColumnName(), field.getAlias());
        }
      });
    }
    return header;
  }
}
