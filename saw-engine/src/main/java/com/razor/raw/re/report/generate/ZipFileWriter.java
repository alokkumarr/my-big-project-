package com.razor.raw.re.report.generate;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ZipFileWriter {
	private static final Logger logger = LoggerFactory.getLogger(ZipFileWriter.class);
	
	private static int BUF_SIZE = 8192;

    public static void writeZip( File inputFile) {
    	logger.debug("ZipFileWriter - writeZip - START");
        ZipOutputStream zipOutputStream = null;

        try {
            zipOutputStream = new ZipOutputStream( new BufferedOutputStream( new FileOutputStream( new File(
                            inputFile.getAbsolutePath().replaceAll("(?i).csv", ".zip")))));
            logger.debug("zip file name here :"+ "ZIP : " + inputFile.getAbsolutePath());
        }
        catch( IOException e) {
            e.printStackTrace();
            logger.error("writeZip :",e);
        }

        byte[] inputBuffer = new byte[ BUF_SIZE];
        int len = 0;
        try {

            ZipEntry zipEntry = new ZipEntry( inputFile.getName());
            zipOutputStream.putNextEntry( zipEntry);

            BufferedInputStream source = new BufferedInputStream( new FileInputStream( inputFile), BUF_SIZE);

            while( (len = source.read( inputBuffer, 0, BUF_SIZE)) != -1)
                zipOutputStream.write( inputBuffer, 0, len);
            source.close();
            zipOutputStream.close();
        }
        catch( IOException e) {
            e.printStackTrace();
            logger.error("read/write zip :",e);
        }
        logger.debug("ZipFileWriter - writeZip - END");
    }

	/**
	 * @param args
	 */
	/*public static void main(String[] args) {
		// TODO Auto-generated method stub

		writeZip(new File("D://pubReports//Email test_2012-08-21_10_45.csv"));
	}*/

}
