package com.razor.raw.re.report.generate;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.razor.raw.core.common.CommonConstants;
import com.razor.raw.re.report.interfaces.IFileExporter;

public class XlsxExporter implements IFileExporter
 {
	private static final Logger logger = LoggerFactory.getLogger(XlsxExporter.class);
	public void addHeaderRow(ExportExcelBean exportExcelBean,
			Workbook wb, Sheet wsheet) {
		logger.debug(this.getClass().getName() + " addHeaderRow starts");
		int col = 0;
		CellStyle cellStyle1 = wb.createCellStyle();
		Font font1 = wb.createFont();
		font1.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font1.setFontHeightInPoints((short) 12);
		font1.setColor(HSSFColor.BLUE_GREY.index);
		cellStyle1.setFont(font1);
		Row row = wsheet.createRow(0);
		Cell cell = row.createCell(col);
		cell.setCellStyle(cellStyle1);
		cell.setCellValue("Report Name          : " + exportExcelBean.getReportName());
		row = wsheet.createRow(1);
		cell = row.createCell(col);
		cell.setCellStyle(cellStyle1);
		cell.setCellValue("Report Description : "+exportExcelBean.getReportDesc());
		row = wsheet.createRow(2);
		cell = row.createCell(col);
		cell.setCellStyle(cellStyle1);
		cell.setCellValue("Published On          : "+ exportExcelBean.getPublishDate());
		row = wsheet.createRow(3);
		row = wsheet.createRow(4);
		
		CellStyle cellStyle = wb.createCellStyle();
		
		Font font = wb.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setFontHeightInPoints((short) 12);
		font.setColor(HSSFColor.BLUE_GREY.index);
		cellStyle.setFont(font);
		cellStyle.setAlignment(CellStyle.ALIGN_LEFT);
		cellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		//style.setFillBackgroundColor(IndexedColors.AQUA.getIndex());
		cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		//cellStyle.setFillPattern(CellStyle);
		
		for (String colHeader : exportExcelBean.getColumnHeader()) {
			cell = row.createCell(col);
			cell.setCellStyle(cellStyle);
			cell.setCellValue(colHeader);
			col++;
		}
		logger.debug(this.getClass().getName() + " addHeaderRow ends");
	}

	private CellStyle getStyletoCell(SXSSFWorkbook workBook)
	{
		CellStyle cs = workBook.createCellStyle();
		cs.setBorderLeft(CellStyle.BORDER_THICK);
		cs.setBorderRight(CellStyle.BORDER_THICK);
		cs.setBorderTop(CellStyle.BORDER_THICK);
		cs.setBorderBottom(CellStyle.BORDER_THICK);
		cs.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
		cs.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
		cs.setLeftBorderColor(IndexedColors.GREY_25_PERCENT .getIndex());
		cs.setRightBorderColor(IndexedColors.GREY_25_PERCENT .getIndex());
		Font font = workBook.createFont();
		font.setFontHeightInPoints((short) 10);
		cs.setFont(font);
		cs.setWrapText(true);
		return cs;
	}
	private void addxlsxCell(String value, int colNum, Row excelRow,
			String specialType, SXSSFWorkbook workBook, CellStyle cellStyle) {
		SXSSFCell cell = (SXSSFCell) excelRow.createCell(colNum);
		//CellStyle cellStyle = getCommonStyletoCell(cellStyle2);
		if (StringUtils.isEmpty(value) || value.equalsIgnoreCase("EMPTY")) {
			cell.setCellValue("");
			cell.setCellStyle(cellStyle);
		} else if (specialType.equalsIgnoreCase("String")) {
			String val = (String) value;
			cellStyle.setAlignment(CellStyle.ALIGN_LEFT);
			cell.setCellStyle(cellStyle);
			cell.setCellValue(val);

		} else if (specialType.equalsIgnoreCase("numaric")) {
			String val = (String) value;
			cellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
			DataFormat format = workBook.createDataFormat();
			cellStyle.setDataFormat(format.getFormat("0.0"));
			cell.setCellType(SXSSFCell.CELL_TYPE_NUMERIC);
			cell.setCellStyle(cellStyle);
			Double d = new Double(val);
			cell.setCellValue(d);
		} else if (specialType.equalsIgnoreCase("date"))
			{
				String val = (String) value;
				cellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
				cell.setCellStyle(cellStyle);
				cell.setCellValue(val);
			}
		else
		{
			String val = (String) value;
			cellStyle.setAlignment(CellStyle.ALIGN_LEFT);
			cell.setCellStyle(cellStyle);
			cell.setCellValue(val);
		}
	}
	
	private CellStyle getCommonStyletoCell(CellStyle cs) {
		//CellStyle cs = workBook.createCellStyle();
		cs.setBorderLeft(CellStyle.BORDER_THICK);
		cs.setBorderRight(CellStyle.BORDER_THICK);
		cs.setBorderTop(CellStyle.BORDER_THICK);
		cs.setBorderBottom(CellStyle.BORDER_THICK);
		cs.setBottomBorderColor(IndexedColors.GREY_25_PERCENT .getIndex());
		cs.setTopBorderColor(IndexedColors.GREY_25_PERCENT .getIndex());
		cs.setLeftBorderColor(IndexedColors.GREY_25_PERCENT .getIndex());
		cs.setRightBorderColor(IndexedColors.GREY_25_PERCENT .getIndex());
		/*Font font = workBook.createFont();
		font.setFontHeightInPoints((short) 10);
		cs.setFont(font);
		cs.setWrapText(true);*/
		return cs;
	}

	public File exportJsonData(ExportExcelBean exportExcelBean)
			throws IOException {
		BufferedOutputStream stream = null;
		Workbook workBook = null;
		File xlsxFile = null;
		try {
				// xlsx support
				xlsxFile = new File(exportExcelBean.getServerPathLocation()+File.separator+exportExcelBean.getFileName());
				xlsxFile.createNewFile();
				stream = new BufferedOutputStream(new FileOutputStream(xlsxFile));
			    workBook = new SXSSFWorkbook(-1);
				SXSSFSheet sheet = (SXSSFSheet) workBook.createSheet(exportExcelBean.getReportName());
				addHeaderRow(exportExcelBean, workBook, sheet);
				addxlsxDataRows(exportExcelBean, (SXSSFWorkbook) workBook, sheet);
				workBook.write(stream);
				stream.flush();
				return xlsxFile;

		} catch (IOException e) {
			if (xlsxFile != null) {
				xlsxFile.delete();
			}
			throw e;
		} finally {
			if (stream != null)
				stream.close();
		}
	}

	private void addxlsxDataRows(ExportExcelBean exportExcelBean,
			SXSSFWorkbook workBook, SXSSFSheet workSheet) {
		CellStyle cellStyle = getStyletoCell(workBook);
		logger.debug(this.getClass().getName() + " addxlsxDataRows starts");
		for (int rowNum = 0; rowNum < exportExcelBean.getRowBufferList().size(); rowNum++) {
			int rowInExcel =rowNum+5; 
			SXSSFRow excelRow = (SXSSFRow) workSheet.createRow(rowInExcel);
			String rowStr = exportExcelBean.getRowBufferList().get(rowNum).toString();
			//Object obj = gson.fromJson(resultRows.get(rowNum), class1);
			//BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(class1.cast(obj));
			String[] splittedStrings = rowStr.split(CommonConstants.DATA_SPLITER_REGEX);
			for (int colNum = 0; colNum < splittedStrings.length; colNum++) {
				//String propertyName = exportExcelBean.getColumnHeader()[colNum];
				String cellType = exportExcelBean.getColumnDataType()[colNum];
				String value = splittedStrings[colNum];
				//Object object = wrapper.getPropertyValue(propertyName.getPropertyName());
				addxlsxCell(value, colNum, excelRow, cellType, workBook, cellStyle);
			}
		}
		logger.debug(this.getClass().getName() + " addxlsxDataRows ends");
	}
	
	/**
	 * This method is used to make a parsable row which can be converted into
	 * Excel Cell
	 * 
	 * @param values
	 * @param rowBuffer
	 * @return
	 */
	public StringBuffer rowMaker(String values, StringBuffer rowBuffer) {

		if (values != null && !"".equals(values) && !"null".equalsIgnoreCase(values)) {
			rowBuffer.append(values);
			rowBuffer.append(CommonConstants.DATA_SPLITER);
		} else {
			rowBuffer.append("EMPTY");
			rowBuffer.append(CommonConstants.DATA_SPLITER);
		}
		return rowBuffer;
	}

	@Override
	public StringBuffer appendHeader(String[] rowHeader) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File generateFile(ExportExcelBean exportExcelBean, String fileName,
			List<StringBuffer> recordRowList) throws IOException {
		logger.debug(this.getClass().getName() + " generateFile starts");
		BufferedOutputStream stream = null;
		Workbook workBook = null;
		File xlsxFile = null;
		try {
				// xlsx support
				xlsxFile = new File(exportExcelBean.getServerPathLocation()+File.separator+exportExcelBean.getFileName());
				xlsxFile.createNewFile();
				stream = new BufferedOutputStream(new FileOutputStream(xlsxFile));
			    workBook = new SXSSFWorkbook(1000);
				SXSSFSheet sheet = (SXSSFSheet) workBook.createSheet(exportExcelBean.getReportName());
				addHeaderRow(exportExcelBean, workBook, sheet);
				addxlsxDataRows(exportExcelBean, (SXSSFWorkbook) workBook, sheet);
				workBook.write(stream);
				stream.flush();

		} catch (IOException e) {
			if (xlsxFile != null) {
				xlsxFile.delete();
			}
			throw e;
		} finally {
			if (stream != null)
				stream.close();
		}
		logger.debug(this.getClass().getName() + " generateFile ends");
		return xlsxFile;
	}
	
/*	public static void main(String[] args) {
		
		XlsxExporter exporter = new XlsxExporter();
		ExportExcelBean excelBean = new ExportExcelBean();
		excelBean.setFileName("My_File_today.xlsx");
		String[] columnHeader = new String[10]; 
		for(int i = 0; i<10; i++)
		{
			columnHeader[i] = "Header "+i;
		}
		
		String[] columnDataType = new String[10];
		columnDataType[0] = "number";
		for(int i = 1; i<10; i++)
		{
			if(i % 3 == 0)
			columnDataType[i] = "numaric";
			else if(i % 3 == 1)	
				columnDataType[i] = "String";
			else 
				columnDataType[i] = "date";
		}
		
		excelBean.setColumnHeader(columnHeader);
		excelBean.setColumnDataType(columnDataType);
		excelBean.setPublishDate("published Date");
		excelBean.setReportDesc("reportDesc");
		excelBean.setReportName("reportName");
		excelBean.setServerPathLocation("D:\\rrm\\pubReports");
		
		List<StringBuffer> bufferList = new ArrayList<StringBuffer>();
		StringBuffer rowBuffer = null;
		
		
		for(int k=0; k< 50000; k++)
		{
			rowBuffer = new StringBuffer();
			rowBuffer = exporter.rowMaker(""+1000000, rowBuffer);
			for(int i = 1; i<10; i++)
			{
				if(i % 3 == 0)
				{
					//columnDataType[i] = "numaric";
					rowBuffer = exporter.rowMaker(""+i*1000, rowBuffer);
				}
				else if(i % 3 == 1)
				{
					//columnDataType[i] = "String";
					rowBuffer = exporter.rowMaker("Datatttttttttttt", rowBuffer);
				}
				else 
				{
					rowBuffer = exporter.rowMaker(new Date().toString(), rowBuffer);
					//columnDataType[i] = "date";
				}
			}
			bufferList.add(rowBuffer);
		}
		excelBean.setRowBufferList(bufferList);
		
		try {
			exporter.generateFile(excelBean, "kkkkkk_t.xlsx", bufferList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Done .....................!");
	} */
	

}

