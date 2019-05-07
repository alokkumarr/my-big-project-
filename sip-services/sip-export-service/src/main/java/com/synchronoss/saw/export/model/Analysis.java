package com.synchronoss.saw.export.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class Analysis {

    @JsonProperty("sqlBuilder")
    private SqlBuilder sqlBuilder;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * Gets sqlBuilder
     *
     * @return value of sqlBuilder
     */
    public SqlBuilder getSqlBuilder() {
        return sqlBuilder;
    }

    /**
     * Sets sqlBuilder
     */
    public void setSqlBuilder(SqlBuilder sqlBuilder) {
        this.sqlBuilder = sqlBuilder;
    }

    /**
     * Gets additionalProperties
     *
     * @return value of additionalProperties
     */
    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    /**
     * Sets additionalProperties
     */
    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public Analysis withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }
}
