
package com.synchronoss.saw.storage.proxy.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "storage",
  "action",
  "requestBy",
  "requestedTime",
  "productCode",
  "moduleName",
  "resultFormat"
})
public class StorageProxy {

    /**
     * The Storage Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("storage")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private StorageProxy.Storage storage = StorageProxy.Storage.fromValue("ES");
    /**
     * The Storage Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("action")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private StorageProxy.Action action = StorageProxy.Action.fromValue("search");
    @JsonProperty("query")
    private String query;
    
    @JsonProperty("statusMessage")
    private String statusMessage;

    /**
    * It holds the query
    * <p>
    * An explanation about the purpose of this instance.
    * 
    */
    @JsonProperty("entityId")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private String entityId;
    /**
    * It holds the query
    * <p>
    * An explanation about the purpose of this instance.
    * 
    */
    @JsonProperty("pageNum")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private Integer pageNum = 0;
    /**
    * It holds the query
    * <p>
    * An explanation about the purpose of this instance.
    * 
    */
    @JsonProperty("pageSize")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private Integer pageSize= 25;

    
    /**
     * It holds the user requested by
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("requestBy")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private String requestBy = "admin@synchronoss.com";
    /**
     * It holds the objectType
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("objectType")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private String objectType;
    /**
     * It holds the indexName
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("indexName")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private String indexName;
    /**
     * It holds the tableName
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("tableName")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private String tableName;
    /**
     * It holds the objectName
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("objectName")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private String objectName;
    /**
     * It holds the user requested time
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("requestedTime")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private String requestedTime;

    @JsonProperty("responseTime")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private String responseTime;
    
    @JsonProperty("dataColumnHeader")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private Object dataColumnHeader;

    
    /**
     * The Productcode Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("productCode")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private String productCode;
    /**
     * The Modulename Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("moduleName")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private String moduleName;
    @JsonProperty("dataSecurityKey")
    private List<Object> dataSecurityKey = null;
    /**
     * The Resultformat Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("resultFormat")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private StorageProxy.ResultFormat resultFormat = StorageProxy.ResultFormat.fromValue("json");
    @JsonProperty("data")
    private List<Object> data = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("sqlBuilder")
    private SqlBuilder sqlBuilder;

    
    /**
     * The Storage Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("storage")
    public StorageProxy.Storage getStorage() {
        return storage;
    }

    /**
     * The Storage Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("storage")
    public void setStorage(StorageProxy.Storage storage) {
        this.storage = storage;
    }

    /**
     * The Storage Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("action")
    public StorageProxy.Action getAction() {
        return action;
    }

    /**
     * The Storage Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("action")
    public void setAction(StorageProxy.Action action) {
        this.action = action;
    }

    @JsonProperty("query")
    public String getQuery() {
        return query;
    }

    @JsonProperty("query")
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * It holds the user requested by
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("requestBy")
    public String getRequestBy() {
        return requestBy;
    }

    /**
     * It holds the user requested by
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("requestBy")
    public void setRequestBy(String requestBy) {
        this.requestBy = requestBy;
    }

    /**
     * It holds the objectType
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("objectType")
    public String getObjectType() {
        return objectType;
    }

    /**
     * It holds the objectType
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("objectType")
    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    /**
     * It holds the indexName
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("indexName")
    public String getIndexName() {
        return indexName;
    }

    /**
     * It holds the indexName
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("indexName")
    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    /**
     * It holds the tableName
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("tableName")
    public String getTableName() {
        return tableName;
    }

    /**
     * It holds the tableName
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("tableName")
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * It holds the objectName
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("objectName")
    public String getObjectName() {
        return objectName;
    }

    /**
     * It holds the objectName
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("objectName")
    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    /**
     * It holds the user requested time
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("requestedTime")
    public String getRequestedTime() {
        return requestedTime;
    }

    /**
     * It holds the user requested time
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("requestedTime")
    public void setRequestedTime(String requestedTime) {
        this.requestedTime = requestedTime;
    }

    /**
     * The Productcode Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("productCode")
    public String getProductCode() {
        return productCode;
    }

    /**
     * The Productcode Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("productCode")
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    /**
     * The Modulename Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("moduleName")
    public String getModuleName() {
        return moduleName;
    }
    @JsonProperty("statusMessage")
    public String getStatusMessage() {
      return statusMessage;
    }
    @JsonProperty("statusMessage")
    public void setStatusMessage(String statusMessage) {
      this.statusMessage = statusMessage;
    }

    /**
     * The Modulename Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("moduleName")
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    @JsonProperty("dataSecurityKey")
    public List<Object> getDataSecurityKey() {
        return dataSecurityKey;
    }

    @JsonProperty("dataSecurityKey")
    public void setDataSecurityKey(List<Object> dataSecurityKey) {
        this.dataSecurityKey = dataSecurityKey;
    }

    /**
     * The Resultformat Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("resultFormat")
    public StorageProxy.ResultFormat getResultFormat() {
        return resultFormat;
    }

    /**
     * The Resultformat Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("resultFormat")
    public void setResultFormat(StorageProxy.ResultFormat resultFormat) {
        this.resultFormat = resultFormat;
    }

    @JsonProperty("data")
    public List<Object> getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(List<Object> data) {
        this.data = data;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
    
    /**
    * It holds the query
    * <p>
    * An explanation about the purpose of this instance.
    * 
    */
    @JsonProperty("entityId")
    public String getEntityId() {
    return entityId;
    }

