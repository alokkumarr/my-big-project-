package com.razor.raw.re.report.generate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.razor.raw.re.report.interfaces.IFileExporter;




public class CSVReportDataExporter implements IFileExporter{
	private static final Logger logger = LoggerFactory.getLogger(CSVReportDataExporter.class);
	/**
	 * This methd used for generating CSV header
	 * 
	 */
	public StringBuffer appendHeader(String[] rowHeader){
		logger.debug("User activity started here:" + this.getClass().getName()	+ " appendCSVHeader method");
		StringBuffer csvHeader = new StringBuffer();
		for (int i = 0; i < rowHeader.length; i++) {
			csvHeader.append("\"");
			csvHeader.append(rowHeader[i]);
			csvHeader.append("\"");
			if(i < rowHeader.length-1){
				csvHeader.append(",");
			}
		}
		logger.debug("User activity ended here:" + this.getClass().getName()+ " appendCSVHeader method");
		return csvHeader;
	}
	/**
	 * This method is used to make a single row data to be written in CSV file
	 * 
	 * @param values
	 * @param rowBuffer
	 * @return
	 */
	public StringBuffer rowMaker(String values, StringBuffer rowBuffer) {
		if (values != null && !"".equals(values)) {
			rowBuffer.append("\"");
			rowBuffer.append(values);
			rowBuffer.append("\"");
		} else {
			rowBuffer.append("\"");
			rowBuffer.append("\"");
		}
		return rowBuffer;
	}
	
	public File generateFile(ExportExcelBean exportExcelBean, String fileName, List<StringBuffer> recordRowList) {
    	BufferedWriter writer = null;
    	 File file = null;
    	logger.debug("User activity started here:" + this.getClass().getName()+ " generateCSV method");
        try {
            logger.debug("User activity started here:" + this.getClass().getName()	+ " generateCSV method");
            file = new File(fileName);
            writer = new BufferedWriter( new FileWriter( fileName));
            if(recordRowList != null){
            	for(StringBuffer sb : recordRowList){
                	String printLine = sb.toString();
                    writer.write( printLine);
                    writer.newLine();
                }
            }
           
        }catch( Exception e) {
        	logger.error("Exception occured while writing CSV file:" + this.getClass().getName()+ " generateCSV method", e);
        } finally{
        	try{
        		if(writer!=null){
        			writer.close();
        		}
        	} catch(Exception ex){
        		logger.error("Exception occured while closing CSV file:" + this.getClass().getName()+ " generateCSV method");
        	}
        }
        
        logger.debug("User activity Ends here:" + this.getClass().getName()+ " generateCSV method");
        return file;
    }
	
	public static boolean deleteCSVFile(File sourceFile,	boolean isDeleteSourceFile) throws IOException {
		logger.debug(" Requested CSV file to deleted  :" + CSVReportDataExporter.class
				+ sourceFile.getAbsolutePath());
		if (!sourceFile.exists())
			return false;

		if (isDeleteSourceFile) {
			sourceFile.delete();
		}
		return true;
	}
}
