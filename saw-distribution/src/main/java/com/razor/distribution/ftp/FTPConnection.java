/*
 * FILE            : FTPConnection.java
 *
 * PACKAGE         : package package com.nisco.sf.util;
 *
 * AUTHOR          : Surendra Rajaneni
 *
 * DATE            :  13-06-2014
 *
 * VERSION         : 1.0
 *
 * ABSTRACT        : Description of the class
 *
 * 
 *
 */

package com.razor.distribution.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author This is a simple wrapper around the Jakarta Commons FTP library.
 *         <p/>
 *         If you want more information on the Jakarta Commons libraries (there
 *         is a LOT more you can do than what you see here), go to:
 *         http://jakarta.apache.org/commons
 *         <p/>
 */
public class FTPConnection extends FTPClient {
	private static final Logger logger = LoggerFactory.getLogger(FTPConnection.class);
	/**
	 * A convenience method for connecting and logging in
	 * 
	 * @param host
	 * @param userName
	 * @param password
	 * @return
	 * @throws IOException
	 * @throws UnknownHostException
	 * @throws FTPConnectionClosedException
	 */
	public boolean connectAndLogin(String host, String userName, String password) throws IOException,
			UnknownHostException, FTPConnectionClosedException {
		boolean success = false;
		connect(host);
		int reply = getReplyCode();
		if (FTPReply.isPositiveCompletion(reply))
			success = login(userName, password);
		logger.debug(" connectAndLogin - "+success);
		if (!success)
			disconnect();
		return success;
	}

	/**
	 * Turn passive transfer mode on or off
	 * 
	 * @param setPassive
	 */
	public void setPassiveMode(boolean setPassive) {
		if (setPassive)
			enterLocalPassiveMode();
		else
			enterLocalActiveMode();
	}

	/**
	 * Use ASCII mode for file transfers
	 * 
	 * @return
	 * @throws IOException
	 */
	public boolean ascii() throws IOException {
		return setFileType(FTP.ASCII_FILE_TYPE);
	}

	/**
	 * Use Binary mode for file transfers
	 * 
	 * @return
	 * @throws IOException
	 */
	public boolean binary() throws IOException {
		return setFileType(FTP.BINARY_FILE_TYPE);
	}

	/**
	 * Download a file from the server, and save it to the specified local file
	 * 
	 * @param serverFile
	 * @param localFile
	 * @return
	 * @throws IOException
	 * @throws FTPConnectionClosedException
	 */
	public boolean downloadFile(String serverFile, String localFile) throws IOException, FTPConnectionClosedException {
		FileOutputStream out = new FileOutputStream(localFile);
		boolean result = retrieveFile(serverFile, out);
		out.close();
		return result;
	}

	public boolean downloadFiles(String localDir, String serverDir) throws IOException, FTPConnectionClosedException {
		String fileNames[] = listNames(serverDir);
		for (int i = 0; i < fileNames.length; i++) {
			if (!downloadFile(serverDir + File.separator + fileNames[i], localDir + File.separator + fileNames[i]))
				return false;
		}
		return true;
	}

	/**
	 * Upload a file to the server
	 * 
	 * @param localFile
	 * @param serverFile
	 * @return
	 * @throws IOException
	 * @throws FTPConnectionClosedException
	 */
	public boolean uploadFile(String localFile, String serverFile) throws IOException, FTPConnectionClosedException {
		FileInputStream in = new FileInputStream(localFile);
		boolean result = storeFile(serverFile, in);
		in.close();
		logger.debug(" uploadFile - "+result);
		return result;
	}

	public boolean uploadFiles(String localDir, String serverDir) throws IOException, FTPConnectionClosedException {
		File file = new File(localDir);
		String[] fileNames = file.list();
		if (null == fileNames) {
			return false;
		}
		int size = fileNames.length;
		for (int x = 0; x < size; x++) {
			String localFile = localDir + File.separator + fileNames[x];
			String serverFile = serverDir + File.separator + fileNames[x];
			if (!uploadFile(localFile, serverFile)) {
				return false;
			}
		}
		return true;
	}

}
