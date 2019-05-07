package com.synchronoss.saw.export.model.ftp;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({
        "ftp"
})
public class FTP {

    @JsonProperty("ftp")
    private List<String> ftp;

    @JsonProperty("ftp")
    public List<String> getFtp() {
        return ftp;
    }

    @JsonProperty("ftp")
    public void setFtp(List<String> ftp) {
        this.ftp = ftp;
    }
}
