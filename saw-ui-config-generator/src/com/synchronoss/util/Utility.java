package com.synchronoss.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Utility {
	private static String filePath = "config/config.properties";
	
	public static HashMap<Integer, HashMap<Integer, String>> readExcelFile(String filePath){
		HashMap<Integer,HashMap<Integer, String>> rowMap = new HashMap<Integer,HashMap<Integer,String>>();
		try {
			InputStream inputStream = new FileInputStream(filePath);
			XSSFWorkbook  workBook = new XSSFWorkbook(inputStream);
			XSSFSheet sheet = workBook.getSheetAt(0);
			XSSFRow row; 
			XSSFCell cell;
			Iterator<?> rows = sheet.rowIterator();
			while (rows.hasNext()){
				row=(XSSFRow) rows.next();
				if(!(row.getCell(0).getCellType() == Cell.CELL_TYPE_BLANK)){
					int index = row.getRowNum();
					Iterator<?> cells = row.cellIterator();
					HashMap<Integer, String> cellMap = new HashMap<Integer, String>();
					while (cells.hasNext())
					{
						cell=(XSSFCell) cells.next();
						if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING)
						{
							cellMap.put(cell.getColumnIndex(), cell.getStringCellValue());
						}
					}
					rowMap.put(index, cellMap);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rowMap;
	}
	
	public static HashMap<String, String> readProperties(){
		Properties prop = new Properties();
		InputStream input = null;
		HashMap<String, String> configMap = new HashMap<String, String>();
		try {
			input = new FileInputStream(filePath);
			prop.load(input);
			for (String key : prop.stringPropertyNames()) {
			    String value = prop.getProperty(key);
			    configMap.put(key, value);
			}
			return configMap;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getPropertyValue(String key){
		HashMap<String, String> configMap = readProperties();
		if(configMap!=null){
			return configMap.get(key);
		}
		return null;
	}
	
	public static void writeJsonFile(String filePath,String fileName,String jsonString){
		File directory = new File(filePath);
		if(!directory.exists()){
			directory.mkdir();
		}
		File file = new File(directory,fileName+".json");
		try {
			FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
			fileWriter.write(jsonString);
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
