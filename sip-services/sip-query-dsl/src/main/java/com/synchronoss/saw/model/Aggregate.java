package com.synchronoss.saw.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;

public enum Aggregate {
    AVG("avg"),
    SUM("sum"),
    MIN("min"),
    MAX("max"),
    COUNT("count"),
    PERCENTAGE("percentage"),
    PERCENTAGE_BY_ROW("percentagebyrow"),
    DISTINCTCOUNT("distinctcount");

    private static final Map<String, Aggregate> CONSTANTS = new HashMap<>();

    static {
        for (Aggregate c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private final String value;

    private Aggregate(String value) {
        this.value = value;
    }

    @JsonCreator
    public static Aggregate fromValue(String value) {
        Aggregate constant = CONSTANTS.get(value.toLowerCase());
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
