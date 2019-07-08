package com.synchronoss.saw.logs.models;

import com.synchronoss.saw.logs.entities.BisFileLog;
import java.io.Serializable;
import java.util.List;

public class BisLogs implements Serializable {

  /** serialization id. */
  private static final long serialVersionUID = -3249526919895533260L;

  public BisLogs(long totalRows, int numOfPages) {
    this.totalRows = totalRows;
    this.numOfPages = numOfPages;
  }

  List<BisFileLog> bisFileLogs;
  long totalRows;
  int numOfPages;

  public List<BisFileLog> getBisFileLogs() {
    return bisFileLogs;
  }

  public void setBisFileLogs(List<BisFileLog> bisFileLogs) {
    this.bisFileLogs = bisFileLogs;
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
}
