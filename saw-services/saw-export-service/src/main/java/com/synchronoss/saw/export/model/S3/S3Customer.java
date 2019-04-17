package com.synchronoss.saw.export.model.S3;

import java.util.List;

public class S3Customer {
    public S3Customer() {
    }

    public List<S3Details> s3List;

    public List<S3Details> getS3List() {
        return s3List;
    }

    public void setFtpList(List<S3Details> s3List) {
        this.s3List = s3List;
    }
}
