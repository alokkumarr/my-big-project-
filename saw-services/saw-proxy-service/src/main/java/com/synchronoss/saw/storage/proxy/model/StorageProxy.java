
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
  "resultFormat",
  "aggregationData"
})
public class StorageProxy {

    /**
     * The Storage Schema
     * <p>
     * An explanation about the purpose of this instance.
     * (Required)
     * 
     */
    @JsonProperty("storage")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private StorageProxy.Storage storage = StorageProxy.Storage.fromValue("ES");
    /**
     * The Storage Schema
     * <p>
     * An explanation about the purpose of this instance.
     * (Required)
     * 
     */
    @JsonProperty("action")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private StorageProxy.Action action = StorageProxy.Action.fromValue("search");
    /**
     * It holds the query
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("_id")
    private String _id;

    @JsonProperty("query")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private String query;
    /**
     * It holds the query
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    /**
     * It holds the name
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("name")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private String name;
    
    @JsonProperty("content")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private String content;

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
    private float pageNum;
    /**
     * It holds the size
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */

    @JsonProperty("size")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private String size;
    /**
     * It holds the indexRelativePath
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */

    @JsonProperty("indexRelativePath")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private String indexRelativePath;

    /**
     * It holds the query
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */

    @JsonProperty("count")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private String count;
    /**
     * It holds the query
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
   
    
    @JsonProperty("pageSize")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private int pageSize;
    /**
     * It holds the statusMessage
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("statusMessage")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private String statusMessage;
    /**
     * It holds the user requested by
     * <p>
     * An explanation about the purpose of this instance.
     * (Required)
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
     * (Required)
     * 
     */
    @JsonProperty("requestedTime")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private String requestedTime;
    /**
     * It holds the user requested time
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("responseTime")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private String responseTime;
    /**
     * The Productcode Schema
     * <p>
     * An explanation about the purpose of this instance.
     * (Required)
     * 
     */
    @JsonProperty("productCode")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private String productCode;
    /**
     * The Modulename Schema
     * <p>
     * An explanation about the purpose of this instance.
     * (Required)
     * 
     */
    @JsonProperty("moduleName")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private String moduleName;
    /**
     * The Modulename Schema
     * <p>
     * An explanation about the purpose of this instance.
     * (Required)
     * 
     */
    @JsonProperty("aggregationData")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private Object aggregationData;

    @JsonProperty("dataSecurityKey")
    private List<Object> dataSecurityKey = null;
    /**
     * The Resultformat Schema
     * <p>
     * An explanation about the purpose of this instance.
     * (Required)
     * 
     */
    @JsonProperty("resultFormat")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private StorageProxy.ResultFormat resultFormat = StorageProxy.ResultFormat.fromValue("json");
    @JsonProperty("data")
    private List<Object> data = null;
    @JsonProperty("sqlBuilder")
    private SqlBuilder sqlBuilder;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * The Storage Schema
     * <p>
     * An explanation about the purpose of this instance.
     * (Required)
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
     * (Required)
     * 
     */
    @JsonProperty("_id")    
    public String get_id() {
      return _id;
    }
    /**
     * The Storage Schema
     * <p>
     * An explanation about the purpose of this instance.
     * (Required)
     * 
     */
    @JsonProperty("_id") 
    public void set_id(String _id) {
      this._id = _id;
    }

    /**
     * The Storage Schema
     * <p>
     * An explanation about the purpose of this instance.
     * (Required)
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
     * (Required)
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
     * (Required)
     * 
     */
    @JsonProperty("action")
    public void setAction(StorageProxy.Action action) {
        this.action = action;
    }

    /**
     * It holds the query
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("query")
    public String getQuery() {
        return query;
    }

    /**
     * It holds the query
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("query")
    public void setQuery(String query) {
        this.query = query;
    }
    
    
    @JsonProperty("name")
    public String getName() {
      return name;
    }
    @JsonProperty("name")
    public void setName(String name) {
      this.name = name;
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
    public float getPageNum() {
        return pageNum;
    }

    /**
     * It holds the query
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("pageNum")
    public void setPageNum(float pageNum) {
        this.pageNum = pageNum;
    }

    /**
     * It holds the query
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("pageSize")
    public int getPageSize() {
        return pageSize;
    }
    
    
    @JsonProperty("content")
    public String getContent() {
      return content;
    }
    @JsonProperty("content")    
    public void setContent(String content) {
      this.content = content;
    }
    /**
     * It holds the query
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("pageSize")
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * It holds the statusMessage
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("statusMessage")
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * It holds the statusMessage
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("statusMessage")
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    /**
     * It holds the user requested by
     * <p>
     * An explanation about the purpose of this instance.
     * (Required)
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
     * (Required)
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
     * (Required)
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
     * (Required)
     * 
     */
    @JsonProperty("requestedTime")
    public void setRequestedTime(String requestedTime) {
        this.requestedTime = requestedTime;
    }

    /**
     * It holds the user requested time
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("responseTime")
    public String getResponseTime() {
        return responseTime;
    }

    /**
     * It holds the user requested time
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("responseTime")
    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

    /**
     * The Productcode Schema
     * <p>
     * An explanation about the purpose of this instance.
     * (Required)
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
     * (Required)
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
     * (Required)
     * 
     */
    @JsonProperty("moduleName")
    public String getModuleName() {
        return moduleName;
    }

    /**
     * The Modulename Schema
     * <p>
     * An explanation about the purpose of this instance.
     * (Required)
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
     * (Required)
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
     * (Required)
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

    @JsonProperty("sqlBuilder")
    public SqlBuilder getSqlBuilder() {
        return sqlBuilder;
    }

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
    
    @JsonProperty("aggregationData")
    public Object getAggregationData() {
      return aggregationData;
    }
    @JsonProperty("aggregationData")
    public void setAggregationData(Object aggregationData) {
      this.aggregationData = aggregationData;
    }
    @JsonProperty("size")
    public String getSize() {
      return size;
    }
    @JsonProperty("size")
    public void setSize(String size) {
      this.size = size;
    }
    @JsonProperty("count")
    public String getCount() {
      return count;
    }
    @JsonProperty("count")
    public void setCount(String count) {
      this.count = count;
    }
    @JsonProperty("indexRelativePath")    
    public String getIndexRelativePath() {
      return indexRelativePath;
    }
    @JsonProperty("indexRelativePath")    
    public void setIndexRelativePath(String indexRelativePath) {
      this.indexRelativePath = indexRelativePath;
    }




    public enum Action {

        SEARCH("search"),
        VALIDATE("validate"),
        COUNT("count"),
        CREATE("create"),
        DELETE("delete"),
        UPDATE("update"),
        READ("read"),
        EXECUTE("execute"),
        AGGREGATE("aggregate"),
        PIVOT("pivot"),
        CATINDICES("catIndices"),
        CATALIASES("catAliases"),
        MAPPINGINDICES("mappingsIndices"),
        MAPPINGALIASES("mappingsAliases");
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
        RDMS("RDMS"),
        METADATA("METADATA");
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
