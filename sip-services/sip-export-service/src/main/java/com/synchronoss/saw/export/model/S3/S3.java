package com.synchronoss.saw.export.model.S3;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonPropertyOrder({
    "S3"
})
public class S3 {
    @JsonProperty("S3")
    private List<String> s3;

    @JsonProperty("S3")
    public List<String> getS3() {
        return s3;
    }

    @JsonProperty("S3")
    public void setS3(List<String> s3) {
        this.s3 = s3;
    }

}
