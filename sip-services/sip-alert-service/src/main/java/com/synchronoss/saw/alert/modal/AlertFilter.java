package com.synchronoss.saw.alert.modal;

import com.synchronoss.saw.model.Field.Type;
import com.synchronoss.saw.model.Model.Operator;
import com.synchronoss.saw.model.Model.Preset;

public class AlertFilter {

  private String fieldName;
  private Object value;
  private Preset preset;
  private Type type;
  private Operator operator;
  private String lte;
  private String gte;

  public AlertFilter() {}

  /**
   * This is parameterized constructor.
   *
   * @param fieldName of type String.
   * @param value is of type Object.
   * @param type is of type Type.
   * @param operator is of type Operator.
   */
  public AlertFilter(String fieldName, Object value, Type type, Operator operator) {
    this.fieldName = fieldName;
    this.value = value;
    this.type = type;
    this.operator = operator;
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public Preset getPreset() {
    return preset;
  }

  public void setPreset(Preset preset) {
    this.preset = preset;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public Operator getOperator() {
    return operator;
  }

  public void setOperator(Operator operator) {
    this.operator = operator;
  }

  public String getLte() {
    return lte;
  }

  public void setLte(String lte) {
    this.lte = lte;
  }

  public String getGte() {
    return gte;
  }

  public void setGte(String gte) {
    this.gte = gte;
  }
}
