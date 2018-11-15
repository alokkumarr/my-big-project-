package com.synchronoss.saw.batch.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BisComponentState {
	DATA_RECEIVED("DATA_RECEIVED"),
	DATA_REMOVED("DATA_REMOVED");
    private final String value;
    private final static Map<String, BisComponentState> CONSTANTS = new HashMap<String, BisComponentState>();

    static {
        for (BisComponentState c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private BisComponentState(String value) {
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
    public static BisComponentState fromValue(String value) {
    	BisComponentState constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }
}
