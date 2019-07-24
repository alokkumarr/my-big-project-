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
@JsonPropertyOrder({"artifactsName", "columnName", "type", "order", "aggregate"})
public class Sort {

  @JsonProperty("artifactsName")
  private String artifactsName;

  @JsonProperty("columnName")
  private String columnName;

  @JsonProperty("type")
  private Type type;

  @JsonProperty("order")
  private Order order;

  @JsonProperty("aggregate")
  private Aggregate aggregate;

  @JsonProperty("artifactsName")
  public String getArtifactsName() {
    return artifactsName;
  }

  @JsonProperty("artifactsName")
  public void setArtifactsName(String artifactsName) {
    this.artifactsName = artifactsName;
  }

  @JsonProperty("columnName")
  public String getColumnName() {
    return columnName;
  }

  @JsonProperty("columnName")
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

  @JsonProperty("order")
  public Order getOrder() {
    return order;
  }

  @JsonProperty("order")
  public void setOrder(Order order) {
    this.order = order;
  }

  @JsonProperty("aggregate")
  public Aggregate getAggregate() {
    return aggregate;
  }

  @JsonProperty("aggregate")
  public void setAggregate(Aggregate aggregate) {
    this.aggregate = aggregate;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("artifactsName", artifactsName)
        .append("columnName", columnName)
        .append("type", type)
        .append("order", order)
        .append("aggregate", aggregate)
        .toString();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(order)
        .append(columnName)
        .append(type)
        .append(artifactsName)
        .append(aggregate)
        .toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof Sort) == false) {
      return false;
    }
    Sort rhs = ((Sort) other);
    return new EqualsBuilder()
        .append(order, rhs.order)
        .append(columnName, rhs.columnName)
        .append(type, rhs.type)
        .append(artifactsName, rhs.artifactsName)
        .append(aggregate, rhs.aggregate)
        .isEquals();
  }

  public enum Order {
    DESC("desc"),
    ASC("asc");
    private static final Map<String, Sort.Order> CONSTANTS = new HashMap<String, Sort.Order>();

    static {
      for (Sort.Order c : values()) {
        CONSTANTS.put(c.value, c);
      }
    }

    private final String value;

    private Order(String value) {
      this.value = value;
    }

    @JsonCreator
    public static Sort.Order fromValue(String value) {
      Sort.Order constant = CONSTANTS.get(value.toLowerCase());
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

  public enum Type {
    DATE("date"),
    TIMESTAMP("timestamp"),
    LONG("long"),
    DOUBLE("double"),
    INTEGER("integer"),
    STRING("string"),
    FLOAT("float");
    private static final Map<String, Sort.Type> CONSTANTS = new HashMap<String, Sort.Type>();

    static {
      for (Sort.Type c : values()) {
        CONSTANTS.put(c.value, c);
      }
    }

    private final String value;

    private Type(String value) {
      this.value = value;
    }

    @JsonCreator
    public static Sort.Type fromValue(String value) {
      Sort.Type constant = CONSTANTS.get(value.toLowerCase());
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

  public enum Aggregate {
    AVG("avg"),
    SUM("sum"),
    MIN("min"),
    MAX("max"),
    COUNT("count"),
    PERCENTAGE("percentage"),
    PERCENTAGE_BY_ROW("percentagebyrow"),
    DISTINCTCOUNT("distinctcount");

    private static final Map<String, Sort.Aggregate> CONSTANTS = new HashMap<>();

    static {
      for (Sort.Aggregate c : values()) {
        CONSTANTS.put(c.value, c);
      }
    }

    private final String value;

    private Aggregate(String value) {
      this.value = value;
    }

    @JsonCreator
    public static Sort.Aggregate fromValue(String value) {
      Sort.Aggregate constant = CONSTANTS.get(value.toLowerCase());
      if (constant == null) {
        throw new IllegalArgumentException(value);
      } else {
        return constant;
      }
    }

    @Override
    public String toString() {
      return this.value.toLowerCase();
    }

    @JsonValue
    public String value() {
      return this.value.toLowerCase();
    }
  }
}
