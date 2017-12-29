package com.synchronoss.saw.export.pivot;

import com.synchronoss.saw.export.model.Analysis;
import com.synchronoss.saw.export.model.ColumnField;
import com.synchronoss.saw.export.model.DataField;
import com.synchronoss.saw.export.model.RowField;
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

    private Analysis analysis;

    /**
     *
     * @param analysis
     */
    public CreatePivotTable(Analysis analysis)
    {
        this.analysis = analysis;
    }

    /**
     * Create pivot table using file reference
     * @param file
     */
    public void createPivot (File file)
    {
        try {
            logger.debug(this.getClass().getName() + " createPivot starts");
            FileInputStream fis = new FileInputStream(file);
            /** Finds the workbook instance for XLSX file */
            XSSFWorkbook myWorkBook = new XSSFWorkbook (fis);
            XSSFSheet sheet1 = myWorkBook.createSheet();
            /** Return first sheet from the XLSX workbook */
            XSSFSheet sheet = myWorkBook.getSheetAt(0);

            AreaReference areaReference = prepareAreaReference(sheet);
            XSSFPivotTable pivotTable  = sheet1.createPivotTable(areaReference,new CellReference(pivotCellReference),sheet);
            setPivotFields(pivotTable,areaReference);

            FileOutputStream fileOut = new FileOutputStream("pivot-analysis.xlsx");
            myWorkBook.setSheetVisibility(0, SheetVisibility.VERY_HIDDEN);
            myWorkBook.write(fileOut);

            myWorkBook.close();
            fileOut.close();
            logger.debug(this.getClass().getName() + " createPivot ends");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create pivot table reference to Excel workbook.
     * @param workbook
     * @param file
     */
    public void createPivot (Workbook workbook, File file)
    {
        logger.debug(this.getClass().getName() + " createPivot starts");
        XSSFWorkbook wb = (XSSFWorkbook) workbook;
        try {
            XSSFSheet sheet1 =  wb.createSheet();
            /** Return first sheet from the XLSX workbook */
            XSSFSheet sheet = wb.getSheetAt(0);
            AreaReference areaReference = prepareAreaReference(sheet);
            XSSFPivotTable pivotTable  = sheet1.createPivotTable(areaReference,new CellReference(pivotCellReference),sheet);
            setPivotFields(pivotTable,areaReference);
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
     * To add column level in pivot table.
     * @param columnIndex
     * @param pivotTable
     * @param pivotArea
     */
    public void addColLabel(int columnIndex,XSSFPivotTable pivotTable,AreaReference pivotArea ) {
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
        if(pivotTable.getCTPivotTableDefinition().getColFields() != null) {
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
     * @param sheet
     * @return
     */
    private AreaReference prepareAreaReference(XSSFSheet sheet)
    {
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
     *  This method is used to create the pivotfields Row field,
     *  Column fields , Data fields.
     * @param pivotTable
     * @param areaReference
     */
    private void setPivotFields(XSSFPivotTable pivotTable,AreaReference areaReference)
    {
        logger.debug(this.getClass().getName() + " setPivotFields start here");
        List<RowField> rowFieldList = analysis.getSqlBuilder().getRowFields();
        List<ColumnField> columnFields = analysis.getSqlBuilder().getColumnFields();
        List<DataField> dataFields = analysis.getSqlBuilder().getDataFields();
       // int count = rowFieldList.size()+columnFields.size()+dataFields.size()-1;
        int count =0;
        logger.debug(this.getClass().getName() + " set pivot Row fields ");
        /** set row labels */
        for(RowField rowField : rowFieldList)
        {
            pivotTable.addRowLabel(count++);
        }
        logger.debug(this.getClass().getName() + " set pivot Column fields ");
        /** set the column labels */
        for (ColumnField columnField: columnFields)
        {
            addColLabel(count++,pivotTable,areaReference);
        }
        logger.debug(this.getClass().getName() + " set pivot Data fields ");
        /**
         *  Set the data Fields
         */

        for (DataField dataField : dataFields)
        {
            switch(dataField.getAggregate()) {
                case SUM :
                    pivotTable.addColumnLabel(DataConsolidateFunction.SUM,count,dataField.getColumnName());
                    pivotTable.addDataColumn(count,true);
                    pivotTable.getCTPivotTableDefinition().getPivotFields().getPivotFieldArray(count++).setDataField(true);
                    break;
                case AVG:
                    pivotTable.addColumnLabel(DataConsolidateFunction.AVERAGE,count,dataField.getColumnName());
                    pivotTable.addDataColumn(count,true);
                    pivotTable.getCTPivotTableDefinition().getPivotFields().getPivotFieldArray(count++).setDataField(true);
                    break;
                case MAX:
                    pivotTable.addColumnLabel(DataConsolidateFunction.MAX,count,dataField.getColumnName());
                    pivotTable.addDataColumn(count,true);
                    pivotTable.getCTPivotTableDefinition().getPivotFields().getPivotFieldArray(count++).setDataField(true);
                    break;
                case MIN:
                    pivotTable.addColumnLabel(DataConsolidateFunction.MIN,count,dataField.getColumnName());
                    pivotTable.addDataColumn(count,true);
                    pivotTable.getCTPivotTableDefinition().getPivotFields().getPivotFieldArray(count++).setDataField(true);
                    break;
                case COUNT:
                    pivotTable.addColumnLabel(DataConsolidateFunction.COUNT,count,dataField.getColumnName());
                    pivotTable.addDataColumn(count,true);
                    pivotTable.getCTPivotTableDefinition().getPivotFields().getPivotFieldArray(count++).setDataField(true);
                    break;
            }
        }
        logger.debug(this.getClass().getName() + " setPivotFields ends here");
    }

}
