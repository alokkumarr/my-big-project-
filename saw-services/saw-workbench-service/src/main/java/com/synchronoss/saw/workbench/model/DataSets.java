
package com.synchronoss.saw.workbench.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

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
