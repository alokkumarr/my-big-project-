package com.synchronoss.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.synchronoss.entity.UIArtifact;
import com.synchronoss.entity.UIDashboard;
import com.synchronoss.util.Utility;
import com.synchronoss.util.Versioning;

public class ExcelToJsonConvertor {
	private String filePath;
	private String jsonFileOutputPath;
	
	private int dispayNameColIndex;
	private int dashboardNameColIndex;
	private int esIndexColIndex;
	private int displayOrderIndex;

	private String [] requiredExcelColArr;
	private String [] artifactArray;
	private String [] dashboardNameArray;
	private Logger log = Logger.getLogger(ExcelToJsonConvertor.class.getName());
	private HashMap<String, Integer> jsonColIndexMap = new HashMap<String, Integer>();
	private static String logFilePath;
	
	public ExcelToJsonConvertor() {
		//get various file paths
		this.filePath = Utility.getPropertyValue("excelFilePath",null);
		this.jsonFileOutputPath = Utility.getPropertyValue("jsonFileOutputPath",null);
		ExcelToJsonConvertor.logFilePath = Utility.getPropertyValue("logFilePath",null);
		this.requiredExcelColArr = Utility.getPropertyValue("requiredExcelColArr",null).split(",");
		this.artifactArray = Utility.getPropertyValue("artifactArray",null).split(",");
	}

