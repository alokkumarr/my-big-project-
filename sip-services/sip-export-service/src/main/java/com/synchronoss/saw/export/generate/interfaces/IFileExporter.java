/**
 *
 */
package com.synchronoss.saw.export.generate.interfaces;

import com.synchronoss.saw.export.generate.ExportBean;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.util.List;

public interface IFileExporter {

  StringBuffer appendHeader(String[] rowHeader);

  StringBuffer rowMaker(String values, StringBuffer rowBuffer);

  Workbook getWorkBook(ExportBean exportBean, List<Object> recordRowList) throws IOException;
}
