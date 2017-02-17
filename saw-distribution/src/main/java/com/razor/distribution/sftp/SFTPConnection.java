package com.razor.distribution.sftp;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.vfs2.AllFileSelector;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.UserAuthenticator;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.local.LocalFile;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author surendra.rajaneni
 * 
 */
public class SFTPConnection {

	private static final Logger logger = LoggerFactory.getLogger(SFTPConnection.class);
	private StandardFileSystemManager manager = null;
	private static final String FTP_SEPERATOR = "/";
	private FileSystemOptions opts = new FileSystemOptions();
	private String userName = null;
	private String host = null;
	private int port;
	private String password = null;
	private String pwd = null;

	/**
	 * @return the pwd
	 */
	public String getPwd() {
		return pwd;
	}

	/**
	 * @param pwd
	 *            the pwd to set
	 */
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host
	 *            the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

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
	public boolean connectAndLogin(String host, int port, String userName, String password) throws Exception {
		boolean success = false;
		try {
			manager = new StandardFileSystemManager();
			// add any sftp session config values
			this.userName = userName;
			this.host = host;
			this.port = port;
			this.password = password;
			SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");
			SftpFileSystemConfigBuilder.getInstance().setProxyHost(opts, host);
			UserAuthenticator auth = new StaticUserAuthenticator(null, userName, password);
			DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(opts, auth);
			manager.init();
			success = true;
		} catch (Exception e) {
			throw e;
		}

		return success;
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
	public boolean downloadFile(String remoteFile, String localFile) throws Exception {
		boolean copyFlag = false;
		try {
			String remotefilepath = "sftp://" + getUserName() + ":" + getPassword() + "@" + getHost() + ":" + getPort()
					+ FTP_SEPERATOR + remoteFile;
			LocalFile localFileObject = (LocalFile) manager.resolveFile(localFile);
			FileObject remoteFileObj = manager.resolveFile(remotefilepath, opts);
			localFileObject.copyFrom(remoteFileObj, new AllFileSelector());
			copyFlag = true;
		} catch (FileSystemException e) {
			logger.error(" SFTPConnection - retrieveFile - Exception - ", e);
			throw new Exception(e);
		}
		logger.info(" SFTPConnection - retrieveFile - End");
		return copyFlag;
	}

	/**
	 * Upload a file to the server
	 * 
	 * @param localFile
	 * @param serverFile
	 * @return
	 * @throws Exception
	 */
	public boolean uploadFile(String localFile, String remoteFilePath) throws Exception {
		boolean result = false;
		try {
			String remotefilepath = "sftp://" + getUserName() + ":" + getPassword() + "@" + getHost() + ":" + getPort()
					+ FTP_SEPERATOR + remoteFilePath;
			// prepare remote file object for use
			FileObject remoteFile = manager.resolveFile(remotefilepath, opts);
			// prepare local file object
			FileObject localFileObject = manager.resolveFile(localFile);
			// copy from local to remote
			remoteFile.copyFrom(localFileObject, Selectors.SELECT_ALL);
			result = true;
		} catch (Exception e) {
			logger.error(" SFTPConnection - retrieveFile - Exception - ", e);
			throw new Exception(e);
		}
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
			try {
				if (!uploadFile(localFile, serverFile)) {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * from remote directory get all files
	 * 
	 * @return
	 * @throws FileSystemException
	 */
	public String[] listFileNames(String remoteFilePath) throws FileSystemException {
		logger.info(" SFTPConnection - listFileNames - Start");
		FileObject sourceDir = null;
		FileObject[] children = null;
		String[] remoteFileNames = null;
		// sftp://your-ftp-username":your-ftp-password@your-ftp-url/afolder/
		String remotefilepath = "sftp://" + getUserName() + ":" + getPassword() + "@" + getHost() + ":" + getPort()
				+ FTP_SEPERATOR + remoteFilePath;
		try {
			sourceDir = manager.resolveFile(remotefilepath, opts);

			children = sourceDir.getChildren();

			remoteFileNames = new String[children.length];
			for (int i = 0; i < children.length; i++) {
				remoteFileNames[i] = children[i].getName().getBaseName();
			}
			logger.info(" SFTPConnection - listFileNames - End");
			return remoteFileNames;
		} catch (FileSystemException e) {
			logger.error(" SFTPConnection - listFileNames - Exception - ", e);
			throw e;
		}

	}

	public void disconnect() {
		manager.close();
	}

	/**
	 * rename remote file.
	 * 
	 * @param remoteFileName
	 * @param localFilePath
	 * @throws FileSystemException
	 */
	public void rename(String existingFilePath, String newRemoteFilePath) throws Exception {
		logger.info(" SFTPConnection - rename - Start");
		logger.info(" SFTPConnection - rename - existingFilePath - " + existingFilePath + " newRemoteFilePath - "
				+ newRemoteFilePath);
		FileObject newFile;
		String sftpUri = "sftp://" + getUserName() + ":" + getPassword() + "@" + getHost() + ":" + getPort()
				+ FTP_SEPERATOR;

		try {

			FileObject remoteFileObject = manager.resolveFile(sftpUri + existingFilePath);

			newFile = manager.resolveFile(sftpUri + newRemoteFilePath);
			if (remoteFileObject.canRenameTo(newFile)) {
				remoteFileObject.moveTo(newFile);
				logger.info(" SFTPConnection - rename - rename is success");
			}

		} catch (FileSystemException e) {
			logger.error(" SFTPConnection - rename - ExceptionS", e);
			throw new Exception(e);
		}
		logger.info(" SFTPConnection - rename - End");
	}

	public void deleteFile(String remoteFile) throws Exception {
		logger.info(" SFTPConnection - deleteFile - Start");
		logger.info(" SFTPConnection - deleteFile - remoteFile - " + remoteFile);
		String remotefilepath = "sftp://" + getUserName() + ":" + getPassword() + "@" + getHost() + ":" + getPort()
				+ FTP_SEPERATOR + remoteFile;
		FileObject remoteFileObj;
		try {
			remoteFileObj = manager.resolveFile(remotefilepath, opts);
			if (remoteFileObj.exists())
				remoteFileObj.delete();
		} catch (FileSystemException e) {
			logger.error(" SFTPConnection - deleteFile - Exception", e);
			throw new Exception(e);
		}
		logger.info(" SFTPConnection - deleteFile - End");
	}

}

