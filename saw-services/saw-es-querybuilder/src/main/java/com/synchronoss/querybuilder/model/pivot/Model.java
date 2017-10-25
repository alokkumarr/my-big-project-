
package com.synchronoss.querybuilder.model.pivot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "booleanCriteria",
    "operator",
    "dynamic",
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
    @JsonProperty("dynamic")
    private Model.Dynamic dynamic;
    @JsonProperty("value")
    private Integer value;
    @JsonProperty("otherValue")
    private Integer otherValue;
    @JsonProperty("gte")
    private String gte;
    @JsonProperty("lte")
    private String lte;
    @JsonProperty("format")
    private String format;
    @JsonProperty("modelValues")
    private List<Object> modelValues = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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
    
    @JsonProperty("dynamic")
    public Model.Dynamic getDynamic() {
      return dynamic;
    }
    @JsonProperty("dynamic")
    public void setDynamic(Model.Dynamic dynamic) {
      this.dynamic = dynamic;
    }
    @JsonProperty("value")
    public Integer getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(Integer value) {
        this.value = value;
    }

    @JsonProperty("otherValue")
    public Integer getOtherValue() {
        return otherValue;
    }

    @JsonProperty("otherValue")
    public void setOtherValue(Integer otherValue) {
        this.otherValue = otherValue;
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

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public enum BooleanCriteria {

        AND("AND"),
        OR("OR");
        private final String value;
        private final static Map<String, Model.BooleanCriteria> CONSTANTS = new HashMap<String, Model.BooleanCriteria>();

        static {
            for (Model.BooleanCriteria c: values()) {
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

        @JsonCreator
        public static Model.BooleanCriteria fromValue(String value) {
            Model.BooleanCriteria constant = CONSTANTS.get(value);
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
        BTW("BTW");
        private final String value;
        private final static Map<String, Model.Operator> CONSTANTS = new HashMap<String, Model.Operator>();

        static {
            for (Model.Operator c: values()) {
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

        @JsonCreator
        public static Model.Operator fromValue(String value) {
            Model.Operator constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }
    
    public enum Dynamic {

      YTD("YTD"),
      MTD("MTD"),
      LTM("Last 3 months"),
      LSM("Last Six Months"),
      LM("Last Month"),
      LQ("Last Quarter"),
      LW("Last Week"),
      TW("This week"),
      LTW("Last two weeks");
      private final String value;
      private final static Map<String, Model.Dynamic> CONSTANTS = new HashMap<String, Model.Dynamic>();

      static {
          for (Model.Dynamic c: values()) {
              CONSTANTS.put(c.value, c);
          }
      }

      private Dynamic(String value) {
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

      @JsonCreator
      public static Model.Dynamic fromValue(String value) {
          Model.Dynamic constant = CONSTANTS.get(value);
          if (constant == null) {
              throw new IllegalArgumentException(value);
          } else {
              return constant;
          }
      }

  }

}
