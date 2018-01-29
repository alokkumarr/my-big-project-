package com.synchronoss.saw.storage.proxy.model;

import java.util.List;

/**
 * @author spau0004
 *
 */
public class TabularFormat {

  private String columnHeader;
  private List<Object> data;
  public String getColumnHeader() {
    return columnHeader;
  }
  public void setColumnHeader(String columnHeader) {
    this.columnHeader = columnHeader;
  }
  public List<Object> getData() {
    return data;
  }
  public void setData(List<Object> data) {
    this.data = data;
  }
 
}
