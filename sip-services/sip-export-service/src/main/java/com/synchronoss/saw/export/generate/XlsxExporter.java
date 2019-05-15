package com.synchronoss.saw.export.generate;

import com.synchronoss.saw.export.generate.interfaces.IFileExporter;
import com.synchronoss.saw.export.model.DataField;
import com.synchronoss.saw.model.Field;
import org.apache.commons.net.ntp.TimeStamp;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class XlsxExporter implements IFileExporter {

  private static final Logger logger = LoggerFactory.getLogger(XlsxExporter.class);
  public static final String DATA_SPLITER = "|||";

  public void addHeaderRow(ExportBean exportBean, Workbook wb, Sheet wsheet) {
    logger.debug(this.getClass().getName() + " addHeaderRow starts");
    int col = 0;
    Field.Type[] type = exportBean.getColumnFieldDataType();
    DataField.Type[] specialType = exportBean.getColumnDataType();

    Font font = wb.createFont();
    font.setFontHeightInPoints((short) 10);
    font.setColor(IndexedColors.BLACK1.getIndex());
    font.setBold(true);
    font.setItalic(false);

    Row row = wsheet.createRow(0);
    for (String colHeader : exportBean.getColumnHeader()) {
      CellStyle cellStyle = wb.createCellStyle();
      cellStyle.setFont(font);

      Cell cell = row.createCell(col);
      DataFormat format = wb.createDataFormat();

      if (specialType != null) {
        if (specialType[col] == null) {
          cellStyle.setAlignment(HorizontalAlignment.LEFT);
          cellStyle.setDataFormat((format.getFormat("General")));
          cell.setCellStyle(cellStyle);
        } else if (specialType[col].toString().equalsIgnoreCase(DataField.Type.STRING.value())) {
          cellStyle.setAlignment(HorizontalAlignment.LEFT);
          cellStyle.setDataFormat((format.getFormat("General")));
          cell.setCellStyle(cellStyle);
        } else if (specialType[col].value().equalsIgnoreCase(DataField.Type.FLOAT.value())
            || specialType[col].value().equalsIgnoreCase(DataField.Type.DOUBLE.value())) {
          cellStyle.setAlignment(HorizontalAlignment.RIGHT);
          cellStyle.setDataFormat(format.getFormat("0.00"));
          cell.setCellStyle(cellStyle);
        } else if (specialType[col].value().equalsIgnoreCase(DataField.Type.INT.value())
            || specialType[col].value().equalsIgnoreCase(DataField.Type.LONG.value())) {
          cellStyle.setAlignment(HorizontalAlignment.RIGHT);
          cellStyle.setDataFormat(format.getFormat("0"));
          cell.setCellStyle(cellStyle);
        } else if (specialType[col].value().equalsIgnoreCase(DataField.Type.DATE.value())
            || specialType[col].value().equalsIgnoreCase(DataField.Type.TIMESTAMP.value())) {
          cellStyle.setAlignment(HorizontalAlignment.LEFT);
          cellStyle.setDataFormat((format.getFormat("General")));
          cell.setCellStyle(cellStyle);
        }
      } else {
        if (type[col] == null) {
          cellStyle.setAlignment(HorizontalAlignment.LEFT);
          cellStyle.setDataFormat((format.getFormat("General")));
          cell.setCellStyle(cellStyle);
        } else if (type[col].toString().equalsIgnoreCase(Field.Type.STRING.value())) {
          cellStyle.setAlignment(HorizontalAlignment.LEFT);
          cellStyle.setDataFormat((format.getFormat("General")));
          cell.setCellStyle(cellStyle);
        } else if (type[col].value().equalsIgnoreCase(Field.Type.FLOAT.value())
            || type[col].value().equalsIgnoreCase(Field.Type.DOUBLE.value())) {
          cellStyle.setAlignment(HorizontalAlignment.RIGHT);
          cellStyle.setDataFormat(format.getFormat("0.00"));
          cell.setCellStyle(cellStyle);
        } else if (type[col].value().equalsIgnoreCase(Field.Type.INTEGER.value())
            || type[col].value().equalsIgnoreCase(Field.Type.LONG.value())) {
          cellStyle.setAlignment(HorizontalAlignment.RIGHT);
          cellStyle.setDataFormat(format.getFormat("0"));
          cell.setCellStyle(cellStyle);
        } else if (type[col].value().equalsIgnoreCase(Field.Type.DATE.value())
            || type[col].value().equalsIgnoreCase(Field.Type.TIMESTAMP.value())) {
          cellStyle.setAlignment(HorizontalAlignment.LEFT);
          cellStyle.setDataFormat((format.getFormat("General")));
          cell.setCellStyle(cellStyle);
        }
      }
      cell.setCellValue(colHeader);
      col++;
    }
    logger.debug(this.getClass().getName() + " addHeaderRow ends");
  }

  private CellStyle getStyletoCell(XSSFWorkbook workBook) {
    CellStyle cs = workBook.createCellStyle();
    cs.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
    cs.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
    cs.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
    cs.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
    Font font = workBook.createFont();
    font.setFontHeightInPoints((short) 10);
    cs.setFont(font);
    cs.setWrapText(true);
    return cs;
  }

  private void addxlsxCell(
      String value, int colNum, Row excelRow, String specialType, Workbook workBook) {
    XSSFCell cell = (XSSFCell) excelRow.createCell(colNum);
    CellStyle cellStyle = workBook.createCellStyle();
    if (StringUtils.isEmpty(value) || value.equalsIgnoreCase("EMPTY")) {
      cell.setCellValue("");
      DataFormat format = workBook.createDataFormat();
      cellStyle.setDataFormat((format.getFormat("General")));
      cell.setCellStyle(cellStyle);
    } else if (specialType != null && specialType.equalsIgnoreCase(DataField.Type.STRING.value())) {
      DataFormat format = workBook.createDataFormat();
      cellStyle.setDataFormat((format.getFormat("General")));
      cellStyle.setAlignment(HorizontalAlignment.LEFT);
      cell.setCellStyle(cellStyle);
      cell.setCellValue(value);
    } else if (specialType != null
        && (specialType.equalsIgnoreCase(DataField.Type.FLOAT.value())
        || specialType.equalsIgnoreCase(DataField.Type.DOUBLE.value()))) {
      cellStyle.setAlignment(HorizontalAlignment.RIGHT);
      DataFormat format = workBook.createDataFormat();
      cellStyle.setDataFormat(format.getFormat("0.00"));
      cell.setCellType(CellType.NUMERIC);
      cell.setCellStyle(cellStyle);
      Double d = new Double(value);
      cell.setCellValue(d);
    } else if (specialType != null
        && (specialType.equalsIgnoreCase(DataField.Type.INT.value())
        || specialType.equalsIgnoreCase(DataField.Type.LONG.value()))) {
      cellStyle.setAlignment(HorizontalAlignment.RIGHT);
      DataFormat format = workBook.createDataFormat();
      cellStyle.setDataFormat(format.getFormat("0"));
      cell.setCellType(CellType.NUMERIC);
      cell.setCellStyle(cellStyle);
      Double d = new Double(value);
      cell.setCellValue(d);
    } else if (specialType != null
        && (specialType.equalsIgnoreCase(DataField.Type.DATE.value())
        || specialType.equalsIgnoreCase(DataField.Type.TIMESTAMP.value()))) {
      cellStyle.setAlignment(HorizontalAlignment.RIGHT);
      DataFormat format = workBook.createDataFormat();
      cellStyle.setDataFormat((format.getFormat("General")));
      cell.setCellStyle(cellStyle);
      cell.setCellValue(value);
    } else {
      DataFormat format = workBook.createDataFormat();
      cellStyle.setDataFormat((format.getFormat("General")));
      cell.setCellStyle(cellStyle);
      cell.setCellValue(value);
    }
  }

  /**
   * @param exportBean
   * @param workBook
   * @param workSheet
   * @param recordRow
   */
  public void addxlsxRow(
      ExportBean exportBean, Workbook workBook, XSSFSheet workSheet, Object recordRow) {
    logger.debug(this.getClass().getName() + " addxlsxRows starts");
    String[] header = null;

    XSSFRow excelRow = workSheet.createRow(workSheet.getLastRowNum() + 1);
    Object data = recordRow;

    if (data instanceof LinkedHashMap) {

      if (exportBean.getColumnHeader() == null || exportBean.getColumnHeader().length == 0) {
        Object[] obj = ((LinkedHashMap) data).keySet().toArray();
        header = Arrays.copyOf(obj, obj.length, String[].class);

        DataField.Type[] columnDataType = new DataField.Type[header.length];
        exportBean.setColumnHeader(header);

        int i = 0;
        for (String val : header) {
          if (i < header.length) {
            Object obj1 = ((LinkedHashMap) data).get(val);
            if (obj1 instanceof Date) {
              columnDataType[i] = DataField.Type.DATE;
            } else if (obj1 instanceof Float) {
              columnDataType[i] = DataField.Type.FLOAT;
            } else if (obj1 instanceof Double) {
              columnDataType[i] = DataField.Type.DOUBLE;
            } else if (obj1 instanceof Integer) {
              columnDataType[i] = DataField.Type.INT;
            } else if (obj1 instanceof Long) {
              columnDataType[i] = DataField.Type.LONG;
            } else if (obj1 instanceof String) {
              columnDataType[i] = DataField.Type.STRING;
            } else if (obj1 instanceof TimeStamp) {
              columnDataType[i] = DataField.Type.TIMESTAMP;
            }
            i++;
          }
        }

        exportBean.setColumnDataType(columnDataType);
        addHeaderRow(exportBean, workBook, workSheet);
      }
      if (header == null || header.length <= 0) {
        header = exportBean.getColumnHeader();
      }

      int colNum = 0;
      for (String val : header) {
        if (val instanceof String) {
          String value = String.valueOf(((LinkedHashMap) data).get(val));
          if (exportBean.getColumnDataType() != null && exportBean.getColumnDataType().length > 0) {
            addxlsxCell(
                value, colNum, excelRow, exportBean.getColumnDataType()[colNum].value(), workBook);
          } else {
            addxlsxCell(
                value,
                colNum,
                excelRow,
                exportBean.getColumnFieldDataType()[colNum].value(),
                workBook);
          }
          colNum++;
        }
      }
    }
  }

  private void addxlsxRows(
      ExportBean exportBean, Workbook workBook, XSSFSheet workSheet, List<Object> recordRowList) {
    logger.debug(this.getClass().getName() + " addxlsxRows starts");
    String[] header = null;
    for (int rowNum = 0; rowNum < recordRowList.size(); rowNum++) {
      XSSFRow excelRow = workSheet.createRow(rowNum + 1);
      Object data = recordRowList.get(rowNum);

      if (data instanceof LinkedHashMap) {

        if (exportBean.getColumnHeader() == null || exportBean.getColumnHeader().length == 0) {
          Object[] obj = ((LinkedHashMap) data).keySet().toArray();
          header = Arrays.copyOf(obj, obj.length, String[].class);
          exportBean.setColumnHeader(header);
          addHeaderRow(exportBean, workBook, workSheet);
        }
        int colNum = 0;
        for (String val : header) {
          if (val instanceof String) {
            String value = String.valueOf(((LinkedHashMap) data).get(val));
            if (exportBean.getColumnDataType() != null
                && exportBean.getColumnDataType().length > 0) {
              addxlsxCell(
                  value,
                  colNum,
                  excelRow,
                  exportBean.getColumnDataType()[colNum].value(),
                  workBook);
            } else {
              addxlsxCell(
                  value,
                  colNum,
                  excelRow,
                  exportBean.getColumnFieldDataType()[colNum].value(),
                  workBook);
            }
            colNum++;
          }
        }
      }
    }
    // logger.debug(this.getClass().getName() + " addxlsxDataRows ends");
  }

  /** This method is used to make a parsable row which can be converted into Excel Cell */
  public StringBuffer rowMaker(String values, StringBuffer rowBuffer) {

    if (values != null && !"".equals(values) && !"null".equalsIgnoreCase(values)) {
      rowBuffer.append(values);
      rowBuffer.append(DATA_SPLITER);
    } else {
      rowBuffer.append("EMPTY");
      rowBuffer.append(DATA_SPLITER);
    }
    return rowBuffer;
  }

  @Override
  public StringBuffer appendHeader(String[] rowHeader) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public File generateFile(ExportBean exportBean, List<Object> recordRowList) throws IOException {
    BufferedOutputStream stream = null;
    Workbook workBook = null;
    File xlsxFile = null;
    try {
      // xlsx support
      xlsxFile = new File(exportBean.getFileName());
      xlsxFile.createNewFile();
      stream = new BufferedOutputStream(new FileOutputStream(xlsxFile));
      workBook = new XSSFWorkbook();
      XSSFSheet sheet = (XSSFSheet) workBook.createSheet(exportBean.getReportName());
      // addHeaderRow(exportBean, workBook, sheet);
      addxlsxRows(exportBean, workBook, sheet, recordRowList);
      workBook.write(stream);
      stream.flush();
      return xlsxFile;
    } catch (IOException e) {
      if (xlsxFile != null) {
        xlsxFile.delete();
      }
      throw e;
    } finally {
      if (stream != null) {
        stream.close();
      }
    }
  }

  @Override
  public Workbook getWorkBook(ExportBean exportBean, List<Object> recordRowList)
      throws IOException {
    Workbook workBook = new XSSFWorkbook();
    workBook.getSpreadsheetVersion();
    XSSFSheet sheet = (XSSFSheet) workBook.createSheet(exportBean.getReportName());
    // addHeaderRow(exportBean, workBook, sheet);
    addxlsxRows(exportBean, workBook, sheet, recordRowList);
    return workBook;
  }

  /**
   * Function accepts workbook and auto adjusts the size of columns to fit the size of any row in
   * that column.
   *
   * @param workbook
   */
  public void autoSizeColumns(Workbook workbook) {
    int numberOfSheets = workbook.getNumberOfSheets();
    for (int i = 0; i < numberOfSheets; i++) {
      Sheet sheet = workbook.getSheetAt(i);
      if (sheet.getPhysicalNumberOfRows() > 0) {
        Row row = sheet.getRow(0);
        if (row != null) {
          Iterator<Cell> cellIterator = row.cellIterator();
          while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            int columnIndex = cell.getColumnIndex();
            sheet.autoSizeColumn(columnIndex);
          }
        }
      }
    }
  }
}
