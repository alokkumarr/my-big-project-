package com.synchronoss.saw.export;

import com.synchronoss.saw.export.generate.ExportBean;
import com.synchronoss.saw.export.generate.XlsxExporter;
import com.synchronoss.saw.export.model.DataResponse;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;

public class XlsxExporterTest {
    static ExportBean exportBean;
    static DataResponse dataResponse;
    static Long LimittoExport = Long.valueOf(50000);
    XlsxExporter xlsxExporter;

    @Before
    public void setUp() {

        exportBean = new ExportBean();
        LinkedHashMap dispatchBean = new LinkedHashMap();
        xlsxExporter = new XlsxExporter();
        dispatchBean.put("fileType","xslx");
        dispatchBean.put("description", "TestXslx");
        dispatchBean.put("name","TestExcel");
        dispatchBean.put("publishedTime",System.currentTimeMillis());

        exportBean.setReportDesc(String.valueOf(((LinkedHashMap) dispatchBean).get("description")));
        exportBean.setReportName(String.valueOf(((LinkedHashMap) dispatchBean).get("name")));
        exportBean.setPublishDate(String.valueOf(((LinkedHashMap) dispatchBean).get("publishedTime")));
        exportBean.setCreatedBy(String.valueOf(((LinkedHashMap) dispatchBean).get("userFullName")));
        exportBean.setReportName("MyTestAnalysis");

        dataResponse = new DataResponse();
        LinkedHashMap obj1 = new LinkedHashMap();
        obj1.put("AccountNum",10206088);
        obj1.put("SWITCH_MOU",3.23);
        obj1.put("REGION","US");

        LinkedHashMap obj2 = new LinkedHashMap();
        obj2.put("AccountNum",10206089);
        obj2.put("SWITCH_MOU",1.20);
        obj2.put("REGION","US");

        LinkedHashMap obj3 = new LinkedHashMap();
        obj3.put("AccountNum",10206082);
        obj3.put("SWITCH_MOU",4.5);
        obj3.put("REGION","US");

        ArrayList list = new ArrayList();
        list.add(obj1);
        list.add(obj2);
        list.add(obj3);

        dataResponse.setData(list);

    }

    @Test
    public void streamToXslxReport() {
        Workbook workBook = new XSSFWorkbook();
        workBook.getSpreadsheetVersion();
        XSSFSheet sheet = (XSSFSheet) workBook.createSheet(exportBean.getReportName());

        dataResponse.getData()
            .stream()
            .limit(LimittoExport)
            .forEach(
                line -> {
                    try {
                        xlsxExporter.addxlsxRow(exportBean, workBook, sheet, line);

                    } catch (Exception e) {

                    }
                }
            );
        assertEquals(sheet.getPhysicalNumberOfRows(),4);
    }

}
