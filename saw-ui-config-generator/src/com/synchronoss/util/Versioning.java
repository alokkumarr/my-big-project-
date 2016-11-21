package com.synchronoss.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.synchronoss.main.ExcelToJsonConvertor;

public class Versioning {
	private static String previousVersion;
	private static String currentVersion;
	private static String isSubVersion = "false";
	private static String versionPropFilePath = "config/version.properties";
	
	public static String createUpdateVersion(){
		Versioning.previousVersion  = Utility.getPropertyValue("previous_version", versionPropFilePath);
		Versioning.currentVersion  = Utility.getPropertyValue("current_version", versionPropFilePath);
		Versioning.isSubVersion = Utility.getPropertyValue("isSubversion", null);
	
		if(!isSubVersion.equalsIgnoreCase("true")){
			if(currentVersion.equals(previousVersion)){
				if (currentVersion=="0.0.0") {
					currentVersion = "1.0.0";
					previousVersion = currentVersion;
				}else{
					String restOfString = currentVersion.substring(1);
					int version = Integer.parseInt(currentVersion.substring(0,1))+1;
					currentVersion = ""+version + restOfString;
				}
			}else{
				previousVersion = currentVersion;
				String restOfString = currentVersion.substring(1);
				int version = Integer.parseInt(currentVersion.substring(0,1))+1;
				currentVersion = ""+version + restOfString;
			}
		}else{
			int subVersion = Integer.parseInt(currentVersion.substring(currentVersion.lastIndexOf(".")+1))+1;
			previousVersion = currentVersion;
			currentVersion = currentVersion.substring(0, currentVersion.lastIndexOf(".")+1)+subVersion;
		}
		updatePropertyFile();
		return currentVersion;
	}
	
	public static void updatePropertyFile(){
		Properties props = new Properties();
		FileInputStream inputStream;
		FileOutputStream outputStream = null;
		try {
			inputStream = new FileInputStream(new File(versionPropFilePath));
			props.load(inputStream);
			props.setProperty("previous_version", previousVersion);
			props.setProperty("current_version", currentVersion);
			inputStream.close();
			outputStream = new FileOutputStream(new File(versionPropFilePath));
			props.store(outputStream, null);
		} catch (IOException e) {
			System.out.println(e.getStackTrace());
		}finally{
			try {
				outputStream.close();
			} catch (IOException e) {
				System.out.println(e.getStackTrace());
			}
		}
	}
}