	/**
	 * This method validates the excel sheet on the basis of required column
	 * names placed in the config.properties file in the config folder
	 * @param firstRow
	 * @return boolean
	 */
	public boolean validateExcelSheet(HashMap<Integer, String> firstRow){
		boolean isFileValid = false;
		Set<String> validationColSet = new HashSet<String>();
		int valuesFound = 0;
		for(String value: firstRow.values()){
			for(int i =0;i<requiredExcelColArr.length;i++){
				if(value.contains(requiredExcelColArr[i])){
					if(validationColSet.add(requiredExcelColArr[i])){
						valuesFound++;
					}
				}
			}
		}
		if(valuesFound==requiredExcelColArr.length){
			isFileValid = true;
			log.info("File is validated");
			System.out.println("File is validated");
		}else{
			log.error("File not Valid! Either of the columns: "+requiredExcelColArr+" not present ");
			System.out.println("File not Valid! Either of the columns: "+requiredExcelColArr+" not present ");
			
			System.exit(1);
		}
		return isFileValid;
	}
	/**
	 * This method is the main method. This method validates the excel file. 
	 * If the file gets validated only then it goes on various processes to 
	 * generate the json files required by the UI.
	 */
	public void createJsonObject(){
		Gson gson = new Gson();
		HashMap<Integer, HashMap<Integer, String>> excelData = null;
		try {
			excelData = Utility.readExcelFile(filePath);
		} catch (IOException e) {
			log.error(e.getStackTrace());
			System.out.println(e.getStackTrace());
			e.printStackTrace();
		}
		if(validateExcelSheet(excelData.get(0))){
			String verString = Versioning.createUpdateVersion();
			populateJsonColIndexMap(excelData.get(0));
			excelData.remove(0);
			ArrayList<UIDashboard> dashboardList = createDashboardObject();
			HashMap<String, ArrayList<HashMap<Integer, String>>> revisedData = dashboardDataSegregator(excelData);
			for (Iterator<UIDashboard> iterator = dashboardList.iterator(); iterator
					.hasNext();) {
				UIDashboard uiDashboard = (UIDashboard) iterator.next();
				String dashboardName = uiDashboard.dashboardName();
				ArrayList<HashMap<Integer, String>> dashboardData = revisedData.get(dashboardName);
				HashMap<String, ArrayList<UIArtifact>> map = artifactListMapGenerator(dashboardData, dashboardName);
				uiDashboard.setData(map);
			}
			try {
				Utility.writeJsonFile(jsonFileOutputPath, "MetadataDashboards_VS_"+verString, gson.toJson(dashboardList));
			} catch (IOException e) {
				log.error(e.getStackTrace());
				System.out.println(e.getStackTrace());
				e.printStackTrace();
			}
		}
	}
	/**
	 * This method generates the HashMap<String, ArrayList<UIArtifact>>
	 * @param dataList
	 * @param dashboardName
	 * @return HashMap<String, ArrayList<UIArtifact>>
	 */
	public HashMap<String, ArrayList<UIArtifact>> artifactListMapGenerator(ArrayList<HashMap<Integer, String>> dataList,String dashboardName){
		HashMap<String, ArrayList<UIArtifact>> finalObject = new HashMap<String, ArrayList<UIArtifact>>();
		for(String key:jsonColIndexMap.keySet()){
			int colIndex = jsonColIndexMap.get(key);
			String artifactName = key;
			ArrayList<UIArtifact> artifactList = new ArrayList<UIArtifact>();
			for(HashMap<Integer, String> rowDataMap:dataList){
				if(rowDataMap.get(colIndex)!=null){
					String displayString = rowDataMap.get(dispayNameColIndex);
					UIArtifact artifact = new UIArtifact();
					artifact.setDisplayName(displayString);
					artifact.setEsIndexName(rowDataMap.get(esIndexColIndex));
					artifact.setEsIndexColName(displayString.replace(" ", "_").toUpperCase());
					artifact.setDashboardName(dashboardName);
					artifact.setType(rowDataMap.get(colIndex));
					artifact.setDisplayIndex(rowDataMap.get(displayOrderIndex)!=null?rowDataMap.get(displayOrderIndex):0);
					artifactList.add(artifact);
				}
			}
			finalObject.put(artifactName, artifactList);
		}
		return finalObject;
	}
	/**
	 * This method is used to segregate the excel data according to 
	 * different dashboards 
	 * @param excelData
	 * @return HashMap<String, ArrayList<HashMap<Integer, String>>>
	 */
	public HashMap<String, ArrayList<HashMap<Integer, String>>> dashboardDataSegregator(HashMap<Integer, HashMap<Integer, String>> excelData){
		HashMap<String, ArrayList<HashMap<Integer, String>>> dashboardBasedDataMap = new HashMap<String, ArrayList<HashMap<Integer,String>>>();
		for (int i = 0; i < dashboardNameArray.length; i++) {
			String dashboardName = dashboardNameArray[i];
			ArrayList<HashMap<Integer, String>> dashBoardDataList = new ArrayList<HashMap<Integer,String>>();
			for(HashMap<Integer, String> value: excelData.values()){
				if(value.get(dashboardNameColIndex).equalsIgnoreCase(dashboardName)){
					dashBoardDataList.add(value);
				}
			}
			dashboardBasedDataMap.put(dashboardName, dashBoardDataList);
		}
		return dashboardBasedDataMap;
	}
	/**
	 * This method is used to find the indexes for the required fields
	 * @param firstRow
	 */
	public void populateJsonColIndexMap(HashMap<Integer, String> firstRow){
		for(Integer key: firstRow.keySet()){
			if(firstRow.get(key).contains("Display_Name")){
				dispayNameColIndex = key;
			}else if(firstRow.get(key).contains("ES_Index")){
				esIndexColIndex = key;
			}else if(firstRow.get(key).contains("Dashboard")){
				dashboardNameColIndex = key;
			}else if(firstRow.get(key).contains("Order")){
				displayOrderIndex = key;
			}
		}
		for(int i=0;i<artifactArray.length;i++){
			for(Integer key: firstRow.keySet()){
				if(firstRow.get(key).equalsIgnoreCase(artifactArray[i])){
					jsonColIndexMap.put(firstRow.get(key), key);
				}
			}
		}
	}
	public ArrayList<UIDashboard> createDashboardObject(){
		ArrayList<UIDashboard> dashboardList = new ArrayList<UIDashboard>();
		ArrayList<String>dashboardNameList = new ArrayList<String>();
		String[]dashbaoardNameWithIndexArray = Utility.getPropertyValue("dashboardDisplayIndexes",null).split(",");
		for (int i = 0; i < dashbaoardNameWithIndexArray.length; i++) {
			String [] outerLoopStringArr = dashbaoardNameWithIndexArray[i].split("_");
			String dashboardName = "";
			int dashboardDisplayOrder = Integer.parseInt(outerLoopStringArr[outerLoopStringArr.length-1]);
			for (int j = 0; j < (outerLoopStringArr.length-1); j++) {
				if(j!=outerLoopStringArr.length-2){
					dashboardName += outerLoopStringArr[j] + " ";
				}else{
					dashboardName += outerLoopStringArr[j];
				}
			}
			dashboardNameList.add(dashboardName);
			UIDashboard dashboard = new UIDashboard();
			dashboard.setDashboardDisplayIndex(dashboardDisplayOrder);
			dashboard.setDashboardName(dashboardName);
			dashboardList.add(dashboard);
		}
		this.dashboardNameArray = dashboardNameList.toArray(new String[dashboardNameList.size()]);
		return dashboardList;
	}
	public static void main(String[] args) {
		ExcelToJsonConvertor convertor = new ExcelToJsonConvertor();
		Utility.convertConsoleToFile(logFilePath);
		convertor.createJsonObject();
	}

}
