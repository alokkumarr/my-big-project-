package com.synchronoss.saw.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "type",
  "artifactsName",
  "isOptional",
  "columnName",
  "isRuntimeFilter",
  "isGlobalFilter",
  "model"
})
public class Filter {

  @JsonProperty("type")
  private Type type;

  @JsonProperty("artifactsName")
  private String artifactsName;

  @JsonProperty("isOptional")
  private Boolean isOptional;

  @JsonProperty("columnName")
  private String columnName;

  @JsonProperty("isRuntimeFilter")
  private Boolean isRuntimeFilter;

  @JsonProperty("isGlobalFilter")
  private Boolean isGlobalFilter;

  @JsonProperty("isAggregationFilter")
  private Boolean isAggregationFilter;

  @JsonProperty("aggregate")
  private Aggregate aggregate;

  @JsonProperty("model")
  private Model model;

  @JsonProperty("description")
  private String description;

  @JsonProperty("type")
  public Type getType() {
    return type;
  }

  @JsonProperty("type")
  public void setType(Type type) {
    this.type = type;
  }

  @JsonProperty("artifactsName")
  public String getArtifactsName() {
    return artifactsName;
  }

  @JsonProperty("artifactsName")
  public void setArtifactsName(String artifactsName) {
    this.artifactsName = artifactsName;
  }

  @JsonProperty("isOptional")
  public Boolean getIsOptional() {
    return isOptional;
  }

  @JsonProperty("isOptional")
  public void setIsOptional(Boolean isOptional) {
    this.isOptional = isOptional;
  }

  @JsonProperty("columnName")
  public String getColumnName() {
    return columnName;
  }

  @JsonProperty("columnName")
  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  @JsonProperty("isRuntimeFilter")
  public Boolean getIsRuntimeFilter() {
    return isRuntimeFilter;
  }

  @JsonProperty("isRuntimeFilter")
  public void setIsRuntimeFilter(Boolean isRuntimeFilter) {
    this.isRuntimeFilter = isRuntimeFilter;
  }

  @JsonProperty("isGlobalFilter")
  public Boolean getIsGlobalFilter() {
    return isGlobalFilter;
  }

  @JsonProperty("isGlobalFilter")
  public void setIsGlobalFilter(Boolean isGlobalFilter) {
    this.isGlobalFilter = isGlobalFilter;
  }

  /**
   * Gets isAggregationFilter
   *
   * @return value of isAggregationFilter
   */
  public Boolean getAggregationFilter() {
    return isAggregationFilter;
  }

  /** Sets isAggregationFilter */
  public void setAggregationFilter(Boolean aggregationFilter) {
    isAggregationFilter = aggregationFilter;
  }

  @JsonProperty("aggregate")
  public Aggregate getAggregate() {
    return aggregate;
  }

  @JsonProperty("aggregate")
  public void setAggregate(Aggregate aggregate) {
    this.aggregate = aggregate;
  }

  @JsonProperty("model")
  public Model getModel() {
    return model;
  }

  @JsonProperty("model")
  public void setModel(Model model) {
    this.model = model;
  }

  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  @JsonProperty("description")
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("type", type)
        .append("artifactsName", artifactsName)
        .append("isOptional", isOptional)
        .append("columnName", columnName)
        .append("isRuntimeFilter", isRuntimeFilter)
        .append("isGlobalFilter", isGlobalFilter)
        .append("isAggregationFilter",isAggregationFilter)
        .append("aggregate",aggregate)
        .append("model", model)
        .toString();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(model)
        .append(isRuntimeFilter)
        .append(artifactsName)
        .append(columnName)
        .append(isGlobalFilter)
        .append(isAggregationFilter)
        .append(aggregate)
        .append(type)
        .append(isOptional)
        .toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof Filter) == false) {
      return false;
    }
    Filter rhs = ((Filter) other);
    return new EqualsBuilder()
        .append(model, rhs.model)
        .append(isRuntimeFilter, rhs.isRuntimeFilter)
        .append(artifactsName, rhs.artifactsName)
        .append(columnName, rhs.columnName)
        .append(isGlobalFilter, rhs.isGlobalFilter)
        .append(isAggregationFilter,rhs.isAggregationFilter)
        .append(aggregate,rhs.aggregate)
        .append(type, rhs.type)
        .append(isOptional, rhs.isOptional)
        .isEquals();
  }

  public enum IsRuntimeFilter {
    FALSE(false),
    TRUE(true);
    private static final Map<Boolean, Filter.IsRuntimeFilter> CONSTANTS =
        new HashMap<Boolean, Filter.IsRuntimeFilter>();

    static {
      for (Filter.IsRuntimeFilter c : values()) {
        CONSTANTS.put(c.value, c);
      }
    }

    private final Boolean value;

    private IsRuntimeFilter(Boolean value) {
      this.value = value;
    }

    @JsonCreator
    public static Filter.IsRuntimeFilter fromValue(Boolean value) {
      Filter.IsRuntimeFilter constant = CONSTANTS.get(value);
      if (constant == null) {
        throw new IllegalArgumentException((value + ""));
      } else {
        return constant;
      }
    }

    @JsonValue
    public Boolean value() {
      return this.value;
    }
  }

  public enum IsGlobalFilter {
    FALSE(false),
    TRUE(true);
    private static final Map<Boolean, Filter.IsGlobalFilter> CONSTANTS = new HashMap<>();

    static {
      for (Filter.IsGlobalFilter c : values()) {
        CONSTANTS.put(c.value, c);
      }
    }

    private final Boolean value;

    private IsGlobalFilter(Boolean value) {
      this.value = value;
    }

    @JsonCreator
    public static Filter.IsGlobalFilter fromValue(Boolean value) {
      Filter.IsGlobalFilter constant = CONSTANTS.get(value);
      if (constant == null) {
        throw new IllegalArgumentException((value + ""));
      } else {
        return constant;
      }
    }

    @JsonValue
    public Boolean value() {
      return this.value;
    }
  }

  public enum Type {
    LONG("long"),
    STRING("string"),
    INTEGER("integer"),
    DOUBLE("double"),
    DATE("date"),
    TIMESTAMP("timestamp"),
    FLOAT("float");
    private static final Map<String, Filter.Type> CONSTANTS = new HashMap<String, Filter.Type>();

    static {
      for (Filter.Type c : values()) {
        CONSTANTS.put(c.value, c);
      }
    }

    private final String value;

    private Type(String value) {
      this.value = value;
    }

    @JsonCreator
    public static Filter.Type fromValue(String value) {
      Filter.Type constant = CONSTANTS.get(value.toLowerCase());
      if (constant == null) {
        throw new IllegalArgumentException(value);
      } else {
        return constant;
      }
    }

    @Override
    public String toString() {
      return this.value;
    }

    @JsonValue
    public String value() {
      return this.value;
    }
  }
}
