package com.razor.distribution;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.razor.distribution.ftp.FTPConnection;
import com.razor.distribution.sftp.SFTPConnection;
import com.razor.raw.core.pojo.CustomerFTP;
import com.razor.raw.logging.LogManager;

public class UploadUtility {
	private static final Logger logger = LoggerFactory.getLogger(UploadUtility.class);
	/**
	 * 
	 * @param logFiles
	 * @param feedProps
	 */
	public void uploadFile(ArrayList<String> logFiles, CustomerFTP customerFTP) {
		logger.debug(" UtilFeedImpl - uploadFile - Start");
		boolean sftpEnable = customerFTP.isSftpOn();
		// for SFTP check
		if (sftpEnable) {
			SFTPUpload(logFiles, customerFTP);
		} else {
			FTPUpload(logFiles, customerFTP);
		}

		logger.debug(" UtilFeedImpl - uploadFile - End");
	}

	/**
	 * upload files using SFTPClient
	 * 
	 * @param logFiles
	 */
	private void SFTPUpload(ArrayList<String> logFiles, CustomerFTP sftpUtil) {
		logger.debug(LogManager.CATEGORY_REPORT, LogManager.LEVEL_INFO, " UtilFeedImpl - SFTPUpload - Start");
		SFTPConnection sftpConnection = new SFTPConnection();
		boolean result;
		try {
			// default port for SFTP
			int port = sftpUtil.getPort();
			if (port == 0)
				port = 22;

			if (sftpConnection.connectAndLogin(sftpUtil.getHost(), port, sftpUtil.getuName(),
					constructPassword(sftpUtil.getPassword()))) {
				for (String fileName : logFiles) {
					result = sftpConnection.uploadFile(fileName,
							sftpUtil.getFolder() + "/" + new File(fileName).getName());
					if (!result) {
						logger.debug(LogManager.CATEGORY_REPORT, LogManager.LEVEL_ERROR,
								" File uploading failed - Exception");
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(this.getClass().getName(), e);
		} catch (Exception e) {
			logger.error(this.getClass().getName(), e);
			e.printStackTrace();
		} finally {
			try {
				if (sftpConnection != null) {
					sftpConnection.disconnect();
				}
			} catch (Exception e) {
				logger.error(this.getClass().getName(), e);
				e.printStackTrace();
			}
		}
		logger.debug(LogManager.CATEGORY_REPORT, LogManager.LEVEL_INFO, " UtilFeedImpl - SFTPUpload - End");
	}

	/**
	 * upload files using FTPClient
	 * 
	 * @param logFiles
	 */
	private void FTPUpload(ArrayList<String> logFiles, CustomerFTP sftpUtil) {
		logger.debug(" UtilFeedImpl - FTPUpload - Start");
		FTPConnection ftpConnection = new FTPConnection();

		boolean result;

		try {
			if (ftpConnection.connectAndLogin(sftpUtil.getHost(), sftpUtil.getuName(), sftpUtil.getPassword())) {

				ftpConnection.setPassiveMode(true);

				for (String fileName : logFiles) {

					// getting corrupted (NISCO00007594)

					if ((fileName != null && fileName.toLowerCase().endsWith(".gz"))
							|| (fileName != null && fileName.toLowerCase().endsWith(".zip"))
							|| (fileName != null && fileName.toLowerCase().endsWith(".xls"))
							|| (fileName != null && fileName.toLowerCase().endsWith(".xlsx"))) {
						ftpConnection.binary();
					} else {
						ftpConnection.ascii();
					}
					// End

					result = ftpConnection.uploadFile(fileName,
							sftpUtil.getFolder() + "/" + new File(fileName).getName());
					if (!result) {
						logger.debug(LogManager.CATEGORY_REPORT, LogManager.LEVEL_ERROR,
								" File uploading failed - Exception");
					}
				}
			}
		} catch (IOException e) {
			logger.error(" File uploading failed - Exception", e);
		} finally {
			try {
				if (ftpConnection != null) {
					ftpConnection.logout();
					ftpConnection.disconnect();
				}
			} catch (Exception e) {
				logger.error(" File uploading failed - Exception", e);
			}
		}
		logger.debug(" UtilFeedImpl - FTPUpload - End");

	}

	/**
	 * 
	 * @param password
	 * @return
	 */
	public String constructPassword(String password) {
		String pass = "";
		char[] passChar = password.toCharArray();
		for (int i = 0; i < passChar.length; i++) {
			switch (passChar[i]) {
			case ' ':
				pass += "%20";
				break;
			case '"':
				pass += "%22";
				break;
			case '#':
				pass += "%23";
				break;
			case '$':
				pass += "%24";
				break;
			case '%':
				pass += "%25";
				break;
			case '&':
				pass += "%26";
				break;
			case '\'':
				pass += "%27";
				break;
			case '(':
				pass += "%28";
				break;
			case ')':
				pass += "%29";
				break;
			case '*':
				pass += "%2A";
				break;
			case '+':
				pass += "%2B";
				break;
			case ',':
				pass += "%2C";
				break;
			case '-':
				pass += "%2D";
				break;
			case '.':
				pass += "%2E";
				break;
			case '/':
				pass += "%2F";
				break;
			case ':':
				pass += "%3A";
				break;
			case ';':
				pass += "%3B";
				break;
			case '<':
				pass += "%3C";
				break;
			case '=':
				pass += "%3D";
				break;
			case '>':
				pass += "%3E";
				break;
			case '?':
				pass += "%3F";
				break;
			case '@':
				pass += "%40";
				break;
			case '\\':
				pass += "%5C";
				break;
			case '^':
				pass += "%5E";
				break;
			case '|':
				pass += "%7C";
				break;
			default:
				pass += passChar[i];
				break;

			}
		}
		return pass;
	}

}
