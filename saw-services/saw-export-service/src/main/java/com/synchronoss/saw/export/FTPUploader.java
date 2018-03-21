package com.synchronoss.saw.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.zip.ZipInputStream;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FTPUploader {

    FTPClient ftp = null;
    private static final Logger logger = LoggerFactory.getLogger(FTPUploader.class);

    public FTPUploader(String host, int port, String user, String pwd) throws Exception {
        ftp = new FTPClient();
        int reply;
        ftp.setControlKeepAliveTimeout(300);
        ftp.connect(host, port);
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new Exception("Exception in connecting to FTP Server");
        }
        ftp.login(user, pwd);
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        ftp.enterLocalPassiveMode();
}

    public void uploadFile(String localFileFullName, String fileName, String hostDir)
            throws Exception {
        try (InputStream input = new FileInputStream(new File(localFileFullName))) {
            this.ftp.storeFile(hostDir + fileName, input);
        }
    }

    public void disconnect() {
        if (this.ftp.isConnected()) {
            try {
                this.ftp.logout();
                this.ftp.disconnect();
                logger.debug("Disconnected from ftp server");
            } catch (IOException f) {
                logger.debug("Exception while disconnecting from ftp server" + f.getMessage());
            }
        }
    }
}