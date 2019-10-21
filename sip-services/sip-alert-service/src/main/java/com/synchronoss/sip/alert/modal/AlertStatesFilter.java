package com.synchronoss.sip.alert.modal;

import java.util.List;
import sncr.bda.store.generic.schema.Sort;

public class AlertStatesFilter {

  private List<AlertFilter> filters;
  private List<Sort> sorts;

  public List<AlertFilter> getFilters() {
    return filters;
  }

  public void setFilters(List<AlertFilter> filters) {
    this.filters = filters;
  }

  public List<Sort> getSorts() {
    return sorts;
  }

  public void setSorts(List<Sort> sorts) {
    this.sorts = sorts;
  }
}
