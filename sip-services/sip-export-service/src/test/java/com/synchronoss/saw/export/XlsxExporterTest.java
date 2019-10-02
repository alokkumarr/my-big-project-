package com.synchronoss.saw.export;

import com.synchronoss.saw.export.generate.ExportBean;
import com.synchronoss.saw.export.generate.ExportServiceImpl;
import com.synchronoss.saw.export.generate.XlsxExporter;
import com.synchronoss.saw.export.model.DataResponse;
import com.synchronoss.saw.model.Artifact;
import com.synchronoss.saw.model.Field;
import com.synchronoss.saw.model.SipQuery;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class XlsxExporterTest {
  static ExportBean exportBean;
  static DataResponse dataResponse;
  static Long LimittoExport = Long.valueOf(50000);
  XlsxExporter xlsxExporter;
  private List<Field> fields = new LinkedList<>();
  private SipQuery sipQuery = new SipQuery();
  private static final Logger logger = LoggerFactory.getLogger(XlsxExporterTest.class);

  @Mock
  private RestTemplate restTemplate;
  private String analysisId;

  @Before
  public void setUp() {
    exportBean = new ExportBean();
    LinkedHashMap dispatchBean = new LinkedHashMap();
    xlsxExporter = new XlsxExporter();
    analysisId= UUID.randomUUID().toString();
    ClassLoader classLoader = getClass().getClassLoader();

    dispatchBean.put("fileType", "xlsx");
    dispatchBean.put("description", "TestXlsx");
    dispatchBean.put("name", "TestExcel");
    dispatchBean.put("publishedTime", System.currentTimeMillis());

    exportBean.setReportDesc(String.valueOf(((LinkedHashMap) dispatchBean).get("description")));
    exportBean.setReportName(String.valueOf(((LinkedHashMap) dispatchBean).get("name")));
    exportBean.setPublishDate(String.valueOf(((LinkedHashMap) dispatchBean).get("publishedTime")));
    exportBean.setCreatedBy(String.valueOf(((LinkedHashMap) dispatchBean).get("userFullName")));
    exportBean.setReportName("MyTestAnalysis");
    exportBean.setFileName(
        new File(classLoader.getResource("test").getPath()) + File.separator + "abc.xlsx");
    // Here we are creating /test dir under /Resources/ Dir to write mock xlsx file.

    dataResponse = new DataResponse();
    LinkedHashMap obj1 = new LinkedHashMap();
    obj1.put("AccountNum", 10206088);
    obj1.put("SWITCH_MOU", 3.23);
    obj1.put("REGION", "US");

    LinkedHashMap obj2 = new LinkedHashMap();
    obj2.put("AccountNum", 10206089);
    obj2.put("SWITCH_MOU", 1.20);
    obj2.put("REGION", "US");

    LinkedHashMap obj3 = new LinkedHashMap();
    obj3.put("AccountNum", 10206082);
    obj3.put("SWITCH_MOU", 4.5);
    obj3.put("REGION", "US");

    ArrayList list = new ArrayList();
    list.add(obj1);
    list.add(obj2);
    list.add(obj3);

    dataResponse.setData(list);

    Field field = new Field();
    field.setDataField("COORDINATES");
    field.setDisplayName("COORDINATES.keyword");
    field.setColumnName("COORDINATES");
    field.setVisibleIndex(0);
    fields.add(field);

    field = new Field();
    field.setDataField("date");
    field.setDisplayName("Date");
    field.setColumnName("date");
    field.setVisibleIndex(1);
    fields.add(field);

    field = new Field();
    field.setDataField("integer");
    field.setDisplayName("Integer");
    field.setColumnName("integer");
    field.setVisibleIndex(2);
    fields.add(field);

    Artifact artifact = new Artifact();
    artifact.setFields(fields);
    List<Artifact> artifactList = new ArrayList<>();
    artifactList.add(artifact);

    sipQuery.setArtifacts(artifactList);
  }

  @Test
  public void mockTest() {
    Workbook workBook = new SXSSFWorkbook();
    workBook.getSpreadsheetVersion();
    SXSSFSheet sheet = (SXSSFSheet) workBook.createSheet(exportBean.getReportName());
    xlsxExporter.buildXlsxSheet(sipQuery, exportBean, workBook, sheet, dataResponse.getData(), 3l, 10l);
    assertEquals(sheet.getPhysicalNumberOfRows(), 4);
  }

  @Test
  public void streamToXlsxReportTest() {
    ExportServiceImpl exportService = new ExportServiceImpl();
    try {
      boolean haveSheetCreated = exportService.streamToXlsxReport(sipQuery, analysisId, "esReport", LimittoExport, exportBean, restTemplate);
      assertEquals(haveSheetCreated, Boolean.TRUE);
    } catch (IOException e) {
      logger.error(" Error in streamToXlsxReport : {}", ExceptionUtils.getStackTrace(e));
    }
  }
}
