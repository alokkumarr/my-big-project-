package com.synchronoss.sip.alert.modal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;

public enum Operator {

    GT("GT"),
    LT("LT"),
    GTE("GTE"),
    LTE("LTE"),
    EQ("EQ"),
    NEQ("NEQ"),
    BTW("BTW"),
    SW("SW"),
    EW("EW"),
    CONTAINS("CONTAINS"),
    ISIN("ISIN"),
    ISNOTIN("ISNOTIN");
    private final String value;
    private final static Map<String, Operator> CONSTANTS = new HashMap<>();

    static {
        for (Operator c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private Operator(String value) {
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
    public static Operator fromValue(String value) {
        Operator constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }
}
