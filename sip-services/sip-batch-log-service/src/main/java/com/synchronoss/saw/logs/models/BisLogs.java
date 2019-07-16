package com.synchronoss.saw.logs.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synchronoss.saw.logs.entities.BisFileLog;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.List;

@ApiModel("This model payload holds the details to BIS-job-logs in the system")
public class BisLogs implements Serializable {

  /** serialization id. */
  private static final long serialVersionUID = -3249526919895533260L;

  public BisLogs(long totalRows, int numOfPages) {
    this.totalRows = totalRows;
    this.numOfPages = numOfPages;
  }

  @ApiModelProperty(value = "Returns list of File Logs")
  @JsonProperty("bisFileLogs")
  List<BisFileLog> bisFileLogs;

  @ApiModelProperty(value = "Returns total rows", dataType = "Long")
  @JsonProperty("totalRows")
  long totalRows;

  @ApiModelProperty(
      value = "Returns total number of pages(default - 10 rows per page)",
      dataType = "Integer")
  @JsonProperty("numOfPages")
  int numOfPages;

  @JsonProperty("bisFileLogs")
  public List<BisFileLog> getBisFileLogs() {
    return bisFileLogs;
  }

  @JsonProperty("bisFileLogs")
  public void setBisFileLogs(List<BisFileLog> bisFileLogs) {
    this.bisFileLogs = bisFileLogs;
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
}
