package com.synchronoss.saw.logs.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.List;

@ApiModel("This model payload holds the details to BIS-jobs in the system")
public class SipBisJobs implements Serializable {

  /** serialization id. */
  private static final long serialVersionUID = -3249526977895533260L;

  @ApiModelProperty(value = "Returns list of jobs")
  @JsonProperty("jobDetails")
  List<JobDetails> jobDetails;

  @ApiModelProperty(value = "Returns total rows", dataType = "Long")
  @JsonProperty("totalRows")
  long totalRows;

  @ApiModelProperty(
      value = "Returns total number of pages(default - 10 rows per page)",
      dataType = "Integer")
  @JsonProperty("numOfPages")
  int numOfPages;

  public SipBisJobs(long totalRows, int numOfPages) {
    this.totalRows = totalRows;
    this.numOfPages = numOfPages;
  }

  @JsonProperty("totalRows")
  public long getTotalRows() {
    return totalRows;
  }

  @JsonProperty("totalRows")
  public void setTotalRows(long totalRows) {
    this.totalRows = totalRows;
  }

  @JsonProperty("numOfPages")
  public int getNumOfPages() {
    return numOfPages;
  }

  @JsonProperty("numOfPages")
  public void setNumOfPages(int numOfPages) {
    this.numOfPages = numOfPages;
  }

  @JsonProperty("jobDetails")
  public List<JobDetails> getJobDetails() {
    return jobDetails;
  }

  @JsonProperty("jobDetails")
  public void setJobDetails(List<JobDetails> jobDetails) {
    this.jobDetails = jobDetails;
  }
}