    /**
    * It holds the query
    * <p>
    * An explanation about the purpose of this instance.
    * 
    */
    @JsonProperty("entityId")
    public void setEntityId(String entityId) {
    this.entityId = entityId;
    }

    /**
    * It holds the query
    * <p>
    * An explanation about the purpose of this instance.
    * 
    */
    @JsonProperty("pageNum")
    public Integer getPageNum() {
    return pageNum;
    }

    /**
    * It holds the query
    * <p>
    * An explanation about the purpose of this instance.
    * 
    */
    @JsonProperty("pageNum")
    public void setPageNum(Integer pageNum) {
    this.pageNum = pageNum;
    }

    /**
    * It holds the query
    * <p>
    * An explanation about the purpose of this instance.
    * 
    */
    @JsonProperty("pageSize")
    public Integer getPageSize() {
    return pageSize;
    }

    /**
    * It holds the query
    * <p>
    * An explanation about the purpose of this instance.
    * 
    */
    @JsonProperty("pageSize")
    public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
    }
    
    @JsonProperty("sqlBuilder")
    public SqlBuilder getSqlBuilder() {
      return sqlBuilder;
    }
    @JsonProperty("sqlBuilder")
    public void setSqlBuilder(SqlBuilder sqlBuilder) {
      this.sqlBuilder = sqlBuilder;
    }
    @JsonProperty("responseTime")
    public String getResponseTime() {
      return responseTime;
    }
    @JsonProperty("responseTime")
    public void setResponseTime(String responseTime) {
      this.responseTime = responseTime;
    }

    @JsonProperty("dataColumnHeader")
    public Object getDataColumnHeader() {
      return dataColumnHeader;
    }
    @JsonProperty("dataColumnHeader")
    public void setDataColumnHeader(Object dataColumnHeader) {
      this.dataColumnHeader = dataColumnHeader;
    }




    public enum Action {

        SEARCH("search"),
        AGGREGATE("aggregate"),
        CREATE("create"),
        DELETE("delete"),
        SNCRPIVOT("sncrpivot"),
        COUNT("count");
        private final String value;
        private final static Map<String, StorageProxy.Action> CONSTANTS = new HashMap<String, StorageProxy.Action>();

        static {
            for (StorageProxy.Action c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Action(String value) {
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
        public static StorageProxy.Action fromValue(String value) {
            StorageProxy.Action constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

    public enum ResultFormat {

        TABULAR("tabular"),
        JSON("json");
        private final String value;
        private final static Map<String, StorageProxy.ResultFormat> CONSTANTS = new HashMap<String, StorageProxy.ResultFormat>();

        static {
            for (StorageProxy.ResultFormat c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private ResultFormat(String value) {
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
        public static StorageProxy.ResultFormat fromValue(String value) {
            StorageProxy.ResultFormat constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

    public enum Storage {

        ES("ES"),
        DL("DL"),
        RDMS("RDMS");
        private final String value;
        private final static Map<String, StorageProxy.Storage> CONSTANTS = new HashMap<String, StorageProxy.Storage>();

        static {
            for (StorageProxy.Storage c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Storage(String value) {
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
        public static StorageProxy.Storage fromValue(String value) {
            StorageProxy.Storage constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
