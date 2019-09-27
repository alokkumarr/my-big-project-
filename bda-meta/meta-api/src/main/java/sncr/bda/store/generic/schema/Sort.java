package sncr.bda.store.generic.schema;

import org.ojai.store.SortOrder;

public class Sort {

  private String fieldName;

  private SortOrder sortOrder;

  public Sort() {}

  public Sort(String fieldName, SortOrder sortOrder) {
    this.fieldName = fieldName;
    this.sortOrder = sortOrder;
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public SortOrder getSortOrder() {
    return sortOrder;
  }

  public void setSortOrder(SortOrder sortOrder) {
    this.sortOrder = sortOrder;
  }
}
