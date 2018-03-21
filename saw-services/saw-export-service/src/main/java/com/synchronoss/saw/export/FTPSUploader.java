package com.synchronoss.saw.export;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.zip.ZipInputStream;

public class FTPSUploader {

    FTPSClient ftp = null;
    private static final Logger logger = LoggerFactory.getLogger(FTPSUploader.class);

    public FTPSUploader(String host, int port, String user, String pwd) throws Exception {
        ftp = new FTPSClient();
        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        int reply;
        ftp.setControlKeepAliveTimeout(300);
        ftp.connect(host, port);
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new Exception("Exception in connecting to SFTP Server");
        }
        ftp.login(user, pwd);
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        ftp.enterLocalPassiveMode();
    }

    public void uploadFile(String localFileFullName, String fileName, String hostDir)
            throws Exception {
        try (InputStream input = new FileInputStream(new File(localFileFullName))) {
            this.ftp.storeFile(hostDir + fileName + ".zip", new ZipInputStream(input));
        }
    }

    public void disconnect() {
        if (this.ftp.isConnected()) {
            try {
                this.ftp.logout();
                this.ftp.disconnect();
                logger.debug("Disconnected from ftps server");
            } catch (IOException f) {
                logger.debug("Exception while disconnecting from ftps server" + f.getMessage());
            }
        }
    }
}