package com.synchronoss.saw.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import com.synchronoss.bda.sip.dsk.BooleanCriteria;
import com.synchronoss.bda.sip.dsk.Model;
import com.synchronoss.bda.sip.dsk.SipDskAttribute;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(Include.NON_NULL)
@ApiModel
@JsonPropertyOrder({
    "columnName",
    "type",
    "artifactsName",
    "isOptional",
    "isRuntimeFilter",
    "isGlobalFilter",
    "booleanCriteria",
    "booleanQuery",
    "model"
})
public class SipFilter {

  @JsonProperty("columnName")
  @ApiModelProperty(notes = "Column name/Attribute Name", name = "columnName")
  private String columnName;

  @JsonProperty("type")
  private Type type;

  @JsonProperty("artifactsName")
  private String artifactsName;

  @JsonProperty("isOptional")
  private Boolean isOptional;

  @JsonProperty("isRuntimeFilter")
  private Boolean isRuntimeFilter;

  @JsonProperty("isGlobalFilter")
  private Boolean isGlobalFilter;

  @JsonProperty("isAggregationFilter")
  private Boolean isAggregationFilter;

  @JsonProperty("aggregate")
  private Aggregate aggregate;

  @JsonProperty("model")
  @ApiModelProperty(notes = "Attribute Model", name = "model")
  private com.synchronoss.bda.sip.dsk.Model model;

  @JsonProperty("booleanCriteria")
  @ApiModelProperty(notes = "Conjunction used", name = "booleanCriteria")
  private BooleanCriteria booleanCriteria;

  @JsonProperty("booleanQuery")
  @ApiModelProperty(
      notes = "List of attributes on which boolean criteria has to be applied",
      name = "booleanQuery")
  private List<SipDskAttribute> booleanQuery;

  public String getColumnName() {
    return columnName;
  }

  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

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

  /**
   * Sets isAggregationFilter
   */
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

  public com.synchronoss.bda.sip.dsk.Model getModel() {
    return model;
  }

  public void setModel(Model model) {
    this.model = model;
  }

  public BooleanCriteria getBooleanCriteria() {
    return booleanCriteria;
  }

  public void setBooleanCriteria(BooleanCriteria booleanCriteria) {
    this.booleanCriteria = booleanCriteria;
  }

  public List<SipDskAttribute> getBooleanQuery() {
    return booleanQuery;
  }

  public void setBooleanQuery(List<SipDskAttribute> booleanQuery) {
    this.booleanQuery = booleanQuery;
  }

  public enum IsRuntimeFilter {
    FALSE(false),
    TRUE(true);
    private static final Map<Boolean, SipFilter.IsRuntimeFilter> CONSTANTS =
        new HashMap<Boolean, SipFilter.IsRuntimeFilter>();

    static {
      for (SipFilter.IsRuntimeFilter c : values()) {
        CONSTANTS.put(c.value, c);
      }
    }

    private final Boolean value;

    private IsRuntimeFilter(Boolean value) {
      this.value = value;
    }

    @JsonCreator
    public static SipFilter.IsRuntimeFilter fromValue(Boolean value) {
      SipFilter.IsRuntimeFilter constant = CONSTANTS.get(value);
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
    private static final Map<Boolean, SipFilter.IsGlobalFilter> CONSTANTS = new HashMap<>();

    static {
      for (SipFilter.IsGlobalFilter c : values()) {
        CONSTANTS.put(c.value, c);
      }
    }

    private final Boolean value;

    private IsGlobalFilter(Boolean value) {
      this.value = value;
    }

    @JsonCreator
    public static SipFilter.IsGlobalFilter fromValue(Boolean value) {
      SipFilter.IsGlobalFilter constant = CONSTANTS.get(value);
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
    private static final Map<String, SipFilter.Type> CONSTANTS = new HashMap<>();

    static {
      for (SipFilter.Type c : values()) {
        CONSTANTS.put(c.value, c);
      }
    }

    private final String value;

    private Type(String value) {
      this.value = value;
    }

    @JsonCreator
    public static SipFilter.Type fromValue(String value) {
      SipFilter.Type constant = CONSTANTS.get(value.toLowerCase());
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

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("columnName", columnName)
        .append("model", model)
        .append("booleanCriteria", booleanCriteria)
        .append("booleanQuery", booleanQuery)
        .append("type", type)
        .append("artifactsName", artifactsName)
        .append("isOptional", isOptional)
        .append("isRuntimeFilter", isRuntimeFilter)
        .append("isGlobalFilter", isGlobalFilter)
        .append("isAggregationFilter", isAggregationFilter)
        .append("aggregate", aggregate)
        .toString();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(columnName)
        .append(model)
        .append(booleanCriteria)
        .append(booleanQuery)
        .append(type)
        .append(artifactsName)
        .append(isOptional)
        .append(isRuntimeFilter)
        .append(isGlobalFilter)
        .append(isAggregationFilter)
        .append(aggregate)
        .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if ((obj instanceof SipFilter) == false) {
      return false;
    }
    SipFilter rhs = ((SipFilter) obj);
    return new EqualsBuilder()
        .append(columnName, rhs.columnName)
        .append(model, rhs.model)
        .append(booleanCriteria, rhs.booleanCriteria)
        .append(booleanQuery, rhs.booleanQuery)
        .append(type, rhs.type)
        .append(artifactsName, rhs.artifactsName)
        .append(isOptional, rhs.isOptional)
        .append(isRuntimeFilter, rhs.isRuntimeFilter)
        .append(isGlobalFilter, rhs.isGlobalFilter)
        .append(isAggregationFilter, rhs.isAggregationFilter)
        .append(aggregate, rhs.aggregate)
        .isEquals();
  }
}
