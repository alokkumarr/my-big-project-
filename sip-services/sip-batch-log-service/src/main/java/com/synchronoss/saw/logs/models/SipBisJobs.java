package com.synchronoss.saw.logs.models;

import java.io.Serializable;
import java.util.List;

public class SipBisJobs implements Serializable {

  /** serialization id. */
  private static final long serialVersionUID = -3249526977895533260L;

  List<JobDetails> jobDetails;
  long totalRows;
  int numOfPages;

  public SipBisJobs(long totalRows, int numOfPages) {
    this.totalRows = totalRows;
    this.numOfPages = numOfPages;
  }

  public long getTotalRows() {
    return totalRows;
  }

  public void setTotalRows(long totalRows) {
    this.totalRows = totalRows;
  }

  public int getNumOfPages() {
    return numOfPages;
  }

  public void setNumOfPages(int numOfPages) {
    this.numOfPages = numOfPages;
  }

  public List<JobDetails> getJobDetails() {
    return jobDetails;
  }

  public void setJobDetails(List<JobDetails> jobDetails) {
    this.jobDetails = jobDetails;
  }
}
