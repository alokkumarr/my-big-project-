package com.synchronoss.saw.export.generate;

import com.synchronoss.saw.export.generate.interfaces.IFileExporter;
import com.synchronoss.saw.export.model.DataField;
import com.synchronoss.saw.export.util.ExportUtils;
import com.synchronoss.saw.model.Field;
import com.synchronoss.saw.model.SipQuery;
import org.apache.commons.net.ntp.TimeStamp;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.*;

public class XlsxExporter implements IFileExporter {

  private static final Logger logger = LoggerFactory.getLogger(XlsxExporter.class);
  private static final String DATA_SPLITER = "|||";
  private static final String GENERAL = "General";

  @Override
  public Workbook getWorkBook(ExportBean exportBean, List<Object> recordRowList) {
    Workbook workBook = new XSSFWorkbook();
    XSSFSheet sheet = (XSSFSheet) workBook.createSheet(exportBean.getReportName());
    addxlsxRows(exportBean, workBook, sheet, recordRowList);
    return workBook;
  }

  /**
   * Added the cells in xlsx workbook with cell style.
   *
   * @param exportBean
   * @param workBook
   * @param workSheet
   * @param recordRowList
   */
  private void addxlsxRows(
      ExportBean exportBean, Workbook workBook, XSSFSheet workSheet, List<Object> recordRowList) {
    logger.debug(this.getClass().getName() + " addxlsxRows starts");

    // Create instance here to optimize apache POI cell style
    CellStyle cellStyle = workBook.createCellStyle();
    String[] header = null;
    for (int rowNum = 0; rowNum < recordRowList.size(); rowNum++) {
      XSSFRow excelRow = workSheet.createRow(rowNum + 1);
      Object data = recordRowList.get(rowNum);
      if (data instanceof LinkedHashMap) {
        if (exportBean.getColumnHeader() == null || exportBean.getColumnHeader().length == 0) {
          Object[] obj = ((LinkedHashMap) data).keySet().toArray();
          header = Arrays.copyOf(obj, obj.length, String[].class);
          exportBean.setColumnHeader(header);
          addHeaderRow(exportBean, workBook, workSheet, null);
        }
        buildXlsxCells(exportBean, workBook, header, cellStyle, excelRow, (LinkedHashMap) data);
      }
    }
    logger.debug(this.getClass().getName() + " addxlsxDataRows ends");
  }

  /**
   * Added the cells in xlsx workbook with cell style.
   *
   * @param value
   * @param cell
   * @param specialType
   * @param workBook
   * @param cellStyle
   */
  private void addXlsxCell(
      String value, Cell cell, String specialType, Workbook workBook, CellStyle cellStyle) {

    if (StringUtils.isEmpty(value)
        || value.equalsIgnoreCase("EMPTY")
        || value.equalsIgnoreCase("null")) {
      cell.setCellValue("null");
      DataFormat format = workBook.createDataFormat();
      cellStyle.setDataFormat((format.getFormat(GENERAL)));
      cell.setCellStyle(cellStyle);
    } else if (specialType != null && specialType.equalsIgnoreCase(DataField.Type.STRING.value())) {
      DataFormat format = workBook.createDataFormat();
      cellStyle.setDataFormat((format.getFormat(GENERAL)));
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
      cellStyle.setDataFormat((format.getFormat(GENERAL)));
      cell.setCellStyle(cellStyle);
      cell.setCellValue(value);
    } else {
      DataFormat format = workBook.createDataFormat();
      cellStyle.setDataFormat((format.getFormat(GENERAL)));
      cell.setCellStyle(cellStyle);
      cell.setCellValue(value);
    }
  }

  /**
   * Added the cells in xlsx workbook with cell style.
   *
   * @param sipQuery
   * @param exportBean
   * @param workBook
   * @param workSheet
   * @param recordRow
   */
  public void buildXlsxSheet(
      SipQuery sipQuery,
      ExportBean exportBean,
      Workbook workBook,
      SXSSFSheet workSheet,
      List<Object> recordRow,
      Long limitToExport,
      Long rowCount) {
    logger.debug(this.getClass().getName() + " addXlsxRows starts");

    // Create instance here to optimize apache POI cell style
    String[] header = null;
    CellStyle cellStyle = workBook.createCellStyle();
    Map<String, String> columnHeader = ExportUtils.buildColumnHeaderMap(sipQuery);
    for (int rowNum = 0; rowNum < recordRow.size() && rowNum <= limitToExport; rowNum++) {
      SXSSFRow excelRow = workSheet.createRow(rowCount.intValue() + rowNum);
      Object data = recordRow.get(rowNum);

      if (data instanceof LinkedHashMap) {
        if (exportBean.getColumnHeader() == null || exportBean.getColumnHeader().length == 0) {
          Object[] obj = columnHeader != null && !columnHeader.isEmpty() ?
              columnHeader.keySet().toArray() : ((LinkedHashMap) data).keySet().toArray();
          header = Arrays.copyOf(obj, obj.length, String[].class);

          // set column header to export bean
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
          addReportHeaderRow(exportBean, workBook, workSheet, columnHeader);
        } else if (header == null || header.length <= 0) {
          header = exportBean.getColumnHeader();
          if (rowCount == 1)
            addReportHeaderRow(exportBean, workBook, workSheet, columnHeader);
        }
        buildReportXlsxCells(exportBean, workBook, header, cellStyle, excelRow, (LinkedHashMap) data);
      }
    }
  }

