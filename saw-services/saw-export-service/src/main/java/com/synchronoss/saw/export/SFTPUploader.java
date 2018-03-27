package com.synchronoss.saw.export;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLEncoder;

public class SFTPUploader {

    StandardFileSystemManager manager = null;
    String sftpURL = null;

    private static final Logger logger = LoggerFactory.getLogger(SFTPUploader.class);

    public SFTPUploader(String host, int port, String user, String pwd) throws Exception {
        // Username and Password may or may not have spaces so no trimming required
        // URLEncoding them is necessary because of special characters in username / password.
        String username = URLEncoder.encode(user, "UTF-8");
        String password = URLEncoder.encode(pwd, "UTF-8");
        sftpURL = "sftp://" + username + ":" + password + "@" + host.trim() + ":" + Integer.toString(port) + "/";
        manager = new StandardFileSystemManager();
    }

    public void uploadFile(String localFileFullName, String fileName, String hostDir)
            throws Exception {
        try {
            // local zipped
            File file = new File(localFileFullName);
            // URL preparation
            sftpURL = sftpURL + "/" + hostDir.trim() + "/" + fileName.trim();
            logger.debug("sftpURL: " + sftpURL);
            manager.init();

            // Filesystem Options
            FileSystemOptions opts = new FileSystemOptions();
            SftpFileSystemConfigBuilder sftpBuilder = SftpFileSystemConfigBuilder.getInstance();
            sftpBuilder.setStrictHostKeyChecking(opts, "no");
            sftpBuilder.setUserDirIsRoot(opts, true);
            // ToDo: Make it configurable
            // 10 minutes
            sftpBuilder.setTimeout(opts, 600000);

            // Local and remote file location preparation
            FileObject localFile = manager.resolveFile(file.getAbsolutePath());
            FileObject remoteFile = manager.resolveFile(sftpURL, opts);

            logger.debug("Just before uploading it to server");

            // Copy the actual file
            remoteFile.copyFrom(localFile, Selectors.SELECT_SELF);
        } catch (IOException e) {
            logger.error("SFTP Error: "+ e.getMessage());
        }
    }

    public void disconnect() {
        if(manager!=null) {
            try {
                manager.close();
                logger.debug("closed sftp connection");
            } catch (Exception e) {
                logger.debug("Exception in SFTP disconnection: " + e.getMessage());
            }
        }
    }
}
