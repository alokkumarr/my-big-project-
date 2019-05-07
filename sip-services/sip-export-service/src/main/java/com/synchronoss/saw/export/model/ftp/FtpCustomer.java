package com.synchronoss.saw.export.model.ftp;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class FtpCustomer {

    public FtpCustomer() {
    }

    public List<FTPDetails> ftpList;

    public List<FTPDetails> getFtpList() {
        return ftpList;
    }

    public void setFtpList(List<FTPDetails> ftpList) {
        this.ftpList = ftpList;
    }
}
