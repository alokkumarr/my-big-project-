package com.synchronoss.querybuilder.model.globalfilter;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "columnName",
        "type",
        "size",
        "order"
})
public class Filter {

    /**
     * (Required)
     */
    @JsonProperty("columnName")
   private String columnName;

    /**
     * (Required)
     */
    @JsonProperty("type")
   private Type type;

    /**
     * (Optional)
     */
    @JsonProperty("size")
   private  int size;

    /**
     * (Optional)
     */
    @JsonProperty("order")
   private Order order;

    /**
     * Gets columnName
     *
     * @return value of columnName
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Sets columnName
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * Gets type
     *
     * @return value of type
     */
    public Type getType() {
        return type;
    }

    /**
     * Sets type
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Gets size
     *
     * @return value of size
     */
    public int getSize() {
        return size;
    }

    /**
     * Sets size
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Gets order
     *
     * @return value of order
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Sets order
     */
    public void setOrder(Order order) {
        this.order = order;
    }

    public enum Type {

        DATE("date"),
        TIMESTAMP("timestamp"),
        LONG("long"),
        DOUBLE("double"),
        INT("integer"),
        STRING("string"),
        FLOAT("float");
        private final String value;
        private final static Map<String, Type> CONSTANTS = new HashMap<String, Type>();

        static {
            for (Type c: values()) {
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
        public static Type fromValue(String value) {
            Type constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

    public enum Order {

        DESC("desc"),
        ASC("asc");
        private final String value;
        private final static Map<String, Order> CONSTANTS = new HashMap<String, Order>();

        static {
            for (Order c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Order(String value) {
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
        public static Order fromValue(String value) {
            Order constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }
    }
}