  /**
   * Add header row to build sheet with alignment.
   *
   * @param exportBean
   * @param wb
   * @param sheet
   */
  public void addReportHeaderRow(ExportBean exportBean, Workbook wb, SXSSFSheet sheet, Map<String, String> columnHeader) {
    logger.debug(this.getClass().getName() + " addHeaderRow starts");
    int col = 0;
    Field.Type[] type = exportBean.getColumnFieldDataType();
    DataField.Type[] specialType = exportBean.getColumnDataType();

    Font font = wb.createFont();
    font.setFontHeightInPoints((short) 10);
    font.setColor(IndexedColors.BLACK1.getIndex());
    font.setBold(true);
    font.setItalic(false);

    SXSSFRow row = sheet.createRow(0);
    CellStyle cellStyle = wb.createCellStyle();
    for (String colHeader : exportBean.getColumnHeader()) {
      if (columnHeader != null && !columnHeader.isEmpty() && columnHeader.get(colHeader) != null) {
        colHeader = columnHeader.get(colHeader);
      }
      cellStyle.setFont(font);
      SXSSFCell cell = row.createCell(col);
      DataFormat format = wb.createDataFormat();

      DataField.Type types = specialType[col];
      if (types == null) {
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setDataFormat((format.getFormat(GENERAL)));
        cell.setCellStyle(cellStyle);
      } else if (types.value().equalsIgnoreCase(DataField.Type.STRING.value())) {
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setDataFormat((format.getFormat(GENERAL)));
        cell.setCellStyle(cellStyle);
      } else if (types.value().equalsIgnoreCase(DataField.Type.FLOAT.value())
          || types.value().equalsIgnoreCase(DataField.Type.DOUBLE.value())) {
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        cellStyle.setDataFormat(format.getFormat("0.00"));
        cell.setCellStyle(cellStyle);
      } else if (types.value().equalsIgnoreCase(DataField.Type.INT.value())
          || types.value().equalsIgnoreCase(DataField.Type.LONG.value())) {
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        cellStyle.setDataFormat(format.getFormat("0"));
        cell.setCellStyle(cellStyle);
      } else if (types.value().equalsIgnoreCase(DataField.Type.DATE.value())
          || types.value().equalsIgnoreCase(DataField.Type.TIMESTAMP.value())) {
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setDataFormat((format.getFormat(GENERAL)));
        cell.setCellStyle(cellStyle);
      }

      cell.setCellValue(colHeader);
      col++;
    }
    logger.debug(this.getClass().getName() + " addHeaderRow ends");
  }

  /**
   * BuildXlsxCells for the input data of workbook.
   *
   * @param exportBean
   * @param workBook
   * @param header
   * @param cellStyle
   * @param excelRow
   * @param data
   */
  private void buildReportXlsxCells(
      ExportBean exportBean,
      Workbook workBook,
      String[] header,
      CellStyle cellStyle,
      SXSSFRow excelRow,
      LinkedHashMap data) {

    int colNum = 0;
    for (String val : header) {
      if (val instanceof String) {
        String value = String.valueOf(data.get(val));
        DataField.Type[] types = exportBean.getColumnDataType();
        SXSSFCell cell = excelRow.createCell(colNum);
        if (types != null && types.length > 0) {
          DataField.Type colType = exportBean.getColumnDataType()[colNum];
          String dataType = colType != null ? colType.value() : null;
          addXlsxCell(value, cell, dataType, workBook, cellStyle);
        }
        colNum++;
      }
    }
  }

