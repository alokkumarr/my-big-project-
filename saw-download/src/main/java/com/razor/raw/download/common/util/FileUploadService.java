package com.razor.raw.download.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.razor.raw.logging.LogManager;


/**
 * 
 * @author AJAY.KUMAR
 *This FileUploadUtil is used to store the file in system location 
 */
@Service
public class FileUploadService {
	/**
	 * 
	 * @param sourceFile
	 * @param destFile
	 * @throws IOException
	 */
	public static void uploadFileToSystem(File sourceFile, File destFile) throws IOException {
		LogManager.log(LogManager.CATEGORY_REPORT, LogManager.LEVEL_INFO, " FileUploadUtil - uploadFileToSystem - Start");
	    if(!destFile.exists()) {
	        destFile.createNewFile();
	    }
	    FileChannel source = null;
	    FileChannel destination = null;
	    try {
	        source = new FileInputStream(sourceFile).getChannel();
	        destination = new FileOutputStream(destFile).getChannel();
	        destination.transferFrom(source, 0, source.size());
	        LogManager.log(LogManager.CATEGORY_REPORT, LogManager.LEVEL_INFO, " FileUploadUtil - uploadFileToSystem - End");
	    }catch (Exception e) {
			LogManager.log(LogManager.CATEGORY_REPORT, LogManager.LEVEL_ERROR, "Exception occured at in FileUploadUtil uploadFileToSystem"
					+ LogManager.printStackTrace(e));
		}finally {
	        if(source != null) {
	            source.close();
	        }
	        if(destination != null) {
	            destination.close();
	        }
	    }
	}
	
	/*public static void main(String[] args) throws IOException {
		File file = new File("D:\\temporary\\IMC12c-upgrade.xlsx");
		File desFile = new File("D:\\TestFolder\\IMC12c-upgrade111.xlsx");
		uploadFileToSystem(file, desFile);
		System.out.println("Hello");
	}
	*/
	
}
