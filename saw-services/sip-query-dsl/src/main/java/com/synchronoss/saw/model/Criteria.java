package com.synchronoss.saw.model;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "artifactsName",
    "columnName",
    "side"
})
public class Criteria {

    @JsonProperty("artifactsName")
    private String artifactsName;
    @JsonProperty("columnName")
    private String columnName;
    @JsonProperty("side")
    private Side side;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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

    @JsonProperty("side")
    public Side getSide() {
        return side;
    }

    @JsonProperty("side")
    public void setSide(Side side) {
        this.side = side;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public enum Side {
        LEFT("left"),
        RIGHT("right");

        private final String value;
        private final static Map<String, Criteria.Side> CONSTANTS = new HashMap<>();

        static {
            for (Criteria.Side c : values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Side(String value) {
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
        public static Side fromValue(String value) {
            Side constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException("Join type not implemented: " + value);
            } else {
                return constant;
            }
        }
    }
}
