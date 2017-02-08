/**
 * 
 */
package com.razor.raw.re.report.process;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




/**
 * 
 * @author surendra.rajaneni
 *
 */
public class OutputFileDetails implements Serializable {

	private static final long serialVersionUID = 6420209275134363011L;
	private static final Logger logger = LoggerFactory.getLogger(OutputFileDetails.class);
	private String outputFileFormat;
	private String outputFileName;

	/**
	 * @return the outputFileFormat
	 */
	public String getOutputFileFormat() {
		return outputFileFormat;
	}

	/**
	 * @param outputFileFormat the outputFileFormat to set
	 */
	public void setOutputFileFormat(String outputFileFormat) {
		this.outputFileFormat = outputFileFormat;
	}

	/**
	 * @return the outputFileName
	 */
	public String getOutputFileName() {
		return outputFileName;
	}

	/**
	 * @param outputFileName the outputFileName to set
	 */
	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	/**
	 * Parametrized Constructor
	 * 
	 * @param outputFile
	 *            : The name of the file
	 * @param fileFormatExtension
	 *            : the file extension
	 */
	public OutputFileDetails(String outputFile, String fileFormatExtension) {
		this.outputFileFormat = fileFormatExtension;
		this.outputFileName = outputFile;

	}

	public String customFileNameWithName() {
		logger.debug("OutputFileDetails - customFileNameWithName - START");
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH_mm");
		Date date = new Date();
		String fileName = outputFileName + "_" + dateFormatter.format(date)
				+ outputFileFormat;
		logger.debug("OutputFileDetails - customFileNameWithName - END - fileName - "+fileName);
		return fileName;
	}

}
