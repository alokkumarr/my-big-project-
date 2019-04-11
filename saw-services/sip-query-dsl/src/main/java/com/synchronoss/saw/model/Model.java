package com.synchronoss.saw.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "booleanCriteria",
    "operator",
    "preset",
    "value",
    "otherValue",
    "gte",
    "lte",
    "format",
    "modelValues"
})
public class Model {

  @JsonProperty("booleanCriteria")
  private Model.BooleanCriteria booleanCriteria;

  @JsonProperty("operator")
  private Model.Operator operator;

  @JsonProperty("preset")
  private Model.Preset preset;

  @JsonProperty("value")
  private Double value;

  @JsonProperty("otherValue")
  private Double otherValue;

  @JsonProperty("gt")
  private String gt;

  @JsonProperty("lt")
  private String lt;

  @JsonProperty("gte")
  private String gte;

  @JsonProperty("lte")
  private String lte;

  @JsonProperty("format")
  private String format;

  @JsonProperty("modelValues")
  private List<Object> modelValues = null;

  @JsonProperty("booleanCriteria")
  public Model.BooleanCriteria getBooleanCriteria() {
    return booleanCriteria;
  }

  @JsonProperty("booleanCriteria")
  public void setBooleanCriteria(Model.BooleanCriteria booleanCriteria) {
    this.booleanCriteria = booleanCriteria;
  }

  @JsonProperty("operator")
  public Model.Operator getOperator() {
    return operator;
  }

  @JsonProperty("operator")
  public void setOperator(Model.Operator operator) {
    this.operator = operator;
  }

  @JsonProperty("preset")
  public Model.Preset getPreset() {
    return preset;
  }

  @JsonProperty("preset")
  public void setPreset(Model.Preset preset) {
    this.preset = preset;
  }

  @JsonProperty("value")
  public Double getValue() {
    return value;
  }

  @JsonProperty("value")
  public void setValue(Double value) {
    this.value = value;
  }

  @JsonProperty("otherValue")
  public Double getOtherValue() {
    return otherValue;
  }

  @JsonProperty("otherValue")
  public void setOtherValue(Double otherValue) {
    this.otherValue = otherValue;
  }

  @JsonProperty("gt")
  public String getGt() {
    return gt;
  }

  @JsonProperty("gt")
  public void setGt(String gt) {
    this.gt = gt;
  }

  @JsonProperty("lt")
  public String getLt() {
    return lt;
  }

  @JsonProperty("lt")
  public void setLt(String lt) {
    this.lt = lt;
  }

  @JsonProperty("gte")
  public String getGte() {
    return gte;
  }

  @JsonProperty("gte")
  public void setGte(String gte) {
    this.gte = gte;
  }

  @JsonProperty("lte")
  public String getLte() {
    return lte;
  }

  @JsonProperty("lte")
  public void setLte(String lte) {
    this.lte = lte;
  }

  @JsonProperty("format")
  public String getFormat() {
    return format;
  }

  @JsonProperty("format")
  public void setFormat(String format) {
    this.format = format;
  }

  @JsonProperty("modelValues")
  public List<Object> getModelValues() {
    return modelValues;
  }

  @JsonProperty("modelValues")
  public void setModelValues(List<Object> modelValues) {
    this.modelValues = modelValues;
  }

  public enum BooleanCriteria {
    AND("AND"),
    OR("OR");
    private final String value;
    private static final Map<String, Model.BooleanCriteria> CONSTANTS =
        new HashMap<>();

    static {
      for (Model.BooleanCriteria c : values()) {
        CONSTANTS.put(c.value, c);
      }
    }

    private BooleanCriteria(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return this.value;
    }

    @JsonValue
    public String value() {
      return this.value;
    }

    /**
     * Creates BooleanCrieteria Object from a string value.
     *
     * @param value Enum value in String
     * @return
     */
    @JsonCreator
    public static Model.BooleanCriteria fromValue(String value) {
      Model.BooleanCriteria constant = CONSTANTS.get(value.toUpperCase());
      if (constant == null) {
        throw new IllegalArgumentException(value);
      } else {
        return constant;
      }
    }
  }

  public enum Operator {
    GT("GT"),
    LT("LT"),
    GTE("GTE"),
    LTE("LTE"),
    EQ("EQ"),
    NEQ("NEQ"),
    BTW("BTW"),
    SW("SW"),
    EW("EW"),
    CONTAINS("CONTAINS"),
    ISIN("ISIN"),
    ISNOTIN("ISNOTIN");
    private final String value;
    private static final Map<String, Model.Operator> CONSTANTS =
        new HashMap<>();

    static {
      for (Model.Operator c : values()) {
        CONSTANTS.put(c.value, c);
      }
    }

    private Operator(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return this.value;
    }

    @JsonValue
    public String value() {
      return this.value;
    }

    /**
     * Creates Operator Object from a string value.
     *
     * @param value Enum value in String
     * @return
     */
    @JsonCreator
    public static Model.Operator fromValue(String value) {
      Model.Operator constant = CONSTANTS.get(value.toUpperCase());
      if (constant == null) {
        throw new IllegalArgumentException(value);
      } else {
        return constant;
      }
    }
  }

  public enum Preset {
    Yesterday("YESTERDAY"),
    Today("TODAY"),
    YTD("YTD"),
    MTD("MTD"),
    LTM("LTM"),
    LSM("LSM"),
    LM("LM"),
    LQ("LQ"),
    LY("LY"),
    LW("LW"),
    TW("TW"),
    LSW("LSW"),
    LTW("LTW"),
    NA("NA");
    private final String value;
    private static final Map<String, Model.Preset> CONSTANTS = new HashMap<>();

    static {
      for (Model.Preset c : values()) {
        CONSTANTS.put(c.value, c);
      }
    }

    private Preset(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return this.value.toUpperCase();
    }

    @JsonValue
    public String value() {
      return this.value.toUpperCase();
    }

    /**
     * Creates Preset Object from a string value.
     *
     * @param value Enum value in String
     * @return
     */
    @JsonCreator
    public static Model.Preset fromValue(String value) {
      Model.Preset constant = CONSTANTS.get(value.toUpperCase());
      if (constant == null) {
        throw new IllegalArgumentException(value);
      } else {
        return constant;
      }
    }
  }
}