  /**
   * BuildXlsxCells for the input data of workbook.
   *
   * @param exportBean
   * @param workBook
   * @param header
   * @param cellStyle
   * @param excelRow
   * @param data
   */
  private void buildXlsxCells(
      ExportBean exportBean,
      Workbook workBook,
      String[] header,
      CellStyle cellStyle,
      XSSFRow excelRow,
      LinkedHashMap data) {

    int colNum = 0;
    for (String val : header) {
      if (val instanceof String) {
        String value = String.valueOf(data.get(val));
        DataField.Type[] types = exportBean.getColumnDataType();
        XSSFCell cell = excelRow.createCell(colNum);
        if (types != null && types.length > 0) {
          DataField.Type colType = exportBean.getColumnDataType()[colNum];
          String dataType = colType != null ? colType.value() : null;
          addXlsxCell(value, cell, dataType, workBook, cellStyle);
        } else {
          Field.Type colType = exportBean.getColumnFieldDataType()[colNum];
          String dataType = colType != null ? colType.value() : null;
          addXlsxCell(value, cell, dataType, workBook, cellStyle);
        }
        colNum++;
      }
    }
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
    return null;
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

  /**
   * Add header row to build sheet with alignment.
   *
   * @param exportBean
   * @param wb
   * @param sheet
   */
  public void addHeaderRow(ExportBean exportBean, Workbook wb, Sheet sheet, Map<String, String> columnHeader) {
    logger.debug(this.getClass().getName() + " addHeaderRow starts");
    int col = 0;
    Field.Type[] type = exportBean.getColumnFieldDataType();
    DataField.Type[] specialType = exportBean.getColumnDataType();

    Font font = wb.createFont();
    font.setFontHeightInPoints((short) 10);
    font.setColor(IndexedColors.BLACK1.getIndex());
    font.setBold(true);
    font.setItalic(false);

    Row row = sheet.createRow(0);
    CellStyle cellStyle = wb.createCellStyle();
    for (String colHeader : exportBean.getColumnHeader()) {
      if (columnHeader != null && !columnHeader.isEmpty() && columnHeader.get(colHeader) != null) {
        colHeader = columnHeader.get(colHeader);
      }
      cellStyle.setFont(font);
      Cell cell = row.createCell(col);
      DataFormat format = wb.createDataFormat();

      if (specialType != null) {
        DataField.Type types = specialType[col];
        if (types == null) {
          cellStyle.setAlignment(HorizontalAlignment.LEFT);
          cellStyle.setDataFormat((format.getFormat(GENERAL)));
          cell.setCellStyle(cellStyle);
        } else if (types.value().equalsIgnoreCase(DataField.Type.STRING.value())) {
          cellStyle.setAlignment(HorizontalAlignment.LEFT);
          cellStyle.setDataFormat((format.getFormat(GENERAL)));
          cell.setCellStyle(cellStyle);
        } else if (types.value().equalsIgnoreCase(DataField.Type.FLOAT.value())
            || types.value().equalsIgnoreCase(DataField.Type.DOUBLE.value())) {
          cellStyle.setAlignment(HorizontalAlignment.RIGHT);
          cellStyle.setDataFormat(format.getFormat("0.00"));
          cell.setCellStyle(cellStyle);
        } else if (types.value().equalsIgnoreCase(DataField.Type.INT.value())
            || types.value().equalsIgnoreCase(DataField.Type.LONG.value())) {
          cellStyle.setAlignment(HorizontalAlignment.RIGHT);
          cellStyle.setDataFormat(format.getFormat("0"));
          cell.setCellStyle(cellStyle);
        } else if (types.value().equalsIgnoreCase(DataField.Type.DATE.value())
            || types.value().equalsIgnoreCase(DataField.Type.TIMESTAMP.value())) {
          cellStyle.setAlignment(HorizontalAlignment.LEFT);
          cellStyle.setDataFormat((format.getFormat(GENERAL)));
          cell.setCellStyle(cellStyle);
        }
      } else {
        Field.Type fieldType = type[col];
        if (fieldType == null) {
          cellStyle.setAlignment(HorizontalAlignment.LEFT);
          cellStyle.setDataFormat((format.getFormat(GENERAL)));
          cell.setCellStyle(cellStyle);
        } else if (fieldType.toString().equalsIgnoreCase(Field.Type.STRING.value())) {
          cellStyle.setAlignment(HorizontalAlignment.LEFT);
          cellStyle.setDataFormat((format.getFormat(GENERAL)));
          cell.setCellStyle(cellStyle);
        } else if (fieldType.value().equalsIgnoreCase(Field.Type.FLOAT.value())
            || fieldType.value().equalsIgnoreCase(Field.Type.DOUBLE.value())) {
          cellStyle.setAlignment(HorizontalAlignment.RIGHT);
          cellStyle.setDataFormat(format.getFormat("0.00"));
          cell.setCellStyle(cellStyle);
        } else if (fieldType.value().equalsIgnoreCase(Field.Type.INTEGER.value())
            || fieldType.value().equalsIgnoreCase(Field.Type.LONG.value())) {
          cellStyle.setAlignment(HorizontalAlignment.RIGHT);
          cellStyle.setDataFormat(format.getFormat("0"));
          cell.setCellStyle(cellStyle);
        } else if (fieldType.value().equalsIgnoreCase(Field.Type.DATE.value())
            || fieldType.value().equalsIgnoreCase(Field.Type.TIMESTAMP.value())) {
          cellStyle.setAlignment(HorizontalAlignment.LEFT);
          cellStyle.setDataFormat((format.getFormat(GENERAL)));
          cell.setCellStyle(cellStyle);
        }
      }
      cell.setCellValue(colHeader);
      col++;
    }
    logger.debug(this.getClass().getName() + " addHeaderRow ends");
  }
}
