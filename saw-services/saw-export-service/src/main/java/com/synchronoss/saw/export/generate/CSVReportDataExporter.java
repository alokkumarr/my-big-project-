package com.synchronoss.saw.export.generate;

import com.synchronoss.saw.export.generate.interfaces.IFileExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;



public class CSVReportDataExporter implements IFileExporter {
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
	    if(rowBuffer.length()>0)
        {
            // append delimeter
            rowBuffer.append(",");
        }
		if (values != null && !("".equals(values) || ("null".equalsIgnoreCase(values)))) {
			rowBuffer.append("\"");
			rowBuffer.append(values);
			rowBuffer.append("\"");
		} else {
			rowBuffer.append("\"");
			rowBuffer.append("\"");
		}
		return rowBuffer;
	}
	
	public File generateFile(ExportBean exportBean, List<Object> recordRowList) {
    	BufferedWriter writer = null;
    	 File file = null;
    	logger.debug(" Activity started here:" + this.getClass().getName()+ " generateCSV method");
        try {
           StringBuffer rowbuffer =null;
           String [] header = null;
            file = new File(exportBean.getFileName());
            writer = new BufferedWriter( new FileWriter(exportBean.getFileName()));
            if(recordRowList != null){
            	for(Object data : recordRowList){
            		if(data instanceof LinkedHashMap) {
            		    rowbuffer = new StringBuffer();
            		    if (exportBean.getColumnHeader()==null || exportBean.getColumnHeader().length==0) {
                            Object [] obj = ((LinkedHashMap) data).keySet().toArray();
                             header = Arrays.copyOf(obj,
                                        obj.length, String[].class);
                            exportBean.setColumnHeader(header);
                            writer.write(this.appendHeader(header).toString());
                            writer.newLine();
                        }
            		  for (String val : header)
            		  if (val instanceof String) {
            		        String value = String.valueOf(((LinkedHashMap) data).get(val));
                         rowbuffer = this.rowMaker(value ,rowbuffer);
                      }
                    }
                	String printLine = rowbuffer.toString();
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
        
        logger.debug(" Activity Ends here:" + this.getClass().getName()+ " generateCSV method");
        return file;
    }


}
