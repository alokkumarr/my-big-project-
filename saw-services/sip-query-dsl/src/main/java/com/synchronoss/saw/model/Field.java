
package com.synchronoss.saw.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.*;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "dataField",
    "area",
    "alias",
    "columnName",
    "displayName",
    "type",
    "aggregate",
    "groupInterval"
})
public class Field {

    @JsonProperty("dataField")
    private String dataField;
    @JsonProperty("area")
    private String area;
    @JsonProperty("alias")
    private String alias;
    @JsonProperty("columnName")
    private String columnName;
    @JsonProperty("displayName")
    private String displayName;
    @JsonProperty("type")
    private Type type;
    @JsonProperty("aggregate")
    private Aggregate aggregate;
    @JsonProperty("groupInterval")
    private GroupInterval groupInterval;
    @JsonProperty("dateFormat")
    private String dateFormat;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("dataField")
    public String getDataField() {
        return dataField;
    }

    @JsonProperty("dataField")
    public void setDataField(String dataField) {
        this.dataField = dataField;
    }

    @JsonProperty("area")
    public String getArea() {
        return area;
    }

    @JsonProperty("area")
    public void setArea(String area) {
        this.area = area;
    }

    @JsonProperty("alias")
    public String getAlias() {
        return alias;
    }

    @JsonProperty("alias")
    public void setAlias(String alias) {
        this.alias = alias;
    }

    @JsonProperty("columnName")
    public String getColumnName() {
        return columnName;
    }

    @JsonProperty("columnName")
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    @JsonProperty("displayName")
    public String getDisplayName() {
        return displayName;
    }

    @JsonProperty("displayName")
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @JsonProperty("type")
    public Type getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(Type type) {
        this.type = type;
    }

    @JsonProperty("aggregate")
    public Aggregate getAggregate() {
        return aggregate;
    }

    @JsonProperty("aggregate")
    public void setAggregate(Aggregate aggregate) {
        this.aggregate = aggregate;
    }

    @JsonProperty("groupInterval")
    public GroupInterval getGroupInterval() {
        return groupInterval;
    }

    @JsonProperty("groupInterval")
    public void setGroupInterval(GroupInterval groupInterval) {
        this.groupInterval = groupInterval;
    }

    /**
     * Gets dateFormat
     *
     * @return value of dateFormat
     */
    public String getDateFormat() {
        return dateFormat;
    }

    /**
     * Sets dateFormat
     */
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public enum GroupInterval {

        YEAR("year"),
        MONTH("month"),
        DAY("day"),
        QUARTER("quarter"),
        HOUR("hour"),
        WEEK("week");
        private final String value;
        private final static Map<String, Field.GroupInterval> CONSTANTS = new HashMap<String, Field.GroupInterval>();

        static {
            for (Field.GroupInterval c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private GroupInterval(String value) {
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
        public static Field.GroupInterval fromValue(String value) {
            Field.GroupInterval constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }


    public enum Aggregate {

        AVG("avg"),
        SUM("sum"),
        MIN("min"),
        MAX("max"),
        COUNT("count"),
        PERCENTAGE("percentage");
        private final String value;
        private final static Map<String, Field.Aggregate> CONSTANTS = new HashMap<>();

        static {
            for (Field.Aggregate c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Aggregate(String value) {
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
        public static Field.Aggregate fromValue(String value) {
            Field.Aggregate constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

    public enum Type {

        DATE("date"),
        TIMESTAMP("timestamp"),
        LONG("long"),
        DOUBLE("double"),
        FLOAT("float"),
        INTEGER("integer"),
        STRING("string");
        private final String value;
        private final static Map<String, Field.Type> CONSTANTS = new HashMap<>();

        static {
            for (Field.Type c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Type(String value) {
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
        public static Field.Type fromValue(String value) {
            Field.Type constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("dataField", dataField).append("area", area)
        		.append("alias", alias).append("columnName", columnName).append("displayName", displayName)
        		.append("type", type).append("aggregate", aggregate).append("groupInterval", groupInterval)
        		.append("additionalProperties", additionalProperties).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(aggregate).append(area).append(alias).append(additionalProperties)
        		.append(columnName).append(dataField).append(type).append(displayName).append(groupInterval)
        		.toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Field) == false) {
            return false;
        }
        Field rhs = ((Field) other);
        return new EqualsBuilder().append(aggregate, rhs.aggregate).append(area, rhs.area)
        		.append(alias, rhs.alias).append(additionalProperties, rhs.additionalProperties)
        		.append(columnName, rhs.columnName).append(dataField, rhs.dataField).append(type, rhs.type)
        		.append(displayName, rhs.displayName).append(groupInterval, rhs.groupInterval).isEquals();
    }

}
