
package com.synchronoss.querybuilder.model.report;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "sqlBuilder"
})
public class SqlBuilderReport {

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("sqlBuilder")
    private SqlBuilder sqlBuilder;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("sqlBuilder")
    public SqlBuilder getSqlBuilder() {
        return sqlBuilder;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("sqlBuilder")
    public void setSqlBuilder(SqlBuilder sqlBuilder) {
        this.sqlBuilder = sqlBuilder;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
