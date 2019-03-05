package com.synchronoss.saw.semantic.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataSets {

  private List<DataSet> dataSets;

  public List<DataSet> getDataSets() {
    return dataSets;
  }

  public void setDataSets(List<DataSet> dataSets) {
    this.dataSets = dataSets;
  }
}
