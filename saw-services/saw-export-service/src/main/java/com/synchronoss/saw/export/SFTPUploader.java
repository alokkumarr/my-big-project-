package com.synchronoss.saw.export;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.zip.ZipInputStream;

public class SFTPUploader {

    SSHClient ssh = null;
    SFTPClient ftp = null;
    private static final Logger logger = LoggerFactory.getLogger(SFTPUploader.class);

    public SFTPUploader(String host, int port, String user, String pwd) throws Exception {
        ssh = new SSHClient();
        ssh.loadKnownHosts();
        ssh.connect(host, port);
        ssh.authPassword(user, pwd);
        SFTPClient ftp = ssh.newSFTPClient();
        logger.debug("Connected to sftp server");
    }

    public void uploadFile(String localFileFullName, String fileName, String hostDir)
            throws Exception {
        try (InputStream input = new FileInputStream(new File(localFileFullName))) {
            // ToDo: change this as it doesn't accepts zip input stream
            this.ftp.put(new ZipInputStream(input), hostDir + fileName + ".zip");
        }
    }

    public void disconnect() {
        if(ftp!=null) {
            try {
                ftp.close();
                logger.debug("closed sftp connection");
            } catch (IOException e) {
                logger.debug("Exception in SFTP disconnection: " + e.getMessage());
            }
        }
        if(ssh!=null) {
            try {
                ssh.disconnect();
                logger.debug("closed ssh connection");
            } catch (IOException e) {
                logger.debug("Exception in SSH disconnection: " + e.getMessage());
            }
        }
    }
}
