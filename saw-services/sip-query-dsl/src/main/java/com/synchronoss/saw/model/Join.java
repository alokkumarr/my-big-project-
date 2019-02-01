package com.synchronoss.saw.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "join",
    "criteria"
})
public class Join {

    @JsonProperty("join")
    private JoinType joinType;
    @JsonProperty("criteria")
    private List<Criteria> criteria = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("join")
    public JoinType getJoinType() {
        return joinType;
    }

    @JsonProperty("join")
    public void setJoinType(JoinType joinType) {
        this.joinType = joinType;
    }

    @JsonProperty("criteria")
    public List<Criteria> getCriteria() {
        return criteria;
    }

    @JsonProperty("criteria")
    public void setCriteria(List<Criteria> criteria) {
        this.criteria = criteria;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public enum JoinType {

        INNER("inner"),
        LEFT("left"),
        RIGHT("right");
        private final String value;
        private final static Map<String, Join.JoinType> CONSTANTS = new HashMap<>();

        static {
            for (Join.JoinType c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private JoinType(String value) {
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
        public static JoinType fromValue(String value) {
            JoinType constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException("Join type not implemented: "+ value);
            } else {
                return constant;
            }
        }
    }
}
