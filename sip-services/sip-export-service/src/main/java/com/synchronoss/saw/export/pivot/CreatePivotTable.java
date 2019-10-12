package com.synchronoss.saw.export.pivot;

import com.synchronoss.saw.model.Field;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.DataConsolidateFunction;
import org.apache.poi.ss.usermodel.SheetVisibility;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFPivotTable;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class CreatePivotTable {

  public static final String pivotCellReference = "A1";
  private static final Logger logger = LoggerFactory.getLogger(CreatePivotTable.class);

  /**
   * To add column level in pivot table.
   *
   * @param columnIndex
   * @param pivotTable
   * @param pivotArea
   */
  public void addColLabel(int columnIndex, XSSFPivotTable pivotTable, AreaReference pivotArea) {
    logger.debug(this.getClass().getName() + " addColLabel start here");
    final int lastColIndex = pivotArea.getLastCell().getCol() - pivotArea.getFirstCell().getCol();
    CTPivotFields pivotFields = pivotTable.getCTPivotTableDefinition().getPivotFields();

    CTPivotField pivotField = CTPivotField.Factory.newInstance();
    CTItems items = pivotField.addNewItems();

    pivotField.setAxis(STAxis.AXIS_COL);
    pivotField.setShowAll(false);
    for (int i = 0; i <= lastColIndex; i++) {
      items.addNewItem().setT(STItemType.DEFAULT);
    }
    items.setCount(items.sizeOfItemArray());
    pivotFields.setPivotFieldArray(columnIndex, pivotField);

    CTColFields colFields;
    if (pivotTable.getCTPivotTableDefinition().getColFields() != null) {
      colFields = pivotTable.getCTPivotTableDefinition().getColFields();
    } else {
      colFields = pivotTable.getCTPivotTableDefinition().addNewColFields();
    }

    colFields.addNewField().setX(columnIndex);
    colFields.setCount(colFields.sizeOfFieldArray());
    logger.debug(this.getClass().getName() + " addColLabel ends here");
  }

  /**
   * Pivot table area reference calculation
   *
   * @param sheet
   * @return
   */
  private AreaReference prepareAreaReference(XSSFSheet sheet) {
    logger.debug(this.getClass().getName() + " prepareAreaReference start here");
    int first_row = sheet.getFirstRowNum();
    int last_row = sheet.getLastRowNum();
    int first_col = sheet.getRow(0).getFirstCellNum();
    int last_col = sheet.getRow(0).getLastCellNum();
    CellReference start_point = new CellReference(first_row, first_col);
    CellReference end_point = new CellReference(last_row, last_col - 1);
    logger.debug(this.getClass().getName() + " prepareAreaReference ends here");
    return new AreaReference(start_point, end_point, SpreadsheetVersion.EXCEL2007);
  }

  /**
   * This is the new implementation
   *
   * @param workbook
   * @param file
   * @param fields
   */
  public void createPivot(Workbook workbook, File file, List<Field> fields) {
    logger.debug(this.getClass().getName() + " createPivot starts");
    XSSFWorkbook wb = (XSSFWorkbook) workbook;
    try {
      XSSFSheet sheet1 = wb.createSheet();
      /** Return first sheet from the XLSX workbook */
      XSSFSheet sheet = wb.getSheetAt(0);
      AreaReference areaReference = prepareAreaReference(sheet);
      XSSFPivotTable pivotTable =
          sheet1.createPivotTable(areaReference, new CellReference(pivotCellReference), sheet);
      setPivotFields(pivotTable, areaReference, fields);
      FileOutputStream fileOut = new FileOutputStream(file);
      wb.setSheetVisibility(0, SheetVisibility.VERY_HIDDEN);
      wb.write(fileOut);
      wb.close();
      fileOut.close();
      logger.debug(this.getClass().getName() + " createPivot ends");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * This method is used to create the pivotfields Row field, Column fields , Data fields.
   *
   * @param pivotTable
   * @param areaReference
   */
  private void setPivotFields(
      XSSFPivotTable pivotTable, AreaReference areaReference, List<Field> fields) {
    logger.debug(this.getClass().getName() + " setPivotFields start here");
    // int count = rowFieldList.size()+columnFields.size()+dataFields.size()-1;
    if (fields != null && !fields.isEmpty()) {
      int count = 0;
      logger.debug(this.getClass().getName() + " set pivot Row fields ");
      /** set row labels */
      for (Field rowField : fields) {
        if ("row".equalsIgnoreCase(rowField.getArea())) {
          pivotTable.addRowLabel(count++);
        }
      }

      logger.debug(this.getClass().getName() + " set pivot Column fields ");
      /** set the column labels */
      for (Field columnField : fields) {
        if ("column".equalsIgnoreCase(columnField.getArea())) {
          addColLabel(count++, pivotTable, areaReference);
        }
      }

      logger.debug(this.getClass().getName() + " set pivot Data fields ");
      /**
       * 1) Set the data Fields
       * 2) Maintained the sequence same as UI
       * */
      for (int index = 0; index < fields.size(); index++) {
        for (Field dataField : fields) {
          if ("data".equalsIgnoreCase(dataField.getArea())
              && !"string".equalsIgnoreCase(dataField.getType().value())
              && !"date".equalsIgnoreCase(dataField.getType().value())
              && dataField.getAreaIndex() != null && dataField.getAreaIndex().equals(index)) {

            String columnName = dataField.getAlias() != null && !dataField.getAlias().isEmpty()
                ? dataField.getAlias()
                : dataField.getColumnName();
            switch (dataField.getAggregate()) {
              case SUM:
                pivotTable.addColumnLabel(
                    DataConsolidateFunction.SUM, count, columnName);
                pivotTable.addDataColumn(count, true);
                pivotTable
                    .getCTPivotTableDefinition()
                    .getPivotFields()
                    .getPivotFieldArray(count++)
                    .setDataField(true);
                break;
              case AVG:
                pivotTable.addColumnLabel(
                    DataConsolidateFunction.AVERAGE, count, columnName);
                pivotTable.addDataColumn(count, true);
                pivotTable
                    .getCTPivotTableDefinition()
                    .getPivotFields()
                    .getPivotFieldArray(count++)
                    .setDataField(true);
                break;
              case MAX:
                pivotTable.addColumnLabel(
                    DataConsolidateFunction.MAX, count, columnName);
                pivotTable.addDataColumn(count, true);
                pivotTable
                    .getCTPivotTableDefinition()
                    .getPivotFields()
                    .getPivotFieldArray(count++)
                    .setDataField(true);
                break;
              case MIN:
                pivotTable.addColumnLabel(
                    DataConsolidateFunction.MIN, count, columnName);
                pivotTable.addDataColumn(count, true);
                pivotTable
                    .getCTPivotTableDefinition()
                    .getPivotFields()
                    .getPivotFieldArray(count++)
                    .setDataField(true);
                break;
              case COUNT:
              /* Count is already calculated by elastic search aggregation, no need to calculate it again. consider the default
              value as sum to display count value */
                pivotTable.addColumnLabel(
                    DataConsolidateFunction.SUM, count, columnName);
                pivotTable.addDataColumn(count, true);
                pivotTable
                    .getCTPivotTableDefinition()
                    .getPivotFields()
                    .getPivotFieldArray(count++)
                    .setDataField(true);
                break;
              case DISTINCTCOUNT:
                pivotTable.addColumnLabel(
                    DataConsolidateFunction.SUM, count, columnName);
                pivotTable.addDataColumn(count, true);
                pivotTable
                    .getCTPivotTableDefinition()
                    .getPivotFields()
                    .getPivotFieldArray(count++)
                    .setDataField(true);
                break;
              case PERCENTAGE:
              /* PERCENTAGE is already calculated by elastic search aggregation, no need to calculate it again. consider the default
              value as sum to display PERCENTAGE value */
                pivotTable.addColumnLabel(
                    DataConsolidateFunction.SUM, count, columnName);
                pivotTable.addDataColumn(count, true);
                pivotTable
                    .getCTPivotTableDefinition()
                    .getPivotFields()
                    .getPivotFieldArray(count++)
                    .setDataField(true);
                break;
            }
          }
        }
      }
    }
    logger.debug(this.getClass().getName() + " setPivotFields ends here");
  }
}
