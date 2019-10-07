package sncr.bda.store.generic.schema;

import org.ojai.store.SortOrder;

public class Sort {

  private String fieldName;

  private SortOrder order;

  public Sort() {}

  public Sort(String fieldName, SortOrder order) {
    this.fieldName = fieldName;
    this.order = order;
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public SortOrder getOrder() {
    return order;
  }

  public void setOrder(SortOrder order) {
    this.order = order;
  }
}
