package com.synchronoss.saw.batch.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BisProcessState {
    SUCCESS("SUCCESS"),
    FAILED("FAILED"),
    INPROGRESS("INPROGRESS");
    private final String value;
    private final static Map<String, BisProcessState> CONSTANTS = new HashMap<String, BisProcessState>();

    static {
        for (BisProcessState c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private BisProcessState(String value) {
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
    public static BisProcessState fromValue(String value) {
    	BisProcessState constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }
}
