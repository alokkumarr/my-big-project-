package com.synchronoss.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.Gson;
import com.synchronoss.entity.UIArtifact;
import com.synchronoss.util.Utility;

public class ExcelToJsonConvertor {
	public String filePath;
	public String jsonFileOutputPath;

	public int dispayNameColIndex;
	public int dashboardNameColIndex;
	public int esIndexColIndex;

	public String [] requiredExcelColArr;
	public String [] artifactArray;
	public String [] dashboardNameArray;

	public HashMap<String, Integer> jsonColIndexMap = new HashMap<String, Integer>();

	public ExcelToJsonConvertor() {
		filePath = Utility.getPropertyValue("excelFilePath");
		requiredExcelColArr = Utility.getPropertyValue("requiredExcelColArr").split(",");
		jsonFileOutputPath = Utility.getPropertyValue("jsonFileOutputPath");
		artifactArray = Utility.getPropertyValue("artifactArray").split(",");
		dashboardNameArray = Utility.getPropertyValue("dashboardNames").split(",");
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
		}else{
			System.out.println("File not Valid");
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
		HashMap<Integer, HashMap<Integer, String>> excelData = Utility.readExcelFile(filePath);
		if(validateExcelSheet(excelData.get(0))){
			populateJsonColIndexMap(excelData.get(0));
			excelData.remove(0);
			HashMap<String, ArrayList<HashMap<Integer, String>>> revisedData = dashboardDataSegregator(excelData);
			for (int i = 0; i < dashboardNameArray.length; i++) {
				String dashboardName = dashboardNameArray[i];
				ArrayList<HashMap<Integer, String>> dashboardData = revisedData.get(dashboardName);
				Utility.writeJsonFile(jsonFileOutputPath, dashboardName, gson.toJson(artifactListMapGenerator(dashboardData,dashboardName)));
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
	public static void main(String[] args) {
		new ExcelToJsonConvertor().createJsonObject();
	}

}
